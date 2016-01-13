package com.ylw.split.splitview.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ylw.split.splitview.R;

/**
 * Created by 袁立位 on 2016/1/12.
 */
public class SplitSubPagerAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    private final String[] urls;
    private SparseArray<Object> map = new SparseArray<Object>();

    public SplitSubPagerAdapter(Context context,String[] urls){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.urls=urls;
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        WebView webView = (WebView) inflater.inflate(R.layout.webview, null);
        webView.setId(position);
        webView.setBackgroundColor(0xFFFEFEFE);
        webView.setWebViewClient(wbClient);
        webView.loadUrl(urls[position]);
        container.addView(webView);
        map.append(position, webView);
        return webView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) map.get(position));
        map.delete(position);
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


