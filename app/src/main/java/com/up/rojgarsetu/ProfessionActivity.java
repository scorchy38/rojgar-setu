package com.up.rojgarsetu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.up.rojgarsetu.Helper.LocaleHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;


public class ProfessionActivity extends AppCompatActivity {

    ArrayList<String> selectedProfession, allProfession;

    Translator englishHindiTranslator;
    String language = "en";
    Map<String, String> SkillsMap;

    TextView tv_heading, tv_limit;
    Button bt,bt2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profession);
        getSupportActionBar().hide();
        selectedProfession=new ArrayList<>();
        SkillsMap = new HashMap<>();

        tv_heading = findViewById(R.id.professionsheading);
        tv_limit= findViewById(R.id.professionsLimit);
        bt = findViewById(R.id.button);//Continue
        bt2 = findViewById(R.id.button2);//Back


        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        englishHindiTranslator = Translation.getClient(options);

        Paper.init(this);
        language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));




        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.document("professions/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.getData()!=null){
                    allProfession = (ArrayList<String>) documentSnapshot.getData().get("ProfessionArray");
                    showChips();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfessionActivity.this, "Error Loading Data! Please check connection!", Toast.LENGTH_SHORT).show();

            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i<selectedProfession.size(); i++){
                    System.out.print(selectedProfession.get(i));
                }

                Intent intent = new Intent();
                intent.putExtra("SelectedProfessions", selectedProfession);
                setResult(10, intent);
                finish();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(11, intent);
                finish();
            }
        });


    }


    int index=0;
    ChipGroup chipGroup, selectedChipGroup;
    public void showChips(){
        selectedChipGroup = findViewById(R.id.selectedProfessionsChipGroup);
        selectedChipGroup.removeAllViews();
        chipGroup = findViewById(R.id.professionChipGroup);
        for(int i=0; i<allProfession.size(); i++){
            final Chip chip = new Chip(this);
            changeLanguage(allProfession.get(i),chip);


            chip.setTextStartPadding(8);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            chip.setTextEndPadding(8);
            chip.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            chip.setChecked(false);
            chip.setChipStartPadding(5);
           // chip.setPadding(2,0,2,0);
            chip.setChipEndPadding(5);
            chip.setHeight(40);
            chip.setChipBackgroundColorResource(R.color.Orange);
            final int finalI = i;
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(index==5){
                        Toast.makeText(ProfessionActivity.this, "Max. 5 Skills can be selected...", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chip.setVisibility(View.GONE);

                    final Chip duplicateChip = new Chip(ProfessionActivity.this);
                    duplicateChip.setText(chip.getText().toString());
                    duplicateChip.setCloseIconResource(R.drawable.close_chip);
                    duplicateChip.setCheckedIconVisible(true);
                    duplicateChip.setCloseIconVisible(true);
                    duplicateChip.setTextEndPadding(8);
                    duplicateChip.setTextStartPadding(8);
                    duplicateChip.setChipStartPadding(5);
                    duplicateChip.setChipEndPadding(5);
                    //duplicateChip.setWidth(ChipGroup.LayoutParams.WRAP_CONTENT);
                    duplicateChip.setChipBackgroundColorResource(R.color.OrangeDark);
                    duplicateChip.setTextColor(Color.WHITE);
                    duplicateChip.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                    duplicateChip.setChecked(true);
                    duplicateChip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedChipGroup.removeView(duplicateChip);
                            chip.setVisibility(View.VISIBLE);
                            index--;
                            selectedProfession.remove(chip.getText().toString());

                        }
                    });

                    selectedChipGroup.addView(duplicateChip);
                    selectedProfession.add(allProfession.get(finalI));

                    index++;

                }
            });



            chipGroup.addView(chip);
        }
    }

    void changeLanguage(final String text, final Chip chip) {
        if (language.equalsIgnoreCase("hi")) {
            englishHindiTranslator.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String translatedText) {
                                    chip.setText(translatedText);
                                    SkillsMap.put(translatedText, text);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("Emp. CheckJob", "LanguageTranslation: " +e.getMessage());
                                }
                            });


        } else {
            chip.setText(text);
            SkillsMap.put(text, text);
        }
    }

    private void updateView(String lang) {

        Context context = LocaleHelper.setLocale(this,lang);
        Resources resources = context.getResources();

        //currentHeading.setText(resources.getString(R.string.Search_Job));

        tv_heading.setText(resources.getString(R.string.selectYourSkills));
        tv_limit.setText(resources.getString(R.string.skillsLimit));
        bt2.setText(resources.getString(R.string.back));
        bt.setText(resources.getString(R.string.Continue));



    }


}