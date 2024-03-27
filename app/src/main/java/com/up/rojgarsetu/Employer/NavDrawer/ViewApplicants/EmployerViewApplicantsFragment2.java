package com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.DownloadManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants.EmployerAcceptedApplicantsFragment1;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerViewApplicantsFragment2 extends AppCompatActivity {

    

    Button bt_frag2_viewapplicants_accept;
    Button bt_frag2_viewapplicants_reject;
    Button bt_frag2_viewapplicants_back;
    Button bt_frag2_viewapplicants_contact;
    Button bt_employer_viewapplicants_downloadcv;

    EditText et_frag2_viewapplicants_name;
    EditText et_frag2_viewapplicants_address;
    EditText et_frag2_viewapplicants_skill;
    EditText et_frag2_viewapplicants_category;
    EditText et_frag2_viewapplicants_sector;
    EditText et_frag2_viewapplicants_subsector;
    EditText et_frag2_viewapplicants_state;
    EditText et_frag2_viewapplicants_district;
    EditText et_frag2_viewapplicants_workex;
    EditText et_frag2_viewapplicants_contactno;
    EditText et_qualification, et_gender;
    Map<String,Object> datamap=new HashMap<>();
    Map<String,Object> employeemap=new HashMap<>();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ImageView imageView;


    TextView lc_name2,lc_contact2,lc_address5,lc_skill2,lc_category4,
            lc_sector4,lc_subsector4,lc_state5,lc_district5,
            lc_workex2,textView31,textView32,textView28,
            currentHeading,tv_dowloadCV, tv_qualification, tv_gender;

    LinearLayout ll_downloadCV;

    String email="";
    String id="";

    String jobAppliedID="";
    Translator englishHindiTranslator;
    String language = "en";

    public EmployerViewApplicantsFragment2() {
        // Required empty public constructor
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.fragment_employer_view_applicants2);
        getSupportActionBar().hide();
        


        bt_employer_viewapplicants_downloadcv=findViewById(R.id.bt_employer_viewapplicants_downloadcv);

        final LinearLayout layout_employer_viewapplicants_cv_require = findViewById(R.id.layout_employer_viewapplicants_cv_require);

        Paper.init(EmployerViewApplicantsFragment2.this);
        language = Paper.book().read("language");

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);


        et_frag2_viewapplicants_name=findViewById(R.id.et_frag2_viewapplicants_name);
        et_frag2_viewapplicants_address=findViewById(R.id.et_frag2_viewapplicants_address);
        et_frag2_viewapplicants_category=findViewById(R.id.et_frag2_viewapplicants_category);
        et_frag2_viewapplicants_skill=findViewById(R.id.et_frag2_viewapplicants_skill);
        et_frag2_viewapplicants_sector=findViewById(R.id.et_frag2_viewapplicants_sector);
        et_frag2_viewapplicants_subsector=findViewById(R.id.et_frag2_viewapplicants_subsector);
        et_frag2_viewapplicants_state=findViewById(R.id.et_frag2_viewapplicants_state);
        et_frag2_viewapplicants_district=findViewById(R.id.et_frag2_viewapplicants_district);
        et_frag2_viewapplicants_workex=findViewById(R.id.et_frag2_viewapplicants_workex);
        et_frag2_viewapplicants_contactno=findViewById(R.id.et_frag2_viewapplicants_contactno);
        et_qualification=findViewById(R.id.et_qualification);
        et_gender=findViewById(R.id.et_gender);
        ll_downloadCV=findViewById(R.id.bt_employee_checkjob_uploadcv_ll);

        imageView = findViewById(R.id.imv_profilePic);


        tv_dowloadCV=findViewById(R.id.textView50);
        lc_name2=findViewById(R.id.lc_name2);
        lc_contact2=findViewById(R.id.lc_contact2);
        lc_address5=findViewById(R.id.lc_address5);
        lc_skill2=findViewById(R.id.lc_skill2);
        tv_gender=findViewById(R.id.lc_gender);
        lc_category4=findViewById(R.id.lc_category4);
        lc_sector4=findViewById(R.id.lc_sector4);
        lc_subsector4=findViewById(R.id.lc_subsector4);
        lc_state5=findViewById(R.id.lc_state5);
        lc_district5=findViewById(R.id.lc_district5);
        lc_workex2=findViewById(R.id.lc_workex2);
        textView31=findViewById(R.id.textView31);
        textView32=findViewById(R.id.textView32);
        textView28=findViewById(R.id.textView28);
        tv_qualification=findViewById(R.id.tv_qualification);




        updateView((String) Paper.book().read("language"));




        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();

        //<----------Getting applicant's ID
        try {
            final SharedPreferences prefs = EmployerViewApplicantsFragment2.this.getSharedPreferences("EmployerViewApplicants", MODE_PRIVATE);
            Map<String, ?> map = prefs.getAll();
            id = map.get("id").toString();
            prefs.edit().clear().commit();
        }
        catch(Exception e){
            id = "---";
        }


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference docRef = db.collection("users").document(email).collection("viewapplicants")
                .document(id);
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
                    try {
                        changeLanguage(datamap.get("Name").toString(), et_frag2_viewapplicants_name);
                    }catch(Exception e1){
                        changeLanguage("---", et_frag2_viewapplicants_name);
                    }

                    try {
                        changeLanguage(datamap.get("Telephone").toString(), et_frag2_viewapplicants_contactno);
                    }catch(Exception e1){
                        changeLanguage("---", et_frag2_viewapplicants_contactno);
                    }

                    try {
                        final long OO = 1024 * 1024;
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        storageRef.child("Employee/" + datamap.get("Telephone").toString()).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                                        //   Toast.makeText(EmployerViewApplicantsFragment2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e1) {
                        Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
                    }


                    try {
                        changeLanguage(datamap.get("Address").toString(), et_frag2_viewapplicants_address);
                    }catch(Exception e1){
                        changeLanguage("---", et_frag2_viewapplicants_address);
                    }

                    try{
                        changeLanguage(datamap.get("Establishment State").toString(),et_frag2_viewapplicants_state);
                    }catch(Exception e1){
                        changeLanguage("---",et_frag2_viewapplicants_state);
                    }

                    try {
                        changeLanguage(datamap.get("Establishment District").toString(), et_frag2_viewapplicants_district);
                    }catch(Exception e1){
                        changeLanguage("---", et_frag2_viewapplicants_district);
                    }

                    try {

                        changeLanguage(datamap.get("qualifications").toString(), et_qualification);
                    }
                    catch(Exception e1){
                        changeLanguage("---", et_qualification);
                    }

                    try{
                    changeLanguage(datamap.get("minimumExperience").toString(),et_frag2_viewapplicants_workex);
                    }
                    catch(Exception e1){
                        changeLanguage("---",et_frag2_viewapplicants_workex);

                    }
                    try{
                        changeLanguage(datamap.get("Skill").toString(),et_frag2_viewapplicants_skill);
                    }
                    catch(Exception e1){
                        changeLanguage("---",et_frag2_viewapplicants_skill);
                    }
                    try{
                        changeLanguage(datamap.get("gender").toString(),et_gender);
                    }
                    catch(Exception e1){
                        changeLanguage("---",et_gender);

                    }



                    //et_frag2_viewapplicants_workex.setText(datamap.get("Experience").toString());

                    //ID of the Job to which applicant applied
                    jobAppliedID=datamap.get("jobappliedID").toString();

                    if(!datamap.containsKey("CVUrl"))
                        ll_downloadCV.setVisibility(View.GONE);



                    //<-------------Employee map---------------

                    db.collection("employee").document(datamap.get("Telephone").toString())
                            .collection("appliedJob").document(jobAppliedID)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists()) {
                                        Log.d(TAG, "Current data: " + snapshot.getData());
                                        employeemap=snapshot.getData();



                                    } else {
                                        Log.d(TAG, "Current data: null");
                                    }
                                }
                            });



                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });



        ll_downloadCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageReference=firebaseStorage.getInstance().getReference();
                storageReference.child("uploads").child(jobAppliedID)
                        .child(datamap.get("Telephone").toString()+".pdf")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        downloadFile(EmployerViewApplicantsFragment2.this,
                                datamap.get("Telephone").toString(),
                                ".pdf",
                                DIRECTORY_DOWNLOADS,
                                uri.toString()
                        );

                        Toast.makeText(EmployerViewApplicantsFragment2.this, "Downloading File!", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        ImageView img_dial = findViewById(R.id.imageView13);

        img_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerViewApplicantsFragment2.this);
                alertDialog.setTitle("Make a Call");
                alertDialog.setMessage("Are you sure you want to call employee?");

                alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (ContextCompat.checkSelfPermission(EmployerViewApplicantsFragment2.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EmployerViewApplicantsFragment2.this, new String[]{Manifest.permission.CALL_PHONE},1);
                        }

                        else {
                            dialContactPhone(datamap.get("Telephone").toString());
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

        final ProgressDialog progressDialog = new ProgressDialog(EmployerViewApplicantsFragment2.this);
        progressDialog.setMessage("Accepting...");
        CardView card_accept_viewApplicantDet = findViewById(R.id.cv_viewapplicantDet_accept);

        card_accept_viewApplicantDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerViewApplicantsFragment2.this);
                alertDialog.setTitle("Accept Job Application");
                alertDialog.setMessage("Are you sure you want to accept this job application?");

                alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();

                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        db.setFirestoreSettings(settings);

                        //<---------updating jobOffered of employee to 1

                        employeemap.put("jobOffered","1");

                        db.collection("employee").document(datamap.get("Telephone").toString())
                                .collection("appliedJob").document(jobAppliedID)
                                .set(employeemap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                        db.collection("users").document(email).collection("jobsposted")
                                .document(jobAppliedID)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    final Map<String,Object> tempMap = documentSnapshot.getData();
                                    final int hireDone = Integer.parseInt(tempMap.get("hireDone").toString())+1;
                                    tempMap.put("hireDone",hireDone+"");
                                    db.collection("users").document(email).collection("jobsposted")
                                            .document(jobAppliedID).set(tempMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("hireDone", String.valueOf(hireDone));

                                        }
                                    });
                                    addToAlgolia(tempMap,"jobsposted",documentSnapshot.getId());
                                }
                            }
                        });








                        //<----------Adding applicant to acceptedapplicants collection of employer

                        String acceptedTimeStamp = Long.toString(System.currentTimeMillis());
                        datamap.put("TimeStampAccepted", acceptedTimeStamp);
                        db.collection("users").document(email).collection("acceptedapplicants")
                                .add(datamap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        sendNotification("UserID",datamap.get("Telephone").toString(),"Job Update","You got a Job",jobAppliedID,email);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });


                        //<------------------------Deleting applicant from view applicants

                        db.collection("users").document(email).collection("viewapplicants").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        SharedPreferences.Editor editor = EmployerViewApplicantsFragment2.this.getSharedPreferences("EmployerViewApplicants", MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.apply();
                                        progressDialog.dismiss();


                                        startActivity(new Intent(EmployerViewApplicantsFragment2.this, EmployerAcceptedApplicantsFragment1.class));
                                        Toast.makeText(EmployerViewApplicantsFragment2.this, "Job Application Accepted Successfully", Toast.LENGTH_SHORT).show();


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

                alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();



            }
        });

        final ProgressDialog progressDialogReject = new ProgressDialog(EmployerViewApplicantsFragment2.this);
        progressDialogReject.setMessage("Rejecting...");

        CardView cv_reject_viewApplicantDet = findViewById(R.id.cv_viewApplicantDet_Reject);
        cv_reject_viewApplicantDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialogReject.show();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerViewApplicantsFragment2.this);
                alertDialog.setTitle("Reject Job Application");
                alertDialog.setMessage("Are you sure you want to reject this job application?");

                alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        //<---------updating jobOffered of employee to 0

                        employeemap.put("jobOffered","0");

                        db.collection("employee").document(datamap.get("Telephone").toString())
                                .collection("appliedJob").document(jobAppliedID)
                                .set(employeemap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                        //<------------------------Deleting applicant from view applicants

                        db.collection("users").document(email).collection("viewapplicants").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EmployerViewApplicantsFragment2.this, "Job Application Rejected Sucessfully", Toast.LENGTH_SHORT).show();
                                        sendNotification("UserID",datamap.get("Telephone").toString(),"Job Update","Sorry You Job request is rejected",jobAppliedID,email);
                                        SharedPreferences.Editor editor = EmployerViewApplicantsFragment2.this.getSharedPreferences("EmployerViewApplicants", MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.apply();
                                        progressDialogReject.dismiss();
                                        /*FragmentTransaction ft = EmployerViewApplicantsFragment2.this.getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.fl_main_activity_container, new EmployerViewApplicantsFragment1());
                                        ft.commit();
                                        
                                         */


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialogReject.dismiss();
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });



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
        CardView cv_back_viewApplicantDet = findViewById(R.id.cv_viewApplicantDet_Back);
        cv_back_viewApplicantDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = EmployerViewApplicantsFragment2.this.getSharedPreferences("EmployerViewApplicants", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                finish();

                /*FragmentTransaction ft = EmployerViewApplicantsFragment2.this.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container, new EmployerViewApplicantsFragment1());
                ft.commit();
                
                 */

            }
        });



    }

    void addToAlgolia(Map<String, ?> map, String record, String objectID){

        Client client = new Client("70MTPNC113", "8ed017407978b5156d367a5d2360a84e");

        final Index index = client.getIndex(record);
        final List<JSONObject> array = new ArrayList<JSONObject>();
        try {
            array.add(new JSONObject(map).put("objectID", objectID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        index.addObjectsAsync(new JSONArray(array), null);

    }

    private void dialContactPhone(final String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));


    }


    void downloadFile(Context context, String filename, String fileextension, String destinationDirectory, String url){

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,filename+fileextension);
        downloadManager.enqueue(request);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EmployerViewApplicantsFragment2.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    dialContactPhone(datamap.get("Telephone").toString());
                } else {
                    Toast.makeText(EmployerViewApplicantsFragment2.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    void changeLanguage(String text, final EditText editText){
        if(language.equalsIgnoreCase("hi")){
            //    if(false){
            final String[] temp = new String[1];
            englishHindiTranslator.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String translatedText) {
                                    temp[0] = translatedText;
                                    editText.setText(temp[0]);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployerViewApplicantsFragment2.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    Log.d("Hindi",e.getMessage());
                                }
                            });
        }else{
            editText.setText(text);
        }
    }


    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(EmployerViewApplicantsFragment2.this,lang);
        Resources resources = context.getResources();

        //currentHeading.setText(resources.getText(R.string.View_Applicant_Details));
        lc_name2.setText(resources.getText(R.string.Name_without));
        lc_contact2.setText(resources.getText(R.string.Contact_Number_without));
        lc_address5.setText(resources.getText(R.string.Address_without));
        lc_skill2.setText(resources.getText(R.string.skill));
        lc_category4.setText(resources.getText(R.string.Establishment_Category_without));
        lc_sector4.setText(resources.getText(R.string.establishment_sector));
        lc_subsector4.setText(resources.getText(R.string.establishment_sub_sector));
        lc_state5.setText(resources.getText(R.string.state_without));
        lc_district5.setText(resources.getText(R.string.District_without));
        lc_workex2.setText("Work Experience");
        textView28.setText(resources.getText(R.string.Accept));
        textView31.setText(resources.getText(R.string.Reject));
        textView32.setText(resources.getText(R.string.back));
        tv_dowloadCV.setText(resources.getText(R.string.Download_CV));

    }
    private void sendNotification(final String key, final String value,final String title, final String message,final String path, final String id)
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

                                + "\"data\": {\"key\": \"employee\", \"path\": \""+path+"\",\"id\": \""+id+"\"},"
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
}