package com.example.multiqrscanner.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.multiqrscanner.ScannerCamera2Activity;
import com.example.multiqrscanner.ScannerProcessingAbstract;
import com.example.multiqrscanner.R;
import com.example.multiqrscanner.inbound.GoodsVerificationActivity;
import com.example.multiqrscanner.inbound.GoodsVerificationScanResultActivity;
import com.example.multiqrscanner.misc.MiscUtil;
import com.example.multiqrscanner.misc.RenderCube3D;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ddogleg.struct.FastQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boofcv.abst.fiducial.QrCodeDetectorPnP;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.distort.LensDistortionFactory;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.image.GrayU8;
import georegression.metric.Intersection2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.se.Se3_F64;

/**
 * Used to detect and read information from QR codes
 */
public class QrCodeDetectActivity extends ScannerCamera2Activity {
    private static final String TAG = "QrCodeDetect";
    private String currentActivityFrom = "";
    public static String ImagePathKey = "imagePath";
    public static String QrCodeGsonKey = "qr_code_gson";
    private Gson gson = new Gson();
    private String currentSelectedInboundNo;
    private Integer totalScanFromParent = 0;

    // Switches what information is displayed
    Mode mode = Mode.NORMAL;
    // Does it render failed detections too?
    boolean showFailures = false;

    // Where the number of unique messages are listed
    TextView textUnqiueCount;
    // List of unique qr codes
    public static final Object uniqueLock = new Object();
    public static final Map<String, QrCodeWrapper> unique = new HashMap<>();
    // qr which has been selected and should be viewed
    public static String selectedQR = null;
    // TODO don't use a static method and forget detection if the activity is exited by the user

    // Location in image coordinates that the user is touching
    Point2D_F64 touched = new Point2D_F64();
    boolean touching = false;
    boolean touchProcessed = false;
//    boolean scanList = false;


    // Which standard configuration to use
    Detector detectorType = Detector.STANDARD;
    Spinner spinnerDetector;

    public QrCodeDetectActivity() {
        super(Resolution.HIGH);
        super.changeResolutionOnSlow = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout controls = (LinearLayout) inflater.inflate(R.layout.qrcode_detect_controls, null);
        Intent intentParent = getIntent();
        String inboundNo = intentParent.getStringExtra(GoodsVerificationActivity.InboundNoKey);
        if (inboundNo != null && !inboundNo.trim().equalsIgnoreCase("")) {
            currentSelectedInboundNo = inboundNo;
        }
        String fromActivityExtra = intentParent.getStringExtra(GoodsVerificationActivity.FromActivityKey);
        if (fromActivityExtra != null && !fromActivityExtra.trim().equalsIgnoreCase("")) {
            currentActivityFrom = fromActivityExtra;
        }
        String totalScan = intentParent.getStringExtra(GoodsVerificationActivity.TotalScanKey);
        if (totalScan != null && !totalScan.trim().equalsIgnoreCase("")) {
            Log.d(TAG, "onCreate: totalScan = "+totalScanFromParent);
            totalScanFromParent = Integer.parseInt(totalScan);
        }

        spinnerDetector = controls.findViewById(R.id.spinner_algs);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.qrcode_detectors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetector.setAdapter(adapter);
        spinnerDetector.setSelection(detectorType.ordinal());
        spinnerDetector.setOnItemSelectedListener(new SelectedListener());

        final ToggleButton toggle = controls.findViewById(R.id.show_failures);
        toggle.setChecked(showFailures);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> showFailures = isChecked);

        textUnqiueCount = controls.findViewById(R.id.total_unique);
        textUnqiueCount.setText("0");

        setControls(controls);
        displayView.setOnTouchListener(new TouchListener());
    }

    private class SelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            detectorType = Detector.values()[position];
            createNewProcessor();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    touching = true;
                }
                break;

                case MotionEvent.ACTION_UP: {
                    touching = false;
                }
                break;
            }

            if (touching) {
                applyToPoint(viewToImage, motionEvent.getX(), motionEvent.getY(), touched);
            }

            return true;
        }
    }

    @Override
    public void createNewProcessor() {
        setProcessing(new QrCodeProcessing());
    }

    public void pressedListView(View view) {
        String imagePath = takeScreenshot();
        if (currentActivityFrom != null && !currentActivityFrom.trim().equalsIgnoreCase("")) {
            if (currentActivityFrom.trim().equalsIgnoreCase(GoodsVerificationActivity.GoodsVerificationValue)) {
                Intent intent = new Intent(this, GoodsVerificationScanResultActivity.class);
                setProcessing(new QrCodeProcessing());
                intent.putExtra(GoodsVerificationActivity.InboundNoKey, currentSelectedInboundNo);
                if (!imagePath.equalsIgnoreCase("")) {
                    intent.putExtra(ImagePathKey, imagePath);
                }
                List<QrCodeSimpleWrapper> qrCodeSimpleWrapperList = new ArrayList<>();
                synchronized (uniqueLock) {
                    for (QrCodeWrapper qrCodeWrapper : QrCodeDetectActivity.unique.values()) {
                        QrCode qr = qrCodeWrapper.getQrCode();
                        // filter out bad characters and new lines
                        String message = qr.message.replaceAll("\\p{C}", " ");
                        QrCodeSimpleWrapper qrCodeSimpleWrapper = new QrCodeSimpleWrapper();
                        qrCodeSimpleWrapper.setCount(qrCodeWrapper.getCount().toString());
                        qrCodeSimpleWrapper.setQrValue(message);
                        qrCodeSimpleWrapperList.add(qrCodeSimpleWrapper);
                    }
                }
                if (qrCodeSimpleWrapperList.size() > 0) {
                    String qrCodeJsonValue = gson.toJson(qrCodeSimpleWrapperList);
                    intent.putExtra(QrCodeGsonKey, qrCodeJsonValue);
                }
                intent.putExtra(GoodsVerificationActivity.TotalScanKey, totalScanFromParent.toString());
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, QrCodeListActivity.class);
            if (!imagePath.equalsIgnoreCase("")) {
                intent.putExtra("imagePath", imagePath);
            }
            setProcessing(new QrCodeProcessing());
            startActivity(intent);
        }
    }

    protected class QrCodeProcessing extends ScannerProcessingAbstract<GrayU8> {

        QrCodeDetectorPnP<GrayU8> detector;

        FastQueue<QrCode> detected = new FastQueue<>(QrCode::new);
        FastQueue<QrCode> failures = new FastQueue<>(QrCode::new);

        Paint colorDetected = new Paint();
        Paint colorFailed = new Paint();
        Path path = new Path();

        int uniqueCount = 0;
        int oldValue = -1;

        final FastQueue<Se3_F64> listPose = new FastQueue<>(Se3_F64::new);
        RenderCube3D renderCube = new RenderCube3D();
        CameraPinholeBrown intrinsic;

        public QrCodeProcessing() {
            super(GrayU8.class);

            ConfigQrCode config;

            switch (detectorType) {
                case FAST: {
                    config = ConfigQrCode.fast();
                }
                break;

                default: {
                    config = new ConfigQrCode();
                }
            }

            detector = FactoryFiducial.qrcode3D(config, GrayU8.class);

            colorDetected.setARGB(0xA0, 0, 0xFF, 0);
            colorDetected.setStyle(Paint.Style.FILL);
            colorFailed.setARGB(0xA0, 0xFF, 0x11, 0x11);
            colorFailed.setStyle(Paint.Style.FILL);
        }

        @Override
        public void initialize(int imageWidth, int imageHeight, int sensorOrientation) {
            touchProcessed = false;
            selectedQR = null;
            touching = false;

            renderCube.initialize(cameraToDisplayDensity);
            intrinsic = lookupIntrinsics();
            detector.setLensDistortion(LensDistortionFactory.narrow(intrinsic), imageWidth, imageHeight);

            synchronized (uniqueLock) {
                uniqueCount = unique.size();
            }
        }

        @Override
        public void onDraw(Canvas canvas, Matrix imageToView) {
            canvas.concat(imageToView);
            synchronized (lockGui) {
                if (oldValue != uniqueCount) {
                    oldValue = uniqueCount;
                    textUnqiueCount.setText(uniqueCount + "");
                }
                switch (mode) {
                    case NORMAL: {
                        for (int i = 0; i < detected.size; i++) {
                            QrCode qr = detected.get(i);
                            MiscUtil.renderPolygon(qr.bounds, path, canvas, colorDetected);

                            if (touching && Intersection2D_F64.containConvex(qr.bounds, touched)) {
                                selectedQR = qr.message;
                            }
                        }
                        for (int i = 0; showFailures && i < failures.size; i++) {
                            QrCode qr = failures.get(i);
                            MiscUtil.renderPolygon(qr.bounds, path, canvas, colorFailed);
                        }
                    }
                    break;

                    case GRAPH: {
                        // TODO implement this in the future
                    }
                    break;
                }

                for (int i = 0; i < listPose.size; i++) {
                    renderCube.drawCube("", listPose.get(i), intrinsic, 1, canvas);
                }
            }

            // touchProcessed is needed to prevent multiple intent from being sent
            if (selectedQR != null && !touchProcessed) {
                touchProcessed = true;
                Intent intent = new Intent(QrCodeDetectActivity.this, QrCodeListActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void process(GrayU8 input) {
            detector.detect(input);
            synchronized (uniqueLock) {
                // always clear map, so doesnt duplicate when processing
                unique.clear();
                for (QrCode qr : detector.getDetector().getDetections()) {
                    if (qr.message == null) {
                        Log.e(TAG, "qr with null message?!?");
                    }
                    // default only contain unique message
                    // modified to save uniue message but with size
                    if (!unique.containsKey(qr.message)) {
                        Log.i(TAG, "Adding new qr code with message of length=" + qr.message.length());
                        QrCodeWrapper qrCodeWrapper = new QrCodeWrapper();
                        qrCodeWrapper.setQrCode(qr.clone());
                        qrCodeWrapper.setCount(1L);
                        unique.put(qr.message, qrCodeWrapper);
                    } else {
                        Log.i(TAG, "Adding existing qr code with message of length=" + qr.message.length());
                        QrCodeWrapper qrCodeWrapper = unique.get(qr.message);
                        assert qrCodeWrapper != null;
                        qrCodeWrapper.setCount(qrCodeWrapper.getCount() + 1L);
                        unique.put(qr.message, qrCodeWrapper);
                    }
                }
                uniqueCount = unique.size();
            }

            synchronized (lockGui) {
                detected.reset();
                for (QrCode qr : detector.getDetector().getDetections()) {
                    detected.grow().set(qr);
                }

                failures.reset();
                for (QrCode qr : detector.getDetector().getFailures()) {
                    if (qr.failureCause.ordinal() >= QrCode.Failure.ERROR_CORRECTION.ordinal()) {
                        failures.grow().set(qr);
                    }
                }

                listPose.reset();
                for (int i = 0; i < detector.totalFound(); i++) {
                    detector.getFiducialToCamera(i, listPose.grow());
                }
            }
        }
    }

    enum Mode {
        NORMAL,
        GRAPH
    }

    enum Detector {
        STANDARD,
        FAST
    }

    private String takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String filePath = "";

        try {
            // image naming and path  to include sd card  appending name you choose for file
            filePath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(filePath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return filePath;
    }

}
