package com.multiqrscanner.inbound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.multiqrscanner.network.RetrofitClientInstance;
import com.multiqrscanner.network.RetrofitClientInstanceInbound;
import com.multiqrscanner.network.model.InboundItemDetail;
import com.multiqrscanner.network.model.InboundVerifySerialNo;
import com.multiqrscanner.network.model.RetroInboundId;
import com.multiqrscanner.network.model.RetroInboundVerifyRequest;
import com.multiqrscanner.network.model.RetroInbounds;
import com.multiqrscanner.network.model.RetroInboundsDetail;
import com.multiqrscanner.network.model.RetroInboundsVerifyResponse;
import com.multiqrscanner.network.model.RetroWarehouse;
import com.multiqrscanner.network.user.GetInboundsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsVerificationActivity extends AppCompatActivity {
    private static String TAG = "GVA";
    private String currentSelectedInboundNo = "";
    public static Integer totalScanParent = 0;
    public static String StatusVerified = "Y";
    public static String StatusNotVerified = "N";
    private static final String ALL_WAREHOUSE = "All Warehouse";

    private HashMap<String, InboundDetail> inboundMap;

    private TextView inboundDate, inboundCustomer, inboundWarehouse, totalScan, totalSummaryInbound;
    private Button verifConfirm, verifCancel, verifScan;
    private ProgressBar progressBar;

    private String[][] dataToShow = {};

    private static String[] HEADER_TO_SHOW = {"Line No", "SKU", "Serial No", "Product", "QTY", "SUBKEY"};

    private List<InboundData> inboundDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundCustomer = findViewById(R.id.tv_inbound_customer_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);

        String warehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS);
        if (warehouse.equalsIgnoreCase(ALL_WAREHOUSE)) {
            warehouse = "";
        }
        inboundDatas = new ArrayList<>();
        List<String> stringDummy = new ArrayList<>();
        stringDummy.add("Please Select Inbound");
        GetInboundsService service = RetrofitClientInstance.getRetrofitInstance().create(GetInboundsService.class);
        Call<RetroInbounds> call = service.getInbounds(new RetroWarehouse(warehouse));
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RetroInbounds>() {
            @Override
            public void onResponse(Call<RetroInbounds> call, Response<RetroInbounds> response) {
                if (response.body() != null && response.body().getInbounds() != null) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "getInbounds = " + response.body().getInbounds());
                    inboundDatas.addAll(response.body().getInbounds());
                    for (InboundData inboundData1 : inboundDatas) {
                        stringDummy.add(inboundData1.getInboundno());
                    }
                    initializeBtn();
                    setTableData();
                    Spinner spinner = findViewById(R.id.spinner_inbound_no);
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(GoodsVerificationActivity.this,
                            R.layout.spinner_item_inbound, stringDummy);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    spinner.setAdapter(adapter);
                    String selectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(GoodsVerificationActivity.this, MiscUtil.InboundNoKey);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                            Log.d(TAG, "adapter = " + (String) adapterView.getItemAtPosition(pos));
                            String item = (String) adapterView.getItemAtPosition(pos);
                            if (!item.trim().equalsIgnoreCase("")) {
                                currentSelectedInboundNo = item;
                                for (InboundData data : inboundDatas) {
                                    if (data.getInboundno().trim().equalsIgnoreCase(item)) {
                                        if (!currentSelectedInboundNo.trim().equalsIgnoreCase(selectedInboundNo)) {
                                            clearSharedPreferences();
                                            MiscUtil.getStringSharedPreferenceByKey(GoodsVerificationActivity.this, MiscUtil.TotalScanKey);
                                            totalScanParent = 0;
                                            totalScan.setText("0");
                                        }
                                        MiscUtil.saveStringSharedPreferenceAsString(GoodsVerificationActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        break;
                                    }
                                }
                            } else {
                                setInboundDetailValue(false, currentSelectedInboundNo, new InboundData("", "", "", "", "", new ArrayList<>()));
                                verifScan.setVisibility(View.GONE);
//                    setTableData();
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
                                    if (data.getInboundno().trim().equalsIgnoreCase(currentSelectedInboundNo)) {
                                        MiscUtil.saveStringSharedPreferenceAsString(GoodsVerificationActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
//                            setInboundDetailValue(false, currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(GoodsVerificationActivity.this, MiscUtil.TotalScanKey);
                    Log.d(TAG, "onResponse: " + totalScanExtra);
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
            }

            @Override
            public void onFailure(Call<RetroInbounds> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

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

    public void injectData(String inboundNo, InboundData data) {
        GetInboundsService service = RetrofitClientInstance.getRetrofitInstance().create(GetInboundsService.class);
        Call<RetroInboundsDetail> call = service.getInboundItemDetail(new RetroInboundId(inboundNo));
        //set loading bar
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RetroInboundsDetail>() {
            @Override
            public void onResponse(Call<RetroInboundsDetail> call, Response<RetroInboundsDetail> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getItems() != null && response.body().getItems().size() > 0) {
                    Log.d(TAG, "role = " + response.body().getItems());
                    List<InboundDetail> inboundList = new ArrayList<>();
                    for (InboundItemDetail inboundItemDetail : response.body().getItems()) {
                        if (inboundItemDetail.getSerialno() == null || inboundItemDetail.getSerialno().trim().equalsIgnoreCase("")) {
                            continue;
                        }
                        inboundList.add(new InboundDetail(inboundItemDetail.getLine() + "",
                                inboundItemDetail.getSku(), inboundItemDetail.getSerialno() == null ? "" : inboundItemDetail.getSerialno(),
                                inboundItemDetail.getProductname(), inboundItemDetail.getQty() + "",
                                inboundItemDetail.getSubkey() == null ? "" : inboundItemDetail.getSubkey(),
                                inboundItemDetail.getVerif()));
                    }
                    HashMap<String, InboundDetail> inboundMap = new HashMap<>();
                    for (InboundDetail data : inboundList) {
                        String defaultStatus = StatusNotVerified;
                        if (data.getSerialNo() == null) {
                            defaultStatus = StatusVerified;
                        }
                        inboundMap.put(data.getSerialNo(), new InboundDetail(data.getLineNo(), data.getSku(),
                                data.getSerialNo(), data.getProductName(), data.getQty(),
                                data.getSubkey(), defaultStatus));
                    }
                    dataToShow = new String[inboundMap.size()][6];
                    // Iterating over keys only
                    int idx = 0;
                    for (String key : inboundMap.keySet()) {
                        InboundDetail inboundDetailData = inboundMap.get(key);
                        if (inboundDetailData != null) {
                            int lineNumber = idx + 1;
                            dataToShow[idx][0] = inboundDetailData.getLineNo();
                            dataToShow[idx][1] = inboundDetailData.getSku();
                            dataToShow[idx][2] = inboundDetailData.getSerialNo();
                            dataToShow[idx][3] = inboundDetailData.getProductName();
                            dataToShow[idx][4] = inboundDetailData.getQty();
                            dataToShow[idx][5] = inboundDetailData.getSubkey();
                            idx++;
                        }
                    }
                    setInboundMap(inboundMap);
                    setTableData();
                    setInboundDetailValue(false, currentSelectedInboundNo, data);
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsDetail> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GoodsVerificationActivity.this, "Failed Retrieve Inbound Detail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setInboundMap(HashMap<String, InboundDetail> inboundMap) {
        this.inboundMap = inboundMap;
    }

    public void initializeBtn() {
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifCancel = findViewById(R.id.btn_goods_verif_cancel);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifConfirm.setOnClickListener(view -> {
            if (Integer.valueOf(String.valueOf(totalScan.getText())).equals(Integer.valueOf(String.valueOf(totalSummaryInbound.getText())))) {
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("Are you sure you want to Confirm this Scan?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                verifyInboundDetails();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                Toast.makeText(this, "Jumlah Scan tidak sama dengan jumlah summary inbounds", Toast.LENGTH_SHORT).show();
            }

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


    public String convertAPIDateToUIDate(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatterFE = new SimpleDateFormat("dd/MM/yyy");

        try {

            Date date = formatter.parse(dateInString.replaceAll("Z$", "+0000"));
            System.out.println(date);

            return formatterFE.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInString;
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
                inboundDate.setText(convertAPIDateToUIDate(inboundData.getInbounddate()));
                inboundCustomer.setText(inboundData.getCustomer());
                inboundWarehouse.setText(inboundData.getWarehouse());
                if (totalScanParent > 0) {
                    totalScan.setText(totalScanParent.toString());
                } else {
                    totalScan.setText("0");
                }
                Long inboundMapSize = this.inboundMap == null ? 0L : this.inboundMap.size();
                totalSummaryInbound.setText(inboundMapSize + "");
            }
        }
    }

    public void verifyInboundDetails() {
        progressBar.setVisibility(View.VISIBLE);
        GetInboundsService service = RetrofitClientInstanceInbound.getRetrofitInstanceInbound().create(GetInboundsService.class);
        String idWarehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWSID);
        String userID = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityUserID);
        Log.d(TAG, "verifyInboundDetails: id warehouse " + idWarehouse + " userID = " + userID);
        List<InboundVerifySerialNo> listSerialNo = new ArrayList<>();
        for (InboundDetail inboundDetail : inboundMap.values()) {
            if (inboundDetail.getStatus().trim().equalsIgnoreCase(StatusVerified)) {
                listSerialNo.add(new InboundVerifySerialNo(inboundDetail.getSerialNo()));
            }
        }
        Call<RetroInboundsVerifyResponse> call = service.verifyInboundItemDetail(new RetroInboundVerifyRequest(idWarehouse, userID, StatusVerified, listSerialNo));
        call.enqueue(new Callback<RetroInboundsVerifyResponse>() {
            @Override
            public void onResponse(Call<RetroInboundsVerifyResponse> call, Response<RetroInboundsVerifyResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getResultCode() > 0) {
                    Toast.makeText(GoodsVerificationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearSharedPreferences();
                    onBackPressed();
                } else {
                    Toast.makeText(GoodsVerificationActivity.this, "Failed verify inbound", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsVerifyResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(GoodsVerificationActivity.this, "Failed to connect to api", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}