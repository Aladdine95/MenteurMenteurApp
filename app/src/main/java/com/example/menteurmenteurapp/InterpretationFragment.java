package com.example.menteurmenteurapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InterpretationFragment extends Fragment {
    private Button terminer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_interpretation, container, false);
        terminer=v.findViewById(R.id.terminerButton);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                refreshHandler.postDelayed(this, 1 * 1000);
            }
        };
        refreshHandler.postDelayed(runnable, 1 * 1000);
        terminer=view.findViewById(R.id.terminerButton);
        terminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
