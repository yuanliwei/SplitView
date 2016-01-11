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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;


public class SplitView3 extends LinearLayout {
    private static final String TAG = "SplitView2";
    private ViewDragHelper mDragger;

    public SplitView3(Context context) {
        this(context, null);
    }

    public SplitView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    View vHead;                   //视频
    WebView vTop;                 //题干
    View vCenter;                 //拖动条
    View vBottom;                 //选项

    View dragView;                //当前拖动的View

    int vHeadHeight = 320;        //视频控件高度
    int vState = 0;               //视频控件状态 0:折叠 1:展开

    float vtH;                    //题干View高度
    float vbH;                    //选项View高度

    public SplitView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                dragView = child;
                if (child == vCenter) {
                    return true;
                }
                if (child == vTop) {
                    if (vTop.getScrollY() == 0)
                        return true;
                }
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return 0;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (dragView == vCenter) {
                    changeLayout();
                    if (child == vCenter) {
                        if (top < vHead.getBottom())
                            return vHead.getBottom();
                    }
                    return top;
                }

                if (dragView == vTop) {
                    changeLayout_vTop();
                    if (top < 0)
                        return 0;
                }
                return top;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.d(TAG, "onViewDragStateChanged - " + state);
                if (state == ViewDragHelper.STATE_DRAGGING) {
                }
                if (state == ViewDragHelper.STATE_IDLE) {
                    if (dragView == vCenter) {
                        changeLayout();
                        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                        ltp.height = vCenter.getTop() - vHead.getBottom();
                        lbp.height = getHeight() - vCenter.getBottom();

//                    vTop.requestLayout();
                        vBottom.requestLayout();
                    } else if (dragView == vTop) {
                        changeLayout_vTop();

                        ViewGroup.LayoutParams lhp = vHead.getLayoutParams();
                        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                        if (vState == 0) {
                            lhp.height = 0;                                // head
                            ltp.height = vCenter.getTop() - 0;             // top
                        } else {
                            lhp.height = vHeadHeight;                      // head
                            ltp.height = vCenter.getTop() - vHeadHeight;   // top
                        }
                        lbp.height = getHeight() - vCenter.getBottom();    // bottom

//                        vHead.requestLayout();
//                        vTop.requestLayout();
                        vBottom.requestLayout();
                    }
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
                    if (yPosition < vHead.getBottom()) {
                        yPosition = vHead.getBottom();
                    }

                    mDragger.settleCapturedViewAt(0, (int) yPosition);
                    invalidate();
                }
                if (releasedChild == vTop) {
                    int videoHeight = vHeadHeight;
                    int yPosition = 0;
                    vState = 0;
                    if (vTop.getTop() > videoHeight / 2) {// count dp?
                        yPosition = videoHeight;
                        vState = 1;
                    }
                    if (Math.abs(yvel) > 200) {
                        yPosition = yvel > 0 ? videoHeight : 0;
                        vState = yvel > 0 ? 1 : 0;
                    }
                    mDragger.settleCapturedViewAt(0, yPosition);
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
        vTop.layout(0, vHead.getBottom(), getWidth(), vCenter.getTop());
        vBottom.layout(0, vCenter.getBottom(), getWidth(), getHeight());
    }

    private void changeLayout_vTop() {
        int w = getWidth();
        int h = getHeight();

        if (vTop.getTop() > 0) {
            vHead.layout(0, 0, w, vTop.getTop());
        }
//        vTop.layout(0, vHead.getBottom(), getWidth(), vCenter.getTop());
//        vCenter.layout(0, vTop.getBottom(), w, vTop.getBottom() + vCenter.getHeight());
//        vBottom.layout(0, vCenter.getBottom(), w, h);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
            if (dragView == vTop) {
                changeLayout_vTop();
            } else if (dragView == vCenter) {
                changeLayout();
            }
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        vHead = getChildAt(0);
        vTop = (WebView) getChildAt(1); //这个必须是WebView
        vCenter = getChildAt(2);
        vBottom = getChildAt(3);
        vtH = vTop.getMeasuredHeight();
        vbH = vBottom.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getHistorySize() > 0 && dragView == vTop) {
            if (event.getY() > event.getHistoricalY(0)) {         //向下滑动

                if (vTop.getScrollY() == 0) {
                    return mDragger.shouldInterceptTouchEvent(event);
                } else {
                    return false;
                }
            } else if (event.getY() < event.getHistoricalY(0)) {  //向上滑动
                if (vTop.getTop() > 0) {
                    return mDragger.shouldInterceptTouchEvent(event);
                } else {
                    return false;
                }
            }
        }
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

}
