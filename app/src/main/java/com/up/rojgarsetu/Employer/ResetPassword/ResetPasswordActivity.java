package com.up.rojgarsetu.Employer.ResetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import io.paperdb.Paper;


public class ResetPasswordActivity extends AppCompatActivity {

    EditText et_old_pass,et_new_pass,et_new_pass2;
    Button bt_submit,bt_back;
    String email="";

    TextView tv_resetPass, tv_newPass, tv_oldPass, tv_retypenewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        et_old_pass=findViewById(R.id.et_old_pass);
        et_new_pass=findViewById(R.id.et_new_pass);
        bt_submit=findViewById(R.id.bt_confirm);
        et_new_pass2=findViewById(R.id.et_new_pass2);
        bt_back = findViewById(R.id.bt_confirm2);

        tv_newPass = findViewById(R.id.tv_newPass);
        tv_oldPass = findViewById(R.id.tv_oldPass);
        tv_resetPass = findViewById(R.id.tv_resetPass);
        tv_retypenewPass = findViewById(R.id.tv_retypenewPass);

        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");

        updateView((String)Paper.book().read("language"));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
try {
    email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
}
catch(Exception e){
    startActivity(new Intent(ResetPasswordActivity.this, ResetPasswordActivity.class));
    finish();
    return;
}


        et_new_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_new_pass.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_new_pass.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });

        et_old_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_old_pass.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_old_pass.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });

        et_new_pass2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    et_new_pass2.setBackgroundResource(R.drawable.back_orange_round_corners);
                }
                else
                    et_new_pass2.setBackgroundResource(R.drawable.back_grey_round_corners);
            }
        });



        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_old_pass.getText().toString().length()==0 ||
                        et_new_pass.getText().toString().length()==0 ||
                        et_new_pass2.getText().toString().length()==0){
                    Toast.makeText(ResetPasswordActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }

                else if(!et_new_pass.getText().toString().equals(et_new_pass2.getText().toString())){

                    Toast.makeText(ResetPasswordActivity.this, "New Passwords Must be Same", Toast.LENGTH_SHORT).show();
                    et_new_pass.setBackgroundResource(R.drawable.back_red_round_corners);
                    et_new_pass2.setBackgroundResource(R.drawable.back_red_round_corners);
                }

                else{

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this);
                    alertDialog.setTitle(R.string.Reset_Password);
                    alertDialog.setMessage("Are you sure to Reset Password?");

                    alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ChangePassword(et_new_pass.getText().toString(),et_old_pass.getText().toString());
                        }
                    });


                    alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.show();

                }

            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordActivity.this.finish();
            }
        });



    }

    void ChangePassword(final String newPassword, String oldPassword){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {

                                        Toast.makeText(ResetPasswordActivity.this, "Please Try Again \nPassword Must be minimum 6 characters", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void updateView(String lang) {
        try {
            Context context = LocaleHelper.setLocale(this, lang);
            Resources resources = context.getResources();
            tv_newPass.setText(resources.getString(R.string.New_Password));
            tv_oldPass.setText(resources.getString(R.string.Old_Password));
            tv_resetPass.setText(resources.getString(R.string.Reset_Password));
            tv_retypenewPass.setText(resources.getString(R.string.Re_Type_New_Password));
            bt_back.setText(resources.getString(R.string.back));
            bt_submit.setText(resources.getString(R.string.submit));

        }
        catch(Exception e){
            Log.e("UpdtView-LoginEmployer", e.getLocalizedMessage());
        }

    }

}