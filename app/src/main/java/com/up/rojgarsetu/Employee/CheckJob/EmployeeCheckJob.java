package com.up.rojgarsetu.Employee.CheckJob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.paperdb.Paper;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.currentHeading;

public class EmployeeCheckJob extends AppCompatActivity {

    ProgressDialog progressDialog;
    Map<String,?> dataMap = new HashMap<>();
    Map<String,Object> userDataMap = new HashMap<>();
    ArrayList<String> jobName=new ArrayList<>();
    ArrayList<String> company=new ArrayList<>();
    ArrayList<String> jobType=new ArrayList<>();
    ArrayList<String> jobDistrict = new ArrayList<>();
    ArrayList<String> documentNumber=new ArrayList<>();
    ArrayList<String> ownerUserID=new ArrayList<>();
    ArrayList<String> jobPostedDate=new ArrayList<>();
    SearchView search_bar;
    String userState,userDistrict, userSector;


    CheckJobAdapter adapter;

    ArrayList<String> userSkills;
    ViewGroup rootView;

    Translator englishHindiTranslator;
    String language = "en";
    String userSkillText;
    Snackbar snackbar;

    Resources hindiResources;
    Context hindiContext;


    DocumentSnapshot lastdocument;
    String lastDocId, secondLastDocId;
    Query jobsQuery;

    RecyclerView rv_job_check;
    LinearLayoutManager recylerLinearLayoutManager;
    Boolean reachedMax=false;

    Snackbar statusSnackBar;

    String userDefaultDistrict, userDefaultState;
    String selectedSkillToSearch;
    CardView selectedSkillCard;
    TextView selectedSkillTextView,tv_changeSearchLocation;

    Map<String, Object> categoryImageMap;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_employee_check_job);
        search_bar=findViewById(R.id.search_bar);
        FrameLayout rootFl = findViewById(R.id.fl_checkJob);
        tv_changeSearchLocation = findViewById(R.id.tv_changeSearchLocation);
        snackbar = Snackbar.make(rootFl,"No Job Available in current Filters",Snackbar.LENGTH_INDEFINITE);
        final Activity activity = this;
        /*if(activity != null && isAdded()) {
            Fragment_Name = getString(R.string.Search_Job);
            currentHeading.setText(Fragment_Name);
        }
        
         */
        categoryImageMap = new HashMap<>();
        categoryImageMap.put("Warehouse/ Logistics", R.drawable.jc_warehouse);
        categoryImageMap.put("Manufacturing/ Operators", R.drawable.jc_operators);
        categoryImageMap.put("Housekeeping/ Helper/ Peon", R.drawable.jc_helper);
        categoryImageMap.put("Security Guard", R.drawable.jc_security);
        categoryImageMap.put("Delivery/ Collection Boy", R.drawable.jc_delivery);
        categoryImageMap.put("Driver", R.drawable.jc_driver);
        categoryImageMap.put("Cook/ Chef/ Waiter", R.drawable.jc_cook);
        categoryImageMap.put("Nurse/ Compounder/ Ward Boy", R.drawable.jc_nurse);
        categoryImageMap.put("Pharmacist/ MR", R.drawable.jc_pharmacist);
        categoryImageMap.put("Office Assistant/ Data Entry/ Receptionist", R.drawable.jc_officeassistant);
        categoryImageMap.put("BPO/ Customer Support/ Telemarketing", R.drawable.jc_telecaller);
        categoryImageMap.put("Sales/ Field Sales", R.drawable.jc_sales);
        categoryImageMap.put("Marketing Executives/ Business Development", R.drawable.jc_marketing);
        categoryImageMap.put("Account/ Cashier", R.drawable.jc_accounts);
        categoryImageMap.put("Teacher/ Tutor/ Trainer", R.drawable.jc_teacher);
        categoryImageMap.put("IT & Hardware Support / Engineering", R.drawable.jc_it);
        categoryImageMap.put("Electrician/ Plumber/ Carpenter", R.drawable.jc_labour);
        categoryImageMap.put("Tailors", R.drawable.jc_tailors);

        statusSnackBar = Snackbar.make(rootFl, "Loading Jobs", Snackbar.LENGTH_INDEFINITE);
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching for Jobs");

        //tb_all = findViewById(R.id.tb_all);

        /*tb_district = findViewById(R.id.tb_district);
        tb_state = findViewById(R.id.tb_state);
        tb_country = findViewById(R.id.tb_country);

        tb_sector = findViewById(R.id.tb_sector);
        tb_all_Sector = findViewById(R.id.tb_allSectors);

        tb_skil = findViewById(R.id.tb_skill);

        filtersLL1 = findViewById(R.id.search_ll1);
        filtersLL2 = findViewById(R.id.search_ll2);

         */








        Paper.init(this);
        language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));




        hindiContext = LocaleHelper.setLocale(this,language);
        hindiResources = hindiContext.getResources();


        selectedSkillToSearch = null;
        final CardView allSkillsCard = (CardView) findViewById(R.id.card_skillAll);
        selectedSkillCard =  allSkillsCard;
        final TextView allSkillsTextView = (TextView) findViewById(R.id.text_skillAll);
        changeLanguage("All Jobs",allSkillsTextView);
        selectedSkillTextView = allSkillsTextView;
        allSkillsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSkillCard.setBackgroundColor(Color.WHITE);
                selectedSkillTextView.setTextColor(Color.BLACK);
                selectedSkillToSearch=null;
                showJobs();
                selectedSkillCard = allSkillsCard;
                selectedSkillTextView = allSkillsTextView;
                allSkillsCard.setBackgroundColor(getResources().getColor(R.color.Orange));
                allSkillsTextView.setTextColor(Color.WHITE);
            }
        });

        final CardView seeMoreCard = findViewById(R.id.card_skillSelect);
        final TextView seeMoreTV = findViewById(R.id.tv_skillSelect);
        changeLanguage("See More",seeMoreTV);
        seeMoreCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedSkillCard.setBackgroundColor(Color.WHITE);
                selectedSkillTextView.setTextColor(Color.BLACK);

                selectedSkillCard = seeMoreCard;
                selectedSkillTextView = seeMoreTV;
                seeMoreCard.setBackgroundColor(getResources().getColor(R.color.Orange));
                seeMoreTV.setTextColor(Color.WHITE);
                seeMoreCategories();
            }
        });


        jobName = new ArrayList<>();
        company = new ArrayList<>();
        jobType = new ArrayList<>();
        jobDistrict = new ArrayList<>();
        documentNumber = new ArrayList<>();
        ownerUserID = new ArrayList<>();
        jobPostedDate = new ArrayList<>();


        rv_job_check = (RecyclerView) findViewById(R.id.rv_job_check);
        rv_job_check.setOnScrollListener(new RecyclerView.OnScrollListener() {




            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!(adapter.getItemCount()==0) && recylerLinearLayoutManager.findLastCompletelyVisibleItemPosition()==adapter.getItemCount()-1 && !reachedMax){
                    Log.e("ScrollItems", "llm: "+ recylerLinearLayoutManager.findLastCompletelyVisibleItemPosition() + " adapter : " + adapter.getItemCount());
                    executeNextBatch();

                }
            }
        });

        recylerLinearLayoutManager = new LinearLayoutManager(this);
        rv_job_check.setLayoutManager(recylerLinearLayoutManager);

        adapter = new CheckJobAdapter(jobName, company, jobType, jobDistrict, documentNumber, ownerUserID, jobPostedDate, this, hindiResources);
        rv_job_check.setAdapter(adapter);




        /*tb_skil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    skillPreference = "true";
                    tb_skil.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_skil.setTextColor(Color.WHITE);

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("skillPreference", "true");
                    filterEditInPref.commit();

                    Log.d("SearchJob", "Skill True");
                    showJobs();
                }
                else{
                    skillPreference = "false";
                    tb_skil.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_skil.setTextColor(getResources().getColor(R.color.OrangeDark));

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("skillPreference", "false");
                    filterEditInPref.commit();

                    Log.d("SearchJob", "Skill False");


                    showJobs();
                }
            }
        });




        //----------------Sector Filter Selector Initialisation---------------

        tb_all_Sector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    sectorPreference = "All Sectors";
                    tb_all_Sector.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_all_Sector.setTextColor(Color.WHITE);

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("sectorPreference", "All Sectors");
                    filterEditInPref.commit();

                    tb_sector.setChecked(false);
                    tb_sector.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_sector.setTextColor(getResources().getColor(R.color.OrangeDark));

                    Log.d("SearchJob", "Sector_AllSector");


                    showJobs();
                }
            }
        });
        tb_sector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    sectorPreference = "Sector";
                    tb_sector.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_sector.setTextColor(Color.WHITE);

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("sectorPreference", "Sector");
                    filterEditInPref.commit();

                    tb_all_Sector.setChecked(false);
                    tb_all_Sector.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_all_Sector.setTextColor(getResources().getColor(R.color.OrangeDark));

                    Log.d("SearchJob", "Sector_OnlySector");

                    showJobs();
                }
            }
        });

        //-----------------Sector Filter Selection Initialisation ends-------------------




        //------------------Location Preference Filter Initialisation Starts-----------

        tb_district.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    locationPreference = "District";
                    tb_district.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_district.setTextColor(Color.WHITE);


                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("locationPreference", "District");
                    filterEditInPref.commit();

                    tb_state.setChecked(false);
                    tb_state.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_state.setTextColor(getResources().getColor(R.color.OrangeDark));

                    tb_country.setChecked(false);
                    tb_country.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_country.setTextColor(getResources().getColor(R.color.OrangeDark));

                    Log.d("SearchJob", "Location_District");

                    showJobs();
                }
            }
        });

        tb_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    locationPreference = "State";
                    tb_state.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_state.setTextColor(Color.WHITE);

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("locationPreference", "State");
                    filterEditInPref.commit();

                    tb_district.setChecked(false);
                    tb_district.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_district.setTextColor(getResources().getColor(R.color.OrangeDark));



                    tb_country.setChecked(false);
                    tb_country.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_country.setTextColor(getResources().getColor(R.color.OrangeDark));

                    Log.d("SearchJob", "Location_State");

                    showJobs();
                }
            }
        });

        tb_country.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    locationPreference = "Country";
                    tb_country.setBackgroundResource(R.drawable.back_filled_dark_orange_0dpcorners);
                    tb_country.setTextColor(Color.WHITE);

                    SharedPreferences.Editor filterEditInPref = getSharedPreferences("SelectedFilters",MODE_PRIVATE).edit();
                    filterEditInPref.putString("locationPreference", "Country");
                    filterEditInPref.commit();

                    tb_district.setChecked(false);
                    tb_district.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_district.setTextColor(getResources().getColor(R.color.OrangeDark));

                    tb_state.setChecked(false);
                    tb_state.setBackgroundResource(R.drawable.back_filled_white_0dpcorners);
                    tb_state.setTextColor(getResources().getColor(R.color.OrangeDark));


                    Log.d("SearchJob", "Location_Country");

                    showJobs();
                }
            }
        });


         */



        Client client = new Client(getResources().getString(R.string.Algolia_App_ID), getResources().getString(R.string.Algolia_Api_Key));
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
                        if(Integer.parseInt(jsonObject1.getString("hireDone")) < Integer.parseInt(jsonObject1.getString("hirePosition"))) {
                            jobName.add(jsonObject1.getString("jobTitle"));
                            company.add(jsonObject1.getString("company"));
                            jobType.add(jsonObject1.getString("jobTypeName"));
                            documentNumber.add(jsonObject1.getString("objectID"));
                            ownerUserID.add(jsonObject1.getString("EmployerEmailid"));
                            jobDistrict.add(jsonObject1.getString("District"));
                            jobPostedDate.add(jsonObject1.getString("TimeStampMilli"));
                        }
                    }

                    if(jobName.size()==0){
                        snackbar.show();
                    }else{
                        snackbar.dismiss();

                        changeLanguage(jobName, "jobName");
                        changeLanguage(company, "company");
                        changeLanguage(jobDistrict, "jobDistrict");
                        changeLanguage(jobType, "jobType");
                        changeLanguage(jobPostedDate, "jobPostedDate");

                    }
                    adapter.notifyDataSetChanged();



                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        };

        /*search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                if(str.equalsIgnoreCase("")){
                    showJobs();

                    search_bar.clearFocus();
                    return false;
                }
              //  Toast.makeText(this, "Showing Results for "+str, Toast.LENGTH_SHORT).show();
                if (CheckNetwork()) {
                    index.searchAsync(new com.algolia.search.saas.Query(str), completionHandler);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }

        });

         */





        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String telephone = firebaseUser.getPhoneNumber();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings setting = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(setting);

        db.document("employee/"+telephone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    userDataMap = task.getResult().getData();
                    try {
                        userDistrict = userDataMap.get("Establishment District").toString();
                        userState = userDataMap.get("Establishment State").toString();
                        userSkills = (ArrayList<String>) userDataMap.get("jobCategoryPreference");
                        System.out.println(userSkills);

                        if(userSkills.get(0)!=null){
                            final TextView tv = findViewById(R.id.tv_skill1);
                            changeLanguage(userSkills.get(0),tv);
                            final CardView cv = findViewById(R.id.card_skill1);
                            cv.setVisibility(View.VISIBLE);
                            try{
                                ImageView imageView = findViewById(R.id.imv_skill1);
                                imageView.setImageResource((Integer) categoryImageMap.get(userSkills.get(0)));
                                imageView.setVisibility(View.VISIBLE);
                            }
                            catch(Exception e){}

                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    selectedSkillCard.setBackgroundColor(Color.WHITE);
                                    selectedSkillTextView.setTextColor(Color.BLACK);

                                    selectedSkillToSearch = userSkills.get(0);
                                    showJobs();

                                    selectedSkillCard = cv;
                                    cv.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    selectedSkillTextView = tv;
                                    tv.setTextColor(Color.WHITE);

                                }
                            });
                        }
                        if(userSkills.get(1)!=null){
                            final TextView tv = findViewById(R.id.tv_skill2);
                            changeLanguage(userSkills.get(1),tv);
                            final CardView cv = findViewById(R.id.card_skill2);
                            cv.setVisibility(View.VISIBLE);
                            try{
                                ImageView imageView = findViewById(R.id.imv_skill2);
                                imageView.setImageResource((Integer) categoryImageMap.get(userSkills.get(1)));
                                imageView.setVisibility(View.VISIBLE);
                            }
                            catch(Exception e){}

                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSkillCard.setBackgroundColor(Color.WHITE);
                                    selectedSkillTextView.setTextColor(Color.BLACK);

                                    selectedSkillToSearch = userSkills.get(1);
                                    showJobs();

                                    selectedSkillCard = cv;
                                    cv.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    selectedSkillTextView = tv;
                                    tv.setTextColor(Color.WHITE);
                                }
                            });
                        }
                        if(userSkills.get(2)!=null){
                            final TextView tv = findViewById(R.id.tv_skill3);
                            changeLanguage(userSkills.get(2),tv);
                            final CardView cv = findViewById(R.id.card_skill3);
                            cv.setVisibility(View.VISIBLE);

                            try{
                                ImageView imageView = findViewById(R.id.imv_skill3);
                                imageView.setImageResource((Integer) categoryImageMap.get(userSkills.get(2)));
                                imageView.setVisibility(View.VISIBLE);
                            }
                            catch(Exception e){}
                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSkillCard.setBackgroundColor(Color.WHITE);
                                    selectedSkillTextView.setTextColor(Color.BLACK);

                                    selectedSkillToSearch = userSkills.get(2);
                                    showJobs();

                                    selectedSkillCard = cv;
                                    cv.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    selectedSkillTextView = tv;
                                    tv.setTextColor(Color.WHITE);
                                }
                            });
                        }
                        if(userSkills.get(3)!=null){
                            final TextView tv = findViewById(R.id.tv_skill4);
                            changeLanguage(userSkills.get(3),tv);
                            final CardView cv = findViewById(R.id.card_skill4);
                            cv.setVisibility(View.VISIBLE);
                            try{
                                ImageView imageView = findViewById(R.id.imv_skill4);
                                imageView.setImageResource((Integer) categoryImageMap.get(userSkills.get(3)));
                                imageView.setVisibility(View.VISIBLE);
                            }
                            catch(Exception e){}

                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSkillCard.setBackgroundColor(Color.WHITE);
                                    selectedSkillTextView.setTextColor(Color.BLACK);

                                    selectedSkillToSearch = userSkills.get(3);
                                    showJobs();

                                    selectedSkillCard = cv;
                                    cv.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    selectedSkillTextView = tv;
                                    tv.setTextColor(Color.WHITE);
                                }
                            });
                        }
                        if(userSkills.get(4)!=null){
                            final TextView tv = findViewById(R.id.tv_skill5);
                            changeLanguage(userSkills.get(4),tv);
                            final CardView cv = findViewById(R.id.card_skill5);
                            try{
                                ImageView imageView = findViewById(R.id.imv_skill5);
                                imageView.setImageResource((Integer) categoryImageMap.get(userSkills.get(4)));
                                imageView.setVisibility(View.VISIBLE);
                            }
                            catch(Exception e){}
                            cv.setVisibility(View.VISIBLE);
                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSkillCard.setBackgroundColor(Color.WHITE);
                                    selectedSkillTextView.setTextColor(Color.BLACK);

                                    selectedSkillToSearch = userSkills.get(4);
                                    showJobs();

                                    selectedSkillCard = cv;
                                    cv.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    selectedSkillTextView = tv;
                                    tv.setTextColor(Color.WHITE);
                                }
                            });

                        }

                        System.out.println(userSkills);
                        System.out.println("Skill: " + userDataMap.get("Skill").toString());
                        System.out.println("State: " + userDataMap.get("Establishment State").toString());
                        System.out.println("District: " + userDataMap.get("Establishment District").toString());

                        userDefaultDistrict = userDataMap.get("Establishment District").toString();
                        userDefaultState = userDataMap.get("Establishment State").toString();

                        TextView tv_l = findViewById(R.id.tv_searchLocation);
                        tv_l.setText(userDefaultDistrict + ", "+ userDefaultState);

                        System.out.println("Sector: " + userDataMap.get("Establishment Sector").toString());
                    }
                    catch(Exception e){ }

                    showJobs();
                }
            }
        });





    }

    void clearArray(){
        jobName.clear();
        company.clear();
        jobType.clear();
        documentNumber.clear();
        ownerUserID.clear();
        jobDistrict.clear();
    }

    public void showJobs() {
        try {

            if (!CheckNetwork()) {
                return;
            }


            reachedMax=false;
            lastdocument=null;
            lastDocId=null;
            secondLastDocId=null;

            statusSnackBar.show();

            clearArray();
            adapter.notifyDataSetChanged();



            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);

            Query collectionGroup = db.collectionGroup("jobsposted");

            //String showing = "Showing ";
            //String selectedLocation = "India", selectedSector = "All Sectors", selectedSkills = userSkillText + " Jobs";

            /*


            if (skillPreference.equals("true")) {
                if (userSkills.size() != 0) {
                    collectionGroup = collectionGroup.whereArrayContainsAny("SkillArray", userSkills);
                    selectedSkills = userSkillText + " Jobs";
                }
            } else {
                selectedSkills = "All Jobs";
            }

            if (locationPreference.equals("District")) {
                collectionGroup = collectionGroup.whereEqualTo("District", userDistrict);
                selectedLocation = userDistrict;
            } else if (locationPreference.equals("State")) {
                collectionGroup = collectionGroup.whereEqualTo("State", userState);
                selectedLocation = userState;

            }

            if (sectorPreference.equals("Sector")) {
                collectionGroup = collectionGroup.whereEqualTo("Sector", userSector);
                selectedSector = userSector + " Sector";
            }

             */


            //showing = showing + selectedSkills + " in " + selectedLocation + " for " + selectedSector;

            //TextView tv = findViewById(R.id.showingStatus);
           // changeLanguage(showing, tv);


            TextView tv_searchLocation = findViewById(R.id.tv_searchLocation);
            if(selectedSkillToSearch!=null){
                collectionGroup = collectionGroup.whereEqualTo("jobCategory", selectedSkillToSearch);
            }

            if(districtToSearch==null && stateToSearch==null){
                tv_searchLocation.setText("India");
                //Search By District
            }
            else if(districtToSearch==null){
                //Search By State
                tv_searchLocation.setText(stateToSearch);
                collectionGroup = collectionGroup.whereArrayContains("SelectedStates", stateToSearch);
            }
            else{
                //Search by district
                tv_searchLocation.setText(districtToSearch + ", " + stateToSearch);
                collectionGroup = collectionGroup.whereArrayContains("SelectedDistricts", districtToSearch);
            }



            jobsQuery = collectionGroup.orderBy("TimeStampMilli", Query.Direction.DESCENDING);

            jobsQuery.limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0) {

                        clearArray();
                        int querResultSize = queryDocumentSnapshots.size();
                        List<DocumentSnapshot> listOfDocs = queryDocumentSnapshots.getDocuments();
                        lastDocId = listOfDocs.get(querResultSize-1).getId();


                        Log.e("--Pagination--", "Last Doc ID: "+ lastDocId + " Reached Max: "+ reachedMax);



                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                dataMap = document.getData();
                                lastdocument = document;

                                if (dataMap.get("jobTitle") != null && Integer.parseInt(dataMap.get("hireDone").toString()) < Integer.parseInt(dataMap.get("hirePosition").toString())) {

                                    Log.d("Job Title from All", dataMap.get("jobTitle").toString());

                                    try {
                                        jobName.add(dataMap.get("jobTitle").toString());
                                    } catch (Exception e) {
                                        jobName.add("---");
                                    }


                                    try {
                                        company.add(dataMap.get("company").toString());
                                    } catch (Exception e) {
                                        company.add("---");
                                    }
                                    try {
                                        jobType.add(dataMap.get("jobTypeName").toString());
                                    } catch (Exception e) {
                                        jobType.add("---");
                                    }

                                    try {
                                        jobDistrict.add(dataMap.get("District").toString());
                                    } catch (Exception e) {
                                        jobDistrict.add("---");
                                    }

                                    documentNumber.add(document.getId());
                                    ownerUserID.add(dataMap.get("EmployerEmailid").toString());
                                    jobPostedDate.add(dataMap.get("TimeStampMilli").toString());

                                }
                            }catch(Exception e){}
                        }
                        statusSnackBar.dismiss();
                        if(jobName.size()==0){
                            snackbar.show();
                        }else{
                            snackbar.dismiss();

                            changeLanguage(jobName, "jobName");
                            changeLanguage(company, "company");
                            changeLanguage(jobDistrict, "jobDistrict");
                            changeLanguage(jobType, "jobType");
                            changeLanguage(jobPostedDate, "jobPostedDate");
                        }


                    }
                    else{
                        snackbar.show();
                    }
                }
            });


        }catch (Exception e){
            Log.e("ShowJobs", e.getLocalizedMessage());
            //Toast.makeText(getContext(),"Connection Slow!",Toast.LENGTH_SHORT).show();
        }


    }






    public void executeNextBatch() {
        if (reachedMax) {
            return;
        }
        statusSnackBar.show();

        System.out.println("-------------------ExecutingNextBatch-------------------");

        if (lastdocument != null) {

            jobsQuery.startAfter(lastdocument).limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                    Log.e("--Pagination--", "Last Doc ID: " + lastDocId + " :: Reached Max: " + reachedMax + " :: Second Last Doc Id: " + secondLastDocId);

                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {

                        secondLastDocId = lastDocId;
                        lastDocId = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1).getId();
                        if (lastDocId.equals(secondLastDocId)) {
                            Log.e("Repeatition of Query", "RepeatedQuery");
                            if (statusSnackBar.isShown()) statusSnackBar.dismiss();
                            return;
                        }


                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                dataMap = document.getData();
                                if (dataMap.get("jobTitle") != null && Integer.parseInt(dataMap.get("hireDone").toString()) < Integer.parseInt(dataMap.get("hirePosition").toString())) {

                                    Log.d("Job Title from All", dataMap.get("jobTitle").toString());
                                    System.out.println("JobName: " + dataMap.get("jobTitle").toString());

                                    try {
                                        jobName.add(dataMap.get("jobTitle").toString());
                                    } catch (Exception e) {
                                        jobName.add("---");
                                    }


                                    try {
                                        company.add(dataMap.get("company").toString());
                                    } catch (Exception e) {
                                        company.add("---");
                                    }
                                    try {
                                        jobType.add(dataMap.get("jobTypeName").toString());
                                    } catch (Exception e) {
                                        jobType.add("---");
                                    }

                                    try {
                                        jobDistrict.add(dataMap.get("District").toString());
                                    } catch (Exception e) {
                                        jobDistrict.add("---");
                                    }

                                    documentNumber.add(document.getId());
                                    ownerUserID.add(dataMap.get("EmployerEmailid").toString());
                                    jobPostedDate.add(dataMap.get("TimeStampMilli").toString());

                                    lastdocument = document;
                                }
                            }catch (Exception e){}
                        }
                        statusSnackBar.dismiss();
                        if (jobName.size() == 0) {
                            snackbar.show();
                        } else {
                            snackbar.dismiss();

                            changeLanguage(jobName, "jobName");
                            changeLanguage(company, "company");
                            changeLanguage(jobDistrict, "jobDistrict");
                            changeLanguage(jobType, "jobType");
                            changeLanguage(jobPostedDate, "jobPostedDate");
                        }


                    } else {
                        if (statusSnackBar.isShown())
                            statusSnackBar.dismiss();
                        reachedMax = true;
                    }


                }
            });


        }
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
                                    else if(name.equalsIgnoreCase("jobPostedDate")){
                                        jobPostedDate.clear();
                                        jobPostedDate.addAll(Arrays.asList(words));
                                    }
                                    adapter.notifyDataSetChanged();

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("Emp. CheckJob", "LanguageTranslation: " +e.getMessage());
                                }
                            });


        } else {
            adapter.notifyDataSetChanged();
        }
    }

    void changeLanguage(final String text, final TextView textView) {
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

                                    Log.d("Emp. CheckJob", "LanguageTranslation: " +e.getMessage());
                                }
                            });


        } else {
            textView.setText(text);
        }
    }


    private void updateView(String lang) {

        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();

        tv_changeSearchLocation.setText(resources.getString(R.string.Change));

       /* tb_skil.setTextOff(resources.getString(R.string.skill));
        tb_district.setTextOff(resources.getString(R.string.District_without));
        tb_state.setTextOff(resources.getString(R.string.state_without));
        tb_country.setTextOff(resources.getString(R.string.Country));
        tb_sector.setTextOff(resources.getString(R.string.sector));
        tb_all_Sector.setTextOff(resources.getString(R.string.All_Sectors));

        tb_skil.setTextOn(resources.getString(R.string.skill));
        tb_district.setTextOn(resources.getString(R.string.District_without));
        tb_state.setTextOn(resources.getString(R.string.state_without));
        tb_country.setTextOn(resources.getString(R.string.Country));
        tb_sector.setTextOn(resources.getString(R.string.sector));
        tb_all_Sector.setTextOn(resources.getString(R.string.All_Sectors));

        */

    }

    boolean CheckNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Snackbar.make(rootView,"Internet Connection Lost",Snackbar.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }



    public void seeMoreCategories(){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.skill_selector_main,null,false);
        final LinearLayout skills_ll = view.findViewById(R.id.skill_ll);
        skills_ll.removeAllViews();


        final AlertDialog alertDialog = alertBuilder.create();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("JobCategories/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> professionslist = (ArrayList<String>) documentSnapshot.get("jobCategories");
                for(final String professionItem : professionslist) {
                    LayoutInflater inflater2 = LayoutInflater.from(EmployeeCheckJob.this);
                    final View view2 = inflater2.inflate(R.layout.skill_selector_view, null, false);
                    CardView card_skill_item = view2.findViewById(R.id.card_skill);
                    TextView tv_skill_item = view2.findViewById(R.id.tv_skill);
                    tv_skill_item.setText(professionItem);
                    try {

                        ImageView imv_skill = view2.findViewById(R.id.imv_skill);
                        imv_skill.setImageResource((Integer) categoryImageMap.get(professionItem));

                    }catch(Exception e){}

                    card_skill_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedSkillToSearch = professionItem;

                            alertDialog.dismiss();
                            showJobs();

                        }
                    });
                    skills_ll.addView(view2);
                }
            }
        });
        alertDialog.setView(view);
        alertDialog.show();



    }

    Map<String, Object> receivedStateMap;
    String stateToSearch=null, districtToSearch=null;

    public void changeLocation(View view){

        AlertDialog.Builder stateAlertBuilder = new AlertDialog.Builder(this);

        LayoutInflater stateInflater = LayoutInflater.from(this);
        final View statesView = stateInflater.inflate(R.layout.state_selector_main,null,false);
        final LinearLayout state_ll = statesView.findViewById(R.id.state_ll);
        state_ll.removeAllViews();

        final AlertDialog stateAlertDialog = stateAlertBuilder.create();
        stateAlertDialog.setCanceledOnTouchOutside(false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("states/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                receivedStateMap = documentSnapshot.getData();
                Set<String> stateSet = receivedStateMap.keySet();
                ArrayList<String> stateList = new ArrayList<>(stateSet);
                Collections.sort(stateList);
                stateList.add(0, "All States");

                for(final String state: stateList){
                    LayoutInflater stateItemInflater = LayoutInflater.from(EmployeeCheckJob.this);
                    final View statesItemView = stateItemInflater.inflate(R.layout.state_selector_item,null,false);
                    TextView tv = statesItemView.findViewById(R.id.tv_state_item);
                    tv.setText(state);
                    state_ll.addView(statesItemView);
                    CardView stateItemcard = statesItemView.findViewById(R.id.card_state_item);
                    stateItemcard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!state.equals("All States")) {
                                stateToSearch = state;
                                stateAlertDialog.dismiss();
                                openDistrictAlertDialog(state);
                            }
                            else{
                                stateToSearch = null;
                                districtToSearch = null;
                                stateAlertDialog.dismiss();
                                showJobs();
                            }


                        }
                    });
                }

            }
        });

        stateAlertDialog.setView(statesView);
        stateAlertDialog.show();





    }


    public void openDistrictAlertDialog(final String state){

        AlertDialog.Builder districtAlertBuilder = new AlertDialog.Builder(this);

        String districtSet =  receivedStateMap.get(state).toString();
        String[] districtArray = districtSet.split(",");
        ArrayList<String> districtList = new ArrayList<>(Arrays.asList(districtArray));
        Collections.sort(districtList);
        districtList.add(0,"All in " + state);


        LayoutInflater districtInflater = LayoutInflater.from(this);
        final View districtView = districtInflater.inflate(R.layout.state_selector_main,null,false);
        TextView viewHeading = districtView.findViewById(R.id.tv_selectorHeading);
        viewHeading.setText("Select District");
        final LinearLayout district_ll = districtView.findViewById(R.id.state_ll);
        district_ll.removeAllViews();

        final AlertDialog districtAlertDialog = districtAlertBuilder.create();
        districtAlertDialog.setCanceledOnTouchOutside(false);

        for(final String district: districtList){

            final View districtItemView = districtInflater.inflate(R.layout.state_selector_item,null,false);
            CardView card_districtItem = districtItemView.findViewById(R.id.card_state_item);
            TextView tv_districtItem = districtItemView.findViewById(R.id.tv_state_item);
            tv_districtItem.setText(district);

            card_districtItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!district.equals("All in "+state)) {
                        districtToSearch = district;
                        districtAlertDialog.dismiss();
                        showJobs();
                    }
                    else{
                        districtToSearch = null;
                        districtAlertDialog.dismiss();
                        showJobs();
                    }

                }
            });
            district_ll.addView(districtItemView);


        }
        districtAlertDialog.setView(districtView);
        districtAlertDialog.show();



    }



}