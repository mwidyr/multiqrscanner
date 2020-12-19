/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.multiqrscanner.barcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.inbound.GoodsVerificationScanResultActivity;
import com.multiqrscanner.inventory.PutawayPalletProductScanResultActivity;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.outbound.GoodsShipmentScanResultActivity;
import com.multiqrscanner.outbound.PickingPlanActivity;
import com.multiqrscanner.outbound.PickingPlanScanResultActivity;
import com.multiqrscanner.qrcode.QrCodeBarcodeSimpleWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.multiqrscanner.barcode.camera.CameraSource;
import com.multiqrscanner.barcode.camera.CameraSourcePreview;
import com.multiqrscanner.barcode.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiqrscanner.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureActivity extends AppCompatActivity {
    private static final String TAG = "Barcode-reader";

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    // use a compound button so either checkbox or switch widgets work.
    private TextView autoFocus;
    private TextView useFlash;
    private ConstraintLayout btnScan;
    private ImageView verifCancel;


    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_barcode_capture);
        btnScan = findViewById(R.id.constraintLayout_qr_barcode_scan);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        autoFocus = findViewById(R.id.auto_focus);
        useFlash = findViewById(R.id.use_flash);
        TextView titleCustom = findViewById(R.id.textView2);
        String activityOrigin = getActivityOrigin();
        if (!activityOrigin.trim().equalsIgnoreCase("")) {
            titleCustom.setText(activityOrigin);
        }
        verifCancel = findViewById(R.id.custom_top_bar_back_button);
        verifCancel.setOnClickListener(view -> {
            onBackPressed();
        });
//        autoFocus.setVisibility(View.GONE);
//        useFlash.setVisibility(View.GONE);

        // read parameters from the intent used to launch the activity.
        boolean autoFocusExtra = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlashExtra = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocusExtra, useFlashExtra);
        } else {
            requestCameraPermission();
        }

        autoFocus.setOnClickListener(view -> {
            Toast.makeText(this, "Auto Focus clicked", Toast.LENGTH_SHORT).show();
        });
        useFlash.setOnClickListener(view -> {
            Toast.makeText(this, "Use Flash clicked", Toast.LENGTH_SHORT).show();
        });

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnScanClick();
            }
        });
    }

    private void btnScanClick() {
        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        List<String> mBarcodes = new ArrayList<>();
        Gson gson = new Gson();
        String listBarcodeShared = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.ListBarcodeKey);
        if (!listBarcodeShared.trim().equalsIgnoreCase("")) {
            mBarcodes = gson.fromJson(listBarcodeShared, new TypeToken<List<String>>() {
            }.getType());
            saveToSharedPrefAndGoToIntent(mBarcodes);
//            String imagePath = takeScreenshot(mBarcodes);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private String takeScreenshot(List<String> mBarcodes) {
//        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data) {
//                saveToSharedPrefAndGoToIntent(data, mBarcodes);
//            }
//        });
//        return "";
//    }

    public void saveToSharedPrefAndGoToIntent(List<String> mBarcodes) {
        Log.d(TAG, "saveToSharedPrefAndGoToIntent: ");
        Gson gson = new Gson();
        String currentActivityFrom = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.FromActivityKey);
        if (!currentActivityFrom.trim().equalsIgnoreCase("")) {
            Log.d(TAG, "saveToSharedPrefAndGoToIntent: " + currentActivityFrom);
            List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
            for (String barcodeValue : mBarcodes) {
                QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
                qrCodeBarcodeSimpleWrapperList.add(wrapper);
            }
            if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
                String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
            }
            if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsVerificationValue)) {
                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);

                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
                startActivity(intent);
                finish();
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsShipmentValue)) {
                Intent intent = new Intent(this, GoodsShipmentScanResultActivity.class);
                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
                startActivity(intent);
                finish();
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PickingPlanValue)) {
                Intent intent = new Intent(this, PickingPlanScanResultActivity.class);
                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
                startActivity(intent);
                finish();
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PutawayValue)) {
                Intent intent = new Intent(this, PutawayPalletProductScanResultActivity.class);
                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
                }
                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
                startActivity(intent);
                finish();
            }
            // still not developed yet
//            else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.InventoryMgmtValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.ReplenishmentValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            }
        }
    }


//    public void saveToSharedPrefAndGoToIntent(byte[] imagePath, List<String> mBarcodes) {
//        Gson gson = new Gson();
//        String currentActivityFrom = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.FromActivityKey);
//        if (!currentActivityFrom.trim().equalsIgnoreCase("")) {
//            if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsVerificationValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsShipmentValue)) {
//                Intent intent = new Intent(this, GoodsShipmentScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PickingPlanValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.InventoryMgmtValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.ReplenishmentValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PutawayValue)) {
//                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
//                String currentSelectedInboundNo = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.InboundNoKey);
//                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
//                if (imagePath.length > 0) {
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.ImagePathKey, gson.toJson(imagePath));
//                    Log.d(TAG, "onCreate: imagePath " + imagePath);
//                }
//                List<QrCodeBarcodeSimpleWrapper> qrCodeBarcodeSimpleWrapperList = new ArrayList<>();
//                for (String barcodeValue : mBarcodes) {
//                    QrCodeBarcodeSimpleWrapper wrapper = new QrCodeBarcodeSimpleWrapper(barcodeValue, "1");
//                    qrCodeBarcodeSimpleWrapperList.add(wrapper);
//                }
//                if (qrCodeBarcodeSimpleWrapperList.size() > 0) {
//                    String qrCodeJsonValue = gson.toJson(qrCodeBarcodeSimpleWrapperList);
//                    MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.QrCodeGsonKey, qrCodeJsonValue);
//                }
//                String totalScanFromParent = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.TotalScanKey);
//                MiscUtil.saveStringSharedPreferenceAsString(this, MiscUtil.TotalScanKey, totalScanFromParent);
//                startActivity(intent);
//                finish();
//            }
//        }
//    }

    public String getActivityOrigin() {
        String currentActivityFrom = MiscUtil.getStringSharedPreferenceByKey(this, MiscUtil.FromActivityKey);
        if (!currentActivityFrom.trim().equalsIgnoreCase("")) {
            if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsVerificationValue)) {
                return "Goods Verification";
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.GoodsShipmentValue)) {
                return "Goods Shipment";
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PickingPlanValue)) {
                return "Picking Plan";
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.InventoryMgmtValue)) {
                return "Inventory Management";
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.ReplenishmentValue)) {
                return "Replenishment";
            } else if (currentActivityFrom.trim().equalsIgnoreCase(MiscUtil.PutawayValue)) {
                return "Putaway";
            }
        }
        return "Scan Activity";
    }


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the oldest barcode currently detected and
     * return it to the caller.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {

        //TODO: use the tap position to select the barcode.
        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent data = new Intent();
                data.putExtra(BarcodeObject, barcode);
                setResult(CommonStatusCodes.SUCCESS, data);
                finish();
            } else {
                Log.d(TAG, "barcode data is null");
            }
        } else {
            Log.d(TAG, "no barcode detected");
        }
        return barcode != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    CameraSource.PictureCallback mPicture = data -> {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "picture file null: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "error File Not Found : " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "error IO EX : " + e.getMessage());
        }
    };

    private static File getOutputMediaFile() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String filePath = "";
        filePath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
//        File mediaStorageDir = new File(filePath);
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//                .format(new Date());
        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");
        mediaFile = new File(filePath);

        return mediaFile;
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}
