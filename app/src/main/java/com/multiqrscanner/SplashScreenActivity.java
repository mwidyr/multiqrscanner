package com.multiqrscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.navdrawer.NavigationViewActivity;

public class SplashScreenActivity extends AppCompatActivity {
    public boolean checkIfLoginCachedAlreadyExist() {
        String username = MiscUtil.getStringSharedPreferenceByKey(SplashScreenActivity.this, MiscUtil.LoginActivityUser);
        String role = MiscUtil.getStringSharedPreferenceByKey(SplashScreenActivity.this, MiscUtil.LoginActivityRole);
        String userID = MiscUtil.getStringSharedPreferenceByKey(SplashScreenActivity.this, MiscUtil.LoginActivityUserID);
        String warehouse = MiscUtil.getStringSharedPreferenceByKey(SplashScreenActivity.this, MiscUtil.LoginActivityWS);
        String menus = MiscUtil.getStringSharedPreferenceByKey(SplashScreenActivity.this, MiscUtil.LoginActivityMenu);

        //if one of it doesnt exist, then login
        return !username.trim().equalsIgnoreCase("") && !role.trim().equalsIgnoreCase("") &&
                !userID.trim().equalsIgnoreCase("") && !warehouse.trim().equalsIgnoreCase("") &&
                !menus.trim().equalsIgnoreCase("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        if (checkIfLoginCachedAlreadyExist()) {
            Intent userInfo = new Intent(SplashScreenActivity.this, NavigationViewActivity.class);
            startActivity(userInfo);
            finish();
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}