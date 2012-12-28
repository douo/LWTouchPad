/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import info.dourok.lwtp.LwtpLog;

/**
 * 
 * @author drcher
 */
public abstract class Component {
    protected int mLeft;
    protected int mTop;
    protected int mBottom;
    protected int mRight;
    protected String name;
    protected boolean enable;
    protected boolean visible;
    protected boolean pressed;
    protected LwtpTouchPad touchPad;
    protected boolean inflated ;
    public Component(LwtpTouchPad pad,String name) {
        touchPad = pad;
        this.name = name;
    }
    
    public Component(LwtpTouchPad pad,int x, int y) {
        touchPad = pad;
        this.mLeft = x;
        this.mTop = y;
    }
    
    public Component(LwtpTouchPad pad,int x, int y, int width, int height) {
        touchPad = pad;
        this.mLeft = x;
        this.mTop = y;
        this.mBottom = height;
        this.mRight = width;
    }

    public boolean contains(int px ,int py){
        return (px > mLeft  && py > mTop && px - mLeft <mRight && py - mTop < mBottom);
    }
    
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        if(pressed!=this.pressed){
            this.pressed = pressed;
            repaint();
        }
    }

    public int getBottom() {
        return mBottom;
    }

    public void setBottom(int mBottom) {
        this.mBottom = mBottom;
    }

    public int getLeft() {
        return mLeft;
    }

    public void setLeft(int mLeft) {
        this.mLeft = mLeft;
    }

    public int getRight() {
        return mRight;
    }

    public void setRight(int mRight) {
        this.mRight = mRight;
    }

    public int getTop() {
        return mTop;
    }

    public void setTop(int mTop) {
        this.mTop = mTop;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    
    final public void repaint(){
        touchPad.invalidate(mLeft, mTop, mRight, mBottom);
    }
    
    final protected void paintComponent(Canvas c){
        int i = c.save();
        c.clipRect(mLeft, mTop, mRight, mBottom);
        c.translate(mLeft, mTop);
        onDraw(c);
        c.restoreToCount(i);
    }
    protected abstract void onLayout(int left,int top, int right ,int bottom);
    
    protected final void layout(boolean changed, int left, int top, int right, int bottom){
         if(!inflated ||changed){
             onLayout(left, top, right, bottom);
             inflated=true;
         }
    }
    
    final public int getWidth(){
        return mRight -mLeft;
    }
    
    final public int getHeight(){
        return mBottom -mTop;
    }
    
    abstract void pointerDown(MotionEvent ev);
    
    abstract void pointerMove(MotionEvent ev);
    
    abstract void pointerUp(MotionEvent ev);
    
    protected void onDraw(Canvas c){}
    
    public void debugRect(){
        LwtpLog.i("DEBUG:"+new Rect(mLeft, mTop, mRight, mBottom).toString());
    }
    
}
