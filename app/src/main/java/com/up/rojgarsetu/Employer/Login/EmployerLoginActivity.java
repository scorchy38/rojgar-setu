package com.up.rojgarsetu.Employer.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.onesignal.OneSignal;
import com.up.rojgarsetu.Employee.Login.EmployeeLoginActivity;
import com.up.rojgarsetu.Employer.Registration.EmployerRegistrationActivity;
import com.up.rojgarsetu.Employer.Registration.NewEmployerRegistrationActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.MainActivity;
import com.up.rojgarsetu.R;

import io.paperdb.Paper;

public class EmployerLoginActivity extends AppCompatActivity {

    EditText et_employer_emailid;
    EditText et_employer_password;
    Button bt_employer_login;
    TextView tv_employer_signup, tv_loginnow;
    private FirebaseAuth mAuth;
    TextView tv_employer_forgotpass;
    TextView lc_emailid,lc_pass,lc_donthaveaccount;
    TextView tv_loginAsEmployee;
    FirebaseUser firebaseUser;
    GoogleSignInClient mGoogleSignInClient;

    CardView card_googleSignIn;

    FirebaseAuth firebaseAuth;


    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }


    protected void onStart () {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null ){


         //   Toast.makeText(this, "Email Sign-in Successful!", Toast.LENGTH_SHORT).show();
            if(currentUser.isEmailVerified()) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {

                firebaseUser=currentUser;
                sendVerificationEmail();
            }
        }


    }


    ProgressDialog progressDialog;


    public void sendVerificationEmail(){
        if(progressDialog!=null && progressDialog.isShowing())
        progressDialog.dismiss();



        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){


                    Toast.makeText(EmployerLoginActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerLoginActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.verification_email_sent,null);
                    alertDialog.setView(view);

                    alertDialog.setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

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
                    Log.d("SendVerificationEmail", "Task not successful"+ task.getException());
                }
            }
        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_login);
        getSupportActionBar().hide();

        et_employer_emailid=findViewById(R.id.et_employer_emailid);
        et_employer_password=findViewById(R.id.et_employer_password);
        bt_employer_login=findViewById(R.id.bt_employer_login);
        tv_employer_signup=findViewById(R.id.tv_employer_signup);
        tv_employer_forgotpass=findViewById(R.id.tv_employer_forgotpass);
        final ImageView iv_pass_btn = findViewById(R.id.iv_pass_btn);
        card_googleSignIn = findViewById(R.id.card_googleSignin);
       // tv_loginnow = findViewById(R.id.tv_loginnow);
      //  tv_loginnow.requestFocus();

        iv_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_employer_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    iv_pass_btn.setImageResource(R.drawable.pass_hide);
                    et_employer_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    iv_pass_btn.setImageResource(R.drawable.pass_show);
                    et_employer_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        lc_emailid=findViewById(R.id.lc_emailid);
        lc_pass=findViewById(R.id.lc_pass);
        lc_donthaveaccount=findViewById(R.id.lc_donthaveaccount);

        mAuth = FirebaseAuth.getInstance();

        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");


        tv_loginAsEmployee = findViewById(R.id.tv_loginAsEmployee);
        updateView((String)Paper.book().read("language"));

        tv_loginAsEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_loginAsEmployee.setBackgroundColor(getResources().getColor(R.color.Grey));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_loginAsEmployee.setBackgroundColor(Color.WHITE);
                    }
                },100);


                Intent i = new Intent(EmployerLoginActivity.this, EmployeeLoginActivity.class);
                startActivity(i);
                finish();
            }
        });



        et_employer_emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_employer_emailid.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_employer_emailid.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });

        et_employer_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_employer_password.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_employer_password.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });


        bt_employer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_employer_login.setBackgroundResource(R.drawable.back_filled_light_orange_round);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bt_employer_login.setBackgroundResource(R.drawable.back_filled_cherry_round);
                    }
                },100);

                String email=et_employer_emailid.getText().toString();
                if(email.length()<1){
                    Toast.makeText(EmployerLoginActivity.this, "Email Cannot be Empty.", Toast.LENGTH_SHORT).show();
                    et_employer_emailid.setBackgroundResource(R.drawable.back_red_round_corners);
                    return;
                }
                String password=et_employer_password.getText().toString();
                if(password.length()<1){
                    Toast.makeText(EmployerLoginActivity.this, "Password Cannot be Empty.", Toast.LENGTH_SHORT).show();
                    et_employer_password.setBackgroundResource(R.drawable.back_red_round_corners);
                    return;
                }

                if(email.length()==0 || password.length()==0)
                    Toast.makeText(EmployerLoginActivity.this, "Email/Password field must be completed", Toast.LENGTH_SHORT).show();
                else
                signInwithEmail(email,password);


            }
        });

        tv_employer_signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            tv_employer_signup.setBackgroundColor(getResources().getColor(R.color.Grey));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_employer_signup.setBackgroundColor(Color.WHITE);
                }
            }, 20);

            Intent intent = new Intent(EmployerLoginActivity.this, NewEmployerRegistrationActivity.class);
            startActivity(intent);
            finish();


            }
        });


        //----------------- Google Sign In Initialisation Start------------------------------------


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleSignInWebClientId))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount((this));

        //--------------------- Google Sign In Initialisation Ends---------------------------------








        //----------------------------Forgot Password--------------------------

        tv_employer_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerLoginActivity.this);
                alertDialog.setTitle(R.string.Forgot_Password);
                alertDialog.setMessage(R.string.Send_Link_to_the_Registered_Email_ID);
                final EditText input = new EditText(EmployerLoginActivity.this);
                ViewGroup.LayoutParams layoutParamsedt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParamsedt);

                LinearLayout linearLayout = new LinearLayout(EmployerLoginActivity.this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                linearLayout.addView(input);
                linearLayout.setPadding(60, 0, 60, 0);
                input.setHint(R.string.email_id);
                input.setBackgroundResource(R.drawable.back_orange_round_corners);
                input.setLeft(20);
                input.setRight(20);
                input.setTop(20);
                input.setBottom(20);

                alertDialog.setView(linearLayout);
                alertDialog.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if(input.getText().toString().length()==0){
                                    Toast.makeText(EmployerLoginActivity.this, "Field Cannot be Empty", Toast.LENGTH_LONG).show();
                                }

                                else{

                                    FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(EmployerLoginActivity.this, "Link sent to account: "+input.getText().toString(), Toast.LENGTH_LONG).show();

                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(EmployerLoginActivity.this, "Error : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }

                            }
                        });
                alertDialog.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                alertDialog.show();
            }
        });



    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.usermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.usermenu_switch_usertype) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployerLoginActivity.this);


            alertDialog.setTitle("Select Job Post");
            alertDialog.setMessage("Use app as?");
            alertDialog.setPositiveButton("Employer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(EmployerLoginActivity.this, "Employer User Type Already Selected", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("Employee", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                    editor.putString("usertype", "Employee");
                    editor.apply();

                    Toast.makeText(EmployerLoginActivity.this, "Employee User Type Selected", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(EmployerLoginActivity.this, EmployeeLoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);

    }

    ProgressDialog pd;
    private void signInwithEmail(final String email, final String password){

        pd = new ProgressDialog(this);
        pd.setTitle("Signing in");
        pd.show();
        progressDialog = pd;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseUser = user;
                            if(!user.isEmailVerified()){
                                mAuth.signOut();
                                pd.dismiss();
                                Toast.makeText(EmployerLoginActivity.this, "Email not yet Verified", Toast.LENGTH_SHORT).show();
                                sendVerificationEmail();
                                return;
                            }
                            else {

                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                editor.putString("frag3_Employer Email ID", email);
                                editor.putString("frag3_Password", password);
                                editor.apply();

                                OneSignal.sendTag("Type", "employer");
                                OneSignal.sendTag("UserID", email);
                                OneSignal.setSubscription(true);
                                Toast.makeText(EmployerLoginActivity.this, "Email Sign-in Successful!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EmployerLoginActivity.this, MainActivity.class);
                                SharedPreferences.Editor editorUser = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                                editorUser.putString("usertype", "Employer");
                                editorUser.commit();
                                startActivity(intent);
                                finish();
                            }
                        } else {

                            Toast.makeText(EmployerLoginActivity.this, "Email Sign-in failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();

                    }
                });

    }
    private void updateView(String lang) {
        try {
            Context context = LocaleHelper.setLocale(this, lang);
            Resources resources = context.getResources();
            lc_emailid.setText(resources.getString(R.string.email_id));
            lc_pass.setText(resources.getString(R.string.password));
            bt_employer_login.setText(resources.getString(R.string.Login));
            lc_donthaveaccount.setText(resources.getString(R.string.Donot_have_an_account));
            tv_employer_signup.setText(resources.getString(R.string.Register));
            tv_loginAsEmployee.setText(resources.getString(R.string.loginAsEmployee));
            tv_employer_forgotpass.setText(resources.getString(R.string.Forgot_Password));
        }
        catch(Exception e){
            Log.e("UpdtView-LoginEmployer", e.getLocalizedMessage());
        }

    }






    //---------------------------------Google Sign In Functions---------------------------------





    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "EmployerLoginActivity";


    public void googleSignIn(View view){

        pd = new ProgressDialog(this);
        pd.setTitle("Signing in");
        pd.show();

        card_googleSignIn.setCardElevation(2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                card_googleSignIn.setElevation(5);

            }
        },300);


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }


    }

    private void firebaseAuthWithGoogle(String idToken) {



        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseUser = user;
                            if(!user.isEmailVerified()){
                                mAuth.signOut();
                                pd.dismiss();
                                Toast.makeText(EmployerLoginActivity.this, "Email not yet Verified", Toast.LENGTH_SHORT).show();
                                sendVerificationEmail();
                                return;
                            }
                            else {


                                OneSignal.sendTag("Type", "employer");
                                OneSignal.sendTag("UserID", user.getEmail());
                                OneSignal.setSubscription(true);
                                Toast.makeText(EmployerLoginActivity.this, "Google Sign-in Successful!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EmployerLoginActivity.this, MainActivity.class);
                                SharedPreferences.Editor editorUser = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
                                editorUser.putString("usertype", "Employer");
                                editorUser.commit();
                                startActivity(intent);
                                finish();
                            }


                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.root_frame_employerLogin), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    //--------------------------Functions for Google Sign In End----------------------------------





}
