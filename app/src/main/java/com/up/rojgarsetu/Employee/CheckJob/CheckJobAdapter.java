package com.up.rojgarsetu.Employee.CheckJob;

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

import com.up.rojgarsetu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELECOM_SERVICE;

public class CheckJobAdapter extends RecyclerView.Adapter<CheckJobAdapter.CheckJobViewHolder> {

    private ArrayList<String> jobName,company,jobType,documentNumber,ownerUserID,district,jobPostedDate;
    private Resources hindiResources;
    Context context;
    public CheckJobAdapter(ArrayList<String> jobName, ArrayList<String> company, ArrayList<String> jobType, ArrayList<String> district, ArrayList<String> documentNumber, ArrayList<String> ownerUserID, ArrayList<String> jobPostedDate, Context context, Resources hindiResources){
        this.jobName = jobName;
        this.company = company;
        this.jobType = jobType;
        this.district = district;
        this.documentNumber = documentNumber;
        this.ownerUserID = ownerUserID;
        this.jobPostedDate = jobPostedDate;
        this.context = context;
        this.hindiResources = hindiResources;
    }


    @NonNull
    @Override
    public CheckJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employee_check_job_layout,parent,false);
        return new CheckJobViewHolder(view);
    }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String retDate = formatter.format(calendar.getTime());
        if(retDate.equals(" 01/01/1970"))
            return "---";
        else
            return retDate;

    }



    @Override
    public void onBindViewHolder(@NonNull final CheckJobViewHolder holder, final int position) {
        try {
            Log.d("jobName",jobName.get(position));
            holder.employee_job_name.setText(jobName.get(position));
            holder.employee_company.setText(company.get(position));
            holder.jobDistrict.setText(district.get(position));
            holder.viewDetails.setText(hindiResources.getString(R.string.View_Details));
            holder.postedDate.setText(getDate(Long.parseLong(jobPostedDate.get(position)), " dd/MM/yyyy"));
            //holder.applicants.setText(applicantNos.get(position));
            holder.cv_jobCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SharedPreferences.Editor editor = context.getSharedPreferences("CheckJob", MODE_PRIVATE).edit();
                        editor.putString("ownerUserID", ownerUserID.get(position));
                        editor.putString("documentNumber", documentNumber.get(position));
                        editor.commit();
                    /*
                    FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fl_main_activity_container, new EmployeeDetailCheckJob());
                    ft.commit();

                     */
                        context.startActivity(new Intent(context, EmployeeDetailCheckJob.class));
                    }
                    catch(Exception e2)
                    {Log.e("Error: ", e2.getMessage());}
                }
            });
        }catch (Exception e){
            Log.e("Error:",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return documentNumber.size();
    }

    public class CheckJobViewHolder extends RecyclerView.ViewHolder{
        TextView employee_job_name,employee_company,employee_type, jobDistrict, viewDetails, postedDate;
        CardView cv_jobCheck;

        public CheckJobViewHolder(View itemView){
            super(itemView);
            employee_job_name = (TextView) itemView.findViewById(R.id.employee_job_name);
            employee_company = (TextView) itemView.findViewById(R.id.employee_company);
            employee_type = (TextView) itemView.findViewById(R.id.employee_type);
            cv_jobCheck = (CardView) itemView.findViewById(R.id.cv_employee_checkJobs);
            jobDistrict = (TextView) itemView.findViewById(R.id.job_district);
            viewDetails = (TextView) itemView.findViewById(R.id.textView29);
            postedDate = (TextView) itemView.findViewById(R.id.job_date);
        }

    }

}
