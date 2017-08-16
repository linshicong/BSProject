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
import com.linshicong.bsproject.activity.SkillDetailActivity;
import com.linshicong.bsproject.bean.Skill;
import com.linshicong.bsproject.db.SkillCache;

import java.net.URL;
import java.util.List;

import static com.linshicong.bsproject.R.id.skill_img;


/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: 技能界面RecyclerViewAdapter
 * @date 2016/9/22 23:36
 */

public class SkillRecycleAdapter extends RecyclerView.Adapter<SkillRecycleAdapter.NewsViewHolder> {
    private List<Skill> skills;
    private Activity context;
    private boolean flag;
    private List<SkillCache> skillCaches;

    public List<SkillCache> getSkillCaches() {
        return skillCaches;
    }

    public void setSkillCaches(List<SkillCache> skillCaches) {
        this.skillCaches = skillCaches;
    }

    public SkillRecycleAdapter(List<Skill> skills, Activity context) {
        this.skills = skills;
        this.context = context;
    }

    public SkillRecycleAdapter(Activity context, boolean flag) {
        this.context = context;
        this.flag = flag;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView skillImg;
        TextView titleText;
        TextView timeText;
        CardView cardview;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardview=(CardView) itemView.findViewById(R.id.card_view);
            skillImg = (SimpleDraweeView) itemView.findViewById(skill_img);
            titleText = (TextView) itemView.findViewById(R.id.skill_title_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }

    @Override
    public SkillRecycleAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.skill_item, viewGroup, false);
        SkillRecycleAdapter.NewsViewHolder nvh = new SkillRecycleAdapter.NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(final SkillRecycleAdapter.NewsViewHolder personViewHolder, final int i) {
        if (flag) {
            if (skills.get(i).getImg().getFileUrl().isEmpty()) {
                personViewHolder.skillImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.titleText.setText(skills.get(i).getTitle());
                personViewHolder.timeText.setText(skills.get(i).getUpdatedAt());
            } else {
                try {
                    URL url = new URL(skills.get(i).getImg().getFileUrl());
                    Uri uri = Uri.parse(url.toURI().toString());
                    personViewHolder.skillImg.setImageURI(uri);
                    personViewHolder.titleText.setText(skills.get(i).getTitle());
                    personViewHolder.timeText.setText(skills.get(i).getUpdatedAt());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (skillCaches.get(i).getSkillImg().isEmpty()) {
                personViewHolder.skillImg.setImageResource(R.mipmap.ic_launcher);
                personViewHolder.titleText.setText(skillCaches.get(i).getTitle());
                personViewHolder.timeText.setText(skillCaches.get(i).getTime());
            } else {
                try {
                    URL url = new URL(skillCaches.get(i).getSkillImg());
                    Uri uri = Uri.parse(url.toURI().toString());
                    personViewHolder.skillImg.setImageURI(uri);
                    personViewHolder.titleText.setText(skillCaches.get(i).getTitle());
                    personViewHolder.timeText.setText(skillCaches.get(i).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        personViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    Intent intent = new Intent(context, SkillDetailActivity.class);
                    intent.putExtra("html", skills.get(i).getHtml().getFileUrl());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }else{
                    Intent intent = new Intent(context, SkillDetailActivity.class);
                    intent.putExtra("html", skillCaches.get(i).getSkillUrl());
                    context.startActivity(intent);
                    context.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (flag) {
            return skills.size();
        }else{
            return skillCaches.size();
        }
    }
}

