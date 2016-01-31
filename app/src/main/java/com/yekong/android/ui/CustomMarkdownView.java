package com.yekong.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import us.feras.mdv.MarkdownView;

/**
 * Created by baoxiehao on 16/1/31.
 */
public class CustomMarkdownView extends MarkdownView {

    // Whether scrollable or not, the parent scrolling is disabled if scrollable
    boolean mScrollable;

    // Whether the view has scrolled to the top or not
    boolean mScrollToTop;

    // The Ys of previous and current touch points to calculate the scrolling direction
    float mPrevY;
    float mCurrY;

    public CustomMarkdownView(Context context) {
        super(context);
    }

    public CustomMarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        super.setOnGenericMotionListener(l);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollToTop = oldt > 0 && t == 0;
        if (mScrollToTop) {
            mScrollable = true;
            setScrollable(true);
        }
    }

    public void setScrollable() {
        mScrollable = true;
        mScrollToTop = true;
        setScrollable(true);
    }

    private void setScrollable(final boolean scrollable) {
        getParent().requestDisallowInterceptTouchEvent(scrollable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevY = mCurrY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mPrevY = mCurrY;
                mCurrY = event.getY();
                break;
        }
        boolean scrollingDown = mPrevY < mCurrY;
        // When scrollable and at the top, disable the scrolling when scroll down again
        if (mScrollable && mScrollToTop && scrollingDown) {
            mScrollable = false;
            mScrollToTop = false;
            setScrollable(false);
        }
        // When scrollable, keep the scrolling when scroll up
        else if (mScrollable && !scrollingDown) {
            setScrollable(true);
        }
        // When scroll to the top, keep the scrolling when scroll up
        else if (mScrollToTop && !scrollingDown) {
            setScrollable(true);
        }
        return super.onTouchEvent(event);
    }
}