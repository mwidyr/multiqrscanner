package com.multiqrscanner.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.multiqrscanner.navdrawer.NavigationViewActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;

/**
 * @author Peter Abeles
 */
public class MiscUtil {
    public static final String mypreference = "mypref";
    public static final String ListBarcodeKey = "listBarcodes";

    public static String InboundNoKey = "inbound_no";
    public static String InboundListDetail = "inbound_list_detail";
    public static String TotalScanKey = "total_scan";
    public static String InboundListScanned = "inbound_list_scanned";
    public static String GoodsVerificationValue = "goods_verification";
    public static String FromActivityKey = "from_activity";
    public static String LoginActivityUser = "login_activity_user";
    public static String LoginActivityRole = "login_activity_role";
    public static String LoginActivityMenu = "login_activity_menu";
    public static String LoginActivityWS = "login_activity_WS";
	public static String ImagePathKey = "imagePath";
	public static String QrCodeGsonKey = "qr_code_gson";


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
