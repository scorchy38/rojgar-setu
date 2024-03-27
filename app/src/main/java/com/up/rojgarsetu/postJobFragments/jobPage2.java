package com.up.rojgarsetu.postJobFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.up.rojgarsetu.R;

import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.up.rojgarsetu.postJobFragments.PostJobActivity.postJobFragmentName;


public class jobPage2 extends Fragment {

   ViewGroup rootView;

   ArrayList<String> al_states;
   Map<String, Object> map_states;
   FirebaseFirestore firestore;

   public static Map<String, Set<String>> selectedDataset;
   RadioButton rb_wfh,rb_location;
   TextView tv_selectState;
   boolean wfhStatus = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.job_page_2, container, false);
        postJobFragmentName = "jobpage2";

        rb_location = rootView.findViewById(R.id.radio_specificLocation);
        rb_wfh = rootView.findViewById(R.id.radio_anywhere);

        rb_wfh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LinearLayout ll = rootView.findViewById(R.id.ll_stateContainer);
                    ll.setVisibility(View.GONE);
                    TextView tv = rootView.findViewById(R.id.tv_selectState);
                    tv.setVisibility(View.GONE);
                    wfhStatus=true;
                }
                else
                {
                    LinearLayout ll = rootView.findViewById(R.id.ll_stateContainer);
                    ll.setVisibility(View.VISIBLE);
                    TextView tv = rootView.findViewById(R.id.tv_selectState);
                    tv.setVisibility(View.VISIBLE);
                    wfhStatus=false;
                }
            }
        });

        selectedDataset = new HashMap<>();




        firestore = FirebaseFirestore.getInstance();


        firestore.document("states/English").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    map_states = documentSnapshot.getData();
                    al_states = new ArrayList<>(map_states.keySet());
                    Collections.sort(al_states);
                    LinearLayout ll = rootView.findViewById(R.id.ll_stateContainer);
                    ll.removeAllViews();

                    for(int i=0; i<al_states.size(); i++) {
                        try {
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View view = inflater.inflate(R.layout.states_recycler_item_layout, ll, false);
                            CheckBox cb = view.findViewById(R.id.cb_state);
                            final String state = al_states.get(i);
                            CardView cv = view.findViewById(R.id.card_state);
                            final int finalI = i;
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        selectedDataset.put(state, new HashSet<String>());
                                        openDistricts(state, view);
                                    } else {
                                        try {
                                            selectedDataset.remove(state);
                                            TextView vt = view.findViewById(R.id.tv_selectedDistricts);
                                            vt.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            });
                            cb.setText(al_states.get(i));
                            ll.addView(view);
                        }
                        catch(Exception e){}
                    }


                    //adapter = new StateRecyclerAdapter(al_states, selectedStates, selectedDistricts, map_states, getActivity());
                    //rv_states.setAdapter(adapter);
                }
            }
        });

        CardView card_continue = rootView.findViewById(R.id.card_continue_frag1);
        card_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Selected Locations: " + selectedDataset);

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
                editor.putBoolean("WorkFromHome", wfhStatus);
                editor.apply();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_postjob, new jobPage3());
                ft.commit();
            }
        });
        CardView card_back = rootView.findViewById(R.id.card_back_frag3_job);
        card_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Selected Locations: " + selectedDataset);

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Post_Job", MODE_PRIVATE).edit();
                editor.putBoolean("WorkFromHome", wfhStatus);
                editor.apply();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fl_postjob, new jobPage1());
                ft.commit();
            }
        });



        return rootView;
    }


    public void openDistricts(String state, final View parentStateView) {
        String districtString = map_states.get(state).toString();

        ArrayList<String> singleDistrictArray = new ArrayList<>();

        Collections.addAll(singleDistrictArray, districtString.split(","));
        Collections.sort(singleDistrictArray);
        Log.e("DistrictsOfThisState", "" + districtString);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.card_district_selector, null, false);

        LinearLayout ll_d = view.findViewById(R.id.ll_districts_forInflate);
        ll_d.removeAllViews();
        final Set<String> thisStateDistricts = selectedDataset.get(state);


        for (int x = 0; x < singleDistrictArray.size(); x++) {
            try{
                LayoutInflater d_inflater = LayoutInflater.from(getActivity());
                View d_view = d_inflater.inflate(R.layout.states_recycler_item_layout, ll_d, false);
                CheckBox cb_district = d_view.findViewById(R.id.cb_state);
                final String district = singleDistrictArray.get(x);
                cb_district.setText(district);
                if (thisStateDistricts.contains(district)) {
                    cb_district.setChecked(true);
                }
                ll_d.addView(d_view);
                cb_district.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            thisStateDistricts.add(district);
                        } else {
                            try {
                                thisStateDistricts.remove(district);
                            } catch (Exception e) {
                            }
                        }
                    }
                });

            } catch(Exception e){}

        }
        alertDialog.setPositiveButton("Add Districts", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                TextView tv = parentStateView.findViewById(R.id.tv_selectedDistricts);
                tv.setText("Selected Districts: "+ thisStateDistricts);
                tv.setVisibility(View.VISIBLE);
            }
        });

        alertDialog.setView(view);
        alertDialog.show();

    }



}