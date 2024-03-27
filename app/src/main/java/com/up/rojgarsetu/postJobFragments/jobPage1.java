package com.up.rojgarsetu.postJobFragments;

import android.animation.ObjectAnimator;
import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;
import static com.up.rojgarsetu.postJobFragments.PostJobActivity.*;

public class jobPage1 extends Fragment {



    TextView lc_gettingstart,tv_jobTiitle,tv_companyName,
            tv_contactName,tv_contactNum,tv_jobdescription,
            currentHeading, tv_contactEmail, tv_continue;

    EditText et_jobDescription, et_jobTitle, et_companyName, et_contactNum,
            et_contactEmail, et_contactName;

    RadioGroup rg_numVisibility;

    FirebaseFirestore firestore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Initialize variable for XML file
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.job_page_1,container,false);

            firestore = FirebaseFirestore.getInstance();

            postJobFragmentName = "jobpage1";


            et_jobDescription = rootView.findViewById(R.id.et_jobDescription);
            et_jobTitle = rootView.findViewById(R.id.et_jobTitle);
            et_companyName = rootView.findViewById(R.id.et_companyName);
            et_contactNum = rootView.findViewById(R.id.et_contactNum);
            et_contactEmail = rootView.findViewById(R.id.et_contactEmail);
            et_contactName = rootView.findViewById(R.id.et_contactName);

            rg_numVisibility = rootView.findViewById(R.id.rg_numVisibility);


            lc_gettingstart = rootView.findViewById(R.id.lc_gettingstart);
            tv_jobTiitle = rootView.findViewById(R.id.tv_jobTitle);
            tv_companyName = rootView.findViewById(R.id.tv_companyName);
            tv_contactName = rootView.findViewById(R.id.tv_contactName);
            tv_contactNum = rootView.findViewById(R.id.tv_contactNum);
            tv_jobdescription = rootView.findViewById(R.id.tv_jobDescription);
            tv_contactEmail = rootView.findViewById(R.id.tv_contactEmail);
            tv_continue = rootView.findViewById(R.id.textView13);


            final CardView card_continue_frag1 = rootView.findViewById(R.id.card_continue_frag1);


            updateView((String) Paper.book().read("language"));

            //-------------Spinner for Location------------------


        /*
            final String[] State = {""};
            final String[] District = {""};
            final ArrayList<String> states = new ArrayList<>();
            states.add(0, "Select");
            final ArrayList<String> total_districtArray = new ArrayList<>();
            total_districtArray.add(0, "Select");
            final ArrayList<String> single_districtArray = new ArrayList<>();
            single_districtArray.add(0, "Select");




                final ArrayAdapter stateAdaptor = new ArrayAdapter(getActivity(), R.layout.spinner_style, states);
                stateAdaptor.setDropDownViewResource(R.layout.spinner_style);
                firestore.document("states/English").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> map = task.getResult().getData();
                        statesMap = map;
                        states.clear();
                        districtString = new String[map.size()];
                        for (String m : map.keySet()) {
                            states.add(m);
                            total_districtArray.add(map.get(m).toString());
                        }
                        Collections.sort(states);
                        states.add(0, "Select");
                        stateAdaptor.notifyDataSetChanged();
                        try {
                            final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
                            spinner_state.setSelection(prefs.getInt("StatePosition", 0));
                        } catch (Exception e) {
                            Log.e("jobPage1", "SpinnerState Error:" + e.getLocalizedMessage());
                        }


                    }
                });

                final ArrayAdapter districtAdaptor = new ArrayAdapter(getActivity(), R.layout.spinner_style, single_districtArray);

                spinner_state.setAdapter(stateAdaptor);
                spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        selectedState = spinner_state.getSelectedItem().toString();
                        spinner_state.setBackgroundResource(R.drawable.back_grey_round_corners);


                        if(et_jobTitle.isFocused()){
                            et_jobTitle.clearFocus();
                        }

                        if(et_jobDescription.isFocused()){
                            et_jobDescription.clearFocus();
                        }


                        if(et_altNum.isFocused()){
                            et_altNum.clearFocus();
                        }


                        if(et_companyName.isFocused()){
                            et_companyName.clearFocus();
                        }


                        if(et_addressLine.isFocused()){
                            et_addressLine.clearFocus();
                        }

                        if(et_contactName.isFocused()){
                            et_contactName.clearFocus();
                        }


                        if(et_contactNum.isFocused()){
                            et_contactNum.clearFocus();
                        }


                        if(et_contactEmail.isFocused()){
                            et_contactEmail.clearFocus();
                        }

                        if (position != 0) {
                            StatePos = position;
                            State[0] = states.get(position);
                                    String districtFetch = statesMap.get(spinner_state.getSelectedItem().toString()).toString();
                                    single_districtArray.clear();
                                    Collections.addAll(single_districtArray, districtFetch.split(","));
                                    Collections.sort(single_districtArray);
                                    single_districtArray.add(0, "Select");

                                    districtAdaptor.notifyDataSetChanged();
                                    try {
                                        final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
                                        spinner_district.setSelection(prefs.getInt("DistrictPosition", 0));
                                    } catch (Exception e) {
                                        Log.e("jobPage1", "DistrictSpinner Error:" + e.getLocalizedMessage());
                                    }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                districtAdaptor.setDropDownViewResource(R.layout.spinner_style);
                spinner_district.setAdapter(districtAdaptor);
                spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_district.setBackgroundResource(R.drawable.back_grey_round_corners);
                        selectedDistrict = spinner_district.getSelectedItem().toString();
                        District[0] = single_districtArray.get(position);
                        DistrictPos = position;

                        if(et_jobTitle.isFocused()){
                            et_jobTitle.clearFocus();
                        }

                        if(et_jobDescription.isFocused()){
                            et_jobDescription.clearFocus();
                        }


                        if(et_altNum.isFocused()){
                            et_altNum.clearFocus();
                        }


                        if(et_companyName.isFocused()){
                            et_companyName.clearFocus();
                        }


                        if(et_addressLine.isFocused()){
                            et_addressLine.clearFocus();
                        }

                        if(et_contactName.isFocused()){
                            et_contactName.clearFocus();
                        }


                        if(et_contactNum.isFocused()){
                            et_contactNum.clearFocus();
                        }


                        if(et_contactEmail.isFocused()){
                            et_contactEmail.clearFocus();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                //----------Spinners for Location End--------------------




//---------------------Getting jobType from Firestore instead of strings.xml------------

                final Integer[] sector = new Integer[1];
                final ArrayList<String> sectorList = new ArrayList<>();
                sectorList.add(0, "Select");
                final ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), R.layout.spinner_style, sectorList);
                adapter2.setDropDownViewResource(R.layout.spinner_style);
                firestore.collection("sector").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null){
                            sectorList.clear();
                            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                                String sectorFromDoc = document.getId();
                                sectorList.add(sectorFromDoc);


                            }

                            Collections.sort(sectorList);
                            sectorList.add(0,"Select");
                            adapter2.notifyDataSetChanged();

                            try {
                                final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
                                if (!prefs.getString("sectorPosition", "").equals(""))
                                    spinner_sector.setSelection(Integer.parseInt(prefs.getString("sectorPosition", "")));
                            } catch (Exception e) {
                                Log.e("Sector", "SectorPrefs Error: " + e.getLocalizedMessage());
                            }


                        }
                    }
                });

                spinner_sector.setAdapter(adapter2);



                spinner_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SectorPosition = position;
                        selectedSector = spinner_sector.getSelectedItem().toString();
                        spinner_sector.setBackgroundResource(R.drawable.back_grey_round_corners);


                        if(et_jobTitle.isFocused()){
                            et_jobTitle.clearFocus();
                        }

                        if(et_jobDescription.isFocused()){
                            et_jobDescription.clearFocus();
                        }


                        if(et_altNum.isFocused()){
                            et_altNum.clearFocus();
                        }


                        if(et_companyName.isFocused()){
                            et_companyName.clearFocus();
                        }


                        if(et_addressLine.isFocused()){
                            et_addressLine.clearFocus();
                        }

                        if(et_contactName.isFocused()){
                            et_contactName.clearFocus();
                        }


                        if(et_contactNum.isFocused()){
                            et_contactNum.clearFocus();
                        }


                        if(et_contactEmail.isFocused()){
                            et_contactEmail.clearFocus();
                        }



                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });




         */

                //-----------Focus Change Listeners to highlight the current Input Field while Entering Data--------

                et_jobTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_jobTitle.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_jobTitle.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });

                et_jobDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_jobDescription.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_jobDescription.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });

                et_companyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_companyName.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_companyName.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });


                et_contactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_contactName.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_contactName.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });

                et_contactNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_contactNum.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_contactNum.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });

                et_contactEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et_contactEmail.setBackgroundResource(R.drawable.border_orange_back_white_round_corners);
                        } else {
                            et_contactEmail.setBackgroundResource(R.drawable.border_grey_back_white_round_corners);
                        }
                    }
                });


                //------------Focus Change Listeners Code End------------------

                rg_numVisibility.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == R.id.radio_callYes){

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
                            editor.putString("contactNumberVisibility", "Number Visible to Job Seekers");
                            editor.apply();

                        }
                        else if(checkedId==R.id.radio_callOnTime){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
                            editor.putString("contactNumberVisibility", "Number Visible Only between 10 AM - 5 PM");
                            editor.apply();

                        }
                        else{
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
                            editor.putString("contactNumberVisibility", "Number Not Visible. Only Email");
                            editor.apply();
                        }

                    }
                });


                try {
                    //Retrieving from SharedPref on back press
                    final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
                    if (!prefs.getString("jobTitle", "").equalsIgnoreCase("")) {
                        et_jobTitle.setText(prefs.getString("jobTitle", ""));
                        et_companyName.setText(prefs.getString("company", ""));
                        et_jobDescription.setText(prefs.getString("jobDescription", ""));
                        et_contactName.setText(prefs.getString("contactPersonName", ""));
                        et_contactNum.setText(prefs.getString("contactNumber", ""));
                        et_contactEmail.setText(prefs.getString("contactEmail", ""));
                        String numVisibility = prefs.getString("contactNumberVisibility","Number Visible Only between 10 AM - 5 PM").toString();
                        switch(numVisibility){
                            case "Number Visible to Job Seekers":
                                rg_numVisibility.check(R.id.radio_callYes);
                                break;
                            case "Number Not Visible. Only Email":
                                rg_numVisibility.check(R.id.radio_onlyEmail);
                                break;
                            default:
                                rg_numVisibility.check(R.id.radio_callOnTime);
                        }
                    }
                }
                catch (Exception e){
                    Log.e("PostJob1","Error getting from Shared Prefs..");
                }


                //Saving in SharedPref

                card_continue_frag1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //------ Tap Effect---------

                        card_continue_frag1.setElevation(0);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                card_continue_frag1.setElevation(5);
                            }
                        }, 200);

                        //------ Tap Effect Ends---------


                        //--------Validation of Input Fields-----------------

                        if (et_jobTitle.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Job Title cannot Be Empty", Toast.LENGTH_SHORT).show();
                            et_jobTitle.setBackgroundResource(R.drawable.back_red_round_corners);
                            return;
                        }

                        if (et_jobDescription.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Job Description cannot Be Empty", Toast.LENGTH_SHORT).show();
                            et_jobDescription.setBackgroundResource(R.drawable.back_red_round_corners);
                            return;
                        }

                        if (et_companyName.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Company cannot Be Empty", Toast.LENGTH_SHORT).show();
                            et_companyName.setBackgroundResource(R.drawable.back_red_round_corners);
                            return;
                        }


try {
    if (et_contactNum.getText().toString().isEmpty() || et_contactNum.getText().toString().length() < 10) {
        Toast.makeText(getActivity(), "Enter Contact Number", Toast.LENGTH_SHORT).show();
        et_contactNum.setBackgroundResource(R.drawable.back_red_round_corners);
        return;
    }
}
catch(Exception e){}
                        if (et_contactEmail.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Enter Contact Email", Toast.LENGTH_SHORT).show();
                            et_contactEmail.setBackgroundResource(R.drawable.back_red_round_corners);
                            return;
                        }
                        else if(!isEmailValid(et_contactEmail.getText().toString())){
                            Toast.makeText(getActivity(), "Enter Valid Email", Toast.LENGTH_SHORT).show();
                            et_contactEmail.setBackgroundResource(R.drawable.back_red_round_corners);
                            return;
                        }



                        //-----------Validation of Input Fields Ends here--------------


                        //---------Traslation Animation on Continue---------

                        ScrollView scrollView_frag1 = rootView.findViewById(R.id.scrollView_frag1);

                        int y = scrollView_frag1.getWidth();
                        int x = card_continue_frag1.getWidth();
                        float w = y - x - (scrollView_frag1.getPaddingRight() + scrollView_frag1.getPaddingLeft());

                        ObjectAnimator animator = ObjectAnimator.ofFloat(card_continue_frag1, "translationX", w / 2);
                        animator.setDuration(400);
                        animator.start();

                        animator = ObjectAnimator.ofFloat(scrollView_frag1, "translationX", -1800);
                        animator.setDuration(400);
                        animator.start();


                        //-------Translation Animation Code Ends-----------





                        //------- After animation, actual action to be performed-------

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                try {


                                    //------------Writing data to Shared Prefs----------------------

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();

                                    editor.putString("jobTitle", et_jobTitle.getText().toString().replaceAll(" +", " "));
                                    editor.putString("jobDescription", et_jobDescription.getText().toString());
                                    editor.putString("company", et_companyName.getText().toString().replaceAll(" +", " "));
                                    editor.putString("contactPersonName", et_contactName.getText().toString());
                                    editor.putString("contactNumber", et_contactNum.getText().toString());
                                    editor.putString("contactEmail", et_contactEmail.getText().toString());

                                    editor.apply();

                                    //----------------Writing to sharedPrefs Ends------------------

                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fl_postjob, new jobPage2());
                                    ft.commit();
                                }
                                catch(Exception e){
                                    ScrollView scrollView_frag1 = rootView.findViewById(R.id.scrollView_frag1);

                                    int y = scrollView_frag1.getWidth();
                                    int x = card_continue_frag1.getWidth();
                                    float w = y - x - (scrollView_frag1.getPaddingRight() + scrollView_frag1.getPaddingLeft());

                                    ObjectAnimator animator = ObjectAnimator.ofFloat(card_continue_frag1, "translationX", 0);
                                    animator.setDuration(400);
                                    animator.start();

                                    animator = ObjectAnimator.ofFloat(scrollView_frag1, "translationX", 0);
                                    animator.setDuration(400);
                                    animator.start();
                                }
                            }

                        }, 400);


                        // ------- Actual Action on Continue Code Ends----------


                    }
                });





        return rootView;
    }




    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();




        lc_gettingstart.setText(resources.getString(R.string.Getting_Started));
        tv_jobTiitle.setText(resources.getString(R.string.Job_Title));
        tv_jobdescription.setText(resources.getString(R.string.Job_Description));
        tv_companyName.setText(resources.getString(R.string.companyName));
        tv_contactName.setText(resources.getString(R.string.contactPersonName));
        tv_contactNum.setText(resources.getString(R.string.contactNum));
        tv_contactEmail.setText(resources.getString(R.string.contactEmail));
        tv_continue.setText(resources.getString(R.string.Continue));


    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
