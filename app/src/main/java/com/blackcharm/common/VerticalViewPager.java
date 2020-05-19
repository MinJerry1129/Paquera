package com.blackcharm.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.blackcharm.SearchTransformer.DepthPageTransformer;

public class VerticalViewPager extends ViewPager {
    public VerticalViewPager(Context context) {
        this(context, null);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPageTransformer(true, new DepthPageTransformer());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        swapTouchEvent(event);
        return super.onInterceptTouchEvent(swapTouchEvent(event));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(ev));
    }
}