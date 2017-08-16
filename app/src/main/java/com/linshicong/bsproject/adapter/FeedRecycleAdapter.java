package com.linshicong.bsproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.display.OtherUserActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.FeedCache;

import java.net.URL;
import java.util.List;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: 关注页面RecycleViewAdapter
 * @date 2016/10/23 16:29
 */
public class FeedRecycleAdapter extends RecyclerView.Adapter<FeedRecycleAdapter.NewsViewHolder> {
    private List<User> users;
    private Activity context;
    private List<FeedCache> feedCaches;
    private boolean flag;

    public List<FeedCache> getFeedCaches() {
        return feedCaches;
    }

    public void setFeedCaches(List<FeedCache> feedCaches) {
        this.feedCaches = feedCaches;
    }

    public FeedRecycleAdapter(List<User> users, Activity context) {
        this.users = users;
        this.context = context;
    }

    public FeedRecycleAdapter(Activity context, boolean flag) {
        this.context = context;
        this.flag = flag;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView userImg;
        TextView userNameText, userDescText, cardNumText;
        CardView cardview;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
            userImg = (SimpleDraweeView) itemView.findViewById(R.id.user_img);
            userNameText = (TextView) itemView.findViewById(R.id.username_text);
            userDescText = (TextView) itemView.findViewById(R.id.user_desc_text);
            cardNumText = (TextView) itemView.findViewById(R.id.card_num_text);
        }
    }

    @Override
    public FeedRecycleAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.feed_item, viewGroup, false);
        NewsViewHolder nvh = new NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(final FeedRecycleAdapter.NewsViewHolder personViewHolder, final int i) {
        if (flag) {
            try {
                URL url = new URL(users.get(i).getImage().getFileUrl());
                Uri uri = Uri.parse(url.toURI().toString());
                personViewHolder.userImg.setImageURI(uri);
                personViewHolder.userNameText.setText(users.get(i).getName());
                personViewHolder.userDescText.setText(users.get(i).getDec());
                personViewHolder.cardNumText.setText(users.get(i).getCardnum() + "");
            } catch (Exception e) {
                personViewHolder.userImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.userNameText.setText(users.get(i).getName());
                personViewHolder.userDescText.setText(users.get(i).getDec());
                personViewHolder.cardNumText.setText(users.get(i).getCardnum() + "");
                e.printStackTrace();
            }
        } else {
            try {
                URL url = new URL(feedCaches.get(i).getUserImg());
                Uri uri = Uri.parse(url.toURI().toString());
                personViewHolder.userImg.setImageURI(uri);
                personViewHolder.userNameText.setText(feedCaches.get(i).getName());
                personViewHolder.userDescText.setText(feedCaches.get(i).getDesc());
                personViewHolder.cardNumText.setText(feedCaches.get(i).getCardNum() + "");
            } catch (Exception e) {
                personViewHolder.userImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.userNameText.setText(feedCaches.get(i).getName());
                personViewHolder.userDescText.setText(feedCaches.get(i).getDesc());
                personViewHolder.cardNumText.setText(feedCaches.get(i).getCardNum() + "");
                e.printStackTrace();
            }
        }
        personViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    Intent intent = new Intent(context, OtherUserActivity.class);
                    intent.putExtra("objid", users.get(i).getObjectId());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent intent = new Intent(context, OtherUserActivity.class);
                    intent.putExtra("objid", feedCaches.get(i).getObjectId());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (flag) {
            return users.size();
        } else {
            return feedCaches.size();
        }
    }
}

