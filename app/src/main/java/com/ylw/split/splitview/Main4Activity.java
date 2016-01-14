package com.ylw.split.splitview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ylw.split.splitview.adapter.SplitPagerAdapter;

public class Main4Activity extends Activity {

    WebViewClient wbClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }
    };
    private PagerAdapter adapter;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.hw_activity_main4);
        initView();
    }

    private void initView() {
        ViewPager pager = (ViewPager) findViewById(R.id.hw_main4_pager);

        adapter = new SplitPagerAdapter(this);
        pager.setAdapter(adapter);
//        pager.setOffscreenPageLimit(5);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }
        });
    }

}
