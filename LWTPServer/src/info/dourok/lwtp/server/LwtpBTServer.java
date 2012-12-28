/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp.server;

import info.dourok.lwtp.LwtpLog;
import java.awt.AWTException;
import java.io.IOException;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 *
 * @author DouO
 */
public class LwtpBTServer {
    static LocalDevice localDevice;
    private static final UUID uuid = new UUID("0000110100001000800000805f9b34fb",false);//00001101-0000-1000-8000-00805f9b34fb <--> 0x1101
    
    private String url;
    private boolean listening;
    private StreamConnection clientConn;
    private LwtpBTServerThread clientThread;
    private StreamConnectionNotifier notifier;
    private LwtpBTServer() throws BluetoothStateException {
        localDevice = LocalDevice.getLocalDevice();
    }
    public void start() throws IOException{
        LwtpLog.v(uuid.toString());
        url =  "btspp://localhost:" 
                + uuid + ";name=LwtpServer";
        notifier = (StreamConnectionNotifier) Connector.open(url);
        if(DiscoveryAgent.GIAC != localDevice.getDiscoverable()){ //确保设备可被发现
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);
        }
        listening = true;
        while(listening){
            clientConn = notifier.acceptAndOpen();
            //localDevice.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
             LwtpLog.v("connected");
            if (clientThread != null && clientThread.isAlive()) {
                clientThread.interrupt();
            }
            clientThread = new LwtpBTServerThread(clientConn);
            clientThread.start();
        }
        notifier.close();
    }
    public void stop() throws IOException{
        listening = false;
        notifier.close();
    }
    public static void main(String[] args) throws IOException {
        LwtpBTServer server = new LwtpBTServer();
        server.start();
    }
    
}


class LwtpBTServerThread extends Thread {

    private StreamConnection clientConn;
    private  RemoteDevice rDevice;
    public LwtpBTServerThread(StreamConnection clientConn) throws IOException {
        super("LwtpBTServerThread");
        this.clientConn = clientConn;
        rDevice = RemoteDevice.getRemoteDevice(clientConn);
        System.out.println("Remote device address :"+rDevice.getBluetoothAddress());
        System.out.println("Remote device name:"+rDevice.getFriendlyName(true));
    }
    
    @Override
    public void run() {
        Kate kate = null;
        try {
            kate = new Kate(clientConn.openInputStream(),clientConn.openOutputStream());
            while (kate.isFun()) {
                kate.chat();
            }
        } catch (IOException ex) {
            LwtpLog.e("Socket Close Fail", ex);
           
        } catch (AWTException ex) {
            LwtpLog.e("AWTException", ex);
        } finally {
            try {
                if (kate != null) {
                    kate.bye();
                }
                clientConn.close();
            } catch (IOException ex) {
                LwtpLog.e("Socket Close Fail", ex);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            LwtpLog.v("interrupt");
            clientConn.close();
        } catch (IOException ex){
            LwtpLog.e("Socket Close Fail", ex);
        }
    }
}