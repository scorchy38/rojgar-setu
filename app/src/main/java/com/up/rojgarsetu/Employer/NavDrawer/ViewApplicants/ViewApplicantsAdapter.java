package com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.up.rojgarsetu.Employee.CheckJob.EmployeeDetailCheckJob;
import com.up.rojgarsetu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELECOM_SERVICE;

public class ViewApplicantsAdapter extends RecyclerView.Adapter<com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.ViewApplicantsAdapter.ViewApplicantsViewHolder> {

    private ArrayList<String> company, jobTitle, applicantName, documentId;
    Context context;
    Resources hindiResources;
    public ViewApplicantsAdapter(ArrayList<String> company, ArrayList<String> jobTitle, ArrayList<String> applicantName, ArrayList<String> documentId,
                                 Context context, Resources hindiResources){
        this.company = company;
        this.jobTitle = jobTitle;
        this.applicantName = applicantName;
        this.documentId = documentId;
        this.context = context;
        this.hindiResources = hindiResources;

    }


    @NonNull
    @Override
    public com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.ViewApplicantsAdapter.ViewApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employer_viewapplicants_listitem,parent,false);
        return new com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.ViewApplicantsAdapter.ViewApplicantsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.ViewApplicantsAdapter.ViewApplicantsViewHolder holder, final int position) {


        holder.tv_applicantName.setText(applicantName.get(position));
        holder.tv_company.setText(company.get(position));
        holder.tv_jobTitle.setText(jobTitle.get(position));
        holder.card_viewApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefs = context.getSharedPreferences("EmployerViewApplicants", MODE_PRIVATE).edit();
                prefs.putString("id", documentId.get(position));
                prefs.commit();

                context.startActivity(new Intent(context, EmployerViewApplicantsFragment2.class));

            }
        });



    }

    @Override
    public int getItemCount() {
        return documentId.size();
    }

    public class ViewApplicantsViewHolder extends RecyclerView.ViewHolder{
        TextView tv_jobTitle, tv_company, tv_applicantName, tv_viewDetails;
        CardView card_viewApplicant;

        public ViewApplicantsViewHolder(View itemView){
            super(itemView);
            tv_jobTitle = (TextView) itemView.findViewById(R.id.tv_employer_viewapplicants_jobtitle);
            tv_company = (TextView) itemView.findViewById(R.id.tv_employer_viewapplicants_company);
            tv_applicantName = (TextView) itemView.findViewById(R.id.tv_employer_viewapplicants_applicantname);
            card_viewApplicant = itemView.findViewById(R.id.cv_employer_viewapplicants);
        }

    }

}
