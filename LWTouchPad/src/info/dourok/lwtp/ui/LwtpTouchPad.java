/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import info.dourok.lwtp.LwtpLog;
import info.dourok.lwtp.client.LwtpClient;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author drcher
 */
public class LwtpTouchPad extends View {

    private LwtpClient lwtpClient;
    private ArrayList<Component> components;
    private Component pressComponent;
    private TouchMode touchMode;

    public LwtpTouchPad(Context context) {
        super(context);
    }

    public LwtpTouchPad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LwtpTouchPad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        components = new ArrayList<Component>();
        components.add(new WheelSlider(this));
        components.add(new MouseButton(this));
        touchMode = new TouchMode();
        setEnabled(false);
    }

    public void updateClient(LwtpClient client) {
        this.lwtpClient = client;
        if (client != null) {
            setEnabled(true);
        }
    }

    void mouseWheelUp() {
        try {
            lwtpClient.mouseWheelUp();
        } catch (IOException ex) {
            LwtpLog.e("mouseWheelUp Fail", ex);
        }
    }

    void mouseWheelDown() {
        try {
            lwtpClient.mouseWheelDown();
        } catch (IOException ex) {
            LwtpLog.e("mouseWheelDown Fail", ex);
        }
    }
    
    void doType(int kcode){
        lwtpClient.doType(kcode);
    }
    
    void keyPress(int kcode){
        lwtpClient.keyPress(kcode);
    }
    
    void keyRelease(int kcode){
        lwtpClient.keyRelease(kcode);
    }

    private void mouseClick(int btn) {
        try {
            LwtpLog.i("mouseClick");
            lwtpClient.mouseClick(btn);
        } catch (IOException ex) {
            LwtpLog.e("MouseClick Fail", ex);
        }
    }

    void mousePress(int btn) {
        try {
            LwtpLog.i("mousePress");
            lwtpClient.mousePress(btn);
        } catch (IOException ex) {
            LwtpLog.e("MouseClick Fail", ex);
        }
    }

    void mouseRelease(int btn) {
        try {
            LwtpLog.i("mouseRealse");
            lwtpClient.mouseRelease(btn);
        } catch (IOException ex) {
            LwtpLog.e("MouseClick Fail", ex);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LwtpLog.v(changed + " " + left + " " + top + " " + right + " " + bottom);
        int parentWidth = right - left;
        int parentHeight = bottom - top;
        for (Component comp : components) {
            //子组件的位置是相对于父母的
            comp.layout(changed, 0, 0, parentWidth, parentHeight);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (lwtpClient == null) {
            return false;
        }
        float px = ev.getX();
        float py = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LwtpLog.v("DOWN: " + px + " " + py);
                for (Component comp : components) {
                    if (comp.contains((int) px, (int) py)) {
                        pressComponent = comp;
                        break;
                    }
                }
                if (pressComponent != null) {
                    pressComponent.pointerDown(ev);
                } else {
                    touchMode.pointerDown(px, py);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                LwtpLog.v("MOVE: " + px + " " + py);
                if (pressComponent != null) {
                    pressComponent.pointerMove(ev);
                } else {
                    touchMode.pointerMove(px, py);
                }
                break;
            case MotionEvent.ACTION_UP:
                LwtpLog.v("UP: " + px + " " + py);
                if (pressComponent != null) {
                    pressComponent.pointerUp(ev);
                    pressComponent = null;
                } else {
                    touchMode.pointerUp(px, py);
                }
                break;
            default:
                LwtpLog.v(ev.getAction() + " OTHER: " + px + " " + py);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Component comp : components) {
            comp.paintComponent(canvas);
        }
    }

    
    //FIXME temporary solution
    private KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.ALPHA);
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = kcm.getDisplayLabel(keyCode);
        LwtpLog.v("Key Up: "+"[code]:"+keyCode+" [char]:"+c+" [char int:]:"+(int)c);
        //需要一个自订标准,来搞定Android keycode 到J2SE Keycode 的映射问题
        if(lwtpClient!=null&&((c<='9'&&c>='0')||(c<='Z'&&c>='A')||c==' '||c=='\n')){
            doType(c);
            return true;
        }else{
            if(keyCode==67){
                doType(8);
                return true;
           }
        }
        return false;
    }

    
    
    /**
     * 模拟触控板的基本动作<br /> 按下，快速抬起为一次鼠标单击动作<br />
     * 按下，快速抬起，再快速按下为一次鼠标按住(MousePress)动作<br /> 按下，快速抬起，再快速按下，再快速抬起为一次鼠标双击动作<br
     * />
     */
    private class TouchMode implements Runnable {

        private float dx, dy, sx, sy;
        private long pTime;
        private static final int CLICK_START = 1;
        private static final int CLICK_WAIT = 2;
        private static final int CLICK_PRESS = 3;
        private static final int CLICK_OVER = 0;
        private int clickState = CLICK_OVER;
        private static final long TIME_SINGLE_CLICK = 100;
        private static final long TIME_WAIT_PRESS = 120;
        private static final long TIME_DOUBLE_CLICK = 100;
        private float weight = 1.0f;
        private float lx = 0, ly = 0;
        private Thread countThread;

        private void mouseMove(float bx, float by) {
            bx *= weight;
            by *= weight;
            bx += lx;
            int ix = (int) bx;
            lx = bx - ix;
            by += ly;
            int iy = (int) by;
            ly = by - iy;
            try {
                lwtpClient.mouseMove(ix, iy);
            } catch (IOException ex) {
                LwtpLog.e("IO", ex);
            }
        }

        public void pointerDown(float px, float py) {
            long time = System.currentTimeMillis();
            LwtpLog.i("Down state:" + clickState);
            switch (clickState) {
                case CLICK_OVER:
                    sx = dx = px;
                    sy = dy = py;
                    clickState = CLICK_START;
                    pTime = time;
                    break;
                case CLICK_WAIT:
                    System.out.println("TIME_WAIT_PRESS:" + (time - pTime) + " /" + TIME_WAIT_PRESS);
                    if (countThread != null && countThread.isAlive()/*
                             * &&time-pTime<TIME_WAIT_PRESS
                             */) {
                        countThread.interrupt();
                        mousePress(LwtpClient.MOUSE_BUTTON_1);
                        clickState = CLICK_PRESS;
                    } else {
                        clickState = CLICK_START;
                    }
                    pTime = time;
                    break;
                default:
                    LwtpLog.w("Impossible Down Action");
            }
        }

        public void pointerMove(float px, float py) {
            System.out.println(System.currentTimeMillis());
            mouseMove(px - dx, py - dy);
            dx = px;
            dy = py;
        }

        public void pointerUp(float px, float py) {
            LwtpLog.i("Up state:" + clickState);
            long time = System.currentTimeMillis();
            switch (clickState) {
                case CLICK_START:
                    System.out.println("TIME_SINGLE_CLICK:" + (time - pTime) + " /" + TIME_SINGLE_CLICK);
                    if (time - pTime < TIME_SINGLE_CLICK) {
                        // countThread用于等待第二次按下的动作来触发MousePress，
                        countThread = new Thread(this);
                        clickState = CLICK_WAIT;
                        countThread.start();
                        pTime = time;
                    } else {
                        clickState = CLICK_OVER;
                    }
                    break;
                case CLICK_PRESS:
                    System.out.println("TIME_DOUBLE_CLICK:" + (time - pTime) + " /" + TIME_DOUBLE_CLICK);
                    if (time - pTime < TIME_DOUBLE_CLICK) {
                        mouseRelease(LwtpClient.MOUSE_BUTTON_1);
                        mouseClick(LwtpClient.MOUSE_BUTTON_1);
                        clickState = CLICK_OVER;
                    } else {
                        mouseRelease(LwtpClient.MOUSE_BUTTON_1);
                        clickState = CLICK_OVER;
                    }
                    break;
                default:
                    LwtpLog.w("Impossible Up Action");
            }
        }

        public void run() {
            switch (clickState) {
                case CLICK_WAIT:
                    try {
                        Thread.sleep(TIME_WAIT_PRESS);
                        clickState = CLICK_OVER;
                        mouseClick(LwtpClient.MOUSE_BUTTON_1);
                    } catch (InterruptedException ex) {
                        //触发MousePress，取消Click时间
                        LwtpLog.i("Interrupted");
                    }
                    break;
            }
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }
}
