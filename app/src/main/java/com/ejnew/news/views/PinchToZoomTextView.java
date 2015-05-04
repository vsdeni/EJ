package com.ejnew.news.views;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ejnew.news.helpers.Utils;

/**
 * http://stackoverflow.com/a/20303367/1735100
 */
public class PinchToZoomTextView extends TextView {
    final static float STEP = 100;
    float mRatio;
    int mBaseDist;
    float mBaseRatio;

    public PinchToZoomTextView(Context context) {
        super(context);
    }

    public PinchToZoomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinchToZoomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mRatio = Utils.pixelsToSp(getTextSize(), getContext()) - 13;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        setMovementMethod(LinkMovementMethod.getInstance());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    int action = event.getAction();
                    int pureaction = action & MotionEvent.ACTION_MASK;
                    if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                        mBaseRatio = mRatio;
                        mBaseDist = getDistance(event);
                    } else {
                        float delta = (getDistance(event) - mBaseDist) / STEP;
                        float multi = (float) Math.pow(2, delta);
                        mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                        PinchToZoomTextView.super.setTextSize(mRatio + 13);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }
}
