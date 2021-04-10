package com.example.menteurmenteurapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Cette activité défini le comportement du bluetooth (recherche des appareils, connexion [création de socket],
 * ainsi que traitement sur les données reçues).
 */
public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter = null;
    private ListView lview = null;
    private Map<String, BluetoothDevice> map = null;

    public static BluetoothSocket bluetoothSocket = null;
    public static ConnectedThread connectedThread;
    public static boolean connectionWentWell = false;
    private final static int BUFFER_SIZE = 150;
    public static List<Float> c_Pulsation;
    public static List<Float> c_Temperature;
    public static List<Float> c_Hygrometrie;
    public static String[] splitted_buffer;

    /**
     * Cette méthode est appelée lors de la création de l'activité de bluetooth.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button menuPrincipalButton = findViewById(R.id.menuPrincipalButton);
        Button connectButton = findViewById(R.id.connectButton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevicesList();
        menuPrincipalButton.setOnClickListener(v -> finish());

        //Définition des conditions et instructions à executer lors du clic sur le bouton de connexion.
        connectButton.setOnClickListener(v -> {
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
        });
    }

    /**
     *
     *  Cette méthode permet de nous lister la liste des appareils appairés au téléphone (à faire au préalable).
     *  Elle est utile lors de la connexion en bluetooth car elle permet de lister l'appareil et de s'y connecter.
     */
    private void pairedDevicesList(){
        lview = findViewById(R.id.appareilBluetoothList);
        Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices(); //Récupération des appareils appairés
        map = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (pairedDeviceSet.size() > 0) {
            for (BluetoothDevice pairedDevice : pairedDeviceSet) { //On liste tout les appareil appareillé et on les affiche sous forme d'éléments de ListView
                map.put(pairedDevice.getAddress(), pairedDevice); //On récupère l'adresse et le nom de l'appareil
                list.add(pairedDevice.getName() + "\n" + pairedDevice.getAddress());
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            lview.setAdapter(adapter);
            //Permet de "sélectionner" l'appareil auquel on souhaite se connecter en récupérant l'adresse de l'appareil ainsi que son nom
            lview.setOnItemClickListener((parent, view, position, id) -> {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                MainActivity.selectedDevice = map.get(address);

                lview.setItemChecked(position, true);
                Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            });
        }
        else { //Cas où aucun appareil bluetooth n'est apparaillé au bluetooth du téléphone
            Toast.makeText(getApplicationContext(), "Aucun appareil bluetooth pairée n'a été trouvé.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  La classe CreateConnexion étend la classe Thread et nous permettra
     *  d'effectuer la connexion avec les appareils déjà appairés avec le
     *  téléphone pour permettre l'échange de données par l'intermédiaire de
     *  la connexion bluetooth.
     */
    private static class CreateConnection extends Thread{

        /**
         * Permet de créer la connexion bluetooth entre le téléphone et le module bluetooth de la carte.
         * @param bluetoothAdapter
         * @param address
         */
        public CreateConnection(BluetoothAdapter bluetoothAdapter, String address){
            BluetoothSocket bluetoothSocketTMP = null;
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            UUID mLieDeviceUUID = MainActivity.selectedDevice.getUuids()[0].getUuid();

            try{
                //Création du socket bluetooth avec grâce à l'UUID de l'appareil auquel on souhaite se connecter
                //Stockage dans une variable temporaire pour éviter les erreurs dans les cas d'exceptions
                bluetoothSocketTMP = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(mLieDeviceUUID);
            }
            catch(IOException e){
                Log.e(TAG, "Échec de la création du socket de connexion (méthode socket()).", e);
            }
            //On stocke la socket que l'on a crée auparavant de façon permanente
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

        /**
         * Cette classe permet de définir les méthodes qui vont définir le comportement après la connexion en bluetooth.
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            /**
             *  On récupère l'InputStream et l'OutputStream grâce à des objets temporaires
             *   pour permettre de les rendre finaux que si aucune exception ne s'est déclenchée.
             *   Des variables permanentes récupèront ensuite l'état actuel des streamers.
             */
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.getMessage();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         * Méthode run qui va constamment attendre les données envoyés par le module bluetooth arduino
         * grâce à un Thread qui va constamment tourner en même temps que le reste de l'application.
         *     On lit ce que l'InputStream récupère depuis la carte Arduino jusqu'à ce que le
         *     caractère terminal soit atteint. La chaîne de caractère sera ensuite découpée en
         *     fonction du séparateur ":" et stocké dans un tableau qui sera ensuite concaténé
         *     aux tableaux correspondant aux capteurs de pulsation, hygrométrie et température.
         */
        @SuppressLint("SetTextI18n")
        public void run() {
            c_Pulsation = new ArrayList<>();
            c_Temperature = new ArrayList<>();
            c_Hygrometrie = new ArrayList<>();
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
                        splitted_buffer = readMessage.split(":"); //Le tableau contenant nos données séparées grâce au séparateur ":"
                        //On affiche au niveau des logs les données que l'on a acquis a des fins de développements
                        Log.e(ts.toString(), "MenteurAEQT: C_Pulsation = " + splitted_buffer[1] + " - C_Température = "
                                + splitted_buffer[2] + " - C_Hygrométrie = " + splitted_buffer[3] ); //Écriture dans la console du message reçu par le arduino.

                        //On change le texte à afficher à l'utilisateur au niveau des capteurs (c.f activity_main.xml => capteurs)
                        MainActivity.pulsationOK.setText(splitted_buffer[1] + " BPM");
                        MainActivity.temperatureOK.setText(splitted_buffer[2] + " °C");
                        MainActivity.hygrometrieOK.setText(splitted_buffer[3] + " g/m3");

                        //On ajoute aux différents tableaux les valeurs correspondants au capteur de pulsation, température, hygrométrie respectivement
                        c_Pulsation.add(Float.parseFloat(splitted_buffer[1]));
                        c_Temperature.add(Float.parseFloat(splitted_buffer[2]));
                        c_Hygrometrie.add(Float.parseFloat(splitted_buffer[3]));

                        //On change la couleur du texte en fonction de l'acquisition des données des capteurs
                        //Si c'est à 0 => Couleur du texte rouge (permet de montrer que le capteur est indisponible)
                        //sinon => Couleur du texte vert
                        if(Float.parseFloat(splitted_buffer[1]) <= (float) 0.0)
                            MainActivity.pulsationOK.setTextColor(Color.RED);
                        else
                            MainActivity.pulsationOK.setTextColor(Color.GREEN);

                        if(Float.parseFloat(splitted_buffer[2]) <= (float) 0.0)
                            MainActivity.temperatureOK.setTextColor(Color.RED);
                        else
                            MainActivity.temperatureOK.setTextColor(Color.GREEN);

                        if(Float.parseFloat(splitted_buffer[3]) <= (float) 0.0)
                            MainActivity.hygrometrieOK.setTextColor(Color.RED);
                        else
                            MainActivity.hygrometrieOK.setTextColor(Color.GREEN);

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

        /**
        *  Cette méthode permet l'envoie d'un buffer de données à la carte arduino
        *  On ne l'utilise pas mais elle peut s'avérer très utile dans le cas d'échanges.
         * @param input String
        */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Application:","Impossible d'envoyer un message.",e);
            }
        }

        /**
         * Cette méthode permet de mettre fin à la connexion bluetooth depuis l'activité principale
         * On ne l'utilise pas, mais elle peut s'avérer utile lorsqu'on veut forcer la fermeture du socket
         * bluetooth.
         * */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}