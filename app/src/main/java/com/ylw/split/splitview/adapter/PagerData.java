package com.ylw.split.splitview.adapter;

/**
 * Created by 袁立位 on 2016/1/13 9:50.
 */
public class PagerData {
    public static final int STATE_HEAD = 0x1;      // 是否有视频
    public static final int STATE_SHOWHEAD = 0x2;  // 视频是否显示
    public static final int STATE_BOTTOM = 0x4;    // 是否有选项
    private String video;                          // 视频文件的路径
    private String topUrl;                         // 上部Webview的url
    private String[] bottomUrls;                   // 底部ViewPager的url列表
    /** SplitView的状态，控制内部View的显示方式 */
    private int viewState = STATE_HEAD | STATE_SHOWHEAD | STATE_BOTTOM;
    private float t_b = 3 / 2f;                      // 上下两部分的比例

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTopUrl() {
        return topUrl;
    }

    public void setTopUrl(String topUrl) {
        this.topUrl = topUrl;
    }

    public String[] getBottomUrls() {
        return bottomUrls;
    }

    public void setBottomUrls(String[] bottomUrls) {
        this.bottomUrls = bottomUrls;
    }

    public int getViewState() {
        return viewState;
    }

    public void setViewState(int viewState) {
        this.viewState = viewState;
    }

    public float getT_b() {
        return t_b;
    }

    public void setT_b(float t_b) {
        this.t_b = t_b;
    }
}
