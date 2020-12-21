package com.multiqrscanner.inventory;

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
import com.multiqrscanner.inbound.model.InboundDetail;
import com.multiqrscanner.inventory.adapter.HorizontalAdapter;
import com.multiqrscanner.inventory.adapter.ILoadMore;
import com.multiqrscanner.inventory.adapter.InboundDetailsAdapter;
import com.multiqrscanner.inventory.model.InventoryData;
import com.multiqrscanner.inventory.model.InventoryDetail;
import com.multiqrscanner.inventory.model.PalletDetail;
import com.multiqrscanner.inventory.model.PutawayData;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.navdrawer.NavigationViewActivity;
import com.multiqrscanner.network.RetrofitClientInstance;
import com.multiqrscanner.network.RetrofitClientInstanceInbound;
import com.multiqrscanner.network.RetrofitClientInstanceInventory;
import com.multiqrscanner.network.model.GetTimeResponse;
import com.multiqrscanner.network.model.InboundItemDetail;
import com.multiqrscanner.network.model.InboundVerifySerialNo;
import com.multiqrscanner.network.model.RetroInboundId;
import com.multiqrscanner.network.model.RetroInboundVerifyRequest;
import com.multiqrscanner.network.model.RetroInboundsDetail;
import com.multiqrscanner.network.model.RetroInboundsVerifyResponse;
import com.multiqrscanner.network.model.RetroInventory;
import com.multiqrscanner.network.model.RetroWarehouse;
import com.multiqrscanner.network.user.GetInboundsService;
import com.multiqrscanner.network.user.GetInventoryService;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.multiqrscanner.qrcode.QrCodePalletValue;

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

public class PutawayActivity extends AppCompatActivity {
    private static String TAG = "PA";
    private String currentSelectedInboundNo = "";
    public static Integer totalScanParent = 0;
    public static Integer totalScanVerified = 0;
    public static String StatusVerified = "Y";
    public static String StatusNotVerified = "N";
    private static final String ALL_WAREHOUSE = "All Warehouse";

    private HashMap<String, InventoryDetail> inboundMap;
    private HashMap<String, PutawayData> palletMap = new HashMap<>();

    private TextView inboundDate, inboundCustomer, inboundWarehouse, totalScan, totalSummaryInbound, searchButton, verifViewDetailTv, verifScanTv;
    private LinearLayout verifConfirm, verifViewDetail, verifScan, verifClear, verifAddPallet;
    private ConstraintLayout totalScanConstrainLayout;
    private ImageView verifCancel;
    private ProgressBar progressBar;
    private AutoCompleteTextView inboundNoTextView;
    private View view;
    private String currentSelectedPalletNo = "";

    List<String> listInboundNos = new ArrayList<>();

    private List<InventoryData> inventoryData;
    RecyclerView recyclerViewDetailProduct;
    List<InventoryDetail> items = new ArrayList<>();

    public void setPalletMap(HashMap<String, PutawayData> palletMap) {
        this.palletMap = palletMap;
    }

    public void setAdapterData() {
        InboundDetailsAdapter adapter;
        adapter = new InboundDetailsAdapter(recyclerViewDetailProduct, this, items);
        recyclerViewDetailProduct.setAdapter(adapter);
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

            }
        });
    }

    // Recycler View object
    RecyclerView recyclerView;
    // Array list for recycler view data source
    ArrayList<String> palletList;
    // Layout Manager
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    // adapter class object
    HorizontalAdapter adapter;
    // Linear Layout Manager
    LinearLayoutManager HorizontalLayout;

    public void AddItemsToRecyclerViewArrayList() {
        // Adding items to ArrayList
        palletList = new ArrayList<>();
    }

    private void setupHorizontalPalletView() {
        //begin set pallet list
        // initialisation with id's
        recyclerView = findViewById(R.id.putaway_pallet_list);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        // Set LayoutManager on Recycler View
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList();
        setupDataHorizontalPalletView();
    }

    private void setupDataHorizontalPalletView() {
        // calling constructor of adapter
        // with source list as a parameter
        adapter = new HorizontalAdapter(this, palletList);
        // Set Horizontal Layout Manager
        // for Recycler view
        HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        // Set adapter on recycler view
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();// Notify the adapter
        adapter.setOnItemClickListener(new HorizontalAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Log.d(TAG, "onItemClickListener: view " + view.toString() + " pos " + position);
                Log.d(TAG, "onItemClickListener: pallet_no " + palletList.get(position));
                currentSelectedPalletNo = palletList.get(position);
                PutawayData putawayData = palletMap.get(currentSelectedPalletNo);
                if (putawayData != null) {
                    items = new ArrayList<>(putawayData.getProductDetail().values());
                    setAdapterData();
                }
            }
        });
        // done set pallet list
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_putaway);
        view = findViewById(R.id.background_loading);
        view.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundCustomer = findViewById(R.id.tv_inbound_customer_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);
        recyclerViewDetailProduct = findViewById(R.id.goods_verif_detail);
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifViewDetail = findViewById(R.id.btn_goods_verif_view_detail);
        verifCancel = findViewById(R.id.custom_top_bar_back_button);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifAddPallet = findViewById(R.id.btn_goods_verif_scan_pallet);
        verifClear = findViewById(R.id.btn_goods_verif_clear);
        verifViewDetailTv = findViewById(R.id.verif_view_detail_text);
        verifScanTv = findViewById(R.id.verif_btn_scan_tv);
        totalScanConstrainLayout = findViewById(R.id.constraintLayout_total_scan);
        searchButton = findViewById(R.id.btn_search_inbound);
        inboundNoTextView = findViewById(R.id.spinner_inbound_no);
        recyclerViewDetailProduct.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetailProduct.setVisibility(View.GONE);
        setAdapterData();
        setupHorizontalPalletView();
        getDataFromScanForPalletCode();
        String warehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS);
        if (warehouse.equalsIgnoreCase(ALL_WAREHOUSE)) {
            warehouse = "";
        }
        inventoryData = new ArrayList<>();
        GetInventoryService service = RetrofitClientInstanceInventory.getRetrofitInstanceInventory().create(GetInventoryService.class);
        Call<RetroInventory> callSync = service.getInventory(new RetroWarehouse(warehouse));
        progressBar.setVisibility(View.VISIBLE);
        callSync.enqueue(new Callback<RetroInventory>() {
            @Override
            public void onResponse(Call<RetroInventory> call, Response<RetroInventory> response) {
                if (response.body() != null && response.body().getInbounds() != null) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "getInbounds = " + response.body().getInbounds());
                    inventoryData.addAll(response.body().getInbounds());
                    for (InventoryData inventoryData1 : inventoryData) {
                        listInboundNos.add(inventoryData1.getInboundno());
                    }
                    initializeBtn();
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PutawayActivity.this,
                            R.layout.spinner_item_inbound, listInboundNos);
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    inboundNoTextView.setAdapter(adapter);
                    String selectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.InboundNoKey);
                    inboundNoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                            Log.d(TAG, "adapter = " + (String) adapterView.getItemAtPosition(pos));
                            String item = (String) adapterView.getItemAtPosition(pos);
                            if (!item.trim().equalsIgnoreCase("")) {
                                currentSelectedInboundNo = item;
                                for (InventoryData data : inventoryData) {
                                    if (data.getInboundno().trim().equalsIgnoreCase(item)) {
                                        if (!currentSelectedInboundNo.trim().equalsIgnoreCase(selectedInboundNo)) {
                                            clearSharedPreferences();
                                            MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.TotalScanKey);
                                            totalScanParent = 0;
                                            totalScan.setText("0");
                                        }
                                        MiscUtil.saveStringSharedPreferenceAsString(PutawayActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        verifAddPallet.setVisibility(View.VISIBLE);
                                        verifAddPallet.setClickable(true);
                                        break;
                                    }
                                }
                            } else {
                                setInboundDetailValue(false, currentSelectedInboundNo, new InventoryData("", "", "", "", "", new ArrayList<>()));
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
                                for (InventoryData data : inventoryData) {
                                    if (data.getInboundno().trim().equalsIgnoreCase(currentSelectedInboundNo)) {
                                        MiscUtil.saveStringSharedPreferenceAsString(PutawayActivity.this,
                                                MiscUtil.LoginActivityWSID, data.getId());
                                        injectData(currentSelectedInboundNo, data);
                                        verifScan.setVisibility(View.VISIBLE);
                                        verifScan.setClickable(true);
                                        verifAddPallet.setVisibility(View.VISIBLE);
                                        verifAddPallet.setClickable(true);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    String totalScanExtra = MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.TotalScanKey);
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
                            verifAddPallet.setVisibility(View.VISIBLE);
                            verifAddPallet.setClickable(true);
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
            public void onFailure(Call<RetroInventory> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(PutawayActivity.this, "Failed call data api", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //flow when add pallet -> get scanned qr code, parse to qrCodeProductValue, add pallet detail to palletmap with empty map product, set adapter again
    public void getDataFromScanForPalletCode() {
        String currentActivityFrom = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.FromActivityKey);
        Log.d(TAG, "getDataFromScanForPalletCode: " + currentActivityFrom);
        if (currentActivityFrom.equalsIgnoreCase(MiscUtil.PutawayPalletValue)) {
            Gson gson = new Gson();
            String palletMapString = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.PalletListDetail);
            Log.d(TAG, "getDataFromScanForPalletCode: pmString " + palletMapString);
            Log.d(TAG, "getDataFromScanForPalletCode: pmLocal " + this.palletMap);
            if (!palletMapString.equalsIgnoreCase("")) {
                this.palletMap = gson.fromJson(palletMapString, new TypeToken<HashMap<String, PutawayData>>() {
                }.getType());
                Log.d(TAG, "getDataFromScanForPalletCode: pmParse " + this.palletMap);
            }
            String qrCodeListExtra = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.QrCodeGsonKey);
            Log.d(TAG, "getDataFromScanForPalletCode: " + qrCodeListExtra);
            if (!qrCodeListExtra.equalsIgnoreCase("")) {
                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrappers = gson.fromJson(qrCodeListExtra, new TypeToken<ArrayList<QrCodeBarcodeSimpleWrapper>>() {
                }.getType());
                if (qrCodeBarcodeSimpleWrappers.size() > 0) {
                    for (QrCodeBarcodeSimpleWrapper qrCode : qrCodeBarcodeSimpleWrappers) {
                        // get qrcode detail value
                        QrCodePalletValue qrCodePalletValue = new QrCodePalletValue();
                        try {
                            Log.d(TAG, "getDataFromScanForPalletCode: " + qrCode.getQrValue());
                            qrCodePalletValue = gson.fromJson(qrCode.getQrValue(), QrCodePalletValue.class);
                            Log.d(TAG, "getDataFromScanForPalletCode: " + qrCodePalletValue);
                            String palletNoString = qrCodePalletValue.getPallet_no().toString();
                            if (!this.palletMap.containsKey(palletNoString)) {
                                PutawayData inputPutawayData = new PutawayData(new PalletDetail(Long.parseLong(palletNoString)), new HashMap<>());
                                this.palletMap.put(palletNoString, inputPutawayData);
                                break;// because only 1 pallet needed
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "onCreate: " + ex.getMessage(), ex);
                            continue;
                        }
                    }
                }
            }
            palletList.addAll(this.palletMap.keySet());
            setupDataHorizontalPalletView();
        }
    }

    public void injectData(String inboundNo, InventoryData data) {
        GetInventoryService service = RetrofitClientInstanceInventory.getRetrofitInstanceInventory().create(GetInventoryService.class);
        Call<RetroInboundsDetail> callSync = service.getInventoryItemDetail(new RetroInboundId(inboundNo));
        callSync.enqueue(new Callback<RetroInboundsDetail>() {
            @Override
            public void onResponse(Call<RetroInboundsDetail> call, Response<RetroInboundsDetail> response) {
                if (response.body() != null && response.body().getItems() != null && response.body().getItems().size() > 0) {
                    Log.d(TAG, "role = " + response.body().getItems());
                    List<InventoryDetail> inboundList = new ArrayList<>();
                    for (InboundItemDetail inboundItemDetail : response.body().getItems()) {
                        if (inboundItemDetail.getSerialno() == null || inboundItemDetail.getSerialno().trim().equalsIgnoreCase("")) {
                            continue;
                        }
                        inboundList.add(new InventoryDetail(inboundItemDetail.getLine() + "",
                                inboundItemDetail.getSku(), inboundItemDetail.getSerialno() == null ? "" : inboundItemDetail.getSerialno(),
                                inboundItemDetail.getProductname(), inboundItemDetail.getQty() + "",
                                inboundItemDetail.getSubkey() == null ? "" : inboundItemDetail.getSubkey(),
                                inboundItemDetail.getVerif(), 0L));
                    }
                    HashMap<String, InventoryDetail> inboundMap = new HashMap<>();
                    for (InventoryDetail detail : inboundList) {
                        String defaultStatus = StatusNotVerified;
                        if (detail.getSerialNo() == null) {
                            defaultStatus = StatusVerified;
                        }
                        inboundMap.put(detail.getSerialNo(), new InventoryDetail(detail.getLineNo(), detail.getSku(),
                                detail.getSerialNo(), detail.getProductName(), detail.getQty(),
                                detail.getSubkey(), defaultStatus, detail.getInputDate()));
                    }

                    Gson gson = new Gson();
                    String inboundMapString = MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.InboundListDetail);
                    if (!inboundMapString.trim().equalsIgnoreCase("")) {
                        inboundMap = gson.fromJson(inboundMapString, new TypeToken<HashMap<String, InventoryDetail>>() {
                        }.getType());
                    }
                    setInboundMap(inboundMap);

                    HashMap<String, PutawayData> palletMap = new HashMap<>();
                    // step to get scan result and map to core pallet map
                    //get core pallet map sharedpref
                    String palletMapString = MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.PalletListDetail);
                    if (!palletMapString.trim().equalsIgnoreCase("")) {
                        palletMap = gson.fromJson(palletMapString, new TypeToken<HashMap<String, PutawayData>>() {
                        }.getType());
                    }

                    //get scan result
                    HashMap<String, InventoryDetail> productMap = new HashMap<>();
                    String productListString = MiscUtil.getStringSharedPreferenceByKey(PutawayActivity.this, MiscUtil.ScanProductListDetail);
                    if (!productListString.trim().equalsIgnoreCase("")) {
                        productMap = gson.fromJson(productListString, new TypeToken<HashMap<String, InventoryDetail>>() {
                        }.getType());
                    /*
                    1. filter scan result so there are no duplicate across map (loop core map and compare values map to scan result, if values same and key == current selected its okay)
                    2. check if key current selected pallet already exist in core pallet
                    3. if exist then replace data with scan result
                     */
                        for (String key : palletMap.keySet()) {
                            PutawayData putawayData = palletMap.get(key);
                            assert putawayData != null;
                            for (String keyData : putawayData.getProductDetail().keySet()) {
                                // filter key data if it exist then remove
                                productMap.remove(keyData);
                            }
                        }
                    }
                    PutawayData putawayData = palletMap.get(currentSelectedPalletNo);
                    for (String key : productMap.keySet()) {
                        InventoryDetail productData = productMap.get(key);
                        assert productData != null;
                        assert putawayData != null;
                        putawayData.getProductDetail().put(productData.getSerialNo(), productData);
                    }
                    setPalletMap(palletMap);

                    List<InventoryDetail> itemInventoryDetail = new ArrayList<>();
                    // Iterating over keys only
                    int idx = 0;
                    assert putawayData != null;
                    for (String key : putawayData.getProductDetail().keySet()) {
                        InventoryDetail inventoryDetailData = putawayData.getProductDetail().get(key);
                        itemInventoryDetail.add(inventoryDetailData);
                        idx++;
                    }
                    items = itemInventoryDetail;
                    setAdapterData();

                    totalScanVerified = idx;
                    setInboundDetailValue(false, currentSelectedInboundNo, data);
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsDetail> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(PutawayActivity.this, "Failed Retrieve Inbound Detail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setInboundMap(HashMap<String, InventoryDetail> inboundMap) {
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

        verifAddPallet.setOnClickListener(view -> {
            if (currentSelectedInboundNo.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose inbound no first", Toast.LENGTH_SHORT).show();
            } else {
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.FromActivityKey, MiscUtil.PutawayPalletValue);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                Gson gson = new Gson();
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.PalletListDetail, gson.toJson(this.palletMap));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ListBarcodeKey);
                startActivityForResult(intent, MainBarcodeQRCodeActivity.RC_BARCODE_CAPTURE);
            }
        });

        setSearchButtonInbound();
        verifViewDetail.setOnClickListener(view -> {
            if (recyclerViewDetailProduct.getVisibility() == View.GONE) {
                recyclerViewDetailProduct.setVisibility(View.VISIBLE);
                verifViewDetailTv.setText("Hide");
            } else {
                recyclerViewDetailProduct.setVisibility(View.GONE);
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
            if (currentSelectedPalletNo == null || currentSelectedPalletNo.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose pallet no first", Toast.LENGTH_SHORT).show();
            } else {
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.FromActivityKey, MiscUtil.PutawayValue);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.CurrentPalletKey, currentSelectedPalletNo);
                Gson gson = new Gson();
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail, gson.toJson(this.inboundMap));
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey, currentSelectedInboundNo);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanParent.toString());
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
        verifAddPallet.setVisibility(View.GONE);
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
        verifAddPallet.setVisibility(View.GONE);
        recyclerViewDetailProduct.setVisibility(View.GONE);
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

        if (inboundNoTextView == null || inboundNoTextView.getText() == null || inboundNoTextView.getText().toString().trim().equalsIgnoreCase("")) {
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


    public void setInboundDetailValue(Boolean dummy, String inboundNo, InventoryData inventoryData) {
        if (!inboundNo.trim().equalsIgnoreCase("")) {
            //real value
            inboundDate.setText(convertAPIDateToUIDate(inventoryData.getInbounddate()));
            inboundCustomer.setText(inventoryData.getCustomer());
            inboundWarehouse.setText(inventoryData.getWarehouse());
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
        GetInventoryService service = RetrofitClientInstanceInventory.getRetrofitInstanceInventory().create(GetInventoryService.class);
        String idWarehouse = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWSID);
        String userID = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityUserID);
        Log.d(TAG, "verifyInboundDetails: id warehouse " + idWarehouse + " userID = " + userID);
        List<InboundVerifySerialNo> listSerialNo = new ArrayList<>();
        for (InventoryDetail inventoryDetail : inboundMap.values()) {
            if (inventoryDetail.getStatus().trim().equalsIgnoreCase(StatusVerified)) {
                listSerialNo.add(new InboundVerifySerialNo(inventoryDetail.getSerialNo(), inventoryDetail.getInputDate()));
            }
        }
        Log.d(TAG, "verifyInboundDetails: listSerialNo " + listSerialNo.toString());
        Call<RetroInboundsVerifyResponse> call = service.verifyInventoryItemDetail(new RetroInboundVerifyRequest(idWarehouse, userID,
                StatusVerified, MiscUtil.getCurrentTimeInMilis(Calendar.getInstance()), listSerialNo, null));
        call.enqueue(new Callback<RetroInboundsVerifyResponse>() {
            @Override
            public void onResponse(Call<RetroInboundsVerifyResponse> call, Response<RetroInboundsVerifyResponse> response) {
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                if (response.body() != null && response.body().getResultCode() > 0) {
                    Toast.makeText(PutawayActivity.this, "Success Confirm Inbound", Toast.LENGTH_SHORT).show();
                    String selectedInbound = inboundNoTextView.getText().toString();
                    clearAllData();
                    listInboundNos.remove(selectedInbound);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PutawayActivity.this,
                            R.layout.spinner_item_inbound, listInboundNos);
                    inboundNoTextView.setAdapter(adapter);
                } else {
                    Toast.makeText(PutawayActivity.this, "Failed verify inbound", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetroInboundsVerifyResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(PutawayActivity.this, "Failed to connect to api", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        });
    }
}