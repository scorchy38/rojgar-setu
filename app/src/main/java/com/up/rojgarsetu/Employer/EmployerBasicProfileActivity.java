package com.up.rojgarsetu.Employer;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.up.rojgarsetu.MainActivity;
import com.up.rojgarsetu.R;

import java.util.HashMap;
import java.util.Map;


public class EmployerBasicProfileActivity extends AppCompatActivity {


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    TextView welcome_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employer_basic_profile);

        getSupportActionBar().hide();

        welcome_tv = findViewById(R.id.tv_welcomeUser);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        welcome_tv.setText("Welcome, "+ firebaseUser.getEmail());
        Log.e("UserEmail" ,"Email: "+ firebaseUser.getEmail());


    }

    String companyName, contactNum, contactEmail;


    public void onSave(View v){

        EditText edt = findViewById(R.id.et_companyName);
        companyName = edt.getText().toString();

        edt = findViewById(R.id.et_contactNum);
        contactNum = edt.getText().toString();

        if(companyName.length()<1){
            Toast.makeText(this, "Please enter Company Name", Toast.LENGTH_SHORT).show();
        }
        else if(contactNum.length()<10){
            Toast.makeText(this, "Please enter Contact Number", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadData();
        }


    }


    public void uploadData(){


        Map<String, Object> uploadMap = new HashMap<>();
        uploadMap.put("employerEmailId", firebaseUser.getEmail());
        uploadMap.put("employerPhone", contactNum);
        uploadMap.put("organisationName", companyName);



        firestore.document("users/"+firebaseUser.getEmail()).set(uploadMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EmployerBasicProfileActivity.this, "Save Successful!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(EmployerBasicProfileActivity.this, MainActivity.class));
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmployerBasicProfileActivity.this, "Unable to Save. Check connection and Retry", Toast.LENGTH_SHORT).show();

            }
        });

    }


}