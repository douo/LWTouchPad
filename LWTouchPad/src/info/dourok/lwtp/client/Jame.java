/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.client;

import info.dourok.lwtp.LwtpLog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author drcher
 */
public class Jame extends LwtpProcotol{

    public Jame(Socket clientSocket) throws IOException {
        super(clientSocket);
    }

    public Jame(InputStream is, OutputStream os) {
        super(is, os);
    }
    
    
    public void chat(){
        switch (getState()) {
            case STATE_WAITING:
                if (KATE.equals(readString(true))) {
                    writeString(JIM);
                    sayTime();
                    setState(STATE_MEETING);
                }
                break;
            case STATE_MEETING:
                if (WOW.equals(readString(true))) {
                    setState(STATE_DATING);
                } else {
                    setState(STATE_MEETING);//FIX
                }
                break;
            case STATE_DATING:
                break;
            case STATE_BREAK:
                break;
        }
    }
    
    private void sayTime(){
        try {
            dos.writeLong(System.currentTimeMillis());
        } catch (IOException ex) {
            handleException("sayTime", FLAG_WRITE, ex);
        }
    }
    public void say(int[] bs){
        try{
            for(int i=0;i<bs.length;i++){
                dos.writeInt(bs[i]);
            }
        }catch(IOException ex){
            handleException("say", FLAG_WRITE|bs[0], ex);
        }
    }
    public void sayMouseMove(int dx ,int dy){
        say(new int[]{FLAG_MOUSE_MOVE,dx,dy});
    }
    public void sayMouseWheelUp(){
        say(new int[]{FLAG_MOUSE_WHEEL_UP});
    }
    public void sayMouseWheelDown(){
        say(new int[]{FLAG_MOUSE_WHEEL_DOWN});
    }
    public void sayMouseClick(int btn){
        say(new int[]{FLAG_MOUSE_CLICK,btn});
    }
    public void sayMousePress(int btn){
        say(new int[]{FLAG_MOUSE_PRESS,btn});
    }
    public void sayMouseRelease(int btn){
        say(new int[]{FLAG_MOUSE_RELEASE,btn});
    }
    public void sayDoType(int btn){
        say(new int[]{FLAG_KEYCLICK,btn});
    }
    public void sayKeyPress(int btn){
        say(new int[]{FLAG_KEYPRESS,btn});
    }
    public void sayKeyRelease(int btn){
        say(new int[]{FLAG_KEYRELEASE,btn});
    }
}
