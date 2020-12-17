package com.multiqrscanner.inbound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.barcode.BarcodeCaptureActivity;
import com.multiqrscanner.barcode.MainBarcodeQRCodeActivity;
import com.multiqrscanner.inbound.adapter.ILoadMore;
import com.multiqrscanner.inbound.adapter.ScanResultAdapter;
import com.multiqrscanner.inbound.model.InboundDetail;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GoodsVerificationScanResultActivity extends AppCompatActivity {
    private static String TAG = "GVSRA";
    private TextView inboundNoVal;
    private TextView btnScan, btnSubmit, btnCancel;
    private ImageView btnBack;
    private Integer totalScanFromParentAct = 0;
    private Integer totalScanFromChildAct = 0;
    private Gson gson = new Gson();
    private String currentSelectedInboundNo;
    private List<InboundDetail> inboundDetailList;
    HashMap<String, InboundDetail> inboundMap;

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
                        for (QrCodeBarcodeSimpleWrapper qrCode : qrCodeBarcodeSimpleWrappers) {
                            if (inboundMapSharedPref.get(qrCode.getQrValue()) != null) {
                                InboundDetail inboundDetail = new InboundDetail(
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getLineNo(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getSku(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getSerialNo(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getProductName(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getQty(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getSubkey(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getStatus(),
                                        inboundMapSharedPref.get(qrCode.getQrValue()).getInputDate()
                                );
                                if (inboundDetail.getInputDate() == null || inboundDetail.getInputDate() == 0L) {
                                    inboundDetail.setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                }
                                inboundDetailDisplay.add(inboundDetail);
                                if (inboundMapSharedPref.get(qrCode.getQrValue()).getInputDate() == null || inboundMapSharedPref.get(qrCode.getQrValue()).getInputDate() == 0L) {
                                    inboundMapSharedPref.get(qrCode.getQrValue()).setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                }
                                if (inboundMapSharedPref.get(qrCode.getQrValue()).getStatus().trim().equalsIgnoreCase(GoodsVerificationActivity.StatusNotVerified)) {
                                    inboundMapSharedPref.get(qrCode.getQrValue()).setStatus(GoodsVerificationActivity.StatusVerified);
                                }
                            }
                        }
                        Log.d(TAG, "onCreate: inboundMapSharedPref edited " + inboundMapSharedPref);
                        Log.d(TAG, "onCreate: inboundDetailDisplay " + inboundDetailDisplay);
                        setInboundMap(inboundMapSharedPref);
                        this.inboundDetailList = inboundDetailDisplay;
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
        setAdapterData();
//        initTable();
        initializeBtn();
        if (totalScanFromChildAct > 0) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setClickable(true);
        }
    }

    public void setAdapterData() {
        ScanResultAdapter adapter;
        adapter = new ScanResultAdapter(recyclerView, this, inboundDetailList);
        recyclerView.setAdapter(adapter);
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
        });
        btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsVerificationActivity.class);
            startActivity(intent);
            finish();
        });
        btnSubmit.setVisibility(View.GONE);
    }
}