package com.example.matt.onresumeapp;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;
import java.util.UUID;

public class ComunicationActivity extends AppCompatActivity {


    // Defining variables

    TextView copyInfo;
    TextView handlerInfo;
    Handler h;
    BluetoothAdapter btAdapter;

    private String adress;
    private ConnectedThread mConnectedThread;
    private static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mmSocket;
    private StringBuilder stBuilder = new StringBuilder();

    final int handlerState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);

        // Defining simple TextViews using to show messages on screen

        copyInfo = (TextView) findViewById(R.id.copyInfo);
        handlerInfo = (TextView) findViewById(R.id.handlerInfo);

        // Handler important to process and show message from Thread!!!!!!!!!!!

        h = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);





                if(msg.what == handlerState){    // If recive any message from thread then....

                    byte[] rBuff = (byte[]) msg.obj; // recived message (bytes)

                    String readMessage = new String(rBuff, 0, msg.arg1); // convert that message to string


                    handlerInfo.setText(readMessage); // Show message

                }


            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    // method to connect BT Device

    private BluetoothSocket btSocketConnection (BluetoothDevice device) throws IOException{

        return device.createInsecureRfcommSocketToServiceRecord(myUUID);
    }


    // On resume method
    @Override
    public void onResume() {
        super.onResume();


        // Import adress MAC from previous Intent!

        Intent newIntent = getIntent();
        adress = newIntent.getStringExtra("EXTRA_ADRESS");


        // Get that adress and assign
        BluetoothDevice device = btAdapter.getRemoteDevice(adress);

        // Get bluetooth Socket to connect....
        try {
            mmSocket = btSocketConnection(device);


        } catch (IOException e) {

            Toast.makeText(getBaseContext(), "Socket's method failed", Toast.LENGTH_LONG).show();

        }

        // Try co connect.....

        try {
            mmSocket.connect();
        } catch (IOException e){

            try {

                mmSocket.close();
            } catch (IOException e1)
            {
                Toast.makeText(getBaseContext(), "Socket's connect() method failed", Toast.LENGTH_LONG).show();
            }

        }

        // Start Threating
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();


    }




    // Threating method using to send received data from Arduino
    private class ConnectedThread extends Thread{

        // Definig variables
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){

            mmSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut= null;


            // Get Input and output connection!!!

            try {

                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e){

                Toast.makeText(getBaseContext(), "Error occurred when creating input stream", Toast.LENGTH_LONG).show();

            }

            try {
                tmpOut = socket.getOutputStream();

            } catch (IOException e){


                Toast.makeText(getBaseContext(), "Error occurred when creating output stream", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }


        // Run Thread!

        public void run() {
            super.run();

            byte[] mmBuffer = new byte[1024]; // Bytes returned from read() !!!
            int numBytes;

            // Start listening to the InputStream until an exception occurs
            while (true){
                try{

                    // Read from InputStream

                    numBytes = mmInStream.read(mmBuffer);

                    //Send to the optained bytes to the UI activity

                    h.obtainMessage(handlerState, numBytes, -1, mmBuffer).sendToTarget();

                } catch (IOException e){

                    Toast.makeText(getBaseContext(), "Input stream was disconnected", Toast.LENGTH_LONG).show();
                    break;
                }

            }

        }
    }

}
