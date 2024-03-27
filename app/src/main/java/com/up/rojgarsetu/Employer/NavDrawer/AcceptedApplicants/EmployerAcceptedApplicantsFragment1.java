package com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.EmployerViewApplicantsFragment1_Model;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;


public class EmployerAcceptedApplicantsFragment1 extends AppCompatActivity {


    RecyclerView recyclerView;
    String email = "";
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<EmployerViewApplicantsFragment1_Model, MyViewHolder> adapter;
    Map<String,Object> datamap=new HashMap<>();
    String language;
    Translator englishHindiTranslator;
    TextView currentHeading;
    View v;
    Context context;
    Resources resources;
    @Override
    public void onStart() {
        super.onStart();
        if(!CheckNetwork()){
            return;
        }
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.fragment_employer_accepted_applicants1);
        getSupportActionBar().hide();
        
        if(!CheckNetwork()){
            return;
        }



        Paper.init(EmployerAcceptedApplicantsFragment1.this);
        language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");

        context = LocaleHelper.setLocale(EmployerAcceptedApplicantsFragment1.this,language);
        resources = context.getResources();

        updateView((String)Paper.book().read("language"));


        TranslatorOptions option =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(option);



        recyclerView = findViewById(R.id.rv_employer_acceptedapplicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(EmployerAcceptedApplicantsFragment1.this));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();
        firebaseFirestore= FirebaseFirestore.getInstance();
        Query query=firebaseFirestore.collection("users").document(email)
                .collection("acceptedapplicants").orderBy("TimeStampAccepted", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<EmployerViewApplicantsFragment1_Model> options = new FirestoreRecyclerOptions.Builder<EmployerViewApplicantsFragment1_Model>()
                .setQuery(query, EmployerViewApplicantsFragment1_Model.class)
                .build();

        adapter=new FirestoreRecyclerAdapter<EmployerViewApplicantsFragment1_Model, MyViewHolder>(options){


            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull EmployerViewApplicantsFragment1_Model model) {

              //  holder.tv_employer_acceptedapplicants_applicantname.setText(model.getName());
                changeLanguage(holder.tv_employer_acceptedapplicants_applicantname,model.getName());

                //<----------Fetching Job Posted details based on jobappliedID (same as ID of job posted)
                String jobappliedID = model.getJobappliedID();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                db.setFirestoreSettings(settings);

                final DocumentReference docRef = db.collection("users").document(email).collection("jobsposted")
                        .document(jobappliedID);
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
                         //   holder.tv_employer_acceptedapplicants_jobtitle.setText(datamap.get("jobTitle").toString());
                         //   holder.tv_employer_acceptedapplicants_jobtype.setText(datamap.get("jobTypeName").toString());
                         //   holder.tv_employer_acceptedapplicants_company.setText(datamap.get("company").toString());
                            changeLanguage(holder.tv_employer_acceptedapplicants_jobtitle,datamap.get("jobTitle").toString());
                            changeLanguage(holder.tv_employer_acceptedapplicants_company,datamap.get("company").toString());
                            //changeLanguage(holder.tv_employer_acceptedapplicants_jobtype,datamap.get("jobTypeName").toString());
                            try {
                                holder.tv_employer_acceptedapplicants_viewdetails.setText(resources.getString(R.string.View_Accepted_Applicants));
                                holder.tv_acceptedApplicantName.setText(resources.getString(R.string.Applicant_Name));
                            }catch(Exception e2){

                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

                //------------>

                holder.cv_employer_acceptedapplicants.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());


                        //<--------Storing jobapplicant's id in shared prefs
                        SharedPreferences.Editor editor = EmployerAcceptedApplicantsFragment1.this.getSharedPreferences("EmployerAcceptedApplicants", MODE_PRIVATE).edit();
                        editor.putString("id",snapshot.getId());
                        editor.apply();

                        startActivity(new Intent(EmployerAcceptedApplicantsFragment1.this, EmployerAcceptedApplicantsFragment2.class));


                    }
                });

            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employer_acceptedapplicants_listitem, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);


    }

    private void updateView(String language) {

       // currentHeading.setText(resources.getString(R.string.View_Accepted_Applicants));


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
                                    Toast.makeText(EmployerAcceptedApplicantsFragment1.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("Hindi", e.getMessage());
                                }
                            });


        } else {
            textView.setText(text);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_employer_acceptedapplicants_applicantname,tv_employer_acceptedapplicants_jobtitle,tv_employer_acceptedapplicants_jobtype,
                tv_employer_acceptedapplicants_company, tv_employer_acceptedapplicants_viewdetails,tv_acceptedApplicantName;
        CardView cv_employer_acceptedapplicants;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_employer_acceptedapplicants_applicantname=itemView.findViewById(R.id.tv_employer_acceptedapplicants_applicantname);
            tv_employer_acceptedapplicants_jobtitle=itemView.findViewById(R.id.tv_employer_acceptedapplicants_jobtitle);
            tv_employer_acceptedapplicants_jobtype=itemView.findViewById(R.id.tv_employer_acceptedapplicants_jobtype);
            tv_employer_acceptedapplicants_company=itemView.findViewById(R.id.tv_employer_acceptedapplicants_company);
            tv_employer_acceptedapplicants_viewdetails=itemView.findViewById(R.id.tv_employer_acceptedapplicants_viewdetails);
            cv_employer_acceptedapplicants = itemView.findViewById(R.id.cv_employer_acceptedapplicants);
            tv_acceptedApplicantName = itemView.findViewById(R.id.titleAcceptedApplicantName);
        }

    }

    boolean CheckNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            Snackbar.make(v,"Internet Connection Lost",Snackbar.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }

}