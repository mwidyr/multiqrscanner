package com.multiqrscanner.inbound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.ScannerMainActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.qrcode.QrCodeDetectActivity;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class GoodsVerificationScanResultActivity extends AppCompatActivity {
    private static String TAG = "GVSRA";
    private TextView inboundNoVal;
    private Button btnScan, btnConfirm;
    private Integer totalScanFromParentAct = 0;
    private Integer totalScanFromChildAct = 0;
    private Gson gson = new Gson();
    private String currentSelectedInboundNo;
    private ImageView imageView;

    private String[][] dataToShow = {
//            {"Bear Brand", "001", "No"},
//            {"Bear Brand", "002", "No"},
//            {"Bear Brand", "003", "Yes"},
    };

    private static String[] HEADER_TO_SHOW = {"Product", "Serial No", "Status"};

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
        String totalScanExtra = intent.getStringExtra(MiscUtil.TotalScanKey);
        if (totalScanExtra != null && !totalScanExtra.trim().equalsIgnoreCase("")) {
            Log.d(TAG, "onCreate: totalScan = " + totalScanExtra);
            totalScanFromParentAct = Integer.parseInt(totalScanExtra);
        }else{
            totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(this,MiscUtil.TotalScanKey);
            totalScanFromParentAct = Integer.parseInt(totalScanExtra);
        }
//        String imagePathExtra = intent.getStringExtra(MiscUtil.ImagePathKey);
        String imagePathExtra = MiscUtil.getStringSharedPreferenceByKey(this,MiscUtil.ImagePathKey);
        Log.d(TAG, "onCreate: imagePath "+imagePathExtra);
        if (imagePathExtra != null && !imagePathExtra.trim().equalsIgnoreCase("")) {
            imageView = findViewById(R.id.iv_goods_verif_scan_result);
            Bitmap imageBitmap = readImageFromUri(imagePathExtra);
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap);
            }
            // done set image
        }
//        String qrCodeListExtra = intent.getStringExtra(MiscUtil.QrCodeGsonKey);
        String qrCodeListExtra = MiscUtil.getStringSharedPreferenceByKey(this,MiscUtil.QrCodeGsonKey);
        if (qrCodeListExtra != null && !qrCodeListExtra.trim().equalsIgnoreCase("")) {
            List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrappers = gson.fromJson(qrCodeListExtra, new TypeToken<ArrayList<QrCodeBarcodeSimpleWrapper>>() {
            }.getType());
            if (qrCodeBarcodeSimpleWrappers.size() > 0) {
                dataToShow = new String[qrCodeBarcodeSimpleWrappers.size()][3];
                for (int i = 0; i < qrCodeBarcodeSimpleWrappers.size(); i++) {
                    dataToShow[i][0] = qrCodeBarcodeSimpleWrappers.get(i).getQrValue();
                    dataToShow[i][1] = qrCodeBarcodeSimpleWrappers.get(i).getCount();
                    dataToShow[i][2] = "Yes";
                }
                totalScanFromChildAct = qrCodeBarcodeSimpleWrappers.size();
            }
        }

//        List<InboundResult> myData = new ArrayList<>();
//        myData.add(new InboundResult("Bear Brand", "001", "No"));
//        myData.add(new InboundResult("Bear Brand", "002", "Yes"));
//        myData.add(new InboundResult("Bear Brand", "003", "No"));

        TableView<String[]> tableView = findViewById(R.id.table_view_goods_verif_result);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, HEADER_TO_SHOW));
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(this, 3, 150);
        columnModel.setColumnWidth(1, 100);
        columnModel.setColumnWidth(2, 80);
        tableView.setColumnModel(columnModel);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, dataToShow));
        int colorEvenRows = getResources().getColor(R.color.LightSlateGray);
        int colorOddRows = getResources().getColor(R.color.Gray);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        initializeBtn();
    }

    public void initializeBtn() {
        btnScan = findViewById(R.id.btn_goods_verif_result_scan);
        btnConfirm = findViewById(R.id.btn_goods_verif_result_confirm);
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
        btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsVerificationActivity.class);
            int totalScanAll = totalScanFromParentAct + totalScanFromChildAct;
            Log.d(TAG, "initializeBtn: totalParent = " + totalScanFromParentAct + " totalChild = " + totalScanFromChildAct);
            intent.putExtra(MiscUtil.TotalScanKey, Integer.toString(totalScanAll));
            startActivity(intent);
            finish();
        });
    }
}