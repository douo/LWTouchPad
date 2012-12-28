/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author drcher
 */
public class NewClass {
    
    public static void t(String p){
        int i = p.indexOf(' ');
        String t = p.substring(0,i);
        p = p.substring(i+1,p.length());
        //id
        System.out.println((Integer.parseInt(t)));
        //name
        i = p.indexOf(' ');
        t = p.substring(0,i);
        p = p.substring(i+1,p.length());
        System.out.println(t);
        i = p.indexOf(' ');
        t = p.substring(0,i);
        p = p.substring(i+1,p.length());
        System.out.println(t);
        System.out.println(Integer.parseInt(p));
    }
    
    public static void main(String[] args) {
        if(true){
            System.out.println("abcdefgabcedfg".replace('a', 'z'));
            t("1 skdj_dsfjl lld 39");
            //test();
            return;
        }
        JFrame f = new JFrame("abc");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);
                System.out.println(ke.toString());
            }
            
        });
        
        f.setVisible(true);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Robot r = new Robot();
                    Thread.sleep(5000);
                    r.keyPress(72);
                    r.keyRelease(72);
                    r.keyPress(65480);
                    r.keyRelease(65480);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AWTException ex) {
                    Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
        }).start();
//        double x,y;
//        int i=0,j=0;
//        while(i<1000000000){
//            x = Math.random();
//            y = Math.random();
//            i++;
//            if(x*x+y*y<=1){
//                j++;
//            }
//         
//        }
//           System.out.println(j+"/"+i+":"+(4.0*j/i));
    }
    
    public static void test(){
        int i=0;
        int k=0;
        long a = System.currentTimeMillis();
        for(;i<1000000000;i++)for(int j=0;j<1000000000;j++){k++;a();}
        System.out.println(System.currentTimeMillis()-a);
        System.out.println(k);
    }
    public static final boolean d = false;
    public static void a(){if(d){Math.random();}
    }
}
