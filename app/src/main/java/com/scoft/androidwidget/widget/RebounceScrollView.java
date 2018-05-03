package com.scoft.androidwidget.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by scoft on 18-5-3.
 */

public class RebounceScrollView extends ScrollView {
    private View mContentView;
    private Rect mOriginalRect = new Rect();
    private boolean canPullDown = false;
    private boolean canPullUp = false;
    private float startY;
    static private final float FACTOR = 0.3f;
    static private final long ANIMA_TIME = 300;
    private boolean isMove = false;

    public RebounceScrollView(Context context) {
        super(context);
    }

    public RebounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RebounceScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mOriginalRect.set(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mContentView == null) {
            return super.dispatchTouchEvent(ev);
        }
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                canPullDown = canPullDown();
                canPullUp = canPullUp();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canPullUp && !canPullDown) {
                    startY = ev.getY();
                    canPullUp = canPullUp();
                    canPullDown = canPullDown();
                }
                float detlaY = (ev.getY() - startY);
                boolean shouldMove = (canPullDown() & detlaY > 0)
                        || (canPullUp && detlaY < 0);
                android.util.Log.d("TEST","shouldMove = "+shouldMove);
                if (shouldMove) {
                    int offset = (int) (detlaY * FACTOR);
                    mContentView.layout(mOriginalRect.left,mOriginalRect.top+offset,mOriginalRect.right,mOriginalRect.bottom+offset);
                    isMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!isMove){
                    break;
                }
                TranslateAnimation animation = new TranslateAnimation(0,0,mContentView.getTop(),mOriginalRect.top);
                animation.setDuration(ANIMA_TIME);
                mContentView.startAnimation(animation);
                mContentView.layout(mOriginalRect.left,mOriginalRect.top,mOriginalRect.right,mOriginalRect.bottom);

                canPullDown = false;
                canPullUp = false;
                isMove = false;
                break;
            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canPullDown() {
        return getScrollY() == 0 || mContentView.getHeight() < getHeight() + getScrollY();
    }

    private boolean canPullUp() {
        return mContentView.getHeight() <= getHeight() + getScrollY();
    }
}
