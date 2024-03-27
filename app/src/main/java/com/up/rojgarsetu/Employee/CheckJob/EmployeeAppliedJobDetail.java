package com.up.rojgarsetu.Employee.CheckJob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.currentHeading;

public class EmployeeAppliedJobDetail extends Fragment {
    Map<String,?> dataMap = new HashMap<>();
    Map<String,Object> applyMap = new HashMap<>();
    Map<String,Object> applyPersonalMap = new HashMap<>();
    boolean CVRequired;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;
    Translator englishHindiTranslator;
    String language = "en";
    String CVUrl="";



    TextView cmpName, cmpAddress, cmpState, cmpDistrict, cmpContact, cmpEmail,
            jobTitle, jobDesc, skillsReq, salary, jobType, cvReq, cajd_applicationStatus;

    TextView titlecajd_state, titlecajd_district, titlecajd_jobTitle, titlecajd_jobDesc, titlecajd_applicationStatus,
            titlecajd_jobSkills,titlecajd_salary, titlecajd_jobType, titlecajd_cvReq, tv_uploadtext;

    LinearLayout cvRequireLL;

    String employerTelephone, employerEmail;
    String contactNumberVisibility;



    TextView companyName, companyAddress, tv_jobLocation, tv_contactName,
            tv_contactNum, tv_contactEmail, tv_jobTitle, tv_jobDescription,
            et_skills, et_qualification, et_salaryRange, et_jobType, et_cvReq;

    TextView et_jobLocation, tv_skills, tv_qualification, tv_salaryRange,
            tv_jobType, tv_cvReq, tv_reUpload, tv_uploadCV;

    CardView call_card, email_card;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_employee_applied_job_detail_new, container, false);
        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            Fragment_Name = getString(R.string.Applied_Job_Details);
            currentHeading.setText(Fragment_Name);
        }


        companyName = rootView.findViewById(R.id.companyName);
        companyAddress = rootView.findViewById(R.id.companyAddress);
        et_jobLocation = rootView.findViewById(R.id.et_jobLocation);
        tv_contactName = rootView.findViewById(R.id.tv_contactName);
        tv_contactNum= rootView.findViewById(R.id.tv_contactNum);
        tv_contactEmail = rootView.findViewById(R.id.tv_contactEmail);
        tv_jobTitle = rootView.findViewById(R.id.tv_jobTitle);
        tv_jobDescription = rootView.findViewById(R.id.tv_jobDescription);
        et_skills = rootView.findViewById(R.id.et_skills);
        et_qualification = rootView.findViewById(R.id.et_qualification);
        et_salaryRange = rootView.findViewById(R.id.et_salaryRange);
        et_jobType= rootView.findViewById(R.id.et_jobTypeOrMinExp);
        et_cvReq = rootView.findViewById(R.id.et_cvReq);
        cajd_applicationStatus = rootView.findViewById(R.id.cajd_appliedStatus);

        final LinearLayout llv_cv = rootView.findViewById(R.id.llv_cv);




        tv_jobLocation = rootView.findViewById(R.id.tv_jobLocation);
        tv_skills = rootView.findViewById(R.id.tv_skills);
        tv_qualification = rootView.findViewById(R.id.tv_qualification);
        tv_salaryRange = rootView.findViewById(R.id.tv_salaryRange);
        tv_jobType = rootView.findViewById(R.id.tv_jobTypeOrMinExp);
        tv_cvReq = rootView.findViewById(R.id.tv_cvReq);
        tv_reUpload = rootView.findViewById(R.id.tv_reupload);
        tv_uploadCV=rootView.findViewById(R.id.tv_uploadCV);

        call_card = rootView.findViewById(R.id.call_card);
        email_card = rootView.findViewById(R.id.email_card);

        cvRequireLL = rootView.findViewById(R.id.llv_cv);


        tv_uploadtext = rootView.findViewById(R.id.textView50);




        Paper.init(getActivity());
        language = Paper.book().read("language");
        updateView((String)Paper.book().read("language"));
        storageReference= FirebaseStorage.getInstance().getReference();

        CVRequired=false;
        //Change Language
        Paper.init(getActivity());
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String telephone = firebaseUser.getPhoneNumber();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final SharedPreferences map = getActivity().getSharedPreferences("CheckJob", Context.MODE_PRIVATE);
        applyPersonalMap = (Map<String, Object>) map.getAll();
        try {
            String jobPostedOwner = applyPersonalMap.get("ownerUserID").toString();
            map.edit().clear().commit();
            try {
                final long OO = 1024 * 1024;
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                storageRef.child("Employee/" + jobPostedOwner).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ImageView dp = rootView.findViewById(R.id.cmp_logo);
                        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        dp.setImageBitmap(bit);
                        System.out.println("Online se hua READ ");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Emp.AppliedJobDetail", "Image:" + e.getLocalizedMessage());
                            }
                        });
            } catch (Exception e1) {
                Log.e("Emp.AppliedJobDetail", "Image:" + e1.getLocalizedMessage());
            }





/*
        final EditText et_detail_check_job_jobTitle = rootView.rootView.findViewById(R.id.et_detail_check_job_jobTitle);
        final EditText et_detail_check_job_company = rootView.rootView.findViewById(R.id.et_detail_check_job_company);
        final EditText et_detail_check_job_state = rootView.rootView.findViewById(R.id.et_detail_check_job_state);
        final EditText et_detail_check_job_district = rootView.rootView.findViewById(R.id.et_detail_check_job_district);
        final EditText et_detail_check_job_type = rootView.rootView.findViewById(R.id.et_detail_check_job_type);
        final EditText et_detail_check_job_startingRs = rootView.rootView.findViewById(R.id.et_detail_check_job_startingRs);
        final EditText et_detail_check_job_endingRs = rootView.rootView.findViewById(R.id.et_detail_check_job_endingRs);
        final EditText et_detail_check_job_SalaryDetail = rootView.rootView.findViewById(R.id.et_detail_check_job_salaryDetails);
        //EditText et_detail_check_job_upload = rootView.rootView.findViewById(R.id.et_detail_check_job_upload);
        final EditText et_detail_check_job_contactNumber = rootView.rootView.findViewById(R.id.et_detail_check_job_contactNumber);
        final EditText et_detail_check_job_Address = rootView.rootView.findViewById(R.id.et_detail_check_job_Address);
        final TextView tv_detail_check_job_salary_period = rootView.rootView.findViewById(R.id.tv_detail_check_job_salary_period);

        //bt_employee_checkjob_uploadcv=rootView.rootView.findViewById(R.id.bt_employee_checkjob_uploadcv);




        lc_jobtitle2=rootView.rootView.findViewById(R.id.lc_jobtitle2);
        lc_jobdescription4=rootView.rootView.findViewById(R.id.lc_jobdescription4);
        lc_companyname2=rootView.rootView.findViewById(R.id.lc_companyname2);
        lc_state6=rootView.rootView.findViewById(R.id.lc_state6);
        lc_district6=rootView.rootView.findViewById(R.id.lc_district6);
        textView10=rootView.rootView.findViewById(R.id.textView10);
        textView5=rootView.rootView.findViewById(R.id.textView5);
        lc_contact3=rootView.rootView.findViewById(R.id.lc_contact3);
        lc_address7=rootView.rootView.findViewById(R.id.lc_address7);


 */


            //final CardView bt_detail_check_job_Apply = rootView.rootView.findViewById(R.id.card_checkJobDetails_apply);


            db.collection("employee").document(telephone)
                    .collection("appliedJob")
                    .document(applyPersonalMap.get("documentNumber").toString())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
                                if (documentSnapshot.getData().get("jobOffered").toString().equalsIgnoreCase("1")) {
                                    cajd_applicationStatus.setText(R.string.Accept);
                                    cajd_applicationStatus.setTextColor(getResources().getColor(R.color.GreenCV));
                                } else if (documentSnapshot.getData().get("jobOffered").toString().equalsIgnoreCase("0")) {
                                    cajd_applicationStatus.setText(R.string.Reject);
                                    cajd_applicationStatus.setTextColor(Color.RED);
                                } else {
                                    cajd_applicationStatus.setText(R.string.Decision_Pending);
                                    cajd_applicationStatus.setTextColor(getResources().getColor(R.color.Orange));

                                }
                            }
                        }
                    });


            db.collection("users").document(jobPostedOwner)
                    .collection("jobsposted")
                    .document(applyPersonalMap.get("documentNumber").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        dataMap = documentSnapshot.getData();

                        try {
                            String st = dataMap.get("jobTitle").toString();
                            changeLanguage(st, tv_jobTitle);
                        }
                        catch(Exception e){
                            changeLanguage("---", tv_jobTitle);
                        }

                        try {
                            String st = dataMap.get("jobDescription").toString();
                            changeLanguage(st, tv_jobDescription);
                        }
                        catch(Exception e){
                            changeLanguage("---", tv_jobDescription);
                        }

                        try {
                            String st = dataMap.get("company").toString();
                            changeLanguage(st, companyName);
                        }
                        catch(Exception e){
                            changeLanguage("---", companyName);
                        }

                        try {
                            String location = "";

                            if(dataMap.get("selectedLocationSet")!=null){
                                Map<String, ArrayList<String>> selectedLocationSet = (Map<String, ArrayList<String>>) dataMap.get("selectedLocationSet");
                                for(String state : selectedLocationSet.keySet()  ){
                                    ArrayList<String> districtsSet = selectedLocationSet.get(state);
                                    for(String district : districtsSet){
                                        location = location + "[" + district + ", " + state + "]\n";

                                    }
                                }

                            }
                            else{
                                String st_state = dataMap.get("State").toString();
                                String st_district = dataMap.get("District").toString();
                                location = st_district + ", " + st_state;

                            }


                            changeLanguage(location, et_jobLocation);
                        }
                        catch(Exception e){
                            Log.e("JobLocation", "" + e.getMessage());
                            changeLanguage("---", et_jobLocation);
                        }



                        try {
                            String st;
                            Object obj = dataMap.get("minExpReq");
                            if(obj!=null){
                                st = obj.toString();
                                tv_jobType.setText("Minimum Experience Required");
                            }
                            else{
                                st = dataMap.get("jobTypeName").toString();
                            }


                            changeLanguage(st, et_jobType);
                        }
                        catch(Exception e){
                            changeLanguage("---", et_jobType);
                        }


                        try {
                            String startingS = dataMap.get("startingRs").toString();
                            String endingS = dataMap.get("endingRs").toString();
                            String salaryP = dataMap.get("salaryPeriod").toString();

                            String salaryDetail = startingS + " to " + endingS + " " + salaryP;
                            changeLanguage(salaryDetail, et_salaryRange);
                        }
                        catch(Exception e){
                            changeLanguage("---", et_salaryRange);
                        }

                        try {
                            String st = dataMap.get("contactNumber").toString();
                            employerTelephone = st;
                        }
                        catch(Exception e){
                        }

                        try {
                            String st = dataMap.get("address").toString();
                            changeLanguage(st, companyAddress);
                        }
                        catch(Exception e){
                            changeLanguage("---", companyAddress);
                        }

                        try {
                            String st = dataMap.get("Skills").toString();
                            changeLanguage(st, et_skills);
                        }
                        catch(Exception e){
                            changeLanguage("---", et_skills);
                        }

                        try {
                            String st = dataMap.get("CvRequired").toString();
                            changeLanguage(st, et_cvReq);
                        }
                        catch(Exception e){
                            changeLanguage("---", et_cvReq);
                        }
                        try {
                            String st = dataMap.get("contactEmail").toString();
                            employerEmail= st;
                        }
                        catch(Exception e){
                            String st = dataMap.get("EmployerEmailid").toString();
                        }

                        try {
                            String st = dataMap.get("qualifications").toString();
                            changeLanguage(st, et_qualification);
                        }
                        catch(Exception e){
                            changeLanguage("---", et_qualification);
                        }

                        try {
                            String st = dataMap.get("contactPersonName").toString();
                            changeLanguage(st, tv_contactName);
                        }
                        catch(Exception e){
                            changeLanguage("---", tv_contactName);
                        }

                        if (dataMap.get("CvRequired").toString().equalsIgnoreCase("No")) {
                            llv_cv.setVisibility(View.GONE);
                        } else if (dataMap.get("CvRequired").toString().equalsIgnoreCase("Yes")) {
                            CVRequired = true;
                        }
                    }
                }
            });


            cvRequireLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storageReference = firebaseStorage.getInstance().getReference();
                    storageReference.child("uploads").child(applyPersonalMap.get("documentNumber").toString())
                            .child(telephone + ".pdf")
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadFile(getActivity(),
                                    telephone,
                                    ".pdf",
                                    DIRECTORY_DOWNLOADS,
                                    uri.toString()
                            );

                            Toast.makeText(getActivity(), "Downloading File! Check Notification", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });


        }
        catch(Exception e){
            Log.e("EmployeeAppliedJobDet", "Error : at getting data in AppliedJobDetail" + e.getLocalizedMessage());
            Toast.makeText(activity, "Some error occurred. Please try again", Toast.LENGTH_SHORT).show();
        }


        call_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEmployer(v);
            }
        });

        email_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEmployer(v);
            }
        });



        return rootView;









/*


        final EditText et_detail_applied_job_offered = rootView.rootView.findViewById(R.id.et_detail_applied_job_offered);
        final EditText et_detail_applied_job_jobTitle = rootView.rootView.findViewById(R.id.et_detail_applied_job_jobTitle);
        final EditText et_detail_applied_job_company = rootView.rootView.findViewById(R.id.et_detail_applied_job_company);
        final EditText et_detail_applied_job_state = rootView.rootView.findViewById(R.id.et_detail_applied_job_state);
        final EditText et_detail_applied_job_district = rootView.rootView.findViewById(R.id.et_detail_applied_job_district);
        final EditText et_detail_applied_job_type = rootView.rootView.findViewById(R.id.et_detail_applied_job_type);
        final EditText et_detail_applied_job_startingRs = rootView.rootView.findViewById(R.id.et_detail_applied_job_startingRs);
        final EditText et_detail_applied_job_endingRs = rootView.rootView.findViewById(R.id.et_detail_applied_job_endingRs);
        //final EditText et_detail_applied_job_upload = rootView.rootView.findViewById(R.id.et_detail_applied_job_upload);
        final EditText et_detail_applied_job_salaryDetail = rootView.rootView.findViewById(R.id.et_detail_applied_job_salaryDetails);
        final EditText et_detail_applied_job_contactNumber = rootView.rootView.findViewById(R.id.et_detail_applied_job_contactNumber);
        final EditText et_detail_applied_job_Address = rootView.rootView.findViewById(R.id.et_detail_applied_job_Address);
        final LinearLayout layout_detail_applied_job_cv_require = rootView.rootView.findViewById(R.id.layout_detail_applied_job_cv_require);
        et_detail_applied_job_jobDescription=rootView.rootView.findViewById(R.id.et_detail_applied_job_jobDescription);
        bt_employee_checkjob_downloadcv=rootView.rootView.findViewById(R.id.bt_employee_appliedjob_downloadcv);

        lc_jobdescription5=rootView.rootView.findViewById(R.id.lc_jobdescription5);
        lc_offer = rootView.rootView.findViewById(R.id.lc_offer);
        lc_jobtitle3 = rootView.rootView.findViewById(R.id.lc_jobtitle3);
        lc_companyname3 = rootView.rootView.findViewById(R.id.lc_companyname3);
        lc_state8 = rootView.rootView.findViewById(R.id.lc_state8);
        lc_district8 = rootView.rootView.findViewById(R.id.lc_district8);
        textView10 = rootView.rootView.findViewById(R.id.textView10);
        textView5 = rootView.rootView.findViewById(R.id.textView5);
        textView7 = rootView.rootView.findViewById(R.id.textView7);
        tv_detail_applied_job_salary_period = rootView.rootView.findViewById(R.id.tv_detail_applied_job_salary_period);
        lc_contact4 = rootView.rootView.findViewById(R.id.lc_contact4);
        lc_address8 = rootView.rootView.findViewById(R.id.lc_address8);
        textView27 = rootView.rootView.findViewById(R.id.textView27);

        updateView((String)Paper.book().read("language"));


        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);


        CVRequired=false;
        Paper.init(getActivity());
        language = Paper.book().read("language");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String telephone = firebaseUser.getPhoneNumber();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final SharedPreferences map = getActivity().getSharedPreferences("CheckJob", Context.MODE_PRIVATE);



        applyPersonalMap = (Map<String, Object>) map.getAll();
        db.collection("users").document(map.getString("ownerUserID",""))
                .collection("jobsposted")
                .document(map.getString("documentNumber","")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e==null && documentSnapshot!=null && documentSnapshot.exists()){
                    dataMap = documentSnapshot.getData();

                    String startingS = dataMap.get("startingRs").toString();
                    String endingS = dataMap.get("endingRs").toString();
                    String salaryP = dataMap.get("salaryPeriod").toString();

                    String salaryDetail = startingS + " to "+ endingS + " : "  + salaryP;

                    changeLanguage(dataMap.get("jobTitle").toString(),et_detail_applied_job_jobTitle);
                    changeLanguage(dataMap.get("jobDescription").toString(),et_detail_applied_job_jobDescription);
                    changeLanguage(dataMap.get("company").toString(),et_detail_applied_job_company);
                    changeLanguage(dataMap.get("State").toString(),et_detail_applied_job_state);
                    changeLanguage(dataMap.get("District").toString(),et_detail_applied_job_district);
                    changeLanguage(dataMap.get("jobTypeName").toString(),et_detail_applied_job_type);
                    changeLanguage(salaryDetail,et_detail_applied_job_salaryDetail);
                    changeLanguage(dataMap.get("contactNumber").toString(),et_detail_applied_job_contactNumber);
                    changeLanguage(dataMap.get("address").toString(),et_detail_applied_job_Address);
                    if(dataMap.get("CvRequired").toString().equalsIgnoreCase("No")){
                        layout_detail_applied_job_cv_require.setVisibility(View.GONE);
                    }else if(dataMap.get("CvRequired").toString().equalsIgnoreCase("Yes")){
                        CVRequired=true;

                    }
                }
            }
        });


        bt_employee_checkjob_downloadcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageReference=firebaseStorage.getInstance().getReference();
                storageReference.child("uploads").child(map.getString("documentNumber",""))
                        .child(telephone+".pdf")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        downloadFile(getActivity(),
                                telephone,
                                ".pdf",
                                DIRECTORY_DOWNLOADS,
                                uri.toString()
                        );

                        Toast.makeText(getActivity(), "Downloading File!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        final CardView card_checkJobDetails_back = rootView.rootView.findViewById(R.id.card_checkJobDetails_back);
        card_checkJobDetails_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new EmployeeAppliedJob());
                ft.commit();
            }
        });

        return rootView;
        
 */
    }

    void downloadFile(Context context, String filename, String fileextension, String destinationDirectory, String url){

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,filename+fileextension);
        downloadManager.enqueue(request);

    }

    void changeLanguage(String text, final TextView textView){
        if(language.equalsIgnoreCase("hi")){
      //  if(false){
        final String[] temp = new String[1];
        englishHindiTranslator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                temp[0] = translatedText;
                                textView.setText(temp[0]);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                Log.d("Emp.AppliedJobDet","ChangeLanguage: "+ e.getMessage());
                            }
                        });
    }else{
            textView.setText(text);
        }
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();
        currentHeading.setText(resources.getString(R.string.Applied_Job_Details));

        //tv_reupload.setText(resources.getString(R.string.Tap_Button_To_Reupload));
        //tv_checkJobDetails_apply.setText(resources.getString(R.string.Apply));
        //textView27.setText(resources.getString(R.string.back));
    }


    public void callEmployer(View view){


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},1);
            return;
        }


        if(employerTelephone!=null){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setMessage("Are you sure you want to call the Employer?");
            alertBuilder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:" + employerTelephone));
                    startActivity(phoneIntent);
                }
            });
            alertBuilder.show();
        }

    }

    public void emailEmployer(View view){

        if(employerEmail!=null) {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:"));
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{employerEmail});
            email.putExtra(Intent.EXTRA_SUBJECT, "Application for Job through Rojgar Setu");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }
    }



}
