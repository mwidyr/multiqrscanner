package com.multiqrscanner.inbound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.barcode.BarcodeCaptureActivity;
import com.multiqrscanner.barcode.MainBarcodeQRCodeActivity;
import com.multiqrscanner.inbound.adapter.ILoadMore;
import com.multiqrscanner.inbound.adapter.ScanResultAdapter;
import com.multiqrscanner.inbound.model.InboundDetail;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.network.RetrofitClientInstance;
import com.multiqrscanner.network.RetrofitClientInstanceInbound;
import com.multiqrscanner.network.model.GetTimeResponse;
import com.multiqrscanner.network.model.RetroInventory;
import com.multiqrscanner.network.model.RetroWarehouse;
import com.multiqrscanner.network.user.GetInventoryService;
import com.multiqrscanner.network.user.GetLoginService;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.qrcode.QrCodeProductValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsVerificationScanResultActivity extends AppCompatActivity {
    private static String TAG = "GVSRA";
    public static String ValidInbound = "Valid";
    public static String InvalidInbound = "Invalid";

    private TextView inboundNoVal;
    private LinearLayout btnScan, btnSubmit, btnCancel;
    private TextView totalScan, totalValidInbound, totalInvalidInbound;
    private ImageView btnBack;
    private Integer totalScanFromParentAct = 0;
    private Integer totalScanFromChildAct = 0;
    private Gson gson = new Gson();
    private String currentSelectedInboundNo;
    private List<InboundDetail> inboundDetailList;
    private List<QrCodeProductValue> qrCodeProductValues = new ArrayList<>();
    HashMap<String, InboundDetail> inboundMap;
    private Integer totalIntInvalidInbound = 0;
    private Integer totalIntValidInbound = 0;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification_scan_result);
        inboundNoVal = findViewById(R.id.tv_goods_verif_result_inbound_no_val);

        Intent intent = getIntent();
        if (!MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey).trim().equalsIgnoreCase("")) {
            currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
            Log.d(TAG, "onCreate: dari shared preference = " + currentSelectedInboundNo);
        }

        String inboundNo = intent.getStringExtra(MiscUtil.InboundNoKey);
        if (inboundNo != null && !inboundNo.trim().equalsIgnoreCase("")) {
            inboundNoVal.setText(inboundNo);
            currentSelectedInboundNo = inboundNo;
        } else {
            Toast.makeText(this, "Empty Inbound No", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
        totalScanFromParentAct = Integer.parseInt(totalScanExtra);

//        String bitmapArrayExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.ImagePathKey);
//        if (!bitmapArrayExtra.trim().equalsIgnoreCase("")) {
//            imageView = findViewById(R.id.iv_goods_verif_scan_result);
//            byte[] byteImage = gson.fromJson(bitmapArrayExtra, byte[].class);
//            Bitmap imageBitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
//            ;
//            if (imageBitmap != null) {
//                imageView.setImageBitmap(imageBitmap);
//            }
//        }

        String qrCodeListExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.QrCodeGsonKey);
        if (!qrCodeListExtra.trim().equalsIgnoreCase("")) {
            List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrappers = gson.fromJson(qrCodeListExtra, new TypeToken<ArrayList<QrCodeBarcodeSimpleWrapper>>() {
            }.getType());
            if (qrCodeBarcodeSimpleWrappers.size() > 0) {
                String inboundListString = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundListDetail);
                if (!inboundListString.trim().equalsIgnoreCase("")) {
                    HashMap<String, InboundDetail> inboundMapSharedPref = gson.fromJson(inboundListString, new TypeToken<HashMap<String, InboundDetail>>() {
                    }.getType());
                    Log.d(TAG, "onCreate: inboundMapSharedPref = " + inboundMapSharedPref);
                    if (inboundMapSharedPref.size() > 0) {
                        List<InboundDetail> inboundDetailDisplay = new ArrayList<>();
                        List<QrCodeProductValue> qrCodeProductValuesDisplay = new ArrayList<>();
                        for (QrCodeBarcodeSimpleWrapper qrCode : qrCodeBarcodeSimpleWrappers) {
                            // get qrcode detail value
                            QrCodeProductValue qrCodeProductValue = new QrCodeProductValue();
                            try {
                                qrCodeProductValue = gson.fromJson(qrCode.getQrValue(), QrCodeProductValue.class);
                            } catch (Exception ex) {
                                Log.e(TAG, "onCreate: " + ex.getMessage(), ex);
                                continue;
                            }
                            Long serialNoScanLong = qrCodeProductValue.getSerialNo();
                            if(serialNoScanLong == null){
                                continue;
                            }
                            String serialNoScan = serialNoScanLong.toString();
                            if (inboundMapSharedPref.get(serialNoScan) != null) {
                                InboundDetail inboundDetail = new InboundDetail(
                                        inboundMapSharedPref.get(serialNoScan).getLineNo(),
                                        inboundMapSharedPref.get(serialNoScan).getSku(),
                                        inboundMapSharedPref.get(serialNoScan).getSerialNo(),
                                        inboundMapSharedPref.get(serialNoScan).getProductName(),
                                        inboundMapSharedPref.get(serialNoScan).getQty(),
                                        inboundMapSharedPref.get(serialNoScan).getSubkey(),
                                        inboundMapSharedPref.get(serialNoScan).getStatus(),
                                        inboundMapSharedPref.get(serialNoScan).getInputDate()
                                );
                                qrCodeProductValue.setValid(ValidInbound);
                                totalIntValidInbound++;
                                if (inboundDetail.getInputDate() == null || inboundDetail.getInputDate() == 0L) {
                                    inboundDetail.setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                }
                                inboundDetailDisplay.add(inboundDetail);
                                if (inboundMapSharedPref.get(serialNoScan).getStatus().trim().equalsIgnoreCase(GoodsVerificationActivity.StatusNotVerified)) {
                                    inboundMapSharedPref.get(serialNoScan).setStatus(GoodsVerificationActivity.StatusVerified);
                                }
                                if (inboundMapSharedPref.get(serialNoScan).getInputDate() == null || inboundMapSharedPref.get(serialNoScan).getInputDate() == 0L) {
                                    inboundMapSharedPref.get(serialNoScan).setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                    GetLoginService service = RetrofitClientInstance.getRetrofitInstance().create(GetLoginService.class);
                                    Call<GetTimeResponse> callSync = service.getServerTimeInMilis();
                                    callSync.enqueue(new Callback<GetTimeResponse>() {
                                        @Override
                                        public void onResponse(Call<GetTimeResponse> call, Response<GetTimeResponse> response) {
                                            GetTimeResponse apiResponse = response.body();
                                            if (apiResponse != null) {
                                                Objects.requireNonNull(inboundMapSharedPref.get(serialNoScan)).setInputDate(Long.parseLong(apiResponse.getDate()));
                                                setInboundMap(inboundMapSharedPref);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<GetTimeResponse> call, Throwable t) {
                                            Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                        }
                                    });
                                }

                            } else {
                                qrCodeProductValue.setValid(InvalidInbound);
                                totalIntInvalidInbound++;
                            }
                            qrCodeProductValuesDisplay.add(qrCodeProductValue);
                        }
                        this.inboundDetailList = inboundDetailDisplay;
                        this.qrCodeProductValues = qrCodeProductValuesDisplay;
                        Log.d(TAG, "onCreate: qrCodeProductValuesDisplay " + qrCodeProductValuesDisplay);
                        int totalInboundDisplayNotVerified = 0;
                        for (InboundDetail inboundDetail : inboundDetailDisplay) {
                            if (!inboundDetail.getStatus().trim().equalsIgnoreCase(GoodsVerificationActivity.StatusVerified)) {
                                totalInboundDisplayNotVerified++;
                            }
                        }
                        totalScanFromChildAct = totalInboundDisplayNotVerified;
                    }
                }
            }
        }
        recyclerView = findViewById(R.id.goods_verif_result_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeBtn();
        setAdapterData();
//        initTable();
        if (totalScanFromChildAct > 0) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setClickable(true);
        }
    }

    public void setAdapterData() {
        ScanResultAdapter adapter;
        adapter = new ScanResultAdapter(recyclerView, this, qrCodeProductValues);
        recyclerView.setAdapter(adapter);
        totalScan.setText("Total Scan: " + qrCodeProductValues.size());
        totalValidInbound.setText("Status Valid:" + totalIntValidInbound);
        totalInvalidInbound.setText("Status Invalid: " + totalIntInvalidInbound);
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

            }
        });
    }

    private void setInboundMap(HashMap<String, InboundDetail> inboundMapInput) {
        this.inboundMap = inboundMapInput;
    }

    public void initializeBtn() {
        btnScan = findViewById(R.id.tv_rescan);
        btnSubmit = findViewById(R.id.tv_submit);
        btnCancel = findViewById(R.id.tv_cancel);
        btnBack = findViewById(R.id.custom_top_bar_back_button);
        totalScan = findViewById(R.id.total_scan_result);
        totalValidInbound = findViewById(R.id.total_valid_inbound);
        totalInvalidInbound = findViewById(R.id.total_invalid_inbound);
        btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        btnScan.setOnClickListener(view -> {
            finish();
            // launch barcode activity.RC_BARCODE_CAPTURE
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ListBarcodeKey);
            startActivityForResult(intent, MainBarcodeQRCodeActivity.RC_BARCODE_CAPTURE);
            finish();
        });
        btnSubmit.setOnClickListener(view -> {
            if (totalScanFromChildAct > 0) {
                Intent intent = new Intent(this, GoodsVerificationActivity.class);
                int totalScanAll = totalScanFromParentAct + totalScanFromChildAct;
                intent.putExtra(MiscUtil.TotalScanKey, Integer.toString(totalScanAll));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, Integer.toString(totalScanAll));
                Log.d(TAG, "initializeBtn: " + totalScanAll);
                Gson gson = new Gson();
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListScanned, gson.toJson(this.inboundDetailList));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                startActivity(intent);
                finish();
            } else {
                onBackPressed();

            }
        });
        btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsVerificationActivity.class);
            startActivity(intent);
            finish();
        });
//        btnSubmit.setVisibility(View.GONE);
    }
}