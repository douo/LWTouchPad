/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp.client;


import info.dourok.lwtp.LwtpLog;
import java.io.IOException;
import static info.dourok.lwtp.client.Jame.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;




/**
 * 提供与服务器交互的基本接口
 *
 * @author drcher
 */
public abstract class LwtpClient {

    public static final int MOUSE_BUTTON_1 = 0x51;
    public static final int MOUSE_BUTTON_2 = 0x52;
    public static final int MOUSE_BUTTON_3 = 0x53;
    public static final long CLICK_DELAY = 500;
    
    protected InputStream is;
    protected OutputStream os;
    private Jame jame;
    private ArrayBlockingQueue<int[]> bQueue;
    protected boolean connecting;
    private StateAgentImpl stateAgent  = new StateAgentImpl();
    protected final  boolean init(){
            jame = new Jame(is,os);
            jame.setStateAgent(stateAgent);
                while (!jame.isDating()) {
                    jame.chat();
                }
                bQueue = new ArrayBlockingQueue<int[]>(10);
                new EventConsumer().start();
        connecting = true;
        return true;
    }
    
    
    
    public final  boolean isConnect(){
        return connecting;
    }
    
    protected abstract void close() throws IOException;
    
    public final void disconnect(){
        try {
            close();
            connecting = false;
        } catch (IOException ex) {
            LwtpLog.e("Close Fail", ex);
        }
    }
    
    public final void mouseMove(int dx, int dy) throws IOException {
        int[] t = new int[]{FLAG_MOUSE_MOVE,dx,dy};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }

    public final void mouseClick(int btn) throws IOException {
        int [] t = new int[]{FLAG_MOUSE_CLICK,btn};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }

    public final void keyPress(int kcode) {
        int [] t = new int[]{FLAG_KEYPRESS,kcode};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }
    
    public final void keyRelease(int kcode){
        int [] t = new int[]{FLAG_KEYRELEASE,kcode};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }
    
    public final void doType(int kcode){
        int [] t = new int[]{FLAG_KEYCLICK,kcode};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }
    

    public final void mouseWheelUp() throws IOException {
        int [] t = new int[]{FLAG_MOUSE_WHEEL_UP};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }

    public final void mouseWheelDown() throws IOException {
        int [] t = new int[]{FLAG_MOUSE_WHEEL_DOWN};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }

    public final void mousePress(int btn) throws IOException {
        int [] t = new int[]{FLAG_MOUSE_PRESS,btn};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }

    public final void mouseRelease(int btn) throws IOException {
        int [] t = new int[]{FLAG_MOUSE_RELEASE,btn};
        try {
            bQueue.put(t);
        } catch (InterruptedException ex) {
            LwtpLog.e(null, ex);
        }
    }    

    final  class EventConsumer extends Thread {
        @Override
        public void run() {
            while(isConnect()){
                try {
                    int[] t = bQueue.take();
                    jame.say(t);
//                    switch(t[0]){
//                        case FLAG_MOUSE_MOVE:
//                            jame.sayMouseMove(t[1], t[2]);
//                            break;
//                        case FLAG_MOUSE_CLICK:
//                            jame.sayMouseClick(t[1]);
//                            break;
//                        case FLAG_MOUSE_PRESS:
//                            jame.sayMousePress(t[1]);
//                            break;
//                        case FLAG_MOUSE_RELEASE:
//                            jame.sayMouseRelease(t[1]);
//                            break;
//                        case FLAG_MOUSE_WHEEL_DOWN:
//                            jame.sayMouseWheelDown();
//                            break;
//                        case FLAG_MOUSE_WHEEL_UP:
//                            jame.sayMouseWheelUp();
//                            break;
//                        case FLAG_KEYPRESS:
//                            break;
//                    }
                }catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    final class StateAgentImpl implements  LwtpProcotol.StateAgent{

        @Override
        public void handleException(String info, int flag, Exception ex) {
            LwtpLog.e(info, ex);
            disconnect();
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
            LwtpLog.v(newState+"");
        }
    }
}
