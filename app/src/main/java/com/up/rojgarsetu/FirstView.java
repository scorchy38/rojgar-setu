package com.up.rojgarsetu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.up.rojgarsetu.Helper.LocaleHelper;

import java.util.ArrayList;

import io.paperdb.Paper;

import static android.content.Context.MODE_PRIVATE;


public class FirstView extends Fragment {


    int cardNum;
    CardView newLogoCard;

    ViewGroup rootView;
    int bottomOfOld, bottomOfNew;
    public static boolean downloadComplete = false;
    public static boolean downloadRequired = false;


    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int[] SlideImgs = {R.drawable.sc0, R.drawable.sc1, R.drawable.sc2 , R.drawable.sc4, R.drawable.sc3};
    private String[] SlideTextEnglish = {"Welcome to the RojgarSetu App\n\nLet us guide you through the basic features of this application","First the employer will post a job requirement that he/she wishes to fill", "Then these job posting will be shown to you on the basis of where the job is located and your area of expertise", "Apply for a job you want and send in your Resume or CV", "Employer will choose the candidates that they prefer and will get in touch with them"};
    private String[] SlideTextHindi = {"रोजगर सेतु ऐप में आपका स्वागत है।\n\nआइए हम आपको इस एप्लिकेशन की मूलभूत विशेषताओं के बारे में बताते हैं।","पहले नियोक्ता एक नौकरी की आवश्यकता को पोस्ट करेगा जिसे वह भरना चाहता है।", " फिर ये जॉब पोस्टिंग आपको उस जगह के आधार पर दिखाई जाएगी जहां नौकरी स्थित है और आपकी विशेषज्ञता का क्षेत्र है।", "अपनी इच्छित नौकरी के लिए आवेदन करें और अपने रिज्यूमे या सीवी में भेजें।", "ियोक्ता उन उम्मीदवारों का चयन करेगा जिन्हें वे पसंद करते हैं और उनके साथ संपर्क करेंगे।"};

    private ArrayList<Integer> SlideArray = new ArrayList<Integer>();
    private ArrayList<String> SlideTextArray = new ArrayList<String>();
    private FirstViewAdapter adapter;
    TextView tv_continue,tv_prev,tv_next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_first_view, container, false);

        cardNum = 0;
        snackbar = Snackbar.make(rootView.findViewById(R.id.firstViewFrameLayout), "डाउनलोडिंग हिंदी", Snackbar.LENGTH_INDEFINITE);
        newLogoCard = rootView.findViewById(R.id.logo_card);
        final CardView card_logo = rootView.findViewById(R.id.card_logo);
        TextView tv_welcome_english = rootView.findViewById(R.id.welcome_english);
        TextView tv_welcome_hindi = rootView.findViewById(R.id.welcome_hindi);
        final ConstraintLayout constraintLayout_firstView = rootView.findViewById(R.id.cl_firstView);
        tv_continue = rootView.findViewById(R.id.tv_continue);
        tv_prev = rootView.findViewById(R.id.tv_prev);
        tv_next = rootView.findViewById(R.id.tv_next);

        Paper.init(getContext());
        //      updateView((String) Paper.book().read("language"));

        final TextView tv_selectedEng = rootView.findViewById(R.id.tv_selectedEnglish);
        final TextView tv_selectedHindi = rootView.findViewById(R.id.tv_selectedHindi);
        final CardView proceed_firstView = rootView.findViewById(R.id.proceed_firstView);

        ViewTreeObserver vto = card_logo.getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                card_logo.getViewTreeObserver().removeOnPreDrawListener(this);
                bottomOfOld = card_logo.getBottom();

                ViewTreeObserver vto = newLogoCard.getViewTreeObserver();

                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        newLogoCard.getViewTreeObserver().removeOnPreDrawListener(this);
                        bottomOfNew = newLogoCard.getBottom();
                        int diff = bottomOfOld - bottomOfNew;
                        ObjectAnimator animator;

                        animator = ObjectAnimator.ofFloat(newLogoCard, "translationY", diff);
                        animator.setDuration(400);
                        animator.start();
                        animator = ObjectAnimator.ofFloat(newLogoCard, "scaleX", (float) 0.72);
                        animator.setDuration(400);
                        animator.start();
                        animator = ObjectAnimator.ofFloat(newLogoCard, "scaleY", (float) 0.72);
                        animator.setDuration(400);
                        animator.start();

                        return true;
                    }
                });

                return true;
            }
        });


        //--------------- Language Toggle Button Properties Declaration--------------

        final ToggleButton toggleButton_hindi = rootView.findViewById(R.id.toggle_hindi);
        final ToggleButton toggleButton_english = rootView.findViewById(R.id.toggle_english);

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


        //-------------- Language Toggle Button Properties Declaration Ends-----------------


        //-------------- Element Loading Animation ----------------------------

        ObjectAnimator animator;


        animator = ObjectAnimator.ofFloat(tv_welcome_english, "translationY", 0);
        animator.setDuration(500);
        animator.start();

        animator = ObjectAnimator.ofFloat(tv_welcome_english, "alpha", 1);
        animator.setDuration(500);
        animator.start();

        animator = ObjectAnimator.ofFloat(tv_welcome_hindi, "translationY", 0);
        animator.setDuration(500);
        animator.start();

        animator = ObjectAnimator.ofFloat(tv_welcome_hindi, "alpha", 1);
        animator.setDuration(500);
        animator.start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                View divider = rootView.findViewById(R.id.divider17);
                ObjectAnimator animatorDiv = ObjectAnimator.ofFloat(divider, "scaleX", 1);
                animatorDiv.setDuration(500);
                animatorDiv.start();


                TextView textView_choose = rootView.findViewById(R.id.choose_EnglishText);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(textView_choose, "alpha", 1);
                animator2.setDuration(500);
                animator2.start();

                TextView textView_choose2 = rootView.findViewById(R.id.choose_hindiText);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(textView_choose2, "alpha", 1);
                animator3.setDuration(500);
                animator3.start();

                CardView toggleCardHindi = rootView.findViewById(R.id.card_hinditoggle);

                ConstraintLayout cl_firstView = rootView.findViewById(R.id.cl_firstView);

                ObjectAnimator animator = ObjectAnimator.ofFloat(toggleCardHindi, "translationX", 0);
                animator.setDuration(600);
                animator.start();

                CardView toggleCardEng = rootView.findViewById(R.id.card_englishtoggle);
                ObjectAnimator animatorEng = ObjectAnimator.ofFloat(toggleCardEng, "translationX", 0);
                animatorEng.setDuration(600);
                animatorEng.start();

            }
        }, 500);

        //-------------- Element Loading Animations End---------------------------


        //-------------Proceed Button Action---------------


        proceed_firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Showing card 1");
                String language = Paper.book().read("language");
                if (language.equalsIgnoreCase("hi")) {
                    downloadHindi();
                    }
                else{
                    if(snackbar.isShown()) snackbar.dismiss();
                }
                ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(constraintLayout_firstView, "translationX", -1800);
                animatorProceed.setDuration(400);
                animatorProceed.start();

                initialiseViewPager(language);


                ConstraintLayout cl_splashCards = rootView.findViewById(R.id.cl_splashCards);
                animatorProceed = ObjectAnimator.ofFloat(cl_splashCards, "translationX", 0);
                animatorProceed.setDuration(400);
                animatorProceed.start();
                cardNum = 1;




            }
        });


        //------------Proceed Button Action Ends-------------


        //---------Continue button of Splash Cards------------
/*
        CardView continue_splash = rootView.findViewById(R.id.continue_splashCards);
        continue_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardNum == 1) {
                    System.out.println("Showing card 2");
                    LinearLayout splCard1 = rootView.findViewById(R.id.sl01);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(splCard1, "translationX", -1200);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();

                    CardView pt1 = rootView.findViewById(R.id.pt1);
                    pt1.setCardBackgroundColor(Color.WHITE);

                    CardView pt2 = rootView.findViewById(R.id.pt2);
                    pt2.setCardBackgroundColor(getResources().getColor(R.color.OrangeDark));

                    LinearLayout splCard2 = rootView.findViewById(R.id.sl02);
                    animatorProceed = ObjectAnimator.ofFloat(splCard2, "translationX", 0);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();
                    cardNum = 2;

                } else if (cardNum == 2) {
                    System.out.println("Showing card 3");
                    LinearLayout splCard2 = rootView.findViewById(R.id.sl02);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(splCard2, "translationX", -1200);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();

                    CardView pt2 = rootView.findViewById(R.id.pt2);
                    pt2.setCardBackgroundColor(Color.WHITE);

                    CardView pt3 = rootView.findViewById(R.id.pt3);
                    pt3.setCardBackgroundColor(getResources().getColor(R.color.OrangeDark));

                    LinearLayout splCard3 = rootView.findViewById(R.id.sl03);
                    animatorProceed = ObjectAnimator.ofFloat(splCard3, "translationX", 0);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();
                    cardNum = 3;
                } else if (cardNum == 3) {
                    System.out.println("Showing card 4");
                    LinearLayout splCard3 = rootView.findViewById(R.id.sl03);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(splCard3, "translationX", -1200);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();

                    CardView pt3 = rootView.findViewById(R.id.pt3);
                    pt3.setCardBackgroundColor(Color.WHITE);

                    CardView pt4 = rootView.findViewById(R.id.pt4);
                    pt4.setCardBackgroundColor(getResources().getColor(R.color.OrangeDark));

                    LinearLayout splCard4 = rootView.findViewById(R.id.sl04);
                    animatorProceed = ObjectAnimator.ofFloat(splCard4, "translationX", 0);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();
                    cardNum = 4;
                } else if (cardNum == 4) {
                    System.out.println("Showing card 5");
                    LinearLayout splCard4 = rootView.findViewById(R.id.sl04);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(splCard4, "translationX", -1200);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();

                    CardView pt4 = rootView.findViewById(R.id.pt4);
                    pt4.setCardBackgroundColor(Color.WHITE);

                    CardView pt5 = rootView.findViewById(R.id.pt5);
                    pt5.setCardBackgroundColor(getResources().getColor(R.color.OrangeDark));

                    LinearLayout splCard5 = rootView.findViewById(R.id.sl05);
                    animatorProceed = ObjectAnimator.ofFloat(splCard5, "translationX", 0);
                    animatorProceed.setDuration(400);
                    animatorProceed.start();
                    cardNum = 5;

                } else if (cardNum == 5) {
                    if (downloadComplete) {
                        Log.d("Hindi", downloadComplete + "");
                        Toast.makeText(getContext(), "Downloading Hindi Language...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    click5();
                }
            }
        });

 */


        return rootView;
    }


    public void click5() {
        ConstraintLayout cl_splashCards = rootView.findViewById(R.id.cl_splashCards);
        ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(cl_splashCards, "translationX", -1800);
        animatorProceed.setDuration(400);
        animatorProceed.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.splash_framelayout, new UserTypeSelection());
                ft.commit();
            }
        }, 100);
    }
    ConstraintLayout cl_splashCards;


    Snackbar snackbar;
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
                            Toast.makeText(getContext(), "Hindi Downloaded successfully", Toast.LENGTH_SHORT).show();
                            snackbar.dismiss();
                            Snackbar.make(rootView.findViewById(R.id.firstViewFrameLayout), "हिंदी शुरू हुई | आगे बढ़ें", Snackbar.LENGTH_INDEFINITE);
                            downloadComplete = false;
                        }
                        catch (Exception e){
                            Log.e("FirstView", "DownloadHindiSuccess");
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


    private void updateView(String lang) {
        Context context = LocaleHelper.setLocale(getContext(), lang);
        Resources resources = context.getResources();
        tv_continue.setText(resources.getString(R.string.continue_text));
        tv_next.setText(resources.getString(R.string.continue_text));
        tv_prev.setText(resources.getString(R.string.back));
    }



    private void initialiseViewPager(String language) {
        SlideArray.clear();
        SlideTextArray.clear();

        final CardView[] pt = {
                rootView.findViewById(R.id.pt1),
                rootView.findViewById(R.id.pt2),
                rootView.findViewById(R.id.pt3),
                rootView.findViewById(R.id.pt4),
                rootView.findViewById(R.id.pt5)
        };

        for (int i = 0; i < SlideImgs.length; i++) {
            SlideArray.add(SlideImgs[i]);
            if(language.equalsIgnoreCase("hi"))
                SlideTextArray.add(SlideTextHindi[i]);
            else
                SlideTextArray.add(SlideTextEnglish[i]);
        }

        mPager = (ViewPager) rootView.findViewById(R.id.splashPager);
        adapter = new FirstViewAdapter(getActivity(), SlideArray, SlideTextArray,cl_splashCards,
                getActivity(),snackbar,pt,mPager);
        mPager.setAdapter(adapter);




        final CardView prev = rootView.findViewById(R.id.cardView4);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                int position = mPager.getCurrentItem();

                if(position==0) {
                    ConstraintLayout cl_splashCards = rootView.findViewById(R.id.cl_splashCards);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(cl_splashCards, "translationX", 1800);
                    animatorProceed.setDuration(100);
                    animatorProceed.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.splash_framelayout, new FirstView());
                            ft.commit();
                        }
                    }, 100);
                }
                else{
                    mPager.setCurrentItem(position-1,true);
                    tv_prev.setTextColor(getResources().getColor((R.color.OrangeDark)));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv_prev.setTextColor(getResources().getColor((R.color.Orange)));
                            }catch(Exception e){

                            }
                        }
                    },200);

                }
            }
        });




        final CardView next = rootView.findViewById(R.id.cardView5);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = mPager.getCurrentItem();




                if(position==SlideImgs.length-1) {
                    try {
                        if (snackbar.isShown()) {
                            Toast.makeText(getActivity(), "हिंदी डाउनलोड के लिए प्रतीक्षा करें", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    catch(Exception e){
                        //No Download Required so, proceed...
                    }

                    ConstraintLayout cl_splashCards = rootView.findViewById(R.id.cl_splashCards);
                    ObjectAnimator animatorProceed = ObjectAnimator.ofFloat(cl_splashCards, "alpha", -1800);
                    animatorProceed.setDuration(300);
                    animatorProceed.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity()!=null) {
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("FirstLaunch", MODE_PRIVATE).edit();
                                editor.putString("FirstLaunch", "False");
                                editor.apply();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.splash_framelayout, new UserTypeSelection());
                                ft.commit();
                            }
                        }
                    }, 100);
                }
                else{
                    mPager.setCurrentItem(position+1,true);
                    tv_next.setTextColor(getResources().getColor((R.color.OrangeDark)));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv_next.setTextColor(getResources().getColor((R.color.Orange)));
                            }catch(Exception e){

                            }
                        }
                    },200);


                }
            }
        });

    mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for(int i=0; i<5; i++){
                if (i ==  position) {
                    pt[i].setCardBackgroundColor(getActivity().getResources().getColor(R.color.OrangeDark));
                }
                else
                    pt[i].setCardBackgroundColor(Color.WHITE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    });
    }

}