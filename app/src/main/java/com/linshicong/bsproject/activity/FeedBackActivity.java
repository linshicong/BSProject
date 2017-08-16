package com.linshicong.bsproject.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.FeedBack;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 用户反馈逻辑处理
 * @date 2016/10/31 15:41
 */
public class FeedBackActivity extends SlideBackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.feedback_name_text)
    EditText feedbackNameText;
    @Bind(R.id.feedback_content_text)
    EditText feedbackContentText;
    @Bind(R.id.commit_btn)
    CircularProgressButton commitBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        commitBtn.setIndeterminateProgressMode(true);
        commitBtn.setProgress(0);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("發送反饋");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @OnClick(R.id.commit_btn)
    public void onClick() {
        /**点击按钮后设置按钮为不可响应，防止多次点击而造成应用Crash*/
        commitBtn.setEnabled(false);
        String feedbackName = feedbackNameText.getText().toString();
        String feedbackContent = feedbackContentText.getText().toString();
        if (feedbackName.isEmpty()) {
            Toast.makeText(this, "請填寫反饋名稱", Toast.LENGTH_SHORT).show();
            commitBtn.setEnabled(true);
            return;
        }
        if (feedbackContent.isEmpty()) {
            Toast.makeText(this, "請填寫反饋內容", Toast.LENGTH_SHORT).show();
            commitBtn.setEnabled(true);
            return;
        }
        if (!HttpUtil.isConnected(FeedBackActivity.this)) {
            Toast.makeText(this, "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
            return;
        }
        commitBtn.setProgress(50);
        FeedBack feedBack = new FeedBack();
        feedBack.setUserid(BmobUtil.GetCurrentUser().getObjectId());
        feedBack.setPhonenumber(BmobUtil.GetCurrentUser().getMobilePhoneNumber());
        feedBack.setName(feedbackName);
        feedBack.setContent(feedbackContent);
        feedBack.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    commitBtn.setProgress(100);
                    Toast.makeText(FeedBackActivity.this, "反馈成功,感谢您的反馈", Toast.LENGTH_SHORT).show();
                    feedbackNameText.setText("");
                    feedbackContentText.setText("");
                    commitBtn.setEnabled(true);
                    finish();
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
    }
}
