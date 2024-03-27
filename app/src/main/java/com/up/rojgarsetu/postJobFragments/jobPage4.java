package com.up.rojgarsetu.postJobFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.paperdb.Paper;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.up.rojgarsetu.Employer.EmployerMainFragment;
import com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus.EmployerCheckJobsPostedStatus1;
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
import java.util.Set;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employer.EmployerMainFragment.userSector;
import static com.up.rojgarsetu.postJobFragments.PostJobActivity.postJobFragmentName;
import static com.up.rojgarsetu.postJobFragments.jobPage3.selectedProfession;
import static com.up.rojgarsetu.postJobFragments.jobPage2.selectedDataset;


public class jobPage4 extends Fragment {

    ProgressDialog progressDialog;
    TextView textView4,textView11;

    TextView tv_confirmText, tv_jobTitle, tv_jobDescription, tv_companyname,
            tv_contactName, tv_contactNum, tv_contactEmail, tv_selectedStates, tv_selectedDistricts,
            tv_salaryRange, tv_hirePosition, tv_cvReq, tv_qualification, tv_minExpReq, tv_skillsReq;

    EditText et_jobTitle, et_jobDescription, et_companyName,
            et_contactName, et_contactNum, et_contactNumVisibility, et_contactEmail,
            et_selectedStates, et_selectedDistricts, et_salaryRange, et_hirePosition,
            et_cvReq, et_qualification, et_minExpReq, et_skillsReq;

    String employerEmailId;
    String districtText = "", firstDistrict="";
    int countOfStates = 0, countOfDistrictSet = 0;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ArrayList<String> selectedDistricts, selectedStates;

    Map<String, ArrayList<String>> selectedLocationSet = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.job_page_4,container,false);
        //Retrieving from SharedPref

        postJobFragmentName = "jobpage4";


        final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);

        progressDialog = new ProgressDialog(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        employerEmailId = firebaseUser.getEmail();



        tv_confirmText = rootView.findViewById(R.id.confirmText);
        tv_jobTitle = rootView.findViewById(R.id.tv_jobTitle);
        tv_jobDescription = rootView.findViewById(R.id.tv_jobDescription);
        tv_companyname = rootView.findViewById(R.id.tv_companyName);
        tv_contactName = rootView.findViewById(R.id.tv_contactName);
        tv_contactNum = rootView.findViewById(R.id.tv_contactNum);
        tv_contactEmail = rootView.findViewById(R.id.tv_contactEmail);
        tv_selectedDistricts = rootView.findViewById(R.id.tv_selectedDistricts);
        tv_selectedStates = rootView.findViewById(R.id.tv_selectedStates);
        tv_salaryRange = rootView.findViewById(R.id.tv_salaryRange);
        tv_hirePosition = rootView.findViewById(R.id.tv_hirePosition);
        tv_cvReq = rootView.findViewById(R.id.tv_cvReq);
        tv_qualification = rootView.findViewById(R.id.tv_qualification);
        tv_minExpReq = rootView.findViewById(R.id.tv_minExpReq);
        tv_skillsReq = rootView.findViewById(R.id.tv_skillsReq);


        et_jobTitle = rootView.findViewById(R.id.et_jobTitle);
        et_jobDescription = rootView.findViewById(R.id.et_jobDescription);
        et_companyName = rootView.findViewById(R.id.et_companyName);
        et_contactName = rootView.findViewById(R.id.et_contactName);
        et_contactNum = rootView.findViewById(R.id.et_contactNum);
        et_contactNumVisibility = rootView.findViewById(R.id.et_contactNumVisibility);
        et_contactEmail = rootView.findViewById(R.id.et_contactEmail);
        et_selectedDistricts = rootView.findViewById(R.id.et_selectedDistricts);
        et_selectedStates = rootView.findViewById(R.id.et_selectedStates);
        et_salaryRange = rootView.findViewById(R.id.et_salaryRange);
        et_hirePosition = rootView.findViewById(R.id.et_hirePosition);
        et_cvReq = rootView.findViewById(R.id.et_cvReq);
        et_qualification = rootView.findViewById(R.id.et_qualification);
        et_minExpReq = rootView.findViewById(R.id.et_minExpReq);
        et_skillsReq = rootView.findViewById(R.id.et_skillsReq);




        final CardView card_back_frag4 = rootView.findViewById(R.id.card_back_frag4);
        final CardView card_submit_frag4 = rootView.findViewById(R.id.card_submit_frag4);


        textView4 = rootView.findViewById(R.id.textView4);
        textView11 = rootView.findViewById(R.id.textView11);

        updateView((String) Paper.book().read("language"));



        et_jobTitle.setText(prefs.getString("jobTitle",""));
        et_jobDescription.setText(prefs.getString("jobDescription",""));
        et_companyName.setText(prefs.getString("company",""));
        et_contactName.setText(prefs.getString("contactPersonName",""));
        et_contactNum.setText(prefs.getString("contactNumber",""));
        et_contactNumVisibility.setText(prefs.getString("contactNumberVisibility",""));
        et_contactEmail.setText(prefs.getString("contactEmail",""));


        Boolean wfhStatus = prefs.getBoolean("WorkFromHome", true);


        if(!wfhStatus) {
            Set<String> states = selectedDataset.keySet();
            countOfStates = states.size();
            selectedDistricts = new ArrayList<>();
            for (String state : states) {
                Set<String> districtSet = selectedDataset.get(state);
                stateText = stateText + state + "\n";
                countOfDistrictSet = districtSet.size();
                ArrayList<String> thisStateDistricts = new ArrayList<>(districtSet);
                selectedDistricts.addAll(thisStateDistricts);
                selectedLocationSet.put(state, thisStateDistricts);

                for(String district: districtSet){
                    districtText = districtText + ""+district + ",  " + state + "\n";
                }


            }
            selectedStates = new ArrayList<>(states);

            String st_states = selectedStates.toString();
            String st_districts = selectedDistricts.toString();
            et_selectedStates.setText(st_states);
            et_selectedDistricts.setText(st_districts);
        }
        else{
            districtText = "Work From Home";
            stateText = "Work From Home";
            et_selectedStates.setText("Work From Home");
            et_selectedDistricts.setText("Work From Home");
        }

        et_cvReq.setText(prefs.getString("CvRequired",""));
        et_hirePosition.setText(prefs.getString("hirePosition",""));
        et_minExpReq.setText(prefs.getString("minExpReq",""));
        et_skillsReq.setText(prefs.getString("Skills",""));
        et_qualification.setText(prefs.getString("qualifications",""));
        et_salaryRange.setText(prefs.getString("salaryText",""));



        //Uploading to Firebase and deleting SharedPref

        card_submit_frag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------Press Effect--------

                card_submit_frag4.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_submit_frag4.setElevation(5);
                    }
                },200);

                //------Press Effect Ends--------

                progressDialog.setMessage("Posting Job");
                progressDialog.show();


                if(selectedDistricts!=null) {
                    for (String dist : selectedDistricts) {
                        sendNotification("District", dist, "New Job", "Job posted in your District");
                    }
                }


                updateCloudDatabase();
            }
        });

        card_back_frag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------Press Effect--------

                card_back_frag4.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_back_frag4.setElevation(5);
                    }
                },200);

                //------Press Effect Ends--------
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_postjob,new jobPage3());
                ft.commit();
            }
        });


        return rootView;
    }
    String stateText="";

    void updateCloudDatabase(){



        //<------------important addition don't deletee
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
        editor.putString("EmployerEmailid", employerEmailId);
        editor.putString("hireDone","0");
        editor.putString("numberOfApplicants","0");
        editor.apply();
        //---------------->

        final SharedPreferences prefs = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE);
        final Map<String, Object> map = (Map<String, Object>) prefs.getAll();
        map.put("SkillArray", selectedProfession);
        map.put("SelectedStates", selectedStates);
        map.put("SelectedDistricts", selectedDistricts);
        map.put("selectedLocationSet", selectedLocationSet);
        map.put("jobTypeName", "---");
        map.put("District", districtText);
        map.put("State", stateText);
        map.put("address", " ");
        if(countOfDistrictSet==1 && countOfStates ==1){
            map.put("District", selectedDistricts.get(0));
            map.put("State", selectedStates.get(0));
        }


        Timestamp timestamp = Timestamp.now();
        map.put("TimeStamp", timestamp);

        String timeStampMili = Long.toString(System.currentTimeMillis());
        map.put("TimeStampMilli", timeStampMili);



        firestore.collection("users").document(employerEmailId).collection("jobsposted")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(),"Job Post Submitted Successfully",Toast.LENGTH_SHORT).show();
                        prefs.edit().clear().commit();
                        progressDialog.dismiss();

                        //------------- Adding to jobsposted record in Algolia
                        //addToAlgolia(map,"jobsposted",documentReference.getId());
                        //-----------------------------------------

                        /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fl_postjob,new EmployerMainFragment());
                        ft.commit();

                         */
                        getActivity().finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Job Post was Not Submitted",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();



        tv_confirmText.setText(resources.getString(R.string.confirmText));
        tv_jobTitle.setText(resources.getString(R.string.Job_Title_without));
        tv_jobDescription.setText(resources.getString(R.string.Job_Description_without));
        tv_companyname.setText(resources.getString(R.string.company_without));

        tv_salaryRange.setText(resources.getString(R.string.Salary_without));
        tv_hirePosition.setText(resources.getString(R.string.hirePosition_without));
        tv_cvReq.setText(resources.getString(R.string.Receive_CV_without));
        tv_contactNum.setText(resources.getString(R.string.Contact_Number_without));

        tv_contactName.setText(resources.getString(R.string.contactPersonName));
        tv_contactEmail.setText(resources.getString(R.string.email_id));
        tv_qualification.setText(resources.getString(R.string.qualifications));
        tv_skillsReq.setText(resources.getString(R.string.SkillsRequired));


        textView4.setText(resources.getString(R.string.back));
        textView11.setText(resources.getString(R.string.submit));

    }

    void addToAlgolia(Map<String, ?> map, String record, String objectID){

        Client client = new Client(getResources().getString(R.string.Algolia_App_ID), getResources().getString(R.string.Algolia_Api_Key));

        final Index index = client.getIndex(record);
        final List<JSONObject> array = new ArrayList<JSONObject>();
        try {
            array.add(new JSONObject(map).put("objectID", objectID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        index.addObjectsAsync(new JSONArray(array), null);

    }

    private void sendNotification(final String key, final String value,final String title,final String message)
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

                                + "\"data\": {\"foo\": \"bar\"},"
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
