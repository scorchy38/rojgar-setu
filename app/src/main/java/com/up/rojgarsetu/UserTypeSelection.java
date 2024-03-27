package com.up.rojgarsetu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.up.rojgarsetu.Employee.Login.EmployeeLoginActivity;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;

import io.paperdb.Paper;


public class UserTypeSelection extends Fragment {

    TextView loginSeeker,loginProvider,jobSeeker,jobProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_user_type_selection, container, false);


        final CardView card_jobSeekerLogin = rootView.findViewById(R.id.card_jobSeekerLogin);
        final CardView card_jobProviderLogin = rootView.findViewById(R.id.card_jobProviderLogin);
        loginProvider = rootView.findViewById(R.id.loginProvider);
        loginSeeker = rootView.findViewById(R.id.loginSeeker);
        jobProvider = rootView.findViewById(R.id.jobProvider);
        jobSeeker = rootView.findViewById(R.id.jobSeeker);


        updateView((String) Paper.book().read("language"));

        ObjectAnimator animator = ObjectAnimator.ofFloat(card_jobProviderLogin, "translationX", 0);
        animator.setDuration(400);
        animator.start();
        animator = ObjectAnimator.ofFloat(card_jobSeekerLogin, "translationX", 0);
        animator.setDuration(500);
        animator.start();

        card_jobProviderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EmployerLoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        card_jobSeekerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EmployeeLoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return rootView;
    }
    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getContext(),lang);
        Resources resources = context.getResources();
        loginSeeker.setText(resources.getString(R.string.Login));
        loginProvider.setText(resources.getString(R.string.Login));
        jobSeeker.setText(resources.getString(R.string.JobSeeker));
        jobProvider.setText(resources.getString(R.string.JobProvider));
    }

}