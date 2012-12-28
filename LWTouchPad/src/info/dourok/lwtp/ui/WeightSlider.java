/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.lwtp.ui;

import android.view.MotionEvent;

/**
 * 
 * @author drcher
 */
public class WeightSlider extends Component{

    public WeightSlider(LwtpTouchPad pad, String name) {
        super(pad, name);
    }

//    public WeightSlider(int x , int y,int width,int height) {
//        super(x, y, width, height);
//    }
    
    @Override
    void pointerDown(MotionEvent ev) {
    }

    @Override
    void pointerMove(MotionEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void pointerUp(MotionEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onLayout(int left, int top, int right, int botton) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
