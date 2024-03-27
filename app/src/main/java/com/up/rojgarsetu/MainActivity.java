package com.up.rojgarsetu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.up.rojgarsetu.Employer.EmployerBasicProfile;
import com.up.rojgarsetu.Employer.EmployerBasicProfileActivity;
import com.up.rojgarsetu.Employer.EmployerMainFragment;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Employer.NavDrawer.About.EmployerAboutFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.AcceptedApplicants.EmployerAcceptedApplicantsFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.CheckJobsPostedStatus.EmployerCheckJobsPostedStatus1;
import com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.EmployerViewApplicantsFragment1;
import com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants.EmployerViewApplicantsFragment2;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.postJobFragments.PostJobActivity;
import com.up.rojgarsetu.postJobFragments.jobPage1;
import com.up.rojgarsetu.postJobFragments.jobPage3;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth firebaseAuth ;
    TextView currentHeading;
    ImageView iv_changelang;
    ImageView iv_share;
    public static String Fragment_Name;
    String userEmail;
    FirebaseUser firebaseUser;
    TextView tv_contact_us;

    NavigationView navigationView;
    public static boolean fromLCER=false;

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);

        try {
            Fragment_Name = getResources().getString(R.string.Home);
        }
        catch (Exception e){
            Fragment_Name = "Home";
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        currentHeading = findViewById(R.id.currentHeading);
        tv_contact_us = findViewById(R.id.tv_contact_us);
        Paper.init(this);
        final String language = Paper.book().read("language");

        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();






        SharedPreferences editor = getSharedPreferences("NotificationEmployerApply", MODE_PRIVATE);
        String fragmentName = editor.getString("id","");

        if(fragmentName.length()!=0){
            editor.edit().remove("id").apply();
            SharedPreferences.Editor editor1 = getSharedPreferences("EmployerViewApplicants",MODE_PRIVATE).edit();
            editor1.putString("id",fragmentName);
            editor1.commit();
            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new EmployerViewApplicantsFragment2());
            ft.commit();

             */
            startActivity(new Intent(MainActivity.this, EmployerViewApplicantsFragment2.class));
            return;
        }


        /*SharedPreferences prefs = getSharedPreferences("FRAGMENT", MODE_PRIVATE);


            if(prefs.getString("fragment","-1").equals("Post A Job")){

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new jobPage1());
                ft1.commit();

            }

            else if(prefs.getString("fragment","-1").equals("Jobs Posted")){

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployerCheckJobsPostedStatus1());
                ft1.commit();

            }

            else if(prefs.getString("fragment","-1").equals("View Applicants")){

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployerViewApplicantsFragment1());
                ft1.commit();

            }

            else if(prefs.getString("fragment","-1").equals("View Accepted Applicants")){

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployerAcceptedApplicantsFragment1());
                ft1.commit();

            }
            else if(prefs.getString("fragment","-1").equals("About")){

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fl_main_activity_container,new EmployerAboutFragment1());
                ft1.commit();

            }
            else{

         */

if(firebaseUser!=null && firebaseUser.getEmail()!=null) {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    firestore.document(("users/" + firebaseUser.getEmail())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists() && documentSnapshot != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container, new EmployerMainFragment());
                ft.commitAllowingStateLoss();
            } else {
                startActivity(new Intent(MainActivity.this, EmployerBasicProfileActivity.class));
                finish();

            }

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

        }
    });
}


















                tv_contact_us.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

                        View view = getLayoutInflater().inflate(R.layout.contact_us_employer, null);

                        LinearLayout ll_email = view.findViewById(R.id.ll_email);
                        ll_email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent email = new Intent(Intent.ACTION_SENDTO);
                                email.setData(Uri.parse("mailto:"));
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "contact@chimpstechs.com"});
                                email.putExtra(Intent.EXTRA_SUBJECT, "Rojgar Setu | Employer Support");
                                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                            }
                        });
                        alertBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertBuilder.setView(view);
                        alertBuilder.show();



                    }
                });






                String email;
                try {

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    email = firebaseUser.getEmail();
                }
                catch(Exception e){
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    this.finish();
                    return;
                }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);



            if(email!=null) {


                final DocumentReference docRef = db.collection("users").document(email);
                final String finalEmail = email;
                final String finalEmail1 = email;
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }


                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            final View headerView = navigationView.getHeaderView(0);

                            TextView navUserDisplayName = (TextView) headerView.findViewById(R.id.userDisplayName);
                            try {
                                navUserDisplayName.setText(snapshot.getData().get("frag1_Full Name of Factory/Establishment").toString());
                            } catch (Exception e1) {
                                navUserDisplayName.setText(snapshot.getData().get("organisationName").toString());
                            }
                            TextView navUsername = (TextView) headerView.findViewById(R.id.employer_emailid_nav);
                            navUsername.setText(finalEmail);
                            userEmail = finalEmail;


                            ConstraintLayout changeLogo = headerView.findViewById(R.id.change_dipi);
                            changeLogo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    takePic(v);
                                }
                            });


                            //-------------------Read image from Cloud---------------------------

                            try {
                                final long OO = 1024 * 1024;
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                storageRef.child("Employee/" + finalEmail1).getBytes(OO).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        if (bytes != null) {
                                            ImageView dp = headerView.findViewById(R.id.dipi);
                                            Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            dp.setImageBitmap(bit);

                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("EmployeeMainActivity", "Getting Image Failed" + e.getLocalizedMessage());
                                            }
                                        });
                            } catch (Exception e2) {
                                Log.e("EmployeeMainActivity", "Getting Image Failed" + e.getLocalizedMessage());
                            }


                            //-------------------Read image from Cloud Ends---------------------------


                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
            }






        iv_changelang=findViewById(R.id.iv_changelang);

        iv_changelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new LanguageChange());
                ft.commit();
            }
        });

        iv_share=findViewById(R.id.iv_share);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareSub = "Download Rojgar Setu | Original Indian Job Search App. Find Job Providers/Job Seekers in your own District/State. \n" +
                        "https://play.google.com/store/apps/details?id=com.up.rojgarsetu";
                myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            }
        });

    }



    public void nikal(View v) {

        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        editor = getSharedPreferences("USER TYPE", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Email Log-Out Successful!", Toast.LENGTH_SHORT).show();
        OneSignal.setSubscription(false);
        Intent intent=new Intent(MainActivity.this, EmployerLoginActivity.class);
        startActivity(intent);
        finish();
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(Fragment_Name.equals(getResources().getString(R.string.Home))){

            if(fromLCER){
                ((CardView) MainActivity.this.findViewById(R.id.action_bar)).setVisibility(View.VISIBLE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_main_activity_container,new EmployerMainFragment());
                ft.commit();
                fromLCER=false;

            }

            else if (doubleBackToExitPressedOnce) {
                SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                super.onBackPressed();
                return;
            }


            this.doubleBackToExitPressedOnce = true;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.fl_main_activity_container), "Press Back again to EXIT!", Snackbar.LENGTH_SHORT);
            snackbar.show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);


        }


        else if(Fragment_Name.equals("Post A Job3")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new jobPage3());
            ft.commit();
        }
        else if(Fragment_Name.equals("Post A Job2")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new jobPage1());
            ft.commit();
        }


       // else if(!Fragment_Name.equals("Post A Job")){
        else{
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new EmployerMainFragment());
            ft.commit();
        }

        }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);



            SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
            editor.putString("fragment","Home");
            editor.apply();


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new EmployerMainFragment());
            ft.commit();




        } else if (id == R.id.nav_item2) {

            SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
            editor.putString("fragment","Post A Job");
            editor.apply();

            startActivity(new Intent(MainActivity.this, PostJobActivity.class));



        } else if (id == R.id.nav_item3) {

            SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
            editor.putString("fragment","Jobs Posted");
            editor.apply();

            startActivity(new Intent(MainActivity.this, EmployerCheckJobsPostedStatus1.class));



        }

        else if (id == R.id.nav_item4) {


            SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
            editor.putString("fragment","View Applicants");
            editor.apply();

            /*
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new EmployerViewApplicantsFragment1());
            ft.commit();

             */

            startActivity(new Intent(MainActivity.this, EmployerViewApplicantsFragment1.class));



        }
        else if (id == R.id.nav_item5) {

            startActivity(new Intent(MainActivity.this, EmployerAcceptedApplicantsFragment1.class));



        }

         else if (id == R.id.nav_group2_item1) {

            //startActivity(new Intent(this,AboutView.class));
            //for editing profile

            SharedPreferences.Editor editor = getSharedPreferences("FRAGMENT", MODE_PRIVATE).edit();
            editor.putString("fragment","About");
            editor.apply();


            currentHeading.setText("About");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_main_activity_container,new EmployerAboutFragment1());
            ft.commit();

            //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.replace(R.id.fl_main_activity_container,new EmployerAboutFragment1());
            //ft.commit();


        } else if (id == R.id.nav_signout) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Sign Out")
                    .setMessage("Are you sure you want to SignOut?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            nikal(null);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openDrawer(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();


        Menu navMenu = navigationView.getMenu();

        TextView change  = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView49);
        change.setText(resources.getString(R.string.Change));

        MenuItem item0 = navMenu.findItem(R.id.nav_item1);
        item0.setTitle(resources.getString(R.string.Home));

        item0 = navMenu.findItem(R.id.nav_item2);
        item0.setTitle(resources.getString(R.string.Post_A_Job));

        item0 = navMenu.findItem(R.id.nav_item3);
        item0.setTitle(resources.getString(R.string.Check_Jobs_Posted_Status));

        item0 = navMenu.findItem(R.id.nav_item4);
        item0.setTitle(resources.getString(R.string.View_Applicants));


        item0 = navMenu.findItem(R.id.nav_item5);
        item0.setTitle(resources.getString(R.string.View_Accepted_Applicants));

        item0 = navMenu.findItem(R.id.nav_group2_item1);
        item0.setTitle(resources.getString(R.string.Profile));


        item0 = navMenu.findItem(R.id.nav_signout);
        item0.setTitle(resources.getString(R.string.SignOut));

        tv_contact_us.setText(resources.getString(R.string.contact_us));


    }






    //--------------------Image Reading, Compression and Upload-----------------------


    public static Uri uri;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d("ActivityResult", "Request Code "+ requestCode + "Result Code "+ requestCode);



        //---------------------Profile Picture Reading & Cropping-------------------



        if (requestCode == 1) {

            Log.d("Activity Result", "in requestCode1");

            try {
                currImageURI = data.getData();

                String imgDecodableString = getRealPathFromURI(currImageURI);
                if (imgDecodableString == null) {
                    Log.d("MainActivity", "Picture not read");
                    return;
                }
                try {
                    String path = compressImage(imgDecodableString);
                    Uri file = Uri.fromFile(new File(path));
                    if(file!=null) {
                        uri = file;

                    }
                    else{
                        Log.d("Compression Result", "Got NULL URI");
                        uri = currImageURI;

                    }

                }
                catch(Exception e){
                    Log.d("MainActivity", "Picture not Compressed");
                    uri = currImageURI;

                }
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setBorderLineColor(Color.RED)
                        .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                        .start(this);
            }catch(Exception e){
                Log.d("MainActivity", "Picture not Compressed");
            }



        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    uploadImage(uri);

                }
                else  {
                    Uri resultUri = result.getUri();
                    uri = resultUri;
                    uploadImage(uri);

                }
            }
        }
        catch(Exception e){
            Log.d("MainActivity", "Picture not Compressed");

            if(uri!=null){

            }
            else{
                Log.d("Crop Activity Result", "URI NULL");
            }
        }


        //------------------Profile Picture Reading & Cropping Activity Results ends----------------------------------------------------------



        super.onActivityResult(requestCode, resultCode, data);

    }




    public void takePic(View v) {
        context=this;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            //takePic(v);
            return;
        }
        Log.d("TakePic",  " starting intent");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    private static final int CAMERA_REQUEST = 1;

    Uri currImageURI;



    //----------------Logo Image Processing-------------------------

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        String[] projection = { MediaStore.Images.Media.DATA };
        if (this.getContentResolver() != null) {
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }





    private Context context;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;



    public String compressImage( String imagePath)
    {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null)
        {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName=userEmail+".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        return uriString;

    }





    //------------Logo Image Processing ends--------------------------

    //-----------Upload Logo------------------------------

    public void uploadImage(final Uri file)
    {
        final ProgressDialog pdnew = new ProgressDialog(this);
        pdnew.setMessage("Linking Image to Account");
        pdnew.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(file).build();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pdnew.dismiss();
                    Toast.makeText(MainActivity.this, "Image Linked to Account.", Toast.LENGTH_SHORT).show();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = storageRef.child("Employee/"+userEmail);
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();


                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "Unable to link image to account.", Toast.LENGTH_SHORT).show();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference riversRef = storageRef.child("Employee/"+userEmail);
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Uploading Image!");
                    UploadTask uploadTask = riversRef.putFile(file);
                    progressDialog.show();

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();


                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
            }
        });

    }






}
