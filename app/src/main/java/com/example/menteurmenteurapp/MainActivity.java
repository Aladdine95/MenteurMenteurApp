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

/**
 * MainActivity est l'activité principale de l'application, elle permet d'accéder à l'activité de bluetooth
 * et de montrer à l'utilisateur si il est bien connecté au bluetooth ainsi que le bon fonctionnement des capteurs.
 * @author Équipe détecteur de mensonge
 * @version 0.9
 */
public class MainActivity extends AppCompatActivity {

    private Button bluetoothDevices;
    @SuppressLint("StaticFieldLeak")
    public static TextView bluetoothOK, temperatureOK, hygrometrieOK, pulsationOK;
    /**
     * bluetoothModule contient les informations de l'appareil bluetooth du smartphone.
     */
    private BluetoothAdapter bluetoothModule;
    /**
     * boolean vérifiant si un appareil a bien été connecté au bluetooth.
     */
    private boolean isConnectedDevice = false;

    private static final int BT_ENABLE_REQUEST = 1;
    public static final int CALIBRATION_ACTIVITY_REQUEST_CODE = 30;
    public static final int BLUETOOTH_ACTIVITY_REQUEST_CODE = 31;
    /**
     * selectedDevice est l'appareil qui aura été selectionné par l'utilisateur pour la connexion.
     */
    public static BluetoothDevice selectedDevice = null;
    @SuppressLint("StaticFieldLeak")
    public static TextView temperatureVariantes, hygrometrieVariantes, pulsationVariantes;

    /**
     * Méthode appelée lors de la création de l'activité.
     * @param savedInstanceState
     */
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

        if(BluetoothActivity.splitted_buffer != null){
            isConnectedDevice = true;
            bluetoothOK.setText("Connecté");
            bluetoothOK.setTextColor(Color.GREEN);
        }
        else{
            isConnectedDevice = false;
            bluetoothOK.setText("Non connecté");
            bluetoothOK.setTextColor(Color.RED);
        }

        bluetoothModule = BluetoothAdapter.getDefaultAdapter();

        startButton.setOnClickListener(v -> {
            System.out.println(isConnectedDevice);
            if(!isConnectedDevice) {
                Toast.makeText(getApplicationContext(), "Veuillez connecter votre détecteur de mensonge avant de commencer!", Toast.LENGTH_LONG).show();
            }
            else{
                Intent calibrationActivity = new Intent(MainActivity.this, CalibrationActivity.class);
                startActivityForResult(calibrationActivity, CALIBRATION_ACTIVITY_REQUEST_CODE);
            }
        });

        //Au moment du clic sur le bouton de crédits, affichage des auteurs, remerciements et du sujet du programme
        creditsButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Crédits")
                .setMessage("Créateurs:\n\n" +
                        "Aladdine Ben Romdhane\n" +
                        "Quitterie Pilon\n" +
                        "Enzo Kalinowski\n" +
                        "Thomas Hec\n\n" +
                        "Nos remerciements à notre enseignante encadrante : M. Dang Ngoc\n\n" +
                        "Projet d'intégration : Objets connectés\n")
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        quitButton.setOnClickListener(v -> System.exit(1));
        connectBluetooth();
    }

    /**
     * onActivityResult permet, lors du retour sur l'activité en question, d'executer des instructions
     * en conséquence.
     * @param requestCode int
     * @param resultCode int
     * @param intent Intent
     */
    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Permet de vérifier si on acquérit bien des données à partir des capteurs
        //si c'est le cas, on affiche que le bluetooth est connecté, sinon on indique qu'il ne l'est pas
        if(BluetoothActivity.splitted_buffer != null){
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

    /**
     * Cette méthode nous permet de vérifier si le bluetooth est :
     *  => Supporté par le téléphone (si ce n'est pas le cas, toast expliquant que l'appareil n'a pas le bluetooth)
     *  => Désactivé (=> demande donc l'activation)
     *  => Activé mais non connecté (demande donc la connexion)
     *  => Activé et connecté (informe que tout est ok)
     */
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