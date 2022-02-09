package com.iot4all.in.veiwpager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class webpage extends AppCompatActivity {
String url="";
    String str;
Button back_btn,config_btn;
WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        back_btn=findViewById(R.id.back_btn);
        config_btn=findViewById(R.id.config);
        webView=(WebView) findViewById(R.id.webView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String url = extras.getString("url");

        }
        Intent intent = getIntent();

       str = intent.getStringExtra("url");
        str="http://"+str;
        webView.setScrollX(0);
        webView.setScrollY(0);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(str);
        webView.setWebViewClient(new WebViewClient());
        webView.setVerticalScrollBarEnabled(true);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(webpage.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        config_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str= str+"/configuration";
                webView.setScrollX(0);
                webView.setScrollY(0);
                webView.getSettings().setJavaScriptEnabled(true);

                webView.loadUrl(str);
                webView.setWebViewClient(new WebViewClient());
                webView.setVerticalScrollBarEnabled(true);
            }
        });
    }
}