package com.example.multiqrscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.multiqrscanner.barcode.MainBarcodeQRCodeActivity;
import com.example.multiqrscanner.navdrawer.NavigationViewActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(this, NavigationViewActivity.class);
        startActivity(intent);
        finish();
    }
}