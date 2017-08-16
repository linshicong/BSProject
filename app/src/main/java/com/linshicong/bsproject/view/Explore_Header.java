package com.linshicong.bsproject.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linshicong.bsproject.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linshicong on 2017/3/16.
 */

public class Explore_Header extends LinearLayout {
    private TextView monthText, dayText, yearText;
    private TextView contentOne, contentTwo;

    public Explore_Header(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.explore_header, this);
        monthText = (TextView) this.findViewById(R.id.month_text);
        dayText = (TextView) this.findViewById(R.id.day_text);
        yearText = (TextView) this.findViewById(R.id.year_text);
        contentOne = (TextView) this.findViewById(R.id.content_one);
        contentTwo = (TextView) this.findViewById(R.id.content_two);
        AssetManager mgr = context.getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/HY.ttf");
        monthText.setTypeface(tf);
        dayText.setTypeface(tf);
        yearText.setTypeface(tf);
        contentOne.setTypeface(tf);
        contentTwo.setTypeface(tf);
        yearText.setText(Calendar.getInstance().get(Calendar.YEAR) + "");
        dayText.setText(Calendar.getInstance().get(Calendar.DATE)+"");
        int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        try {
            Date date = sdf.parse(m + "");
            sdf = new SimpleDateFormat("MMM", Locale.US);
            monthText.setText(sdf.format(date)+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        contentOne.setText("摘下懷念，");
        contentTwo.setText("記住美妙。");
    }
}
