package com.multiqrscanner.inbound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.multiqrscanner.R;
import com.multiqrscanner.misc.MiscUtil;

public class GoodsVerificationActivity extends AppCompatActivity {
    private static String TAG = "GVA";
    private String currentSelectedInboundNo = "";
    public static Integer totalScanParent = 0;

    private TextView inboundDate, inboundDetail, inboundWarehouse, totalScan, totalSummaryInbound;
    private Button verifConfirm, verifCancel, verifScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_verification);

        inboundDate = findViewById(R.id.tv_inbound_date_val);
        inboundDetail = findViewById(R.id.tv_inbound_detail_val);
        inboundWarehouse = findViewById(R.id.tv_inbound_warehouse_val);
        totalScan = findViewById(R.id.tv_total_scan_val);
        totalSummaryInbound = findViewById(R.id.tv_total_summary_inbounds_val);

        Spinner spinner = findViewById(R.id.spinner_inbound_no);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dummy_inbound_no, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String item = (String) adapterView.getItemAtPosition(pos);
                Log.d(TAG, item);
                if (!item.trim().equalsIgnoreCase("")) {
                    currentSelectedInboundNo = item;
                    setInboundDetailValue(true, currentSelectedInboundNo);
                }
                Toast.makeText(GoodsVerificationActivity.this, item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initializeBtn();
        Intent intent = getIntent();
        String totalScanExtra = intent.getStringExtra(MiscUtil.TotalScanKey);
        if (totalScanExtra != null && !totalScanExtra.trim().equalsIgnoreCase("")) {
            Log.d(TAG, "onCreate: total Scan = " + totalScanExtra);
            totalScanParent = Integer.parseInt(totalScanExtra);
            totalScan.setText(totalScanExtra);
        }
    }

    public void initializeBtn() {
        verifConfirm = findViewById(R.id.btn_goods_verif_confirm);
        verifCancel = findViewById(R.id.btn_goods_verif_cancel);
        verifScan = findViewById(R.id.btn_goods_verif_scan);
        verifConfirm.setOnClickListener(view -> {
            Toast.makeText(this, "Save Scan Result", Toast.LENGTH_SHORT).show();
        });
        verifCancel.setOnClickListener(view -> {
            Toast.makeText(this, "Cancel Scan Result", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
        verifScan.setOnClickListener(view -> {
            if (currentSelectedInboundNo.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose inbound no first", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Go to Scan Result", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GoodsVerificationActivity.this, GoodsVerificationScanResultActivity.class);
                intent.putExtra(MiscUtil.InboundNoKey, currentSelectedInboundNo);
                intent.putExtra(MiscUtil.TotalScanKey, totalScanParent.toString());
                MiscUtil.saveStringSharedPreferenceAsString(this,MiscUtil.InboundNoKey,currentSelectedInboundNo);
                MiscUtil.saveStringSharedPreferenceAsString(this,MiscUtil.TotalScanKey,totalScanParent.toString());
                startActivity(intent);
            }
        });
    }

    public void setInboundDetailValue(Boolean dummy, String inboundNo) {
        if (!inboundNo.trim().equalsIgnoreCase("")) {
            if (dummy) {
                if (inboundNo.equalsIgnoreCase("001")) {
                    inboundDate.setText("15/11/2020");
                    inboundDetail.setText("Detail Inbound From A");
                    inboundWarehouse.setText("Pancaran Group A");
                    if(totalScanParent>0){
                        totalScan.setText(totalScanParent.toString());
                    }else{
                        totalScan.setText("0");
                    }
                    totalSummaryInbound.setText("15");
                } else if (inboundNo.equalsIgnoreCase("005")) {
                    inboundDate.setText("27/10/2020");
                    inboundDetail.setText("Detail Inbound From C");
                    inboundWarehouse.setText("Pancaran Group C");
                    if(totalScanParent>0){
                        totalScan.setText(totalScanParent.toString());
                    }else{
                        totalScan.setText("0");
                    }
                    totalSummaryInbound.setText("10");
                }
            } else {
                //real value
                inboundDate.setText("");
                inboundDetail.setText("");
                inboundWarehouse.setText("");
                totalScan.setText("");
                totalSummaryInbound.setText("");
            }
        }
    }
}