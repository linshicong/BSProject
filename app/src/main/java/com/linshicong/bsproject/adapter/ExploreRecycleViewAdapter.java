package com.linshicong.bsproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.display.DisplayOthersCardActivity;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.db.PostCache;
import com.linshicong.bsproject.util.BmobUtil;

import java.net.URL;
import java.util.List;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date 2016/9/25 1:37
 */
public class ExploreRecycleViewAdapter extends RecyclerView.Adapter<ExploreRecycleViewAdapter.NewsViewHolder> {
    private List<PostCache> postCaches;
    private List<Post> postes;
    private Activity context;
    private boolean flag;

    public ExploreRecycleViewAdapter(List<Post> postes, Activity context) {
        this.postes = postes;
        this.context = context;
    }

    public ExploreRecycleViewAdapter(Activity context,boolean flag) {
        this.context = context;
        this.flag=flag;
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
            cardView=(CardView) itemView.findViewById(R.id.card_view);
            photoImg = (SimpleDraweeView) itemView.findViewById(R.id.photo_img);
            userImg = (ImageView) itemView.findViewById(R.id.user_img);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            oneText = (TextView) itemView.findViewById(R.id.one_text);
            twoText = (TextView) itemView.findViewById(R.id.two_text);
            threeText = (TextView) itemView.findViewById(R.id.three_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }

    @Override
    public ExploreRecycleViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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

    public List<PostCache> getPostCaches() {
        return postCaches;
    }

    public void setPostCaches(List<PostCache> postCaches) {
        this.postCaches = postCaches;
    }

    @Override
    public void onBindViewHolder(final ExploreRecycleViewAdapter.NewsViewHolder personViewHolder, final int i) {
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
            if (postCaches.get(i).getCardUrl().isEmpty()) {
                personViewHolder.photoImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.nameText.setText(postCaches.get(i).getName());
            } else {
                try {
                    URL url = new URL(postCaches.get(i).getCardUrl());
                    Uri uri = Uri.parse(url.toURI().toString());
                    personViewHolder.photoImg.setImageURI(uri);
                    personViewHolder.nameText.setText(postCaches.get(i).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (BmobUtil.GetData(context)) {
                String userurl = postCaches.get(i).getUserImg();
                if (userurl.isEmpty()) {
                    personViewHolder.userImg.setImageResource(R.mipmap.ic_launcher);
                    personViewHolder.oneText.setText(postCaches.get(i).getContentOne());
                    personViewHolder.twoText.setText(postCaches.get(i).getContentTwo());
                    personViewHolder.threeText.setText(postCaches.get(i).getContentThree());
                    String tString = postCaches.get(i).getUpdateTime();
                    String ss = BmobUtil.getDistanceTime(tString);
                    personViewHolder.timeText.setText(ss);
                    personViewHolder.nameText.setText(postCaches.get(i).getName());
                } else {
                    try {
                        URL url = new URL(userurl);
                        Uri uri = Uri.parse(url.toURI().toString());
                        personViewHolder.userImg.setImageURI(uri);
                        personViewHolder.oneText.setText(postCaches.get(i).getContentOne());
                        personViewHolder.twoText.setText(postCaches.get(i).getContentTwo());
                        personViewHolder.threeText.setText(postCaches.get(i).getContentThree());
                        String tString = postCaches.get(i).getUpdateTime();
                        String ss = BmobUtil.getDistanceTime(tString);
                        personViewHolder.timeText.setText(ss);
                        personViewHolder.nameText.setText(postCaches.get(i).getName());
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
                    Intent intent = new Intent(context, DisplayOthersCardActivity.class);
                    intent.putExtra("name", postes.get(i).getUser().getName());
                    intent.putExtra("url", postes.get(i).getCard().getFileUrl());
                    intent.putExtra("objid", postes.get(i).getUser().getObjectId());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent intent = new Intent(context, DisplayOthersCardActivity.class);
                    intent.putExtra("name", postCaches.get(i).getName());
                    intent.putExtra("url", postCaches.get(i).getDetailUrl());
                    intent.putExtra("objid", postCaches.get(i).getObjectId());
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
        }else{
            return postCaches.size();
        }
    }
}

