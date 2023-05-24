package com.example.jayvisiotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ProfileFragment extends Fragment {
    Button GetDevBtn, ConnectBtn, SendBtn, SendBtnON, SendBtnOFF;
    EditText InputText, InputText2;
    TextView ResultText;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    IntentFilter intentFilter;

    InputStream inputStream;
    OutputStream outputStream;
    RxThread rxThread;
    String RxData = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_profile, container, false);

        GetDevBtn = (Button) myView.findViewById(R.id.btn1);
        ConnectBtn = (Button) myView.findViewById(R.id.btn2);
        SendBtn = (Button) myView.findViewById(R.id.btn3);
        SendBtnON = (Button) myView.findViewById(R.id.btn4);
        SendBtnOFF = (Button) myView.findViewById(R.id.btn5);
        InputText = (EditText) myView.findViewById(R.id.editText1);
        InputText2 = (EditText) myView.findViewById(R.id.editText2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        rxThread = new RxThread();

        ConnectBtn.setEnabled(false);

        GetDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                List<BluetoothDevice> devices = (List<BluetoothDevice>) bluetoothAdapter.getBondedDevices();

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

                    for (BluetoothDevice dev : devices) {
                        if (dev.getName().equals("HC-05")) {
                            bluetoothDevice = dev;
                            bluetoothAdapter.cancelDiscovery();
                            break;
                        }
                    }
                    return;
                }

            }
        });

        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        bluetoothSocket.connect();

                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                        rxThread.start();
                        BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
                        Toast.makeText(getActivity(),"Connected.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }catch (Exception e){

                }
            }
        });

        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write((InputText.getText()+"").getBytes());
                }catch (Exception e){

                }
            }
        });

        SendBtnON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("1".getBytes());
                }catch (Exception e){

                }
            }
        });

        SendBtnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream.write("0".getBytes());
                }catch (Exception e){

                }
            }
        });


        BroadcastReceiver Btreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ConnectBtn.setEnabled(true);
                            }
                        });
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        rxThread.isRunning = false;
                        break;
                }
            }
        };

        getActivity().registerReceiver(Btreceiver, intentFilter);

        return myView;
    }

    private class RxThread extends Thread{
        public boolean isRunning;
        byte[] rx;
        RxThread(){
            isRunning = true;
            rx = new byte[10];
        }
        @Override
        public void run(){
            while(isRunning){
                try {
                    if(inputStream.available() > 0){
                        inputStream.read(rx);
                        RxData = new String(rx);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!RxData.equals("")){
                                InputText2.setText(RxData);
                                RxData = "";
                            }
                        }
                    });
                    Thread.sleep(10);
                }catch (Exception e){}
            }
        }
    }
}