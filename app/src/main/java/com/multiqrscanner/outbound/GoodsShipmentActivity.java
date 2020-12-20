package com.multiqrscanner.outbound;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.R;
import com.multiqrscanner.barcode.BarcodeCaptureActivity;
import com.multiqrscanner.barcode.MainBarcodeQRCodeActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.navdrawer.NavigationViewActivity;
import com.multiqrscanner.network.RetrofitClientInstance;
import com.multiqrscanner.network.RetrofitClientInstanceInbound;
import com.multiqrscanner.network.RetrofitClientInstanceOutbound;
import com.multiqrscanner.network.model.InboundItemDetail;
import com.multiqrscanner.network.model.InboundVerifySKU;
import com.multiqrscanner.network.model.InboundVerifySerialNo;
import com.multiqrscanner.network.model.RetroInboundId;
import com.multiqrscanner.network.model.RetroInboundVerifyRequest;
import com.multiqrscanner.network.model.RetroInboundsDetail;
import com.multiqrscanner.network.model.RetroInboundsVerifyResponse;
import com.multiqrscanner.network.model.RetroOutbounds;
import com.multiqrscanner.network.model.RetroWarehouse;
import com.multiqrscanner.network.user.GetInboundsService;
import com.multiqrscanner.network.user.GetOutboundsService;
import com.multiqrscanner.outbound.adapter.ILoadMore;
import com.multiqrscanner.outbound.adapter.OutboundsDetailsAdapter;
import com.multiqrscanner.outbound.model.OutboundData;
import com.multiqrscanner.outbound.model.OutboundDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsShipmentActivity extends AppCompatActivity {
    private static String TAG = "GSA";
    private String currentSelectedInboundNo = "";
    public static Integer totalScanParent = 0;
    public static Integer totalScanVerified = 0;
    public static String StatusVerified = "Y";
    public static String StatusNotVerified = "N";
    private static final String ALL_WAREHOUSE = "All Warehouse";

    private HashMap<String, OutboundDetail> inboundMap;

    private TextView inboundDate, inboundCustomer, inboundWarehouse, totalScan, totalSummaryInbound, searchButton, verifViewDetailTv, verifScanTv;
    private LinearLayout verifConfirm, verifViewDetail, verifScan, verifClear;
    private ConstraintLayout totalScanConstrainLayout;
    private ImageView verifCancel;
    private ProgressBar progressBar;
    private AutoCompleteTextView inboundNoTextView;
    private View view;
    private String[][] dataToShow = {};

    List<String> listInboundNos = new ArrayList<>();

    private List<OutboundData> outboundData;
    RecyclerView recyclerView;
    List<OutboundDetail> items = new ArrayList<>();

    public void setAdapterData() {
        OutboundsDetailsAdapter adapter;
        adapter = new OutboundsDetailsAdapter(recyclerView, this, items);
        recyclerView.setAdapter(adapter);
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_shipment);
        view = findViewById(R.id.background_loading);
        view.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundCustomer = findViewById(R.id.tv_inbound_customer_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);
        recyclerView = findViewById(R.id.goods_verif_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapterData();
        recyclerView.setVisibility(View.GONE);

        String warehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS);
        if (warehouse.equalsIgnoreCase(ALL_WAREHOUSE)) {
            warehouse = "";
        }
        outboundData = new ArrayList<>();
        GetOutboundsService service = RetrofitClientInstanceOutbound.getRetrofitInstanceOutbound().create(GetOutboundsService.class);
        Call<RetroOutbounds> call = service.getOutbounds(new RetroWarehouse(warehouse));
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RetroOutbounds>() {
            @Override
            public void onResponse(Call<RetroOutbounds> call, Response<RetroOutbounds> response) {
                if (response.body() != null && response.body().getInbounds() != null) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "getInbounds = " + response.body().getInbounds());
                    outboundData.addAll(response.body().getInbounds());
                    for (OutboundData outboundData1 : outboundData) {
                        listInboundNos.add(outboundData1.getInboundno());
                    }
                    initializeBtn();
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(GoodsShipmentActivity.this,
                            R.layout.spinner_item_inbound, listInboundNos);
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    inboundNoTextView.setAdapter(adapter);
                    String selectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(GoodsShipmentActivity.this, MiscUtil.InboundNoKey);
                    inboundNoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                            Log.d(TAG, "adapter = " + (String) adapterView.getItemAtPosition(pos));
                            String item = (String) adapterView.getItemAtPosition(pos);
                            if (!item.trim().equalsIgnoreCase("")) {
                                currentSelectedInboundNo = item;
                                Log.d(TAG, "onItemClick: " + outboundData);
                                for (OutboundData data : outboundData) {
                                    if (data.getInboundno().trim().equalsIgnoreCase(item)) {
                                        if (!currentSelectedInboundNo.trim().equalsIgnoreCase(selectedInboundNo)) {
                                            clearSharedPreferences();
                                            MiscUtil.getStringSharedPreferenceByKey(GoodsShipmentActivity.this, MiscUtil.TotalScanKey);
                                            totalScanParent = 0;
                                            totalScan.setText("0");
                                        }
                                        MiscUtil.saveStringSharedPreferenceAsString(GoodsShipmentActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        break;
                                    }
                                }
                            } else {
                                setInboundDetailValue(false, currentSelectedInboundNo, new OutboundData("", "", "", "", "", new ArrayList<>()));
                                verifScan.setVisibility(View.GONE);
//                    setTableData();
                            }
                        }
                    });

                    if (!selectedInboundNo.trim().equalsIgnoreCase("")) {
                        currentSelectedInboundNo = selectedInboundNo;
                        for (int i = 0; i < listInboundNos.size(); i++) {
                            if (currentSelectedInboundNo.trim().equalsIgnoreCase(listInboundNos.get(i))) {
                                inboundNoTextView.setText(currentSelectedInboundNo);
                                for (OutboundData data : outboundData) {
                                    if (data.getInboundno().trim().equalsIgnoreCase(currentSelectedInboundNo)) {
                                        MiscUtil.saveStringSharedPreferenceAsString(GoodsShipmentActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(GoodsShipmentActivity.this, MiscUtil.TotalScanKey);
                    Log.d(TAG, "onResponse: " + totalScanExtra);
                    if (!totalScanExtra.trim().equalsIgnoreCase("")) {
                        totalScanParent = Integer.parseInt(totalScanExtra);
                        totalScan.setText(totalScanExtra);
                        if (totalScanParent > 0) {
                            verifConfirm.setVisibility(View.VISIBLE);
                            verifConfirm.setClickable(true);
                            verifViewDetail.setVisibility(View.VISIBLE);
                            verifViewDetail.setClickable(true);
                            verifScan.setVisibility(View.VISIBLE);
                            verifScan.setClickable(true);
                            verifClear.setVisibility(View.VISIBLE);
                            verifClear.setClickable(true);
                            totalScanConstrainLayout.setVisibility(View.VISIBLE);
                            setSearchButtonInbound();
                            verifScanTv.setText("Rescan");
                        }
                    } else {
                        verifScanTv.setText("Scan");
                    }
                }
            }

            @Override
            public void onFailure(Call<RetroOutbounds> call, Throwable t) {
                Toast.makeText(GoodsShipmentActivity.this, "Failed retrieve data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    public void injectData(String inboundNo, OutboundData data) {
        Log.d(TAG, "injectData: " + inboundNo + " - " + data);
        GetOutboundsService service = RetrofitClientInstanceOutbound.getRetrofitInstanceOutbound().create(GetOutboundsService.class);
        Call<RetroInboundsDetail> call = service.getOutboundItemDetail(new RetroInboundId(data.getId()));
        call.enqueue(new Callback<RetroInboundsDetail>() {
            @Override
            public void onResponse(Call<RetroInboundsDetail> call, Response<RetroInboundsDetail> response) {
                if (response.body() != null && response.body().getItems() != null && response.body().getItems().size() > 0) {
                    Log.d(TAG, "role = " + response.body().getItems());
                    List<OutboundDetail> inboundList = new ArrayList<>();
                    for (InboundItemDetail inboundItemDetail : response.body().getItems()) {
                        Log.d(TAG, "onResponse: " + inboundItemDetail);
                        //temporary doesnt use serial no
//                        if (inboundItemDetail.getSerialno() == null || inboundItemDetail.getSerialno().trim().equalsIgnoreCase("")) {
//                            continue;
//                        }
                        inboundList.add(new OutboundDetail(inboundItemDetail.getLine() + "",
                                inboundItemDetail.getSku(), inboundItemDetail.getSerialno() == null ? "" : inboundItemDetail.getSerialno(),
                                inboundItemDetail.getProductname(), inboundItemDetail.getQty() + "",
                                inboundItemDetail.getSubkey() == null ? "" : inboundItemDetail.getSubkey(),
                                inboundItemDetail.getVerif(), 0L));
                    }
                    Log.d(TAG, "onResponse: " + inboundList);
                    HashMap<String, OutboundDetail> inboundMap = new HashMap<>();
                    int idxA = 1;
                    for (OutboundDetail data : inboundList) {
                        Log.d(TAG, "onResponse: ");
                        String defaultStatus = StatusNotVerified;
                        if (data.getSerialNo() == null) {
                            defaultStatus = StatusVerified;
                        }
                        inboundMap.put(Integer.toString(idxA), new OutboundDetail(data.getLineNo(), data.getSku(),
                                data.getSerialNo(), data.getProductName(), data.getQty(),
                                data.getSubkey(), defaultStatus, data.getInputDate()));
                        idxA++;
                    }

                    String[][] dataToShowTemp = new String[inboundMap.size()][6];
                    Log.d(TAG, "onResponse:setDataToVerifiedAndSavedPref before verif");

                    Gson gson = new Gson();
                    String inboundMapString = MiscUtil.getStringSharedPreferenceByKey(GoodsShipmentActivity.this, MiscUtil.InboundListDetail);
                    Log.d(TAG, "onResponse: sharedPref " + inboundMapString);
                    Log.d(TAG, "onResponse: savedMap " + inboundMap);
                    if (!inboundMapString.trim().equalsIgnoreCase("")) {
                        HashMap<String, OutboundDetail> inboundDetailListScanned = gson.fromJson(inboundMapString, new TypeToken<HashMap<String, OutboundDetail>>() {
                        }.getType());
                        Log.d(TAG, "onResponse: inboundDetailListScanned " + inboundDetailListScanned);
                        inboundMap = inboundDetailListScanned;
                    }
                    setInboundMap(inboundMap);
                    Log.d(TAG, "onResponse: after edit savedMap " + inboundMap);

                    List<OutboundDetail> itemOutboundDetail = new ArrayList<>();
                    // Iterating over keys only
                    int idx = 0;
                    for (String key : inboundMap.keySet()) {
                        OutboundDetail outboundDetailData = inboundMap.get(key);
                        if (outboundDetailData != null && outboundDetailData.getStatus().equalsIgnoreCase(StatusVerified)) {
                            dataToShowTemp[idx][0] = outboundDetailData.getLineNo();
                            dataToShowTemp[idx][1] = outboundDetailData.getSku();
                            dataToShowTemp[idx][2] = outboundDetailData.getSerialNo();
                            dataToShowTemp[idx][3] = outboundDetailData.getProductName();
                            dataToShowTemp[idx][4] = outboundDetailData.getQty();
                            dataToShowTemp[idx][5] = outboundDetailData.getSubkey();
                            itemOutboundDetail.add(outboundDetailData);
                            idx++;
                        }
                    }
                    items = itemOutboundDetail;
                    setAdapterData();

                    totalScanVerified = idx;
                    dataToShow = new String[idx][6];
                    for (int i = 0; i < idx; i++) {
                        Log.d(TAG, "onResponse: " + Arrays.toString(dataToShowTemp[i]));
                        dataToShow[i][0] = dataToShowTemp[i][0];
                        dataToShow[i][1] = dataToShowTemp[i][1];
                        dataToShow[i][2] = dataToShowTemp[i][2];
                        dataToShow[i][3] = dataToShowTemp[i][3];
                        dataToShow[i][4] = dataToShowTemp[i][4];
                        dataToShow[i][5] = dataToShowTemp[i][5];
                        Log.d(TAG, "onResponse: " + Arrays.toString(dataToShow[i]));
                    }
                    setInboundDetailValue(false, currentSelectedInboundNo, data);
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsDetail> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(GoodsShipmentActivity.this, "Failed Retrieve Inbound Detail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setInboundMap(HashMap<String, OutboundDetail> inboundMap) {
        this.inboundMap = inboundMap;
    }

    public void setSearchButtonInbound() {
        if (inboundNoTextView.getText().toString().trim().equalsIgnoreCase("")) {
            inboundNoTextView.setThreshold(1);
            inboundNoTextView.setClickable(true);
            searchButton.setBackground(getResources().getDrawable(R.drawable.round_green_borderless));
            inboundNoTextView.setEnabled(true);
        } else {
            inboundNoTextView.setThreshold(1);
            inboundNoTextView.setClickable(false);
            searchButton.setBackground(getResources().getDrawable(R.drawable.round_gray_borderless));
            inboundNoTextView.setEnabled(false);
        }
    }

    public void initializeBtn() {
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifViewDetail = findViewById(R.id.btn_goods_verif_view_detail);
        verifCancel = findViewById(R.id.custom_top_bar_back_button);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifClear = findViewById(R.id.btn_goods_verif_clear);
        verifViewDetailTv = findViewById(R.id.verif_view_detail_text);
        verifScanTv = findViewById(R.id.verif_btn_scan_tv);
        totalScanConstrainLayout = findViewById(R.id.constraintLayout_total_scan);
        searchButton = findViewById(R.id.btn_search_inbound);
        inboundNoTextView = findViewById(R.id.spinner_inbound_no);
        setSearchButtonInbound();
        searchButton.setOnClickListener(view1 -> {
            inboundNoTextView.showDropDown();
        });
        verifViewDetail.setOnClickListener(view -> {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
                verifViewDetailTv.setText("Hide");
            } else {
                recyclerView.setVisibility(View.GONE);
                verifViewDetailTv.setText("Show");
            }
        });
        verifConfirm.setOnClickListener(view -> {
            MiscUtil.CustomDialogClass cdc = MiscUtil.customAlertDialog(this, "Confirm?", "This action will confirm the data that you have scanned");
            View.OnClickListener confirmListener = viewA -> {
                verifyInboundDetails();
                cdc.getAlertDialog().dismiss();
            };
            View.OnClickListener cancelListener = viewB -> {
                cdc.getAlertDialog().dismiss();
            };
            MiscUtil.setDialogOnClickListenerAndShow(cdc.getAlertDialog(), cdc.getDialogView(), confirmListener, cancelListener);
        });
        verifClear.setOnClickListener(view -> {
            MiscUtil.CustomDialogClass cdc = MiscUtil.customAlertDialog(this, "Cancel?", "After you cancel, the data is lost and cannot be canceled");
            View.OnClickListener confirmListener = viewA -> {
                clearAllData();
                cdc.getAlertDialog().dismiss();
            };
            View.OnClickListener cancelListener = viewB -> {
                cdc.getAlertDialog().dismiss();
            };
            MiscUtil.setDialogOnClickListenerAndShow(cdc.getAlertDialog(), cdc.getDialogView(), confirmListener, cancelListener);
        });
        verifCancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, NavigationViewActivity.class);

            if (inboundNoTextView.getText().toString().equalsIgnoreCase("")) {
                clearAllData();
                startActivity(intent);
                finish();
            } else {
                MiscUtil.CustomDialogClass cdc = MiscUtil.customAlertDialog(this, "Return to home?", "After you return to home, the data is lost and cannot be canceled");
                View.OnClickListener confirmListener = viewA -> {
                    clearAllData();
                    cdc.getAlertDialog().dismiss();
                    startActivity(intent);
                    finish();
                };
                View.OnClickListener cancelListener = viewB -> {
                    cdc.getAlertDialog().dismiss();
                };
                MiscUtil.setDialogOnClickListenerAndShow(cdc.getAlertDialog(), cdc.getDialogView(), confirmListener, cancelListener);
            }

        });
        verifScan.setOnClickListener(view -> {
            if (currentSelectedInboundNo.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose inbound no first", Toast.LENGTH_SHORT).show();
            } else {
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.FromActivityKey, MiscUtil.GoodsShipmentValue);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                Gson gson = new Gson();
                Log.d(TAG, "initializeBtn: " + inboundMap);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ListBarcodeKey);
                startActivityForResult(intent, MainBarcodeQRCodeActivity.RC_BARCODE_CAPTURE);
            }
        });
        verifConfirm.setVisibility(View.GONE);
        verifViewDetail.setVisibility(View.GONE);
        verifScan.setVisibility(View.GONE);
        verifClear.setVisibility(View.GONE);
        totalScanConstrainLayout.setVisibility(View.GONE);
    }

    public void clearAllData() {
        clearSharedPreferences();
        inboundNoTextView.setText("");
        inboundDate.setText("");
        inboundCustomer.setText("");
        inboundWarehouse.setText("");
        totalScanConstrainLayout.setVisibility(View.GONE);
        totalScan.setText("");
        totalSummaryInbound.setText("");
        verifScanTv.setText("Scan");
        verifViewDetailTv.setText("Show");
        totalScanParent = 0;
        verifConfirm.setVisibility(View.GONE);
        verifViewDetail.setVisibility(View.GONE);
        verifClear.setVisibility(View.GONE);
        verifScan.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        inboundMap = new HashMap<>();
        setAdapterData();
        setSearchButtonInbound();
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
        Intent intent = new Intent(this, NavigationViewActivity.class);

        if (inboundNoTextView.getText().toString().equalsIgnoreCase("")) {
            clearAllData();
            startActivity(intent);
            finish();
        } else {
            MiscUtil.CustomDialogClass cdc = MiscUtil.customAlertDialog(this, "Return to home?", "After you return to home, the data is lost and cannot be canceled");
            View.OnClickListener confirmListener = viewA -> {
                clearAllData();
                cdc.getAlertDialog().dismiss();
                startActivity(intent);
                finish();
            };
            View.OnClickListener cancelListener = viewB -> {
                cdc.getAlertDialog().dismiss();
            };
            MiscUtil.setDialogOnClickListenerAndShow(cdc.getAlertDialog(), cdc.getDialogView(), confirmListener, cancelListener);
        }
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


    public void setInboundDetailValue(Boolean dummy, String inboundNo, OutboundData outboundData) {
        if (!inboundNo.trim().equalsIgnoreCase("")) {
            //real value
            inboundDate.setText(convertAPIDateToUIDate(outboundData.getInbounddate()));
            inboundCustomer.setText(outboundData.getCustomer());
            inboundWarehouse.setText(outboundData.getWarehouse());
            if (totalScanVerified > 0) {
                totalScan.setText(totalScanVerified.toString());
            } else {
                totalScan.setText("0");
            }
            Long inboundMapSize = this.inboundMap == null ? 0L : this.inboundMap.size();
            totalSummaryInbound.setText(inboundMapSize + "");
        }
    }

    public void verifyInboundDetails() {
        progressBar.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        GetOutboundsService service = RetrofitClientInstanceOutbound.getRetrofitInstanceOutbound().create(GetOutboundsService.class);
        String idWarehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWSID);
        String userID = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityUserID);
        Log.d(TAG, "verifyInboundDetails: id warehouse " + idWarehouse + " userID = " + userID);
        List<InboundVerifySerialNo> listSerialNo = new ArrayList<>();
        HashMap<String, InboundVerifySKU> skuMap = new HashMap<>();
        List<InboundVerifySKU> listSkuQty = new ArrayList<>();
        for (OutboundDetail outboundDetail : inboundMap.values()) {
            if (outboundDetail.getStatus().trim().equalsIgnoreCase(StatusVerified)) {
                listSerialNo.add(new InboundVerifySerialNo(outboundDetail.getSerialNo(), outboundDetail.getInputDate()));
                if (!skuMap.containsKey(outboundDetail.getSku())) {
                    skuMap.put(outboundDetail.getSku(), new InboundVerifySKU(outboundDetail.getSku(), "1", outboundDetail.getInputDate()));
                } else {
                    InboundVerifySKU skuVerif = skuMap.get(outboundDetail.getSku());
                    int qty = Integer.parseInt(skuVerif.getQty());
                    qty = qty + 1;
                    skuMap.put(outboundDetail.getSku(), new InboundVerifySKU(outboundDetail.getSku(), Integer.toString(qty), outboundDetail.getInputDate()));
                }
            }
        }
        for (String key : skuMap.keySet()) {
            listSkuQty.add(new InboundVerifySKU(key, skuMap.get(key).getQty(), skuMap.get(key).getDate()));
        }

        Log.d(TAG, "verifyInboundDetails: listSerialNo " + listSerialNo.toString());
        Log.d(TAG, "verifyInboundDetails: sku " + listSkuQty.toString());
        RetroInboundVerifyRequest request = new RetroInboundVerifyRequest(idWarehouse, userID,
                StatusVerified, MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()), listSerialNo, listSkuQty);
        Gson gson = new Gson();
        String stringJson = gson.toJson(request);
        Log.d(TAG, "verifyInboundDetails: json "+stringJson);
        Call<RetroInboundsVerifyResponse> call = service.verifyOutboundItemDetail(request);
        call.enqueue(new Callback<RetroInboundsVerifyResponse>() {
            @Override
            public void onResponse(Call<RetroInboundsVerifyResponse> call, Response<RetroInboundsVerifyResponse> response) {
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                if (response.body() != null && response.body().getResultCode() > 0) {
                    Toast.makeText(GoodsShipmentActivity.this, "Success Confirm Outbound", Toast.LENGTH_SHORT).show();
                    String selectedInbound = inboundNoTextView.getText().toString();
                    clearAllData();
                    listInboundNos.remove(selectedInbound);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(GoodsShipmentActivity.this,
                            R.layout.spinner_item_inbound, listInboundNos);
                    inboundNoTextView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "onResponse: failed " + response.body());
                    Toast.makeText(GoodsShipmentActivity.this, "Failed verify outbound", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsVerifyResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(GoodsShipmentActivity.this, "Failed to connect to api", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        });
    }
}