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
import android.os.Handler;
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
    public static Handler handler;
    public static ConnectedThread connectedThread;
    public static boolean connectionWentWell = false;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

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

    private static class CreateConnection extends Thread{

        public CreateConnection(BluetoothAdapter bluetoothAdapter, String address){
            BluetoothSocket bluetoothSocketTMP = null;
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            UUID mLieDeviceUUID = MainActivity.selectedDevice.getUuids()[0].getUuid();

            try{
                bluetoothSocketTMP = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(mLieDeviceUUID);
            }
            catch(IOException e){
                Log.e(TAG, "Socket's create() method failed", e);
            }

            bluetoothSocket = bluetoothSocketTMP;
        }

        public void run(){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            System.out.println("ENTERED");
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                bluetoothSocket.connect();
                Log.e("Status", "Device connected");
                connectionWentWell = true;
                //handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    bluetoothSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    connectionWentWell = false;
                    //handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                    connectionWentWell = false;
                }
                return;
            }
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.run();
        }
    }

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { }
        }
    }
}