package com.example.hp.androidtask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsFragment extends Fragment  implements CompoundButton.OnCheckedChangeListener{

    private Switch switchThema;
    private boolean themaLight;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        switchThema=view.findViewById(R.id.switchThema);
        themaLight = MainActivity.thema;

            if(themaLight){
                switchThema.setChecked(false);
            }else {
                switchThema.setChecked(true);
            }
            switchThema.setOnCheckedChangeListener(this);

        return view;
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if(b==true){
                MainActivity.thema = false;
            }else{
                MainActivity.thema = true;
            }
    }
}
