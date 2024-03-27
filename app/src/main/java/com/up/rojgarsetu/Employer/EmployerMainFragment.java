package com.up.rojgarsetu.Employer;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.up.rojgarsetu.Employer.NavDrawer.About.EmployerAboutFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants.EmployerAcceptedApplicantsFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus.EmployerCheckJobsPostedStatus1;
import com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus.EmployerCheckJobsPostedStatus2;
import com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.EmployerViewApplicantsFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.EmployerViewApplicantsFragment2;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.MainActivity;
import com.up.rojgarsetu.R;
import com.up.rojgarsetu.postJobFragments.PostJobActivity;
import com.up.rojgarsetu.postJobFragments.jobPage1;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;
import static com.up.rojgarsetu.MainActivity.Fragment_Name;
public class EmployerMainFragment extends Fragment {



    private static ViewPager mPager;
    private static int currentPage = 0;
    MyAdapter sliderAdapter;
    private ArrayList<Bitmap> SlideShowArray = new ArrayList<Bitmap>();
    ArrayList<String> SliderURL = new ArrayList<>();
    ViewGroup rootView;
    Map<String,Object> datamap=new HashMap<>();
    String email;
    TextView establishmentname, establishmentAddress, jobsPosted, newApplicants, acceptedApplicantsCount;

    TextView titlejobsPostedCount, titleApplicantsCount, titleAcceptedCount,
            titleJobsPostedButton, titlePostNewJob, titleViewApplicants, titleViewAcceptedApplicants,editDet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CardView banner1,banner2, banner3;
    int WidthofCard;
    boolean startMovement;
    LinearLayout bannerLayout;
    ImageView imgBanner1;
    TextView currentHeading;
    public static String userSector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= (ViewGroup) inflater.inflate(R.layout.fragment_employer_main, container, false);
        currentHeading = getActivity().findViewById(R.id.currentHeading);
        currentHeading.setText(getString(R.string.Home));
        Fragment_Name = getString(R.string.Home);

        establishmentname = rootView.findViewById(R.id.establishmentName);
        establishmentAddress = rootView.findViewById(R.id.establishmentAddress);
        jobsPosted = rootView.findViewById(R.id.jobsPosted);
        newApplicants = rootView.findViewById(R.id.newApplicants);
        acceptedApplicantsCount = rootView.findViewById(R.id.acceptedApplicantscount);

        titleApplicantsCount = rootView.findViewById(R.id.titleERMainapplicants);
        titlejobsPostedCount = rootView.findViewById(R.id.titleErMainjobsPosted);
        titleAcceptedCount = rootView.findViewById(R.id.titleERMainAccepted);
        titleJobsPostedButton = rootView.findViewById(R.id.titleERMainjobsPostedButton);
        titlePostNewJob = rootView.findViewById(R.id.titleERMainpostNewJob);
        titleViewApplicants = rootView.findViewById(R.id.titleERMainViewApplicants);
        titleViewAcceptedApplicants = rootView.findViewById(R.id.titleERMainviewAcceptedApplicants);
        editDet = rootView.findViewById(R.id.textView2);



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
                    for(QueryDocumentSnapshot document : snapshots){
                        SliderURL.add(document.getData().get("url").toString());
                        String id = document.getData().get("id").toString();
                        final long OO = 1024 * 1024;
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        storageRef.child("banner/" + id).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                if(bytes!=null) {
                                    Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    SlideShowArray.add(bit);
                                    sliderAdapter.notifyDataSetChanged();
                                    //System.out.println("Online se hua READ ");
                                }
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("EmployeeMainFragment", "Getting Image Failed" + e.getLocalizedMessage()
                                        );
                                    }
                                });
                    }

                }
            }
        });








        CardView cv = rootView.findViewById(R.id.main_card_jobsPosted);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EmployerCheckJobsPostedStatus1.class));
            }
        });
        CardView cv2 = rootView.findViewById(R.id.card_postNewJob);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostJobActivity.class));
            }
        });
        CardView cv3 = rootView.findViewById(R.id.card_viewApplications);
        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new EmployerViewApplicantsFragment1());
                ft.commit();

                 */
                startActivity(new Intent(getActivity(), EmployerViewApplicantsFragment1.class));
            }
        });
        CardView cv4 = rootView.findViewById(R.id.card_viewAcceptedApplications);
        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EmployerAcceptedApplicantsFragment1.class));
            }
        });
        TextView tv5 = rootView.findViewById(R.id.textView2);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new EmployerAboutFragment1());
                ft.commit();
            }
        });

        init(); // Initialise Banner Slider

        fillEmployerData();

        return rootView;
    }



    @Override
    public void onDestroyView() {
        swipeTimer.cancel();
        super.onDestroyView();
    }


    //-----------------Banner Images ViewPager --------------------------
    Timer swipeTimer;

    private void init() {
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        sliderAdapter = new MyAdapter(getActivity(), SlideShowArray,SliderURL);
        mPager.setAdapter(sliderAdapter);

        final Handler handler = new Handler();


        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == SlideShowArray.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
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

    //-----------------Banner Images ViewPager Ends --------------------------




    //------------------Fill HomePage Data from Database-------------------------

    public void fillEmployerData(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();


//-------------------Read image from Cloud---------------------------

                try {
                    final long OO = 1024 * 1024;
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    storageRef.child("Employee/" + email).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ImageView dp = rootView.findViewById(R.id.imageView11);
                            Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            dp.setImageBitmap(bit);
                            //System.out.println("Online se hua READ ");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("EmployerMainFragment", "Getting Image Failed" + e.getLocalizedMessage());
                                }
                            });
                } catch (Exception e) {
                    Log.e("EmployerMainFragment", "Getting Image Failed" + e.getLocalizedMessage());

                }


        //-------------------Read image from Cloud Ends---------------------------



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference docRef = db.collection("users").document(email);
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
                    UpdateFields();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }


    public void UpdateFields(){
        try{
            establishmentname.setText(datamap.get("frag1_Full Name of Factory/Establishment").toString());
        }
        catch(Exception e){
            establishmentname.setText(datamap.get("organisationName").toString());
        }
        try{
            establishmentAddress.setText(datamap.get("frag1_Establishment Address").toString());
        }
        catch(Exception e){
            establishmentAddress.setText(datamap.get("employerEmailId").toString());
        }
        //userSector = datamap.get("frag1_Establishment Sector").toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final int[] countJobsPosted = new int[1];
        db.collection("users/"+email+"/jobsposted").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    countJobsPosted[0] = queryDocumentSnapshots.size();
                    jobsPosted.setText(Integer.toString(countJobsPosted[0]));
                }
                catch(Exception e1){}
            }
        });


        final int[] accepteedapplicantscount = new int[1];
        db.collection("users/"+email+"/acceptedapplicants").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    accepteedapplicantscount[0] = queryDocumentSnapshots.size();
                    acceptedApplicantsCount.setText(Integer.toString(accepteedapplicantscount[0]));
                }
                catch(Exception e1){}
            }
        });


        final int[] viewapplicantscount = new int[1];
        db.collection("users/"+email+"/viewapplicants").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    viewapplicantscount[0] = queryDocumentSnapshots.size();
                    newApplicants.setText(Integer.toString(viewapplicantscount[0]));
                }
                catch(Exception e1){}
            }
        });

    }


    //------------------Fill HomePage Dta from Database Ends-------------------------



    private void updateView(String language) {

        Context context = LocaleHelper.setLocale(getActivity(),language);
        Resources resources = context.getResources();
        currentHeading.setText(resources.getString(R.string.Home));

        titlejobsPostedCount.setText(resources.getText(R.string.Jobs_Posted));
        titleApplicantsCount.setText(resources.getText(R.string.Applicants));
        titleAcceptedCount.setText(resources.getText(R.string.Accepted));
        titleJobsPostedButton.setText(resources.getText(R.string.Jobs_Posted));
        titlePostNewJob.setText(resources.getText(R.string.Post_New_Job));
        titleViewApplicants.setText(resources.getText(R.string.View_Applicants));
        titleViewAcceptedApplicants.setText(resources.getText(R.string.View_Accepted_Applicants));
        editDet.setText(resources.getText(R.string.editDetails));

    }



}