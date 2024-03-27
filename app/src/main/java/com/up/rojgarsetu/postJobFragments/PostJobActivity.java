package com.up.rojgarsetu.postJobFragments;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.up.rojgarsetu.Employer.EmployerMainFragment;
import com.up.rojgarsetu.MainActivity;
import com.up.rojgarsetu.R;

public class PostJobActivity extends AppCompatActivity {

    public static String postJobFragmentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_post_job);
        postJobFragmentName = "jobpage1";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_postjob,new jobPage1());
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        */


        if(postJobFragmentName.equals("jobpage1")){
            finish();
        }
        else if(postJobFragmentName.equals("jobpage2")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_postjob,new jobPage1());
            ft.commit();
        }
        else if(postJobFragmentName.equals("jobpage3")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_postjob,new jobPage2());
            ft.commit();
        }
        else if(postJobFragmentName.equals("jobpage4")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_postjob,new jobPage3());
            ft.commit();
        }

    }


}