package com.linshicong.bsproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.temple.TempleFive;
import com.linshicong.bsproject.activity.temple.TempleFour;
import com.linshicong.bsproject.activity.temple.TempleOne;
import com.linshicong.bsproject.activity.temple.TempleSix;
import com.linshicong.bsproject.activity.temple.TempleThree;
import com.linshicong.bsproject.activity.temple.TempleTwo;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: 选择模板ViewPagerAdapter
 * @date 2016/9/24 19:43
 */
public class MPagerAdapter extends PagerAdapter {
    private Context context;
    private int[] images = {R.mipmap.temple_one, R.mipmap.temple_two,
            R.mipmap.temple_three, R.mipmap.temple_four, R.mipmap.temple_five, R.mipmap.temple_six};

    public MPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(images[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position) {
                    case 0:
                        context.startActivity(new Intent(context, TempleOne.class));
                        break;
                    case 1:
                        context.startActivity(new Intent(context, TempleTwo.class));
                        break;
                    case 2:
                        context.startActivity(new Intent(context, TempleThree.class));
                        break;
                    case 3:
                        context.startActivity(new Intent(context, TempleFour.class));
                        break;
                    case 4:
                        context.startActivity(new Intent(context, TempleFive.class));
                        break;
                    case 5:
                        context.startActivity(new Intent(context, TempleSix.class));
                        break;
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
