package com.linshicong.bsproject.activity.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewUtils;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.SlideBackActivity;
import com.linshicong.bsproject.adapter.TimelineRecyclerViewAdapter;
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.linshicong.bsproject.view.OthersHeaderView;
import com.lusfold.spinnerloading.SpinnerLoading;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.ui
 * @Description: 其他用户的详情逻辑
 * @date 2016/9/25 2:08
 */
public class OtherUserActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.rl)
    RelativeLayout rl;
    private TimelineRecyclerViewAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private String objId;
    BroadcastReceiver broadcastReceiver;
    private boolean update = true;
    private LocalBroadcastManager local;
    private SpinnerLoading spinnerLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otheruser_activity);
        ButterKnife.bind(this);
        initView();
        Intent intent = getIntent();
        objId = intent.getStringExtra("objid");
        spinnerLoading = new SpinnerLoading(this);
        spinnerLoading.setBackground(getResources().getDrawable(R.drawable.shadow));
        spinnerLoading.setPaintMode(1);
        spinnerLoading.setItemCount(8);
        spinnerLoading.setCircleRadius(20);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(spinnerLoading, layoutParams);
        refreshData();
        //下拉刷新处理
        recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
        recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                refreshData();
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
            }
        });

        /**
         * 监听广播，如果有查看方式的改变时重新刷新数据
         */
        local = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.linshicong.MY");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                update = false;
                refreshData();
            }
        };
        local.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 初始化数据
     */
    private void initView() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("他的卡片");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得要取消注册本地广播，否则会引起上下文的错乱。
        local.unregisterReceiver(broadcastReceiver);
    }

    /**
     * 加载数据
     */
    private void refreshData() {
        if (!HttpUtil.isConnected(this)) {
            Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setObjectId(objId);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.include("user");
        query.setLimit(50);
        //如果是查看方式的更改而导致的加载数据，则使用缓存，否则使用网络刷新数据
        if (update) {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            update = true;
        }
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    if (spinnerLoading != null) {
                        spinnerLoading.setVisibility(View.GONE);
                        rl.removeView(spinnerLoading);
                    }
                    //用RecycleView加载List数据，根据查看方式选择不同的加载样式
                    if (list.size() != 0 && !list.get(0).getUser().isPricard()) {
                        list.clear();
                        Toast.makeText(OtherUserActivity.this, "您無權限查看對方卡片。", Toast.LENGTH_SHORT).show();
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(OtherUserActivity.this);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(OtherUserActivity.this, 2, GridLayoutManager.VERTICAL, false);
                    adapter = new TimelineRecyclerViewAdapter(OtherUserActivity.this, true);
                    adapter.setPostes(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(OtherUserActivity.this, adapter);
                    if (BmobUtil.GetData(OtherUserActivity.this)) {
                        recyclerView.setLayoutManager(layoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setLayoutManager(gridLayoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    RecyclerViewUtils.setHeaderView(recyclerView, new OthersHeaderView(OtherUserActivity.this, objId));
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
