package com.linshicong.bsproject.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.fragment.LoginFragment;
import com.linshicong.bsproject.util.BmobUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.linshicong.bsproject.util.Constant.explore;
import static com.linshicong.bsproject.util.Constant.feed;
import static com.linshicong.bsproject.util.Constant.skill;
import static com.linshicong.bsproject.util.Constant.timeline;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.activity
 * @Description: 用户主页面逻辑处理
 * @date 2016/9/10 22:32
 */
public class MainActivity extends AppCompatActivity {
    private long first_time;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        /**初始化控件*/
        initView();
        /**
         * 判断之前是否登录过，如果登录过，则直接进入主页
         * 否则进入登录注册界面
         */
        if (BmobUtil.isLogin()) {
            /**
             * 设置四个变量为true,表示是刚开启应用或刚清除过缓存，所有图片从新加载
             * 有Dialog弹出，以免空页面造成不好的用户体验
             */
            timeline = true;
            explore = true;
            feed = true;
            skill = true;

            /**
             * 获得用户的设备号码，并设置到user表中，并开启监听表数据
             * 如果该用户的设备号发生改变，即表示有该账号在其他地方登陆
             */
            String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
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
            /**用户已经登陆过，则直接进入主页面*/
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(new Intent(MainActivity.this, IndexActivity.class));
        } else {
            /**用户还没登陆过，则进入登陆注册界面*/
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_content, new LoginFragment(), LoginFragment.class.getSimpleName())
                    .commit();
        }
    }

    /**
     * 初始化控件
     */
    public void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.frame_content);
    }

    /**
     * 返回点击事情处理
     * 如果当前是在LoginFragment，则双击退出应用
     * 如果不在LoginFragment，返回跳转到LoginFragment
     */
    @Override
    public void onBackPressed() {
        long second_time = System.currentTimeMillis();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (fm.findFragmentByTag(LoginFragment.class.getSimpleName()) != null) {
            if (second_time - first_time > 2000) {
                Snackbar sb = Snackbar.make(frameLayout, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.show();
                first_time = second_time;
            } else finish();
        } else {
            fragmentTransaction.replace(R.id.frame_content, new LoginFragment(), LoginFragment.class.getSimpleName())
                    .commit();
        }
    }
}
