package com.linshicong.bsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.util.HttpUtil;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by linshicong on 2017/2/17.
 */

public class SendSkillActivity extends SlideBackActivity {
    @Bind(R.id.choose_files_btn)
    CircularProgressButton chooseFilesBtn;
    @Bind(R.id.upload_files_btn)
    CircularProgressButton uploadFilesBtn;
    private String path;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.skill_name_text)
    EditText skillNameText;
    @Bind(R.id.skill_files_text)
    TextView skillFilesText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_skill_activity);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    path = list.get(0).getPath();
                    skillFilesText.setText(path);
                }
                break;
        }
    }

    private void initView() {
        path = "";
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("投稿");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        uploadFilesBtn.setIndeterminateProgressMode(true);
        uploadFilesBtn.setProgress(0);
    }


    @OnClick({R.id.choose_files_btn, R.id.upload_files_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_files_btn:
                Intent intent = new Intent(this, NormalFilePickActivity.class);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                intent.putExtra(NormalFilePickActivity.SUFFIX,
                        new String[]{"doc", "docx", "ppt", "pptx", "pdf", "md"});
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.upload_files_btn:
                uploadFilesBtn.setEnabled(false);
                if ("".equals(skillNameText.getText().toString())) {
                    Toast.makeText(this, "請輸入名稱", Toast.LENGTH_SHORT).show();
                    uploadFilesBtn.setEnabled(true);
                    return;
                }
                if ("".equals(path)) {
                    Toast.makeText(this, "請選擇文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!HttpUtil.isConnected(SendSkillActivity.this)) {
                    Toast.makeText(SendSkillActivity.this, "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadFilesBtn.setProgress(50);
                BmobFile bmobFile = new BmobFile(new File(path));
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        uploadFilesBtn.setEnabled(true);
                        if (e == null) {
                            uploadFilesBtn.setProgress(100);
                            finish();
                        } else {
                            uploadFilesBtn.setProgress(-1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadFilesBtn.setProgress(0);
                                }
                            }, 2000);
                            Log.i("e", e.toString());
                        }
                    }
                });
                break;
        }
    }
}
