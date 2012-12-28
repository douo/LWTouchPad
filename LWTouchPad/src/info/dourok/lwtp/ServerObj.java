/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DouO
 */
public class ServerObj implements Comparable<ServerObj> {

    private static ArrayList<ServerObj> serverList = new ArrayList<ServerObj>();
    private static Context context;
    private static String storedName = "server_list.cfg";
    private static ServerObj curServerObj;
    private static ServerObj defaultServerObj;

    public static ArrayList<ServerObj> list() {
        return serverList;
    }

    public static void prepare(Context c) {
        try {
            context = c;
            loadList();
        } catch (Exception e) {
            context = null;
        }
    }
    
    private static void loadList() throws IOException{
        serverList.clear();
        File f = new File(context.getExternalFilesDir(null), storedName);
            if (!f.exists()) {
                f.createNewFile();
                updateServerObj("", "home", 8964);
                updateList();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String tmp = br.readLine();
                while(tmp!=null){
                    ServerObj obj = new ServerObj();
                    if(tmp.startsWith("*")){
                        tmp = tmp.substring(1);
                        defaultServerObj = obj;
                    }
                    try{
                        obj.gen(tmp);
                    }catch(IndexOutOfBoundsException ex){
                        tmp = br.readLine();
                        continue;
                    }
                    serverList.add(obj);
                     tmp = br.readLine();
                }
                br.close();
            }
    }
    
    public static boolean updateList() {
        boolean result  = false;
        if(context==null)
            return result;
        File f = new File(context.getExternalFilesDir(null), storedName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            for(ServerObj obj: serverList){
                writer.append(obj.toString());
                writer.newLine();
            }
            writer.flush();
            writer.close();
            result =true;
        } catch (IOException ex) {
            result = false;
            Logger.getLogger(ServerObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
        
    }
    
    public static ServerObj updateServerObj(String name,String host,int post){
        ServerObj obj = new ServerObj();
        obj.setName(name);
        obj.setHost(host);
        obj.setPort(post);
        serverList.remove(obj);
        addOrderly(obj);
        return obj;
    }
    
    public static ServerObj updateServerObj(ServerObj obj,String name,String host,int post){
        if(!serverList.contains(obj)){
            if(!obj.getName().equals(name)){
                obj.setName(name);
                serverList.remove(obj);
                addOrderly(obj);
            }
            obj.setHost(host);
            obj.setPort(post);
            return obj;
        }else{
            return updateServerObj(name, host, post);
        }
    }
    
    private static void addOrderly(ServerObj obj){
        for(int i =0 ; i<serverList.size();i++){
            if(serverList.get(i).compareTo(obj)>0){
                serverList.add(i, obj);
                return;
            }else if(serverList.get(i).compareTo(obj)==0){
                return ;
            }
        }
        serverList.add(obj);
    }
    
    public static ServerObj getServerObj(String name){
        for(ServerObj obj:serverList){
            if(obj.getName().equals(name)){
                return obj;
            }
        }
        return null;
    }
    public static ServerObj getServerObj(int idx){
        
        return serverList.get(idx);
    }
    
    public static ServerObj getDefault(){
        return defaultServerObj;
    }
    
    
    private String host;
    private int port;
    private String psw; //TODO 等待服务器支持密码验证
    private String name;

    private void gen(String p) throws IndexOutOfBoundsException{
        int i = p.indexOf(' ');
        String t = p.substring(0,i);
        p = p.substring(i+1,p.length());
        //name
        setName(t);
        i = p.indexOf(' ');
        t = p.substring(0,i);
        p = p.substring(i+1,p.length());
        setHost(t);
        setPort(Integer.parseInt(p));
    }
    
    
    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }
    /**
     * 不能包含*号,因其用于标记default.下划线等同于空格
     * @param name 
     */
    private void setName(String name) {
        this.name = name.replace('_', ' ').replace("*", "");
    }

    public int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerObj other = (ServerObj) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return name.replace(' ', '_')+" "+host +" "+port;
    }

    public int compareTo(ServerObj obj) {
        return getName().compareTo(obj.getName());
    }
    
}
