package com.multiqrscanner.inbound;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.multiqrscanner.inbound.model.InboundData;
import com.multiqrscanner.inbound.model.InboundDetail;
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
    private HashMap<String, InboundDetail> inboundMap;

    private TextView inboundDate, inboundCustomer, inboundWarehouse, totalScan, totalSummaryInbound;
    private Button verifConfirm, verifCancel, verifScan;

    private String[][] dataToShow = {};

    private static String[] HEADER_TO_SHOW = {"Line No", "SKU", "Serial No", "Product", "QTY", "SUBKEY"};

    private List<InboundData> inboundDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification);

        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundCustomer = findViewById(R.id.tv_inbound_customer_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);

        List<InboundDetail> inboundDetailList = new ArrayList<>();
        inboundDetailList.add(new InboundDetail("1", "8876", "109", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "110", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "111", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "112", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "113", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "114", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList.add(new InboundDetail("1", "8876", "115", "Susu Bendera", "1", "#LOT32", StatusNotVerified));

        List<InboundDetail> inboundDetailList2 = new ArrayList<>();
        inboundDetailList2.add(new InboundDetail("1", "8876", "119", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "120", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "121", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "122", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "123", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "124", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "125", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
        inboundDetailList2.add(new InboundDetail("1", "8876", "126", "Susu Bendera", "1", "#LOT32", StatusNotVerified));

        InboundData inboundData = new InboundData("WMS.IRC.100011", "15/11/2020", "PT. ZYH", "Warehouse 1", inboundDetailList2);
        InboundData inboundData2 = new InboundData("WMS.IRC.100012", "5/2/2020", "PT. HYZ", "Warehouse 2", inboundDetailList);
        InboundData inboundData3 = new InboundData("WMS.IRC.100013", "15/2/2020", "PT. HYZ", "Warehouse 3", inboundDetailList);
        InboundData inboundData4 = new InboundData("WMS.IRC.100014", "25/2/2020", "PT. HYZ", "Warehouse 4", inboundDetailList);
        InboundData inboundData5 = new InboundData("WMS.IRC.100015", "7/2/2020", "PT. HYZ", "Warehouse 5", inboundDetailList);
        InboundData inboundData6 = new InboundData("WMS.IRC.100016", "17/2/2020", "PT. HYZ", "Warehouse 6", inboundDetailList);
        InboundData inboundData7 = new InboundData("WMS.IRC.100017", "27/2/2020", "PT. HYZ", "Warehouse 7", inboundDetailList);
        InboundData inboundData8 = new InboundData("WMS.IRC.100018", "10/2/2020", "PT. HYZ", "Warehouse 8", inboundDetailList);
        this.inboundDatas = new ArrayList<>();
        this.inboundDatas.add(inboundData);
        this.inboundDatas.add(inboundData2);
        this.inboundDatas.add(inboundData3);
        this.inboundDatas.add(inboundData4);
        this.inboundDatas.add(inboundData5);
        this.inboundDatas.add(inboundData6);
        this.inboundDatas.add(inboundData7);
        this.inboundDatas.add(inboundData8);
        List<String> stringDummy = new ArrayList<>();
        stringDummy.add("");
        for (InboundData inboundData1 : inboundDatas) {
            stringDummy.add(inboundData1.getInboundNo());
        }
        initializeBtn();
        setTableData();
        Spinner spinner = findViewById(R.id.spinner_inbound_no);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_inbound, stringDummy);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        String selectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String item = (String) adapterView.getItemAtPosition(pos);
                if (!item.trim().equalsIgnoreCase("")) {
                    currentSelectedInboundNo = item;
                    for (InboundData data : inboundDatas) {
                        if (data.getInboundNo().trim().equalsIgnoreCase(item)) {
                            if (!currentSelectedInboundNo.trim().equalsIgnoreCase(selectedInboundNo)) {
                                clearSharedPreferences();
                                MiscUtil.getStringSharedPreferenceByKey(GoodsVerificationActivity.this, MiscUtil.TotalScanKey);
                                totalScanParent = 0;
                                totalScan.setText("0");
                            }
                            setInboundDetailValue(false, currentSelectedInboundNo, data);
                            injectData(data.getInboundDetailList());
                            setTableData();
                            verifScan.setVisibility(View.VISIBLE);
                            verifScan.setClickable(true);
                            break;
                        }
                    }
                } else {
                    setInboundDetailValue(false, currentSelectedInboundNo, new InboundData("", "", "", "", new ArrayList<>()));
                    verifScan.setVisibility(View.GONE);
                    setTableData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!selectedInboundNo.trim().equalsIgnoreCase("")) {
            currentSelectedInboundNo = selectedInboundNo;
            for (int i = 0; i < stringDummy.size(); i++) {
                if (currentSelectedInboundNo.trim().equalsIgnoreCase(stringDummy.get(i))) {
                    spinner.setSelection(i);
                    for (InboundData data : inboundDatas) {
                        if (data.getInboundNo().trim().equalsIgnoreCase(currentSelectedInboundNo)) {
                            setInboundDetailValue(false, currentSelectedInboundNo, data);
                            injectData(data.getInboundDetailList());
                            verifScan.setVisibility(View.VISIBLE);
                            verifScan.setClickable(true);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
        if (!totalScanExtra.trim().equalsIgnoreCase("")) {
            totalScanParent = Integer.parseInt(totalScanExtra);
            totalScan.setText(totalScanExtra);
            if (totalScanParent > 0) {
                verifConfirm.setVisibility(View.VISIBLE);
                verifConfirm.setClickable(true);
                verifCancel.setVisibility(View.VISIBLE);
                verifCancel.setClickable(true);
                verifScan.setVisibility(View.VISIBLE);
                verifScan.setClickable(true);
            }
        }
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
        SimpleTableDataAdapter simpleTableData = new SimpleTableDataAdapter(this, dataToShow);
        simpleTableData.setTextSize(10);
        tableView.setDataAdapter(simpleTableData);
        int colorEvenRows = getResources().getColor(R.color.WhiteSmoke);
        int colorOddRows = getResources().getColor(R.color.White);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
    }

    public void injectData(List<InboundDetail> inboundList) {
        if (inboundList.size() > 0) {
            HashMap<String, InboundDetail> inboundMap = new HashMap<>();
            for (InboundDetail data : inboundList) {
                inboundMap.put(data.getSerialNo(), new InboundDetail("1", data.getSku(), data.getSerialNo(), data.getProductName(), data.getQty(), data.getSubkey(), StatusNotVerified));
            }
            dataToShow = new String[inboundMap.size()][6];
            // Iterating over keys only
            int idx = 0;
            for (String key : inboundMap.keySet()) {
                InboundDetail inboundDetailData = inboundMap.get(key);
                if (inboundDetailData != null) {
                    int lineNumber = idx + 1;
                    dataToShow[idx][0] = Integer.toString(lineNumber);
                    dataToShow[idx][1] = inboundDetailData.getSku();
                    dataToShow[idx][2] = inboundDetailData.getSerialNo();
                    dataToShow[idx][3] = inboundDetailData.getProductName();
                    dataToShow[idx][4] = inboundDetailData.getQty();
                    dataToShow[idx][5] = inboundDetailData.getSubkey();
                    idx++;
                }
            }
            this.inboundMap = inboundMap;
        } else {
//            HashMap<String, InboundDetail> inboundMap = new HashMap<>();
//            inboundMap.put("109", new InboundDetail("1", "8876", "109", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("110", new InboundDetail("1", "8876", "110", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("111", new InboundDetail("1", "8876", "111", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("112", new InboundDetail("1", "8876", "112", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("113", new InboundDetail("1", "8876", "113", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("114", new InboundDetail("1", "8876", "114", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            inboundMap.put("115", new InboundDetail("1", "8876", "115", "Susu Bendera", "1", "#LOT32", StatusNotVerified));
//            dataToShow = new String[inboundMap.size()][6];
//            // Iterating over keys only
//            int idx = 0;
//            for (String key : inboundMap.keySet()) {
//                InboundDetail inboundDetailData = inboundMap.get(key);
//                if (inboundDetailData != null) {
//                    Integer lineNumber = idx + 1;
//                    dataToShow[idx][0] = lineNumber.toString();
//                    dataToShow[idx][1] = inboundDetailData.getSku();
//                    dataToShow[idx][2] = inboundDetailData.getSerialNo();
//                    dataToShow[idx][3] = inboundDetailData.getProductName();
//                    dataToShow[idx][4] = inboundDetailData.getQty();
//                    dataToShow[idx][5] = inboundDetailData.getSubkey();
//                    idx++;
//                }
//            }
//            this.inboundMap = inboundMap;
        }
    }

    public void initializeBtn() {
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifCancel = findViewById(R.id.btn_goods_verif_cancel);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifConfirm.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("Are you sure you want to Confirm this Scan?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearSharedPreferences();
                            onBackPressed();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
        verifCancel.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("Are you sure you want to Cancel this Scan?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            clearSharedPreferences();
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

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
                    List<InboundDetail> inboundDetailListScanned = gson.fromJson(inboundListScannedString, new TypeToken<ArrayList<InboundDetail>>() {
                    }.getType());
                    for (InboundDetail inboundDetail2 : inboundDetailListScanned) {
                        if (inboundMap.get(inboundDetail2.getSerialNo()) != null) {
                            Objects.requireNonNull(inboundMap.get(inboundDetail2.getSerialNo())).setStatus(StatusVerified);
                        }
                    }
                }
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                startActivity(intent);
            }
        });
        verifConfirm.setVisibility(View.GONE);
        verifCancel.setVisibility(View.GONE);
        verifScan.setVisibility(View.GONE);
    }

    public void clearSharedPreferences() {
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

    public void setInboundDetailValue(Boolean dummy, String inboundNo, InboundData inboundData) {
        if (!inboundNo.trim().equalsIgnoreCase("")) {
            if (dummy) {
//                if (inboundNo.equalsIgnoreCase("WMS.IRC.100011")) {
//                    inboundDate.setText("15/11/2020");
//                    inboundCustomer.setText("PT. ZYH");
//                    inboundWarehouse.setText("Warehouse 1");
//                    if (totalScanParent > 0) {
//                        totalScan.setText(totalScanParent.toString());
//                    } else {
//                        totalScan.setText("0");
//                    }
//                    totalSummaryInbound.setText("15");
//                } else if (inboundNo.equalsIgnoreCase("WMS.IRC.100012")) {
//                    inboundDate.setText("5/2/2020");
//                    inboundCustomer.setText("PT. HYZ");
//                    inboundWarehouse.setText("Warehouse 2");
//                    if (totalScanParent > 0) {
//                        totalScan.setText(totalScanParent.toString());
//                    } else {
//                        totalScan.setText("0");
//                    }
//                    totalSummaryInbound.setText("20");
//                }
            } else {
                //real value
                inboundDate.setText(inboundData.getInboundDate());
                inboundCustomer.setText(inboundData.getCustomer());
                inboundWarehouse.setText(inboundData.getWarehouse());
                if (totalScanParent > 0) {
                    totalScan.setText(totalScanParent.toString());
                } else {
                    totalScan.setText("0");
                }
                totalSummaryInbound.setText((inboundData.getInboundDetailList().size() + ""));
            }
        }
    }
}