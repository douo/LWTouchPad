package info.dourok.lwtp.client;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import info.dourok.lwtp.LwtpLog;
import info.dourok.lwtp.client.LwtpClient;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DouO
 */
public class LwtpBluetoothClient extends LwtpClient{
    
    private static LwtpBluetoothClient defaultClent;
    
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private LwtpBluetoothClient() {
    }
    
    public boolean connect(BluetoothDevice device){
         BluetoothSocket tmp = null;
         try {
            // MY_UUID is the app's UUID string, also used by the server code
            mDevice = device;
            tmp = mDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
             LwtpLog.e("createInsecureRfcommSocketToServiceRecord",e); 
             return false;
        }
         
        mSocket = tmp;
        try {
            mSocket.connect();
            is = mSocket.getInputStream();
            os = mSocket.getOutputStream();
        } catch (IOException ex) {
            LwtpLog.e("BlueTooth Socket Connect Fail",ex); 
            try {
                mSocket.close();
            } catch (IOException ex1) {
            }
        }
        
        return init();
    }
    
    public static LwtpBluetoothClient getClient(){
        if(defaultClent==null){
            defaultClent = new LwtpBluetoothClient();
        }
        return defaultClent;
    }

    @Override
    protected void close() throws IOException {
        if(mSocket!=null){
            mSocket.close();
        }
    }
    
    
   
    
}
