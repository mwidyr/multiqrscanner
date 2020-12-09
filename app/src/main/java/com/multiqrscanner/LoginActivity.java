package com.multiqrscanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.duxina.baranenergy.baranenergyapps.Utils.CustomToast;
import com.multiqrscanner.navdrawer.NavigationViewActivity;


public class LoginActivity extends AppCompatActivity {

    private static EditText username, password;
    private static Button loginButton;
    private static CheckBox show_hide_password;
    private static RelativeLayout loginLayout;
    private static Animation shakeAnimation;
    private ProgressBar progressBar;

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
        shakeAnimation = AnimationUtils.loadAnimation(this ,
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
        String emailSave = username.getText().toString();
        String passwordSave = password.getText().toString();

        if (false) {

        } else if (true) {
            progressBar.setVisibility(View.GONE);
            Intent userInfo = new Intent(this, NavigationViewActivity.class);
            startActivity(userInfo);
            if (this != null)
                this.finish();
        }

        //sigin user
//        firebaseAuth.signInWithEmailAndPassword(emailSave, passwordSave).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (!task.isSuccessful()) {
//                    progressBar.setVisibility(View.GONE);
//                    try {
//                        throw task.getException();
//                    } catch (FirebaseAuthInvalidUserException invalidUser) {
//                        Toast.makeText(getActivity(), "Email does not exist", Toast.LENGTH_SHORT)
//                                .show();
//                    } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
//                        Toast.makeText(getActivity(), "Password did not match", Toast.LENGTH_SHORT)
//                                .show();
//                    } catch (Exception ex) {
//                        Toast.makeText(getActivity(), "Couldn't Login... please try again", Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                } else if (task.isSuccessful()) {
//                    progressBar.setVisibility(View.GONE);
//                    Intent userInfo = new Intent(getActivity(), MainActivity.class);
//                    startActivity(userInfo);
//                    if(getActivity() != null)
//                        getActivity().finish();
//                }
//            }
//        });
    }
}
