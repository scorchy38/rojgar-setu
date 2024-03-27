package com.up.rojgarsetu.postJobFragments;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.paperdb.Paper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.up.rojgarsetu.Employee.CheckJob.EmployeeCheckJob;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.ProfessionActivity;
import com.up.rojgarsetu.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;
import static com.up.rojgarsetu.postJobFragments.PostJobActivity.postJobFragmentName;

public class jobPage3 extends Fragment {


    TextView tv_jobDetails, tv_skills, tv_qualification, tv_salaryRange, tv_salaryBenefit,
    tv_cveq, tv_jobType, tv_hirePosition, tv_selectSkills, tv_continue, tv_back, tv_preferredCourse;

    EditText et_skillReq, et_minSalary, et_maxSalary, et_hirePosition, et_preferredCourse;

    CardView card_selectSkills;
    ToggleButton toggleButton_cvYes, toggleButton_cvNo, toggleButton_cvOptional;
    ToggleButton toggleButton_perDay, toggleButton_perMonth,toggleButton_perYear;

    RadioGroup rg_qualification, rg_salaryOption, rg_minExpReq;


    FirebaseFirestore firestore;

    Map<String, Object> categoryImageMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.job_page_3,container,false);

        postJobFragmentName = "jobpage3";
        firestore = FirebaseFirestore.getInstance();

        toggleButton_cvYes = rootView.findViewById(R.id.toggleButton_cvYes);
        toggleButton_cvNo = rootView.findViewById(R.id.toggleButton_cvNo);
        toggleButton_cvOptional = rootView.findViewById(R.id.toggleButton_cvOptional);
        final CardView card_back_frag3_job = rootView.findViewById(R.id.card_back_frag3_job);
        final CardView card_continue_frag3_job = rootView.findViewById(R.id.card_continue_frag3_job);

        toggleButton_perDay = rootView.findViewById(R.id.toggleButton_perDay);
        toggleButton_perMonth = rootView.findViewById(R.id.toggleButton_perMonth);
        toggleButton_perYear = rootView.findViewById(R.id.toggleButton_perYear);

        tv_jobDetails = rootView.findViewById(R.id.tv_jobDetails);
        tv_skills = rootView.findViewById(R.id.tv_skills);
        tv_qualification = rootView.findViewById(R.id.tv_qualification);
        tv_salaryRange = rootView.findViewById(R.id.tv_salaryRange);
        tv_salaryBenefit = rootView.findViewById(R.id.tv_salaryBenefit);
        tv_cveq= rootView.findViewById(R.id.tv_cvReq);
        tv_hirePosition = rootView.findViewById(R.id.tv_hirePosition);
        tv_selectSkills = rootView.findViewById(R.id.tv_selectSkills);
        tv_continue = rootView.findViewById(R.id.tv_continue);
        tv_back = rootView.findViewById(R.id.tv_back);
        tv_preferredCourse = rootView.findViewById(R.id.tv_preferredCourse);

        et_skillReq = rootView.findViewById(R.id.et_skillReq);
        et_minSalary = rootView.findViewById(R.id.et_minSalary);
        et_maxSalary = rootView.findViewById(R.id.et_maxSalary);
        et_hirePosition = rootView.findViewById(R.id.et_hirePosition);
        et_preferredCourse = rootView.findViewById(R.id.et_preferredCourse);

        rg_salaryOption = rootView.findViewById(R.id.rg_salary);
        rg_minExpReq = rootView.findViewById(R.id.rg_minExpReq);
        rg_qualification = rootView.findViewById(R.id.rg_qualification);

        card_selectSkills = rootView.findViewById(R.id.card_selectSkills);

        categoryImageMap = new HashMap<>();
        categoryImageMap.put("Warehouse/ Logistics", R.drawable.jc_warehouse);
        categoryImageMap.put("Manufacturing/ Operators", R.drawable.jc_operators);
        categoryImageMap.put("Housekeeping/ Helper/ Peon", R.drawable.jc_helper);
        categoryImageMap.put("Security Guard", R.drawable.jc_security);
        categoryImageMap.put("Delivery/ Collection Boy", R.drawable.jc_delivery);
        categoryImageMap.put("Driver", R.drawable.jc_driver);
        categoryImageMap.put("Cook/ Chef/ Waiter", R.drawable.jc_cook);
        categoryImageMap.put("Nurse/ Compounder/ Ward Boy", R.drawable.jc_nurse);
        categoryImageMap.put("Pharmacist/ MR", R.drawable.jc_pharmacist);
        categoryImageMap.put("Office Assistant/ Data Entry/ Receptionist", R.drawable.jc_officeassistant);
        categoryImageMap.put("BPO/ Customer Support/ Telemarketing", R.drawable.jc_telecaller);
        categoryImageMap.put("Sales/ Field Sales", R.drawable.jc_sales);
        categoryImageMap.put("Marketing Executives/ Business Development", R.drawable.jc_marketing);
        categoryImageMap.put("Account/ Cashier", R.drawable.jc_accounts);
        categoryImageMap.put("Teacher/ Tutor/ Trainer", R.drawable.jc_teacher);
        categoryImageMap.put("IT & Hardware Support / Engineering", R.drawable.jc_it);
        categoryImageMap.put("Electrician/ Plumber/ Carpenter", R.drawable.jc_labour);
        categoryImageMap.put("Tailors", R.drawable.jc_tailors);

        updateView((String) Paper.book().read("language"));

        rg_salaryOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_salary1){
                    LinearLayout ll_1, ll_2;
                    ll_1 = rootView.findViewById(R.id.ll_salaryRange);
                    ll_2 = rootView.findViewById(R.id.ll_salaryPeriod);
                    ll_1.setVisibility(View.GONE);
                    ll_2.setVisibility(View.GONE);
                }
                else{
                    LinearLayout ll_1, ll_2;
                    ll_1 = rootView.findViewById(R.id.ll_salaryRange);
                    ll_2 = rootView.findViewById(R.id.ll_salaryPeriod);
                    ll_1.setVisibility(View.VISIBLE);
                    ll_2.setVisibility(View.VISIBLE);
                }
            }
        });

        /*final Integer[] jobType = new Integer[1];
        final ArrayList<String> jobTypeList = new ArrayList<>();
        jobTypeList.add(0, "Select");
        final ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), R.layout.spinner_style, jobTypeList);
        adapter2.setDropDownViewResource(R.layout.spinner_style);
        firestore.document("JobType/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, ?> map = documentSnapshot.getData();
                jobTypeList.clear();
                for (String m : map.keySet()) {
                    jobTypeList.add(m);
                }
                Collections.sort(jobTypeList);
                jobTypeList.add(0, "Select");
                adapter2.notifyDataSetChanged();

                try {
                    final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
                    if (!prefs.getString("jobType", "").equals(""))
                        spinner_jobType.setSelection(Integer.parseInt(prefs.getString("jobType", "")));
                }
                catch(Exception e){
                    Log.e("jobPage1", "JobTypePrefs Error: "+e.getLocalizedMessage());
                }

            }
        });

         */

/*
        spinner_jobType.setAdapter(adapter2);

        spinner_jobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jobType[0] = position;
                spinner_jobType.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

 */


    //-----------Focus Change Listeners to highlight the current Input Field while Entering Data--------


        et_minSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_minSalary.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                } else {
                    et_minSalary.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                }
            }
        });

        et_maxSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_maxSalary.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                } else {
                    et_maxSalary.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                }
            }
        });


        et_hirePosition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_hirePosition.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                } else {
                    et_hirePosition.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                }
            }
        });

        et_preferredCourse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_preferredCourse.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                } else {
                    et_preferredCourse.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                }
            }
        });



    //------------Focus Change Listeners Code End------------------


    //--------Initialising Toggle Buttons----------------
        final String[] CvRequired = new String[1];

        toggleButton_cvYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                if (isChecked) {
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
                if (isChecked) {
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

        toggleButton_cvOptional.setChecked(true);


        //--------Initialising Toggle Buttons Code Ends ---------------




        //---------Toggle Button for Salary Period initialisation as a MultiStateButton--------------

        final String[] SalaryPeriod = new String[1];

        toggleButton_perDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SalaryPeriod[0] = "per Day";
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
                if (isChecked) {
                    SalaryPeriod[0] = "per Month";
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
                if (isChecked) {
                    SalaryPeriod[0] = "per Year";
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

        toggleButton_perDay.setChecked(true); // Per Day Button Set as default Checked Button


        //-----------Toggle Button Initialisation Ends -------------------



        card_selectSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeMoreCategories();
            }
        });





        //Retrieving from SharedPref on back press
        final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
        if (!prefs.getString("hirePosition", "").equalsIgnoreCase("")) {
            et_hirePosition.setText(prefs.getString("hirePosition", ""));




            if (prefs.getString("CvRequired", "").contains("Yes")) {
                toggleButton_cvYes.setChecked(true);
            } else if (prefs.getString("CvRequired", "").contains("No"))
                toggleButton_cvNo.setChecked(true);
            else
                toggleButton_cvOptional.setChecked(true);

            CvRequired[0] = prefs.getString("CvRequired", "");




            try {
                Set<String> skillsetfromPrefs = prefs.getStringSet("SkillArray", null);
                selectedProfession = new ArrayList<>(skillsetfromPrefs);

                String skills = "";
                for(int i=0; i<selectedProfession.size(); i++){
                    if(i==selectedProfession.size()-1){
                        skills= skills + selectedProfession.get(i);
                    }
                    else
                        skills = skills + selectedProfession.get(i) + ", ";
                }
                et_skillReq.setText(skills);


                Log.e("SkillsfromSharedPrefs", "Skills: " + prefs.getStringSet("SkillArray", null).toString());
            }
            catch(Exception e1){}




            try{
                String salaryOption = prefs.getString("salaryOption", "As per Industry Norms");
                if(salaryOption.equals("As per Industry Norms")){
                    rg_salaryOption.check(R.id.rb_salary1);
                }
                else{
                    rg_salaryOption.check(R.id.rb_salary2);
                    String minSalary = prefs.getString("startingRs", "0");
                    String maxSalary = prefs.getString("endingRs","0");
                    String salaryPeriod = prefs.getString("salaryPeriod", "Day");

                    et_minSalary.setText(minSalary);
                    et_maxSalary.setText(maxSalary);
                    if(salaryPeriod.contains("Year")){
                        toggleButton_perYear.setChecked(true);
                    }
                    else if(salaryPeriod.contains("Day")){
                        toggleButton_perDay.setChecked(true);
                    }
                    else{
                        toggleButton_perMonth.setChecked(true);
                    }
                    SalaryPeriod[0] = salaryPeriod;

                }

            }
            catch (Exception e){}



            /*
            et_maxSalary.setText(prefs.getString("endingRs", ""));
            if (prefs.getString("salaryPeriod", "").contains("Year"))
                toggleButton_perYear.setChecked(true);
            else if (prefs.getString("salaryPeriod", "").contains("Day"))
                toggleButton_perDay.setChecked(true);
            else
                toggleButton_perMonth.setChecked(true);
            SalaryPeriod[0] = prefs.getString("salaryPeriod", "");
            //CVToggle.setValue(prefs.getInt("CVPosition",0));

                */


            try {
                String qualification = prefs.getString("qualifications", "12th Passed");
                switch (qualification) {
                    case "10th or Below":
                        rg_qualification.check(R.id.rb_q1);
                        break;
                    case "12th Passed":
                        rg_qualification.check(R.id.rb_q2);
                        break;
                    case "ITI/Diploma":
                        rg_qualification.check(R.id.rb_q3);
                        break;
                    case "Graduation":
                        rg_qualification.check(R.id.rb_q4);
                        break;
                    case "Post Graduation":
                        rg_qualification.check(R.id.rb_q5);
                        break;

                }
            }
            catch(Exception e){}

            try {
                String minExpReq = prefs.getString("minExpReq", "1-2 Years");
                switch (minExpReq) {
                    case "Fresher":
                        rg_minExpReq.check(R.id.rb_minExp1);
                        break;
                    case "3-5 Years":
                        rg_minExpReq.check(R.id.rb_minExp3);
                        break;
                    case "5-8 Years":
                        rg_minExpReq.check(R.id.rb_minExp4);
                        break;
                    case "More than 8 Years":
                        rg_minExpReq.check(R.id.rb_minExp5);
                        break;
                    default:
                        rg_minExpReq.check(R.id.rb_minExp2);

                }
            }
            catch(Exception e){}


        }




        //Saving in SharedPref

        card_continue_frag3_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------Press Effect--------

                card_continue_frag3_job.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_continue_frag3_job.setElevation(5);
                    }
                }, 200);

                //------Press Effect Ends--------



                if (et_skillReq.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please Select Job Category", Toast.LENGTH_SHORT).show();
                    et_skillReq.setBackgroundResource(R.drawable.back_red_round_corners);
                    return;
                }

                int qualificationCheckedId = rg_qualification.getCheckedRadioButtonId();
                String qualification = "";

                switch(qualificationCheckedId){
                    case R.id.rb_q1:
                        qualification ="10th or Below";
                        break;
                    case R.id.rb_q2:
                        qualification ="12th Passed";
                        break;
                    case R.id.rb_q3:
                        qualification ="ITI/Diploma";
                        break;
                    case R.id.rb_q4:
                        qualification ="Graduation";
                        break;
                    case R.id.rb_q5:
                        qualification ="Post-Graduation";
                        break;
                }



                String SalaryText= "", salaryOption = "";
                int checkedRadioButton = rg_salaryOption.getCheckedRadioButtonId();
                if(checkedRadioButton==R.id.rb_salary1){
                    SalaryText = "As per Industry Norms";
                    salaryOption = "As per Industry Norms";
                }
                else{
                    salaryOption = "Specific Salary";

                    int StartingSalary, EndingSalary;
                    try {
                         StartingSalary = Integer.parseInt(et_minSalary.getText().toString());
                    }catch(Exception e){
                        StartingSalary = 0;
                    }

                    try {
                        EndingSalary = Integer.parseInt(et_maxSalary.getText().toString());
                    }catch(Exception e){
                        EndingSalary = 0;
                    }



                    if (et_minSalary.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter the Min. Salary", Toast.LENGTH_SHORT).show();
                        et_minSalary.setBackgroundResource(R.drawable.back_red_round_corners);
                        return;

                    }  else if (et_maxSalary.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), "Please Enter the Max.Salary", Toast.LENGTH_SHORT).show();
                        et_maxSalary.setBackgroundResource(R.drawable.back_red_round_corners);
                        return;
                    }  else if (EndingSalary < StartingSalary) {
                        Toast.makeText(getActivity(), "Max. Salary should be higher than Min. Salary", Toast.LENGTH_SHORT).show();
                        et_minSalary.setBackgroundResource(R.drawable.back_red_round_corners);
                        et_maxSalary.setBackgroundResource(R.drawable.back_red_round_corners);
                        return;

                    }
                    else{
                        SalaryText = StartingSalary + " to " + EndingSalary + " : " + SalaryPeriod[0];
                    }

                }


                int minExpId = rg_minExpReq.getCheckedRadioButtonId();
                RadioButton rbExp = rootView.findViewById(minExpId);
                String minExpReq = "";
                switch(minExpId){
                    case R.id.rb_minExp1:
                        minExpReq = "Fresher";
                        break;
                    case R.id.rb_minExp2:
                        minExpReq = "1-2 Years";
                        break;
                    case R.id.rb_minExp3:
                        minExpReq = "3-5 Years";
                        break;
                    case R.id.rb_minExp4:
                        minExpReq = "5-8 Years";
                        break;
                    case R.id.rb_minExp5:
                        minExpReq = "More than 8 Years";
                        break;
                }




                if(et_hirePosition.getText().toString().length() < 1 || et_hirePosition.getText().toString().length() > 3 ) {
                    Toast.makeText(getActivity(), "No. Of Vacancies must be between 0 to 1000", Toast.LENGTH_SHORT).show();
                    et_hirePosition.setBackgroundResource(R.drawable.back_red_round_corners);
                    return;
                }

                final String preferredCourse = et_preferredCourse.getText().toString();


                    //-----Translation Animation -------------
                    final ScrollView scrollView_frag3 = rootView.findViewById(R.id.scrollView_frag3);

                    ObjectAnimator animator = ObjectAnimator.ofFloat(scrollView_frag3, "translationX", -1800);
                    animator.setDuration(400);
                    animator.start();

                    //------Translation Animation Ends here ---------


                    //------Actual Task to be done on Clicking Continue after animation-------
                final String finalSalaryOption = salaryOption;
                final String finalSalaryText = SalaryText;
                final String finalQualification = qualification;
                final String finalMinExpReq = minExpReq;
                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                SharedPreferences.Editor editor = prefs.edit();


                                editor.putString("Skills", et_skillReq.getText().toString());
                                editor.putString("qualifications", finalQualification);
                                editor.putString("jobCategory", jobCategory);
                                editor.putString("preferredCourse", preferredCourse);
                                editor.putString("salaryOption", finalSalaryOption);
                                if (finalSalaryOption.equals("As per Industry Norms")) {
                                    editor.putString("salaryText", finalSalaryOption);
                                    editor.putString("startingRs", "---");
                                    editor.putString("endingRs", "---");
                                    editor.putString("salaryPeriod", "---");
                                } else {
                                    editor.putString("startingRs", et_minSalary.getText().toString());
                                    editor.putString("endingRs", et_maxSalary.getText().toString());
                                    editor.putString("salaryPeriod", SalaryPeriod[0]);
                                    editor.putString("salaryText", finalSalaryText);
                                }

                                editor.putString("minExpReq", finalMinExpReq);

                                editor.putString("CvRequired", CvRequired[0]);
                                editor.putString("hirePosition", et_hirePosition.getText().toString());
                                //Set<String> professionSet = new HashSet<>(selectedProfession);
                                //editor.putStringSet("SkillArray", professionSet);

                                editor.apply();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fl_postjob, new jobPage4());
                                ft.commit();
                            }
                            catch(Exception e){
                                ObjectAnimator animator = ObjectAnimator.ofFloat(scrollView_frag3, "translationX", 0);
                                animator.setDuration(400);
                                animator.start();

                            }

                        }
                    }, 400);


                    //--------- Task code ends here -------

            }
        });

        card_back_frag3_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------Press Effect--------

                card_back_frag3_job.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_back_frag3_job.setElevation(5);
                    }
                }, 200);

                //------Press Effect Ends--------


                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_postjob, new jobPage2());
                ft.commit();
            }
        });

        return rootView;
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
            et_skillReq.setText(professionsForET);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();


        try {

            toggleButton_cvNo.setTextOff(resources.getString(R.string.NoText));
            toggleButton_cvYes.setTextOff(resources.getString(R.string.YesText));
            toggleButton_cvOptional.setTextOff(resources.getString(R.string.OptionalText));

            toggleButton_perDay.setTextOn(resources.getString(R.string.perDay));
            toggleButton_perMonth.setTextOn(resources.getString(R.string.perMonth));
            toggleButton_perYear.setTextOn(resources.getString(R.string.perYear));

            toggleButton_perDay.setTextOff(resources.getString(R.string.perDay));
            toggleButton_perMonth.setTextOff(resources.getString(R.string.perMonth));
            toggleButton_perYear.setTextOff(resources.getString(R.string.perYear));


            toggleButton_cvNo.setTextOn(resources.getString(R.string.NoText));
            toggleButton_cvYes.setTextOn(resources.getString(R.string.YesText));
            toggleButton_cvOptional.setTextOn(resources.getString(R.string.OptionalText));


            tv_selectSkills.setText(resources.getString(R.string.Select));
            tv_skills.setText("Job Category");
            tv_jobDetails.setText(resources.getString(R.string.Job_Details));
            tv_cveq.setText(resources.getString(R.string.Receive_CV));
            tv_qualification.setText(resources.getString(R.string.qualifications));
            tv_salaryRange.setText(resources.getString(R.string.Salary));
            tv_salaryBenefit.setText(resources.getString(R.string.SalaryBenefit));
            tv_jobType.setText(resources.getString(R.string.jobType));
            tv_hirePosition.setText(resources.getString(R.string.hirePosition));

            tv_continue.setText(resources.getString(R.string.Continue));
            tv_back.setText(resources.getString(R.string.back));
        }
        catch(Exception e){}
    }

    String jobCategory = null;

    public void seeMoreCategories(){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.skill_selector_main,null,false);
        final LinearLayout skills_ll = view.findViewById(R.id.skill_ll);
        skills_ll.removeAllViews();


        final AlertDialog alertDialog = alertBuilder.create();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("JobCategories/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> professionslist = (ArrayList<String>) documentSnapshot.get("jobCategories");
                for(final String professionItem : professionslist) {
                    LayoutInflater inflater2 = LayoutInflater.from(getActivity());
                    final View view2 = inflater2.inflate(R.layout.skill_selector_view, null, false);
                    CardView card_skill_item = view2.findViewById(R.id.card_skill);
                    TextView tv_skill_item = view2.findViewById(R.id.tv_skill);
                    tv_skill_item.setText(professionItem);

                    try {

                        ImageView imv_skill = view2.findViewById(R.id.imv_skill);
                        imv_skill.setImageResource((Integer) categoryImageMap.get(professionItem));

                    }catch(Exception e){}

                    card_skill_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jobCategory = professionItem;
                            et_skillReq.setText(jobCategory);
                            et_skillReq.setVisibility(View.VISIBLE);
                            alertDialog.dismiss();

                        }
                    });
                    skills_ll.addView(view2);
                }
            }
        });
        alertDialog.setView(view);
        alertDialog.show();



    }


}
