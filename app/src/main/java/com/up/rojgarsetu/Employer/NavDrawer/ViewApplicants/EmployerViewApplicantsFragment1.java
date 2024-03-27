package com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employer.EmployerMainFragment;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Employer.NavDrawer.About.EmployerAboutFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants.EmployerAcceptedApplicantsFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus.EmployerCheckJobsPostedStatus1;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.MainActivity;
import com.up.rojgarsetu.R;
import com.up.rojgarsetu.postJobFragments.jobPage1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;

public class EmployerViewApplicantsFragment1 extends AppCompatActivity  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    String email = "";
    private FirebaseFirestore firebaseFirestore;
    ViewApplicantsAdapter adapter;
    Map<String,Object> datamap=new HashMap<>();
    String language;
    Translator englishHindiTranslator;
    TextView currentHeading;
    Context context;
    Resources resources;

    TextView tv_changeJob, tv_showingJob;

    Map<String, String> mapOfJobs;
    ArrayList<String> listOfJobs;

    ArrayList<String> jobTitle = new ArrayList<>();
    ArrayList<String> company = new ArrayList<>();
    ArrayList<String> applicantName = new ArrayList<>();
    ArrayList<String> documentId = new ArrayList<>();

    //NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.fragment_employer_view_applicants1);
        getSupportActionBar().hide();
        Paper.init(EmployerViewApplicantsFragment1.this);
        language = Paper.book().read("language");

        //navigationView = findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        if(!CheckNetwork()){
            return;
        }
        context = LocaleHelper.setLocale(EmployerViewApplicantsFragment1.this,language);
        resources = context.getResources();
        TranslatorOptions option =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(option);


        tv_changeJob = findViewById(R.id.tv_changeJob);
        tv_showingJob = findViewById(R.id.tv_showingJob);



        updateView((String)Paper.book().read("language"));

        recyclerView = findViewById(R.id.rv_employer_viewapplicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(EmployerViewApplicantsFragment1.this));
        adapter = new ViewApplicantsAdapter(company, jobTitle, applicantName, documentId, this, resources);
        recyclerView.setAdapter(adapter);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();



        firebaseFirestore= FirebaseFirestore.getInstance();
        showApplicantsByJob("All Jobs");


        tv_changeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EmployerViewApplicantsFragment1.this);
                final AlertDialog alertDialog = alertBuilder.create();
                final LayoutInflater inflater = LayoutInflater.from(EmployerViewApplicantsFragment1.this);
                View view = inflater.inflate(R.layout.card_job_selector,null,false);

                final LinearLayout ll = view.findViewById(R.id.ll_jobs_forInflate);
                ll.removeAllViews();

                firebaseFirestore.collection("users/"+email + "/jobsposted").orderBy("TimeStampMilli", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mapOfJobs= new HashMap<>();

                        for(final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            mapOfJobs.put(documentSnapshot.get("jobTitle").toString(), documentSnapshot.getId());
                        }
                        listOfJobs = new ArrayList<>(mapOfJobs.keySet());
                        listOfJobs.add(0,"All Jobs");
                        mapOfJobs.put("All Jobs", "All Jobs");
                        for(final String jobInList : listOfJobs) {

                            LayoutInflater inflater2 = LayoutInflater.from(EmployerViewApplicantsFragment1.this);
                            View view2 = inflater2.inflate(R.layout.job_recycler_item, null, false);
                            TextView tv = view2.findViewById(R.id.tv_jobName);
                            tv.setText(jobInList);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {

                                        showApplicantsByJob(mapOfJobs.get(jobInList));
                                        tv_showingJob.setText("Showing Applicants for " + jobInList);
                                        alertDialog.dismiss();


                                    } catch (Exception e) {
                                    }
                                }
                            });
                            ll.addView(view2);
                        }
                        }

                });

                alertDialog.setView(view);
                alertDialog.show();



            }
        });
        

    }

    void changeLanguage(final TextView textView, final String text) {
        if (language.equalsIgnoreCase("hi")) {

            englishHindiTranslator.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String translatedText) {
                                    textView.setText(translatedText);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployerViewApplicantsFragment1.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("Hindi", e.getMessage());
                                }
                            });


        } else {
            textView.setText(text);
        }
    }
    


    private void updateView(String language) {

        //currentHeading.setText(resources.getString(R.string.View_Applicants));


    }

    boolean CheckNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)EmployerViewApplicantsFragment1.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Snackbar.make(recyclerView,"Internet Connection Lost",Snackbar.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }

    public void clearArray(){
        jobTitle.clear();
        company.clear();
        applicantName.clear();
        documentId.clear();
    }


    public void showApplicantsByJob(String jobId){

        //System.out.println("Showing Job ID::::: " + jobId);



        clearArray();



        Query query = firebaseFirestore.collection("users/"+email + "/viewapplicants");

        if(!jobId.equals("All Jobs")){
            Log.e("ViewApplicants", "Showing by ID");
            query = query.whereEqualTo("jobappliedID", jobId);
        }
        query = query.orderBy("AppliedtoJobTimeStamp", Query.Direction.DESCENDING);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("ViewApplicants", "Got Doc");
                for(final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if(documentSnapshot!=null){
                        try {

                            String jobAppliedID = documentSnapshot.get("jobappliedID").toString();

                            firebaseFirestore.document("users/"+email+ "/jobsposted/" + jobAppliedID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshotJob, @Nullable FirebaseFirestoreException e) {

                                    try {
                                        jobTitle.add(documentSnapshotJob.get("jobTitle").toString());
                                        company.add(documentSnapshotJob.get("company").toString());
                                        applicantName.add(documentSnapshot.get("Name").toString());
                                        documentId.add(documentSnapshot.getId());
                                        adapter.notifyDataSetChanged();

                                    }
                                    catch (Exception e2){

                                    }



                                }
                            });


                        }
                        catch(Exception e){
                            Log.e("ViewApplicants", "Error filling adapter" + e.getMessage());

                        }
                    }
                }


            }
        });


    }




}