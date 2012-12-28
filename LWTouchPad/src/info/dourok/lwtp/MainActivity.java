/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package info.dourok.lwtp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import info.dourok.lwtp.client.LwtpBluetoothClient;
import info.dourok.lwtp.client.LwtpSocketClient;
import info.dourok.lwtp.ui.LwtpTouchPad;

/**
 *
 * @author drcher
 */
public class MainActivity extends Activity {

    private final static int WAITING_DIALOG = 0x022;
    private final static int TYPE_WIRELESS = 0x01;
    private final static int TYPE_BLUETOOTH = 0x10;
    private int type;
    private final static String DIALOG_MESSAGE_TAG = "info.dourok.lwtp.MainActivity:DIALOG_MESSAGE_TAG";
    private LwtpSocketClient mSocketClient;
    private LwtpBluetoothClient mBluetoothClient;
    private final WirelessHandler mWirelessHandler = new WirelessHandler();
    private final BluetoothHandler mBluetoothHandler = new BluetoothHandler();
    private final MyUIHandler mUIHandler = new MyUIHandler();
    private LwtpTouchPad mTouchPad;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTouchPad = new LwtpTouchPad(this);
        mTouchPad.init();
        setContentView(mTouchPad);
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("TYPE") == TYPE_WIRELESS) {
                mSocketClient = (LwtpSocketClient) getLastNonConfigurationInstance();
                mUIHandler.sendEmptyMessage(MyUIHandler.MESSAGE_UPDATE_TOUCHPAD_CLIENT);
            } else if (savedInstanceState.getInt("TYPE") == TYPE_BLUETOOTH) {
                mBluetoothClient = (LwtpBluetoothClient) getLastNonConfigurationInstance();
                mUIHandler.sendEmptyMessage(MyUIHandler.MESSAGE_UPDATE_TOUCHPAD_BTCLIENT);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        //0.004 Drakest
        //setBrightness_1(0.01f);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        Dialog dialog = null;
        switch (id) {
            case WAITING_DIALOG:
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(getResources().getString(R.string.waiting_dialog_title));
                dialog = progressDialog;
                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle b) {
        switch (id) {
            case WAITING_DIALOG:
                if (b.containsKey(DIALOG_MESSAGE_TAG)) {
                    ProgressDialog pd = (ProgressDialog) dialog;
                    pd.setMessage(b.getString(DIALOG_MESSAGE_TAG));
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_conn:
                mBluetoothHandler.init();
                return true;
            case R.id.wireless_conn:
                mWirelessHandler.init();
                return true;
        }
        return false;
    }

    private void setBrightness_1(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        LwtpLog.i("screenBrightness:" + lp.screenBrightness);
        lp.screenBrightness = f;
        getWindow().setAttributes(lp);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        LwtpLog.v("Down:" + keyCode + " :" + kcm.getNumber(keyCode));
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        LwtpLog.v("LongPress:" + keyCode + " :" + event.toString());
//        return super.onKeyLongPress(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        LwtpLog.v("Up:" + keyCode + " :" + event.toString());
//        return super.onKeyUp(keyCode, event);
//    }
//    KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.ALPHA);
//
//    @Override
//    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
//        LwtpLog.v("Multiple:" + keyCode + " :" + event.toString() + ": " + repeatCount);
//        return super.onKeyMultiple(keyCode, repeatCount, event);
//    }
    /*
     * BLUTTOOTH
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("TYPE", type);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (type == TYPE_WIRELESS) {
            return mSocketClient;
        }
        if (type == TYPE_BLUETOOTH) {
            return mBluetoothClient;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BluetoothHandler.REQUEST_ENABLE_BLUETOOTH) {

                LwtpLog.v("OK");
                mBluetoothHandler.prepare();

            } else if (requestCode == WirelessHandler.REQUEST_SERVER_INFO) {
                LwtpLog.v("OK");
                mWirelessHandler.prepare(data.getStringExtra(ServerListActivity.SERVER_NAME));
            }
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    final class MyUIHandler extends Handler {

        public static final int MESSAGE_UPDATE_TOUCHPAD_BTCLIENT = 0x9;
        public static final int MESSAGE_UPDATE_TOUCHPAD_CLIENT = 0x8;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_TOUCHPAD_CLIENT:
                    type = TYPE_WIRELESS;
                    mTouchPad.updateClient(mSocketClient);
                    break;
                case MESSAGE_UPDATE_TOUCHPAD_BTCLIENT:
                    type = TYPE_BLUETOOTH;
                    mTouchPad.updateClient(mBluetoothClient);
                    break;
            }
        }
    }

    final class WirelessHandler {

        private static final int REQUEST_SERVER_INFO = 0x122;
        private Bundle b;

        public void init() {
            if (b == null) {
                b = new Bundle();
                b.putString(DIALOG_MESSAGE_TAG, getResources().getString(R.string.msg_server_connecting));
            }
            
            mSocketClient = LwtpSocketClient.getClient();
            Intent i = new Intent(MainActivity.this, ServerListActivity.class);

            startActivityForResult(i, REQUEST_SERVER_INFO);
        }

        public void prepare(final String name) {
            showDialog(WAITING_DIALOG, b);
            new Thread() {

                @Override
                public void run() {
                    ServerObj obj = ServerObj.getServerObj(name);
                    if (obj != null) {
                        mSocketClient.connect(obj.getHost(), obj.getPort());
                        mUIHandler.sendEmptyMessage(MyUIHandler.MESSAGE_UPDATE_TOUCHPAD_CLIENT);
                    }
                    removeDialog(WAITING_DIALOG);

                }
            }.start();
        }
    }

    final class BluetoothHandler {

        private static final int REQUEST_ENABLE_BLUETOOTH = 0x121;
        BluetoothAdapter mBluetoothAdapter;
        BluetoothDevice curDevice;
        private Bundle b;

        private void init() {
            if (b == null) {
                b = new Bundle();
                b.putString(DIALOG_MESSAGE_TAG, getResources().getString(R.string.msg_server_connecting));
            }
            mBluetoothClient = LwtpBluetoothClient.getClient();
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                prepare();
            }
        }

        private void prepare() {
            Intent devicepickerIntent = new Intent("android.bluetooth.devicepicker.action.LAUNCH"); //不能确定是否所有设备上都有这个Action
            if (curDevice == null) {
                registerReceiver(mReceiver, new IntentFilter("android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
                startActivity(devicepickerIntent);
            }

        }
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if ("android.bluetooth.devicepicker.action.DEVICE_SELECTED".equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    curDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    LwtpLog.i("device find:" + curDevice.getName() + " : " + curDevice.getAddress());
                    showDialog(WAITING_DIALOG, b);
                    unregisterReceiver(mReceiver);
                    new Thread() {

                        @Override
                        public void run() {
                            mBluetoothAdapter.cancelDiscovery();
                            mBluetoothClient.connect(curDevice);
                            dismissDialog(WAITING_DIALOG);
                            mUIHandler.sendEmptyMessage(MyUIHandler.MESSAGE_UPDATE_TOUCHPAD_BTCLIENT);
                        }
                    }.start();
                }
            }
        };
//    private void startDiscovery() {
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//            mBluetoothAdapter.startDiscovery();
//        }
    }
}
