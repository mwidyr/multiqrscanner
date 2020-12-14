package com.multiqrscanner.navdrawer;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.LoginActivity;
import com.multiqrscanner.R;
import com.multiqrscanner.inbound.GoodsVerificationActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationViewActivity extends AppCompatActivity {
    private String TAG = "NVA";
    private DrawerLayout mDrawerLayout;

    String headerInbound = "Inbound";
    String headerWarehouse = "Inventory";
    String headerOutbound = "Outbound";

    String inventoryVerif = ("Inventory Verification");

    String putaway = ("Putaway");
    String replenishment = ("Replenishment");
    String inventoryMovement = ("Inventory Movement");

    String pickingPlan = ("Picking Plan");
    String outgoingVerify = ("Outgoing Verify");
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        // clear all sharedpreference
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListScanned);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey);
        // done clear

        final ActionBar ab = getSupportActionBar();
        /* to set the menu icon image*/
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        TextView txtRole = (TextView)findViewById(R.id.tx_nva_role_val);
        TextView txtWarehouse = (TextView)findViewById(R.id.tx_nva_warehouse_val);

        txtRole.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityRole));
        txtWarehouse.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, id) -> {
            ExpandableListAdapter eListAdapter = (ExpandableListAdapter) expandableListView.getExpandableListAdapter();
            String item = (String) eListAdapter.getChild(groupPosition, childPosition);
            Log.d(TAG, item);
            if (item.equalsIgnoreCase(inventoryVerif)) {
                Intent intent = new Intent(NavigationViewActivity.this, GoodsVerificationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(NavigationViewActivity.this, item, Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(NavigationViewActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        Gson gson = new Gson();
        String txtMenu = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityMenu);
        List<com.multiqrscanner.network.model.Menu> menus = gson.fromJson(txtMenu,
                new TypeToken<List<com.multiqrscanner.network.model.Menu>>() {}.getType());
        Log.d(TAG, "txtMenu = " + menus);
        boolean bInventoryVerif = false;
        boolean bPutaway = false;
        boolean bReplenishment = false;
        boolean bInventoryMovement = false;
        boolean bPickingPlan = false;
        boolean bOutgoingVerify = false;

        for (int i = 0; i < menus.size(); i++) {
            if(menus.get(i).getMenu().equals("Goods Verification")){
                bInventoryVerif = true;
            } else if(menus.get(i).getMenu().equals("Putaway")){
                bPutaway = true;
            } else if(menus.get(i).getMenu().equals("Replenishment")){
                bReplenishment = true;
            } else if(menus.get(i).getMenu().equals("Inventory Movements")){
                bInventoryMovement = true;
            } else if(menus.get(i).getMenu().equals("Picking Plan")){
                bPickingPlan = true;
            } else if(menus.get(i).getMenu().equals("Goods Shipments")){
                bOutgoingVerify = true;
            }

        }

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(headerInbound);
        item1.setIconImg(android.R.drawable.ic_delete);
        // Adding data header
        if(bInventoryVerif)
            listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName(headerWarehouse);
        item2.setIconImg(android.R.drawable.ic_delete);
        if(bPutaway || bReplenishment || bInventoryMovement)
            listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName(headerOutbound);
        item3.setIconImg(android.R.drawable.ic_delete);
        if(bPickingPlan || bOutgoingVerify)
            listDataHeader.add(item3);

        // Adding child data
        List<String> heading1 = new ArrayList<>();
        if(bInventoryVerif)
            heading1.add(inventoryVerif);

        List<String> heading2 = new ArrayList<>();
        if(bPutaway)
            heading2.add(putaway);
        if(bReplenishment)
            heading2.add(replenishment);
        if(bInventoryMovement)
            heading2.add(inventoryMovement);

        List<String> heading3 = new ArrayList<>();
        if(bPickingPlan)
            heading3.add(pickingPlan);
        if(bOutgoingVerify)
            heading3.add(outgoingVerify);

        if(bInventoryVerif)
            listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        if(bPutaway || bReplenishment || bInventoryMovement)
            listDataChild.put(listDataHeader.get(1), heading2);
        if(bPickingPlan || bOutgoingVerify)
            listDataChild.put(listDataHeader.get(2), heading3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

}