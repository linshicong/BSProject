package com.linshicong.bsproject.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.CloudGalleyRecycleViewAdapter;
import com.linshicong.bsproject.bean.PhotoTable;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.GalleyCache;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 云相册逻辑实现
 * @date 2017/2/28 8:58
 */
public class CloudGalleyActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;

    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private CloudGalleyRecycleViewAdapter adapter;
    private List<PhotoTable> photoTables;
    private TextView galleyName;
    private LinearLayout llDelete;
    private LinearLayout llEdit;
    private List<GalleyCache> galleyCaches;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_galley_activity);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 每次重新回到此界面，先判断是否有缓存数据，有缓存数据先加载缓存数据，没有再从网络上加载
     */
    @Override
    protected void onResume() {
        super.onResume();
        //查找Sqlite中关于云相册的数据
        galleyCaches = DataSupport.findAll(GalleyCache.class);
        if (galleyCaches.isEmpty()) {
            Log.i("photo", "network");
            initData();
        } else {
            Log.i("photo", "cache");
            loadCache(DataSupport.findAll(GalleyCache.class));
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

            }
        });
    }

    /**
     * 从缓存中加载数据
     *
     * @param list
     */
    private void loadCache(final List<GalleyCache> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(CloudGalleyActivity.this);
        adapter = new CloudGalleyRecycleViewAdapter(CloudGalleyActivity.this, false);
        adapter.setGalleyCaches(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(CloudGalleyActivity.this, adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                Intent intent = new Intent(CloudGalleyActivity.this, GalleyPhotoDetailActivity.class);
                intent.putExtra("table_name", list.get(i).getName());
                ArrayList<String> array = new ArrayList<String>();
                array.addAll(list.get(i).getList());
                intent.putStringArrayListExtra("list", array);
                intent.putExtra("objectId", list.get(i).getObjectId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int i) {
                showDialog(list.get(i).getObjectId(), list.get(i).getName(), list.get(i).getNum());
            }
        });
    }

    /**
     * 从下方弹出一个alertDialog
     * 1.   编辑相册
     * 2.   删除相册
     *
     * @param objId
     * @param name
     */
    private void showDialog(final String objId, final String name, final int num) {
        final Dialog dialog = new Dialog(CloudGalleyActivity.this, R.style.Theme_Light_Dialog);
        View dialogView = LayoutInflater.from(CloudGalleyActivity.this).inflate(R.layout.dialog_item, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        llEdit = (LinearLayout) dialog.findViewById(R.id.ll_edit);
        llDelete = (LinearLayout) dialogView.findViewById(R.id.ll_delete);
        galleyName = (TextView) dialogView.findViewById(R.id.galley_name);
        galleyName.setText(name);
        dialog.show();
        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final Dialog mDialog = new Dialog(CloudGalleyActivity.this, R.style.Theme_Light_Dialog);
                View dialogView = LayoutInflater.from(CloudGalleyActivity.this).inflate(R.layout.galley_dialog_activity, null);
                Window window = mDialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.getDecorView().setPadding(0, 0, 0, 0);
                android.view.WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                mDialog.setContentView(dialogView);
                mDialog.show();
                final EditText galleyNameText = (EditText) dialogView.findViewById(R.id.galley_name_text);
                Button commitBtn = (Button) dialogView.findViewById(R.id.commit_btn);
                Button backBtn = (Button) dialogView.findViewById(R.id.back_btn);
                TextView dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
                dialogText.setText("編輯相冊");
                galleyNameText.setText(name);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        return;
                    }
                });
                commitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String tableName = galleyNameText.getText().toString();
                        if (tableName.isEmpty()) {
                            Toast.makeText(CloudGalleyActivity.this, "相冊名不能為空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HttpUtil.isConnected(CloudGalleyActivity.this)) {
                            Toast.makeText(CloudGalleyActivity.this, "網路錯誤,請檢查網絡", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        View v = getWindow().peekDecorView();
                        if (v != null) {
                            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        final ProgressDialog progress = new ProgressDialog(CloudGalleyActivity.this);
                        progress.setMessage("正在修改...");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();
                        PhotoTable photoTable = new PhotoTable();
                        photoTable.setObjectId(objId);
                        photoTable.setName(tableName);
                        photoTable.setNum(num);
                        photoTable.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(CloudGalleyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    GalleyCache g = new GalleyCache();
                                    g.setName(tableName);
                                    g.updateAll("objectId=?", objId);
                                    initData();
                                    mLRecyclerViewAdapter.notifyDataSetChanged();
                                    progress.dismiss();
                                    mDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        });
        //删除点击事件
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(CloudGalleyActivity.this);
                dialog.setTitle("菲林");
                dialog.setMessage("您確認刪除該相冊<<" + name + ">>？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!HttpUtil.isConnected(CloudGalleyActivity.this)) {
                            Toast.makeText(CloudGalleyActivity.this, "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final ProgressDialog progress = new ProgressDialog(CloudGalleyActivity.this);
                        progress.setMessage("正在删除...");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();
                        PhotoTable photoTable = new PhotoTable();
                        photoTable.setObjectId(objId);
                        photoTable.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(CloudGalleyActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    DataSupport.deleteAll(GalleyCache.class, "objectId=?", objId);
                                    initData();
                                    mLRecyclerViewAdapter.notifyDataSetChanged();
                                    progress.dismiss();
                                }
                            }
                        });
                    }
                });
                /**返回*/
                dialog.setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("云相冊");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        galleyCaches = new ArrayList<>();
    }

    /**
     * 加载菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_galley, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单点击事件，即新建相册
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                startActivity(new Intent(this, GalleyDialogActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 从网络中加载数据
     */
    private void initData() {
        if (!HttpUtil.isConnected(this)) {
            Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<PhotoTable> query = new BmobQuery<PhotoTable>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        User user = BmobUtil.GetCurrentUser();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.include("user");
        query.findObjects(new FindListener<PhotoTable>() {
            @Override
            public void done(final List<PhotoTable> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        DataSupport.deleteAll(GalleyCache.class, "objectId=?", list.get(i).getObjectId());
                        GalleyCache galley = new GalleyCache();
                        galley.setName(list.get(i).getName());
                        galley.setNum(list.get(i).getNum());
                        galley.setList(list.get(i).getPhoto());
                        galley.setObjectId(list.get(i).getObjectId());
                        galley.save();
                    }
                    photoTables = list;
                    LinearLayoutManager layoutManager = new LinearLayoutManager(CloudGalleyActivity.this);
                    adapter = new CloudGalleyRecycleViewAdapter(CloudGalleyActivity.this, true);
                    adapter.setPhotoTables(list);
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(CloudGalleyActivity.this, adapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                    mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int i) {
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            Intent intent = new Intent(CloudGalleyActivity.this, GalleyPhotoDetailActivity.class);
                            intent.putExtra("table_name", photoTables.get(i).getName());
                            ArrayList<String> array = new ArrayList<String>();
                            array.addAll(photoTables.get(i).getPhoto());
                            intent.putStringArrayListExtra("list", array);
                            intent.putExtra("objectId", photoTables.get(i).getObjectId());
                            startActivity(intent);
                        }

                        @Override
                        public void onItemLongClick(View view, final int i) {
                            showDialog(list.get(i).getObjectId(), list.get(i).getName(), list.get(i).getNum());
                        }
                    });
                }
            }
        });
    }
}
