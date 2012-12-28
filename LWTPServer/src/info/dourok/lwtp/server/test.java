package info.dourok.lwtp.server;

import java.awt.*;
//import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
public class test{
    public static void main(String args[]) throws AWTException{
        java.awt.Robot robot = new java.awt.Robot();
	robot.setAutoDelay(1000);
	robot.mouseMove(10,19);
//        robot.keyPress(KeyEvent.VK_H);
        PointerInfo pi = MouseInfo.getPointerInfo();
        while(true){
            Point mp =pi.getLocation();
            sleep(50);
            System.out.println(mp);
        }
    }
public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
}
}