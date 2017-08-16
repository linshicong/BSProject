package com.linshicong.bsproject.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.InfoActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;

import java.net.URL;

import cn.bmob.v3.BmobUser;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.view
 * @Description: recycleView头布局
 * @date 2016/9/23 12:06
 */
public class HeaderView extends LinearLayout {
    private Context context;
    private SimpleDraweeView userImg;
    private ImageView showImg;
    private ImageView editImg;
    private TextView cardNumText;
    private TextView nameText;

    public HeaderView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.header_view, this);
        userImg = (SimpleDraweeView) findViewById(R.id.user_img);
        nameText = (TextView) findViewById(R.id.name_text);
        cardNumText = (TextView) findViewById(R.id.card_num_text);
        User user = BmobUser.getCurrentUser(User.class);
        editImg = (ImageView) findViewById(R.id.edit_img);
        showImg = (ImageView) findViewById(R.id.show_img);
        editImg.setImageResource(R.mipmap.ic_edit);
        nameText.setText(user.getName());
        Integer a = user.getCardnum();
        cardNumText.setText(a.toString()+"張卡片");
        if (BmobUtil.GetData(context)) {
            //showImg.setImageResource(R.drawable.ic_scan1);
            showImg.setSelected(true);
        } else {
            //showImg.setImageResource(R.drawable.ic_scan2);
            showImg.setSelected(false);
        }
        try {
            if (user.getImage().getFileUrl().isEmpty()) {
                userImg.setImageResource(R.mipmap.icon);
            } else {
                URL url = new URL(user.getImage().getFileUrl());
                Uri uri = Uri.parse(url.toURI().toString());
                userImg.setImageURI(uri);
            }
        } catch (Exception e) {
            userImg.setImageResource(R.mipmap.icon);
            e.printStackTrace();
        }
        showImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                Intent intent = new Intent();
                intent.setAction("com.linshicong.MY");
                localBroadcastManager.sendBroadcast(intent);
                if (BmobUtil.GetData(context)) {
                    //showImg.setImageResource(R.drawable.ic_scan2);
                    userImg.setSelected(false);
                    BmobUtil.SaveData(context, false);
                } else {
                    //showImg.setImageResource(R.drawable.ic_scan1);
                    userImg.setSelected(true);
                    BmobUtil.SaveData(context, true);
                }
            }
        });
        editImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
