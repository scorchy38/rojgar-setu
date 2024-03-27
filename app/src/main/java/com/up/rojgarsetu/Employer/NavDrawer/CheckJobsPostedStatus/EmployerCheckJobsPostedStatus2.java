package com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.paperdb.Paper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.ProfessionActivity;
import com.up.rojgarsetu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerCheckJobsPostedStatus2 extends AppCompatActivity {

   
    EditText et_frag2_checkjobpostedstatus_Address;
    EditText et_frag2_checkjobpostedstatus_contactNumber;
    EditText et_frag2_checkjobpostedstatus_hirePosition;
    EditText et_frag2_checkjobpostedstatus_hireDone;
    EditText et_frag2_checkjobpostedstatus_company;
    EditText et_frag2_checkjobpostedstatus_jobTitle;
    String id="";
    String email="";
    Spinner spinner_about_state;
    Spinner spinner_about_district;
    EditText et_frag2_checkjobpostedstatus_startingRs;
    EditText et_frag2_checkjobpostedstatus_endingRs, et_skill;
    ToggleButton toggleButton_perDay;
    ToggleButton toggleButton_perMonth;
    ToggleButton toggleButton_perYear;
    ToggleButton toggleButton_cvYes;
    ToggleButton toggleButton_cvNo;
    ToggleButton toggleButton_cvOptional;
    CardView card_cjp_accept, card_cjp_delete, card_cjp_back;
    ProgressDialog progressDialog;
    CardView selectSkills;

    TextView lc_jobtitle,lc_companyname,lc_state3,lc_district3,textView10,
            textView5,textView6,textView7,lc_cvreq,textView9,lc_phone3,lc_address3,
            textView20,textView25,textView21,lc_jobdescription3, titlecjps_skills,currentHeading,btnSelect;



    final String[] SalaryPeriod = new String[1];
    final String[] CvRequired = new String[1];
    final Integer[] jobType = new Integer[1];
    EditText et_frag2_checkjobpostedstatus_jobDescription;

    Map<String,Object> datamap=new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.fragment_employer_check_jobs_posted_status2);
        getSupportActionBar().hide();


        et_frag2_checkjobpostedstatus_jobTitle=findViewById(R.id.et_frag2_checkjobpostedstatus_jobTitle);
        et_frag2_checkjobpostedstatus_company=findViewById(R.id.et_frag2_checkjobpostedstatus_company);
        et_frag2_checkjobpostedstatus_hirePosition=findViewById(R.id.et_frag2_checkjobpostedstatus_hirePosition);
        et_frag2_checkjobpostedstatus_contactNumber=findViewById(R.id.et_frag2_checkjobpostedstatus_contactNumber);
        et_frag2_checkjobpostedstatus_Address=findViewById(R.id.et_frag2_checkjobpostedstatus_Address);
        et_frag2_checkjobpostedstatus_startingRs=findViewById(R.id.et_frag2_checkjobpostedstatus_startingRs);
        et_frag2_checkjobpostedstatus_endingRs=findViewById(R.id.et_frag2_checkjobpostedstatus_endingRs);
        et_frag2_checkjobpostedstatus_jobDescription=findViewById(R.id.et_frag2_checkjobpostedstatus_jobDescription);
        toggleButton_perDay = findViewById(R.id.tb_frag2_checkjobpostedstatus_perDay);
        toggleButton_perMonth = findViewById(R.id.tb_frag2_checkjobpostedstatus_perMonth);
        toggleButton_perYear = findViewById(R.id.tb_frag2_checkjobpostedstatus_perYear);
        et_skill = findViewById(R.id.et_skillReq);
        selectSkills = findViewById(R.id.select_skills);
        toggleButton_cvYes = findViewById(R.id.tb_frag2_checkjobpostedstatus_Yes);
        toggleButton_cvNo = findViewById(R.id.tb_frag2_checkjobpostedstatus_No);
        toggleButton_cvOptional = findViewById(R.id.tb_frag2_checkjobpostedstatus_Optional);
        btnSelect = findViewById(R.id.textView46);

        card_cjp_accept = findViewById(R.id.card_checkJobsPosted_accept);
        card_cjp_delete = findViewById(R.id.card_checkJobsPosted_delete);
        card_cjp_back = findViewById(R.id.card_checkJobsPosted_back);

        lc_jobtitle=findViewById(R.id.lc_jobtitle);
        lc_jobdescription3=findViewById(R.id.lc_jobdescription3);
        lc_companyname=findViewById(R.id.lc_companyname);
        lc_state3=findViewById(R.id.lc_state3);
        lc_district3=findViewById(R.id.lc_district3);
        textView10=findViewById(R.id.textView10);
        textView5=findViewById(R.id.textView5);
        textView6=findViewById(R.id.textView6);
        textView7=findViewById(R.id.textView7);
        titlecjps_skills=findViewById(R.id.titlecjps_skills);
        lc_cvreq=findViewById(R.id.lc_cvreq);
        textView9=findViewById(R.id.textView9);
        lc_phone3=findViewById(R.id.lc_phone3);
        lc_address3=findViewById(R.id.lc_address3);
        textView20=findViewById(R.id.textView20);
        textView25=findViewById(R.id.textView25);
        textView21=findViewById(R.id.textView21);
        selectSkills = findViewById(R.id.selectSkills);




        updateView((String) Paper.book().read("language"));

        Activity activity = EmployerCheckJobsPostedStatus2.this;
        if(activity != null) {
            progressDialog = new ProgressDialog(EmployerCheckJobsPostedStatus2.this);
            progressDialog.setMessage(getString(R.string.changingDetails));
        }

        SharedPreferences prefs = EmployerCheckJobsPostedStatus2.this.getSharedPreferences("EmployerCheckJobsPostedStatus", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        id=map.get("id").toString();

        try {
            prefs = EmployerCheckJobsPostedStatus2.this.getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
            map = prefs.getAll();
            email = map.get("frag3_Employer Email ID").toString();
        }
        catch(Exception e){
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        selectSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmployerCheckJobsPostedStatus2.this, ProfessionActivity.class);
                startActivityForResult(i, 10);

            }
        });

        spinner_about_state=findViewById(R.id.spinner_frag2_checkjobpostedstatus_state);
        final ArrayList<String> states= new ArrayList<>();
        states.add(0,"Select");
        final ArrayAdapter stateAdaptor= new ArrayAdapter(EmployerCheckJobsPostedStatus2.this,R.layout.spinner_style,states);
        stateAdaptor.setDropDownViewResource(R.layout.spinner_style);
        db.document("states/English").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String,?> map = task.getResult().getData();
                states.clear();
                for(String m:map.keySet()){
                    states.add(m);
                }
                Collections.sort(states);
                states.add(0,"Select");
                stateAdaptor.notifyDataSetChanged();
                if(datamap.get("StatePosition")!=null)
                    spinner_about_state.setSelection(Integer.valueOf(datamap.get("StatePosition").toString()));

            }
        });
        final ArrayList<String> districtArray = new ArrayList<>();
        districtArray.add(0,"Select");
        final ArrayAdapter districtAdaptor= new ArrayAdapter(EmployerCheckJobsPostedStatus2.this,R.layout.spinner_style,districtArray);
        spinner_about_district=findViewById(R.id.spinner_frag2_checkjobpostedstatus_district);
        spinner_about_state.setAdapter(stateAdaptor);
        spinner_about_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                spinner_about_state.setBackgroundResource(R.drawable.back_grey_round_corners);
                if(position!=0) {
                    db.document("states/English").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String districtFetch = task.getResult().getData().get(states.get(position)).toString();
                            districtArray.clear();
                            Collections.addAll(districtArray, districtFetch.split(","));
                            Collections.sort(districtArray);
                            districtArray.add(0,"Select");
                            districtAdaptor.notifyDataSetChanged();
                            if(datamap.get("DistrictPosition")!=null)
                                spinner_about_district.setSelection(Integer.valueOf(datamap.get("DistrictPosition").toString()));
                            else
                                spinner_about_district.setSelection(0);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtAdaptor.setDropDownViewResource(R.layout.spinner_style);
        spinner_about_district.setAdapter(districtAdaptor);
        spinner_about_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_about_district.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        spinner_frag2_checkjobpostedstatus_jobType=findViewById(R.id.spinner_frag2_checkjobpostedstatus_jobType);
        ArrayAdapter adaptor=ArrayAdapter.createFromResource(getContext(),R.array.JobTypeArray,android.R.layout.simple_spinner_item);
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_frag2_checkjobpostedstatus_jobType.setAdapter(adaptor);
        spinner_frag2_checkjobpostedstatus_jobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_frag2_checkjobpostedstatus_jobType.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
//---------------------Getting jobType from Firestore instead of strings.xml------------


        toggleButton_perDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SalaryPeriod[0] = "perDay";
                    toggleButton_perDay.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_perDay.setTextColor(Color.WHITE);

                    toggleButton_perMonth.setChecked(false);
                    toggleButton_perMonth.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perMonth.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_perYear.setChecked(false);
                    toggleButton_perYear.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perYear.setTextColor(getResources().getColor(R.color.OrangeDark));
                }

            }
        });

        toggleButton_perMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SalaryPeriod[0] = "perMonth";
                    toggleButton_perMonth.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_perMonth.setTextColor(Color.WHITE);

                    toggleButton_perDay.setChecked(false);
                    toggleButton_perDay.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perDay.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_perYear.setChecked(false);
                    toggleButton_perYear.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perYear.setTextColor(getResources().getColor(R.color.OrangeDark));
                }
            }
        });
        toggleButton_perYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SalaryPeriod[0] = "perYear";
                    toggleButton_perYear.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_perYear.setTextColor(Color.WHITE);

                    toggleButton_perMonth.setChecked(false);
                    toggleButton_perMonth.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perMonth.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_perDay.setChecked(false);
                    toggleButton_perDay.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_perDay.setTextColor(getResources().getColor(R.color.OrangeDark));
                }
            }
        });



        toggleButton_cvYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CvRequired[0] = "Yes";
                    toggleButton_cvYes.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_cvYes.setTextColor(Color.WHITE);

                    toggleButton_cvNo.setChecked(false);
                    toggleButton_cvNo.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvNo.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_cvOptional.setChecked(false);
                    toggleButton_cvOptional.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvOptional.setTextColor(getResources().getColor(R.color.OrangeDark));
                }

            }
        });

        toggleButton_cvNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CvRequired[0] = "No";
                    toggleButton_cvNo.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_cvNo.setTextColor(Color.WHITE);

                    toggleButton_cvYes.setChecked(false);
                    toggleButton_cvYes.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvYes.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_cvOptional.setChecked(false);
                    toggleButton_cvOptional.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvOptional.setTextColor(getResources().getColor(R.color.OrangeDark));
                }

            }
        });

        toggleButton_cvOptional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CvRequired[0] = "Optional";
                    toggleButton_cvOptional.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    toggleButton_cvOptional.setTextColor(Color.WHITE);

                    toggleButton_cvYes.setChecked(false);
                    toggleButton_cvYes.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvYes.setTextColor(getResources().getColor(R.color.OrangeDark));

                    toggleButton_cvNo.setChecked(false);
                    toggleButton_cvNo.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    toggleButton_cvNo.setTextColor(getResources().getColor(R.color.OrangeDark));
                }

            }
        });


        et_frag2_checkjobpostedstatus_jobTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_jobTitle.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_jobTitle.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_company.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_company.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_company.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_hirePosition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_hirePosition.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_hirePosition.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_contactNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_contactNumber.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_contactNumber.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_Address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_Address.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_Address.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_startingRs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_startingRs.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_startingRs.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_endingRs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_endingRs.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_endingRs.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag2_checkjobpostedstatus_jobDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_checkjobpostedstatus_jobDescription.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_checkjobpostedstatus_jobDescription.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        SetSpinnersEnabled(false);
        GetDatabaseInfo(email,id);



        /*card_cjp_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetEditTextsEnabled(true);
                SetSpinnersEnabled(true);
                SetToggleButtonsEnabled(true);

            }
        });

         */

        card_cjp_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                if(et_frag2_checkjobpostedstatus_jobTitle.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_jobDescription.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_company.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_hirePosition.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_company.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_Address.getText().toString().length()==0 ||
                        et_skill.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_startingRs.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_endingRs.getText().toString().length()==0 ||
                        et_frag2_checkjobpostedstatus_contactNumber.getText().toString().length()==0 ||
                        spinner_about_state.getItemAtPosition(spinner_about_state.getSelectedItemPosition()).toString().equals("Select") ||
                        spinner_about_district.getItemAtPosition(spinner_about_district.getSelectedItemPosition()).toString().equals("Select"))
                {
                    Toast.makeText(EmployerCheckJobsPostedStatus2.this, "All Fields marked (*) should be filled", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    if(et_frag2_checkjobpostedstatus_jobTitle.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_jobTitle.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_jobDescription.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_jobDescription.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_contactNumber.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_contactNumber.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_company.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_company.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_hirePosition.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_hirePosition.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_company.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_company.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_Address.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_Address.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_startingRs.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_startingRs.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag2_checkjobpostedstatus_endingRs.getText().toString().length()==0){
                        et_frag2_checkjobpostedstatus_endingRs.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_about_state.getItemAtPosition(spinner_about_state.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_about_state.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_about_district.getItemAtPosition(spinner_about_district.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_about_district.setBackgroundResource(R.drawable.back_red_round_corners);
                    }

                }

                else if(Integer.parseInt(et_frag2_checkjobpostedstatus_startingRs.getText().toString())>Integer.parseInt(et_frag2_checkjobpostedstatus_endingRs.getText().toString())){
                    progressDialog.dismiss();
                    Toast.makeText(EmployerCheckJobsPostedStatus2.this, "Max. Salary should be higher than Min. Salary", Toast.LENGTH_SHORT).show();
                    et_frag2_checkjobpostedstatus_startingRs.setBackgroundResource(R.drawable.back_red_round_corners);
                    et_frag2_checkjobpostedstatus_endingRs.setBackgroundResource(R.drawable.back_red_round_corners);

                }
                else if(et_frag2_checkjobpostedstatus_contactNumber.getText().toString().length()!=10){
                    progressDialog.dismiss();
                    Toast.makeText(EmployerCheckJobsPostedStatus2.this, "Enter Valid Contact Number", Toast.LENGTH_SHORT).show();
                    et_frag2_checkjobpostedstatus_contactNumber.setBackgroundResource(R.drawable.back_red_round_corners);
                }

                else {

                    UpdateDataMap();
                    SetDatabaseInfo(email, id);
                    SetEditTextsEnabled(false);
                    SetSpinnersEnabled(false);
                    SetToggleButtonsEnabled(false);
                }
            }
        });

        card_cjp_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = EmployerCheckJobsPostedStatus2.this.getSharedPreferences("EmployerCheckJobsPostedStatus", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                finish();
            }
        });

        card_cjp_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerCheckJobsPostedStatus2.this);
                alertDialog.setTitle("Delete Job Post");
                alertDialog.setMessage("Are you sure you want to delete this job?\n"+"WARNING: Deleting the job will delete its Accepted Applicants as well");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = EmployerCheckJobsPostedStatus2.this;
                        if(activity != null) {
                            progressDialog.setMessage(getString(R.string.deletingJob));
                            progressDialog.show();
                        }
                        db.collection("users").document(email).collection("jobsposted").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        Toast.makeText(EmployerCheckJobsPostedStatus2.this, "Job Post Deleted Sucessfully", Toast.LENGTH_SHORT).show();


                                        db.collection("users").document(email).collection("acceptedapplicants")
                                                .whereEqualTo("jobappliedID",id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.w(TAG, "Listen failed.", e);
                                                    return;
                                                }

                                                for (QueryDocumentSnapshot doc : value) {

                                                    String employeeMobile=doc.getData().get("Telephone").toString();
                                                    db.collection("employee").document(employeeMobile).collection("appliedJob")
                                                            .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                    db.collection("users").document(email).collection("acceptedapplicants")
                                                            .document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                    db.collection("users").document(email).collection("viewapplicants")
                                                            .document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                        db.collection("users").document(email).collection("viewapplicants")
                                                .whereEqualTo("jobappliedID",id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.w(TAG, "Listen failed.", e);
                                                    return;
                                                }

                                                for (QueryDocumentSnapshot doc : value) {
                                                    String employeeMobile=doc.getData().get("Telephone").toString();
                                                    db.collection("employee").document(employeeMobile).collection("appliedJob")
                                                            .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                    db.collection("users").document(email).collection("acceptedapplicants")
                                                            .document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                    db.collection("users").document(email).collection("viewapplicants")
                                                            .document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                        //--------------Delete job post from algolia-------------
                                        //deleteFromAlgolia("jobsposted",id);

                                        SharedPreferences.Editor editor = EmployerCheckJobsPostedStatus2.this.getSharedPreferences("EmployerCheckJobsPostedStatus", MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.apply();
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();


            }
        });

    }



    public static ArrayList<String> selectedProfession;
    String professionsForET;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==10 && resultCode==10){
            selectedProfession = data.getStringArrayListExtra("SelectedProfessions");
            professionsForET = "";
            for(int i=0; i<selectedProfession.size(); i++){
                if(i==selectedProfession.size()-1){
                    professionsForET= professionsForET + selectedProfession.get(i);
                }
                else
                    professionsForET = professionsForET + selectedProfession.get(i) + ", ";
            }
            et_skill.setText(professionsForET);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    void SetToggleButtonsEnabled(boolean flag){

        if(flag){
            card_cjp_accept.setVisibility(View.VISIBLE);
            //card_cjp_edit.setVisibility(View.GONE);
        }
        else{
            card_cjp_accept.setVisibility(View.GONE);
            //card_cjp_edit.setVisibility(View.VISIBLE);
        }

        toggleButton_perDay.setEnabled(flag);
        toggleButton_perMonth.setEnabled(flag);
        toggleButton_perYear.setEnabled(flag);
        toggleButton_cvYes.setEnabled(flag);
        //et_skill.setEnabled(flag);
        if(flag)
            selectSkills.setVisibility(View.VISIBLE);
        else
            selectSkills.setVisibility(View.GONE);

        toggleButton_cvNo.setEnabled(flag);
        toggleButton_cvOptional.setEnabled(flag);
    }

    void SetSpinnersEnabled(boolean flag){


        if(flag){
            card_cjp_accept.setVisibility(View.VISIBLE);
           // card_cjp_edit.setVisibility(View.GONE);
        }
        else{
            card_cjp_accept.setVisibility(View.GONE);
           // card_cjp_edit.setVisibility(View.VISIBLE);
        }
        if(flag)
            selectSkills.setVisibility(View.VISIBLE);
        else
            selectSkills.setVisibility(View.GONE);
        spinner_about_state.setEnabled(flag);
        spinner_about_district.setEnabled(flag);
    }


    void SetDatabaseInfo(String email, final String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        datamap.put("SkillArray", selectedProfession);

        db.collection("users").document(email).collection("jobsposted").document(id)
                .set(datamap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        progressDialog.dismiss();
                        Toast.makeText(EmployerCheckJobsPostedStatus2.this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();

                        //----------------------update Algolia-------------------------
                        addToAlgolia(datamap,"jobsposted",id);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(EmployerCheckJobsPostedStatus2.this, "Changes were not saved", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });

    }

    void UpdateDataMap() {

        datamap.put("jobTitle",et_frag2_checkjobpostedstatus_jobTitle.getText().toString());
        datamap.put("jobDescription",et_frag2_checkjobpostedstatus_jobDescription.getText().toString());
        datamap.put("company",et_frag2_checkjobpostedstatus_company.getText().toString());
        datamap.put("hirePosition",et_frag2_checkjobpostedstatus_hirePosition.getText().toString());
        datamap.put("contactNumber",et_frag2_checkjobpostedstatus_contactNumber.getText().toString());
        datamap.put("address",et_frag2_checkjobpostedstatus_Address.getText().toString());
        datamap.put("startingRs",et_frag2_checkjobpostedstatus_startingRs.getText().toString());
        datamap.put("endingRs",et_frag2_checkjobpostedstatus_endingRs.getText().toString());
        datamap.put("Skills",et_skill.getText().toString());

        datamap.put("salaryPeriod", SalaryPeriod[0]);
        datamap.put("CvRequired",CvRequired[0]);


    }

        void SetEditTextsEnabled(boolean flag){

        if(flag){
            card_cjp_accept.setVisibility(View.VISIBLE);
            //card_cjp_edit.setVisibility(View.GONE);
        }
        else{
            card_cjp_accept.setVisibility(View.GONE);
           // card_cjp_edit.setVisibility(View.VISIBLE);
        }

            et_frag2_checkjobpostedstatus_jobTitle.setEnabled(flag);
            et_frag2_checkjobpostedstatus_jobDescription.setEnabled(flag);
            et_frag2_checkjobpostedstatus_company.setEnabled(flag);
            et_frag2_checkjobpostedstatus_hirePosition.setEnabled(flag);
            et_frag2_checkjobpostedstatus_contactNumber.setEnabled(flag);
            et_frag2_checkjobpostedstatus_Address.setEnabled(flag);
            et_frag2_checkjobpostedstatus_startingRs.setEnabled(flag);
            et_frag2_checkjobpostedstatus_endingRs.setEnabled(flag);
        }



        void GetDatabaseInfo(String email, String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference docRef = db.collection("users").document(email)
                .collection("jobsposted").document(id);
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

    void UpdateFields() {
        try {
            et_frag2_checkjobpostedstatus_jobTitle.setText(datamap.get("jobTitle").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_company.setText(datamap.get("company").toString());
        }catch(Exception e){}

        try {
            et_frag2_checkjobpostedstatus_hirePosition.setText(datamap.get("hirePosition").toString());
        }catch(Exception e){}

        try {
            et_frag2_checkjobpostedstatus_hireDone.setText(datamap.get("hireDone").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_contactNumber.setText(datamap.get("contactNumber").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_Address.setText(datamap.get("address").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_startingRs.setText(datamap.get("startingRs").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_endingRs.setText(datamap.get("endingRs").toString());
        }catch(Exception e){}

        try{
        et_frag2_checkjobpostedstatus_jobDescription.setText(datamap.get("jobDescription").toString());
        }catch(Exception e){}

        try{
        et_skill.setText((datamap.get("Skills")).toString());
        }catch(Exception e){
            et_skill.setText((datamap.get("jobCategory")).toString());

        }

        try{
        selectedProfession = (ArrayList<String>) datamap.get("SkillArray");
        }catch(Exception e){}


        if(datamap.get("salaryPeriod").toString().contains("Year")){
            toggleButton_perYear.setChecked(true);
            SalaryPeriod[0] = "per Year";

        }

        else if(datamap.get("salaryPeriod").toString().contains("Day")){
            toggleButton_perDay.setChecked(true);
            SalaryPeriod[0] = "per Day";

        }

        else{
            SalaryPeriod[0] = "per Month";
            toggleButton_perMonth.setChecked(true);
        }

        if(datamap.get("CvRequired").toString().contains("Yes")) {
            CvRequired[0] = "Yes";
            toggleButton_cvYes.setChecked(true);
        }
        else if(datamap.get("CvRequired").toString().contains("No")) {
            CvRequired[0] = "No";
            toggleButton_cvNo.setChecked(true);
        }
        else{
            CvRequired[0] = "Optional";
            toggleButton_cvOptional.setChecked(true);
        }





    }



    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(EmployerCheckJobsPostedStatus2.this,lang);
        Resources resources = context.getResources();



        toggleButton_cvNo.setTextOff(resources.getString(R.string.NoText));
        toggleButton_cvYes.setTextOff(resources.getString(R.string.YesText));
        toggleButton_cvOptional.setTextOff(resources.getString(R.string.OptionalText));
        toggleButton_perDay.setTextOff(resources.getString(R.string.perDay));
        toggleButton_perMonth.setTextOff(resources.getString(R.string.perMonth));
        toggleButton_perYear.setTextOff(resources.getString(R.string.perYear));

        toggleButton_cvNo.setTextOn(resources.getString(R.string.NoText));
        toggleButton_cvYes.setTextOn(resources.getString(R.string.YesText));
        toggleButton_cvOptional.setTextOn(resources.getString(R.string.OptionalText));
        toggleButton_perDay.setTextOn(resources.getString(R.string.perDay));
        toggleButton_perMonth.setTextOn(resources.getString(R.string.perMonth));
        toggleButton_perYear.setTextOn(resources.getString(R.string.perYear));



        btnSelect.setText(resources.getString(R.string.Select));



        //currentHeading.setText(resources.getString(R.string.Job_Details));
        lc_jobtitle.setText(resources.getString(R.string.Job_Title));
        lc_jobdescription3.setText(resources.getString(R.string.Job_Description));
        lc_companyname.setText(resources.getString(R.string.company_Name));
        lc_state3.setText(resources.getString(R.string.State));
        lc_district3.setText(resources.getString(R.string.District));
        titlecjps_skills.setText(resources.getString(R.string.SkillsRequired));
        textView5.setText(resources.getString(R.string.Salary));
        textView6.setText(resources.getString(R.string.SalaryBenefit));
        textView7.setText(resources.getString(R.string.to));
        lc_cvreq.setText(resources.getString(R.string.Receive_CV));
        textView9.setText(resources.getString(R.string.hirePosition));
        lc_phone3.setText(resources.getString(R.string.Contact_Number));
        lc_address3.setText(resources.getString(R.string.Address));
        textView20.setText(resources.getString(R.string.back));
        textView25.setText(resources.getString(R.string.deleteJob));
        textView21.setText(resources.getString(R.string.editDetails));
    }

    void addToAlgolia(Map<String, ?> map, String record, String objectID){

        Client client = new Client(getString(R.string.Algolia_App_ID), getString(R.string.Algolia_Api_Key));

        final Index index = client.getIndex(record);
        final List<JSONObject> array = new ArrayList<JSONObject>();
        try {
            array.add(new JSONObject(map).put("objectID", objectID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        index.addObjectsAsync(new JSONArray(array), null);

    }

    void deleteFromAlgolia(String record,String objectID){
        Client client = new Client(getString(R.string.Algolia_App_ID), getString(R.string.Algolia_Api_Key));
        final Index index = client.getIndex(record);
        index.deleteObjectAsync(objectID, null);
    }

    }