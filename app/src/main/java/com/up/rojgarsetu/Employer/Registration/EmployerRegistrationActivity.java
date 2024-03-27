package com.up.rojgarsetu.Employer.Registration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.up.rojgarsetu.Employer.Login.EmployerLoginActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;
import com.up.rojgarsetu.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.paperdb.Paper;

import static com.up.rojgarsetu.Employer.Registration.EmployerRegistrationFragment2.managerMobile;

public class EmployerRegistrationActivity extends AppCompatActivity {
    public static String RegFragName="";

    public static Activity regActivity;

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_registration);
        getSupportActionBar().hide();
        regActivity = this;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_employer_registration_activity,new EmployerRegistrationFragment2());
        ft.commit();

        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));

    }

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(this,lang);

    }

    @Override
    public void onBackPressed() {

        if (RegFragName.equals(("Frag2Reg"))) {
            Intent i = new Intent(EmployerRegistrationActivity.this, EmployerLoginActivity.class);
            startActivity(i);
            this.finish();


        }
    }


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
                    Log.d("EmployerReg", "Image unDecodable");
                    return;
                }
                try {
                    String path = compressImage(imgDecodableString);
                    Uri file = Uri.fromFile(new File(path));
                    if(file!=null) {
                        uri = file;
                        ImageView img = findViewById(R.id.uploadLogoIM);
                        img.setImageURI(uri);
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setBorderLineColor(Color.RED)
                                .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                                .start(regActivity);

                    }
                    else{
                        Log.d("Compression Result", "Got NULL URI");
                        uri = currImageURI;
                        ImageView img = findViewById(R.id.uploadLogoIM);
                        img.setImageURI(uri);
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setBorderLineColor(Color.RED)
                                .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                                .start(regActivity);
                    }

                }
                catch(Exception e){
                    Log.d("EmployerReg", "Compression Failed");
                    uri = currImageURI;
                    ImageView img = findViewById(R.id.uploadLogoIM);
                    img.setImageURI(uri);
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setBorderLineColor(Color.RED)
                            .setGuidelinesColor(Color.GREEN).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1)
                            .start(regActivity);
                }

            }catch(Exception e){
                Log.d("EmployerReg", "Image Crop Failed");
            }



        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("CropInRegistration","into Error if for Cropping");
                    ImageView img = findViewById(R.id.uploadLogoIM);
                    img.setImageURI(uri);

                }
                else  {
                    Uri resultUri = result.getUri();
                    uri = resultUri;
                    Log.d("CropInRegistration","into else for Cropping Result");
                    ImageView img = findViewById(R.id.uploadLogoIM);
                    img.setImageURI(resultUri);


                }
            }
        }
        catch(Exception e){
            Log.d("EmployerReg", "Crop Failed");

            if(uri!=null){
                ImageView img = findViewById(R.id.uploadLogoIM);
                img.setImageURI(uri);
            }
            else{
                Log.d("Crop Activity Result", "URI NULL");
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }




    public void takePic(View v) {
        context=this;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            takePic(v);
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
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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

        String mImageName=managerMobile+".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        return uriString;

    }



    //------------Logo Image Processing ends--------------------------





}
