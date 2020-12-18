package com.multiqrscanner.misc;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.multiqrscanner.R;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public String title, description;
    public TextView titleTv, descriptionTv;

    public CustomDialogClass(Activity a, String title, String description) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.title = title;
        this.description = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        titleTv = findViewById(R.id.txt_dia_title);
        descriptionTv = findViewById(R.id.txt_dia_desc);
        if(title != null && !title.trim().equalsIgnoreCase("")){
            titleTv.setText(title);
        }
        if(description != null && !description.trim().equalsIgnoreCase("")){
            descriptionTv.setText(description);
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
