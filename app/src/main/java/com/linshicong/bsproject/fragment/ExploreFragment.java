package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewUtils;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.ExploreRecycleViewAdapter;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.db.PostCache;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.linshicong.bsproject.view.Explore_Header;
import com.lusfold.spinnerloading.SpinnerLoading;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment.index
 * @Description: 广场Fragment，加载最新的用户上传的卡片
 * @date 2016/9/11 22:39
 */
public class ExploreFragment extends Fragment {
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.refresh_img)
    ImageView refreshImg;
    @Bind(R.id.rl)
    RelativeLayout rl;
    private ExploreRecycleViewAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private static int num = 0;
    private LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver;
    private List<PostCache> postCaches;
    private List<Post> post = new ArrayList<>();//用于过滤没有公开的照片
    private SpinnerLoading spinnerLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        postCaches = DataSupport.findAll(PostCache.class);
        if (postCaches.isEmpty()) {
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
            loadCache(postCaches);
        }
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onScrollUp() {
                refreshImg.setVisibility(View.GONE);
            }

            @Override
            public void onScrollDown() {
                refreshImg.setVisibility(View.VISIBLE);
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

    /**
     * 注册本地广播，如果有切换查看方式时刷新布局
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intent = new IntentFilter();
        intent.addAction("com.linshicong.MY");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onResume();
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver, intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        refreshImg.setVisibility(View.VISIBLE);
        ButterKnife.unbind(this);
    }

    private void initData() {
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }

        DataSupport.deleteAll(PostCache.class);
        final BmobQuery<Post> query = new BmobQuery<Post>();
        query.include("user");
        query.order("-updatedAt");
        query.setLimit(50);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(final List<Post> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).getUser().isPricard()) {
                            post.add(list.get(i));
                        }
                    }
                    list.removeAll(post);
                    for (int i = 0; i < list.size(); i++) {
                        PostCache post = new PostCache();
                        post.setUpdateTime(list.get(i).getUpdatedAt());
                        post.setContentOne(list.get(i).getContentOne());
                        post.setContentTwo(list.get(i).getContentTwo());
                        post.setContentThree(list.get(i).getContentThree());
                        post.setCardUrl(list.get(i).getImage().getFileUrl());
                        post.setDetailUrl(list.get(i).getCard().getFileUrl());
                        post.setName(list.get(i).getUser().getName());
                        post.setObjectId(list.get(i).getUser().getObjectId());
                        post.setPostId(list.get(i).getObjectId());
                        post.setUserImg(list.get(i).getUser().getImage().getFileUrl());
                        post.save();
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                    adapter = new ExploreRecycleViewAdapter(getActivity(), true);
                    adapter.setPostes(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
                    if (BmobUtil.GetData(getActivity())) {
                        recyclerView.setLayoutManager(layoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setLayoutManager(gridLayoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    RecyclerViewUtils.setHeaderView(recyclerView, new Explore_Header(getActivity()));
                    if (spinnerLoading != null) {
                        spinnerLoading.setVisibility(View.GONE);
                        rl.removeView(spinnerLoading);
                    }
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadCache(List<PostCache> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        adapter = new ExploreRecycleViewAdapter(getActivity(), false);
        adapter.setPostCaches(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
        if (BmobUtil.GetData(getActivity())) {
            recyclerView.setLayoutManager(layoutManager);
            mLRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(num);
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            mLRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(num - 2);
        }
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        RecyclerViewUtils.setHeaderView(recyclerView, new Explore_Header(getActivity()));
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.refresh_img)
    public void onClick() {
        num = 0;
        recyclerView.scrollToPosition(num);
        spinnerLoading = new SpinnerLoading(getActivity());
        spinnerLoading.setBackground(getResources().getDrawable(R.drawable.shadow));
        spinnerLoading.setPaintMode(1);
        spinnerLoading.setItemCount(8);
        spinnerLoading.setCircleRadius(20);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(spinnerLoading, layoutParams);
        initData();
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }
}
