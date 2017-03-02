package com.example.matt.onresumeapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Adapter;
import android.app.AlertDialog;
import android.content.DialogInterface;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView infoText;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private List<String> mList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = (TextView) findViewById(R.id.infoText);

        // Sprawdzam czy na tablecie/tel jest moduł BT (dobra praktyka tylko, przeciez wszystkie majo)

        checkBtAvaliability();

        // Sprawdzam ile urządzeń BT mam podłączonych za pomoca BTAdapter i met getBondedDevives

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        //Jak ilosc urzadz polaczonych jest wieksza od zera...

        if(pairedDevices.size() > 0){

            for(BluetoothDevice device : pairedDevices){

                String deviceName = device.getName();  // Pobierz nazwe urzadzenia
                String deviceAdress = device.getAddress(); // Pobierz adres MAC
                mList.add(deviceName + "\n" + deviceAdress);

            }

        }

        // Uruchamiam Alert Dialog (Bedzie wyswietlony w nieskonczonosc :/)
        onCreateDialog().show();



    }

    @Override
    public void onResume()
    {
        super.onResume();




    }



    // Alert Dialog, wyswlietla liste dostepnych urzadzen BT
    private Dialog onCreateDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("List of BT Device")
                .setSingleChoiceItems(mList.toArray(new String[mList.size()]), 0, new DialogInterface.OnClickListener() { // Dostosowuje ArrayList do array
                    public void onClick(DialogInterface dialog, int which) {
                        infoText.setText(mList.get(which)); // Wybieram za pomoca int which ktory element z Listy mList wybieram (HC-06)

                        Intent nIntent = new Intent(MainActivity.this, ComunicationActivity.class);
                        nIntent.putExtra("EXTRA_ADRESS",mList.get(which));
                        startActivity(nIntent);

                    }

                });
        return builder.create();


    }











    // Sprawdzam czy na urządzeniu jest adapter BT, just a good practice :)

    private void checkBtAvaliability(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Jesli urzadzenie nie ma dostepnego moduł BT....

        if(mBluetoothAdapter == null) {

            infoText.setText("Brak dostepnego BT na tym urzadzeniu...");
        } else {

            // Jezeli BT jest wyłączone to popros o włączenie

            if(!mBluetoothAdapter.isEnabled()) {

                // komunikat dla urzytkownika

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            } else {

                infoText.setText("BT jest właczony i działa!");
            }

        }

    }

}
