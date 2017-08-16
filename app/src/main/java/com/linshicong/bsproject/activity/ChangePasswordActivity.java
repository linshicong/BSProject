package com.linshicong.bsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.util.HttpUtil;
import com.linshicong.bsproject.util.MyCountTimer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.ui
 * @Description: 修改密码逻辑处理
 * @date 2016/9/13 1:34
 */
public class ChangePasswordActivity extends SlideBackActivity {
    MyCountTimer timer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.code_text)
    EditText codeText;
    @Bind(R.id.send_code_btn)
    Button sendCodeBtn;
    @Bind(R.id.new_password_text)
    EditText newPasswordText;
    @Bind(R.id.new_repeat_password_text)
    EditText newRepeatPasswordText;
    @Bind(R.id.change_password_btn)
    CircularProgressButton changePasswordBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        /**进入修改密码页面后先判断是否有网络连接，如果无网络连接，则无法进行密码修改*/
        if (!HttpUtil.isConnected(ChangePasswordActivity.this)) {
            Toast.makeText(ChangePasswordActivity.this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
        }
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("修改密碼");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //初始化自定义进度条按钮
        changePasswordBtn.setIndeterminateProgressMode(true);
        changePasswordBtn.setProgress(0);
    }


    /**
     * 发送验证码
     */
    private void sendCode() {
        if (!HttpUtil.isConnected(ChangePasswordActivity.this)) {
            Toast.makeText(ChangePasswordActivity.this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = BmobUser.getCurrentUser().getMobilePhoneNumber();
        timer = new MyCountTimer(60000, 1000, sendCodeBtn);
        timer.start();
        BmobSMS.requestSMSCode(phone, "模板1", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null)
                    Toast.makeText(ChangePasswordActivity.this, "發送成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ChangePasswordActivity.this, "發送失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更改密码
     * 根据短信验证码修改密码
     * 如果验证码正确，则退出登录并回到登录注册界面让用户重新登录
     */
    private void changePassword() {
        String newPassword = newPasswordText.getText().toString();
        String repeatNewPassword = newRepeatPasswordText.getText().toString();
        String cod = codeText.getText().toString();
        if (newPassword.length() == 0) {
            Toast.makeText(this, "密碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (repeatNewPassword.length() == 0) {
            Toast.makeText(this, "請重複密碼", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(repeatNewPassword)) {
            Toast.makeText(this, "密碼不一致，請確認", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!HttpUtil.isConnected(ChangePasswordActivity.this)) {
            Toast.makeText(this, "無網絡連接，請檢查網絡狀態", Toast.LENGTH_SHORT).show();
            return;
        }
        //进度为1-99表示正在加载中
        changePasswordBtn.setProgress(50);
        BmobUser.resetPasswordBySMSCode(cod, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //进度为100表示成功
                    changePasswordBtn.setProgress(100);
                    BmobUser.logOut();
                    finish();
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ChangePasswordActivity.this);
                    Intent intent = new Intent("com.linshicong.finish");
                    localBroadcastManager.sendBroadcast(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                }else{
                    //进度为-1表示失败
                    changePasswordBtn.setProgress(-1);
                    //2秒后重新恢复按钮
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changePasswordBtn.setProgress(0);
                        }
                    },2000);
                }
            }
        });
    }

    @OnClick({R.id.send_code_btn, R.id.change_password_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_code_btn:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendCode();
                    }
                });
                break;
            case R.id.change_password_btn:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changePassword();
                    }
                });
                break;
        }
    }
}
