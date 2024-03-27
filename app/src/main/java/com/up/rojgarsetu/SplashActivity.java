package com.up.rojgarsetu;

import android.animation.ObjectAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.EmployeeMainActivity;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.Notification.NotificationOpenedHandler;

import java.util.Map;

import io.paperdb.Paper;


public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }


    protected void onStart () {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .init();
        OneSignal.setSubscription(true);

        mAuth = FirebaseAuth.getInstance();

        CardView cv = findViewById(R.id.logo_card);
        ObjectAnimator animator = ObjectAnimator.ofFloat(cv,"translationY",0);
        animator.setDuration(800);
        animator.start();

        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));





        //-------------------------------Copied from OnStart------------------------------



        SharedPreferences prefs = getSharedPreferences("USER TYPE", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        Log.d("Splash", String.valueOf(prefs.getAll()));
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(!map.containsKey("usertype")){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences prefsFirstLaunch = getSharedPreferences("FirstLaunch", MODE_PRIVATE);
                    Map<String, ?> mapFirstLaunch = prefsFirstLaunch.getAll();

                    if(!mapFirstLaunch.containsKey("FirstLaunch")){
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.splash_framelayout, new FirstView());
                        ft.commitAllowingStateLoss();
                    }
                    else{
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.splash_framelayout, new UserTypeSelection());
                        ft.commitAllowingStateLoss();

                    }







                    /*

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);


                    alertDialog.setTitle("Select Job Post");
                    alertDialog.setMessage("Use app as?");
                    alertDialog.setPositiveButton("Employer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences.Editor editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                            editor.putString("usertype", "Employer");
                            editor.apply();

                            Intent i = new Intent(SplashActivity.this, EmployerLoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    alertDialog.setNegativeButton("Employee", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences.Editor editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                            editor.putString("usertype", "Employee");
                            editor.apply();

                            Intent intent=new Intent(SplashActivity.this, EmployeeLoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    alertDialog.show();

                     */


                }
            },2000);


        }

        else if(map.get("usertype").toString().equals("Employer")){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    Intent i = new Intent(SplashActivity.this, EmployerLoginActivity.class);
                    startActivity(i);
                    finish();

                }},2000);

        }
        else if(map.get("usertype").toString().equals("Employee")){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    Intent intent=new Intent(SplashActivity.this, EmployeeMainActivity.class);
                    startActivity(intent);
                    finish();
                }},2000);

        }


    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
    }

}
