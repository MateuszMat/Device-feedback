package com.example.macdexam;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.nitri.gauge.Gauge;

public class Tempgraph extends Fragment {
   static Gauge temperaturegauge;

    public Tempgraph() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tempgraph, container, false);
        temperaturegauge = view.findViewById(R.id.tempgauge);
        return view;
    }
}
