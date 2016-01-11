/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ylw.split.splitview.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class SplitView2 extends LinearLayout {
    private static final String TAG = "SplitView2";
    private ViewDragHelper mDragger;

    public SplitView2(Context context) {
        this(context, null);
    }

    public SplitView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    View vTop;
    View vCenter;
    View vBottom;
    float vtH;
    float vbH;

    public SplitView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                View c = getgChildAt(1);
                return child == c;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return 0;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                changeLayout();
                return top;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                if (state == ViewDragHelper.STATE_IDLE) {
                    changeLayout();
                    ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                    ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                    ltp.height = vCenter.getTop();
                    lbp.height = getHeight() - vCenter.getBottom();

                    vTop.requestLayout();
                    vBottom.requestLayout();
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == vCenter) {
                    float yPosition = yvel / 10 + vCenter.getTop();
                    float h = getHeight();
                    float vCenterHeight = vCenter.getHeight();

                    if (yPosition + vCenterHeight > h) {
                        yPosition = h - vCenterHeight;
                    }
                    if (yPosition < 0) {
                        yPosition = 0;
                    }

                    mDragger.settleCapturedViewAt(0, (int) yPosition);
                    invalidate();
                }
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }
        });

    }

    private void changeLayout() {
        vTop.layout(0, 0, getWidth(), vCenter.getTop());
        vBottom.layout(0, vCenter.getBottom(), getWidth(), getHeight());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
            changeLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        vTop = getChildAt(0);
        vCenter = getChildAt(1);
        vBottom = getChildAt(2);
        vtH = vTop.getMeasuredHeight();
        vbH = vBottom.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

}
