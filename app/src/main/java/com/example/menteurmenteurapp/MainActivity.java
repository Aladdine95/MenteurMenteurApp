package com.example.menteurmenteurapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button bluetoothDevices;
    @SuppressLint("StaticFieldLeak")
    public static TextView bluetoothOK, temperatureOK, hygrometrieOK, pulsationOK;
    private BluetoothAdapter bluetoothModule;
    private boolean isConnectedDevice = false;

    private static final int BT_ENABLE_REQUEST = 1;
    public static final int CALIBRATION_ACTIVITY_REQUEST_CODE = 30;
    public static final int BLUETOOTH_ACTIVITY_REQUEST_CODE = 31;
    public static BluetoothDevice selectedDevice = null;

    public static long timestamp_start = System.currentTimeMillis();
    public static TextView temperatureVariantes;
    public static TextView hygrometrieVariantes;
    public static TextView pulsationVariantes;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button creditsButton = findViewById(R.id.creditsButton);
        Button quitButton = findViewById(R.id.quitButton);
        bluetoothDevices = findViewById(R.id.bluetoothDevices);
        bluetoothOK = findViewById(R.id.bluetoothOK);
        temperatureOK = findViewById(R.id.temperatureOK);
        pulsationOK = findViewById(R.id.heartOK);
        hygrometrieOK = findViewById(R.id.hygrometerOK);
        pulsationVariantes = findViewById(R.id.bpmView);
        hygrometrieVariantes = findViewById(R.id.gm3View);
        temperatureVariantes = findViewById(R.id.degresView);

        if(BluetoothActivity.isBluetoothConnected()){
            isConnectedDevice = true;
            bluetoothOK.setText("Connecté");
            bluetoothOK.setTextColor(Color.GREEN);
        }

        startButton.setOnClickListener(v -> {
            /*if(!isConnectedDevice) {
                Toast.makeText(getApplicationContext(), "Veuillez connecter votre détecteur de mensonge avant de commencer!", Toast.LENGTH_LONG).show();
            }
            else{*/
                Intent calibrationActivity = new Intent(MainActivity.this, CalibrationActivity.class);
                startActivityForResult(calibrationActivity, CALIBRATION_ACTIVITY_REQUEST_CODE);
            /*}*/
        });

        creditsButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Crédits")
                .setMessage("Créateurs:\n\n" +
                        "Aladdine Ben Romdhane\n" +
                        "Quitterie Pilon\n" +
                        "Enzo Kalinowski\n" +
                        "Thomas Hec\n\n" +
                        "Projet d'intégration : Objets connectés")
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        quitButton.setOnClickListener(v -> System.exit(1));
        connectBluetooth();
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(BluetoothActivity.isBluetoothConnected()){
            isConnectedDevice = true;
            bluetoothOK.setText("Connecté");
            bluetoothOK.setTextColor(Color.GREEN);
        }
        else{
            isConnectedDevice = false;
            bluetoothOK.setText("Non connecté");
            bluetoothOK.setTextColor(Color.RED);
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
            bluetoothDevices.setOnClickListener(v -> {
                if(!bluetoothModule.isEnabled()){
                    Toast.makeText(getApplicationContext(), "Veuillez activer votre bluetooth!", Toast.LENGTH_LONG).show();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, BT_ENABLE_REQUEST);
                }
                else{
                    Intent calibrationActivity = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivityForResult(calibrationActivity, BLUETOOTH_ACTIVITY_REQUEST_CODE);
                }
            });
        }
    }

}