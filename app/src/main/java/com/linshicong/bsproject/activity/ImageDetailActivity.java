package com.linshicong.bsproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.PhotoPagerAdapter;
import com.linshicong.bsproject.util.BitmapUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 相册大图查看模式
 * @date 2016/9/11 22:41
 */

public class ImageDetailActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.rl)
    RelativeLayout rl;
    private List<String> list;
    private int num;
    private PhotoPagerAdapter photoPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_activity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("photo");
        num = intent.getIntExtra("num", 0);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle(intent.getStringExtra("name"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        photoPagerAdapter = new PhotoPagerAdapter(this, list);
        viewpager.setAdapter(photoPagerAdapter);
        viewpager.setCurrentItem(num);
        viewpager.setOffscreenPageLimit(list.size());
        viewpager.setPageMargin(1);
        /**设置子图片的缩放动画*/
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (viewpager != null) {
                    viewpager.invalidate();
                }
            }

            @Override
            public void onPageSelected(int position) {
                num = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return viewpager.dispatchTouchEvent(motionEvent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HttpUtil.isConnected(this)) {
            Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "下載成功", Toast.LENGTH_SHORT).show();
            ImageLoader.getInstance().loadImage(list.get(num), new SimpleImageLoadingListener() {
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            BitmapUtil.saveImage(loadedImage, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + ".png");
                        }
                    }
            );
        }
        return super.onOptionsItemSelected(item);
    }
}
