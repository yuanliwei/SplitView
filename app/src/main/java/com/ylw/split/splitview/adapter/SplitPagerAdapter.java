package com.ylw.split.splitview.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ylw.split.splitview.R;
import com.ylw.split.splitview.view.SplitView3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 袁立位 on 2016/1/12.
 */
public class SplitPagerAdapter extends PagerAdapter {

    private final Context context;
    WebViewClient wbClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }
    };
    List<PagerData> dataList = new ArrayList<>();
    private SparseArray<Object> map = new SparseArray<Object>();
    private LayoutInflater inflater;

    public SplitPagerAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initDataList();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SplitView3 splitView = (SplitView3) inflater.inflate(R.layout.hw_split_pager_item, null);
        ItemHolder holder = new ItemHolder();
        holder.head = splitView.findViewById(R.id.spi_head);
        holder.top = (WebView) splitView.findViewById(R.id.spi_top);
        holder.center = splitView.findViewById(R.id.spi_drag);
        holder.pager = (ViewPager) splitView.findViewById(R.id.spi_pager);

        initHolderView(splitView, holder, position);
        container.addView(splitView);
        map.append(position, splitView);
        return splitView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) map.get(position));
        map.delete(position);
    }

    private void initHolderView(SplitView3 splitView, ItemHolder holder, int position) {
        PagerData pagerData = dataList.get(position);
        // init head
        // init hw_webview
        holder.top.setWebViewClient(wbClient);
        holder.top.loadUrl(pagerData.getTopUrl());
        // init pager
        holder.pager.setAdapter(new SplitSubPagerAdapter(context, pagerData.getBottomUrls()));
        holder.pager.setOffscreenPageLimit(5);
        int viewState = pagerData.getViewState();
        splitView.initViewState(
                (viewState & PagerData.STATE_HEAD) > 0,
                (viewState & PagerData.STATE_SHOWHEAD) > 0,
                (viewState & PagerData.STATE_BOTTOM) > 0,
                pagerData.getT_b());

    }

    private void initDataList() {
        int urlCount = UrlData.urls.length;
        for (int i = 0; i < urlCount - 35; i += 30) {
            PagerData pagerData = new PagerData();
            pagerData.setTopUrl(UrlData.urls[i] + 11);
            pagerData.setViewState(0);
            String[] urls = Arrays.copyOfRange(UrlData.urls, i, i + 30);
            pagerData.setBottomUrls(urls);
            dataList.add(pagerData);
        }
        dataList.get(0).setViewState(PagerData.STATE_BOTTOM);
        dataList.get(1).setViewState(PagerData.STATE_BOTTOM | PagerData.STATE_HEAD);
        dataList.get(2).setViewState(PagerData.STATE_BOTTOM | PagerData.STATE_HEAD | PagerData.STATE_SHOWHEAD);
        dataList.get(3).setViewState(PagerData.STATE_BOTTOM | PagerData.STATE_SHOWHEAD);
        dataList.get(4).setViewState(PagerData.STATE_HEAD | PagerData.STATE_SHOWHEAD);
        dataList.get(5).setViewState(PagerData.STATE_SHOWHEAD);
        dataList.get(6).setViewState(PagerData.STATE_BOTTOM);
        dataList.get(7).setViewState(PagerData.STATE_BOTTOM | PagerData.STATE_HEAD);
        dataList.get(8).setViewState(PagerData.STATE_HEAD | PagerData.STATE_SHOWHEAD);


//        dataList.get(0).setT_b(1 / 2f);
//        dataList.get(1).setT_b(2 / 2f);
//        dataList.get(2).setT_b(3 / 2f);
//        dataList.get(3).setT_b(1 / 6f);
//        dataList.get(4).setT_b(2 / 6f);
//        dataList.get(5).setT_b(3 / 6f);
//        dataList.get(6).setT_b(4 / 6f);
//        dataList.get(7).setT_b(5 / 6f);
//        dataList.get(8).setT_b(6 / 6f);
    }

    class ItemHolder {
        public View head;                      //视频
        public WebView top;                    //题干WebView
        public View center;                    //拖动条
        public ViewPager pager;                //ViewPager选项
    }
}
