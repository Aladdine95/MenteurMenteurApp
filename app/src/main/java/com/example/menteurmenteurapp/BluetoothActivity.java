package com.example.menteurmenteurapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import static android.content.ContentValues.TAG;

public class BluetoothActivity extends AppCompatActivity {
    private Button menuPrincipalButton, connectButton;
    private BluetoothAdapter bluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDeviceSet;
    private ListView lview = null;
    private Map<String, BluetoothDevice> map = null;
    private int mBufferSize = 50000; //Default

    public static BluetoothSocket bluetoothSocket = null;
    public static ConnectedThread connectedThread;
    public static boolean connectionWentWell = false;
    private final static int BUFFER_SIZE = 150;
    public static float[] c_Pulsation;
    public static float[] c_Temperature;
    public static float[] c_Hygrometrie;

    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        menuPrincipalButton = findViewById(R.id.menuPrincipalButton);
        connectButton = findViewById(R.id.connectButton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevicesList();
        menuPrincipalButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(MainActivity.selectedDevice == null){
                    Toast.makeText(getApplicationContext(), "Aucun appareil n'a été sélectionné.", Toast.LENGTH_LONG).show();
                }
                else{
                    CreateConnection cc = new CreateConnection(bluetoothAdapter, MainActivity.selectedDevice.getAddress());
                    cc.start();
                    if(connectionWentWell) {
                        Toast.makeText(getApplicationContext(), "'" + MainActivity.selectedDevice.getName() + "' a été sélectionné.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Connexion avec '" + MainActivity.selectedDevice.getName() + "' n'a pas été établie," +
                                "veuillez réessayer... ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void pairedDevicesList(){
        lview = (ListView) findViewById(R.id.appareilBluetoothList);
        pairedDeviceSet = bluetoothAdapter.getBondedDevices();
        map = new HashMap<String, BluetoothDevice>();
        List list = new ArrayList();

        if (pairedDeviceSet.size() > 0) {
            for (BluetoothDevice pairedDevice : pairedDeviceSet) {
                map.put(pairedDevice.getAddress(), pairedDevice); //Get the device's name and the address
                list.add(pairedDevice.getName() + "\n" + pairedDevice.getAddress());
            }
            final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            lview.setAdapter(adapter);
            lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String info = ((TextView) view).getText().toString();
                    String address = info.substring(info.length() - 17);

                    MainActivity.selectedDevice = map.get(address);

                    lview.setItemChecked(position, true);
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Aucun appareil bluetooth pairée n'a été trouvé.", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isBluetoothConnected(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED ;
    }

    /*  La classe CreateConnexion étend la classe Thread et nous permettra
     *  d'effectuer la connexion avec les appareils déjà appairés avec le
     *  téléphone pour permettre l'échange de données par l'intermédiaire de
     *  la connexion bluetooth.
     */
    private static class CreateConnection extends Thread{

        public CreateConnection(BluetoothAdapter bluetoothAdapter, String address){
            BluetoothSocket bluetoothSocketTMP = null;
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            UUID mLieDeviceUUID = MainActivity.selectedDevice.getUuids()[0].getUuid();

            try{
                bluetoothSocketTMP = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(mLieDeviceUUID);
            }
            catch(IOException e){
                Log.e(TAG, "Échec de la création du socket de connexion (méthode socket()).", e);
            }

            bluetoothSocket = bluetoothSocketTMP;
        }

        public void run(){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // On connecte notre appareil à travers la socket de connexion jusqu'à
                // ce qu'elle réussise ou rejette une exception.
                bluetoothSocket.connect();
                Log.e("Status", "Appareil connecté au bluetooth");
                connectionWentWell = true;
            } catch (IOException connectException) {
                // Impossible de se connecter, fermeture du socket et retour.
                try {
                    bluetoothSocket.close();
                    Log.e("Status", "Impossible de connecter l'appareil au bluetooth.");
                    connectionWentWell = false;
                } catch (IOException closeException) {
                    Log.e(TAG, "Problème lors de la fermeture de la socket client.", closeException);
                    connectionWentWell = false;
                }
                return;
            }
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.run();
        }
    }

    /**
     *  La classe ConnectedThread étend la classe Thread et nous permettra
     *  de récupérer les données de nos capteurs pour traitement, grâce à
     *  la boucle permanente qui tournera en même temps des autres processus
     *  de notre simulation.
     *
     */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            /*  On récupère l'InputStream et l'OutputStream grâce à des objets temporaires
                pour permettre de les rendre finaux que si aucune exception ne s'est déclenchée.
                Des variables permanentes récupèront ensuite l'état actuel des streamers.
             */
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         *    Ajoute un flottant à un tableau donné.
         *    @param "int"
         *    @param "float[]"
         *    @param "float"
         *
         *    @return "float[]"
         */
        public float[] add_value(int n, float[] array, float element){
            float new_array[] = new float[n + 1];

            new_array[n] = element;

            return new_array;
        }

        /**
         * Méthode run qui va constamment attendre les données envoyés par le module bluetooth arduino
         * grâce à un Thread qui va constamment tourner en même temps que le reste de l'application.
         *     On lit ce que l'InputStream récupère depuis la carte Arduino jusqu'à ce que le
         *     caractère terminal soit atteint. La chaîne de caractère sera ensuite découpée en
         *     fonction du séparateur ":" et stocké dans un tableau qui sera ensuite concaténé
         *     aux tableaux correspondant aux capteurs de pulsation, hygrométrie et température.
         */
        public void run() {
            byte[] buffer = new byte[BUFFER_SIZE];  //Stockage du buffer récupéré dans le stream
            int bytes = 0; // Le nombre de bytes retournées par la méthode read()
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            // Continue d'écouter l'InputStream jusqu'à ce qu'une exception se produise
            while (true) {
                try {
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage; //Chaque ligne reçu seront stockés dans readMessage
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        String[] splitted_buffer = readMessage.split(":"); //Le tableau contenant nos données séparées grâce au séparateur ":"
                        Log.e(ts.toString(), "MenteurAEQT: C_Pulsation = " + splitted_buffer[1] + " - C_Température = "
                                + splitted_buffer[2] + " - C_Hygrométrie = " + splitted_buffer[3] ); //Écriture dans la console du message reçu par le arduino.
                        c_Pulsation = add_value(c_Pulsation.length + 1, c_Pulsation, Float.parseFloat(splitted_buffer[1]));
                        c_Temperature = add_value(c_Temperature.length + 1, c_Temperature, Float.parseFloat(splitted_buffer[2]));
                        c_Hygrometrie = add_value(c_Hygrometrie.length + 1, c_Hygrometrie, Float.parseFloat(splitted_buffer[3]));
                        for(int index = 0; index < c_Pulsation.length; index++){
                            System.out.println("Pulsation:" + c_Pulsation[index]);
                        }
                        for(int index = 0; index < c_Temperature.length; index++){
                            System.out.println("Temperature:" + c_Temperature[index]);
                        }
                        for(int index = 0; index < c_Hygrometrie.length; index++){
                            System.out.println("Hygrometrie:" + c_Hygrometrie[index]);
                        }
                        bytes = 0;
                    }
                    else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /*
        *  Cette méthode permet l'envoie d'un buffer de données à la carte arduino
        *  On ne l'utilise pas.
        */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Application:","Impossible d'envoyer un message.",e);
            }
        }

        /* Cette méthode permet de mettre fin à la connexion bluetooth depuis l'activité principale */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { }
        }
    }
}