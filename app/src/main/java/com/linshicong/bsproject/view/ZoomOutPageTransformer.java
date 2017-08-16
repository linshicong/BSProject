package com.linshicong.bsproject.view;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: 模板选择缩放动画
 * @date 2016/9/24 20:49
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.9f;
    private static float defaultScale = 1.2f;

    public void transformPage(View view, float position) {
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }
        float tempScale = position < 0 ? 1 + position : 1 - position;
        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        //一个公式
        float scaleValue = MIN_SCALE + tempScale * slope;
        //设置缩放比例
        view.setScaleX(scaleValue);
        view.setScaleY(scaleValue);
        //设置透明度
        view.setAlpha(scaleValue);
    }

}
