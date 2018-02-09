package com.example.kapil.turnon;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Control extends AppCompatActivity {
    public static final String TAG = "try";
    ImageButton btnLight, btnFan1,btnFan2, btnAc, btnSpeak;
    Boolean toggleLight=false,toggleFan1=false,toggleFan2=false,toggleAc = false;
    String address = null;
    TextView txtSpeechInput;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onStart() {
        super.onStart();
        new ConnectBT().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        //call the widgtes
        txtSpeechInput =  findViewById(R.id.textSpeak);
        btnSpeak = findViewById(R.id.btnSpeak);

        btnLight = findViewById(R.id.btnLight);
        btnFan1 = findViewById(R.id.btnFan1);
        btnFan2 = findViewById(R.id.btnFan2);
        btnAc = findViewById(R.id.btnAc);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        btnLight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(toggleLight == false){
                    btnLight.setBackgroundColor(Color.GREEN);
                    turnOnLight();
                    toggleLight = true;
                }
                else if(toggleLight == true){
                    btnLight.setBackgroundColor(Color.RED);
                    turnOffLight();
                    toggleLight = false;
                }
            }
        });

        btnFan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(toggleFan1 == false){
                    btnFan1.setBackgroundColor(Color.GREEN);
                    turnOnFan1();
                    toggleFan1 = true;
                }
                else if(toggleFan1 == true){
                    btnFan1.setBackgroundColor(Color.RED);
                    turnOffFan1();
                    toggleFan1 = false;
                }
            }
        });
        btnFan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(toggleFan2 == false){
                    btnFan2.setBackgroundColor(Color.GREEN);
                    turnOnFan2();
                    toggleFan2 = true;
                }
                else if(toggleFan2 == true){
                    btnFan2.setBackgroundColor(Color.RED);
                    turnOffFan2();
                    toggleFan2 = false;
                }
            }
        });

        btnAc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(toggleAc == false){
                    btnAc.setBackgroundColor(Color.GREEN);
                    turnOnAc();
                    toggleAc = true;
                }
                else if(toggleAc == true){
                    btnAc.setBackgroundColor(Color.RED);
                    turnOffAc();
                    toggleAc = false;
                }
            }
        });
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    String command = txtSpeechInput.getText().toString();
                    if(command.contains("light")&& command.contains("on")){ turnOnLight(); toggleLight = true;}
                    else if(command.contains("light")&& command.contains("off")){ turnOffLight(); toggleLight = false;}
                    else if(command.contains("fan 1")&& command.contains("on")){ turnOnFan1(); toggleFan1 = true;}
                    else if(command.contains("fan 1")&& command.contains("off")){ turnOffFan1(); toggleFan1 = false;}
                    else if(command.contains("fan 2")&& command.contains("on")){ turnOnFan2(); toggleFan2 = true;}
                    else if(command.contains("fan 2")&& command.contains("off")){ turnOffFan2(); toggleFan2 = false;}
                    else if(command.contains("ac")&& command.contains("on")){ turnOnAc(); toggleAc = true;}
                    else if(command.contains("ac")&& command.contains("off")){ turnOffAc(); toggleAc = false;}
                }
                break;
            }

        }
    }

    private void turnOnLight()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("a".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOffLight()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("A".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOnFan1()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("b".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOffFan1()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("B".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOnFan2()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("c".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOffFan2()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("C".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOnAc()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("d".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void turnOffAc()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("D".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        else {
            new ConnectBT().execute();
        }
    }
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Control.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice thisDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = thisDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
                //Log.d(TAG, "onPostExecute: " + "Working");
            }
            progress.dismiss();
        }
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
