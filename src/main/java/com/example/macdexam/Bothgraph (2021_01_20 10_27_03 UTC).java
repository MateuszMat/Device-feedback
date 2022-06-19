package com.example.macdexam;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.nitri.gauge.Gauge;

public class Bothgraph extends Fragment {
    static Gauge temperature2gauge;
    static Gauge humidity2gauge;

    public Bothgraph() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bothgraph, container, false);
        humidity2gauge = view.findViewById(R.id.hum2gauge);
        temperature2gauge = view.findViewById(R.id.temp2gauge);

        return view;
    }
}
