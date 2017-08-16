package com.linshicong.bsproject.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.linshicong.bsproject.util.Constant.isUpdate;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.ui
 * @Description: 用户详情界面
 * @date 2016/9/12 1:11
 */
public class InfoActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.user_img)
    SimpleDraweeView userImg;
    @Bind(R.id.username_text)
    TextView usernameText;
    @Bind(R.id.phone_num_text)
    TextView phoneNumText;
    @Bind(R.id.code_img)
    ImageView codeImg;
    @Bind(R.id.name_text)
    EditText nameText;
    @Bind(R.id.user_desc_text)
    EditText userDescText;
    @Bind(R.id.commit_btn)
    CircularProgressButton commitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 预加载数据,加载用户的个人信息
     */
    private void initData() {
        commitBtn.setIndeterminateProgressMode(true);
        commitBtn.setProgress(0);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("编辑个人资料");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        User user = BmobUtil.GetCurrentUser();
        phoneNumText.setText(user.getMobilePhoneNumber());
        usernameText.setText(user.getUsername());
        nameText.setText(user.getName());
        userDescText.setText(user.getDec());
        try {
            if (user.getImage().getFileUrl().isEmpty()) {
                userImg.setImageResource(R.mipmap.icon);
            } else {
                URL url = new URL(user.getImage().getFileUrl());
                Uri uri = Uri.parse(url.toURI().toString());
                userImg.setImageURI(uri);
            }
        } catch (Exception e) {
            userImg.setImageResource(R.mipmap.icon);
            e.printStackTrace();
        }
    }

    /**
     * 获取裁减图片的路径
     */
    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 事件回调逻辑处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = getImagePath(uri, null);
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                userImg.setImageBitmap(bitmap);
                final BmobFile file = new BmobFile(new File(path));
                file.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(InfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            User user1 = BmobUser.getCurrentUser(User.class);
                            if (user1.getImage() != null) {
                                BmobFile b1 = new BmobFile();
                                b1.setUrl(user1.getImage().getFileUrl());
                                b1.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.i("Delete", "删除原图片成功");
                                        }
                                    }
                                });
                            }
                            user1.setImage(file);
                            user1.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(InfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                        initData();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(InfoActivity.this, "失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e("qwe", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick({R.id.user_img, R.id.code_img, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:
                /**点击用户头像重新设置头像，跳转到相册*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivityForResult(intent, 1);
                break;
            case R.id.code_img:
                /**点击二维码图标进入二维码详情界面*/
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                startActivity(new Intent(InfoActivity.this, CodeActivity.class));
                break;
            case R.id.commit_btn:
                /**
                 * 提交按钮点击
                 * 首先判断网络是否连接
                 * 如果已经连接，把用户的信息重新更新到用户表
                 */
                if (!HttpUtil.isConnected(InfoActivity.this)) {
                    Toast.makeText(InfoActivity.this, "无网络连接，请检查网络状态", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = BmobUtil.GetCurrentUser();
                user.setName(BmobUtil.change(nameText.getText().toString()));
                user.setDec(BmobUtil.change(userDescText.getText().toString()));
                commitBtn.setProgress(50);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            isUpdate = true;
                            commitBtn.setProgress(100);
                            Toast.makeText(InfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            commitBtn.setProgress(-1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    commitBtn.setProgress(0);
                                }
                            }, 2000);
                        }
                    }
                });
                break;
        }
    }
}
