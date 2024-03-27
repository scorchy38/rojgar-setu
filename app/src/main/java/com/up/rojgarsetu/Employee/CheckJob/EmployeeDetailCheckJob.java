package com.up.rojgarsetu.Employee.CheckJob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.currentHeading;

public class EmployeeDetailCheckJob extends AppCompatActivity {
    Map<String,?> dataMap = new HashMap<>();
    Map<String,Object> applyMap = new HashMap<>();
    Map<String,Object> applyPersonalMap = new HashMap<>();
    boolean CVRequired;
    Button bt_employee_checkjob_uploadcv;
    StorageReference storageReference;
    String filename="";
    String CVUrl="";
    Translator englishHindiTranslator;
    String language = "en";
    AlertDialog.Builder alertDialog;
    String employerTelephone, employerEmail;
    String contactNumberVisibility;

    LinearLayout cvRequireLL;





    TextView companyName, companyAddress, tv_jobLocation, tv_contactName,
            tv_contactNum, tv_contactEmail, tv_jobTitle, tv_jobDescription,
            et_skills, et_qualification, et_salaryRange, et_jobType, et_cvReq;

    TextView et_jobLocation, tv_skills, tv_qualification, tv_salaryRange,
            tv_jobType, tv_cvReq, tv_reUpload, tv_uploadCV, tv_apply, tv_back;

    CardView cv_apply, cv_back;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_employee_detail_check_job_new);
        //Fragment_Name= getResources().getString(R.string.Job_Details);
        //currentHeading.setText(Fragment_Name);}




        companyName = findViewById(R.id.companyName);
        companyAddress = findViewById(R.id.companyAddress);
        et_jobLocation = findViewById(R.id.et_jobLocation);
        tv_contactName = findViewById(R.id.tv_contactName);
        tv_contactNum= findViewById(R.id.tv_contactNum);
        tv_contactEmail = findViewById(R.id.tv_contactEmail);
        tv_jobTitle = findViewById(R.id.tv_jobTitle);
        tv_jobDescription = findViewById(R.id.tv_jobDescription);
        et_skills = findViewById(R.id.et_skills);
        et_qualification = findViewById(R.id.et_qualification);
        et_salaryRange = findViewById(R.id.et_salaryRange);
        et_jobType= findViewById(R.id.et_jobTypeOrMinExp);
        et_cvReq = findViewById(R.id.et_cvReq);

        final LinearLayout llv_cv = findViewById(R.id.llv_cv);




        tv_jobLocation = findViewById(R.id.tv_jobLocation);
        tv_skills = findViewById(R.id.tv_skills);
        tv_qualification = findViewById(R.id.tv_qualification);
        tv_salaryRange = findViewById(R.id.tv_salaryRange);
        tv_jobType = findViewById(R.id.tv_jobTypeOrMinExp);
        tv_cvReq = findViewById(R.id.tv_cvReq);
        tv_reUpload = findViewById(R.id.tv_reupload);
        tv_uploadCV=findViewById(R.id.tv_uploadCV);

        tv_apply=findViewById(R.id.tv_apply);
        tv_back = findViewById(R.id.tv_back);

        cv_apply=findViewById(R.id.card_apply);
        cv_back = findViewById(R.id.card_back);





        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Apply to Job");
        alertDialog.setMessage(R.string.Dialog_apply_job);
        alertDialog.setView(R.layout.fraud_alert_card);

        Paper.init(this);

        updateView((String)Paper.book().read("language"));
        storageReference= FirebaseStorage.getInstance().getReference();

        CVRequired=false;

        language = Paper.book().read("language");

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

        final SharedPreferences map = this.getSharedPreferences("CheckJob", Context.MODE_PRIVATE);
        applyPersonalMap = (Map<String, Object>) map.getAll();
        String jobPostedOwner = map.getString("ownerUserID","");

        try {
            final long OO = 1024 * 1024;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child("Employee/" + jobPostedOwner).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    ImageView dp = findViewById(R.id.cmp_logo);
                    Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    dp.setImageBitmap(bit);
                    System.out.println("Online se hua READ ");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("EmployeeDetailCheckJob", "Getting Image Failed" + e.getLocalizedMessage());
                        }
                    });
        } catch (Exception e1) {
            Log.e("EmpDetCheckJob", "Getting Image Failed" + e1.getLocalizedMessage());
        }





/*
        final EditText et_detail_check_job_jobTitle = findViewById(R.id.et_detail_check_job_jobTitle);
        final EditText et_detail_check_job_company = findViewById(R.id.et_detail_check_job_company);
        final EditText et_detail_check_job_state = findViewById(R.id.et_detail_check_job_state);
        final EditText et_detail_check_job_district = findViewById(R.id.et_detail_check_job_district);
        final EditText et_detail_check_job_type = findViewById(R.id.et_detail_check_job_type);
        final EditText et_detail_check_job_startingRs = findViewById(R.id.et_detail_check_job_startingRs);
        final EditText et_detail_check_job_endingRs = findViewById(R.id.et_detail_check_job_endingRs);
        final EditText et_detail_check_job_SalaryDetail = findViewById(R.id.et_detail_check_job_salaryDetails);
        //EditText et_detail_check_job_upload = findViewById(R.id.et_detail_check_job_upload);
        final EditText et_detail_check_job_contactNumber = findViewById(R.id.et_detail_check_job_contactNumber);
        final EditText et_detail_check_job_Address = findViewById(R.id.et_detail_check_job_Address);
        final TextView tv_detail_check_job_salary_period = findViewById(R.id.tv_detail_check_job_salary_period);

        //bt_employee_checkjob_uploadcv=findViewById(R.id.bt_employee_checkjob_uploadcv);




        lc_jobtitle2=findViewById(R.id.lc_jobtitle2);
        lc_jobdescription4=findViewById(R.id.lc_jobdescription4);
        lc_companyname2=findViewById(R.id.lc_companyname2);
        lc_state6=findViewById(R.id.lc_state6);
        lc_district6=findViewById(R.id.lc_district6);
        textView10=findViewById(R.id.textView10);
        textView5=findViewById(R.id.textView5);
        lc_contact3=findViewById(R.id.lc_contact3);
        lc_address7=findViewById(R.id.lc_address7);


 */






        try {

            db.collection("users").document(jobPostedOwner)
                    .collection("jobsposted")
                    .document(map.getString("documentNumber", "")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                try {
                                    Map<String, ArrayList<String>> selectedLocationSet = (Map<String, ArrayList<String>>) dataMap.get("selectedLocationSet");
                                    for (String state : selectedLocationSet.keySet()) {
                                        ArrayList<String> districtsSet = selectedLocationSet.get(state);
                                        for (String district : districtsSet) {
                                            location = location + "[" + district + ", " + state + "]\n";

                                        }
                                    }
                                }catch(Exception e){
                                    location = "Work From Home";
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
                                Context context = LocaleHelper.setLocale(getApplicationContext(),(String)Paper.book().read("language"));
                                Resources resources = context.getResources();
                                tv_jobType.setText(resources.getString(R.string.Minimum_exp));
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

                            if(dataMap.get("salaryOption")!=null){
                                changeLanguage(dataMap.get("salaryText").toString(), et_salaryRange);
                            }
                            else {


                                String startingS = dataMap.get("startingRs").toString();
                                String endingS = dataMap.get("endingRs").toString();
                                String salaryP = dataMap.get("salaryPeriod").toString();

                                String salaryDetail = startingS + " to " + endingS + " " + salaryP;
                                changeLanguage(salaryDetail, et_salaryRange);
                            }
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
                            employerEmail = st;
                        }

                        try {
                            String st = dataMap.get("qualifications").toString();
                            String preferredCourse ="";
                            if(dataMap.get("preferredCourse")!=null) {
                                preferredCourse = dataMap.get("preferredCourse").toString();
                            }
                            String fullqualification = st + "\n" + preferredCourse;
                            changeLanguage(fullqualification, et_qualification);
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

        }
        catch(Exception e){
            Log.e("CheckJobDetail", "Error getting jobDetails from Employer");
        }


try {
    db.collection("employee").document(telephone).collection("appliedJob").document(map.getString("documentNumber", "")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot snapshot,
                            @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                try {
                    Context context = LocaleHelper.setLocale(EmployeeDetailCheckJob.this, language);
                    Resources resources = context.getResources();
                    tv_apply.setText(resources.getString(R.string.Applied));
                    cv_apply.setEnabled(false);
                    cvRequireLL.setVisibility(View.GONE);
                } catch (Exception e1) {
                    Log.e("EDCJ", "Change to Aplied");
                }

                //bt_employee_checkjob_uploadcv.setEnabled(false);

            }
        }
    });
}
catch(Exception e){
    Log.e("ECJD", "Error at getting appliedJob status: "+ e.getLocalizedMessage());
}


        llv_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDFFile();
            }
        });

        cv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        try{
                            db.collection("employee").document(telephone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    applyMap = documentSnapshot.getData();
                                    applyMap.put("jobappliedID", map.getString("documentNumber", ""));
                                    final String time = Long.toString(System.currentTimeMillis());
                                    applyMap.put("AppliedtoJobTimeStamp", time);


                                    if (CVRequired && CVUrl.length() != 0) {
                                        applyMap.put("CVUrl", CVUrl);
                                    }


                                    if (CVRequired && CVUrl.length() == 0) {
                                        Toast.makeText(EmployeeDetailCheckJob.this, "Please Upload CV", Toast.LENGTH_SHORT).show();
                                    } else {


                                        try {

                                            db.collection("users").document(map.getString("ownerUserID", ""))
                                                    .collection("viewapplicants").add(applyMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        applyPersonalMap.put("jobTitle", dataMap.get("jobTitle").toString());
                                                        applyPersonalMap.put("jobDescription", dataMap.get("jobDescription").toString());
                                                        applyPersonalMap.put("company", dataMap.get("company").toString());
                                                        applyPersonalMap.put("District", dataMap.get("District").toString());
                                                        applyPersonalMap.put("jobTypeName", dataMap.get("jobTypeName").toString());
                                                        applyPersonalMap.put("jobOffered", "-1");
                                                        applyPersonalMap.put("AppliedtoJobTimeStamp", time);
                                                        final String docID = task.getResult().getId();
                                                        if (CVRequired && CVUrl.length() != 0) {
                                                            applyPersonalMap.put("CVUrl", CVUrl);
                                                        }


                                                        if (CVRequired && CVUrl.length() == 0) {
                                                            Toast.makeText(EmployeeDetailCheckJob.this, "Please Upload CV", Toast.LENGTH_SHORT).show();
                                                        } else {


                                                            db.collection("employee").document(telephone)
                                                                    .collection("appliedJob").document(map.getString("documentNumber", "")).set(applyPersonalMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (this != null)
                                                                        Toast.makeText(EmployeeDetailCheckJob.this, "Applied Successfully", Toast.LENGTH_SHORT).show();
                                                                    sendNotification("UserID", map.getString("ownerUserID", ""), "New Applicant", "New Applicant for " + dataMap.get("jobTitle").toString(), docID);
                                                                    Toast.makeText(EmployeeDetailCheckJob.this, "Applied Successfully", Toast.LENGTH_SHORT).show();

                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }catch(Exception e){
                                            Log.e("ApplytoJobButtonCLick", "Error : "+ e.getLocalizedMessage());
                                        }


                                    }
                                }
                            });
                        }
                        catch (Exception e){
                            Log.e("DetailCheckJob", "Error: "+e);
                        }
                    }
                });

                alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();
            }
        });

        final CardView card_checkJobDetails_back = findViewById(R.id.card_checkJobDetails_back);
        cv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new EmployeeCheckJob());
                ft.commit();

                 */
                finish();
            }
        });

        //return rootView;
    }

    void selectPDFFile(){
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"),1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uploadPDFFile(data.getData());
        }


    }

    private  void uploadPDFFile(Uri data){

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();


        filename=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        StorageReference reference=storageReference.child("uploads/"+applyPersonalMap.get("documentNumber")+"/"+filename+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        try {

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
                            progressDialog.dismiss();
                            CVUrl = url.toString();

                            tv_uploadCV.setText(getResources().getString(R.string.cvUploaded));
                            tv_uploadCV.setVisibility(View.VISIBLE);


                            Toast.makeText(EmployeeDetailCheckJob.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e){
                            Log.e("DetailCheckJob" , "UploadPDFFile Success");
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded: "+(int)progress+"%");

            }
        });

    }

    void changeLanguage(String text, final TextView textView){
        if(language.equalsIgnoreCase("hi")){
            //  if(false){
            final String[] temp = new String[1];
            try {
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
                                        Log.d("Emp. CheckJobDet", "LanguageTranslation: " + e.getMessage());
                                    }
                                });
            }
            catch(Exception e){
                Log.e("TranslateCJD","Error: "+ e.getLocalizedMessage());
                textView.setText(text);
            }
        }else{
            textView.setText(text);
        }
    }



    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();

        //currentHeading.setText(resources.getString(R.string.Job_Details));
        tv_jobType.setText(resources.getString(R.string.Minimum_exp));
        tv_uploadCV.setText(resources.getString(R.string.cvUploaded));
        tv_qualification.setText(resources.getString(R.string.qualifications));
        tv_jobLocation.setText(resources.getString(R.string.Job_Location));
        tv_contactNum.setText(resources.getString(R.string.Call_Now));
        tv_contactEmail.setText(resources.getString(R.string.Send_Email));
        tv_jobType.setText(resources.getString(R.string.jobType));
        tv_salaryRange.setText(resources.getString(R.string.salary));
        tv_skills.setText(resources.getString(R.string.skills));
        tv_cvReq.setText(resources.getString(R.string.cvRequired));
        tv_reUpload.setText(resources.getString(R.string.Tap_Button_To_Reupload));
        tv_apply.setText(resources.getString(R.string.Apply));
        tv_back.setText(resources.getString(R.string.back));
        alertDialog.setMessage(resources.getString(R.string.Dialog_apply_job));
        alertDialog.setTitle(resources.getString(R.string.Apply_Job));
    }


    private void sendNotification(final String key, final String value,final String title,final String message,final String id)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NzhjY2ViODItNDMzNi00NjFmLTgzZmQtZGJmMzllZjIxMzc1");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"758855f0-1bc0-4126-bca5-b7905b6eb08b\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"" + key + "\", \"relation\": \"=\", \"value\": \"" + value + "\"}],"

                                + "\"data\": {\"key\": \"employer\", \"path\": \""+id+"\"},"
                                + "\"headings\": {\"en\": \""+title+"\"},"
                                + "\"contents\": {\"en\": \""+message+"\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }



    public void callEmployer(View view){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
            return;
        }


        if(employerTelephone!=null){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
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