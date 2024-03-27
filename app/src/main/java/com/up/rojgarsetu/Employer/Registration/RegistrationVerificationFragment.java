package com.up.rojgarsetu.Employer.Registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.R;


public class RegistrationVerificationFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    ViewGroup rootView;

    Button proceed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_registration_verification, container, false);
        proceed = rootView.findViewById(R.id.proceedPostverification);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(), EmployerLoginActivity.class);
                startActivity(intent);
                getActivity().finish();


            }
        });




        return rootView;
    }






}