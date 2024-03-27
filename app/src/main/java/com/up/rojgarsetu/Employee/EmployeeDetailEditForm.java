package com.up.rojgarsetu.Employee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.Login.EmployeeLoginActivity;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.ProfessionActivity;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.*;
public class EmployeeDetailEditForm extends Fragment {
    //Initialize Variable Global
    Map<String,Object> datamap=new HashMap<>();
    EditText et_name;
    EditText et_address;
    EditText et_work_experience;
    EditText et_skill;
    Spinner spinner_establishment_category;
    Spinner spinner_establishment_sector;
    Spinner spinner_establishment_subsector;
    Spinner spinner_establishment_district;
    Spinner spinner_establishment_state;
    String telephone;
    CardView card_edef_edit,card_edef_accept,card_delete;

    TextView lc_employeereg2,lc_name4,lc_address9,lc_skill4,lc_category6,
            lc_sector6,lc_subsector6,lc_state9,lc_district9,
            lc_workex4,textView18;
    CardView selectSkills;

    ProgressDialog progressDialog;

    RadioGroup rg_qualification, rg_minExp, rg_gender;
    ImageView imageView;

    RadioButton rb_female,rb_male,rb_other;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.activity_employee_detail_edit_form, container, false);
        //getSupportActionBar().hide();
        //Assigning variable
        Activity activity2 = getActivity();
        if(activity2 != null && isAdded()) {
        Fragment_Name= getString(R.string.editDetails);
        currentHeading.setText(Fragment_Name);
        }

        rb_female  = rootView.findViewById(R.id.rb_female);
        rb_male = rootView.findViewById(R.id.rb_male);
        rb_other = rootView.findViewById(R.id.rb_other);
        et_name=rootView.findViewById(R.id.et_name);
        et_address=rootView.findViewById(R.id.et_address);
        et_skill = rootView.findViewById(R.id.et_skill);
      //  spinner_establishment_category=rootView.findViewById(R.id.spinner_edit_establishment_category);

        spinner_establishment_district=rootView.findViewById(R.id.spinner_district);
        spinner_establishment_state=rootView.findViewById(R.id.spinner_state);
        progressDialog = new ProgressDialog(getActivity());
        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            progressDialog.setMessage(getString(R.string.changingDetails));
        }
        card_edef_edit = rootView.findViewById(R.id.card_edef_edit);
        card_edef_accept = rootView.findViewById(R.id.card_frag3_accept);
        card_delete = rootView.findViewById(R.id.card_delete_acc);
        selectedSkills = new ArrayList<>();

        selectSkills = rootView.findViewById(R.id.select_skills);

        rg_qualification = rootView.findViewById(R.id.rg_qualification);
        rg_minExp = rootView.findViewById(R.id.rg_minExpReq);
        rg_gender = rootView.findViewById(R.id.rg_gender);
        imageView = rootView.findViewById(R.id.imv_profilePic);




        lc_employeereg2=rootView.findViewById(R.id.lc_employeereg);
        lc_name4=rootView.findViewById(R.id.lc_name3);
        lc_address9=rootView.findViewById(R.id.lc_address6);
        lc_skill4=rootView.findViewById(R.id.lc_skill3);
    //    lc_category6=rootView.findViewById(R.id.lc_category6);
        lc_state9=rootView.findViewById(R.id.lc_state7);
        lc_district9=rootView.findViewById(R.id.lc_district7);
        lc_workex4=rootView.findViewById(R.id.lc_workex3);
        textView18=rootView.findViewById(R.id.textView18);




        updateView((String)Paper.book().read("language"));


        //Set Enable False
        switchView(false);

        //Fetch Phone Number
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        telephone = firebaseUser.getPhoneNumber();

        //Change Language
        Paper.init(getActivity());
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));


        //focus set
        et_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_address.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_address.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_name.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_name.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });


        et_skill.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_skill.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_skill.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        //Firebase initialize
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        //Fetching data from firestore
        final DocumentReference docRef = db.collection("employee").document(telephone);
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
                    fillDetail();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        //set Enable true (Edit Button)
        //final Button card_edit = rootView.findViewById(R.id.EditData);
        card_edef_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView(true);
            }
        });



        selectSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInterestedJobCategories();
            }
        });






        card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount(v);
            }
        });


        //Save button to store data in firestore
        //final Button card_accept = rootView.findViewById(R.id.SaveData);
        card_edef_accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //------------Tap Effect-------------
                card_edef_accept.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_edef_accept.setElevation(5);
                    }
                },200);

                //-----------Tap Effect Ends--------

                progressDialog.show();
                try {
                    if (et_name.getText().toString().length() == 0 ||
                            et_address.getText().toString().length() == 0 ||
                            et_skill.getText().toString().length() == 0 ||

                            spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select") ||
                            spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select")) {
                        Toast.makeText(getActivity(), "All Fields marked (*) should be filled", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        if (et_name.getText().toString().length() == 0) {
                            et_name.setBackgroundResource(R.drawable.back_red_round_corners);
                        }
                        if (et_address.getText().toString().length() == 0) {
                            et_address.setBackgroundResource(R.drawable.back_red_round_corners);
                        }

                        if (et_skill.getText().toString().length() == 0) {
                            et_skill.setBackgroundResource(R.drawable.back_red_round_corners);
                        }


                        if (spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select")) {
                            spinner_establishment_state.setBackgroundResource(R.drawable.back_red_round_corners);
                        }
                        if (spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select")) {
                            spinner_establishment_district.setBackgroundResource(R.drawable.back_red_round_corners);
                        }

                    } else {

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE).edit();

                        editor.putString("Establishment State", spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString());
                        editor.putString("Establishment State_pos", spinner_establishment_state.getSelectedItemPosition() + "");

                        editor.putString("Establishment District", spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString());
                        editor.putString("Establishment District_pos", spinner_establishment_district.getSelectedItemPosition() + "");

                        editor.putString("Name", et_name.getText().toString());
                        editor.putString("Address", et_address.getText().toString());
                        editor.putString("Telephone", telephone);

                        editor.apply();
                        OneSignal.sendTag("District", spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString());

                        updateCloudDatabase(telephone);
                    }
                }
                catch(Exception e){}


            }
        });

        return rootView;


    }

    //Updating data in Firestore
    //    ProgressDialog progressBar = new ProgressDialog(this);
    void updateCloudDatabase(final String telephone){
        //    progressBar.setMessage("Writing Data...");
        //    progressBar.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        SharedPreferences prefs = getActivity().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE);
        Map<String, Object> userMap = (Map<String, Object>) prefs.getAll();
        userMap.put("jobCategoryPreference", jobCategoryPreference);

        int qualificationCheckedId = rg_qualification.getCheckedRadioButtonId();


        String qualification = "12th Passed";

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

        userMap.put("qualifications", qualification);


        int genderCheckedId = rg_gender.getCheckedRadioButtonId();


        String gender = "Female";

        switch (genderCheckedId){
            case R.id.rb_female:
                gender = "Female";
                break;
            case R.id.rb_male:
                gender = "Male";
                break;
            case R.id.rb_other:
                gender = "Other";
                break;
        }

        userMap.put("gender", gender);


        int minExp = rg_minExp.getCheckedRadioButtonId();


        String minimumExperience = "Fresher";

        switch (minExp){
            case R.id.rb_minExp1:
                minimumExperience = "Fresher";
                break;
            case R.id.rb_minExp2:
                minimumExperience = "1-2 Years";
                break;
            case R.id.rb_minExp3:
                minimumExperience = "3-5 Years";
                break;
            case R.id.rb_minExp4:
                minimumExperience = "5-8 Years";
                break;
            case R.id.rb_minExp5:
                minimumExperience = "More than 8 Years";
                break;
        }

        userMap.put("minimumExperience", minimumExperience);







        final Activity thisActivity = getActivity();

        db.collection("employee").document(telephone)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        SharedPreferences.Editor editor = thisActivity.getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        progressDialog.dismiss();
                        switchView(false);
                        /*Intent intent=new Intent(getActivity(), EmployeeMainActivity.class);
                        startActivity(intent);
                        finish();

                         */



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        progressDialog.dismiss();

                    }
                });


    }

    ArrayList<String> jobCategoryPreference;
    String qualification, minExp, gender;
    //Filing saved data from firestore
    void fillDetail(){
        try {
            final long OO = 1024 * 1024;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child("Employee/" + telephone).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    ImageView dp = imageView;
                    Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    dp.setImageBitmap(bit);
                    //System.out.println("Online se hua READ ");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
                            //   Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
        }




        if(datamap.containsKey("Name")) {
            et_name.setText(datamap.get("Name").toString());
            et_address.setText(datamap.get("Address").toString());
            try{
                jobCategoryPreference = (ArrayList<String>) datamap.get("jobCategoryPreference");
                String st_category = "";
                for(String category : jobCategoryPreference){
                    st_category = st_category + category + "\n";
                }
                et_skill.setText(st_category);
                et_skill.setVisibility(View.VISIBLE);
            }catch(Exception e){
                et_skill.setText("Please select Interested Job categories");
                et_skill.setVisibility(View.VISIBLE);
            }
            try{
                gender = datamap.get("gender").toString();
                if(gender.equals("Male")){
                    rg_gender.check(R.id.rb_male);
                }
                else if(gender.equals("Female")){
                    rg_gender.check(R.id.rb_female);
                }
                else {
                    rg_gender.check(R.id.rb_other);
                }

            }
            catch(Exception e){

            }

            try{
                minExp = datamap.get("minimumExperience").toString();
                if(minExp.equals("Fresher")){
                    rg_minExp.check(R.id.rb_minExp1);
                }
                else if(minExp.equals("1-2 Years")) {
                    rg_minExp.check(R.id.rb_minExp2);
                }
                else if(minExp.equals("3-5 Years")) {
                    rg_minExp.check(R.id.rb_minExp3);
                }
                else if(minExp.equals("5-8 Years")) {
                    rg_minExp.check(R.id.rb_minExp4);
                }
                else {
                    rg_minExp.check(R.id.rb_minExp5);
                }


            }
            catch(Exception e){

            }

            try{
                qualification = datamap.get("qualification").toString();
                if(qualification.equals("10th or Below")){
                    rg_qualification.check(R.id.rb_q1);
                }
                else if(qualification.equals("12th Passed")){
                    rg_qualification.check(R.id.rb_q2);
                }
                else if(qualification.equals("ITI/Diploma")){
                    rg_qualification.check(R.id.rb_q3);
                }
                else if(qualification.equals("Graduation")){
                    rg_qualification.check(R.id.rb_q4);
                }
                else if(qualification.equals("Post-Graduation")){
                    rg_qualification.check(R.id.rb_q5);
                }


            }
            catch(Exception e){

            }



            fillSpinner();
        }
    }

    //Set Enable true or false
    void switchView(boolean set){
        if(set){
            card_edef_accept.setVisibility(View.VISIBLE);
            card_edef_edit.setVisibility(View.GONE);
        }
        else{
            card_edef_accept.setVisibility(View.GONE);
            card_edef_edit.setVisibility(View.VISIBLE);
        }


        et_name.setEnabled(set);
        et_address.setEnabled(set);

        if(set)
            selectSkills.setVisibility(View.VISIBLE);
        else
            selectSkills.setVisibility(View.GONE);

        //spinner_establishment_category.setEnabled(set);
        spinner_establishment_district.setEnabled(set);
        spinner_establishment_state.setEnabled(set);

    }


    void fillSpinner() {

        try{
        //Firebase initialize
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


            //State Spinner
            final ArrayList<String> states = new ArrayList<>();
            states.add(0, "Select");
            final ArrayAdapter stateAdaptor = new ArrayAdapter(getActivity(), R.layout.spinner_style, states);
            stateAdaptor.setDropDownViewResource(R.layout.spinner_style);
            db.document("states/English").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
                        Map<String, ?> map = documentSnapshot.getData();
                        states.clear();
                        for (String m : map.keySet()) {
                            states.add(m);
                        }
                        Collections.sort(states);
                        states.add(0, "Select");
                        stateAdaptor.notifyDataSetChanged();
                        spinner_establishment_state.setSelection(Integer.valueOf(datamap.get("Establishment State_pos").toString()));

                    }
                }
            });

            final ArrayList<String> districtArray = new ArrayList<>();
            districtArray.add(0, "Select");
            final ArrayAdapter districtAdaptor = new ArrayAdapter(getActivity(), R.layout.spinner_style, districtArray);
            spinner_establishment_state.setAdapter(stateAdaptor);
            spinner_establishment_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                    spinner_establishment_state.setBackgroundResource(R.drawable.back_grey_round_corners);

                    if (et_name.hasFocus())
                        et_name.clearFocus();
                    if (et_address.hasFocus())
                        et_address.clearFocus();


                    if (position != 0) {
                        db.document("states/English").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
                                    String districtFetch = documentSnapshot.getData().get(states.get(position)).toString();
                                    districtArray.clear();
                                    Collections.addAll(districtArray, districtFetch.split(","));
                                    Collections.sort(districtArray);
                                    districtArray.add(0, "Select");
                                    districtAdaptor.notifyDataSetChanged();
                                    spinner_establishment_district.setSelection(Integer.valueOf(datamap.get("Establishment District_pos").toString()));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //District Spinner
            districtAdaptor.setDropDownViewResource(R.layout.spinner_style);
            spinner_establishment_district.setAdapter(districtAdaptor);
            spinner_establishment_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinner_establishment_district.setBackgroundResource(R.drawable.back_grey_round_corners);
                    if (et_name.hasFocus())
                        et_name.clearFocus();
                    if (et_address.hasFocus())
                        et_address.clearFocus();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception e){
            Log.e("EmployeeDetailEdit", "SpinnerTryCatch");
        }
    }


    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();
        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            currentHeading.setText(resources.getString(R.string.editDetails));
        }
        lc_employeereg2.setText(resources.getString(R.string.Employee_Details));
        lc_name4.setText(resources.getText(R.string.name));
        lc_address9.setText(resources.getText(R.string.Address));
        lc_skill4.setText(resources.getString(R.string.Interested_Job_Categories));
      //  lc_category6.setText(resources.getText(R.string.Establishment_Category));
        lc_state9.setText(resources.getText(R.string.State));
        lc_district9.setText(resources.getText(R.string.District));
        lc_workex4.setText(resources.getString(R.string.work_experience));
        textView18.setText(resources.getText(R.string.editDetails));
        rb_female.setText(resources.getString(R.string.female));
        rb_male.setText(resources.getString(R.string.male));
        rb_other.setText(resources.getString(R.string.other));

    }

    ArrayList<String> selectedSkills = new ArrayList<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        //-----------_To return Skills------------------------
        if (requestCode == 10 && resultCode == 10) {
            selectedSkills = data.getStringArrayListExtra("SelectedProfessions");
            String skillsToShow = "";
            for (int i = 0; i < selectedSkills.size(); i++) {
                if (i == selectedSkills.size() - 1)
                    skillsToShow = skillsToShow + selectedSkills.get(i);
                else
                    skillsToShow = skillsToShow + selectedSkills.get(i) + ", ";
            }

            et_skill.setText(skillsToShow);
        }
    }



    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firebaseFirestore;
    String userPhone;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog pd;


    public void deleteAccount(View view){
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Deleting...");
        pd.show();

        Log.d("DeleteEmployeeAccount", "Started Deletion");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userPhone = firebaseAuth.getCurrentUser().getPhoneNumber();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(getActivity().getResources().getString(R.string.deleteConfirm));
        alertDialog.setPositiveButton(getActivity().getResources().getString(R.string.YesText), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("DeleteeProgress", "Positive Butto On Click");

                firebaseFirestore.collectionGroup("viewapplicants").whereEqualTo("Telephone", userPhone).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("DeleteeProgress", "View Applicants Success");
                        if (queryDocumentSnapshots != null) {
                            Log.d("DeleteeProgress", "View Applicants Success If");
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("DeleteEmployeeAccount", "Stopped Deletion View Applicants: " + e.getLocalizedMessage());

                                    }
                                });

                            }
                            deletefromAppliedJob();
                        }
                        else
                        {
                            Log.d("DeleteeProgress", "View Applicants Success Else");
                            deletefromAppliedJob();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DeleteEmployeeAccount", "Stopped Deletion: " + e.getLocalizedMessage());
                        deletefromAppliedJob();

                    }
                });


            }
        });
        alertDialog.show();


    }



    public void deletefromAppliedJob(){
        Log.d("DeleteProgress", "Into deleteFrom Applied");

        firebaseFirestore.collection("employee/"+userPhone+"/appliedJob").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("DeleteProgress", "Into DeletefromVieApplicants Success");
                if(queryDocumentSnapshots!=null){
                    for(QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots){
                        documentSnapshot1.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("DeleteProgress", "AppliedJobDeleteSuccess");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DeleteProgress", "Into DeletefromVieApplicants Failure");

                            }
                        });
                    }
                    deleteUserDoc();
                }
                else {
                    Log.d("DeleteProgress", "Into DeletefromVieApplicants Else");
                    deleteUserDoc();
                }
            }
        });

    }


    public void deleteUserDoc(){
        Log.d("DeleteProgress", "Into DeleteUserDoc");

        firebaseFirestore.document("employee/"+userPhone).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DeleteProgress", "Into Deletefrom Doc Success");

                deleteUserPhoto();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DeleteAccount", "Error: "+ e.getLocalizedMessage());
                deleteUserPhoto();
            }
        });

    }


    public void deleteUserPhoto() {
        Log.d("DeleteProgress", "Into DeleteUserPic");

        try {


            storageReference.child("Employee/" + userPhone).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    unAuthUser();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("DeletePhoto", "Error: "+ e.getLocalizedMessage());
                    unAuthUser();

                }
            });
        }
        catch (Exception e) {
            Log.e("DeletePhoto", "Error: " + e.getLocalizedMessage());
            unAuthUser();
        }

    }


        public void unAuthUser(){
            Log.d("DeleteProgress", "Into unAuthUSer");
            firebaseAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Deleted Account", Toast.LENGTH_SHORT).show();
                    if (firebaseAuth.getCurrentUser() != null) {
                        firebaseAuth.signOut();
                    }
                    pd.dismiss();
                    startActivity(new Intent(getActivity(), EmployeeLoginActivity.class));

                    getActivity().finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    try {
                        Toast.makeText(getActivity(), "Failed To Delete User Account", Toast.LENGTH_SHORT).show();
                        Log.d("DeleteAccount", "Error: " + e.getLocalizedMessage());
                        pd.dismiss();
                    }catch(Exception ex){

                    }

                }
            });
        }




    public void setInterestedJobCategories(){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.skill_selector_for_save,null,false);
        final LinearLayout skills_ll = view.findViewById(R.id.skill_ll);

        jobCategoryPreference = new ArrayList<>();


        final AlertDialog alertDialog = alertBuilder.create();
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("JobCategories/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                skills_ll.removeAllViews();
                ArrayList<String> professionslist = (ArrayList<String>) documentSnapshot.get("jobCategories");
                for(final String professionItem : professionslist) {

                    LayoutInflater inflater2 = LayoutInflater.from(getActivity());
                    final View view2 = inflater2.inflate(R.layout.skill_selector_checkboxes_view, null, false);
                    final CheckBox cb_skill = view2.findViewById(R.id.cb_skill);
                    cb_skill.setText(professionItem);
                    cb_skill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if( isChecked && jobCategoryPreference.size()>2){
                                cb_skill.setChecked(false);
                                Toast.makeText(getActivity(), "Only 3 items can be selected", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(isChecked) {
                                jobCategoryPreference.add(professionItem);
                            }
                            else {
                                try {
                                    jobCategoryPreference.remove(professionItem);
                                } catch (Exception e) {
                                }
                            }
                        }
                    });

                    skills_ll.addView(view2);
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setView(view);
        alertDialog.show();

        Button saveData = view.findViewById(R.id.button4);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String categoriesSelected = "";

                for(String category: jobCategoryPreference){
                    categoriesSelected = categoriesSelected + category + "\n";
                }
                et_skill.setText(categoriesSelected);
                et_skill.setVisibility(View.VISIBLE);
                alertDialog.dismiss();


            }
        });




    }




}