package com.multiqrscanner.navdrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.inbound.GoodsVerificationActivity;
import com.multiqrscanner.misc.MiscUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationViewActivity extends AppCompatActivity {
    private String TAG = "NVA";
    private DrawerLayout mDrawerLayout;

    String headerInbound = "Inbound";
    String headerWarehouse = "Warehouse";
    String headerOutbound = "Outbound";
    String headerRole = "Role : Admin";
    String headerFromWarehouse = "Warehouse : Main";

    String inboundVerif = ("Inbound Verification");
    String goodsVerif = ("Goods Verification");

    String warehouseScan = ("Warehouse Scan");
    String warehouseMovement = ("Warehouse Movement");
    String warehouseVerif = ("Warehouse Verification");

    String outboundScan = ("Outbound Scan");
    String outboundMovement = ("Outbound Movement");
    String outboundVerification = ("Outbound Verification");
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        final ActionBar ab = getSupportActionBar();
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey);
        /* to set the menu icon image*/
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
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
            if (item.equalsIgnoreCase(goodsVerif)) {
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
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(headerInbound);
        item1.setIconImg(android.R.drawable.ic_delete);
        // Adding data header
        listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName(headerWarehouse);
        item2.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName(headerOutbound);
        item3.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item3);

        ExpandedMenuModel item4 = new ExpandedMenuModel();
        item4.setIconName(headerRole);
        item4.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item4);

        ExpandedMenuModel item5 = new ExpandedMenuModel();
        item5.setIconName(headerFromWarehouse);
        item5.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item5);

        // Adding child data
        List<String> heading1 = new ArrayList<>();
        heading1.add(inboundVerif);
        heading1.add(goodsVerif);

        List<String> heading2 = new ArrayList<>();
        heading2.add(warehouseScan);
        heading2.add(warehouseMovement);
        heading2.add(warehouseVerif);

        List<String> heading3 = new ArrayList<>();
        heading3.add(outboundScan);
        heading3.add(outboundMovement);
        heading3.add(outboundVerification);

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);
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