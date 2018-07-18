package com.example.kalyan.tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebNews extends AppCompatActivity {

    private WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_news);

        browser = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        if(intent != null) {
            String newsUrl = intent.getStringExtra("url");
            browser.getSettings().getJavaScriptEnabled();
            browser.loadUrl(newsUrl);
        }
    }
}
