package com.up.rojgarsetu.Employer.NavDrawer.About;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import io.paperdb.Paper;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.up.rojgarsetu.Employer.ResetPassword.ResetPasswordActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerAboutFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    EditText et_companyname, et_contactNum, et_email;
    Map<String,Object> datamap=new HashMap<>();
    String email;

    CardView card_accept,card_edit, cardResetPass;
    ProgressDialog progressDialog;

    TextView tv_companyName, tv_contactNum,tv_contactEmail,textView19,currentHeading,resetPassword;


    public EmployerAboutFragment1() {
        // Required empty public constructor
    }

    public static EmployerAboutFragment1 newInstance(String param1, String param2) {
        EmployerAboutFragment1 fragment = new EmployerAboutFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_employer_about1, container, false);

        Fragment_Name = getString(R.string.Employer_Details);
        currentHeading = getActivity().findViewById(R.id.currentHeading);
        currentHeading.setText(Fragment_Name);


        et_contactNum=v.findViewById(R.id.et_contactNum);
        et_companyname=v.findViewById(R.id.et_companyName);
        et_email=v.findViewById(R.id.et_contactEmail);

        card_accept = v.findViewById(R.id.card_employer_about_accept);
        card_edit = v.findViewById(R.id.card_employerAbout_edit);
        cardResetPass = v.findViewById(R.id.card_resetPass);

        tv_contactEmail=v.findViewById(R.id.tv_contactEmail);
        tv_contactNum = v.findViewById(R.id.tv_contactNum);
        tv_companyName=v.findViewById(R.id.tv_companyName);

        textView19=v.findViewById(R.id.textView19);
        resetPassword=v.findViewById(R.id.textView17);

        //bt_resetpass=v.findViewById(R.id.bt_resetpass);


        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.savingData));
        }





        //--------------------Initialising Spinners-------------------------------


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);







        //----------------------Initilising Spinners End------------------------


        updateView((String) Paper.book().read("language"));

        et_companyname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_companyname.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_companyname.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_contactNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_contactNum.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_contactNum.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_email.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_email.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            email = firebaseUser.getEmail();
        }else{
            Toast.makeText(activity, "Please reload the page!", Toast.LENGTH_SHORT).show();
        }
        SetEditTextsEnabled(false);
        GetDatabaseInfo(email);

        card_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetEditTextsEnabled(true);
                SetEditTextsEnabled(true);

            }
        });

        //----------------Password Reset---------------------------



        cardResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);

            }
        });



        card_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();


                if(et_email.getText().toString().length()==0 ||
                        et_contactNum.getText().toString().length()==0 ||
                        et_companyname.getText().toString().length()==0 )
                {
                    progressDialog.dismiss();
                    if(et_companyname.getText().toString().length()==0){
                        et_companyname.setBackgroundResource(R.drawable.back_red_round_corners);
                        Toast.makeText(getActivity(), "Enter Company Name", Toast.LENGTH_SHORT).show();
                    }
                    if(et_email.getText().toString().length()==0 || isEmailValid(et_email.getText().toString())){
                        Toast.makeText(getActivity(), "Enter Valid Email", Toast.LENGTH_SHORT).show();
                        et_email.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_contactNum.getText().toString().length()==0){
                        Toast.makeText(getActivity(), "Enter Valid Contact Number", Toast.LENGTH_SHORT).show();
                        et_contactNum.setBackgroundResource(R.drawable.back_red_round_corners);
                    }

                }



                else{
                    UpdateDataMap();
                    SetDatabaseInfo(email);
                    SetEditTextsEnabled(false);
                }

            }
        });

        return v;
    }


    void SetEditTextsEnabled(boolean flag){
        if(flag){
            card_accept.setVisibility(View.VISIBLE);
            card_edit.setVisibility(View.GONE);
        }
        else{
            card_accept.setVisibility(View.GONE);
            card_edit.setVisibility(View.VISIBLE);
        }
        et_email.setEnabled(flag);
        et_contactNum.setEnabled(flag);
        et_companyname.setEnabled(flag);

    }

    void GetDatabaseInfo(String email){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference docRef = db.collection("users").document(email);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    datamap=snapshot.getData();
                    UpdateFields();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    void UpdateFields(){

        try{et_companyname.setText(datamap.get("organisationName").toString());}
        catch(Exception e){ et_companyname.setText(datamap.get("frag1_Full Name of Factory/Establishment").toString());}



        try{ et_email.setText(datamap.get("employerEmailId").toString());}
        catch(Exception e){ et_email.setText(datamap.get("frag3_Employer Email ID").toString());}


        try{et_contactNum.setText(datamap.get("employerPhone").toString());}
        catch(Exception e){et_contactNum.setText(datamap.get("frag1_Telephone").toString()); }


    }


    void UpdateDataMap(){
        datamap.put("employerEmailId",et_email.getText().toString());
        datamap.put("employerPhone",et_contactNum.getText().toString());
        datamap.put("organisationName",et_companyname.getText().toString());
    }

    void SetDatabaseInfo(String email){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("users").document(email)
                .set(datamap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Changes Saved Successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Changes were not saved", Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();

        currentHeading.setText(resources.getString(R.string.Employer_Details));
        tv_companyName.setText(resources.getString(R.string.company_Name));
        tv_contactEmail.setText(resources.getString(R.string.contactEmail));
        tv_contactNum.setText(resources.getString(R.string.contactNum));
        textView19.setText(resources.getString(R.string.editDetails));

        resetPassword.setText(resources.getString(R.string.Reset_Password));
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    boolean isValidURL(String potentialUrl){

        return  Patterns.WEB_URL.matcher(potentialUrl).matches();
    }
}