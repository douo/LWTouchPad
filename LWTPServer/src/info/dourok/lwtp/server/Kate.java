/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.server;

import info.dourok.lwtp.LwtpLog;
import info.dourok.lwtp.LwtpProcotol;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * @author drcher
 */
public class Kate extends LwtpProcotol{
    private Robot robot;
    private long timeModified;
    public Kate(Socket clientSocket) throws IOException, AWTException {
        super(clientSocket);
        robot = Robot.getInstance();
    }
    public Kate(InputStream is,OutputStream os) throws IOException, AWTException {
        super(is,os);
        robot = Robot.getInstance();
    }
    public void chat(){
        switch (getState()) {
            case STATE_WAITING:
                writeString(KATE);
                setState(STATE_MEETING);
                break;
            case STATE_MEETING:
                if (JIM.equals(readString(true))) {
                    updateTime();
                    setState(STATE_DATING);
                    writeString(WOW);   //write some machine info
                } else {
                    setState(STATE_MEETING);
                }
                break;
            case STATE_DATING:
                listen();
                break;
            case STATE_BREAK:
                
                break;
        }
    }
    
    private void updateTime(){
        try {
            long clientTime = dis.readLong();
            timeModified = clientTime - System.currentTimeMillis();
        } catch (IOException ex) {
            timeModified = -1;
            handleException("updateTime", 0, ex);
        }
    }
    
//    long t = 0;
    private void listen(){       
        int flag=0;
        try{
            flag = dis.readInt();
            int btn;
            switch (flag){
                case FLAG_MOUSE_MOVE:
                    int dx = dis.readInt();
                    int dy = dis.readInt();
                    LwtpLog.v("dx :"+ dx +"  dy:"+dy);
                    robot.mouseMove(dx, dy);
                    break;
                case FLAG_MOUSE_CLICK:
                    btn = dis.readInt();
                    LwtpLog.v("Mouse CLick:"+btn);
                    robot.mouseClick(btn);
                    break;                    
                case FLAG_MOUSE_WHEEL_UP:
                    robot.mouseWheelUp();
                    break;
                case FLAG_MOUSE_WHEEL_DOWN:
                    robot.mouseWheelDown();
                    break;
                case FLAG_MOUSE_PRESS:
                    btn = dis.readInt();
                    LwtpLog.v("Mouse Press:"+btn);
                    robot.mousePress(btn);
                    break;
                case FLAG_MOUSE_RELEASE:
                    btn = dis.readInt();
                    LwtpLog.v("Mouse Release:"+btn);
                    robot.mouseRelease(btn);
                    break;
                case FLAG_KEYCLICK:
                    btn = dis.readInt();
                    LwtpLog.v("Key Click:"+btn+" "+KeyEvent.getKeyText(btn));
                    robot.doType(btn);
                    break;
                case FLAG_KEYPRESS:
                    btn = dis.readInt();
                    LwtpLog.v("Key Press:"+btn);
                    robot.keyPress(btn);
                    break;
                case FLAG_KEYRELEASE:
                    btn = dis.readInt();
                    LwtpLog.v("Key Release:"+btn);
                    robot.keyRelease(btn);
                    break;
                case FLAG_INFO:
                    String s =readString(false);
                    if(s.equals(BYE)){
                        bye();
                    }
            }
        }catch(IOException ex){
            handleException("listen", flag|FLAG_READ, ex);
        }
    }
    
}
