package com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
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
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerCheckJobsPostedStatus1 extends AppCompatActivity {

    RecyclerView recyclerView;
    String email = "";
    SearchView search_bar;
    private String mParam1;
    private String mParam2;
    Snackbar snackbar;
    public CheckJobsPostedStatusAdaptor Adapter;

    ProgressDialog progressDialog;
    Map<String,?> dataMap = new HashMap<>();
    ArrayList<String> snapshotID=new ArrayList<>();

    ArrayList<String> jobName=new ArrayList<>();
    ArrayList<String> company=new ArrayList<>();
    ArrayList<String> jobType=new ArrayList<>();
    TextView currentHeading;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
/*
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.fragment_employer_check_jobs_posted_status1);

        //search_bar=findViewById(R.id.search_bar);
        getSupportActionBar().hide();


        if(!CheckNetwork()){
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        updateView((String) Paper.book().read("language"));
        recyclerView = findViewById(R.id.rv_employer_checkjobsposted);
        recyclerView.setLayoutManager(new LinearLayoutManager(EmployerCheckJobsPostedStatus1.this));
        Context context = LocaleHelper.setLocale(EmployerCheckJobsPostedStatus1.this,(String) Paper.book().read("language"));
        Adapter = new CheckJobsPostedStatusAdaptor(jobName,company,jobType,snapshotID,EmployerCheckJobsPostedStatus1.this,context.getResources());
        recyclerView.setAdapter(Adapter);
        snackbar = Snackbar.make(recyclerView,"No Job Posted",Snackbar.LENGTH_INDEFINITE);

        email = firebaseUser.getEmail();


        //--------------Search---------------------

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings1 = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings1);




        db.collection("users").document(email)
                .collection("jobsposted").orderBy("TimeStampMilli", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null && snapshots!=null){
                    clearArray();
                    Log.d(TAG, "onEvent: AWESOME");
                    for (QueryDocumentSnapshot document : snapshots){
                        try {
                            dataMap = document.getData();
                            try {
                                jobName.add(dataMap.get("jobTitle").toString());
                            }catch(Exception e2){
                                jobName.add("---");
                            }

                            try{
                                company.add(dataMap.get("company").toString());
                            }
                            catch(Exception e2){
                                company.add("---");
                            }

                            //jobType.add(dataMap.get("jobTypeName").toString());
                            snapshotID.add(document.getId());
                        }catch(Exception e1){}

                    }
                    if(jobName.size()==0){
                        snackbar.show();
                    }else {
                        snackbar.dismiss();
                        Adapter.notifyDataSetChanged();
                    }
                }
            }
        });


        Client client = new Client(getString(R.string.Algolia_App_ID), getString(R.string.Algolia_Api_Key));
        final Index index = client.getIndex("jobsposted");
        JSONObject settings = null;
        try {
            settings = new JSONObject()
                    .put("searchableAttributes", "jobTitle")
                    .put("searchableAttributes", "jobDescription")
                    .put("searchableAttributes", "company")
                    .put("searchableAttributes", "State")
                    .put("searchableAttributes", "District")
                    .put("searchableAttributes", "jobTypeName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        index.setSettingsAsync(settings, null);


        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {

                try {
                    clearArray();
                    JSONArray hits=content.getJSONArray("hits");
                    for(int i=0;i<hits.length();i++){
                        JSONObject jsonObject1 =hits.getJSONObject(i);
                        jobName.add(jsonObject1.getString("jobTitle"));
                        company.add(jsonObject1.getString("company"));
                        jobType.add(jsonObject1.getString("jobTypeName"));
                        snapshotID.add(jsonObject1.getString("objectID"));

                    }

                    if(jobName.size()==0){
                        snackbar.show();
                    }else {
                        snackbar.dismiss();

                    }
                    Adapter.notifyDataSetChanged();

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        };



        /*search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                if(CheckNetwork()) {
                    Toast.makeText(EmployerCheckJobsPostedStatus1.this, "Showing Results for " + str, Toast.LENGTH_SHORT).show();
                    index.searchAsync(new com.algolia.search.saas.Query(str).setFilters("EmployerEmailid:"+email), completionHandler);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(CheckNetwork()) {
                    index.searchAsync(new com.algolia.search.saas.Query(newText).setFilters("EmployerEmailid:"+email),completionHandler);
                }
                return false;
            }
        });

         */

//-------------------------------------------------


    }



    void clearArray(){
        jobName.clear();
        company.clear();
        jobType.clear();
        snapshotID.clear();
    }

    void updateView(String lang){
        Context context = LocaleHelper.setLocale(EmployerCheckJobsPostedStatus1.this,lang);
        Resources resources = context.getResources();
        //currentHeading.setText(resources.getString(R.string.Check_Jobs_Posted_Status));
    }

    boolean CheckNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)EmployerCheckJobsPostedStatus1.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Snackbar.make(recyclerView,"Internet Connection Lost",Snackbar.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }


}