package com.linshicong.bsproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.display.DisplayCardActivity;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.db.UserCache;
import com.linshicong.bsproject.util.BmobUtil;

import java.net.URL;
import java.util.List;

import static com.linshicong.bsproject.R.id.one_text;
import static com.linshicong.bsproject.R.id.photo_img;
import static com.linshicong.bsproject.R.id.three_text;
import static com.linshicong.bsproject.R.id.two_text;
import static com.linshicong.bsproject.R.id.user_img;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date 2016/9/22 23:36
 */
public class TimelineRecyclerViewAdapter extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.NewsViewHolder> {

    private List<Post> postes;
    private Activity context;
    private List<UserCache> userCaches;
    private boolean flag;
    private  Animation animation;

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public List<UserCache> getUserCaches() {
        return userCaches;
    }

    public void setUserCaches(List<UserCache> userCaches) {
        this.userCaches = userCaches;
    }

    public TimelineRecyclerViewAdapter(List<Post> postes, Activity context) {
        this.postes = postes;
        this.context = context;
    }

    public TimelineRecyclerViewAdapter(Activity context, boolean flag) {
        this.context = context;
        this.flag = flag;
    }

    public List<Post> getPostes() {
        return postes;
    }

    public void setPostes(List<Post> postes) {
        this.postes = postes;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        SimpleDraweeView photoImg;
        TextView oneText;
        TextView twoText;
        TextView threeText;
        TextView timeText;
        ImageView userImg;
        TextView nameText;
        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            photoImg = (SimpleDraweeView) itemView.findViewById(photo_img);
            userImg = (ImageView) itemView.findViewById(user_img);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            oneText = (TextView) itemView.findViewById(one_text);
            twoText = (TextView) itemView.findViewById(two_text);
            threeText = (TextView) itemView.findViewById(three_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }

    @Override
    public TimelineRecyclerViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (BmobUtil.GetData(context)) {
            View v = LayoutInflater.from(context).inflate(R.layout.timeline_item_one, viewGroup, false);
            NewsViewHolder nvh = new NewsViewHolder(v);
            return nvh;
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.timeline_item_two, viewGroup, false);
            NewsViewHolder nvh = new NewsViewHolder(v);
            return nvh;
        }
    }

    @Override
    public void onBindViewHolder(final TimelineRecyclerViewAdapter.NewsViewHolder personViewHolder, final int i) {
        if (flag) {
            if (postes.get(i).getImage().getFileUrl().isEmpty()) {
                personViewHolder.photoImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.nameText.setText(postes.get(i).getUser().getName());
            } else {
                try {
                    URL url = new URL(postes.get(i).getImage().getFileUrl());
                    Uri uri = Uri.parse(url.toURI().toString());
                    personViewHolder.photoImg.setImageURI(uri);
                    personViewHolder.nameText.setText(postes.get(i).getUser().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (BmobUtil.GetData(context)) {
                String userurl = postes.get(i).getUser().getImage().getFileUrl();
                if (userurl.isEmpty()) {
                    personViewHolder.userImg.setImageResource(R.mipmap.ic_launcher);
                    personViewHolder.oneText.setText(postes.get(i).getContentOne());
                    personViewHolder.twoText.setText(postes.get(i).getContentTwo());
                    personViewHolder.threeText.setText(postes.get(i).getContentThree());
                    String tString = postes.get(i).getCreatedAt();
                    String ss = BmobUtil.getDistanceTime(tString);
                    personViewHolder.timeText.setText(ss);
                } else {
                    try {
                        URL url = new URL(userurl);
                        Uri uri = Uri.parse(url.toURI().toString());
                        personViewHolder.userImg.setImageURI(uri);
                        personViewHolder.oneText.setText(postes.get(i).getContentOne());
                        personViewHolder.twoText.setText(postes.get(i).getContentTwo());
                        personViewHolder.threeText.setText(postes.get(i).getContentThree());
                        String tString = postes.get(i).getCreatedAt();
                        String ss = BmobUtil.getDistanceTime(tString);
                        personViewHolder.timeText.setText(ss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (userCaches.get(i).getCardUrl().isEmpty()) {
                personViewHolder.photoImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.nameText.setText(BmobUtil.GetCurrentUser().getName());
            } else {
                try {
                    URL url = new URL(userCaches.get(i).getCardUrl());
                    Uri uri = Uri.parse(url.toURI().toString());
                    personViewHolder.photoImg.setImageURI(uri);
                    personViewHolder.nameText.setText(BmobUtil.GetCurrentUser().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (BmobUtil.GetData(context)) {
                String userurl = BmobUtil.GetCurrentUser().getImage().getFileUrl();
                if (userurl.isEmpty()) {
                    personViewHolder.userImg.setImageResource(R.mipmap.ic_launcher);
                    personViewHolder.oneText.setText(userCaches.get(i).getContentOne());
                    personViewHolder.twoText.setText(userCaches.get(i).getContentTwo());
                    personViewHolder.threeText.setText(userCaches.get(i).getContentThree());
                    String tString = userCaches.get(i).getUpdateTime();
                    String ss = BmobUtil.getDistanceTime(tString);
                    personViewHolder.timeText.setText(ss);
                } else {
                    try {
                        URL url = new URL(userurl);
                        Uri uri = Uri.parse(url.toURI().toString());
                        personViewHolder.userImg.setImageURI(uri);
                        personViewHolder.oneText.setText(userCaches.get(i).getContentOne());
                        personViewHolder.twoText.setText(userCaches.get(i).getContentTwo());
                        personViewHolder.threeText.setText(userCaches.get(i).getContentThree());
                        String tString = userCaches.get(i).getUpdateTime();
                        String ss = BmobUtil.getDistanceTime(tString);
                        personViewHolder.timeText.setText(ss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    Intent intent = new Intent(context, DisplayCardActivity.class);
                    intent.putExtra("url", postes.get(i).getCard().getFileUrl());
                    intent.putExtra("objid", postes.get(i).getUser().getObjectId());
                    intent.putExtra("postid", postes.get(i).getObjectId());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent intent = new Intent(context, DisplayCardActivity.class);
                    intent.putExtra("url", userCaches.get(i).getDetailUrl());
                    intent.putExtra("objid", BmobUtil.GetCurrentUser().getObjectId());
                    intent.putExtra("postid", userCaches.get(i).getPostId());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (flag) {
            return postes.size();
        } else {
            return userCaches.size();
        }
    }

}

