/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp;

import java.io.*;
import java.net.Socket;

/**
 * 
 * @author drcher
 */
public class LwtpProcotol {

    protected static final int STATE_WAITING = 0x1;
    protected static final int STATE_MEETING = 0x2;
    protected static final int STATE_DATING = 0x3;
    protected static final int STATE_BREAK = 0x4;
    protected static final int FLAG_INFO = 0x69;
    protected static final int FLAG_MOUSE_MOVE = 0x51;
    protected static final int FLAG_MOUSE_CLICK = 0x54;
    protected static final int FLAG_MOUSE_PRESS = 0x55;
    protected static final int FLAG_MOUSE_RELEASE = 0x56;
    protected static final int FLAG_MOUSE_WHEEL = 0x5A;
    protected static final int FLAG_MOUSE_WHEEL_UP = 0x5B;
    protected static final int FLAG_MOUSE_WHEEL_DOWN = 0x5C; 
    protected static final int FLAG_KEYPRESS = 0x43;
    protected static final int FLAG_KEYRELEASE = 0x44;
    protected static final int FLAG_KEYCLICK = 0x45;
    protected static final int FLAG_WRITE = 0x1FF;
    protected static final int FLAG_READ = 0x0FF;
    
    
    protected static final String KATE = "hi! Jame?";
    protected static final String JIM = "hi, Kate!";
    protected static final String WOW = "You are very tall!";
    protected static final String BYE = "Bye!";
//    private static final int PAC_SIZE = 256;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    private int state = STATE_WAITING;
    private StateAgent stateAgent;
    
    public LwtpProcotol(Socket clientSocket) throws IOException {
        this(clientSocket.getInputStream(), clientSocket.getOutputStream());
    }
    
    public LwtpProcotol(InputStream is,OutputStream os){
        dis = new DataInputStream(is);
        dos = new DataOutputStream(os);
    }

    protected String readString(boolean verify){
        String data = null;
        try {
            if(verify){
                int flag = dis.readInt();
                if(flag != FLAG_INFO)
                    return data;
            }
                int len = dis.readInt();
                byte[] b = new byte[len];
                dis.read(b);
                data = new String(b);
            LwtpLog.v("HEAR:" + data); 
            return data;
        } catch (IOException ex) {
            LwtpLog.v("readString :"+ex.getMessage());
            handleException("readString",FLAG_INFO|FLAG_READ, ex);
            return data;
        }
    }
    
    protected void writeString(String s){
        try {
            LwtpLog.v("Say:" + s);
            int len = s.length();
            dos.writeInt(FLAG_INFO);
            dos.writeInt(len);
            dos.write(s.getBytes());
        } catch (IOException ex) {
            LwtpLog.v("writeString :"+ex.getMessage());
            handleException("writeString",FLAG_INFO|FLAG_WRITE, ex);
        }
    }

    public boolean isFun() {
        return state != STATE_BREAK;
    }

    public boolean isDating() {
        return state == STATE_DATING;
    }

    
    public void bye() {
        try {
            LwtpLog.v("bye");
            dis.close();
            dos.close();
            setState(STATE_BREAK);
        } catch (IOException ex) {
            handleException("bye", 0, ex);
        }
    }
    

    final protected  void handleException(String info , int flag ,Exception ex){
        if(stateAgent!=null){
                stateAgent.handleException(info, flag, ex);
            }
    }
    final protected int getState(){
        return state;
    }
    
    final protected  void setState(int state){
        int oldState= state;
        this.state = state;
        if(stateAgent!=null){
            stateAgent.onStateChanged(oldState, state);
        }
    }

    public StateAgent getStateAgent() {
        return stateAgent;
    }

    public void setStateAgent(StateAgent stateAgent) {
        this.stateAgent = stateAgent ;
    }
    
    
    
    public static interface StateAgent {
        /**
         * 
         * @param flag  0xABB a = write or read ; BB = Flag_xxx;
         * @param ex 
         */
        public void handleException(String info,int flag,Exception ex);
        
        public void onStateChanged(int oldState,int newState);
    }
}
