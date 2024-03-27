package com.up.rojgarsetu.Employee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.up.rojgarsetu.Employee.CheckJob.EmployeeAppliedJob;
import com.up.rojgarsetu.Employee.CheckJob.EmployeeCheckJob;
import com.up.rojgarsetu.Employer.MyAdapter;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.Employee.EmployeeMainActivity.currentHeading;


public class EmployeeMainFragment extends Fragment {


    ViewGroup rootView;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Bitmap> SlideShowArray = new ArrayList<Bitmap>();
    MyAdapter sliderAdapter;
    Map<String,Object> datamap=new HashMap<>();
    String telephone;
    TextView userName, userPhone;
    ArrayList<String> SliderURL = new ArrayList<>();
    CardView banner1,banner2, banner3;
    int WidthofCard;
    boolean startMovement;
    LinearLayout bannerLayout;
    ImageView imgBanner1;



    TextView title_search, title_applied, title_edit;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_employee_main, container, false);
        userName = rootView.findViewById(R.id.jobSeekerName);
        userPhone = rootView.findViewById(R.id.jobSeekerNumber);
       getActivity().findViewById(R.id.action_bar).setVisibility(View.VISIBLE);
        Activity activity = getActivity();
        if(activity != null && isAdded()) {
            Fragment_Name = getString(R.string.Home);
            currentHeading.setText(Fragment_Name);
        }
        sliderAdapter = new MyAdapter(getActivity(), SlideShowArray,SliderURL);
        title_applied = rootView.findViewById(R.id.titleEmpMainApplied);
        title_search = rootView.findViewById(R.id.titleEmpMainSearch);
        title_edit = rootView.findViewById(R.id.EditEmployeeDetails);





        //Change Language
        Paper.init(getActivity());
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));



//Firebase Initialize
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("banner").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null && snapshots !=null){
                    SliderURL.clear();
                    for(final QueryDocumentSnapshot document : snapshots){
                        Log.d("document", String.valueOf(document.getData()));
                        SliderURL.add(document.getData().get("url").toString());
                        final String id = document.getData().get("id").toString();
                        final long OO = 1024 * 1024;
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        storageRef.child("banner/" + id).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                if(bytes!=null) {
                                    try{
                                    Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    SlideShowArray.add(bit);
                                 //   Log.d("ImagesAda",SlideShowArray.size()+"");
                                    sliderAdapter.notifyDataSetChanged();
                                    //System.out.println(id+document.getData().get("url").toString());
                            }
                                    catch(Exception e){
                                        Log.e("EmployeeMainFragment", "banner mage on Success" + e.getLocalizedMessage());
                                    }

                                }

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("EmployeeMainFragment", "Getting banner Failed" + e.getLocalizedMessage()
                                        );
                                    }
                                });
                    }
                    sliderAdapter.notifyDataSetChanged();
                }
            }
        });



        CardView cv = rootView.findViewById(R.id.card_searchJob);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*

                FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployeeCheckJob());
                ft1.commit();

 */

                startActivity(new Intent(getActivity(), EmployeeCheckJob.class));


            }
        });
        CardView cv2 = rootView.findViewById(R.id.card_appliedJobs);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployeeAppliedJob());
                ft1.commit();
            }
        });
        TextView tv3 = rootView.findViewById(R.id.EditEmployeeDetails);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployeeDetailEditForm());
                ft1.commit();
            }
        });


        init();
        fillEmployeeData();


        return rootView;
    }



    @Override
    public void onDestroyView() {
        swipeTimer.cancel();
        super.onDestroyView();
    }



    //--------------------Banner Slide Show ViewPager-------------------
    Timer swipeTimer;
    private void init() {
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(sliderAdapter);

        final Handler handler = new Handler();


        final Runnable Update = new Runnable() {
            public void run() {
                if(SlideShowArray.size()>1) {
                    if (currentPage == SlideShowArray.size()) {
                        currentPage = 0;
                    }
                    mPager.setCurrentItem(currentPage++, true);
                }
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 0, 5000);
    }


    //----------------Banner Slide Show ViewPager Code ends-----------------------


    //---------------Getting user Data to display on HomePage-------------

    public void fillEmployeeData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        telephone = firebaseUser.getPhoneNumber();



                try {
                    final long OO = 1024 * 1024;
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    storageRef.child("Employee/" + telephone).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ImageView dp = rootView.findViewById(R.id.employee_homeImage);
                            Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            dp.setImageBitmap(bit);
                            //System.out.println("Online se hua READ ");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
                                 //   Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    Log.e("EmployeeMainFragment","Set Image :" + e.getMessage());
                }



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);

            final DocumentReference docRef = db.collection("employee").document(telephone);
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
                        userName.setText(datamap.get("Name").toString());
                        userPhone.setText(datamap.get("Telephone").toString());

                        if(datamap.get("jobCategoryPreference")==null){
                            setInterestedJobCategories(datamap.get("Telephone").toString());
                        }

                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

        }
    //-------------------Getting Data from Database to display on HomePage Ends-----------------




    public void updateView(String lang){
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();


        currentHeading.setText(resources.getString(R.string.Home));

        title_edit.setText(resources.getString(R.string.editDetails));
        title_search.setText(resources.getString(R.string.Search_Job));
        title_applied.setText(resources.getString(R.string.Applied_Job));


    }


    ArrayList<String> jobCategoriesPreference = new ArrayList<>();


    public void setInterestedJobCategories(final String phone){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.skill_selector_for_save,null,false);
        final LinearLayout skills_ll = view.findViewById(R.id.skill_ll);



        final AlertDialog alertDialog = alertBuilder.create();
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("JobCategories/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                skills_ll.removeAllViews();
                ArrayList<String> professionslist = (ArrayList<String>) documentSnapshot.get("jobCategories");
                for(final String professionItem : professionslist) {

                    LayoutInflater inflater2 = LayoutInflater.from(getActivity());
                    final View view2 = inflater2.inflate(R.layout.skill_selector_checkboxes_view, null, false);
                    final CheckBox cb_skill = view2.findViewById(R.id.cb_skill);
                    cb_skill.setText(professionItem);
                    cb_skill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if( isChecked && jobCategoriesPreference.size()>2){
                                cb_skill.setChecked(false);
                                Toast.makeText(getActivity(), "Only 3 items can be selected", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(isChecked) {
                                jobCategoriesPreference.add(professionItem);
                            }
                            else {
                                try {
                                    jobCategoriesPreference.remove(professionItem);
                                } catch (Exception e) {
                                }
                            }
                        }
                    });

                    skills_ll.addView(view2);
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setView(view);
        alertDialog.show();

        Button saveData = view.findViewById(R.id.button4);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> jobCategoryPreference = new HashMap<>();
                jobCategoryPreference.put("jobCategoryPreference", jobCategoriesPreference);
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage("Saving Data");
                pd.show();


                firestore.document("employee/"+phone).set(jobCategoryPreference, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                         pd.dismiss();
                         alertDialog.dismiss();
                        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }



    }









