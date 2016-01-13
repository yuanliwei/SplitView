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

/**
 * Created by y on 2016/1/12.
 */
public class SplitPagerAdapter extends PagerAdapter {

    private SparseArray<Object> map = new SparseArray<Object>();
    private LayoutInflater inflater;

    public SplitPagerAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SplitView3 splitView = (SplitView3) inflater.inflate(R.layout.split_pager_item, null);
        ItemHolder holder = new ItemHolder();
        holder.head = splitView.findViewById(R.id.spi_head);
        holder.top = (WebView) splitView.findViewById(R.id.spi_top);
        holder.center = splitView.findViewById(R.id.spi_drag);
        holder.pager = (ViewPager) splitView.findViewById(R.id.spi_pager);

        initHolderView(holder, position);
        container.addView(splitView);
        map.append(position, splitView);
        return splitView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) map.get(position));
        map.delete(position);
    }

    private void initHolderView(ItemHolder holder, int position) {
        // init head
        // init webview
        holder.top.setWebViewClient(wbClient);
        holder.top.loadUrl("http://www.guokr.com/post/71625" + position);
        // init pager
    }

    class ItemHolder {
        public View head;                      //视频
        public WebView top;                    //题干WebView
        public View center;                    //拖动条
        public ViewPager pager;                //ViewPager选项
    }

    WebViewClient wbClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }
    };
}
