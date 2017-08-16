package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.SkillDetailActivity;
import com.linshicong.bsproject.adapter.SkillRecycleAdapter;
import com.linshicong.bsproject.bean.Skill;
import com.linshicong.bsproject.db.SkillCache;
import com.linshicong.bsproject.util.HttpUtil;
import com.lusfold.spinnerloading.SpinnerLoading;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 技巧界面
 * @date 2016/9/11 22:42
 */
public class SkillFragment extends Fragment {
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.rl)
    RelativeLayout rl;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private SkillRecycleAdapter adapter;
    private static int num = 0;
    private SpinnerLoading spinnerLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.skill_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        List<SkillCache> skillCaches = DataSupport.findAll(SkillCache.class);
        if (skillCaches.isEmpty()) {
            spinnerLoading = new SpinnerLoading(getActivity());
            spinnerLoading.setBackground(getResources().getDrawable(R.drawable.shadow));
            spinnerLoading.setPaintMode(1);
            spinnerLoading.setItemCount(8);
            spinnerLoading.setCircleRadius(20);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl.addView(spinnerLoading, layoutParams);
            initData();
        } else {
            loadCache(skillCaches);
        }
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }

            @Override
            public void onBottom() {
            }

            @Override
            public void onScrolled(int i, int i1) {
                num = recyclerView.getCurrentPosition();
            }
        });
        super.onResume();
    }

    private void initData() {
        Log.i("skill", "network");
        final BmobQuery<Skill> query = new BmobQuery<Skill>();
        query.include("skill");
        query.order("-updatedAt");
        query.setLimit(50);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        DataSupport.deleteAll(SkillCache.class);

        query.findObjects(new FindListener<Skill>() {
            @Override
            public void done(final List<Skill> list, BmobException e) {
                if (e == null) {
                    //保存到缓存中
                    for (int i = 0; i < list.size(); i++) {
                        SkillCache skill = new SkillCache();
                        skill.setTitle(list.get(i).getTitle());
                        skill.setSkillImg(list.get(i).getImg().getFileUrl());
                        skill.setTitle(list.get(i).getTitle());
                        skill.setTime(list.get(i).getUpdatedAt());
                        skill.setSkillUrl(list.get(i).getHtml().getFileUrl());
                        skill.save();
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    adapter = new SkillRecycleAdapter(getActivity(), true);
                    adapter.setSkills(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                    mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int i) {
                            Intent intent = new Intent(getActivity(), SkillDetailActivity.class);
                            intent.putExtra("html", list.get(i).getHtml().getFileUrl());
                            startActivity(intent);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }

                        @Override
                        public void onItemLongClick(View view, int i) {

                        }
                    });
                    if (spinnerLoading != null) {
                        spinnerLoading.setVisibility(View.GONE);
                        rl.removeView(spinnerLoading);
                    }
                }
            }
        });
    }

    /**
     * 从cache中读取
     *
     * @param list
     */
    private void loadCache(final List<SkillCache> list) {
        Log.i("skill", "cache");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new SkillRecycleAdapter(getActivity(), false);
        adapter.setSkillCaches(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(num);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(getActivity(), SkillDetailActivity.class);
                intent.putExtra("html", list.get(i).getSkillUrl());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int i) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}