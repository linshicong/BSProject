package com.linshicong.bsproject.activity.display;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.linshicong.bsproject.bean.Post;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.UserCache;
import com.linshicong.bsproject.util.BitmapUtil;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.litepal.crud.DataSupport;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import tyrantgit.explosionfield.ExplosionField;

import static com.linshicong.bsproject.util.Constant.isUpdate;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity.display
 * @Description: 我的卡片逻辑处理
 * @date 2016/9/22 12:46
 */
public class DisplayCardActivity extends SlideBackActivity {
    private String imageUrl, objId, postId;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.card_img)
    SimpleDraweeView cardImg;
    private ExplosionField explosionField;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_card_activity);
        ButterKnife.bind(this);
        initView();
        //获得需要展示的卡片的用户ID，卡片ID，卡片的Url地址
        Intent i = getIntent();
        imageUrl = i.getStringExtra("url");
        objId = i.getStringExtra("objid");
        postId = i.getStringExtra("postid");
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
     * 加载Toolbar的菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    /**
     * 从下方弹出一个下载的弹框，并注册下载点击事件
     */
    private void showDialog() {
        final Dialog dialog = new Dialog(DisplayCardActivity.this, R.style.Theme_Light_Dialog);
        View dialogView = LayoutInflater.from(DisplayCardActivity.this).inflate(R.layout.display_card_dialog, null);
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
                if (!HttpUtil.isConnected(DisplayCardActivity.this)) {
                    Toast.makeText(DisplayCardActivity.this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DisplayCardActivity.this, "下載成功", Toast.LENGTH_SHORT).show();
                    ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    BitmapUtil.saveImage(loadedImage, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + ".png");
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
     * 菜单选项点击逻辑处理
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                item.setEnabled(false);
                //弹出对话框确认是否删除卡片
                AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayCardActivity.this);
                dialog.setTitle("菲林CAMERA");
                dialog.setMessage("是否確定刪除該卡片？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //展示一个动画，把卡片粉碎的效果
                        explosionField.explode(cardImg);
                        cardImg.setVisibility(View.GONE);
                        //从Bmob服务器中的Post表中把该卡片的数据删除
                        final Post post = new Post();
                        post.setObjectId(getIntent().getStringExtra("postid"));
                        post.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(DisplayCardActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    item.setEnabled(true);
                                    //更新用户的卡片数
                                    User user = BmobUtil.GetCurrentUser();
                                    user.setCardnum(user.getCardnum() - 1);
                                    user.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i("cardnum", "cardnum");
                                            }
                                        }
                                    });
                                    isUpdate = true;
                                    //把该卡片从缓存中删除
                                    DataSupport.deleteAll(UserCache.class, "postId=?", postId);
                                    finish();
                                } else {
                                    Toast.makeText(DisplayCardActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                                    Log.i("showone", e.toString());
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("暫不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                dialog.show();
                break;
            case R.id.share:
                final ProgressDialog progress = new ProgressDialog(DisplayCardActivity.this);
                if (!HttpUtil.isConnected(this)) {
                    Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
                    break;
                }
                progress.setMessage("正在加載分享界面...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                //先下载图片，然后调用Android源生的分享功能分享到第三方APP
                ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                BitmapUtil.saveImage(loadedImage, "download.png");
                                progress.dismiss();
                                ShareActivity.shareSingleImage(DisplayCardActivity.this);
                            }
                        }
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 判断该卡片的用户和当前用户的ID是否一样
     * 如果一样，则可以进行删除操作
     * 否则，隐藏了删除的图标
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!objId.equals(BmobUtil.GetCurrentUser().getObjectId())) {
            menu.findItem(R.id.action_settings).setVisible(false);
        } else {
            menu.findItem(R.id.action_settings).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 初始化控件
     */
    public void initView() {
        explosionField = ExplosionField.attach2Window(this);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("卡片詳情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
