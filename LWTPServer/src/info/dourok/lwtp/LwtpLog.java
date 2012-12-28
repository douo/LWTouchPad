/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.dourok.lwtp;

/**
 * 
 * @author drcher
 */


public class LwtpLog {
    public static final String TAG ="lwtp";
    public static final boolean USE_VERBOSE =  true;
    public static final boolean USE_ERROR = false;
    public static final boolean USE_INFO = false;
    public static final boolean USE_WARING = false;
    public static final boolean USE_DEBUG = false;
    public static void v(String i){
        if(USE_VERBOSE){
            System.out.println("v:"+i);
        }
    }
    
    public static void i(String i){
        if(USE_INFO){
            System.out.println("i:"+i);
        }
    
    }
    
    public static void e(String i,Throwable tr){
        if(USE_ERROR){
            System.out.println("e:"+i);
            tr.printStackTrace();
        }
        
    }
}
