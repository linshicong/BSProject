package com.linshicong.bsproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.adapter.PhotoRecycleViewAdapter;
import com.linshicong.bsproject.bean.PhotoTable;
import com.linshicong.bsproject.db.GalleyCache;
import com.linshicong.bsproject.util.HttpUtil;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.linshicong.bsproject.util.Constant.isAll;
import static com.linshicong.bsproject.util.Constant.isCheck;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;


/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 云相册大图模式
 * @date 2017/2/28 8:11
 */

public class GalleyPhotoDetailActivity extends SlideBackActivity {
    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.table_name_text)
    TextView tableNameText;
    @Bind(R.id.add_photo_text)
    TextView addPhotoText;
    @Bind(R.id.ll_layout)
    LinearLayout llLayout;
    @Bind(R.id.cancel_btn)
    Button cancelBtn;
    @Bind(R.id.number_text)
    TextView numberText;
    @Bind(R.id.select_all_btn)
    Button selectAllBtn;
    @Bind(R.id.clear_btn)
    Button clearBtn;
    @Bind(R.id.recyclerView)
    LRecyclerView recyclerView;
    @Bind(R.id.header)
    LinearLayout header;
    @Bind(R.id.del_btn)
    ImageView delBtn;
    private String objId;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private PhotoRecycleViewAdapter adapter;
    private int num;
    public List<String> selectList = null;
    private List<String> photoList;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galley_photo_detail_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        name = intent.getStringExtra("table_name");
        photoList = intent.getStringArrayListExtra("list");
        if (photoList.isEmpty()) {
            num = 0;
        } else {
            num = photoList.size();
        }
        objId = intent.getStringExtra("objectId");
        tableNameText.setText(name);
        selectList = new ArrayList<>();
        System.out.print(photoList);
    }

    @Override
    protected void onResume() {
        loadCache();
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
        super.onResume();
    }

    /**
     * 从缓存中加载数据
     */
    private void loadCache() {
        if (num == 0) {
            return;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(GalleyPhotoDetailActivity.this, 3, GridLayoutManager.VERTICAL, false);
        adapter = new PhotoRecycleViewAdapter(GalleyPhotoDetailActivity.this);
        adapter.setPhotos(photoList);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(GalleyPhotoDetailActivity.this, adapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        mLRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (isCheck) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        selectList.remove(photoList.get(i));
                    } else {
                        checkBox.setChecked(true);
                        selectList.add(photoList.get(i));
                    }
                } else {
                    Intent in = new Intent(GalleyPhotoDetailActivity.this, ImageDetailActivity.class);
                    ArrayList<String> array = new ArrayList<String>();
                    array.addAll(photoList);
                    in.putStringArrayListExtra("photo", array);
                    in.putExtra("name", name);
                    in.putExtra("num", i);
                    startActivity(in);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onItemLongClick(View view, int i) {
                header.setVisibility(View.VISIBLE);
                llLayout.setVisibility(View.GONE);
                delBtn.setVisibility(View.VISIBLE);
                isCheck = true;
                loadCache();
            }
        });
    }


    /**
     * 从网络中加载数据
     */
    private void initData() {
        if (!HttpUtil.isConnected(GalleyPhotoDetailActivity.this)) {
            Toast.makeText(GalleyPhotoDetailActivity.this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<PhotoTable> query = new BmobQuery<PhotoTable>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.addWhereEqualTo("objectId", objId);
        query.order("-updatedAt");
        query.setLimit(50);
        query.findObjects(new FindListener<PhotoTable>() {
            @Override
            public void done(final List<PhotoTable> list, BmobException e) {
                if (e == null) {
                    PhotoTable photoTable = list.get(0);
                    num = photoTable.getNum();
                    if (num == 0) {
                        return;
                    }
                    photoList = photoTable.getPhoto();
                    GalleyCache galleyCache = new GalleyCache();
                    galleyCache.setNum(photoTable.getNum());
                    galleyCache.setList(photoTable.getPhoto());
                    galleyCache.updateAll("objectId=?", objId);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(GalleyPhotoDetailActivity.this, 3, GridLayoutManager.VERTICAL, false);
                    adapter = new PhotoRecycleViewAdapter(GalleyPhotoDetailActivity.this);
                    adapter.setPhotos(photoTable.getPhoto());
                    mLRecyclerViewAdapter = new LRecyclerViewAdapter(GalleyPhotoDetailActivity.this, adapter);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mLRecyclerViewAdapter);
                    recyclerView.setRefreshProgressStyle(ProgressStyle.LineScale);
                    recyclerView.refreshComplete();
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                    mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int i) {
                            if (isCheck) {
                                CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
                                if (checkBox.isChecked()) {
                                    checkBox.setChecked(false);
                                    selectList.remove(photoList.get(i));
                                } else {
                                    checkBox.setChecked(true);
                                    selectList.add(photoList.get(i));
                                }
                            } else {
                                Intent in = new Intent(GalleyPhotoDetailActivity.this, ImageDetailActivity.class);
                                ArrayList<String> array = new ArrayList<String>();
                                array.addAll(photoList);
                                in.putStringArrayListExtra("photo", array);
                                in.putExtra("name", name);
                                startActivity(in);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }

                        @Override
                        public void onItemLongClick(View view, int i) {
                            header.setVisibility(View.VISIBLE);
                            llLayout.setVisibility(View.GONE);
                            delBtn.setVisibility(View.VISIBLE);
                            isCheck = true;
                            loadCache();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (!HttpUtil.isConnected(GalleyPhotoDetailActivity.this)) {
                        Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final ProgressDialog progress = new ProgressDialog(this);
                    progress.setMessage("上傳中...");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    int n = list.size();
                    final String[] paths = new String[n];
                    for (int i = 0; i < n; i++) {
                        paths[i] = list.get(i).getPath();
                    }
                    BmobFile.uploadBatch(paths, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> list, final List<String> list1) {
                            if (list1.size() == paths.length) {
                                PhotoTable photoTable = new PhotoTable();
                                photoTable.setObjectId(objId);
                                if (num == 0) {
                                    photoTable.setPhoto(list1);
                                    photoTable.setNum(list1.size());
                                } else {
                                    list1.addAll(photoList);
                                    photoTable.setPhoto(list1);
                                    photoTable.setNum(list1.size());
                                }
                                photoTable.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            GalleyCache galleyCache = new GalleyCache();
                                            galleyCache.setList(list1);
                                            galleyCache.setNum(list1.size());
                                            galleyCache.updateAll("objectId=?", objId);
                                            Toast.makeText(GalleyPhotoDetailActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                                            progress.dismiss();
                                            initData();
                                            recyclerView.refreshComplete();
                                            mLRecyclerViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onProgress(int i, int i1, int i2, int i3) {
                        }

                        @Override
                        public void onError(int i, String s) {
                        }
                    });

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.back_btn, R.id.add_photo_text, R.id.cancel_btn, R.id.select_all_btn, R.id.clear_btn, R.id.del_btn})
    public void onClick(View view) {
        BmobQuery<PhotoTable> query = new BmobQuery<PhotoTable>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_photo_text:
                Intent intent1 = new Intent(this, ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.cancel_btn:
                selectList.clear();
                header.setVisibility(View.GONE);
                llLayout.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.GONE);
                selectAllBtn.setVisibility(View.VISIBLE);
                clearBtn.setVisibility(View.GONE);
                isCheck = false;
                isAll = false;
                loadCache();
                break;
            case R.id.select_all_btn:
                isAll = true;
                selectList.clear();
                selectList.addAll(photoList);
                selectAllBtn.setVisibility(View.GONE);
                clearBtn.setVisibility(View.VISIBLE);
                loadCache();
                break;
            case R.id.clear_btn:
                selectList.clear();
                clearBtn.setVisibility(View.GONE);
                selectAllBtn.setVisibility(View.VISIBLE);
                isAll = false;
                loadCache();
                break;
            case R.id.del_btn:
                header.setVisibility(View.GONE);
                llLayout.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.GONE);
                selectAllBtn.setVisibility(View.VISIBLE);
                clearBtn.setVisibility(View.GONE);
                isCheck = false;
                isAll = false;
                PhotoTable pT = new PhotoTable();
                photoList.removeAll(selectList);
                if (!HttpUtil.isConnected(GalleyPhotoDetailActivity.this)) {
                    Toast.makeText(this, "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
                    return;
                }
                pT.setObjectId(objId);
                pT.setNum(photoList.size());
                pT.setPhoto(photoList);
                pT.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            GalleyCache galleyCache = new GalleyCache();
                            galleyCache.setList(photoList);
                            galleyCache.setNum(photoList.size());
                            galleyCache.updateAll("objectId=?", objId);
                        }
                    }
                });
                Toast.makeText(this, "刪除成功", Toast.LENGTH_SHORT).show();
                loadCache();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        header.setVisibility(View.GONE);
        llLayout.setVisibility(View.VISIBLE);
        delBtn.setVisibility(View.GONE);
        selectAllBtn.setVisibility(View.VISIBLE);
        clearBtn.setVisibility(View.GONE);
        isCheck = false;
        isAll = false;
    }
}