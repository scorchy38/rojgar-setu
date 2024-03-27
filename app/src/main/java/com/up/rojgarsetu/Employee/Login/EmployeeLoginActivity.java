package com.up.rojgarsetu.Employee.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.EmployeeDetailForm;
import com.up.rojgarsetu.Employee.EmployeeMainActivity;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class EmployeeLoginActivity extends AppCompatActivity {

    String phoneNumber="";
    Button bt_activity_employee_login_generateotp;
    EditText et_activity_employee_login_phonenumber;
    EditText et_activity_employee_login_otp;
    String verificationCodeBySystem;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    Button bt_activity_employee_login_submit;
    TextView tv_activity_employee_login_resendotp, loginAsEmployer;
    CardView card_genOTP, card_verifyOTP;

    ProgressDialog progressDialog;
    Boolean OTPCard=false;

    TextView textView23,textView24,lc_entermobile,lc_enterotp;

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }

    protected void onStart () {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
          //  Toast.makeText(this, "Sign-in Successful!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, EmployeeMainActivity.class);
            startActivity(intent);
            finish();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);
        getSupportActionBar().hide();
        et_activity_employee_login_otp=findViewById(R.id.et_activity_employee_login_otp);
        et_activity_employee_login_phonenumber= findViewById(R.id.et_activity_employee_login_phonenumber);
        bt_activity_employee_login_generateotp=findViewById(R.id.bt_activity_employee_login_generateotp);
        bt_activity_employee_login_submit=findViewById(R.id.bt_activity_employee_login_submit);
        tv_activity_employee_login_resendotp=findViewById(R.id.tv_activity_employee_login_resendotp);
        card_genOTP = findViewById(R.id.card_genOTP);
        card_verifyOTP = findViewById(R.id.card_verifyOTP);
        progressDialog = new ProgressDialog(this);
        loginAsEmployer = findViewById(R.id.tv_loginAsEmployer);

        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        lc_entermobile = findViewById(R.id.lc_entermobile);
        lc_enterotp = findViewById(R.id.lc_enterotp);


        mAuth = FirebaseAuth.getInstance();

        //Change Language
        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));


        bt_activity_employee_login_generateotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage(getResources().getString(R.string.SendingOTP));
                progressDialog.show();


                phoneNumber=et_activity_employee_login_phonenumber.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        EmployeeLoginActivity.this,               // Activity (for callback binding)
                        mCallbacks);

            }
        });

        bt_activity_employee_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=et_activity_employee_login_otp.getText().toString();
                progressDialog.setMessage(getResources().getString(R.string.LoggingIn));
                progressDialog.show();
                verifyCode(code);
            }
        });

        tv_activity_employee_login_resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=et_activity_employee_login_phonenumber.getText().toString();
                resendVerificationCode(phoneNumber,mResendToken);

            }
        });

        loginAsEmployer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginAsEmployer.setBackgroundColor(getResources().getColor(R.color.Grey));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginAsEmployer.setBackgroundColor(Color.WHITE);
                    }
                },100);

                Intent i = new Intent (EmployeeLoginActivity.this, EmployerLoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(OTPCard){
            ObjectAnimator animator = ObjectAnimator.ofFloat(card_genOTP,"translationX",0);
            animator.setDuration(800);
            animator.start();
            animator = ObjectAnimator.ofFloat(card_verifyOTP,"translationX",18000);
            animator.setDuration(800);
            animator.start();

            OTPCard=false;

        }
        else{
            super.onBackPressed();
        }
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            ObjectAnimator animator = ObjectAnimator.ofFloat(card_genOTP,"translationX",-1800);
            animator.setDuration(800);
            animator.start();
            animator = ObjectAnimator.ofFloat(card_verifyOTP,"translationX",0);
            animator.setDuration(800);
            animator.start();

            OTPCard=true;
            et_activity_employee_login_otp.requestFocus();
            progressDialog.dismiss();



            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem=s;
            Toast.makeText(EmployeeLoginActivity.this, "OTP Generated", Toast.LENGTH_SHORT).show();
            mResendToken = forceResendingToken;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                //Toast.makeText(EmployeeLoginActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();

                verifyCode(code);
            }

        }



        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(EmployeeLoginActivity.this, "Please try again!" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser){
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }
        catch(Exception e){
            Toast.makeText(this, "Try Again or Check Network Connection.", Toast.LENGTH_SHORT).show();

            Log.e("EmployeeLogin", "Verify Code: "+ e.getLocalizedMessage());
        }
    }

    private  void signInTheUserByCredentials(PhoneAuthCredential credential){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(EmployeeLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            SharedPreferences.Editor editor = getSharedPreferences("EMPLOYEE DETAILS", MODE_PRIVATE).edit();
                            editor.putString("phonenumber", phoneNumber);
                            editor.apply();
                            Toast.makeText(EmployeeLoginActivity.this, "OTP Verification Successful!", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editorUser = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                            editorUser.putString("usertype","Employee");
                            editorUser.commit();
                            progressDialog.dismiss();

                            OneSignal.sendTag("Type","employee");
                            OneSignal.sendTag("UserID","+91"+phoneNumber);
                            OneSignal.setSubscription(true);

                            boolean isNew =  task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                Intent intent=new Intent(EmployeeLoginActivity.this, EmployeeDetailForm.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent=new Intent(EmployeeLoginActivity.this, EmployeeMainActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeLoginActivity.this, "Sign In failed!", Toast.LENGTH_SHORT).show();
                            Log.e("EmployeeLogin", "ErrorSign In The User Task failed");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.usermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.usermenu_switch_usertype) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployeeLoginActivity.this);


            alertDialog.setTitle("Select Job Post");
            alertDialog.setMessage("Use app as?");
            alertDialog.setPositiveButton("Employer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                    editor.putString("usertype", "Employer");
                    editor.apply();

                    Toast.makeText(EmployeeLoginActivity.this, "Employer User Type Selected", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(EmployeeLoginActivity.this, EmployerLoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            alertDialog.setNegativeButton("Employee", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(EmployeeLoginActivity.this, "Employee User Type Already Selected", Toast.LENGTH_SHORT).show();


                }
            });
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);

    }
    private void updateView(String lang) {

        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();

        textView23.setText(resources.getText(R.string.JobSeeker));
        textView24.setText(resources.getText(R.string.Login));
        lc_entermobile.setText(resources.getText(R.string.EnterMobileNumber));
        bt_activity_employee_login_generateotp.setText(resources.getText(R.string.GenerateOTP));
        loginAsEmployer.setText(resources.getText(R.string.loginAsEmployer));
        lc_enterotp.setText(resources.getText(R.string.EnterOTP));
        bt_activity_employee_login_submit.setText(resources.getText(R.string.continue_text));
        tv_activity_employee_login_resendotp.setText(resources.getText(R.string.ResendOTP));

    }

}