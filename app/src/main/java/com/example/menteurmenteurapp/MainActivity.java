package com.example.menteurmenteurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button startButton, creditsButton, bluetoothDevices;
    private TextView bluetoothOK;
    private BluetoothAdapter bluetoothModule;
    private Set<BluetoothDevice> pairedDeviceSet;

    private static final int BT_ENABLE_REQUEST = 1;
    private static final int SETTINGS = 20;
    public static final int CALIBRATION_ACTIVITY_REQUEST_CODE = 30;
    public static final int BLUETOOTH_ACTIVITY_REQUEST_CODE = 31;
    public static BluetoothDevice selectedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        creditsButton = findViewById(R.id.creditsButton);
        bluetoothDevices = findViewById(R.id.bluetoothDevices);
        bluetoothOK = findViewById(R.id.bluetoothOK);

        if(BluetoothActivity.isBluetoothConnected()){
            bluetoothOK.setText("Connecté");
            bluetoothOK.setTextColor(Color.GREEN);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(selectedDevice == null) {
                    Toast.makeText(getApplicationContext(), "Veuillez connecter votre détecteur de mensonge avant de commencer!", Toast.LENGTH_LONG).show();
                }
                else{*/
                    Intent calibrationActivity = new Intent(MainActivity.this, CalibrationActivity.class);
                    startActivityForResult(calibrationActivity, CALIBRATION_ACTIVITY_REQUEST_CODE);
               // }
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
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        connectBluetooth();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(BluetoothActivity.isBluetoothConnected()){
            bluetoothOK.setText("Connecté");
            bluetoothOK.setTextColor(Color.GREEN);
        }
    }

    public void connectBluetooth() {
        bluetoothModule = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothModule == null) {
            Toast.makeText(getApplicationContext(), "Votre appareil ne supporte pas le bluetooth...", Toast.LENGTH_LONG).show();
        } else {
            if(!bluetoothModule.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Veuillez activer votre bluetooth!", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BT_ENABLE_REQUEST);
            }
            else{
                Toast.makeText(getApplicationContext(), "Votre bluetooth est activé et est prêt à être appairé à l'appareil!", Toast.LENGTH_LONG).show();
            }
            bluetoothDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!bluetoothModule.isEnabled()){
                        Toast.makeText(getApplicationContext(), "Veuillez activer votre bluetooth!", Toast.LENGTH_LONG).show();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, BT_ENABLE_REQUEST);
                    }
                    else{
                        Intent calibrationActivity = new Intent(MainActivity.this, BluetoothActivity.class);
                        startActivityForResult(calibrationActivity, BLUETOOTH_ACTIVITY_REQUEST_CODE);
                    }
                }
            });
        }
    }

}