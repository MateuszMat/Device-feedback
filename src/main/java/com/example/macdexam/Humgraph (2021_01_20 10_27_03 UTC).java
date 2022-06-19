package com.example.macdexam;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.nitri.gauge.Gauge;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Humgraph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Humgraph extends Fragment {

    static Gauge humiditygauge;

    public Humgraph() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_humgraph, container, false);
        humiditygauge = view.findViewById(R.id.humgauge);
        return view;
    }
}
