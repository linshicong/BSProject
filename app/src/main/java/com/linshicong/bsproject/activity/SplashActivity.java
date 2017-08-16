package com.linshicong.bsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.linshicong.bsproject.R;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: Splash界面
 * @date 2016/9/9 15:08
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        /**
         * 通过自定义View展示启动动画
         * 接着4s后(包括了动画事件3s)进入主页面
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                //isFirst=false;
                finish();
            }
        },4000);
    /*    if (isFirst) {

        }else{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent);
            finish();
        }*/
    }
}