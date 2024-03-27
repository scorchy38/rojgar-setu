package com.up.rojgarsetu.Employee.CheckJob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.currentHeading;

public class EmployeeAppliedJob extends Fragment {

    ProgressDialog progressDialog;
    Map<String,?> dataMap = new HashMap<>();
    ArrayList<String> jobName=new ArrayList<>();
    ArrayList<String> company=new ArrayList<>();
    ArrayList<String> jobType=new ArrayList<>();
    ArrayList<String> jobDistrict = new ArrayList<>();
    ArrayList<String> documentNumber=new ArrayList<>();
    ArrayList<String> ownerUserID=new ArrayList<>();
    ArrayList<String> jobOffered = new ArrayList<>();
    AppliedJobAdapter adapter;
    Translator englishHindiTranslator;
    String language = "en";
    View rootView;
    Snackbar snackbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.activity_employee_applied_job, container, false);
        Paper.init(getActivity());
        language = Paper.book().read("language");
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);
        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            Fragment_Name = getString(R.string.Applied_Job);
            currentHeading.setText(Fragment_Name);
        }

        snackbar = Snackbar.make(rootView,"No Applied Job",Snackbar.LENGTH_INDEFINITE);
        Context context = LocaleHelper.setLocale(getActivity(),language);
        Resources resources = context.getResources();
        final RecyclerView rv_job_check = (RecyclerView)rootView.findViewById(R.id.rv_Applied_job_check);
        rv_job_check.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AppliedJobAdapter(jobName,company,jobType,jobDistrict, documentNumber,ownerUserID,jobOffered,getActivity(),resources);
        rv_job_check.setAdapter(adapter);

        if(!CheckNetwork()){
            return rootView;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Applied Job");
        progressDialog.show();


        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String telephone = firebaseUser.getPhoneNumber();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("employee").document(telephone)
                .collection("appliedJob").orderBy("AppliedtoJobTimeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null && snapshots!=null){
                    clearArray();
                    for (QueryDocumentSnapshot document : snapshots){
                        dataMap = document.getData();
                        jobName.add(dataMap.get("jobTitle").toString());
                        company.add(dataMap.get("company").toString());
                        jobType.add(dataMap.get("jobTypeName").toString());
                        jobDistrict.add(dataMap.get("District").toString());
                        documentNumber.add(dataMap.get("documentNumber").toString());
                        ownerUserID.add(dataMap.get("ownerUserID").toString());
                        jobOffered.add(dataMap.get("jobOffered").toString());
                    }
                    if(jobName.size()==0){
                        snackbar.show();
                    }else{
                        snackbar.dismiss();
                        changeLanguage(jobName,"jobName");
                        changeLanguage(company,"company");
                        changeLanguage(jobType,"jobType");
                        changeLanguage(jobDistrict,"jobDistrict");
                    }




                   // adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                }
            }
        });



        return rootView;
    }

    void clearArray(){
       jobName.clear();
       company.clear();;
       jobType.clear();;
       jobDistrict.clear();;
       documentNumber.clear();;
       ownerUserID.clear();;
       jobOffered.clear();;
    }

       void changeLanguage(final ArrayList<String> arrayList, final String name) {
                if (language.equalsIgnoreCase("hi")) {
                    String text = String.valueOf(arrayList);
                        englishHindiTranslator.translate(text)
                                .addOnSuccessListener(
                                        new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(@NonNull String translatedText) {
                                                translatedText = translatedText.replace("[","");
                                                translatedText = translatedText.replace("]","");
                                                String[] words = translatedText.split(", ");
                                                if(name.equalsIgnoreCase("jobName")){
                                                    jobName.clear();
                                                    jobName.addAll(Arrays.asList(words));
                                                }else if(name.equalsIgnoreCase("company")){
                                                    company.clear();
                                                    company.addAll(Arrays.asList(words));
                                                }else if(name.equalsIgnoreCase("jobDistrict")){
                                                    jobDistrict.clear();
                                                    jobDistrict.addAll(Arrays.asList(words));
                                                }else if(name.equalsIgnoreCase("jobType")){
                                                    jobType.clear();
                                                    jobType.addAll(Arrays.asList(words));
                                                }

                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d("Hindi", e.getMessage());
                                            }
                                        });


                } else {
                    adapter.notifyDataSetChanged();
                }
        }




    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();

        currentHeading.setText(resources.getString(R.string.Applied_Job));





    }

    boolean CheckNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Snackbar.make(rootView,"Internet Connection Lost",Snackbar.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }


}