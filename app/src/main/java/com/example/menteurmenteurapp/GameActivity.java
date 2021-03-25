package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //init 2 fragments with FragmentManager
        if(savedInstanceState == null) {
            //give information to the fragmentUser fragment
            Bundle bundleUser = new Bundle();
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_interpretation_container_view, InterpretationFragment.class, )
                    .add(R.id.fragment_graph_container_view, GraphFragment.class, bundleUser)
                    .commit();
        }
    }
}
