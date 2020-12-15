package com.multiqrscanner.inbound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.ScannerMainActivity;
import com.multiqrscanner.inbound.model.InboundDetail;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class GoodsVerificationScanResultActivity extends AppCompatActivity {
    private static String TAG = "GVSRA";
    private TextView inboundNoVal;
    private Button btnScan, btnSubmit, btnCancel;
    private Integer totalScanFromParentAct = 0;
    private Integer totalScanFromChildAct = 0;
    private Gson gson = new Gson();
    private String currentSelectedInboundNo;
    private ImageView imageView;
    private List<InboundDetail> inboundDetailList;
    private String[][] dataToShow = {};
    HashMap<String, InboundDetail> inboundMap;
    private static String[] HEADER_TO_SHOW = {"Product", "SN", "Status"};

    public Bitmap readImageFromUri(String filePath) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

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

        String bitmapArrayExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.ImagePathKey);
        if (!bitmapArrayExtra.trim().equalsIgnoreCase("")) {
            imageView = findViewById(R.id.iv_goods_verif_scan_result);
            byte[] byteImage = gson.fromJson(bitmapArrayExtra, byte[].class);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            ;
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap);
            }
        }

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
                        dataToShow = new String[inboundDetailDisplay.size()][3];
                        for (int i = 0; i < inboundDetailDisplay.size(); i++) {
                            dataToShow[i][0] = inboundDetailDisplay.get(i).getProductName();
                            dataToShow[i][1] = inboundDetailDisplay.get(i).getSerialNo();
                            dataToShow[i][2] = inboundDetailDisplay.get(i).getStatus();
                        }
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
        initTable();
        initializeBtn();
        if (totalScanFromChildAct > 0) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setClickable(true);
        }
    }

    private void setInboundMap(HashMap<String, InboundDetail> inboundMapInput) {
        this.inboundMap = inboundMapInput;
    }

    public void initTable() {
        TableView<String[]> tableView = findViewById(R.id.table_view_goods_verif_result);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, HEADER_TO_SHOW));
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(this, 3, 150);
        columnModel.setColumnWidth(0, 150);
        columnModel.setColumnWidth(1, 80);
        columnModel.setColumnWidth(2, 80);
        tableView.setColumnModel(columnModel);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, dataToShow));
        int colorEvenRows = getResources().getColor(R.color.LightSlateGray);
        int colorOddRows = getResources().getColor(R.color.Gray);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
    }

    public void initializeBtn() {
        btnScan = findViewById(R.id.btn_goods_verif_result_scan);
        btnSubmit = findViewById(R.id.btn_goods_verif_result_confirm);
        btnCancel = findViewById(R.id.btn_goods_verif_cancel);
        btnScan.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScannerMainActivity.class);
            intent.putExtra(MiscUtil.TotalScanKey, totalScanFromParentAct.toString());
            intent.putExtra(MiscUtil.FromActivityKey, MiscUtil.GoodsVerificationValue);
            intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
            MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.FromActivityKey, MiscUtil.GoodsVerificationValue);
            MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParentAct.toString());
            MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
            startActivity(intent);
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