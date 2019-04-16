package com.example.dixon.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button mBT1;
    public Button mBT2;
    public ListView mLV1;
    public ListView mLV2;
    public ArrayList<String> list1=new ArrayList<>();
    public ArrayAdapter<String> adapter1;
    public ArrayList<String> list2=new ArrayList<>();
    public ArrayAdapter<String> adapter2;
    public String msg;
    public Button mS1;
    public Button mS2;
    public Button mS3;
    public Button mS4;
    public Button mS5;
    public Button mS6;

    BluetoothAdapter mBA=BluetoothAdapter.getDefaultAdapter();
    public BluetoothDevice device;
    BluetoothSocket mSocket;
    OutputStream os;
    /** unregister broadcast **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(uReceiver);
        unregisterReceiver(cReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBT1=findViewById(R.id.b1);
        mLV1=findViewById(R.id.lv1);
        adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list1);
        mLV1.setAdapter(adapter1);

        mBT2=findViewById(R.id.b2);
        adapter2=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list2);
        mLV2=findViewById(R.id.lv2);
        mLV2.setAdapter(adapter2);
        // Register for broadcasts when a device is discovered.
        IntentFilter mFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver,mFilter);
        //Register for broadcasts when device discovery finished.
        IntentFilter uFilter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(uReceiver,uFilter);
        //Register for broadcasts when bluetooth is connected.
        IntentFilter cFilter=new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(cReceiver,cFilter);

        mS1=findViewById(R.id.s1);
        mS1.setOnClickListener(this);
        mS2=findViewById(R.id.s2);
        mS2.setOnClickListener(this);
        mS3=findViewById(R.id.s3);
        mS3.setOnClickListener(this);
        mS4=findViewById(R.id.s4);
        mS4.setOnClickListener(this);
        mS5=findViewById(R.id.s5);
        mS5.setOnClickListener(this);
        mS6=findViewById(R.id.s6);
        mS6.setOnClickListener(this);

/** check bluetooth function and get bonded devices **/
        if(mBA!=null){
            if(mBA.isEnabled()){
                list1.clear();
                Set<BluetoothDevice> pairedDevice=mBA.getBondedDevices();
                if(pairedDevice.size()>0){
                    for(BluetoothDevice device:pairedDevice){
                        String name=device.getName();
                        String address=device.getAddress();
                        String str=name+"="+address;
                        list1.add(str);
                        adapter1.notifyDataSetChanged();
                    }
                }
            }else {
                Intent BTenable=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(BTenable);
            }

        }
        else {
            Toast.makeText(MainActivity.this,"此设备不支持蓝牙！！！",Toast.LENGTH_LONG).show();
        }
/**connect bundled bluetooth **/
        mLV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s=adapter1.getItem(position);
                String address=s.substring(s.indexOf("=")+1).trim();
                device=mBA.getRemoteDevice(address);
                UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try{
                    mSocket=device.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                    os=mSocket.getOutputStream();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(mSocket.isConnected()){
                    mS1.setEnabled(true);
                    mS2.setEnabled(true);
                    mS3.setEnabled(true);
                    mS4.setEnabled(true);
                    mS5.setEnabled(true);
                    mS6.setEnabled(true);
                }
            }
        });
/** connect searched bluetooth **/
        mLV2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s=adapter2.getItem(position);
                String address=s.substring(s.indexOf("=")+1).trim();
                device=mBA.getRemoteDevice(address);
                UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try{
                    mSocket=device.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                    os=mSocket.getOutputStream();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(mSocket.isConnected()){
                    mS1.setEnabled(true);
                    mS2.setEnabled(true);
                    mS3.setEnabled(true);
                    mS4.setEnabled(true);
                    mS5.setEnabled(true);
                    mS6.setEnabled(true);

                }
            }
        });
    }
    /** define broadcast for ACTION_ACL_CONNECTED **/
    private BroadcastReceiver cReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
                Toast.makeText(MainActivity.this,"连接完成----",Toast.LENGTH_LONG).show();
                mBT1.setText("已配对的蓝牙设备");
            }
        }
    };
    /** define broadcast for ACTION_FOUND **/
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name=device.getName();
                String add=device.getAddress();
                String str=name+"="+add;
                list2.add(str);
                adapter2.notifyDataSetChanged();
            }
        }
    };
    /** define broadcast for ACTION_DISCOVERY_FINISHED **/
    private BroadcastReceiver uReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                Toast.makeText(MainActivity.this,"搜索完成！！！",Toast.LENGTH_LONG).show();
                mBT2.setText("搜索周围蓝牙");
            }
        }
    };
    /** send message **/
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.s1:
                msg = "1";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s2:
                msg = "2";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s3:
                msg = "3";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s4:
                msg = "4";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s5:
                msg = "5";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s6:
                try{
                    mSocket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!mSocket.isConnected()){
                    mS1.setEnabled(false);
                    mS2.setEnabled(false);
                    mS3.setEnabled(false);
                    mS4.setEnabled(false);
                    mS5.setEnabled(false);
                    mS6.setEnabled(false);
                }
                break;
        }
    }
    /** start bluetooth discovery  **/
    public void b2_click(View view){
        list2.clear();
        mBA.startDiscovery();
        mBT2.setText("正在搜索。。。。。。");
    }

}