package com.multiqrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.duxina.baranenergy.baranenergyapps.Utils.CustomToast;
import com.google.gson.Gson;
import com.multiqrscanner.misc.MiscUtil;
import com.multiqrscanner.navdrawer.NavigationViewActivity;
import com.multiqrscanner.network.RetrofitClientInstance;
import com.multiqrscanner.network.model.RetroLogin;
import com.multiqrscanner.network.model.RetroUser;
import com.multiqrscanner.network.user.GetLoginService;


public class LoginActivity extends AppCompatActivity {

    private static EditText username, password;
    private static Button loginButton;
    private static CheckBox show_hide_password;
    private static RelativeLayout loginLayout;
    private static Animation shakeAnimation;
    private ProgressBar progressBar;

    private static final String TAG = "LoginActivity";
    private static final String ALL_WAREHOUSE = "All Warehouse";

    public LoginActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setListeners();
    }


    // Initiate Views
    private void initViews() {
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginBtn);
        show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);
        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);

        // loading bar
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);

        // Setting text selector over textviews
/*        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);

//            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }*/
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {
                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {
                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text
                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTextAppearance(LoginActivity.this, R.style.fontForShowPassword);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text
                            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTextAppearance(LoginActivity.this, R.style.fontForShowPassword);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
                        }
                    }
                });
    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getusername = username.getText().toString();
        String getPassword = password.getText().toString();
        // Check for both field is empty or not
        if (getusername.equals("") || getusername.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(this,
                    "Enter both credentials.");
        } else
            loginProcess();
    }


    //login activity
    private void loginProcess() {
        //set loading bar
        progressBar.setVisibility(View.VISIBLE);
        //get email and password
        String txtUsername = username.getText().toString();
        String txtPassword = password.getText().toString();
        GetLoginService service = RetrofitClientInstance.getRetrofitInstance().create(GetLoginService.class);
        Call<RetroLogin> call = service.loginUser(new RetroUser(txtUsername, txtPassword));
        call.enqueue(new Callback<RetroLogin>() {
            @Override
            public void onResponse(Call<RetroLogin> call, Response<RetroLogin> response) {
                if (response.body().getResultCode() == 1) {
                    Log.d(TAG, "role = " + response.body().getRole());
                    Log.d(TAG, "warehouse = " + response.body().getWarehouse());
                    MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityUser, txtUsername);
                    MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityRole, response.body().getRole());
                    MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityUserID, response.body().getUserId());
                    if (!response.body().getWarehouse().equalsIgnoreCase("")) {
                        MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityWS, response.body().getWarehouse());
                    } else {
                        MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityWS, ALL_WAREHOUSE);
                    }
                    Gson gson = new Gson();
                    MiscUtil.saveStringSharedPreferenceAsString(LoginActivity.this, MiscUtil.LoginActivityMenu, gson.toJson(response.body().getMenus()));
                    progressBar.setVisibility(View.GONE);
                    Intent userInfo = new Intent(LoginActivity.this, NavigationViewActivity.class);
                    startActivity(userInfo);
                } else {
                    new CustomToast().Show_Toast(LoginActivity.this, "User or Password is wrong");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RetroLogin> call, Throwable t) {
            }
        });
    }
}

