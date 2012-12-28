/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.server;

import info.dourok.lwtp.LwtpLog;
import info.dourok.lwtp.LwtpProcotol;
import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 
 * @author drcher
 */
public class LwtpServer {

    private boolean listening;
    private ServerSocket serverSocket = null;
    private int port;
    private Socket clientSocket = null;
    LwtpServerThread lwtpServerThread = null;

    public LwtpServer() {
        this(8964);
    }

    public LwtpServer(int port) {
        listening = false;
        this.port = port;
        
    }

    public void start() throws IOException, AWTException {
        serverSocket = new ServerSocket(port);
        //serverSocket.setReceiveBufferSize(128);
        listening = true;
        while (listening) {
            clientSocket = serverSocket.accept();            

            LwtpLog.v("connected");
            if (lwtpServerThread != null && lwtpServerThread.isAlive()) {
                lwtpServerThread.interrupt();
            }
            lwtpServerThread = new LwtpServerThread(clientSocket);
            lwtpServerThread.start();
        }
        serverSocket.close();
    }

    //TODO fill it
    public void stop() throws IOException {
        listening = false;
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException, AWTException {
        LwtpServer lwtpServer = new LwtpServer();
        lwtpServer.start();
    }
}

class LwtpServerThread extends Thread {

    private Socket clientSocket;
    private static StateAgentImpl stateAgent  = new StateAgentImpl();
    private  Kate kate ;
    public LwtpServerThread(Socket clientSocket) {
        super("LwtpServerThread");
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
       
        try {
            kate = new Kate(clientSocket);
            kate.setStateAgent(stateAgent);
            while (kate.isFun()) {
                kate.chat();
            }
        } catch (IOException ex) {
            LwtpLog.e("Socket Close Fail", ex);
        } catch (AWTException ex) {
            LwtpLog.e("AWTException", ex);
        } finally {
            try {
                //Kata打开的流已经被关闭
                clientSocket.close();
            } catch (IOException ex) {
                LwtpLog.e("Socket Close Fail", ex);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            // 确保流已被关闭
            if(kate.isFun()){
                kate.bye();
            }
            LwtpLog.v("interrupt");
            clientSocket.close();
        } catch (IOException ex){
            LwtpLog.e("Socket Close Fail", ex);
        }
    }
    
    final static class StateAgentImpl implements  LwtpProcotol.StateAgent{

        @Override
        public void handleException(String info, int flag, Exception ex) {
            LwtpLog.e(info, ex);
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
            LwtpLog.v(newState+"");
        }
        
    }
}
