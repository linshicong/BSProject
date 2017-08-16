package com.linshicong.bsproject.activity.display;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.ShareActivity;
import com.linshicong.bsproject.activity.SlideBackActivity;
import com.linshicong.bsproject.util.BitmapUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity.display
 * @Description: 展示卡片详情逻辑
 * @date 2016/9/23 20:16
 */
public class DisplayOthersCardActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.card_img)
    SimpleDraweeView cardImg;
    private String objId;
    private String imageUrl;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_card_activity);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        Intent intent = getIntent();
        toolbar.setTitle(intent.getStringExtra("name"));
        setSupportActionBar(toolbar);
        //获取需要展示的卡片Url和卡片id
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageUrl = intent.getStringExtra("url");
        objId = intent.getStringExtra("objid");
        try {
            URL url = new URL(imageUrl);
            uri = Uri.parse(url.toURI().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (HttpUtil.isConnected(this)) {
            cardImg.setImageURI(uri);
        } else {
            boolean isCacheInDisk = Fresco.getImagePipelineFactory().getMainDiskStorageCache().hasKey(new SimpleCacheKey(imageUrl));
            if (isCacheInDisk) {
                cardImg.setImageURI(uri);
            } else {
                Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            }
        }
        cardImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog();
                return false;
            }
        });
    }

    /**
     * 从下方弹出一个下载的弹框，并注册下载点击事件
     */
    private void showDialog() {
        final Dialog dialog = new Dialog(DisplayOthersCardActivity.this, R.style.Theme_Light_Dialog);
        View dialogView = LayoutInflater.from(DisplayOthersCardActivity.this).inflate(R.layout.display_card_dialog, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        LinearLayout ll = (LinearLayout) dialogView.findViewById(R.id.ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HttpUtil.isConnected(DisplayOthersCardActivity.this)) {
                    Toast.makeText(DisplayOthersCardActivity.this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DisplayOthersCardActivity.this, "下載成功", Toast.LENGTH_SHORT).show();
                    ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    BitmapUtil.saveImage(loadedImage,new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())+".png");
                                }
                            }
                    );

                }
                dialog.dismiss();
            }

        });
        //通知相冊更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.parse("file:///sdcard/Feilin/");
        intent.setData(uri);
        sendBroadcast(intent);
        dialog.show();
    }

    /**
     * toolbar菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //进入用户的详情界面
            case R.id.action_report:
                Intent intent = new Intent(this, OtherUserActivity.class);
                intent.putExtra("objid", objId);
                startActivity(intent);
                break;
            //分享卡片
            case R.id.share:
                if (!HttpUtil.isConnected(this)) {
                    Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                    break;
                }
                final ProgressDialog progress = new ProgressDialog(DisplayOthersCardActivity.this);
                progress.setMessage("正在加載分享界面...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                BitmapUtil.saveImage(loadedImage, "download.png");
                                progress.dismiss();
                                ShareActivity.shareSingleImage(DisplayOthersCardActivity.this);
                            }
                        }
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 菜单加载事件
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_others, menu);
        return true;
    }
}