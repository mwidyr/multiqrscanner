package com.multiqrscanner.inbound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.R;
import com.multiqrscanner.ScannerMainActivity;
import com.multiqrscanner.inbound.model.Inbound;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.navdrawer.NavigationViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class GoodsVerificationActivity extends AppCompatActivity {
    private static String TAG = "GVA";
    private String currentSelectedInboundNo = "";
    public static Integer totalScanParent = 0;
    public static String StatusVerified = "Yes";
    public static String StatusNotVerified = "No";
    private HashMap<String, Inbound> inboundMap;

    private TextView inboundDate, inboundCustomer, inboundWarehouse, totalScan, totalSummaryInbound;
    private Button verifConfirm, verifCancel, verifScan;

    private String[][] dataToShow = {};

    private static String[] HEADER_TO_SHOW = {"Line No", "SKU", "Serial No", "Product", "QTY", "SUBKEY"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification);

        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundCustomer = findViewById(R.id.tv_inbound_customer_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);
        Intent intent = getIntent();
//        String totalScanExtra = intent.getStringExtra(MiscUtil.TotalScanKey);
        String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
        if (totalScanExtra != null && !totalScanExtra.trim().equalsIgnoreCase("")) {
            Log.d(TAG, "onCreate: total Scan = " + totalScanExtra);
            totalScanParent = Integer.parseInt(totalScanExtra);
            totalScan.setText(totalScanExtra);
        }


        Spinner spinner = findViewById(R.id.spinner_inbound_no);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dummy_inbound_no, R.layout.spinner_item_inbound);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String item = (String) adapterView.getItemAtPosition(pos);
                Log.d(TAG, item);
                if (!item.trim().equalsIgnoreCase("")) {
                    currentSelectedInboundNo = item;
                    setInboundDetailValue(true, currentSelectedInboundNo);
                }
                Toast.makeText(GoodsVerificationActivity.this, item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initializeBtn();
        setTableData();
    }

    public void setTableData() {
        TableView<String[]> tableView = findViewById(R.id.table_view_goods_verif_page);
        SimpleTableHeaderAdapter simpleHeader = new SimpleTableHeaderAdapter(this, HEADER_TO_SHOW);
        simpleHeader.setTextSize(12);
        tableView.setHeaderAdapter(simpleHeader);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(this, 6, 200);
        columnModel.setColumnWidth(0, 70);
        columnModel.setColumnWidth(1, 60);
        columnModel.setColumnWidth(2, 70);
        columnModel.setColumnWidth(3, 100);
        columnModel.setColumnWidth(4, 50);
        columnModel.setColumnWidth(5, 80);
        tableView.setColumnModel(columnModel);
        injectData();
        SimpleTableDataAdapter simpleTableData = new SimpleTableDataAdapter(this, dataToShow);
        simpleTableData.setTextSize(10);
        tableView.setDataAdapter(simpleTableData);
        int colorEvenRows = getResources().getColor(R.color.LightSlateGray);
        int colorOddRows = getResources().getColor(R.color.Gray);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
    }

    public void injectData() {
        HashMap<String, Inbound> inboundMap = new HashMap<>();
        inboundMap.put("109", new Inbound("1", "8876", "109", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("110", new Inbound("1", "8876", "110", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("111", new Inbound("1", "8876", "111", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("112", new Inbound("1", "8876", "112", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("113", new Inbound("1", "8876", "113", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("114", new Inbound("1", "8876", "114", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundMap.put("115", new Inbound("1", "8876", "115", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        dataToShow = new String[inboundMap.size()][6];
        // Iterating over keys only
        int idx = 0;
        for (String key : inboundMap.keySet()) {
            Inbound inboundData = inboundMap.get(key);
            if (inboundData != null) {
                Integer lineNumber = idx + 1;
                dataToShow[idx][0] = lineNumber.toString();
                dataToShow[idx][1] = inboundData.getSku();
                dataToShow[idx][2] = inboundData.getSerialNo();
                dataToShow[idx][3] = inboundData.getProductName();
                dataToShow[idx][4] = inboundData.getQty();
                dataToShow[idx][5] = inboundData.getSubkey();
                idx++;
            }
        }
        this.inboundMap = inboundMap;
    }

    public void initializeBtn() {
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifCancel = findViewById(R.id.btn_goods_verif_cancel);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifConfirm.setOnClickListener(view -> {
            Toast.makeText(this, "Confirm Scan Result", Toast.LENGTH_SHORT).show();
            clearSharedPreferences();
        });
        verifCancel.setOnClickListener(view -> {
            Toast.makeText(this, "Cancel Scan Result", Toast.LENGTH_SHORT).show();
            clearSharedPreferences();
            onBackPressed();
        });
        verifScan.setOnClickListener(view -> {
            if (currentSelectedInboundNo.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose inbound no first", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, ScannerMainActivity.class);
                intent.putExtra(MiscUtil.TotalScanKey, totalScanParent.toString());
                intent.putExtra(MiscUtil.FromActivityKey, MiscUtil.GoodsVerificationValue);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.FromActivityKey, MiscUtil.GoodsVerificationValue);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                // use getter and setter to set inboundlist in shared preference
                Gson gson = new Gson();
                String inboundListScannedString = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundListScanned);
                if (!inboundListScannedString.trim().equalsIgnoreCase("")) {
                    List<Inbound> inboundListScanned = gson.fromJson(inboundListScannedString, new TypeToken<ArrayList<Inbound>>() {
                    }.getType());
                    for (Inbound inbound2 : inboundListScanned) {
                        if (inboundMap.get(inbound2.getSerialNo()) != null) {
                            Objects.requireNonNull(inboundMap.get(inbound2.getSerialNo())).setStatus(StatusVerified);
                        }
                    }
                }
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                startActivity(intent);
            }
        });
    }

    public void clearSharedPreferences(){
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListScanned);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearSharedPreferences();
        Intent intent = new Intent(this, NavigationViewActivity.class);
        startActivity(intent);
        finish();
    }

    public void setInboundDetailValue(Boolean dummy, String inboundNo) {
        if (!inboundNo.trim().equalsIgnoreCase("")) {
            if (dummy) {
                if (inboundNo.equalsIgnoreCase("WMS.IRC.100011")) {
                    inboundDate.setText("15/11/2020");
                    inboundCustomer.setText("PT. ZYH");
                    inboundWarehouse.setText("Warehouse 1");
                    if (totalScanParent > 0) {
                        totalScan.setText(totalScanParent.toString());
                    } else {
                        totalScan.setText("0");
                    }
                    totalSummaryInbound.setText("15");
                } else if (inboundNo.equalsIgnoreCase("WMS.IRC.100012")) {
                    inboundDate.setText("5/2/2020");
                    inboundCustomer.setText("PT. HYZ");
                    inboundWarehouse.setText("Warehouse 2");
                    if (totalScanParent > 0) {
                        totalScan.setText(totalScanParent.toString());
                    } else {
                        totalScan.setText("0");
                    }
                    totalSummaryInbound.setText("20");
                }
            } else {
                //real value
                inboundDate.setText("");
                inboundCustomer.setText("");
                inboundWarehouse.setText("");
                totalScan.setText("");
                totalSummaryInbound.setText("");
            }
        }
    }
}