package com.linshicong.bsproject.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.URL;
import java.util.List;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date 2017/3/4 19:40
 */
public class PhotoPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> list;

    public PhotoPagerAdapter(Context context,List<String> list) {
        this.context = context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
        simpleDraweeView.setScrollBarFadeDuration(3000);
        simpleDraweeView.setScaleType(ImageView.ScaleType.FIT_XY);
        try{
            URL url = new URL(list.get(position));
            Uri uri = Uri.parse(url.toURI().toString());
            simpleDraweeView.setImageURI(uri);
        }catch(Exception e){
            e.printStackTrace();
        }

        container.addView(simpleDraweeView);
        return simpleDraweeView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((SimpleDraweeView) object);
    }
}

