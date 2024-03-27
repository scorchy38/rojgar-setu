package com.up.rojgarsetu.postJobFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.up.rojgarsetu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELECOM_SERVICE;

public class StateRecyclerAdapter extends RecyclerView.Adapter<StateRecyclerAdapter.StateViewHolder> {

    private ArrayList<String> al_states, selectedDistricts, seletedStates;
    Map<String, Object> statesMap;
    Context context;
    public StateRecyclerAdapter(ArrayList<String> al_states, ArrayList<String> selectedDistricts, ArrayList<String> selectedStates, Map<String, Object> statesMap, Context context){
        this.al_states = al_states;
        this.selectedDistricts = selectedDistricts;
        this.seletedStates = selectedStates;
        this.statesMap = statesMap;
        this.context = context;
    }


    @NonNull
    @Override
    public StateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.states_recycler_item_layout,parent,false);
        return new StateViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final StateViewHolder holder, final int position) {

        final CheckBox cb = holder.cb_state;

        cb.setText(al_states.get(position));
        final String state = al_states.get(position);

        cb.setText(state);

        if(seletedStates.contains(state)){
            cb.setChecked(true);
        }
        else{
            cb.setChecked(false);
        }



        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if(isChecked){
                    seletedStates.add(state);
                    Toast.makeText(context, "Added "+ state, Toast.LENGTH_SHORT).show();
                    openDistricts(state);


                }
                else{
                    try {
                        seletedStates.remove(state);
                        Toast.makeText(context, "Removed "+ state, Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e){}
                }




            }
        });

    }

    @Override
    public int getItemCount() {
        return al_states.size();
    }

    public class StateViewHolder extends RecyclerView.ViewHolder{
        CheckBox cb_state;
        TextView tv_selectedDistricts;
        CardView card_state;

        public StateViewHolder(View itemView){
            super(itemView);
            cb_state = itemView.findViewById(R.id.cb_state);
            card_state = itemView.findViewById(R.id.card_state);
            tv_selectedDistricts = itemView.findViewById(R.id.tv_selectedDistricts);
            tv_selectedDistricts.setVisibility(View.GONE);
        }

    }




    public void openDistricts(String state){
        String districtString = statesMap.get(state).toString();

        ArrayList<String> singleDistrictArray = new ArrayList<>();

        Collections.addAll(singleDistrictArray, districtString.split(","));
        Collections.sort(singleDistrictArray);
        Log.e("DistrictsOfThisState", "" + districtString);



    }

}
