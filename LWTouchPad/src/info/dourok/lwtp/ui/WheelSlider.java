/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package info.dourok.lwtp.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import info.dourok.lwtp.LwtpLog;

/**
 *
 * @author drcher
 */
public class WheelSlider extends Component {
    private static final int DEFAULT_WIDTH =30;
    private static final int COR_SIZE=30;
    private WheelMode mWheelMode;
    private Path mMidShape;
//    private Path mCorShape;
    private Paint mNormalPaint;
    private Paint mPressedPaint;
    private int mWidth;
    
    public WheelSlider(LwtpTouchPad touchPad) {
        super(touchPad, "wheelslider");
        mWheelMode = new WheelModeImpl();
        mMidShape = new Path();
        mWidth = DEFAULT_WIDTH;
        Paint p =new Paint();
        p.setColor(0x88ffffff);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        mNormalPaint = p;
        p =new Paint();
        p.setColor(0xffffffff);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        mPressedPaint = p;
    }

    private void generatePath() {
        mMidShape.reset();
        int width = getWidth();
        int height = getHeight();
        
        int sHeight = height /6;
        int pBlank = sHeight /3;
        final int tx = 1; 
        final int ex = width -1;
        int ty = sHeight;
        for (int i = 0; i < 12; i++) {
            mMidShape.moveTo(tx, ty);
            mMidShape.lineTo(ex, ty);
            ty+=pBlank;
        }
    }
    @Override
    protected void onLayout(int left, int top, int right, int bottom) {
            System.out.println("changed");
            mTop = top;
            mLeft = right - mWidth;
            mRight = right;
            mBottom = bottom;
            inflated = true;
            debugRect();
            generatePath();
    }

    @Override
    protected void onDraw(Canvas c) {
       // c.drawColor(0xff00f0f0);
        Paint p ;
        if(isPressed()){
            p = mPressedPaint;
        }else{
            p = mNormalPaint;
        }
        c.drawPath(mMidShape, p);
    }

    @Override
    void pointerDown(MotionEvent ev) {
        setPressed(true);
        mWheelMode.clear(ev.getY());
        LwtpLog.v(getName() + " press");
    }

    @Override
    void pointerMove(MotionEvent ev) {
        mWheelMode.check(ev.getY());
    }

    @Override
    void pointerUp(MotionEvent ev) {
        setPressed(false);
        mWheelMode.clear(ev.getY());
    }

    private interface WheelMode {

        void check(float dv);

        void setUp(float v);

        void clear(float dv);
    }

    final class WheelModeImpl implements WheelMode {

        private float DEFAULT_DISTANCE = 10;
        private float cv;

        public void check(float dv) {
            float dd = dv - cv;
            if (Math.abs(dd) >= DEFAULT_DISTANCE) {
                if (dd > 0) {
                    touchPad.mouseWheelDown();
                } else {
                    touchPad.mouseWheelUp();
                }
                cv = dv;
            }
        }

        public void setUp(float v) {
            //DO NOTHING
        }

        public void clear(float dv) {
            cv = dv;
        }
    }

    private class WheelModeImpl_2 implements WheelMode {

        private final int GENEVA_LEVEL[] = {20, 10, 5, 2};
        private final int GENEVA_AREA[] = {40, 50, 50, 60};
        private float sv, av, pv;
        private int gIndex;

        public void check(float dv) {
            float dd = dv - pv;
            if (Math.abs(dd) >= GENEVA_LEVEL[gIndex]) {
                LwtpLog.i("gi:" + gIndex + "sy:" + sv + " ay:" + av + " py:" + pv + " dv:" + dv);
                if (dd > 0) {
                    touchPad.mouseWheelDown();
                } else {
                    touchPad.mouseWheelUp();
                }
                pv = dv;
                dd = dv - av;
                if (Math.abs(dd) >= GENEVA_AREA[gIndex]) {
                    if (Math.abs(dv - sv) < Math.abs(dv - av)) {
                        gIndex = gIndex > 0 ? gIndex - 1 : gIndex;
                    } else {
                        gIndex = gIndex < GENEVA_LEVEL.length - 1 ? gIndex + 1 : gIndex;
                    }
                    av = dv;
                }
            }
        }

        public void setUp(float v) {
            gIndex = 0;
        }

        public void clear(float dv) {
            av = pv = sv = dv;
            gIndex = 0;
        }
    }
}
