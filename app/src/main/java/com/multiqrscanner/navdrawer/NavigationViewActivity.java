package com.multiqrscanner.navdrawer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout btnGoodsVerif, btnPutaway, btnReplenishment, btnMove, btnPickingPlan, btnGoodsShipment;

    String headerInbound = "Inbound";
    String headerWarehouse = "Inventory";
    String headerOutbound = "Outbound";

    String inventoryVerif = ("Inventory Verification");

    String putaway = ("Putaway");
    String replenishment = ("Replenishment");
    String inventoryMovement = ("Inventory Movement");

    String pickingPlan = ("Picking Plan");
    String outgoingVerify = ("Outgoing Verify");

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

        TextView txtRole = (TextView)findViewById(R.id.tx_role_val);
        TextView txtWarehouse = (TextView)findViewById(R.id.tx_warehouse_val);
        TextView txtUsername = (TextView)findViewById(R.id.tx_username);

        btnGoodsVerif = findViewById(R.id.btn_goods_verif);
        btnPutaway = findViewById(R.id.btn_putaway);
        btnReplenishment = findViewById(R.id.btn_replenishment);
        btnMove = findViewById(R.id.btn_move);
        btnPickingPlan = findViewById(R.id.btn_picking_plan);
        btnGoodsShipment = findViewById(R.id.btn_goods_shipment);


        txtRole.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityRole));
        txtWarehouse.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS));
        txtUsername.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityUser));

        prepareListData();

        // setting list adapter

        btnGoodsVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userInfo = new Intent(NavigationViewActivity.this, GoodsVerificationActivity.class);
                startActivity(userInfo);
            }
        });

        ImageButton btnLogout = findViewById(R.id.btn_logout_main_menu);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(NavigationViewActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void prepareListData() {

        Gson gson = new Gson();
        String txtMenu = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityMenu);
        List<com.multiqrscanner.network.model.Menu> menus = gson.fromJson(txtMenu,
                new TypeToken<List<com.multiqrscanner.network.model.Menu>>() {}.getType());
        boolean bInventoryVerif = false;
        boolean bPutaway = false;
        boolean bReplenishment = false;
        boolean bInventoryMovement = false;
        boolean bPickingPlan = false;
        boolean bOutgoingVerify = false;

        for (int i = 0; i < menus.size(); i++) {
            switch (menus.get(i).getMenu()) {
                case "Goods Verification":
                    bInventoryVerif = true;
                    break;
                case "Putaway":
                    bPutaway = true;
                    break;
                case "Replenishment":
                    bReplenishment = true;
                    break;
                case "Inventory Movements":
                    bInventoryMovement = true;
                    break;
                case "Picking Plan":
                    bPickingPlan = true;
                    break;
                case "Goods Shipments":
                    bOutgoingVerify = true;
                    break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}