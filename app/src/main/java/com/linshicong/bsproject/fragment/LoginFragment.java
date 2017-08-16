package com.linshicong.bsproject.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.linshicong.bsproject.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.fragment
 * @Description: 登录注册界面逻辑处理
 * @date 2016/9/9 16:43
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LoginFragment.class.getSimpleName();
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.register_btn)
    Button registerBtn;
    @Bind(R.id.wx_btn)
    ImageView wxBtn;
    @Bind(R.id.fb_btn)
    ImageView fbBtn;
    @Bind(R.id.wb_btn)
    ImageView wbBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.login_btn, R.id.register_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                /**点击登陆，进入登陆界面*/
                getFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frame_content, new VerifyLoginFragment(), VerifyLoginFragment.class.getSimpleName())
                        .commit();
                break;
            case R.id.register_btn:
                /**点击注册，进入注册界面*/
                getFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frame_content, new RegisterFragment(), RegisterFragment.class.getSimpleName()).commit();
                break;
        }
    }
}
