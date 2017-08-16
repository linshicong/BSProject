package com.linshicong.bsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.linshicong.bsproject.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.index.activity
 * @Description: 技巧详情逻辑处理
 * @date 2016/10/31 22:11
 */
public class SkillDetailActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.webview)
    WebView webview;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_detail_activity);
        ButterKnife.bind(this);
        initView();
        Intent intent = getIntent();
        path = intent.getStringExtra("html");
        Log.i("path", path);
        loadHtml(path);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("技巧詳情");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        path = "";
    }

    /**
     * 通过WebView加载下载的html
     */
    private void loadHtml(String path) {
            LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT);
            webview.setLayoutParams(mWebViewLP);
            webview.setInitialScale(25);
            webview.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
            WebSettings settings = webview.getSettings();
            //适应屏幕
            settings.setUseWideViewPort(true);
            settings.setSupportZoom(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setLoadWithOverviewMode(true);
            //允许Webview加载javascript
            settings.setJavaScriptEnabled(true);
            //使html的图片适应手机屏幕
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setDisplayZoomControls(false);
            webview.loadUrl(path);
    }
}
