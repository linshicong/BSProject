package com.linshicong.bsproject.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;

import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.view
 * @Description: 其他用户头布局
 * @date 2016/9/25 12:40
 */
public class OthersHeaderView extends LinearLayout {
    private Activity context;
    private SimpleDraweeView userImg;
    private ImageView editImg;
    private ImageView showImg;
    private TextView cardNumText;
    private TextView nameText;


    public OthersHeaderView(final Activity context, final String objid) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.header_view, this);
        userImg = (SimpleDraweeView) findViewById(R.id.user_img);
        nameText = (TextView) findViewById(R.id.name_text);
        cardNumText = (TextView) findViewById(R.id.card_num_text);
        showImg = (ImageView) findViewById(R.id.show_img);
        editImg = (ImageView) findViewById(R.id.edit_img);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<User> query = new BmobQuery<User>();
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
                query.addWhereEqualTo("objectId", objid);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        final User user = list.get(0);
                        if (BmobUtil.checkFeed(context, objid)) {
                            editImg.setImageResource(R.mipmap.feed_check);
                            Log.i("FEED","check");
                        } else {
                            editImg.setImageResource(R.mipmap.feed_uncheck);
                            Log.i("FEED","uncheck");
                        }

                        if (BmobUtil.GetData(context)) {
                            //showImg.setImageResource(R.drawable.ic_scan1);
                            showImg.setSelected(true);
                        } else {
                            //showImg.setImageResource(R.drawable.ic_scan2);
                            showImg.setSelected(false);
                        }
                        if (!user.isPricard()) {
                            cardNumText.setText("0張卡片");
                        } else {
                            Integer a = user.getCardnum();
                            cardNumText.setText(a.toString() + "張卡片");
                        }
                        nameText.setText(user.getName());
                        try {
                            if (user.getImage().getFileUrl().isEmpty()) {
                                userImg.setImageResource(R.mipmap.icon);
                            } else {
                                URL url = new URL(user.getImage().getFileUrl());
                                Uri uri = Uri.parse(url.toURI().toString());
                                userImg.setImageURI(uri);
                            }
                        } catch (Exception e1) {
                            userImg.setImageResource(R.mipmap.icon);
                            e1.printStackTrace();
                        }
                        editImg.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (BmobUtil.checkFeed(context, objid)) {
                                    BmobUtil.removeToFeed(context, user);
                                    editImg.setImageResource(R.mipmap.feed_uncheck);
                                } else {
                                    BmobUtil.addToFeed(context, user);
                                    editImg.setImageResource(R.mipmap.feed_check);
                                }
                            }
                        });
                        showImg.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                                Intent intent = new Intent();
                                intent.setAction("com.linshicong.MY");
                                localBroadcastManager.sendBroadcast(intent);
                                if (BmobUtil.GetData(context)) {
                                    //showImg.setImageResource(R.drawable.ic_scan2);
                                    showImg.setSelected(false);
                                    BmobUtil.SaveData(context, false);
                                } else {
                                    //showImg.setImageResource(R.drawable.ic_scan1);
                                    showImg.setSelected(true);
                                    BmobUtil.SaveData(context, true);
                                }
                            }
                        });
                    }
                });
            }
        });

    }

}
