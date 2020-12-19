package com.multiqrscanner.navdrawer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.LoginActivity;
import com.multiqrscanner.R;
import com.multiqrscanner.inbound.GoodsVerificationActivity;
import com.multiqrscanner.inventory.PutawayActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.outbound.GoodsShipmentActivity;
import com.multiqrscanner.outbound.PickingPlanActivity;

import java.util.List;

public class NavigationViewActivity extends AppCompatActivity {
    private String TAG = "NVA";
    private LinearLayout btnGoodsVerif, btnPutaway, btnReplenishment, btnMove, btnPickingPlan, btnGoodsShipment;
    private ConstraintLayout circleGoodsVerif, circlePutaway, circleReplenishment, circleMove, circlePickingPlan, circleGoodsShipment;


    public void clearAllSharedPreference() {
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListScanned);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundListDetail);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.InboundNoKey);
        MiscUtil.clearStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey);
    }

    @Override
    public void onBackPressed() {
        // exit app when on back pressed
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        clearAllSharedPreference();

        TextView txtRole = (TextView) findViewById(R.id.tx_role_val);
        TextView txtWarehouse = (TextView) findViewById(R.id.tx_warehouse_val);
        TextView txtUsername = (TextView) findViewById(R.id.tx_username);

        btnGoodsVerif = findViewById(R.id.btn_goods_verif);
        btnPutaway = findViewById(R.id.btn_putaway);
        btnReplenishment = findViewById(R.id.btn_replenishment);
        btnMove = findViewById(R.id.btn_move);
        btnPickingPlan = findViewById(R.id.btn_picking_plan);
        btnGoodsShipment = findViewById(R.id.btn_goods_shipment);

        circleGoodsVerif = findViewById(R.id.circle_goods_verif);
        circlePutaway = findViewById(R.id.circle_putaway);
        circleReplenishment = findViewById(R.id.circle_replenishment);
        circleMove = findViewById(R.id.circle_move);
        circlePickingPlan = findViewById(R.id.circle_picking_plan);
        circleGoodsShipment = findViewById(R.id.circle_goods_shipment);


        txtRole.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityRole));
        txtWarehouse.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityWS));
        txtUsername.setText(MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityUser));

        prepareListData();

        // setting list adapter
        btnGoodsVerif.setOnClickListener(view -> {
            Intent userInfo = new Intent(NavigationViewActivity.this, GoodsVerificationActivity.class);
            startActivity(userInfo);
            finish();
        });
        btnPutaway.setOnClickListener(view -> {
            Intent userInfo = new Intent(NavigationViewActivity.this, PutawayActivity.class);
            startActivity(userInfo);
            finish();
        });
        btnPickingPlan.setOnClickListener(view -> {
            Intent userInfo = new Intent(NavigationViewActivity.this, PickingPlanActivity.class);
            startActivity(userInfo);
            finish();
        });
        btnGoodsShipment.setOnClickListener(view -> {
            Intent userInfo = new Intent(NavigationViewActivity.this, GoodsShipmentActivity.class);
            startActivity(userInfo);
            finish();
        });
        //still not developed yet
//        btnReplenishment.setOnClickListener(view -> {
//            Intent userInfo = new Intent(NavigationViewActivity.this, GoodsVerificationActivity.class);
//            startActivity(userInfo);
//            finish();
//        });
//        btnMove.setOnClickListener(view -> {
//            Intent userInfo = new Intent(NavigationViewActivity.this, GoodsVerificationActivity.class);
//            startActivity(userInfo);
//            finish();
//        });

        ImageButton btnLogout = findViewById(R.id.btn_logout_main_menu);
        btnLogout.setOnClickListener(view -> {
            MiscUtil.CustomDialogClass cdc = MiscUtil.customAlertDialog(this, "Logout?", "Are you sure want to logout?");
            View.OnClickListener confirmListener = viewA -> {
                Intent intent = new Intent(NavigationViewActivity.this, LoginActivity.class);
                clearAllSharedPreference();
                clearAllLoginCached();
                cdc.getAlertDialog().dismiss();
                startActivity(intent);
                finish();
            };
            View.OnClickListener cancelListener = viewB -> {
                cdc.getAlertDialog().dismiss();
            };
            MiscUtil.setDialogOnClickListenerAndShow(cdc.getAlertDialog(), cdc.getDialogView(), confirmListener, cancelListener);
        });
    }

    public void clearAllLoginCached() {
        MiscUtil.clearStringSharedPreferenceAsString(NavigationViewActivity.this, MiscUtil.LoginActivityUser);
        MiscUtil.clearStringSharedPreferenceAsString(NavigationViewActivity.this, MiscUtil.LoginActivityRole);
        MiscUtil.clearStringSharedPreferenceAsString(NavigationViewActivity.this, MiscUtil.LoginActivityUserID);
        MiscUtil.clearStringSharedPreferenceAsString(NavigationViewActivity.this, MiscUtil.LoginActivityWS);
        MiscUtil.clearStringSharedPreferenceAsString(NavigationViewActivity.this, MiscUtil.LoginActivityMenu);
    }

    private void prepareListData() {

        Gson gson = new Gson();
        String txtMenu = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.LoginActivityMenu);
        List<com.multiqrscanner.network.model.Menu> menus = gson.fromJson(txtMenu,
                new TypeToken<List<com.multiqrscanner.network.model.Menu>>() {}.getType());
        boolean bGoodsVerif = false;
        boolean bPutaway = false;
        boolean bReplenishment = false;
        boolean bInventoryMovement = false;
        boolean bPickingPlan = false;
        boolean bGoodsShipment = false;

        for (int i = 0; i < menus.size(); i++) {
            switch (menus.get(i).getMenu()) {
                case "Goods Verification":
                    bGoodsVerif = true;
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
                    bGoodsShipment = true;
                    break;
            }
        }

        if(!bGoodsVerif){
            circleGoodsVerif.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circleGoodsVerif.setClickable(false);
        } else if(!bPutaway){
            circlePutaway.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circlePutaway.setClickable(false);
        } else if(!bReplenishment){
            circleReplenishment.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circleReplenishment.setClickable(false);
        } else if(!bInventoryMovement){
            circleMove.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circleMove.setClickable(false);
        } else if(!bPickingPlan){
            circlePickingPlan.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circlePickingPlan.setClickable(false);
        } else if(!bGoodsShipment){
            circleGoodsShipment.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_grey));
            circleGoodsShipment.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}