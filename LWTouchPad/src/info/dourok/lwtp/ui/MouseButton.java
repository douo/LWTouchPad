/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.Shape;
import android.view.MotionEvent;

/**
 *
 * @author DouO
 */
public class MouseButton extends Component {

    private boolean expanded;
    private final static int COLLAPSE_WIDTH = 60;
    private final static int COLLAPSE_HEIGHT = 30;
    private Rect[] leftButtonRects;
    private Rect[] rightButtonRects;
    private Rect[] middleButtonRects;
    private Shape leftButtonView;
    private Shape rightButtonView;
    private Shape middleButtonView;
    private int mCollapseLeft;
    private int mCollapseRight;
    private int mCollapseTop;
    private int mCollapseBottom;
    private int mExpandLeft;
    private int mExpandRight;
    private int mExpandTop;
    private int mExpandBottom;
    private Paint mPaint;

    public MouseButton(LwtpTouchPad pad) {
        super(pad, "MouseButton");
        expanded = false;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(0xff00f0f0);
         
    }

    @Override
    protected void onDraw(Canvas c) {
        c.drawColor(0xff00f0f0);
    }

    private void expand(boolean e) {
        if (expanded != e) {
            expanded = e;
            if (expanded) {
                mLeft = mExpandLeft;
                mRight = mExpandRight;
                mTop = mExpandTop;
                mBottom = mExpandBottom;
            } else {
                mLeft = mCollapseLeft;
                mRight = mCollapseRight;
                mTop = mCollapseTop;
                mBottom = mCollapseBottom;
            }
            repaint();
        }
    }

    @Override
    protected void onLayout(int left, int top, int right, int bottom) {
        int parentWidth = right - left;
        mExpandLeft = parentWidth / 6;
        mExpandRight = right - parentWidth / 6;
        mExpandBottom = bottom;
        mExpandTop = bottom - parentWidth / 3;

        mCollapseLeft = (parentWidth - COLLAPSE_WIDTH)/2;
        mCollapseRight = mCollapseLeft + COLLAPSE_WIDTH;
        mCollapseBottom = bottom;
        mCollapseTop = bottom - COLLAPSE_HEIGHT;
        if(expanded){
            mLeft = mExpandLeft;
            mRight = mExpandRight;
            mTop = mExpandTop;
            mBottom = mExpandBottom;
        }else{
            mLeft = mCollapseLeft;
            mRight = mCollapseRight;
            mTop = mCollapseTop;
            mBottom = mCollapseBottom;
        }
        init();
    }

    @Override
    void pointerDown(MotionEvent ev) {
        setPressed(true);
    }

    @Override
    void pointerMove(MotionEvent ev) {
    }

    @Override
    void pointerUp(MotionEvent ev) {
        setPressed(false);
        expand(!expanded);
    }
}
