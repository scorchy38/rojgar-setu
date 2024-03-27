package com.up.rojgarsetu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FirstViewAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private ArrayList<String> introText;
    private LayoutInflater inflater;
    private Context context;
    ConstraintLayout clSplashCards;
    FragmentActivity fragmentActivity;
    ViewPager mPager;
    Snackbar snackbar;
    CardView pt[];

    public FirstViewAdapter(Context context, ArrayList<Integer> images, ArrayList<String> introText, ConstraintLayout cl_splashCards,
                            FragmentActivity fragmentActivity, Snackbar snackbar,CardView[] pt,ViewPager mPager) {
        this.context = context;
        this.images=images;
        this.introText=introText;
        this.clSplashCards = cl_splashCards;
        this.fragmentActivity = fragmentActivity;
        this.snackbar=snackbar;
        this.pt=pt;
        this.mPager = mPager;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {
        final View myImageLayout = inflater.inflate(R.layout.splash_cards, view, false);

        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.sc01);
        TextView myText = (TextView) myImageLayout
                .findViewById(R.id.ftext01);
        myImage.setImageResource(images.get(position));
        myText.setText(introText.get(position));
        view.addView(myImageLayout, 0);




        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
