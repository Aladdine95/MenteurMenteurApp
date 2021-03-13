package com.example.menteurmenteurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startButton, creditsButton;

    public static final int CALIBRATION_ACTIVITY_REQUEST_CODE = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        creditsButton = findViewById(R.id.creditsButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrationActivity = new Intent(MainActivity.this, CalibrationActivity.class);
                startActivityForResult(calibrationActivity, CALIBRATION_ACTIVITY_REQUEST_CODE);
            }
        });

        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Crédits")
                        .setMessage("Créateurs:\n\n" +
                                "Aladdine Ben Romdhane\n" +
                                "Quitterie Pilon\n" +
                                "Enzo Kalinowski\n" +
                                "Thomas Hec\n\n" +
                                "Projet d'intégration : Objets connectés")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }
}