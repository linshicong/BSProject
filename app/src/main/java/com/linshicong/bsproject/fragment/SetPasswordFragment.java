package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.IndexActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.HttpUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 设置账号密码逻辑处理
 * @date 2017/1/6 13:54
 */
public class SetPasswordFragment extends Fragment {
    private static final String TAG = SetPasswordFragment.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.username_text)
    EditText usernameText;
    @Bind(R.id.password_text)
    EditText passwordText;
    @Bind(R.id.repeat_password_text)
    EditText repetPasswordText;
    @Bind(R.id.begin_btn)
    CircularProgressButton beginBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setpassword_fragment, null);
        ButterKnife.bind(this, view);
        initToolbar();
        return view;
    }

    /**
     * 初始化控件
     */
    public void initToolbar() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("設置個人信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        beginBtn.setIndeterminateProgressMode(true);
        beginBtn.setProgress(0);
    }

    /**
     * 设置账号详情，并登陆
     */
    public void loginOn() {
        if (!HttpUtil.isConnected(getActivity())){
            Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡連接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phone = getArguments().getString("phone");
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        final String repeatPassword = repetPasswordText.getText().toString();
        if (username.length() == 0) {
            Toast.makeText(getActivity(), "賬號不能為空...." + username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() == 0) {
            Toast.makeText(getActivity(), "密碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (repeatPassword.length() == 0) {
            Toast.makeText(getActivity(), "請重複密碼", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(getActivity(), "密碼不一致，請確認", Toast.LENGTH_SHORT).show();
            return;
        }
        /**获得用户的设备号并更新到用户表中*/
        String ANDROID_ID = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);
        User bu = new User();
        bu.setUsername(username);
        bu.setPassword(password);
        bu.setName(username);
        bu.setCardnum(0);
        bu.setPricard(false);
        bu.setAutosave(true);
        bu.setDeviceid(ANDROID_ID);
        bu.setDec("用白色明信片框住每個美麗瞬間");
        bu.setMobilePhoneNumber(phone);
        bu.setMobilePhoneNumberVerified(true);
        bu.setImage(new BmobFile("icon.png","user","http://bmob-cdn-6538.b0.upaiyun.com/2016/10/28/51972c9070114d7195e5f498f1fde378.jpg"));
        beginBtn.setProgress(50);
        bu.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    /**注册成功，用用户账号密码登录*/
                    BmobUser bmobUser = new BmobUser();
                    bmobUser.setUsername(username);
                    bmobUser.setPassword(password);
                    bmobUser.login(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if (e == null) {
                                /**登录成功*/
                                //progress.dismiss();
                                beginBtn.setProgress(100);
                                Toast.makeText(getActivity(), "登錄成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), IndexActivity.class);
                                getActivity().overridePendingTransition(0, 0);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                //progress.dismiss();
                                beginBtn.setProgress(-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        beginBtn.setProgress(0);
                                    }
                                },2000);
                                Log.i(TAG, e.toString());
                                Toast.makeText(getActivity(), "登錄失敗，請檢查網絡", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    beginBtn.setProgress(-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            beginBtn.setProgress(0);
                        }
                    },2000);
                    Toast.makeText(getActivity(), "註冊失敗，請檢查網絡", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.toString());
                    return;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 按钮监听点击事件
     */
    @OnClick(R.id.begin_btn)
    public void onClick() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginOn();
            }
        });
    }
}