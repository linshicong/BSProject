package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.IndexActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;
import com.linshicong.bsproject.util.MyCountTimer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 短信验证登录逻辑
 * @date 2016/9/9 17:05
 */
public class VerifyLoginFragment extends Fragment {
    private static final String TAG = VerifyLoginFragment.class.getSimpleName();
    MyCountTimer timer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.phone_num_text)
    EditText phoneNumText;
    @Bind(R.id.code_text)
    EditText codeText;
    @Bind(R.id.send_code_btn)
    Button sendCodeBtn;
    @Bind(R.id.password_login_text)
    TextView passwordLoginText;
    @Bind(R.id.login_btn)
    CircularProgressButton loginBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verify_login_fragment, null);
        ButterKnife.bind(this, view);
        initToolbar();
        return view;
    }

    /**
     * 请求短信验证码
     */
    private void requestSMSCode() {
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡連接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phonenum = phoneNumText.getText().toString();
        if (!TextUtils.isEmpty(phonenum)) {
            /**
             * 首先查询用户表中，该用户是否已经注册过的
             * 如果没有注册过，则弹出对话框提醒用户还没有注册
             * 如果已经注册过，则获取短信验证码
             */
            BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
            query.addWhereEqualTo("mobilePhoneNumber", phonenum);
            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    if (e == null) {
                        //查询成功
                        if (list.isEmpty()) {
                            /**用户还没注册*/
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("菲林CAMERA");
                            dialog.setMessage("您還沒註冊，是否幫您跳轉到註冊界面？");
                            dialog.setCancelable(false);
                            /**跳转到注册界面*/
                            dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame_content, new RegisterFragment())
                                            .commit();
                                }
                            });
                            /**返回*/
                            dialog.setNegativeButton("暫不", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            dialog.show();
                        } else {
                            /**用户已经注册过，发送短信验证码到手机*/
                            timer = new MyCountTimer(60000, 1000, sendCodeBtn);
                            timer.start();
                            BmobSMS.requestSMSCode(phonenum, "模板1", new QueryListener<Integer>() {
                                @Override
                                public void done(Integer integer, BmobException e) {
                                    if (e == null) {// 验证码发送成功
                                        Toast.makeText(getActivity(), "驗證碼發送成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "雁陣嗎發送失敗,請檢查網絡", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, e.toString());
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    } else {
                        /**查询用户失败*/
                        Toast.makeText(getActivity(), "查找失敗，請檢查網絡", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, e.toString());
                    }
                }
            });
        } else Toast.makeText(getActivity(), "請輸入手機號碼", Toast.LENGTH_SHORT).show();
    }

    /**
     * 短信验证登陆方法
     */
    private void oneKeyLogin() {
        if (!HttpUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡連接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phonenum = phoneNumText.getText().toString();
        final String code = codeText.getText().toString();
        if (TextUtils.isEmpty(phonenum)) {
            Toast.makeText(getActivity(), "手機號碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getActivity(), "驗證碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        loginBtn.setProgress(50);
        BmobUser.loginBySMSCode(phonenum, code, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    loginBtn.setProgress(100);
                    String ANDROID_ID = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);
                    User user1 = BmobUtil.GetCurrentUser();
                    user1.setDeviceid(ANDROID_ID);
                    user1.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("device", "success");
                            }
                        }
                    });
                    Intent intent = new Intent(getActivity(), IndexActivity.class);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    loginBtn.setProgress(-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginBtn.setProgress(0);
                        }
                    }, 2000);
                    Log.i(TAG, e.toString());
                    return;
                }
            }
        });
    }


    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar.setTitle("手機登錄");
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        loginBtn.setIndeterminateProgressMode(true);
        loginBtn.setProgress(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /***
     * 按钮监听点击事件
     *
     * @param view
     */

    @OnClick({R.id.send_code_btn, R.id.password_login_text, R.id.login_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_code_btn:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestSMSCode();

                    }
                });
                break;
            case R.id.password_login_text:
                /**点击密码登录，跳转到密码登录界面*/
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_content, new PasswordLoginFragment(), PasswordLoginFragment.class.getSimpleName())
                        .commit();
                break;
            case R.id.login_btn:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oneKeyLogin();

                    }
                });
                break;
        }
    }
}
