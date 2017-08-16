package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.linshicong.bsproject.R;
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
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 注册手机验证逻辑处理
 * @date 2017/1/6 18:39
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    MyCountTimer timer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.phone_num_text)
    EditText phoneNumText;
    @Bind(R.id.code_text)
    EditText codeText;
    @Bind(R.id.send_code_btn)
    Button sendCodeBtn;
    @Bind(R.id.next_btn)
    CircularProgressButton nextBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, null);
        ButterKnife.bind(this, view);
        initToolbar(view);
        return view;
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar(View v) {
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_black_ui_back_arrow);
        toolbar.setTitle("手機註冊");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        nextBtn.setIndeterminateProgressMode(true);
        nextBtn.setProgress(0);
    }

    /**
     * 请求短信验证
     * 先查询用户表中该用户是否已经注册过了
     * 如果已经注册过了，则弹出对话框提醒用户已经注册过了
     */
    private void requestSMSCode() {
        if (!HttpUtil.isConnected(getActivity())){
            Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡連接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String number = phoneNumText.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
            query.addWhereEqualTo("mobilePhoneNumber", number);
            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    if (e == null) {
                        Log.i(TAG, "查找成功");
                        if (list.isEmpty()) {
                            /**用户还没有注册，请求短信验证码*/
                            timer = new MyCountTimer(60000, 1000, sendCodeBtn);
                            timer.start();
                            BmobSMS.requestSMSCode(number, "模板1", new QueryListener<Integer>() {
                                @Override
                                public void done(Integer integer, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getActivity(), "驗證碼發送成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.i(TAG, e.toString());
                                        timer.cancel();
                                    }
                                }
                            });
                        } else {
                            /**用户已经注册过，弹出对话框提醒用户已经注册过*/
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("菲林CAMERA");
                            dialog.setMessage("您已經註冊過了，是否幫您跳轉到登录界面？");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame_content, new VerifyLoginFragment(), VerifyLoginFragment.class.getSimpleName())
                                            .commit();
                                }
                            });
                            dialog.setNegativeButton("暫不", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().onBackPressed();
                                }
                            });
                            dialog.show();
                        }
                    } else {
                        Log.i(TAG, "查找失敗" + e.toString());
                    }
                }
            });
        } else Toast.makeText(getActivity(), "請輸入手機號碼", Toast.LENGTH_SHORT).show();

    }

    /**
     * 验证短信验证码
     */
    private void oneKeyLogin() {
        if (!HttpUtil.isConnected(getActivity())){
            Toast.makeText(getActivity(), "網絡錯誤，請檢查網絡連接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phonenum = phoneNumText.getText().toString();
        final String phonecode = codeText.getText().toString();
        if (TextUtils.isEmpty(phonenum)) {
            Toast.makeText(getActivity(), "手機號碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phonecode)) {
            Toast.makeText(getActivity(), "驗證碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        }
        nextBtn.setProgress(50);
        BmobSMS.verifySmsCode(phonenum, phonecode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    /**验证成功，跳转到设置用户详情界面*/
                    //progress.dismiss();
                    nextBtn.setProgress(100);
                    SetPasswordFragment sp = new SetPasswordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phonenum);
                    sp.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.frame_content, sp, SetPasswordFragment.class.getSimpleName())
                            .commit();
                } else {
                    /**验证失败，可能是网络错误或者是验证码错误*/
                    //progress.dismiss();
                    nextBtn.setProgress(-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextBtn.setProgress(0);
                        }
                    },2000);
                    Log.i(TAG, e.toString());
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
     *
     * @param view
     */
    @OnClick({R.id.send_code_btn, R.id.next_btn})
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
            case R.id.next_btn:
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

