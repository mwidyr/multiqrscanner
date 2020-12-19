package com.multiqrscanner.outbound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.R;
import com.multiqrscanner.barcode.BarcodeCaptureActivity;
import com.multiqrscanner.barcode.MainBarcodeQRCodeActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.outbound.adapter.ILoadMore;
import com.multiqrscanner.outbound.adapter.ScanResultAdapter;
import com.multiqrscanner.outbound.model.OutboundDetail;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.multiqrscanner.qrcode.QrCodeProductValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GoodsShipmentScanResultActivity extends AppCompatActivity {
    private static String TAG = "GVSSRA";
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
    private List<OutboundDetail> outboundDetailList;
    private List<QrCodeProductValue> qrCodeProductValues;
    HashMap<String, OutboundDetail> inboundMap;
    private Integer totalIntInvalidInbound = 0;
    private Integer totalIntValidInbound = 0;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_shipment_scan_result);
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
                    HashMap<String, OutboundDetail> inboundMapSharedPref = gson.fromJson(inboundListString, new TypeToken<HashMap<String, OutboundDetail>>() {
                    }.getType());
                    Log.d(TAG, "onCreate: inboundMapSharedPref = " + inboundMapSharedPref);
                    if (inboundMapSharedPref.size() > 0) {
                        List<OutboundDetail> outboundDetailDisplay = new ArrayList<>();
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
                            String serialNoScan = qrCodeProductValue.getSerialNo().toString();
                            if (inboundMapSharedPref.get(serialNoScan) != null) {
                                OutboundDetail outboundDetail = new OutboundDetail(
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
                                if (outboundDetail.getInputDate() == null || outboundDetail.getInputDate() == 0L) {
                                    outboundDetail.setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                }
                                outboundDetailDisplay.add(outboundDetail);
                                if (inboundMapSharedPref.get(serialNoScan).getInputDate() == null || inboundMapSharedPref.get(serialNoScan).getInputDate() == 0L) {
                                    inboundMapSharedPref.get(serialNoScan).setInputDate(MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()));
                                }
                                if (inboundMapSharedPref.get(serialNoScan).getStatus().trim().equalsIgnoreCase(GoodsShipmentActivity.StatusNotVerified)) {
                                    inboundMapSharedPref.get(serialNoScan).setStatus(GoodsShipmentActivity.StatusVerified);
                                }
                            } else {
                                qrCodeProductValue.setValid(InvalidInbound);
                                totalIntInvalidInbound++;
                            }
                            qrCodeProductValuesDisplay.add(qrCodeProductValue);
                        }
                        setOutboundMap(inboundMapSharedPref);
                        this.outboundDetailList = outboundDetailDisplay;
                        this.qrCodeProductValues = qrCodeProductValuesDisplay;
                        Log.d(TAG, "onCreate: qrCodeProductValuesDisplay " + qrCodeProductValuesDisplay);
                        int totalInboundDisplayNotVerified = 0;
                        for (OutboundDetail outboundDetail : outboundDetailDisplay) {
                            if (!outboundDetail.getStatus().trim().equalsIgnoreCase(GoodsShipmentActivity.StatusVerified)) {
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

    private void setOutboundMap(HashMap<String, OutboundDetail> inboundMapInput) {
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
                Intent intent = new Intent(this, GoodsShipmentActivity.class);
                int totalScanAll = totalScanFromParentAct + totalScanFromChildAct;
                intent.putExtra(MiscUtil.TotalScanKey, Integer.toString(totalScanAll));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, Integer.toString(totalScanAll));
                Log.d(TAG, "initializeBtn: " + totalScanAll);
                Gson gson = new Gson();
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListScanned, gson.toJson(this.outboundDetailList));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                startActivity(intent);
                finish();
            }else{
                onBackPressed();
            }
        });
        btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsShipmentActivity.class);
            startActivity(intent);
            finish();
        });
//        btnSubmit.setVisibility(View.GONE);
    }
}