package com.up.rojgarsetu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.up.rojgarsetu.Employee.EmployeeMainActivity;
import com.up.rojgarsetu.Helper.LocaleHelper;

import java.util.Map;

import io.paperdb.Paper;

import static com.up.rojgarsetu.Employee.EmployeeMainActivity.Fragment_Name;
import static com.up.rojgarsetu.MainActivity.fromLCER;

public class LanguageChange extends Fragment {
    Snackbar snackbar;
    CardView newLogoCard;
    public static boolean downloadComplete = false;
    ViewGroup rootView;
    Map<String, ?> map;
    TextView tv_continue;
    String language;
    CardView actionbar;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = (ViewGroup) inflater.inflate(R.layout.fragment_language_change, container, false);
    snackbar = Snackbar.make(rootView.findViewById(R.id.firstViewFrameLayout), "डाउनलोडिंग हिंदी", Snackbar.LENGTH_INDEFINITE);
    newLogoCard = rootView.findViewById(R.id.logo_card);

    Fragment_Name= "ChangeLanguage";
    fromLCER=true;
    Paper.init(getContext());
    actionbar = getActivity().findViewById(R.id.action_bar);
    actionbar.setVisibility(View.GONE);
    final TextView tv_selectedEng = rootView.findViewById(R.id.tv_selectedEnglish);
    final TextView tv_selectedHindi = rootView.findViewById(R.id.tv_selectedHindi);
    tv_continue = rootView.findViewById(R.id.tv_continue);
    final CardView proceed_firstView = rootView.findViewById(R.id.proceed_firstView);
    final ToggleButton toggleButton_hindi = rootView.findViewById(R.id.toggle_hindi);
    final ToggleButton toggleButton_english = rootView.findViewById(R.id.toggle_english);
    SharedPreferences prefs = getActivity().getSharedPreferences("USER TYPE", Context.MODE_PRIVATE);
    language = Paper.book().read("language");

    map = prefs.getAll();
    if(language.equalsIgnoreCase("hi")){
        toggleButton_hindi.setChecked(true);
        toggleButton_hindi.setBackgroundResource(R.drawable.back_filled_orange_22_5_dp_round);
        toggleButton_hindi.setTextColor(Color.WHITE);
        tv_selectedHindi.setVisibility(View.VISIBLE);
        proceed_firstView.setVisibility(View.VISIBLE);
        toggleButton_english.setChecked(false);
        toggleButton_english.setBackgroundResource(R.drawable.back_filled_white_25dpcorners);
        toggleButton_english.setTextColor(getResources().getColor(R.color.Orange));
        tv_selectedEng.setVisibility(View.INVISIBLE);
    }else{
        toggleButton_english.setChecked(true);
        toggleButton_english.setBackgroundResource(R.drawable.back_filled_orange_22_5_dp_round);
        toggleButton_english.setTextColor(Color.WHITE);
        tv_selectedEng.setVisibility(View.VISIBLE);
        proceed_firstView.setVisibility(View.VISIBLE);
        toggleButton_hindi.setChecked(false);
        toggleButton_hindi.setBackgroundResource(R.drawable.back_filled_white_25dpcorners);
        toggleButton_hindi.setTextColor(getResources().getColor(R.color.Orange));
        tv_selectedHindi.setVisibility(View.INVISIBLE);
    }

    toggleButton_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                toggleButton_english.setBackgroundResource(R.drawable.back_filled_orange_22_5_dp_round);
                toggleButton_english.setTextColor(Color.WHITE);
                tv_selectedEng.setVisibility(View.VISIBLE);
                proceed_firstView.setVisibility(View.VISIBLE);
                Paper.book().write("language", "en");
                updateView((String) Paper.book().read("language"));
                downloadComplete = false;

                toggleButton_hindi.setChecked(false);
                toggleButton_hindi.setBackgroundResource(R.drawable.back_filled_white_25dpcorners);
                toggleButton_hindi.setTextColor(getResources().getColor(R.color.Orange));
                tv_selectedHindi.setVisibility(View.INVISIBLE);
            }
        }
    });

    toggleButton_hindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                toggleButton_hindi.setBackgroundResource(R.drawable.back_filled_orange_22_5_dp_round);
                toggleButton_hindi.setTextColor(Color.WHITE);
                tv_selectedHindi.setVisibility(View.VISIBLE);
                proceed_firstView.setVisibility(View.VISIBLE);
                Paper.book().write("language", "hi");
                updateView((String) Paper.book().read("language"));
                downloadComplete = true;
                toggleButton_english.setChecked(false);
                toggleButton_english.setBackgroundResource(R.drawable.back_filled_white_25dpcorners);
                toggleButton_english.setTextColor(getResources().getColor(R.color.Orange));
                tv_selectedEng.setVisibility(View.INVISIBLE);

            }
        }
    });

    proceed_firstView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String language = Paper.book().read("language");
            Log.e("hindil",language);
            if (language.equalsIgnoreCase("hi")) {
                downloadHindi();
                return;
            }
            else{
                if(snackbar.isShown()) snackbar.dismiss();
            }
            if(!downloadComplete){
                Log.e("hindi","test");
                if(map.get("usertype")!=null && map.get("usertype").toString().equals("Employee")) {
                    Intent intent = new Intent(getActivity(), EmployeeMainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }else{
                snackbar.show();
            }
        }
    });

    return rootView;
}

    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getContext(), lang);
        Resources resources = context.getResources();
        tv_continue.setText(resources.getString(R.string.continue_text));
    }

    @Override
    public void onDestroyView() {
        actionbar.setVisibility(View.VISIBLE);
        super.onDestroyView();
    }

    void downloadHindi() {

        snackbar.show();
        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        TranslateRemoteModel Model =
                new TranslateRemoteModel.Builder(TranslateLanguage.HINDI).build();
        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();
        modelManager.download(Model, conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        try {
                            snackbar.dismiss();
                            Snackbar.make(rootView.findViewById(R.id.firstViewFrameLayout), "हिंदी शुरू हुई | आगे बढ़ें", Snackbar.LENGTH_INDEFINITE);
                            downloadComplete = false;
                            if(map.get("usertype").toString().equals("Employee")) {
                                Intent intent = new Intent(getActivity(), EmployeeMainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }else{
                                Intent intent = new Intent(getActivity(),MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                        catch (Exception e){
                            Log.e("FirstView", e.getMessage());
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getContext(), "Download Cancel Default Language English", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Toast.makeText(getContext(),"Error: "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        // Error.
                    }
                });
    }
}
