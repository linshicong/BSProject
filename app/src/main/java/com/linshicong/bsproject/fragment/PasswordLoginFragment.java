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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.IndexActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.util.BmobUtil;
import com.linshicong.bsproject.util.HttpUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 密码登录逻辑
 * @date 2016/9/9 18:47
 */
public class PasswordLoginFragment extends Fragment {
    private static final String TAG = PasswordLoginFragment.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.username_text)
    EditText usernameText;
    @Bind(R.id.password_text)
    EditText passwordText;
    @Bind(R.id.verify_login_text)
    TextView verifyLoginText;
    @Bind(R.id.login_btn)
    CircularProgressButton loginBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.password_login_fragment, null);
        ButterKnife.bind(this, view);
        initToolbar();
        return view;
    }

    /**
     * 登录事件逻辑处理
     * 先查询用户表中用户是否已经注册过
     * 如果没有，则跳转到用户注册界面
     */
    public void loginOn() {
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if (e == null) {
                    if (list.isEmpty()) {
                        /**用户还没注册，弹出对话框提醒用户还没有注册*/
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("菲林CAMERA");
                        dialog.setMessage("您還沒註冊，是否幫您跳轉到註冊界面？");
                        dialog.setCancelable(false);
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
                        loginBtn.setProgress(0);
                    } else {
                        /**用户已经注册，验证密码*/
                        BmobUser bmobUser = new BmobUser();
                        bmobUser.setUsername(username);
                        bmobUser.setPassword(password);
                        bmobUser.login(new SaveListener<BmobUser>() {
                            @Override
                            public void done(BmobUser bmobUser, BmobException e) {
                                if (e == null) {
                                    loginBtn.setProgress(100);
                                    /**用户登录，更新用户登录的设备号*/
                                    String ANDROID_ID = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);
                                    User user = BmobUtil.GetCurrentUser();
                                    user.setDeviceid(ANDROID_ID);
                                    user.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i("device", "success");
                                            }
                                        }
                                    });
                                    startActivity(new Intent(getActivity(), IndexActivity.class));
                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    getActivity().finish();
                                } else {
                                    loginBtn.setProgress(-1);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loginBtn.setProgress(0);
                                        }
                                    }, 2000);
                                    return;
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "登錄失敗，請檢查網絡", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.toString());
                }
            }
        });

    }

    /**
     * 初始化控件
     */
    public void initToolbar() {
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("賬號登陸");
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
    @OnClick({R.id.verify_login_text, R.id.login_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verify_login_text:
                /**点击手机验证登陆，跳转到验证登陆界面*/
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_content, new VerifyLoginFragment(), VerifyLoginFragment.class.getSimpleName())
                        .commit();
                break;
            case R.id.login_btn:
                if (usernameText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "請填寫賬號", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "請填寫密碼", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!HttpUtil.isConnected(getActivity())){
                    Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginBtn.setProgress(50);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginOn();
                    }
                });
                break;
        }
    }
}