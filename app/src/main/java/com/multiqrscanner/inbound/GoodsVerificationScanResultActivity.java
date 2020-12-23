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
import com.multiqrscanner.network.model.RetroUser;
import com.multiqrscanner.network.model.RetroWarehouse;
import com.multiqrscanner.network.user.GetInventoryService;
import com.multiqrscanner.network.user.GetLoginService;
import com.multiqrscanner.outbound.GoodsShipmentActivity;
import com.multiqrscanner.outbound.model.OutboundDetail;
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
    private String timeInMilistDB;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification_scan_result);
        inboundNoVal = findViewById(R.id.tv_goods_verif_result_inbound_no_val);
        btnScan = findViewById(R.id.tv_rescan);
        btnSubmit = findViewById(R.id.tv_submit);
        btnCancel = findViewById(R.id.tv_cancel);
        btnBack = findViewById(R.id.custom_top_bar_back_button);
        totalScan = findViewById(R.id.total_scan_result);
        totalValidInbound = findViewById(R.id.total_valid_inbound);
        totalInvalidInbound = findViewById(R.id.total_invalid_inbound);
        recyclerView = findViewById(R.id.goods_verif_result_detail);

        GetLoginService service = RetrofitClientInstance.getRetrofitInstance().create(GetLoginService.class);
        Call<GetTimeResponse> callSync = service.getServerTimeInMilis(new RetroUser());
        callSync.enqueue(new Callback<GetTimeResponse>() {
            @Override
            public void onResponse(Call<GetTimeResponse> call, Response<GetTimeResponse> response) {
                GetTimeResponse apiResponse = response.body();
                Log.d(TAG, "onResponse: " + response.body());
                if (apiResponse != null) {
                    timeInMilistDB = apiResponse.getDate();
                    Log.d(TAG, "onResponse: timeInMilistDB " + timeInMilistDB);
                    initOnCreate();
                }
            }

            @Override
            public void onFailure(Call<GetTimeResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: get time " + t.getMessage(), t);
                Toast.makeText(GoodsVerificationScanResultActivity.this, "Use Device Time...", Toast.LENGTH_SHORT).show();
                initOnCreate();
            }
        });


    }

    public void initOnCreate(){
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
                            if (serialNoScanLong == null) {
                                continue;
                            }
                            String serialNoScan = serialNoScanLong.toString();
                            qrCodeProductValue.setValid(InvalidInbound);
                            totalIntInvalidInbound++;
                            for (InboundDetail detail : inboundMapSharedPref.values()) {
                                Log.d(TAG, "initOnCreate: detail "+detail);
                                Log.d(TAG, "initOnCreate: qrCodeProductValue "+qrCodeProductValue);
                                Long detailSerialNo;
                                if(detail.getSerialNo().trim().equalsIgnoreCase("") ||
                                        qrCodeProductValue.getSerialNo().toString().trim().equalsIgnoreCase("") ){
                                    continue;
                                }
                                detailSerialNo = Long.parseLong(detail.getSerialNo().trim());
                                if (detailSerialNo.equals(qrCodeProductValue.getSerialNo())) {
                                    Log.d(TAG, "onCreate: "+detail);
                                    InboundDetail inboundDetail = new InboundDetail(
                                            Objects.requireNonNull(detail).getLineNo(),
                                            Objects.requireNonNull(detail).getSku(),
                                            Objects.requireNonNull(detail).getSerialNo(),
                                            Objects.requireNonNull(detail).getProductName(),
                                            Objects.requireNonNull(detail).getQty(),
                                            Objects.requireNonNull(detail).getSubkey(),
                                            Objects.requireNonNull(detail).getStatus(),
                                            Objects.requireNonNull(detail).getInputDate()
                                    );
                                    qrCodeProductValue.setValid(ValidInbound);
                                    totalIntValidInbound++;
                                    totalIntInvalidInbound--;
                                    if (inboundDetail.getInputDate() == null || inboundDetail.getInputDate() == 0L) {
                                        inboundDetail.setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                    }
                                    inboundDetailDisplay.add(inboundDetail);
                                    if (Objects.requireNonNull(detail).getInputDate() == null || Objects.requireNonNull(detail).getInputDate() == 0L) {
                                        Objects.requireNonNull(detail).setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                        if(timeInMilistDB != null && !timeInMilistDB.trim().equalsIgnoreCase("")){
                                            Objects.requireNonNull(detail).setInputDate(Long.parseLong(timeInMilistDB));
                                        }
                                    }
                                    if (Objects.requireNonNull(detail).getStatus().trim().equalsIgnoreCase(GoodsShipmentActivity.StatusNotVerified)) {
                                        Objects.requireNonNull(detail).setStatus(GoodsShipmentActivity.StatusVerified);
                                    }
                                }
                            }
                            qrCodeProductValuesDisplay.add(qrCodeProductValue);
                        }
                        setInboundMap(inboundMapSharedPref);
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
                Log.d(TAG, "initializeBtn: timeinmilistDB " + timeInMilistDB);
                Log.d(TAG, "initializeBtn: inboundMap " + inboundMap);
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