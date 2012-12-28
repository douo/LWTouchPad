/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.List;

/**
 *
 * @author DouO
 */
public class ServerListActivity extends Activity {
    public static String SERVER_NAME="server_name";
    
    
    private EditText nameTextField;
    private EditText hostTextField;
    private EditText pswTextField;
    private EditText portTextField;

    private ArrayAdapter serverListAdaper ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View listheader =inflater.inflate(R.layout.server_list_header, null);
        ServerObj.prepare(this);
        serverListAdaper = new ServerListAdaper(this, R.layout.server_list_view, ServerObj.list());
        nameTextField = (EditText) listheader.findViewById(R.id.server_name);
        pswTextField = (EditText) listheader.findViewById(R.id.server_psw);
        portTextField = (EditText) listheader.findViewById(R.id.server_port);
        hostTextField = (EditText) listheader.findViewById(R.id.server_host);
        View.OnFocusChangeListener emptyListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                LwtpLog.v(hasFocus + "");
                if (!hasFocus) {
                    EditText tx = (EditText) v;
                    if (tx.getText() == null || tx.getText().length() == 0) {
                        tx.setError(ServerListActivity.this.getResources().getString(R.string.msg_cannot_blank));
                    } else {
                        tx.setError(null);
                    }
                }
            }
        };
        hostTextField.setOnFocusChangeListener(emptyListener);
        portTextField.setOnFocusChangeListener(emptyListener);
        
       
        ListView lv = (ListView) findViewById(R.id.server_list);
        lv.addHeaderView(listheader,null,false);
        lv.setAdapter(serverListAdaper);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                connect(ServerObj.getServerObj(pos-1));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_list, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LwtpLog.v("ondestroy");
    }

    
    
    private boolean isValid(){
        return (hostTextField.getText() != null && hostTextField.getText().length() != 0
                    && portTextField.getText() != null && portTextField.getText().length() != 0);
    }
    
    private ServerObj updateServrObjFromTextField(){
        if(isValid()){
            ServerObj obj = ServerObj.updateServerObj(nameTextField.getText().toString(),
                    hostTextField.getText().toString(), 
                    Integer.parseInt(portTextField.getText().toString()));
            nameTextField.setText("");
            hostTextField.setText("");
            hostTextField.requestFocus();
            portTextField.setText("");
            pswTextField.setText("");
            serverListAdaper.notifyDataSetChanged();
            return obj;
        }
        return null;
    }
    
    private void save(){
          if(updateServrObjFromTextField()!=null)
              serverListAdaper.notifyDataSetChanged();
    }
    
    private void connect(ServerObj obj){
        Intent i = new Intent();
        i.putExtra(SERVER_NAME,obj.getName());
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void finish() {
        ServerObj.updateList();
        super.finish();
    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        MenuItem item = menu.findItem(R.id.server_list_menu_conn);
        if (item != null) {
                item.setEnabled(isValid());
            return true;
        }else
            return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.server_list_menu_conn:
                connect(updateServrObjFromTextField());
                break;
            case R.id.server_list_menu_cancel:
                finish();
        }
        return true;
    }
    private static class ServerListAdaper extends ArrayAdapter<ServerObj> {
        int resource;

        public ServerListAdaper(Context context, int textViewResourceId, List<ServerObj> objects) {
            super(context, textViewResourceId, objects);
            resource = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View curView = null;
            ServerObj obj = getItem(position);
            String s1=null;
            String s2=null;
            String dsc = obj.getHost()+":"+obj.getPort();
            if(obj.getName().equals("")){
                s1 = dsc;
            }else{
                s1 = obj.getName();
                s2 = dsc;
            }
            if(convertView==null){
                
                LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                curView = inflater.inflate(resource, parent, false);
            }else{
                curView =convertView;
            }
            TextView main = (TextView) curView.findViewById(R.id.server_list_view_main);
            TextView vice = (TextView) curView.findViewById(R.id.server_list_view_vice);
            
            main.setText(s1);
            vice.setText(s2);
            
            return curView;
        }
        
    }
}
