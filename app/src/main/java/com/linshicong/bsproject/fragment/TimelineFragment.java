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
import android.util.Log;
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
import com.linshicong.bsproject.activity.TempleActivity;
import com.linshicong.bsproject.adapter.TimelineRecyclerViewAdapter;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.UserCache;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.linshicong.bsproject.view.HeaderView;
import com.lusfold.spinnerloading.SpinnerLoading;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.linshicong.bsproject.util.Constant.isUpdate;
import static com.linshicong.bsproject.util.Constant.timeline;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment.index
 * @Description: 個人頁面邏輯處理
 * @date 2016/9/11 22:29
 */
public class TimelineFragment extends Fragment {
    private static final String TAG = TimelineFragment.class.getSimpleName();
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.add_img)
    ImageView addImg;
    @Bind(R.id.rl)
    RelativeLayout rl;
    private TimelineRecyclerViewAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver;
    private  SpinnerLoading spinnerLoading;
    private List<UserCache> userCaches;
    private static int num = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 注册本地广播，如果有切换查看方式的刷新布局
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
    public void onResume() {
        userCaches = DataSupport.findAll(UserCache.class);
        if (userCaches.isEmpty()) {
            spinnerLoading=new SpinnerLoading(getActivity());
            spinnerLoading.setBackground(getResources().getDrawable(R.drawable.shadow));
            spinnerLoading.setPaintMode(1);
            spinnerLoading.setItemCount(8);
            spinnerLoading.setCircleRadius(20);
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(200,200);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl.addView(spinnerLoading,layoutParams);
            initData();
        } else {
            if (isUpdate) {
                isUpdate = false;
                initData();
            } else {
                loadCache(userCaches);
            }
        }
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onScrollUp() {
                addImg.setVisibility(View.GONE);
            }

            @Override
            public void onScrollDown() {
                addImg.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        addImg.setVisibility(View.VISIBLE);
        ButterKnife.unbind(this);
    }

    private void loadCache(final List<UserCache> userCaches) {
        Log.i("timeline", "cache");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        adapter = new TimelineRecyclerViewAdapter(getActivity(), false);
        adapter.setUserCaches(userCaches);
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
        RecyclerViewUtils.setHeaderView(recyclerView, new HeaderView(getActivity()));
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initData() {
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("timeline", "network");
        DataSupport.deleteAll(UserCache.class);

        final User user = BmobUtil.GetCurrentUser();
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.setLimit(50);
        query.include("user");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(final List<Post> list, BmobException e) {
               // rl.removeView(loadingView);
                if (spinnerLoading!=null) {
                    spinnerLoading.setVisibility(View.GONE);
                    rl.removeView(spinnerLoading);
                }
                timeline = false;
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        UserCache usercache = new UserCache();
                        usercache.setContentOne(list.get(i).getContentOne());
                        usercache.setContentTwo(list.get(i).getContentTwo());
                        usercache.setContentThree(list.get(i).getContentThree());
                        usercache.setPostId(list.get(i).getObjectId());
                        usercache.setCardUrl(list.get(i).getImage().getFileUrl());
                        usercache.setDetailUrl(list.get(i).getCard().getFileUrl());
                        usercache.setUpdateTime(list.get(i).getUpdatedAt());
                        usercache.save();
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                    adapter = new TimelineRecyclerViewAdapter(getActivity(), true);
                    adapter.setPostes(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(getActivity(), adapter);
                    if (BmobUtil.GetData(getActivity())) {
                        recyclerView.setLayoutManager(layoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setLayoutManager(gridLayoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotateMultiple);
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    RecyclerViewUtils.setHeaderView(recyclerView, new HeaderView(getActivity()));
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @OnClick(R.id.add_img)
    public void onClick() {
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(getActivity(), TempleActivity.class));
    }
}
