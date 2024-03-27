package com.up.rojgarsetu.Employer.Registration;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.Login.EmployeeLoginActivity;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.R;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.up.rojgarsetu.Employer.Registration.EmployerRegistrationActivity.uri;


public class NewEmployerRegistrationActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    EditText edt_cmpName, edt_cmpphone, edt_email, edt_pass, edt_rePass;
    Map<String, Object> empMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_new_employer_registration);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        edt_cmpphone = findViewById(R.id.emp_reg_address);
        edt_cmpName = findViewById(R.id.emp_reg_cmpName);
        edt_email = findViewById(R.id.emp_reg_email);
        edt_pass = findViewById(R.id.emp_reg_pass);
        edt_rePass = findViewById(R.id.emp_reg_rePass);

        edt_cmpName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_cmpName.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                {
                    edt_cmpName.setBackgroundResource(R.drawable.back_grey_round_corners);
                }
            }
        });

        edt_cmpphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_cmpphone.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                {
                    edt_cmpphone.setBackgroundResource(R.drawable.back_grey_round_corners);
                }
            }
        });
        edt_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_email.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                {
                    edt_email.setBackgroundResource(R.drawable.back_grey_round_corners);
                }
            }
        });
        edt_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_pass.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                {
                    edt_pass.setBackgroundResource(R.drawable.back_grey_round_corners);
                }
            }
        });

        edt_rePass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_rePass.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                {
                    edt_rePass.setBackgroundResource(R.drawable.back_grey_round_corners);
                }
            }
        });




    }
    String companyName, companyPhone, userEmail, userPass, userRePass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    public void onRegisterClick(View view){

        companyName = edt_cmpName.getText().toString();
        companyPhone = edt_cmpphone.getText().toString();
        userEmail = edt_email.getText().toString();
        userPass = edt_pass.getText().toString();
        userRePass = edt_rePass.getText().toString();

        if(companyName.length()==0){
            Toast.makeText(this, "Enter Company Name", Toast.LENGTH_SHORT).show();
            edt_cmpName.setBackgroundResource(R.drawable.back_red_round_corners);
            return;
        }
        if(companyPhone.length()==0){
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            edt_cmpphone.setBackgroundResource(R.drawable.back_red_round_corners);
            return;
        }
        if(!isEmailValid(userEmail)){
            Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            edt_email.setBackgroundResource(R.drawable.back_red_round_corners);
            return;
        }
        if(userPass.length()<8){
            Toast.makeText(this, "Enter Password of Minimum 08 characters", Toast.LENGTH_SHORT).show();
            edt_pass.setBackgroundResource(R.drawable.back_red_round_corners);
            return;
        }

        if(!userPass.equals(userRePass)){
            Toast.makeText(this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
            edt_pass.setBackgroundResource(R.drawable.back_red_round_corners);
            edt_rePass.setBackgroundResource(R.drawable.back_red_round_corners);
            return;
        }


        empMap = new HashMap<>();

        empMap.put("employerEmailId", userEmail);
        empMap.put("employerPhone", companyPhone);
        empMap.put("organisationName", companyName);

        createAccount(userEmail, userPass);




    }

    ProgressDialog progressBar;
    private void createAccount(final String email, final String password){

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Signing Up...");
        progressBar.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseUser = firebaseAuth.getCurrentUser();

                            Toast.makeText(NewEmployerRegistrationActivity.this, "User Account Created", Toast.LENGTH_SHORT).show();
                            //String name = user.getDisplayName();
                            //String email = user.getEmail();
                            //Uri photoUrl = user.getPhotoUrl();
                            //setprofileinfo(email,name,photoUrl.toString());

                            updateclouddatabase(email, password);





                        } else {

                            Toast.makeText(NewEmployerRegistrationActivity.this, "Error! Please Try Again Or Check Connection",Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }

                    }
                });

    }

    void updateclouddatabase(final String email, final String password) {
        progressBar.setMessage("Writing Data...");
        progressBar.show();



        firestore.document("users/" + email).set(empMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                OneSignal.sendTag("Type", "employer");
                OneSignal.sendTag("UserID", email);

                SharedPreferences.Editor editorUser = NewEmployerRegistrationActivity.this.getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                editorUser.putString("usertype", "Employer");
                editorUser.commit();

                sendVerificationEmail();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void sendVerificationEmail(){



        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.dismiss();


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewEmployerRegistrationActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.verification_email_sent,null);
                    alertDialog.setView(view);

                    alertDialog.setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(NewEmployerRegistrationActivity.this, EmployerLoginActivity.class);
                            startActivity(intent);
                            NewEmployerRegistrationActivity.this.finish();


                        }
                    });
                    alertDialog.setNegativeButton("Re-Send Verification Email", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendVerificationEmail();

                        }
                    });
                    alertDialog.show();






                }
                else{
                    progressBar.dismiss();
                    Log.d("SendVerificationEmail", "Task not successful"+ task.getException());
                }
            }
        });



    }




    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}