package com.up.rojgarsetu.Employer.Registration;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.paperdb.Paper;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.Employer.Registration.EmployerRegistrationActivity.RegFragName;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerRegistrationFragment2 extends Fragment {

    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;


    TextView lc_employerreg,lc_ownerman,lc_ownername,
            lc_managername,lc_manmobile,lc_manemailid;
    static String managerMobile;

    public EmployerRegistrationFragment2() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_employer_registration_fragment2, container, false);


        RegFragName="Frag2Reg";
        //Button bt_manager_next=v.findViewById(R.id.bt_manager_next);
        final CardView card_next = v.findViewById(R.id.card_next);
        final CardView card_prev = v.findViewById(R.id.card_prev);
        final CardView card_frag1 = v.findViewById(R.id.card_frag1);
        final EditText et_frag2_owner_name=v.findViewById(R.id.et_frag2_owner_name);
        final EditText et_frag2_manager_emailid=v.findViewById(R.id.et_frag2_manager_emailid);
        final EditText et_frag2_manager_mobile=v.findViewById(R.id.et_frag2_manager_mobile);
        final EditText et_frag2_manager_name=v.findViewById(R.id.et_frag2_manager_name);

        lc_employerreg=v.findViewById(R.id.lc_employerreg);
        lc_ownerman=v.findViewById(R.id.lc_ownerman);
        lc_ownername=v.findViewById(R.id.lc_ownername);
        lc_managername=v.findViewById(R.id.lc_managername);
        lc_manmobile=v.findViewById(R.id.lc_manmobile);
        lc_manemailid=v.findViewById(R.id.lc_manemailid);


        updateView((String) Paper.book().read("language"));


        et_frag2_owner_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_owner_name.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_owner_name.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        et_frag2_manager_emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_manager_emailid.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_manager_emailid.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        et_frag2_manager_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_manager_name.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_manager_name.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });

        et_frag2_manager_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    et_frag2_manager_mobile.setBackgroundResource(R.drawable.back_orange_round_corners);
                else
                    et_frag2_manager_mobile.setBackgroundResource(R.drawable.back_grey_round_corners);

            }
        });





        SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        if(map.containsKey("frag2_Owner Name")){
            et_frag2_owner_name.setText(map.get("frag2_Owner Name").toString());
            et_frag2_manager_emailid.setText(map.get("frag2_Manager Email ID").toString());
            et_frag2_manager_mobile.setText(map.get("frag2_Manager Mobile No").toString());
            et_frag2_manager_name.setText(map.get("frag2_Manager Name").toString());
        }



        card_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {

                card_next.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_next.setElevation(5);
                    }
                },200);





                   if(et_frag2_owner_name.getText().toString().length()==0){
                       Toast.makeText(getActivity(), "Owner Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
                       et_frag2_owner_name.setBackgroundResource(R.drawable.back_red_round_corners);
                   }
                   else if(et_frag2_manager_name.getText().toString().length()==0){
                       Toast.makeText(getActivity(), "Manager Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
                       et_frag2_manager_name.setBackgroundResource(R.drawable.back_red_round_corners);
                   }
                   else if(et_frag2_manager_mobile.getText().toString().length()==0){
                       Toast.makeText(getActivity(), "Manager Mobile Cannot Be Empty", Toast.LENGTH_SHORT).show();
                       et_frag2_manager_mobile.setBackgroundResource(R.drawable.back_red_round_corners);
                   }
                   else if(et_frag2_manager_mobile.getText().toString().length()!=10){
                       Toast.makeText(getActivity(), "Enter Valid Manager Mobile Number", Toast.LENGTH_SHORT).show();
                       et_frag2_manager_mobile.setBackgroundResource(R.drawable.back_red_round_corners);
                   }

                   else if(et_frag2_manager_emailid.getText().toString().length()==0){
                       Toast.makeText(getActivity(), "Manager Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
                       et_frag2_manager_emailid.setBackgroundResource(R.drawable.back_red_round_corners);
                   }

                    else if(!isEmailValid(et_frag2_manager_emailid.getText().toString())){
                    Toast.makeText(getActivity(), "Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                    et_frag2_manager_emailid.setBackgroundResource(R.drawable.back_red_round_corners);
                    }

                else{


                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putString("frag2_Owner Name", et_frag2_owner_name.getText().toString());
                    editor.putString("frag2_Manager Email ID", et_frag2_manager_emailid.getText().toString());
                    editor.putString("frag2_Manager Mobile No", et_frag2_manager_mobile.getText().toString());
                    managerMobile = et_frag2_manager_mobile.getText().toString();
                    editor.putString("frag2_Manager Name", et_frag2_manager_name.getText().toString());

                    editor.apply();

                     ConstraintLayout cl = v.findViewById(R.id.cl_next);
                int y = cl.getWidth();
                int x = card_next.getWidth();
                float w = y - x - (cl.getPaddingRight() + cl.getPaddingLeft());

                ObjectAnimator animator = ObjectAnimator.ofFloat(card_prev, "translationX", -w / 2);
                animator.setDuration(500);
                animator.start();

                animator = ObjectAnimator.ofFloat(card_next, "translationX", w / 2);
                animator.setDuration(500);
                animator.start();

                animator = ObjectAnimator.ofFloat(card_frag1, "translationX", -1800);
                animator.setDuration(500);
                animator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fl_employer_registration_activity,new EmployerRegistrationFragment1());
                        ft.commit();
                    }
                },500);

                }

            }
        });

        return v;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void showprogressbar(){

        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage("Registring Employer ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();


        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                progressBar.dismiss();
                Toast.makeText(getActivity(), "Registration Complete!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), EmployerLoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        };

        handler.postDelayed(r, 3000);


    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();
        lc_employerreg.setText(resources.getString(R.string.employer_registration));
        lc_ownerman.setText(resources.getString(R.string.ownerdetails));
        lc_ownername.setText(resources.getString(R.string.Owner_Name));
        lc_managername.setText(resources.getString(R.string.Manager_Name));
        lc_manmobile.setText(resources.getString(R.string.Manager_Mobile_Number));
        lc_manemailid.setText(resources.getString(R.string.Manager_Email_ID));

    }

}
