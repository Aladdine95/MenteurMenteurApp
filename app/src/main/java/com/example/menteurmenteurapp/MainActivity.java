package com.example.menteurmenteurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startButton;

    public static final int CALIBRATION_ACTIVITY_REQUEST_CODE = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrationActivity = new Intent(MainActivity.this, CalibrationActivity.class);
                startActivityForResult(calibrationActivity, CALIBRATION_ACTIVITY_REQUEST_CODE);
            }
        });
    }
}