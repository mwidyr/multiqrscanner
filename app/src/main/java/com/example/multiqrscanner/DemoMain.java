package com.example.multiqrscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.multiqrscanner.barcode.BarcodeActivity;
import com.example.multiqrscanner.model.RetroLogin;
import com.example.multiqrscanner.model.RetroUser;
import com.example.multiqrscanner.network.RetrofitClientInstance;
import com.example.multiqrscanner.network.user.GetLoginService;
import com.example.multiqrscanner.qrcode.QrCodeDetectActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boofcv.io.calibration.CalibrationIO;
import boofcv.struct.calib.CameraPinholeBrown;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemoMain extends AppCompatActivity {

    public static final String TAG = "DemoMain";

    List<Group> groups = new ArrayList<>();

    boolean waitingCameraPermissions = true;

    DemoApplication app;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        app = (DemoApplication) getApplication();
        if (app == null)
            throw new RuntimeException("App is null!");

        try {
            loadCameraSpecs();
        } catch (NoClassDefFoundError e) {
            // Some people like trying to run this app on really old versions of android and
            // seem to enjoy crashing and reporting the errors.
            e.printStackTrace();
            abortDialog("Camera2 API Required");
            return;
        }
        Button btnQrCodeScanner = findViewById(R.id.btn_qr_code_scanner);
        btnQrCodeScanner.setOnClickListener(view -> {
            Intent intent = new Intent(this, QrCodeDetectActivity.class);
            startActivity(intent);
        });
        Button btnBarCodeScanner = findViewById(R.id.btn_barcode_scanner);
        btnBarCodeScanner.setOnClickListener(view -> {
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivity(intent);
        });
        Button btnLogin = findViewById(R.id.btn_try_login);
        btnLogin.setOnClickListener(view -> {
            /*Create handle for the RetrofitInstance interface*/
            GetLoginService service = RetrofitClientInstance.getRetrofitInstance().create(GetLoginService.class);
            Call<RetroLogin> call = service.loginUser(new RetroUser("m.widy.ramadhani@gmail.com", "mwidyr", "sakti123"));
            call.enqueue(new Callback<RetroLogin>() {
                @Override
                public void onResponse(Call<RetroLogin> call, Response<RetroLogin> response) {
                    Log.d(TAG, "onResponse login user success : "+response.toString());
                    Log.d(TAG, "onResponse login user success : "+response.body());
                    Toast.makeText(DemoMain.this, response.body().toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<RetroLogin> call, Throwable t) {
                    Log.d(TAG, "onResponse login user: failure");
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!waitingCameraPermissions && app.changedPreferences) {
            loadIntrinsics(this, app.preference.cameraId, app.preference.calibration, null);
        }
    }

    public void pressedWebsite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://boofcv.org"));
        startActivity(browserIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.preferences: {
//				Intent intent = new Intent(this, PreferenceActivity.class);
//				startActivity(intent);
                return true;
            }
            case R.id.info: {
//				Intent intent = new Intent(this, CameraInformationActivity.class);
//				startActivity(intent);
                return true;
            }
            case R.id.about:
//				Intent intent = new Intent(this, AboutActivity.class);
//				startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadCameraSpecs() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        } else {
            waitingCameraPermissions = false;

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (manager == null)
                throw new RuntimeException("No cameras?!");
            try {
                String[] cameras = manager.getCameraIdList();

                for (String cameraId : cameras) {
                    CameraSpecs c = new CameraSpecs();
                    app.specs.add(c);
                    c.deviceId = cameraId;
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    c.facingBack = facing != null && facing == CameraCharacteristics.LENS_FACING_BACK;
                    StreamConfigurationMap map = characteristics.
                            get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null) {
                        continue;
                    }
                    Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);
                    if (sizes == null)
                        continue;
                    c.sizes.addAll(Arrays.asList(sizes));
                }
            } catch (CameraAccessException e) {
                throw new RuntimeException("No camera access??? Wasn't it just granted?");
            }

            // Now that it can read the camera set the default settings
            setDefaultPreferences();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCameraSpecs();
                    setDefaultPreferences();
                } else {
                    dialogNoCameraPermission();
                }
                return;
            }
        }
    }

    private void setDefaultPreferences() {
        app.preference.showSpeed = false;
        app.preference.autoReduce = true;

        // There are no cameras.  This is possible due to the hardware camera setting being set to false
        // which was a work around a bad design decision where front facing cameras wouldn't be accepted as hardware
        // which is an issue on tablets with only front facing cameras
        if (app.specs.size() == 0) {
            dialogNoCamera();
        }
        // select a front facing camera as the default
        for (int i = 0; i < app.specs.size(); i++) {
            CameraSpecs c = app.specs.get(i);

            app.preference.cameraId = c.deviceId;
            if (c.facingBack) {
                break;
            }
        }

        if (!app.specs.isEmpty()) {
            loadIntrinsics(this, app.preference.cameraId, app.preference.calibration, null);
        }
    }

    private void dialogNoCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device has no cameras!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogNoCameraPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Denied access to the camera! Exiting.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void loadIntrinsics(Activity activity,
                                      String cameraId,
                                      List<CameraPinholeBrown> intrinsics,
                                      List<File> locations) {
        intrinsics.clear();
        if (locations != null)
            locations.clear();

        File directory = new File(getExternalDirectory(activity), "calibration");
        if (!directory.exists())
            return;
        File files[] = directory.listFiles();
        if (files == null)
            return;
        String prefix = "camera" + cameraId;
        for (File f : files) {
            if (!f.getName().startsWith(prefix))
                continue;
            try {
                FileInputStream fos = new FileInputStream(f);
                Reader reader = new InputStreamReader(fos);
                CameraPinholeBrown intrinsic = CalibrationIO.load(reader);
                intrinsics.add(intrinsic);
                if (locations != null) {
                    locations.add(f);
                }
            } catch (RuntimeException | FileNotFoundException ignore) {
            }
        }
    }

    public static File getExternalDirectory(Activity activity) {
        // if possible use a public directory. If that fails use a private one
//		if(Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
//			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//			if( !dir.exists() )
//				dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//			return new File(dir,"org.boofcv.android");
//		} else {
        return activity.getExternalFilesDir(null);
//		}
    }

    public void onContentChanged() {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }


    private static class Group {
        String name;
        List<String> children = new ArrayList<String>();
        List<Class<Activity>> actions = new ArrayList<Class<Activity>>();

        private Group(String name) {
            this.name = name;
        }

        public void addChild(String name, Class action) {
            children.add(name);
            actions.add(action);
        }
    }

    public static CameraSpecs defaultCameraSpecs(DemoApplication app) {
        for (int i = 0; i < app.specs.size(); i++) {
            CameraSpecs s = app.specs.get(i);
            if (s.deviceId.equals(app.preference.cameraId))
                return s;
        }
        throw new RuntimeException("Can't find default camera");
    }

    /**
     * Displays a warning dialog and then exits the activity
     */
    private void abortDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Fatal error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    DemoMain.this.finish();
                });
        alertDialog.show();
    }

}
