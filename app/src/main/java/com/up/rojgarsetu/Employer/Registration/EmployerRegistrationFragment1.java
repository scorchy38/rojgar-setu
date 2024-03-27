package com.up.rojgarsetu.Employer.Registration;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.paperdb.Paper;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.Employer.Registration.EmployerRegistrationFragment2.managerMobile;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerRegistrationFragment1 extends Fragment{



    TextView lc_employerreg2, lc_orgdetails,lc_factname, lc_address, lc_category,
            lc_sector, lc_subsector, lc_state, lc_district, lc_pincode,lc_phone,
            lc_websiteurl,lc_status;
    Activity ac_name;

    public EmployerRegistrationFragment1() {


    }
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_employer_registration_fragment1, container, false);

        final CardView card_frag2 = v.findViewById(R.id.card_fragment2);
        final CardView card_frag2_prev = v.findViewById(R.id.card_frag2_prev);
        final CardView card_frag2_next = v.findViewById(R.id.card_frag2_next);

        SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        final Map<String, ?> dataMap = prefs.getAll();


        //Button bt_employer_next=v.findViewById(R.id.bt_employer_next);
        //Button bt_employer_previous=v.findViewById(R.id.bt_employer_previous);
        final EditText et_frag1_fullnameoffactory=v.findViewById(R.id.et_frag1_fullnameoffactory);
        final EditText et_frag1_address=v.findViewById(R.id.et_frag1_address);
        final EditText et_frag1_pincode=v.findViewById(R.id.et_frag1_pinocde);
        final EditText et_frag1_telephone=v.findViewById(R.id.et_frag1_telephone);
        final EditText et_frag1_websiteurl=v.findViewById(R.id.et_frag1_websiteurl);

        lc_employerreg2=v.findViewById(R.id.lc_employerreg2);
        lc_orgdetails=v.findViewById(R.id.lc_orgdetails);
        lc_factname=v.findViewById(R.id.lc_factname);
        lc_address=v.findViewById(R.id.lc_address);
        lc_category=v.findViewById(R.id.lc_category);
        lc_sector=v.findViewById(R.id.lc_sector);
        lc_subsector=v.findViewById(R.id.lc_subsector);
        lc_state=v.findViewById(R.id.lc_state);
        lc_district=v.findViewById(R.id.lc_district);
        lc_pincode=v.findViewById(R.id.lc_pincode);
        lc_phone=v.findViewById(R.id.lc_phone);
        lc_websiteurl=v.findViewById(R.id.lc_websiteurl);
        lc_status=v.findViewById(R.id.lc_status);


        ac_name=getActivity();

        updateView((String) Paper.book().read("language"));


        et_frag1_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag1_address.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag1_address.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag1_fullnameoffactory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag1_fullnameoffactory.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag1_fullnameoffactory.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag1_pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag1_pincode.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag1_pincode.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag1_telephone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag1_telephone.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag1_telephone.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });
        et_frag1_websiteurl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag1_websiteurl.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag1_websiteurl.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final Spinner spinner_establishment_category=v.findViewById(R.id.spinner_establishment_category);
        final ArrayList<String> categoryArray = new ArrayList<>();
        categoryArray.add(0,"Select");
        final ArrayAdapter<CharSequence> adaptorCategory=new ArrayAdapter(getActivity(),R.layout.spinner_style,categoryArray);

        db.collection("category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if(task.isSuccessful()){
                  Map<String,?> map;
                  categoryArray.clear();
                  categoryArray.add(0,"Select");
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      map = document.getData();
                      List<String> temp = ((List<String>)map.get(String.valueOf(map.keySet()).replace("[","").replace("]","")));
                        categoryArray.add(temp.get(1));
                  }
                  adaptorCategory.notifyDataSetChanged();
                  if(dataMap.get("frag1_Establishment Category_pos")!=null)
                  spinner_establishment_category.setSelection(Integer.valueOf(dataMap.get("frag1_Establishment Category_pos").toString()));
              }
            }
        });



        adaptorCategory.setDropDownViewResource(R.layout.spinner_style);
        spinner_establishment_category.setAdapter(adaptorCategory);
        spinner_establishment_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_establishment_category.setBackgroundResource(R.drawable.back_grey_round_corners);

                if(et_frag1_address.hasFocus())
                    et_frag1_address.clearFocus();
                if(et_frag1_fullnameoffactory.hasFocus())
                    et_frag1_fullnameoffactory.clearFocus();
                if(et_frag1_pincode.hasFocus())
                    et_frag1_pincode.clearFocus();
                if(et_frag1_telephone.hasFocus())
                    et_frag1_telephone.clearFocus();
                if(et_frag1_websiteurl.hasFocus())
                    et_frag1_websiteurl.clearFocus();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final Spinner spinner_establishment_sector=v.findViewById(R.id.spinner_establishment_sector);
        final ArrayList<String> sectorArray = new ArrayList<>();
        final Spinner spinner_establishment_subsector=v.findViewById(R.id.spinner_establishment_subsector);
        final ArrayList<String> subSectorArray = new ArrayList();
        subSectorArray.add(0,"Select");
        final ArrayAdapter adaptorSector=new ArrayAdapter(getActivity(),R.layout.spinner_style,sectorArray);
        sectorArray.add(0,"Select");
        final ArrayAdapter subSectorAdaptor=new ArrayAdapter(getActivity(),R.layout.spinner_style, subSectorArray);
        adaptorSector.setDropDownViewResource(R.layout.spinner_style);
        db.collection("sector").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        sectorArray.add(document.getId());
                    }
                    adaptorSector.notifyDataSetChanged();
                    if(dataMap.get("frag1_Establishment Sector_pos")!=null)
                    spinner_establishment_sector.setSelection(Integer.valueOf(dataMap.get("frag1_Establishment Sector_pos").toString()));
                }
            }
        });
        spinner_establishment_sector.setAdapter(adaptorSector);
        spinner_establishment_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    spinner_establishment_sector.setBackgroundResource(R.drawable.back_grey_round_corners);
                    db.document("sector/" + sectorArray.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                subSectorArray.clear();
                                subSectorArray.add(0,"Select");
                                List<String> temp = (List<String>) task.getResult().getData().get("English");
                                subSectorArray.addAll(temp);
                                subSectorAdaptor.notifyDataSetChanged();

                                if(dataMap.get("frag1_Establishment SubSector_pos")!=null)
                                    spinner_establishment_subsector.setSelection(Integer.valueOf(dataMap.get("frag1_Establishment SubSector_pos").toString()));
                                else
                                    spinner_establishment_subsector.setSelection(0);
                            }


                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        subSectorAdaptor.setDropDownViewResource(R.layout.spinner_style);
        spinner_establishment_subsector.setAdapter(subSectorAdaptor);
        spinner_establishment_subsector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_establishment_subsector.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner spinner_establishment_state=v.findViewById(R.id.spinner_state);
        final ArrayList<String> states= new ArrayList<>();
        states.add(0,"Select");
        final ArrayAdapter stateAdaptor= new ArrayAdapter(getActivity(),R.layout.spinner_style,states);
        stateAdaptor.setDropDownViewResource(R.layout.spinner_style);
        db.document("states/English").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    Map<String, ?> map = task.getResult().getData();
                    states.clear();
                    for (String m : map.keySet()) {
                        states.add(m);
                    }
                    Collections.sort(states);
                    states.add(0, "Select");
                    stateAdaptor.notifyDataSetChanged();
                    if (dataMap.get("frag1_Establishment State_pos") != null)
                        spinner_establishment_state.setSelection(Integer.valueOf(dataMap.get("frag1_Establishment State_pos").toString()));
                }
                catch(Exception e){
                    Toast.makeText(ac_name, "Check Connection! Unable to load States.", Toast.LENGTH_SHORT).show();
                }

        }
        });
        final ArrayList<String> districtArray = new ArrayList<>();
        districtArray.add(0,"Select");
        final ArrayAdapter districtAdaptor= new ArrayAdapter(getActivity(),R.layout.spinner_style,districtArray);
        final Spinner spinner_establishment_district=v.findViewById(R.id.spinner_district);
        spinner_establishment_state.setAdapter(stateAdaptor);
        spinner_establishment_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                spinner_establishment_state.setBackgroundResource(R.drawable.back_grey_round_corners);
                if(position!=0) {
                    db.document("states/English").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String districtFetch = task.getResult().getData().get(states.get(position)).toString();
                            districtArray.clear();
                            Collections.addAll(districtArray, districtFetch.split(","));
                            Collections.sort(districtArray);
                            districtArray.add(0,"Select");
                            districtAdaptor.notifyDataSetChanged();
                            if(dataMap.get("frag1_Establishment District_pos")!=null)
                                spinner_establishment_district.setSelection(Integer.valueOf(dataMap.get("frag1_Establishment District_pos").toString()));
                            else
                                spinner_establishment_district.setSelection(0);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtAdaptor.setDropDownViewResource(R.layout.spinner_style);
        spinner_establishment_district.setAdapter(districtAdaptor);
        spinner_establishment_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_establishment_district.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final Spinner spinner_establishment_status=v.findViewById(R.id.spinner_establishment_status);
        ArrayAdapter adaptor=ArrayAdapter.createFromResource(getContext(),R.array.establishment_status,R.layout.spinner_style);
        adaptor.setDropDownViewResource(R.layout.spinner_style);
        spinner_establishment_status.setAdapter(adaptor);
        spinner_establishment_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_establishment_status.setBackgroundResource(R.drawable.back_grey_round_corners);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if(dataMap.containsKey("frag1_Full Name of Factory/Establishment")) {
            et_frag1_fullnameoffactory.setText(dataMap.get("frag1_Full Name of Factory/Establishment").toString());
            et_frag1_address.setText(dataMap.get("frag1_Establishment Address").toString());
            et_frag1_telephone.setText(dataMap.get("frag1_Telephone").toString());
            spinner_establishment_status.setSelection(Integer.valueOf(dataMap.get("frag1_Status_pos").toString()));

            et_frag1_pincode.setText(dataMap.get("frag1_Pin Code").toString());
            et_frag1_websiteurl.setText(dataMap.get("frag1_Website URL").toString());
        }
        card_frag2_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(et_frag1_fullnameoffactory.getText().toString().length()==0 ||
                        et_frag1_address.getText().toString().length()==0 ||
                        et_frag1_telephone.getText().toString().length()==0 ||
                        spinner_establishment_category.getItemAtPosition(spinner_establishment_category.getSelectedItemPosition()).toString().equals("Select")||
                        spinner_establishment_sector.getItemAtPosition(spinner_establishment_sector.getSelectedItemPosition()).toString().equals("Select") ||
                        spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select") ||
                        spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select") ||
                        spinner_establishment_status.getItemAtPosition(spinner_establishment_status.getSelectedItemPosition()).toString().equals("Select")){

                    Toast.makeText(getActivity(), "All Fields marked (*) should be filled", Toast.LENGTH_SHORT).show();
                    if(et_frag1_fullnameoffactory.getText().toString().length()==0){
                        et_frag1_fullnameoffactory.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag1_address.getText().toString().length()==0){
                        et_frag1_address.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(et_frag1_telephone.getText().toString().length()==0){
                        et_frag1_telephone.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_establishment_category.getItemAtPosition(spinner_establishment_category.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_establishment_category.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_establishment_sector.getItemAtPosition(spinner_establishment_sector.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_establishment_sector.setBackgroundResource(R.drawable.back_red_round_corners);
                    }

                    if(spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString().equals("Select")){
                        spinner_establishment_state.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString().equals("Select") ){
                        spinner_establishment_district.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(spinner_establishment_status.getItemAtPosition(spinner_establishment_status.getSelectedItemPosition()).toString().equals("Select") ){
                        spinner_establishment_status.setBackgroundResource(R.drawable.back_red_round_corners);
                    }


                }

                else if(et_frag1_websiteurl.getText().toString().length()!=0 && !isValidURL(et_frag1_websiteurl.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter Valid Website URL", Toast.LENGTH_SHORT).show();
                    et_frag1_websiteurl.setBackgroundResource(R.drawable.back_red_round_corners);
                }
                else if(et_frag1_telephone.getText().toString().length()!=10){
                    Toast.makeText(getActivity(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                    et_frag1_telephone.setBackgroundResource(R.drawable.back_red_round_corners);
                }
                else if(et_frag1_pincode.getText().toString().length()!=0 && et_frag1_pincode.getText().toString().length()!=6){
                    Toast.makeText(getActivity(), "Please Enter Valid Pin Code", Toast.LENGTH_SHORT).show();
                    et_frag1_pincode.setBackgroundResource(R.drawable.back_red_round_corners);
                }

                else{



                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();



                    editor.putString("frag1_Establishment Category", spinner_establishment_category.getItemAtPosition(spinner_establishment_category.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Establishment Category_pos", spinner_establishment_category.getSelectedItemPosition()+"");

                    editor.putString("frag1_Establishment Sector", spinner_establishment_sector.getItemAtPosition(spinner_establishment_sector.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Establishment Sector_pos", spinner_establishment_sector.getSelectedItemPosition()+"");

                    editor.putString("frag1_Establishment SubSector", spinner_establishment_subsector.getItemAtPosition(spinner_establishment_subsector.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Establishment SubSector_pos", spinner_establishment_subsector.getSelectedItemPosition()+"");

                    editor.putString("frag1_Establishment State", spinner_establishment_state.getItemAtPosition(spinner_establishment_state.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Establishment State_pos", spinner_establishment_state.getSelectedItemPosition()+"");

                    editor.putString("frag1_Establishment District", spinner_establishment_district.getItemAtPosition(spinner_establishment_district.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Establishment District_pos", spinner_establishment_district.getSelectedItemPosition()+"");

                    editor.putString("frag1_Status", spinner_establishment_status.getItemAtPosition(spinner_establishment_status.getSelectedItemPosition()).toString());
                    editor.putString("frag1_Status_pos", spinner_establishment_status.getSelectedItemPosition()+"");





                    editor.putString("frag1_Full Name of Factory/Establishment", et_frag1_fullnameoffactory.getText().toString());
                    editor.putString("frag1_Establishment Address", et_frag1_address.getText().toString());
                    editor.putString("frag1_Telephone", et_frag1_telephone.getText().toString());


                    if(et_frag1_pincode.getText().toString().length()!=0)
                        editor.putString("frag1_Pin Code", et_frag1_pincode.getText().toString());
                    else
                        editor.putString("frag1_Pin Code", "");



                    if(et_frag1_websiteurl.getText().toString().length()!=0)
                        editor.putString("frag1_Website URL", et_frag1_websiteurl.getText().toString());
                    else
                        editor.putString("frag1_Website URL","");



                    editor.apply();




                    ObjectAnimator animator = ObjectAnimator.ofFloat(card_frag2_next, "translationX", 1800);
                    animator.setDuration(300);
                    animator.start();
                    animator = ObjectAnimator.ofFloat(card_frag2, "translationX", -1800);
                    animator.setDuration(300);
                    animator.start();



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fl_employer_registration_activity,new EmployerRegistrationFragment3());
                            ft.commit();
                        }
                    },300);
                }


            }
        });

        card_frag2_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {


                ConstraintLayout cl = v.findViewById(R.id.cl_next_frag2);
                int y = cl.getWidth();
                int x = card_frag2_next.getWidth();
                float w = y - x - (cl.getPaddingRight() + cl.getPaddingLeft());

                ObjectAnimator animator = ObjectAnimator.ofFloat(card_frag2_prev, "translationX", w / 2);
                animator.setDuration(500);
                animator.start();

                animator = ObjectAnimator.ofFloat(card_frag2_next, "translationX", -w / 2);
                animator.setDuration(500);
                animator.start();

                animator = ObjectAnimator.ofFloat(card_frag2, "translationX", 1800);
                animator.setDuration(500);
                animator.start();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fl_employer_registration_activity, new EmployerRegistrationFragment2());
                            ft.commit();
                        }
                        catch(Exception e){}
                    }
                },450);

            }
        });


        LinearLayout uploadLogoLL = v.findViewById(R.id.uploadLogoLL);
        uploadLogoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic(v);
            }
        });

        return v;
    }

    boolean isValidURL(String potentialUrl){

        return  Patterns.WEB_URL.matcher(potentialUrl).matches();
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();

        lc_employerreg2.setText(resources.getString(R.string.employer_registration));
        lc_orgdetails.setText(resources.getString(R.string.Organisation_Details));
        lc_factname.setText(resources.getString(R.string.Full_Name_Establishment));
        lc_address.setText(resources.getString(R.string.Establishment_Address));
        lc_category.setText(resources.getString(R.string.Establishment_Category));
        lc_sector.setText(resources.getString(R.string.Establishment_Sector));
        lc_subsector.setText(resources.getString(R.string.Establishment_Sub_Sector));
        lc_state.setText(resources.getString(R.string.State));
        lc_district.setText(resources.getString(R.string.District));
        lc_pincode.setText(resources.getString(R.string.Pin_Code));
        lc_websiteurl.setText(resources.getString(R.string.Website));
        lc_status.setText(resources.getString(R.string.status_unit));

    }






    //--------------------Image Reading, Compression and Upload-----------------------


    //public static Uri uri;


    //@Override
    /*public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d("ActivityResult", "Request Code "+ requestCode + "Result Code "+ requestCode);



        //---------------------Profile Picture Reading & Cropping-------------------



            if (requestCode == 1) {

                Log.d("Activity Result", "in requestCode1");

                try {
                    currImageURI = data.getData();

                    String imgDecodableString = getRealPathFromURI(currImageURI);
                    if (imgDecodableString == null) {
                        Log.d("EmployerReg", "Image unDecodable");
                        return;
                    }
                    try {
                        String path = compressImage(imgDecodableString);
                        Uri file = Uri.fromFile(new File(path));
                        if(file!=null) {
                            uri = file;
                            ImageView img = v.findViewById(R.id.uploadLogoIM);
                            img.setImageURI(uri);
                            CropImage.activity(uri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setBorderLineColor(Color.RED)
                                    .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                                    .start(regActivity);

                        }
                        else{
                            Log.d("Compression Result", "Got NULL URI");
                            uri = currImageURI;
                            ImageView img = v.findViewById(R.id.uploadLogoIM);
                            img.setImageURI(uri);
                            CropImage.activity(uri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setBorderLineColor(Color.RED)
                                    .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                                    .start(regActivity);
                        }

                    }
                    catch(Exception e){
                        Log.d("EmployerReg", "Compression Failed");
                        uri = currImageURI;
                        ImageView img = v.findViewById(R.id.uploadLogoIM);
                        img.setImageURI(uri);
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setBorderLineColor(Color.RED)
                                .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                                .start(regActivity);
                    }

                }catch(Exception e){
                    Log.d("EmployerReg", "Image Crop Failed");
                }



        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                  if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                      Log.d("CropInRegistration","into Error if for Cropping");
                    ImageView img = v.findViewById(R.id.uploadLogoIM);
                    img.setImageURI(uri);

                }
                else  {
                    Uri resultUri = result.getUri();
                    uri = resultUri;
                      Log.d("CropInRegistration","into else for Cropping Result");
                    ImageView img = v.findViewById(R.id.uploadLogoIM);
                    img.setImageURI(resultUri);


                }
            }
        }
        catch(Exception e){
            Log.d("EmployerReg", "Crop Failed");

            if(uri!=null){
                ImageView img = v.findViewById(R.id.uploadLogoIM);
                img.setImageURI(uri);
            }
            else{
                Log.d("Crop Activity Result", "URI NULL");
            }
        }


        //------------------Profile Picture Reading & Cropping Activity Results ends----------------------------------------------------------



        super.onActivityResult(requestCode, resultCode, data);

    }

     */




    public void takePic(View v) {
        context=getActivity();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            //takePic(v);
            return;
        }
        Log.d("TakePic",  " starting intent");
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    private static final int CAMERA_REQUEST = 1;

    Uri currImageURI;



    //----------------Logo Image Processing-------------------------

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        String[] projection = { MediaStore.Images.Media.DATA };
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }





    private Context context;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;



    public String compressImage( String imagePath)
    {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName=managerMobile+".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        return uriString;

    }



    //------------Logo Image Processing ends--------------------------





}