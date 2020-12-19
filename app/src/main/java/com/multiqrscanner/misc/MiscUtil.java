package com.multiqrscanner.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.gson.Gson;
import com.multiqrscanner.R;

import java.util.Calendar;

import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;

/**
 * @author Peter Abeles
 */
public class MiscUtil {
    public static final String mypreference = "mypref";
    public static final String ListBarcodeKey = "listBarcodes";

    public static String InboundNoKey = "inbound_no";
    public static String CurrentPalletKey = "current_pallet";
    public static String InboundListDetail = "inbound_list_detail";
    public static String ScanProductListDetail = "scan_product_list_detail";
    public static String TotalScanKey = "total_scan";
    public static String InboundListScanned = "inbound_list_scanned";
    public static String PalletListDetail = "pallet_list_detail";
    public static String GoodsVerificationValue = "goods_verification";
    public static String GoodsShipmentValue = "goods_shipment";
    public static String PickingPlanValue = "picking_plan";
    public static String PickingPlanPalletValue = "picking_plan_pallet";
    public static String PutawayValue = "putaway";
    public static String PutawayPalletValue = "putaway_pallet";
    public static String ReplenishmentValue = "replenishment";
    public static String InventoryMgmtValue = "inventory_mgmt";
    public static String FromActivityKey = "from_activity";
    public static String LoginActivityUser = "login_activity_user";
    public static String LoginActivityRole = "login_activity_role";
    public static String LoginActivityMenu = "login_activity_menu";
    public static String LoginActivityWS = "login_activity_WS";
    public static String LoginActivityUserID = "login_activity_user_id";
    public static String LoginActivityWSID = "login_activity_ws_id";
    public static String ImagePathKey = "imagePath";
    public static String QrCodeGsonKey = "qr_code_gson";

    public static class CustomDialogClass{
        View dialogView;
        AlertDialog alertDialog;

        public CustomDialogClass(View dialogView, AlertDialog alertDialog) {
            this.dialogView = dialogView;
            this.alertDialog = alertDialog;
        }

        public View getDialogView() {
            return dialogView;
        }

        public AlertDialog getAlertDialog() {
            return alertDialog;
        }
    }

    public static CustomDialogClass customAlertDialog(Context context, String title, String desc) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.custom_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(dialogView);
        TextView titleTv = dialogView.findViewById(R.id.txt_dia_title);
        titleTv.setText(title);
        TextView descTv = dialogView.findViewById(R.id.txt_dia_desc);
        descTv.setText(desc);
        return new CustomDialogClass(dialogView,alertDialog);
    }

    public static void setDialogOnClickListenerAndShow(AlertDialog alertDialog, View dialogView,  View.OnClickListener confirmListener, View.OnClickListener cancelListener){
        dialogView.findViewById(R.id.btn_yes).setOnClickListener(confirmListener);
        dialogView.findViewById(R.id.btn_no).setOnClickListener(cancelListener);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static Long getCurrentTimeInMilis(Calendar inputCalendar) {
        if (inputCalendar == null) {
            inputCalendar = Calendar.getInstance();
        }
        return inputCalendar.getTimeInMillis();
    }

    public static void renderPolygon(Polygon2D_F64 s, Path path, Canvas canvas, Paint paint) {
        path.reset();
        for (int j = 0; j < s.size(); j++) {
            Point2D_F64 p = s.get(j);
            if (j == 0)
                path.moveTo((float) p.x, (float) p.y);
            else
                path.lineTo((float) p.x, (float) p.y);
        }
        Point2D_F64 p = s.get(0);
        path.lineTo((float) p.x, (float) p.y);
        path.close();
        canvas.drawPath(path, paint);
    }

    public static void saveObjectSharedPreferenceAsString(Context mContext, String key, Object object) {
        Gson gson = new Gson();
        SharedPreferences mSettings = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String listBarcodesString = gson.toJson(object);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, listBarcodesString);
        editor.commit();
    }

    public static void saveStringSharedPreferenceAsString(Context mContext, String key, String value) {
        SharedPreferences mSettings = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void clearStringSharedPreferenceAsString(Context mContext, String key) {
        SharedPreferences mSettings = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static String getStringSharedPreferenceByKey(Context mContext, String key) {
        SharedPreferences mSettings = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (!mSettings.contains(key)) {
            return "";
        }
        String response = mSettings.getString(key, "");
        if (response == null) {
            return "";
        }
        return response;
    }
}
