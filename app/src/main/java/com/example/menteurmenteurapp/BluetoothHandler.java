package com.example.menteurmenteurapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothHandler extends AppCompatActivity {
    private Button menuPrincipalButton, connectButton;
    private BluetoothAdapter bluetoothModule;
    private Set<BluetoothDevice> pairedDeviceSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        menuPrincipalButton = findViewById(R.id.menuPrincipalButton);
        connectButton = findViewById(R.id.connectButton);
        bluetoothModule = BluetoothAdapter.getDefaultAdapter();
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
                    Toast.makeText(getApplicationContext(), "'" + MainActivity.selectedDevice.getName() + "' a été sélectionné.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void pairedDevicesList(){

        ListView lview = (ListView) findViewById(R.id.appareilBluetoothList);
        pairedDeviceSet = bluetoothModule.getBondedDevices();
        Map<String, BluetoothDevice> map = new HashMap<String, BluetoothDevice>();
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

}