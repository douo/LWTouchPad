/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.dourok.lwtp;

import android.util.Log;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * 
 * @author drcher
 */


public class LwtpLog {
    public static String TAGS = "LWTP";
    public static final CharArrayWriter caw =new CharArrayWriter();
    public static final PrintWriter pw =new PrintWriter(caw);
    public static final boolean USE_VERBOSE =  true;
    public static final boolean USE_ERROR = true;
    public static final boolean USE_INFO = true;
    public static final boolean USE_WARING = true;
    public static final boolean USE_DEBUG = false;
    
    private LwtpLog(){}
    
    public static void v(String i){
        if(USE_VERBOSE){
            Log.v(TAGS, i);
        }
    }
    public static void i(String i){
        if(USE_INFO){
            Log.i(TAGS, i);
        }
    }
    
    public static void e(String i,Throwable tr){
        if(USE_ERROR){
            Log.e(TAGS, i ,tr);
        }
    }
    public static void w(String i){
        if(USE_WARING){
            Log.w(TAGS, i);
        }
    }
    
    public static void printf(String format ,Object ... args){
        if(USE_INFO){
            pw.printf(format, args);
            String s = new String(caw.toCharArray());
            caw.reset();
            v(s);
        }
    }
}
