package com.up.rojgarsetu.Employee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.up.rojgarsetu.Employee.Login.EmployeeLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.ProfessionActivity;
import com.up.rojgarsetu.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;

public class EmployeeDetailForm extends AppCompatActivity {




    StorageReference storageRef;
    Uri uri;
    FirebaseUser firebaseUser;


    ProgressDialog progressDialog;
    TextView lc_employeereg,lc_name3,lc_address6,lc_skill3,lc_category5,
            lc_sector5,lc_subsector5,lc_state7,lc_district7,
            lc_workex3,textView22;

    EditText et_skill;

    RadioGroup rg_qualification, rg_minExpReq, rg_gender;

    String telephone;


    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail_form);
        // Inflate the layout for getContext() fragment
        //Initial Variable of xml file
        Fragment_Name= getResources().getString(R.string.employee_registration);
        //currentHeading.setText(Fragment_Name);

        storageRef = FirebaseStorage.getInstance().getReference();


        final EditText et_name=findViewById(R.id.et_name);
        final EditText et_address=findViewById(R.id.et_address);
        final EditText et_work_experience=findViewById(R.id.et_experience);
        final CardView card_accept_edf = findViewById(R.id.card_edf_accept);
        final CardView card_logout_edf = findViewById(R.id.card_edf_layout);

        lc_employeereg=findViewById(R.id.lc_employeereg);
        lc_name3=findViewById(R.id.lc_name3);
        lc_address6=findViewById(R.id.lc_address6);
        lc_skill3=findViewById(R.id.lc_skill3);

        lc_state7=findViewById(R.id.lc_state7);
        lc_district7=findViewById(R.id.lc_district7);
        lc_workex3=findViewById(R.id.lc_workex3);
        textView22=findViewById(R.id.textView22);
        et_skill=findViewById(R.id.et_skill);

        rg_qualification = findViewById(R.id.rg_qualification);
        rg_gender = findViewById(R.id.rg_gender);
        rg_minExpReq = findViewById(R.id.rg_minExpReq);


        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");

        updateView((String)Paper.book().read("language"));

        //Getting user Phone Number for Saving Data
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        telephone = firebaseUser.getPhoneNumber();

        //Assigning focus check
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


        et_work_experience.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_work_experience.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_work_experience.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        CardView selectSkills = findViewById(R.id.select_skills);
        selectSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInterestedJobCategories();

            }
        });

        LinearLayout updateProfile = findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic(v);
            }
        });

        //Firebase Initialize
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        //Category Spinner
//        final ArrayList<String> categoryArray = new ArrayList<>();
//        categoryArray.add(0,"Select");
//        final ArrayAdapter<CharSequence> adaptorCategory=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,categoryArray);
//
//        db.collection("category").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//                if (snapshot != null) {
//                    Map<String,?> map;
//                    categoryArray.clear();
//                    categoryArray.add(0,"Select");
//                    for (QueryDocumentSnapshot document : snapshot) {
//                        map = document.getData();
//                        List<String> temp = ((List<String>)map.get(String.valueOf(map.keySet()).replace("[","").replace("]","")));
//                        categoryArray.add(temp.get(1));
//                    }
//                    adaptorCategory.notifyDataSetChanged();
//                }
//            }
//        });


//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    Map<String,?> map;
//                    categoryArray.clear();
//                    categoryArray.add(0,"Select");
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        map = document.getData();
//                        List<String> temp = ((List<String>)map.get(String.valueOf(map.keySet()).replace("[","").replace("]","")));
//                        categoryArray.add(temp.get(1));
//                    }
//                    adaptorCategory.notifyDataSetChanged();
//
//                }
//            }
//        });


//        final Spinner spinner_establishment_category=findViewById(R.id.spinner_establishment_category);
//        adaptorCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_establishment_category.setAdapter(adaptorCategory);
//        spinner_establishment_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinner_establishment_category.setBackgroundResource(R.drawable.back_grey_round_corners);
//                if(et_name.hasFocus()){
//                    et_name.clearFocus();
//                }
//                if(et_address.hasFocus())
//                    et_address.clearFocus();
//                if(et_work_experience.hasFocus())
//                    et_work_experience.clearFocus();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        //Sector Spinner
        final Spinner spinner_establishment_sector=findViewById(R.id.spinner_establishment_sector);
        final ArrayList<String> sectorArray = new ArrayList<>();
        final Spinner spinner_establishment_subsector=findViewById(R.id.spinner_establishment_subsector);
        final ArrayList<String> subSectorArray = new ArrayList();
        subSectorArray.add(0,"Select");
        final ArrayAdapter adaptorSector=new ArrayAdapter(getApplicationContext(),R.layout.spinner_style,sectorArray);
        sectorArray.add(0,"Select");
        final ArrayAdapter subSectorAdaptor=new ArrayAdapter(getApplicationContext(),R.layout.spinner_style, subSectorArray);
        adaptorSector.setDropDownViewResource(R.layout.spinner_style);
        db.collection("sector").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null && snapshots!=null){
                    for (QueryDocumentSnapshot document : snapshots) {
                        sectorArray.add(document.getId());
                    }
                    adaptorSector.notifyDataSetChanged();
                }
            }
        });


//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if(task.isSuccessful()){
////                    for (QueryDocumentSnapshot document : task.getResult()) {
////                        sectorArray.add(document.getId());
////                    }
////                    adaptorSector.notifyDataSetChanged();
////                }
////            }
////        });


        //State Spinner
        final ArrayList<String> states= new ArrayList<>();
        states.add(0,"Select");
        final ArrayAdapter stateAdaptor= new ArrayAdapter(getApplicationContext(),R.layout.spinner_style,states);
        stateAdaptor.setDropDownViewResource(R.layout.spinner_style);
        db.document("states/English").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e==null && documentSnapshot!=null && documentSnapshot.exists()){
                    Map<String,?> map = documentSnapshot.getData();
                    states.clear();
                    for(String m:map.keySet()){
                        states.add(m);
                    }
                    Collections.sort(states);
                    states.add(0,"Select");
                    stateAdaptor.notifyDataSetChanged();
                }
            }
        });

//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Map<String,?> map = task.getResult().getData();
//                states.clear();
//                for(String m:map.keySet()){
//                    states.add(m);
//                }
//                Collections.sort(states);
//                states.add(0,"Select");
//                stateAdaptor.notifyDataSetChanged();
//            }
//        });
        final ArrayList<String> districtArray = new ArrayList<>();
        districtArray.add(0,"Select");
        final ArrayAdapter districtAdaptor= new ArrayAdapter(getApplicationContext(),R.layout.spinner_style,districtArray);
        final Spinner spinner_establishment_district=findViewById(R.id.spinner_district);
        final Spinner spinner_establishment_state=findViewById(R.id.spinner_state);
        spinner_establishment_state.setAdapter(stateAdaptor);
        spinner_establishment_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                spinner_establishment_state.setBackgroundResource(R.drawable.back_grey_round_corners);

                if(et_name.hasFocus())
                    et_name.clearFocus();
                if(et_address.hasFocus())
                    et_address.clearFocus();
                if(et_work_experience.hasFocus())
                    et_work_experience.clearFocus();

                if(position!=0) {
                    db.document("states/English").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(e==null && documentSnapshot!=null && documentSnapshot.exists()){
                                String districtFetch = documentSnapshot.getData().get(states.get(position)).toString();
                                districtArray.clear();
                                Collections.addAll(districtArray, districtFetch.split(","));
                                Collections.sort(districtArray);
                                districtArray.add(0,"Select");
                                districtAdaptor.notifyDataSetChanged();
                                spinner_establishment_district.setSelection(0);
                            }
                        }
                    });
//
//                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            String districtFetch = task.getResult().getData().get(states.get(position)).toString();
//                            districtArray.clear();
//                            Collections.addAll(districtArray, districtFetch.split(","));
//                            Collections.sort(districtArray);
//                            districtArray.add(0,"Select");
//                            districtAdaptor.notifyDataSetChanged();
//                            spinner_establishment_district.setSelection(0);
//                        }
//                    });
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
                if(et_name.hasFocus()){
                    et_name.clearFocus();
                }
                if(et_address.hasFocus())
                    et_address.clearFocus();
                if(et_work_experience.hasFocus())
                    et_work_experience.clearFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Save data in SharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();

        if(map.containsKey("Name")) {
            et_name.setText(map.get("Name").toString());
            et_address.setText(map.get("Address").toString());
        }

        //Save Data Button Click Event
        //final Button card_accept = findViewById(R.id.SaveData);
        card_accept_edf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //------------Tap Effect-------------
                card_accept_edf.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_accept_edf.setElevation(5);
                    }
                },200);

                //-----------Tap Effect Ends--------

                progressDialog = new ProgressDialog(EmployeeDetailForm.this);
                progressDialog.setMessage(getResources().getString(R.string.savingData));
                progressDialog.show();




                if(et_name.getText().toString().length()==0 ||
                        et_address.getText().toString().length()==0 ||
                        et_skill.getText().toString().length()==0 ||
                        spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select") ||
                        spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select"))
                {
                    Toast.makeText(getApplicationContext(), "All Fields marked (*) should be filled", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    if(et_name.getText().toString().length()==0){
                        et_name.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_address.getText().toString().length()==0){
                        et_address.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_skill.getText().toString().length()==0){
                        et_skill.setBackgroundResource(R.drawable.back_red_round_corners);
                    }

                   // if(spinner_establishment_category.getItemAtPosition(spinner_establishment_category.getSelectedItemPosition()).toString().equals("Select")){
                    //    spinner_establishment_category.setBackgroundResource(R.drawable.back_red_round_corners);
                   // }

                    if(spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_establishment_state.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select") ){
                        spinner_establishment_district.setBackgroundResource(R.drawable.back_red_round_corners);
                    }


                }

                else{

                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE).edit();

                   // editor.putString("Establishment Category", spinner_establishment_category.getItemAtPosition(spinner_establishment_category.getSelectedItemPosition()).toString());
                   // editor.putString("Establishment Category_pos", spinner_establishment_category.getSelectedItemPosition()+"");


                    editor.putString("Establishment State", spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString());
                    editor.putString("Establishment State_pos", spinner_establishment_state.getSelectedItemPosition()+"");

                    editor.putString("Establishment District", spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString());
                    editor.putString("Establishment District_pos", spinner_establishment_district.getSelectedItemPosition()+"");

                    editor.putString("Name", et_name.getText().toString());
                    editor.putString("Address", et_address.getText().toString());
                    editor.putString("Telephone", telephone);
                    editor.putString("Skill",et_skill.getText().toString());

                    editor.apply();

                    OneSignal.sendTag("District",spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString());

                    updateCloudDatabase(telephone);
                }


            }
        });






        //-----------Code for AutoScrolling to EditText ends-------------


        card_logout_edf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EmployeeDetailForm.this)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure you want to SignOut?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                nikal(null);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }

    ArrayList<String> selectedSkills = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //-----------_To return Skills------------------------
        if(requestCode==10 && resultCode==10){
            selectedSkills = data.getStringArrayListExtra("SelectedProfessions");
            String skillsToShow = "";
            for(int i=0; i<selectedSkills.size(); i++){
                if(i==selectedSkills.size()-1)
                    skillsToShow = skillsToShow + selectedSkills.get(i);
                else
                    skillsToShow= skillsToShow + selectedSkills.get(i) + ", ";
            }

            et_skill.setText(skillsToShow);
        }

        //------------Returning Skills end-------------------------


        //---------------------Profile Picture Update-------------------


        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                try {
                    currImageURI = data.getData();

                    String imgDecodableString = getRealPathFromURI(currImageURI);
                    if (imgDecodableString == null) {
                        Toast.makeText(context, "Couldn't read file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        String path = compressImage(imgDecodableString);
                        Uri file = Uri.fromFile(new File(path));

                        uri = file;
                    }
                    catch(Exception e){
                        Log.e("Emp. Detail Form" , " Image Compression Error");
                        uri = currImageURI;
                    }
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setBorderLineColor(Color.RED)
                            .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                            .start(this);
                }catch(Exception e){
                    Log.e("Emp. Detail Form" , " Image Reading Error");
                }


            }
        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    uri = resultUri;
                    ImageView img = findViewById(R.id.imv_profilePic);
                    img.setImageURI(resultUri);
                    uploadImage(uri);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    ImageView img = findViewById(R.id.imv_profilePic);
                    img.setImageURI(uri);
                    uploadImage(uri);
                }
            }
        }
        catch(Exception e){
            Log.e("Emp. Detail Form" , " Image Cropping Error");

            if(uri!=null){
                ImageView img = findViewById(R.id.imv_profilePic);
                img.setImageURI(uri);
                uploadImage(uri);
            }
        }


        //------------------Profile Picture Returning Code ends----------------------------------------------------------



        super.onActivityResult(requestCode, resultCode, data);

    }

    public void nikal(View v) {

        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = getSharedPreferences("EMPLOYEE DETAILS", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Log-Out Successful!", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(EmployeeDetailForm.this, EmployeeLoginActivity.class);
        startActivity(intent);
        finish();
    }



    Map<String, Object> userMap;
    //Save Data in Firestore
//    ProgressDialog progressBar = new ProgressDialog(getContext());
    void updateCloudDatabase(final String telephone){
    //    progressBar.setMessage("Writing Data...");
    //    progressBar.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE);
        userMap = (Map<String, Object>) prefs.getAll();
        userMap.put("jobCategoryPreference", jobCategoriesPreference);

        int qualificationCheckedId = rg_qualification.getCheckedRadioButtonId();
        RadioButton rbq = findViewById(qualificationCheckedId);


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


        int minExp = rg_minExpReq.getCheckedRadioButtonId();


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






        db.collection("employee").document(telephone)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(),"Save Successfully",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("EMPLOYEE_DETAIL", MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        progressDialog.dismiss();
                        Intent intent=new Intent(getApplicationContext(), EmployeeMainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.w(TAG, "Error writing document", e);

                    }
                });


    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();
        try {
            lc_employeereg.setText(getResources().getText(R.string.employee_registration));
            lc_name3.setText(getResources().getText(R.string.name));
            lc_address6.setText(getResources().getText(R.string.Address));
            lc_skill3.setText(getResources().getText(R.string.skill));
            lc_category5.setText(getResources().getText(R.string.Establishment_Category));
            lc_sector5.setText(getResources().getText(R.string.Establishment_Sector));
            lc_subsector5.setText(getResources().getText(R.string.Establishment_Sub_Sector));
            lc_state7.setText(getResources().getText(R.string.State));
            lc_district7.setText(getResources().getText(R.string.District));
            lc_workex3.setText("Work Experience");
            textView22.setText(getResources().getText(R.string.logOut));
        }
        catch(Exception e){}

    }

//--------------------Profile Picture Reading, Compression and Uploading-----------------------------


    public void takePic(View v) {
        context=this;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EmployeeDetailForm.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(EmployeeDetailForm.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            //takePic(v);
            return;
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    private static final int CAMERA_REQUEST = 1;

    Uri currImageURI;





    public String getRealPathFromURI(Uri uri) {
        String path = "";
        String[] projection = { MediaStore.Images.Media.DATA };
        if (getContentResolver() != null) {
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }





    private Context context;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;



    public String compressImage( String imagePath)
    {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName=telephone+".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        return uriString;

    }

    public void uploadImage(final Uri file)
    {
        final ProgressDialog pdnew = new ProgressDialog(EmployeeDetailForm.this);
        pdnew.setMessage("Linking Image to Account");
        pdnew.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(file).build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pdnew.dismiss();
                    Toast.makeText(EmployeeDetailForm.this, "Image Linked to Account.", Toast.LENGTH_SHORT).show();
                    StorageReference riversRef = storageRef.child("Employee/"+telephone);
                    final ProgressDialog progressDialog = new ProgressDialog(EmployeeDetailForm.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.setCancelable(true);
                            progressDialog.dismiss();
                            Log.e("Emp.DetailForm", "ImageuploadFailed");


                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setCancelable(true);
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeDetailForm.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
                else{
                    Toast.makeText(EmployeeDetailForm.this, "Unable to link image to account.", Toast.LENGTH_SHORT).show();
                    StorageReference riversRef = storageRef.child(telephone);
                    final ProgressDialog progressDialog = new ProgressDialog(EmployeeDetailForm.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.setCancelable(true);
                            progressDialog.dismiss();
                            Log.e("Emp.DetailForm", "ImageuploadFailed");


                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setCancelable(true);
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeDetailForm.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
            }
        });

    }


    ArrayList<String> jobCategoriesPreference;


    public void setInterestedJobCategories(){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.skill_selector_for_save,null,false);
        final LinearLayout skills_ll = view.findViewById(R.id.skill_ll);

        jobCategoriesPreference = new ArrayList<>();


        final AlertDialog alertDialog = alertBuilder.create();
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("JobCategories/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                skills_ll.removeAllViews();
                ArrayList<String> professionslist = (ArrayList<String>) documentSnapshot.get("jobCategories");
                for(final String professionItem : professionslist) {

                    LayoutInflater inflater2 = LayoutInflater.from(EmployeeDetailForm.this);
                    final View view2 = inflater2.inflate(R.layout.skill_selector_checkboxes_view, null, false);
                    final CheckBox cb_skill = view2.findViewById(R.id.cb_skill);
                    cb_skill.setText(professionItem);
                    cb_skill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if( isChecked && jobCategoriesPreference.size()>2){
                                cb_skill.setChecked(false);
                                Toast.makeText(EmployeeDetailForm.this, "Only 3 items can be selected", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(isChecked) {
                                jobCategoriesPreference.add(professionItem);
                            }
                            else {
                                try {
                                    jobCategoriesPreference.remove(professionItem);
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

                for(String category: jobCategoriesPreference){
                    categoriesSelected = categoriesSelected + category + "\n";
                }
                et_skill.setText(categoriesSelected);
                et_skill.setVisibility(View.VISIBLE);
                alertDialog.dismiss();


            }
        });




    }


}