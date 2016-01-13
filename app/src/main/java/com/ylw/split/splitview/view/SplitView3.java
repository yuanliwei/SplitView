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
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SplitView3 extends LinearLayout {
    private static final String TAG = "SplitView3";
    private ViewDragHelper mDragger;
    private boolean hasVideo = true;      //是否有视频
    private boolean hasChoice = true;     //是否有选项
    private boolean showVideo = true;     //是否显示视频
    private float t_b = 3 / 2f;             //上下两部分高度比例

    private boolean hasInit = false;

    public SplitView3(Context context) {
        this(context, null);
    }

    public SplitView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    View vHead;                   //视频
    WebView vTop;                 //题干
    View vCenter;                 //拖动条
    ViewPager vBottom;            //选项

    View dragView;                //当前拖动的View

    int vCenterHeight = 10;       //拖动控件的高度
    int vHeadHeight = 320;        //视频控件高度
    int vState = 1;               //视频控件状态 0:折叠 1:展开
    float videoPercent;           //视频控件高度百分比
    float w_h = 16 / 9f;          //视频画面宽高比

    float vtH;                    //题干View高度
    float vbH;                    //选项View高度

    public SplitView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                dragView = child;
                if (hasChoice && child == vCenter) {
                    return true;
                }
                if (hasVideo && child == vTop) {
//                    if (vTop.getScrollY() == 0)
                    if (vState == 1 || vTop.getScrollY() == 0)
                        return true;
                }
                if (hasChoice && child == vBottom) {
//获取到Viewpager中当前显示的WebView，WebView的id必须是在ViewPager中的position
                    int ci = vBottom.getCurrentItem();
                    WebView webView = (WebView) vBottom.findViewById(ci);
                    if (webView != null && webView.getScrollY() == 0) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return 0;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (dragView == vCenter) {              //拖动中间的拖动条
                    changeLayout();
                    if (top < vHead.getBottom())
                        return vHead.getBottom();
                }

                if (dragView == vTop) {                 //拖动上部的WebView
                    changeLayout_vTop();
                    if (top < 0)
                        return 0;
                }
                if (dragView == vBottom) {              //拖动底部的ViewPager
                    changeLayout_vBottom();
                    top = (top > getHeight()) ? getHeight() : top;
                }
                return top;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.d(TAG, "onViewDragStateChanged - " + state);
                if (state == ViewDragHelper.STATE_IDLE) {
                    if (dragView == vCenter) {
                        changeLayout();
                        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                        ltp.height = vCenter.getTop() - vHead.getBottom();
                        lbp.height = getHeight() - vCenter.getBottom();

                        requestLayout();
                    } else if (dragView == vTop) {
                        changeLayout_vTop();

                        ViewGroup.LayoutParams lhp = vHead.getLayoutParams();
                        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                        if (vState == 0) {
                            lhp.height = 0;                                // head
                            ltp.height = vCenter.getTop();                 // top
                        } else {
                            lhp.height = vHeadHeight;                      // head
                            ltp.height = vCenter.getTop() - vHeadHeight;   // top
                        }
                        lbp.height = getHeight() - vCenter.getBottom();    // bottom

                        requestLayout();
                    } else if (dragView == vBottom) {
                        changeLayout_vBottom();
                        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
                        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
                        ltp.height = vCenter.getTop() - vHead.getBottom();
                        lbp.height = getHeight() - vCenter.getBottom();

                        requestLayout();
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
                } else if (releasedChild == vTop) {
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
                } else if (releasedChild == vBottom) {
                    float yPosition = yvel / 10 + vBottom.getTop();
                    float h = getHeight();
                    float vCenterHeight = vCenter.getHeight();

                    yPosition = (yPosition < vCenterHeight) ? vCenterHeight : yPosition;
                    yPosition = (yPosition > h) ? h : yPosition;

                    mDragger.settleCapturedViewAt(0, (int) yPosition);
                    invalidate();
                }
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                if (child == vTop) {
                    return getMeasuredHeight();
                }
                if (child == vCenter) {
                    return getMeasuredHeight() - child.getMeasuredHeight();
                }
                if (child == vBottom) {
                    return getMeasuredHeight() - child.getMeasuredHeight();
                }
                return getMeasuredHeight() - child.getMeasuredHeight();
            }
        });
    }

    private void changeLayout() {
        int w = getWidth();
        int h = getHeight();
        vTop.layout(0, vHead.getBottom(), w, vCenter.getTop());
        vBottom.layout(0, vCenter.getBottom(), w, h);
        int cc = vBottom.getChildCount();

        ViewPager vp = vBottom;
//        int bw = vp.getWidth();
        int bh = vp.getHeight();

        for (int i = 0; i < cc; i++) {
            View v = vp.getChildAt(i);
            v.layout(v.getLeft(), 0, v.getRight(), bh);
        }
    }

    private void changeLayout_vTop() {
        int w = getWidth();
//        int h = getHeight();

        if (vTop.getTop() > 0) {
            videoPercent = vTop.getTop() * 1f / vHeadHeight;

            float nh = videoPercent * vHeadHeight;
            float nw = nh * w_h;
            int padding = (int) ((w - nw) / 2);

            vHead.layout(padding, 0, w - padding, vTop.getTop());
            vTop.layout(0, vTop.getTop(), w, vCenter.getTop());
        } else {
            videoPercent = 0;
        }
        ((TextView) vHead).setText(String.format("视频：%.02f", videoPercent));
        if (videoPercent > 1) {
            vHead.getBackground().setAlpha(0xff);
        } else {
            vHead.getBackground().setAlpha((int) (0xff * videoPercent));
        }


//        vTop.layout(0, vHead.getBottom(), getWidth(), vCenter.getTop());
//        vCenter.layout(0, vTop.getBottom(), w, vTop.getBottom() + vCenter.getHeight());
//        vBottom.layout(0, vCenter.getBottom(), w, h);
    }

    private void changeLayout_vBottom() {
        int w = getWidth();
        int h = getHeight();

        int bt = vBottom.getTop();
        vBottom.layout(0, bt, w, h);
        vTop.layout(0, vHead.getBottom(), w, bt - vCenter.getHeight());
        vCenter.layout(0, bt - vCenter.getHeight(), w, bt);
        int cc = vBottom.getChildCount();

        ViewPager vp = vBottom;
//        int bw = vp.getWidth();
        int bh = vp.getHeight();

        for (int i = 0; i < cc; i++) {
            View v = vp.getChildAt(i);
            v.layout(v.getLeft(), 0, v.getRight(), bh);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
            if (dragView == vTop) {
                changeLayout_vTop();
            } else if (dragView == vCenter) {
                changeLayout();
            } else if (dragView == vBottom) {
                changeLayout_vBottom();
            }
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        vHead = getChildAt(0);
        vTop = (WebView) getChildAt(1);        //这个必须是WebView
        vCenter = getChildAt(2);
        vBottom = (ViewPager) getChildAt(3);   //这个必须是ViewPager

        if (isInEditMode()) return;
        vtH = vTop.getMeasuredHeight();
        vbH = vBottom.getMeasuredHeight();
        vCenterHeight = vCenter.getMeasuredHeight();
        vHeadHeight = (int) (vTop.getMeasuredWidth() / w_h);

        hasInit = true;
    }

    private boolean firstLayout = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (firstLayout && !isInEditMode()) {
            firstLayout = false;
            //初始化内部控件
            initViewState(hasVideo, showVideo, hasChoice, t_b);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initDragView(vTop, event);
            initDragView(vCenter, event);
            initDragView(vBottom, event);
        }
        if (event.getHistorySize() > 0) {                             //拦截Touch事件
            if (dragView == vTop) {                                   //vTop
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
                } else if (event.getY() == event.getHistoricalY(0)) {
                    Log.d(TAG, "onInterceptTouchEvent - " +
                            "event.getY() == event.getHistoricalY(0)");
                    return false;
                }
            } else if (dragView == vBottom) {                         //vBottom
                if (event.getY() > event.getHistoricalY(0)) {         //向下滑动
                    // 判断当前显示的webView内容是否在顶部
                    int ci = vBottom.getCurrentItem();
                    WebView webView = (WebView) vBottom.findViewById(ci);
                    if (webView.getScrollY() == 0) {
                        return mDragger.shouldInterceptTouchEvent(event);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (dragView == vCenter) {
                return mDragger.shouldInterceptTouchEvent(event);
            }
        }
        return mDragger.shouldInterceptTouchEvent(event);
    }

    private Rect r = new Rect();

    /**
     * 初始化当前拖动的view
     *
     * @param v
     * @param e
     */
    private void initDragView(View v, MotionEvent e) {
        if (v != null) {
            if (v.getGlobalVisibleRect(r)) {
                if (r.contains((int) e.getX(), (int) e.getY())) {
                    dragView = v;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

    /**
     * 初始化内部控件状态：
     *
     * @param hasVideo  是否有视频
     * @param showVideo 视频是否显示
     * @param hasChoice 是否有选项卡
     * @param t_b_      题干与选项高度的比值
     */
    public void initViewState(boolean hasVideo, boolean showVideo, boolean hasChoice, float t_b_) {
        this.hasVideo = hasVideo;
        this.showVideo = showVideo;
        this.hasChoice = hasChoice;
        this.t_b = t_b_;
        if (hasInit == false) return;
        ViewGroup.LayoutParams lhp = vHead.getLayoutParams();
        ViewGroup.LayoutParams ltp = vTop.getLayoutParams();
        ViewGroup.LayoutParams lcp = vCenter.getLayoutParams();
        ViewGroup.LayoutParams lbp = vBottom.getLayoutParams();
        int w = getWidth();
        int h = getHeight();

        if (hasVideo && showVideo) {
            vState = 1;
            videoPercent = 1;
            lhp.height = vHeadHeight;
            int h_r = h - vHeadHeight;                        // 剩余空间
            if (hasChoice) {
                h_r -= vCenterHeight;
                t_b_ = 1 / (t_b_ + 1);
                ltp.height = (int) (h_r * (1 - t_b_));
                lbp.height = h_r - ltp.height;
            } else {
                ltp.height = h_r;
                lcp.height = 0;
                lbp.height = 0;
            }
        } else {
            vState = 0;
            videoPercent = 0;
            lhp.height = 0;
            int h_r = h;                                     // 剩余空间
            if (hasChoice) {
                h_r -= vCenterHeight;

                //   2 / 3
                //   1 + 2 /3 = 5 / 3
                //   1 / (5 / 3) = 3 / 5
                //   1 -  3 / 5  = 2 / 5

                t_b_ = 1 / (t_b_ + 1);
                ltp.height = (int) (h_r * (1 - t_b_));
                lbp.height = h_r - ltp.height;
            } else {
                ltp.height = h_r;
                lcp.height = 0;
                lbp.height = 0;
            }
        }
        vHead.getBackground().setAlpha((int) (0xff * videoPercent));
        vHead.requestLayout();
        vTop.requestLayout();
        vBottom.requestLayout();
        requestLayout();
    }

}

