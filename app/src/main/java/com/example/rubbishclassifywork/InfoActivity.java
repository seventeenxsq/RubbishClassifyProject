package com.example.rubbishclassifywork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends Activity {

    private WebView webView;
    public static final String CONTENT_URL="IntentUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // StatusBarUtil.transparencyBar(this);
//        StatusBarUtil.BlackFontStatusBar(this.getWindow());
        Window window=this.getWindow();
        window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_info);

        webView=findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            //重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //如果不需要其他对点击链接事件的处理返回true，否则返回false
                return true;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);//获得JS权限
        Intent intent = getIntent();//获得意图
        String intent_url= intent.getStringExtra(CONTENT_URL);//获得意图的网址
        webView.loadUrl(intent_url);
    }
}
