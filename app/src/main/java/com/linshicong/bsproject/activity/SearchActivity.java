package com.linshicong.bsproject.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.FeedRecycleAdapter;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

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
 * @Package com.linshicong.bsproject.index.activity
 * @Description: 查找用户界面逻辑处理
 * @date 2016/10/31 22:11
 */
public class SearchActivity extends SlideBackActivity {

    @Bind(R.id.back_img)
    ImageView backImg;
    @Bind(R.id.key_text)
    EditText keyText;
    @Bind(R.id.search_img)
    ImageView searchImg;
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private FeedRecycleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ButterKnife.bind(this);
    }

    /**
     * 查找用户，查找用户关注表指针指向的所有用户
     */
    private void searchUser() {
        String username = BmobUtil.change(keyText.getText().toString());
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("name", username);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        if (list.size() == 0) {
                            Toast.makeText(SearchActivity.this, "沒有該用戶！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        /**查找成功，把查找的信息加载到RecyclerView中*/
                        Toast.makeText(SearchActivity.this, "查找成功", Toast.LENGTH_SHORT).show();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                        adapter = new FeedRecycleAdapter(SearchActivity.this, true);
                        adapter.setUsers(list);
                        mLRecyclerViewAdapter = new LRecyclerViewAdapter(SearchActivity.this, adapter);
                        recyclerView.setLayoutManager(layoutManager);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mLRecyclerViewAdapter);
                    }
                } else {
                    Log.i("list", e.toString());
                }
            }
        });
    }

    @OnClick({R.id.back_img, R.id.search_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.search_img:
                View v = getWindow().peekDecorView();
                if (v != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (!HttpUtil.isConnected(SearchActivity.this)) {
                    Toast.makeText(SearchActivity.this, "無網絡連接，請檢查網絡", Toast.LENGTH_SHORT).show();
                    return;
                }
                searchUser();
                break;
        }
    }
}
