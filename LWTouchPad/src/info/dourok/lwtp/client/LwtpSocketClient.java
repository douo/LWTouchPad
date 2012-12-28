/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp.client;

import info.dourok.lwtp.LwtpLog;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author DouO
 */
public class LwtpSocketClient  extends LwtpClient{
     private Socket clientSocket = null;
     private static LwtpSocketClient defaultClient = new LwtpSocketClient();
    private LwtpSocketClient() {
    }
    public static LwtpSocketClient getClient(){
        return defaultClient;
    }
    
     public boolean connect(Socket socket) {
        try {
            clientSocket = socket;
            
            if (!clientSocket.getTcpNoDelay()) {
                clientSocket.setTcpNoDelay(true);
            }
            is = socket.getInputStream();
            os = socket.getOutputStream();
            LwtpLog.i("Connecting");
            LwtpLog.v(socket.getInetAddress().getHostAddress()+"("+socket.getInetAddress().getCanonicalHostName()+"):"+socket.getPort());
            connecting=true;
            return init();
        } catch (IOException ex) {
            LwtpLog.e("Connecting Error", ex);
            return false;
        }
    }

    @Override
    protected void close() throws IOException {
        if(clientSocket!=null){
            clientSocket.close();
        }
    }
     
     

    public boolean connect(String host, int port) {
        try {
            return connect(new Socket(InetAddress.getByName(host), port));
        } catch (IOException ex) {
            LwtpLog.e("Error", ex);
            return false;
        }
    }
    
    
    
    
}
