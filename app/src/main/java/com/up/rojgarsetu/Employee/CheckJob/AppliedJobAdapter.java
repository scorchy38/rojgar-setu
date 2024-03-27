package com.up.rojgarsetu.Employee.CheckJob;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.up.rojgarsetu.R;

import java.util.ArrayList;

public class AppliedJobAdapter extends RecyclerView.Adapter<AppliedJobAdapter.CheckJobViewHolder> {

private ArrayList<String> jobName,company,jobType,documentNumber,ownerUserID,district,jobOffered;
        FragmentActivity fragmentActivity;
        Resources hindiResources;
public AppliedJobAdapter(ArrayList<String> jobName,ArrayList<String> company,ArrayList<String> jobType,ArrayList<String> district, ArrayList<String> documentNumber,ArrayList<String> ownerUserID,ArrayList<String> jobOffered,FragmentActivity fragmentActivity, Resources hindiResources){
        this.jobName = jobName;
        this.company = company;
        this.jobType = jobType;
        this.district = district;
        this.documentNumber = documentNumber;
        this.ownerUserID = ownerUserID;
        this.fragmentActivity = fragmentActivity;
        this.jobOffered = jobOffered;
        this.hindiResources = hindiResources;
        }


@NonNull
@Override
public CheckJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employee_applied_job_layout,parent,false);
        return new CheckJobViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull final CheckJobViewHolder holder, final int position) {
    try {
        holder.employee_job_name.setText(jobName.get(position));
        holder.employee_company.setText(company.get(position));
        holder.employee_type.setText(jobType.get(position));
        holder.jobDistrict.setText(district.get(position));
        if (jobOffered.get(position).equalsIgnoreCase("1"))
            holder.tv_jobOffered.setText(hindiResources.getString(R.string.Accept));
        else if (jobOffered.get(position).equalsIgnoreCase("0"))
            holder.tv_jobOffered.setText(hindiResources.getString(R.string.Reject));
        else
            holder.tv_jobOffered.setText(hindiResources.getString(R.string.Decision_Pending));
        holder.viewDetails.setText(hindiResources.getString(R.string.View_Details));
        holder.appStatus.setText(hindiResources.getString(R.string.applicationStatus));
        holder.cv_jobCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = fragmentActivity.getSharedPreferences("CheckJob", Context.MODE_PRIVATE).edit();
                editor.putString("ownerUserID", ownerUserID.get(position));
                editor.putString("documentNumber", documentNumber.get(position));
                editor.putString("jobOffered", jobOffered.get(position));
                editor.commit();
                FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container, new EmployeeAppliedJobDetail());
                ft.commit();
            }
        });
    }
    catch(Exception e){}
        }

@Override
public int getItemCount() {
        return documentNumber.size();
        }

public class CheckJobViewHolder extends RecyclerView.ViewHolder{
    TextView employee_job_name,employee_company,employee_type, jobDistrict,tv_jobOffered,viewDetails,appStatus;
    CardView cv_jobCheck;

    public CheckJobViewHolder(View itemView){
        super(itemView);
        employee_job_name = (TextView) itemView.findViewById(R.id.employee_job_name);
        employee_company = (TextView) itemView.findViewById(R.id.employee_company);
        employee_type = (TextView) itemView.findViewById(R.id.employee_type);
        cv_jobCheck = (CardView) itemView.findViewById(R.id.cv_employee_checkJobs);
        jobDistrict = (TextView) itemView.findViewById(R.id.job_district);
        tv_jobOffered = (TextView) itemView.findViewById(R.id.tv_jobOffered);
        viewDetails = (TextView) itemView.findViewById(R.id.textView29);
        appStatus = (TextView) itemView.findViewById(R.id.textView30);
    }
}

}
