package com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants;

import android.Manifest;
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
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerAcceptedApplicantsFragment2 extends AppCompatActivity {
    


    Button bt_employer_acceptedapplicants_downloadcv;

    EditText et_frag2_viewapplicants_name;
    EditText et_frag2_viewapplicants_address;
    EditText et_frag2_acceptedapplicants_skill;
    EditText et_frag2_viewapplicants_category;
    EditText et_frag2_viewapplicants_sector;
    EditText et_frag2_viewapplicants_subsector;
    EditText et_frag2_viewapplicants_state;
    EditText et_frag2_viewapplicants_district;
    EditText et_frag2_viewapplicants_workex;
    EditText et_frag2_viewapplicants_contactno;
    EditText et_qualification, et_gender;
    TextView tv_downloadCV;
    Map<String,Object> datamap=new HashMap<>();
    Map<String,Object> employeemap=new HashMap<>();

    ImageView imageView;




    TextView lc_name,lc_contact, lc_address4,lc_skill, lc_category3,
            lc_sector3,lc_subsector3,lc_state4,lc_district4,
            lc_workex,textView26;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String email="";
    String id="";

    String jobAppliedID="";
    Translator englishHindiTranslator;
    String language = "en";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.fragment_employer_accepted_applicants2);
        getSupportActionBar().hide();
        Fragment_Name = "Accepted Applicants2";

        Paper.init(EmployerAcceptedApplicantsFragment2.this);
        language = Paper.book().read("language");

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);


        et_frag2_viewapplicants_name=findViewById(R.id.et_frag2_acceptedapplicants_name);
        et_frag2_viewapplicants_address=findViewById(R.id.et_frag2_acceptedapplicants_address);
        et_frag2_acceptedapplicants_skill=findViewById(R.id.et_frag2_acceptedapplicants_skill);
        et_frag2_viewapplicants_state=findViewById(R.id.et_frag2_acceptedapplicants_state);
        et_frag2_viewapplicants_district=findViewById(R.id.et_frag2_acceptedapplicants_district);
        et_frag2_viewapplicants_workex=findViewById(R.id.et_frag2_acceptedapplicants_workex);
        et_frag2_viewapplicants_contactno=findViewById(R.id.et_frag2_et_frag2_acceptedapplicants_contactno);
        et_gender = findViewById(R.id.et_gender);
        et_qualification = findViewById(R.id.et_qualification);

        //bt_employer_acceptedapplicants_downloadcv=findViewById(R.id.bt_employer_acceptedapplicants_downloadcv);

        imageView = findViewById(R.id.imv_profilePic);


        lc_name=findViewById(R.id.lc_name);
        lc_contact=findViewById(R.id.lc_contact);
        lc_address4=findViewById(R.id.lc_address4);
        lc_skill=findViewById(R.id.lc_skill);
        lc_category3=findViewById(R.id.lc_category3);
        lc_sector3=findViewById(R.id.lc_sector3);
        lc_subsector3=findViewById(R.id.lc_subsector3);
        lc_state4=findViewById(R.id.lc_state4);
        lc_district4=findViewById(R.id.lc_district4);
        lc_workex=findViewById(R.id.lc_workex);
        textView26=findViewById(R.id.textView26);
        tv_downloadCV=findViewById(R.id.textView50);



        final LinearLayout layout_employer_acceptedapplicants_cv_require = findViewById(R.id.layout_detail_check_job_cv_require);

        updateView((String) Paper.book().read("language"));


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();

        //<----------Getting applicant's ID
        final SharedPreferences prefs = EmployerAcceptedApplicantsFragment2.this.getSharedPreferences("EmployerAcceptedApplicants", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        id=map.get("id").toString();


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference docRef = db.collection("users").document(email).collection("acceptedapplicants")
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

                    datamap = snapshot.getData();

                    if (!datamap.containsKey("CVUrl"))
                        layout_employer_acceptedapplicants_cv_require.setVisibility(View.GONE);

                    datamap=snapshot.getData();
                    try {
                        changeLanguage(datamap.get("Name").toString(), et_frag2_viewapplicants_name);
                    }catch(Exception e1){}

                    try {
                        changeLanguage(datamap.get("Telephone").toString(), et_frag2_viewapplicants_contactno);
                    }catch(Exception e1){}

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
                                        //   Toast.makeText(EmployerAcceptedApplicantsFragment2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e1) {
                        Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
                    }


                    try {
                        changeLanguage(datamap.get("Address").toString(), et_frag2_viewapplicants_address);
                    }catch(Exception e1){}

                    try{
                        changeLanguage(datamap.get("Establishment State").toString(),et_frag2_viewapplicants_state);
                    }catch(Exception e1){}

                    try {
                        changeLanguage(datamap.get("Establishment District").toString(), et_frag2_viewapplicants_district);
                    }catch(Exception e1){}

                    try {

                        changeLanguage(datamap.get("qualifications").toString(), et_qualification);
                    }
                    catch(Exception e1){}

                    try{
                        changeLanguage(datamap.get("minimumExperience").toString(),et_frag2_viewapplicants_workex);
                    }
                    catch(Exception e1){

                    }
                    try{
                        changeLanguage(datamap.get("Skill").toString(),et_frag2_acceptedapplicants_skill);
                    }
                    catch(Exception e1){

                    }
                    try{
                        changeLanguage(datamap.get("gender").toString(),et_gender);
                    }
                    catch(Exception e1){

                    }
                    jobAppliedID = datamap.get("jobappliedID").toString();


                } else {
                    Log.d(TAG, "Current data: null");
                }
            }

        });

        CardView cv = findViewById(R.id.cv_view_acceptedapplicants_back);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = EmployerAcceptedApplicantsFragment2.this.getSharedPreferences("EmployerAcceptedApplicants", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                //finish();

            }
        });


        layout_employer_acceptedapplicants_cv_require.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageReference=firebaseStorage.getInstance().getReference();
                storageReference.child("uploads").child(jobAppliedID)
                        .child(datamap.get("Telephone").toString()+".pdf")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        downloadFile(EmployerAcceptedApplicantsFragment2.this,
                                datamap.get("Telephone").toString(),
                                ".pdf",
                                DIRECTORY_DOWNLOADS,
                                uri.toString()
                        );

                        Toast.makeText(EmployerAcceptedApplicantsFragment2.this, "Downloading File!", Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });

        ImageView img_dial = findViewById(R.id.imageView14);

        img_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerAcceptedApplicantsFragment2.this);
                alertDialog.setTitle("Make a Call");
                alertDialog.setMessage("Are you sure you want to call employee?");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (ContextCompat.checkSelfPermission(EmployerAcceptedApplicantsFragment2.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EmployerAcceptedApplicantsFragment2.this, new String[]{Manifest.permission.CALL_PHONE},1);
                        }

                        else {
                            dialContactPhone(datamap.get("Telephone").toString());
                        }
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


    void downloadFile(Context context, String filename, String fileextension, String destinationDirectory, String url){

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,filename+fileextension);
        downloadManager.enqueue(request);

    }

    private void dialContactPhone(final String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EmployerAcceptedApplicantsFragment2.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    dialContactPhone(datamap.get("Telephone").toString());
                } else {
                    Toast.makeText(EmployerAcceptedApplicantsFragment2.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    void changeLanguage(String text, final EditText editText){
        if(language.equalsIgnoreCase("hi")){
      //  if(false){
            final String[] temp = new String[1];
            englishHindiTranslator.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String translatedText) {
                                    temp[0] = translatedText;
                                    try {
                                        editText.setText(temp[0]);
                                    }catch(Exception e){
                                        //Toast.makeText(EmployerAcceptedApplicantsFragment2.this, "Error : "+ temp[0] , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployerAcceptedApplicantsFragment2.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    Log.d("Hindi",e.getMessage());
                                }
                            });
        }else{
            editText.setText(text);
        }
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(EmployerAcceptedApplicantsFragment2.this,lang);
        Resources resources = context.getResources();


        lc_name.setText(resources.getText(R.string.Name_without));
        lc_contact.setText(resources.getText(R.string.Contact_Number_without));
        lc_address4.setText(resources.getText(R.string.Address_without));
        lc_skill.setText(resources.getText(R.string.skill));
        lc_category3.setText(resources.getText(R.string.Establishment_Category_without));
        lc_sector3.setText(resources.getText(R.string.establishment_sector));
        lc_subsector3.setText(resources.getText(R.string.establishment_sub_sector));
        lc_state4.setText(resources.getText(R.string.state_without));
        lc_district4.setText(resources.getText(R.string.District_without));
        lc_workex.setText(resources.getText(R.string.work_experience_without));
        textView26.setText(resources.getText(R.string.back));

        tv_downloadCV.setText(resources.getString(R.string.Download_CV));


    }


}