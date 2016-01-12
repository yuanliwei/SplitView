package com.ylw.split.splitview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main3Activity extends Activity {

    private PagerAdapter adapter;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.activity_main3);
        initView();
    }

    private void initView() {
        WebView wTop = (WebView) findViewById(R.id.web_top);
        ViewPager wBottom = (ViewPager) findViewById(R.id.pager);

        wTop.setWebViewClient(wbClient);
        wTop.loadUrl("http://www.baidu.com/");
        adapter = new PagerAdapter() {
            private SparseArray<Object> map = new SparseArray<Object>();

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
                WebView textView = (WebView) inflater.inflate(R.layout.webview, null);
//                textView.setText("Page : " + position);
//                textView.setTextSize(60);
//                textView.setTextColor(0xff88ff66);
                textView.setId(position);
                textView.setBackgroundColor(0xFFFFCCCC);
                textView.setWebViewClient(wbClient);
                textView.loadUrl("http://www.guokr.com/post/71625" + position);
                container.addView(textView);
                map.append(position, textView);
                return textView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) map.get(position));
                map.delete(position);
            }
        };
        wBottom.setAdapter(adapter);
        wBottom.setOffscreenPageLimit(5);
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
