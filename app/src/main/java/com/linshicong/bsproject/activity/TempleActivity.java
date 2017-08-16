package com.linshicong.bsproject.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.MPagerAdapter;
import com.linshicong.bsproject.util.ActivityCollecter;
import com.linshicong.bsproject.view.ZoomOutPageTransformer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.ui
 * @Description: 选择模板逻辑处理，采用ViewPager实现画廊效果，中间放大
 * @date 2016/9/19 22:20
 */
public class TempleActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.rl)
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temple_activity);
        ButterKnife.bind(this);
        initView();
        viewpager.setAdapter(new MPagerAdapter(this));
        viewpager.setOffscreenPageLimit(6);
        viewpager.setPageMargin(1);
        /**设置子图片的缩放动画*/
        viewpager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (viewpager != null) {
                    viewpager.invalidate();
                }
            }

            @Override
            public void onPageSelected(int position) {
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

    /**
     * 初始化控件
     */
    private void initView() {
        ActivityCollecter.addActivity(this);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("選擇模板");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        viewpager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollecter.removeActivity(this);
    }
}

