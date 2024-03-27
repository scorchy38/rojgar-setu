package com.up.rojgarsetu.Employer.Registration;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.paperdb.Paper;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.Employer.Registration.EmployerRegistrationActivity.uri;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerRegistrationFragment3 extends Fragment {

    public static ProgressDialog progressBar;
    private FirebaseAuth mAuth;
    TextView lc_employerreg3, textView3,lc_emailid2,lc_pass2,lc_retypepass;

    public static FirebaseUser firebaseUser;
    public static String userEmail,email, password;


    public EmployerRegistrationFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_employer_registration_fragment3, container, false);

        //Button bt_reg_signup=v.findViewById(R.id.bt_reg_signup);
        //Button bt_frag3_employer_previous=v.findViewById(R.id.bt_frag3_employer_previous);
        final EditText et_reg_employer_emailid=v.findViewById(R.id.et_reg_employer_emailid);
        final EditText et_reg_employer_password=v.findViewById(R.id.et_reg_employer_password);
        final EditText et_reg_employer_retypepassword=v.findViewById(R.id.et_reg_employer_retypepassword);

        lc_employerreg3=v.findViewById(R.id.lc_employerreg3);
        textView3=v.findViewById(R.id.textView3);
        lc_emailid2=v.findViewById(R.id.lc_emailid2);
        lc_pass2=v.findViewById(R.id.lc_pass2);
        lc_retypepass=v.findViewById(R.id.lc_retypepass);



        et_reg_employer_emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_reg_employer_emailid.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_reg_employer_emailid.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });

        et_reg_employer_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_reg_employer_password.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_reg_employer_password.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });
        et_reg_employer_retypepassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_reg_employer_retypepassword.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_reg_employer_retypepassword.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });



        final CardView card_frag3_next = v.findViewById(R.id.card_frag3_accept);
        final CardView card_frag3_prev = v.findViewById(R.id.card_frag3_prev);
        final CardView card_frag3 = v.findViewById(R.id.card_frag3);
        ObjectAnimator animator = ObjectAnimator.ofFloat(card_frag3_next, "translationX", 0);
        animator.setDuration(300);
        animator.start();


        updateView((String) Paper.book().read("language"));


        mAuth = FirebaseAuth.getInstance();

        card_frag3_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                card_frag3_next.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_frag3_next.setElevation(5);
                    }
                },200);



                email=et_reg_employer_emailid.getText().toString().toLowerCase();
                password=et_reg_employer_password.getText().toString();
                String retypepassword=et_reg_employer_retypepassword.getText().toString();

                if(email.length()==0 || password.length()==0 || retypepassword.length()==0) {
                    Toast.makeText(getActivity(), "Email/Password field must be completed", Toast.LENGTH_SHORT).show();
                    if(email.length()==0){
                        et_reg_employer_emailid.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(password.length()==0){
                        et_reg_employer_password.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                    if(retypepassword.length()==0){
                        et_reg_employer_retypepassword.setBackgroundResource(R.drawable.back_red_round_corners);
                    }
                }

                else if(!isEmailValid(email)) {
                    Toast.makeText(getActivity(), "Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                    et_reg_employer_emailid.setBackgroundResource(R.drawable.back_red_round_corners);
                }

                else if(!password.equals(retypepassword)) {
                    Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    et_reg_employer_password.setBackgroundResource(R.drawable.back_red_round_corners);
                    et_reg_employer_retypepassword.setBackgroundResource(R.drawable.back_red_round_corners);
                }

                    //account sign up
                else{

                    createAccount(email,password);

                }

                //showprogressbar();
            }
        });


        card_frag3_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {

                card_frag3_prev.setElevation(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_frag3_prev.setElevation(5);
                    }
                },200);

                ObjectAnimator animator = ObjectAnimator.ofFloat(card_frag3, "translationX", 1800);
                animator.setDuration(300);
                animator.start();

                animator = ObjectAnimator.ofFloat(card_frag3_next, "translationX", 1800);
                animator.setDuration(300);
                animator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fl_employer_registration_activity,new EmployerRegistrationFragment1());
                        ft.commit();
                    }
                },300);


            }
        });
        return v;
    }


    private void createAccount(final String email, final String password){

        progressBar = new ProgressDialog(getContext());
        progressBar.setMessage("Signing Up...");
        progressBar.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseUser = user;

                            Toast.makeText(getActivity(), "User Account Created",
                                    Toast.LENGTH_SHORT).show();
                            //String name = user.getDisplayName();
                            //String email = user.getEmail();
                            //Uri photoUrl = user.getPhotoUrl();
                            //setprofileinfo(email,name,photoUrl.toString());

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                            editor.putString("frag3_Employer Email ID", email);
                            editor.putString("frag3_Password", password);
                            editor.apply();
                            updateclouddatabase(email, password);





                        } else {

                            Toast.makeText(getActivity(), "Please Try Again \nPassword Must be minimum 6 characters",Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }

                    }
                });

    }




    public void sendVerificationEmail(){



        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.dismiss();


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    View view = getLayoutInflater().inflate(R.layout.verification_email_sent,null);
                    alertDialog.setView(view);

                    alertDialog.setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(getActivity(), EmployerLoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();


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


    void updateclouddatabase(final String email, final String password){
        progressBar.setMessage("Writing Data...");
        progressBar.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();

        db.collection("users").document(email)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                        editor.clear();
                        editor.putString("frag3_Employer Email ID", email);
                        editor.putString("frag3_Password", password);
                        editor.apply();
                        progressBar.dismiss();

                        OneSignal.sendTag("Type","employer");
                        OneSignal.sendTag("UserID",email);
                        userEmail = email;

                        SharedPreferences.Editor editorUser = getActivity().getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                        editorUser.putString("usertype","Employer");
                        editorUser.commit();
                        if(uri!=null)
                            uploadImage(uri);
                        else{

                            sendVerificationEmail();
                        }




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }


    public void uploadImage(final Uri file)
    {
        final ProgressDialog pdnew = new ProgressDialog(getActivity());
        pdnew.setMessage("Linking Image to Account");
        pdnew.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(file).build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pdnew.dismiss();
                    Toast.makeText(getActivity(), "Image Linked to Account.", Toast.LENGTH_SHORT).show();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = storageRef.child("Employee/"+userEmail);
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Log.d("EmployerReg3", "Image Upload "+ exception.getLocalizedMessage());

                            sendVerificationEmail();


                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Image Uploaded!", Toast.LENGTH_SHORT).show();

                            sendVerificationEmail();

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
                else{
                    Log.d("EmployerReg3", "Image Linking Error");
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = storageRef.child("Employee/"+userEmail);
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Log.d("EmployerReg3", "Image Upload "+ exception.getLocalizedMessage());

                            sendVerificationEmail();
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Image Uploaded!", Toast.LENGTH_SHORT).show();

                            sendVerificationEmail();

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
            }
        });

    }












    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
// upload User Image here...

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getActivity(),lang);
        Resources resources = context.getResources();




        lc_employerreg3.setText(resources.getString(R.string.employer_registration));
        textView3.setText(resources.getString(R.string.Sign_Up_Details));
        lc_emailid2.setText(resources.getString(R.string.email_id));
        lc_pass2.setText(resources.getString(R.string.password));
        lc_retypepass.setText(resources.getString(R.string.Re_Password));


    }




}
