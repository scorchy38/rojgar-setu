package com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.up.rojgarsetu.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class CheckJobsPostedStatusAdaptor extends RecyclerView.Adapter<CheckJobsPostedStatusAdaptor.MyViewHolder>{

    private ArrayList<String> jobName,company,jobType,snapshotID;
    Context context;
    String email="";
    Resources resources;

    public CheckJobsPostedStatusAdaptor(ArrayList<String> jobName,ArrayList<String> company,ArrayList<String> jobType,ArrayList<String> snapshotID,Context context,Resources resources){

        email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.jobName = jobName;
        this.company = company;
        this.jobType = jobType;
        this.snapshotID=snapshotID;
        this.context = context;
        this.resources = resources;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employer_check_jobsposted_listitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_checkjobstatus_jobtitle.setText(jobName.get(position));
        holder.tv_checkjobstatus_company.setText(company.get(position));
        //holder.tv_checkjobstatus_jobtype.setText(jobType.get(position));
        holder.no_of_accepted_applicants.setText(resources.getString(R.string.No_of_Accepted_Applicants));
        holder.no_of_applicants_left.setText(resources.getString(R.string.No_of_Applicants_Left));
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);



        db.collection("users").document(email).collection("viewapplicants")
                .whereEqualTo("jobappliedID", snapshotID.get(position))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        holder.tv_checkjobstatus_noofapplicants.setText(value.size()+"");
                    }
                });

        db.collection("users").document(email).collection("acceptedapplicants")
                .whereEqualTo("jobappliedID", snapshotID.get(position))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        holder.tv_checkjobstatus_accepted.setText(value.size()+"");
                    }
                });

        holder.cv_employer_checkjobsposted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SharedPreferences.Editor editor = context.getSharedPreferences("EmployerCheckJobsPostedStatus", MODE_PRIVATE).edit();
                    editor.putString("id", snapshotID.get(position));
                    editor.apply();

                    context.startActivity(new Intent(context, EmployerCheckJobsPostedStatus2.class));
                }catch(Exception e){}




            }
        });



    }

    @Override
    public int getItemCount() {
        return jobName.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public CardView cv_employer_checkjobsposted;
        TextView tv_checkjobstatus_jobtitle, tv_checkjobstatus_jobtype,tv_checkjobstatus_viewdetails,tv_checkjobstatus_company
                ,tv_checkjobstatus_noofapplicants,tv_checkjobstatus_accepted,no_of_accepted_applicants,no_of_applicants_left;

        public MyViewHolder(final View itemView) {
            super(itemView);
            cv_employer_checkjobsposted = itemView.findViewById(R.id.cv_employer_checkjobsposted);
            tv_checkjobstatus_jobtitle=itemView.findViewById(R.id.tv_checkjobstatus_jobtitle);
            tv_checkjobstatus_jobtype=itemView.findViewById(R.id.tv_checkjobstatus_jobtype);
            tv_checkjobstatus_viewdetails=itemView.findViewById(R.id.tv_checkjobstatus_viewdetails);
            tv_checkjobstatus_company=itemView.findViewById(R.id.tv_checkjobstatus_company);
            tv_checkjobstatus_noofapplicants=itemView.findViewById(R.id.tv_checkjobstatus_noofapplicants);
            tv_checkjobstatus_accepted=itemView.findViewById(R.id.tv_checkjobstatus_accepted);
            no_of_applicants_left=itemView.findViewById(R.id.no_of_applicants_left);
            no_of_accepted_applicants=itemView.findViewById(R.id.no_of_accepted_applicants);

        }

    }
}
