package com.example.multiqrscanner.qrcode;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.multiqrscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.android.ConvertBitmap;

import static com.example.multiqrscanner.qrcode.QrCodeDetectActivity.uniqueLock;

/**
 * Presents a list of detected QR Codes. Can select each one to get more info and copy the message
 */
public class QrCodeListActivity extends AppCompatActivity {
    // List of all qr codes found in the order they were added to the listview
    List<QrCode> qrcodes = new ArrayList<>();

    //    TextView textMessage;
    ImageView imageView;
    ListView listView;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qrcode_list);
        // Create the list of QR Codes
        listView = findViewById(R.id.list_view);
        ArrayList<String> listItems = new ArrayList<>();
        synchronized (uniqueLock) {
            for (QrCodeWrapper qrCodeWrapper : QrCodeDetectActivity.unique.values()) {
                QrCode qr = qrCodeWrapper.getQrCode();
                qrcodes.add(qr);
                // filter out bad characters and new lines
                String message = qr.message.replaceAll("\\p{C}", " ");
                int N = Math.min(25, message.length());
//                listItems.add(String.format("%4d: %25s",qr.message.length(),message.substring(0,N)));
                listItems.add(String.format("%4d: %25s", qrCodeWrapper.getCount(), message.substring(0, N)));
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

//        TextView textVersion = findViewById(R.id.text_version);
//        TextView textError = findViewById(R.id.text_error);
//        TextView textMask = findViewById(R.id.text_mask);
//        TextView textMode = findViewById(R.id.text_mode);
        imageView = findViewById(R.id.image_view_scanned);

        // start set image
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        Bitmap imageBitmap = readImageFromUri(imagePath);
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }
        // done set image

        listView.setClickable(true);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            QrCode qr = qrcodes.get(position);
//            textVersion.setText("" + qr.version);
//            textError.setText("" + qr.error);
//            textMask.setText("" + qr.mask);
//            textMode.setText("" + qr.mode);
//            textMessage.setText("" + qr.message);
        });

        if (QrCodeDetectActivity.selectedQR != null) {
            moveToSelected(QrCodeDetectActivity.selectedQR);
        }
    }

    private void moveToSelected(String target) {
        int matched = -1;
        for (int i = 0; i < qrcodes.size(); i++) {
            if (qrcodes.get(i).message.equals(target)) {
                matched = i;
                break;
            }
        }

        if (matched != -1) {
            listView.smoothScrollToPosition(matched);
            listView.performItemClick(listView.getChildAt(matched), matched,
                    listView.getItemIdAtPosition(matched));
        }
    }

    public void pressedCopyMessage(View view) {
        CharSequence message = "";
        StringBuilder sequenceMessage = new StringBuilder();
        ArrayList<String> listItems = new ArrayList<>();
        synchronized (uniqueLock) {
            for (QrCodeWrapper qrCodeWrapper : QrCodeDetectActivity.unique.values()) {
                QrCode qr = qrCodeWrapper.getQrCode();
                qrcodes.add(qr);
                // filter out bad characters and new lines
                String messageLine = qr.message.replaceAll("\\p{C}", " ");
                sequenceMessage.append(messageLine).append(" ");
            }
            message = sequenceMessage;
            if (message.length() > 0) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("qrcode", message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied " + message.length() + " characters", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pressedScanAgain(View view) {
        synchronized (uniqueLock) {
            QrCodeDetectActivity.unique.clear();
        }
        recreate();
        onBackPressed();
    }

    public void pressedSubmit(View view) {
        Toast.makeText(this, "Submit to Local DB", Toast.LENGTH_SHORT).show();
        ConvertBitmap convertBitmap = new ConvertBitmap();
    }

    public Bitmap readImageFromUri(String filePath) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        File imgFile = new File(getIntent().getStringExtra("imagePath"));
        if (imgFile.exists()) {
            imgFile.delete();
        }
    }
}
