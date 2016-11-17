package com.feed.curation.ntsk.curationapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class ItemDetailActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // WebView
        webView = (WebView)findViewById(R.id.webView);

        // インテントを取得
        Intent intent = getIntent();
        String mtitle = intent.getStringExtra("title");
        String mUrl = intent.getStringExtra("url");
        getSupportActionBar().setTitle(mtitle);

        // URLを読み込み
        webView.loadUrl(mUrl);



    }
}
