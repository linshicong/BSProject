package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.FeedRecycleAdapter;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.FeedCache;
import com.linshicong.bsproject.util.HttpUtil;
import com.lusfold.spinnerloading.SpinnerLoading;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment.index
 * @Description: 关注界面
 * @date 2016/9/11 22:41
 */
public class FeedFragment extends Fragment {
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.rl)
    RelativeLayout rl;
    @Bind(R.id.card_view)
    CardView cardView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private FeedRecycleAdapter adapter;
    private static int num = 0;
    private SpinnerLoading spinnerLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<FeedCache> feedCaches = DataSupport.findAll(FeedCache.class);
        if (feedCaches.isEmpty()) {
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
            loadCache(feedCaches);
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
    }

    private void initData() {
        Log.i("feed", "network");
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        DataSupport.deleteAll(FeedCache.class);
        final BmobQuery<User> query = new BmobQuery<User>();
        User bu = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("feed", new BmobPointer(bu));
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> list, BmobException e) {
                if (spinnerLoading != null) {
                    spinnerLoading.setVisibility(View.GONE);
                    rl.removeView(spinnerLoading);
                }
                if (e == null) {
                    if (list.size() == 0) {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        FeedCache feedCache = new FeedCache();
                        feedCache.setObjectId(list.get(i).getObjectId());
                        feedCache.setName(list.get(i).getName());
                        feedCache.setDesc(list.get(i).getDec());
                        feedCache.setCardNum(list.get(i).getCardnum());
                        feedCache.setUserImg(list.get(i).getImage().getFileUrl());
                        feedCache.save();
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    adapter = new FeedRecycleAdapter(getActivity(), true);
                    adapter.setUsers(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 从cache中加载
     *
     * @param feedCaches
     */
    private void loadCache(final List<FeedCache> feedCaches) {
        Log.i("feed", "cache");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new FeedRecycleAdapter(getActivity(), false);
        adapter.setFeedCaches(feedCaches);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(num);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
