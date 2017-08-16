package com.linshicong.bsproject.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.activity.display.OtherUserActivity;
import com.linshicong.bsproject.fragment.ExploreFragment;
import com.linshicong.bsproject.fragment.FeedFragment;
import com.linshicong.bsproject.fragment.SettingFragment;
import com.linshicong.bsproject.fragment.SkillFragment;
import com.linshicong.bsproject.fragment.TimelineFragment;
import com.linshicong.bsproject.service.AutoUpdateBing;
import com.linshicong.bsproject.util.ActivityCollecter;
import com.linshicong.bsproject.util.BmobUtil;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ValueEventListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
public class IndexActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.frame_index)
    FrameLayout frameIndex;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private long first_time;
    BroadcastReceiver broadcastReceiver;
    private ImageView bingImg;
    private LocalBroadcastManager local,localBroadcastManager;
    private  BmobRealTimeData bmobRealTimeData=new BmobRealTimeData();
    /**
     * 通过三个flag标志位来标识用户目前处于哪个Fragment,根据不同的Fragment来显示不同的Toolbar Menu
     */
    private boolean explore_flag = false;
    private boolean timeline_flag = true;
    private boolean skill_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_activity);
        ButterKnife.bind(this);
        initView();
        listenerData();
        initBroadcast();
        /**
         * 侧滑抽屉选择事件
         * 1.个人页面
         * 2.广场页面
         * 3.技巧页面
         * 4.关注页面
         * 5.设置页面
         * 6.注销用户
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_timeline:
                        explore_flag = false;
                        timeline_flag = true;
                        skill_flag = false;
                        ft.replace(R.id.frame_index, new TimelineFragment(), TimelineFragment.class.getSimpleName()).commit();
                        toolbar.setTitle("個人");
                        item.setChecked(true);
                        break;
                    case R.id.nav_explore:
                        explore_flag = true;
                        timeline_flag = false;
                        skill_flag = false;
                        ft.replace(R.id.frame_index, new ExploreFragment(), ExploreFragment.class.getSimpleName()).commit();
                        toolbar.setTitle("廣場");
                        item.setChecked(true);
                        break;
                    case R.id.nav_feed:
                        explore_flag = false;
                        timeline_flag = true;
                        skill_flag = false;
                        ft.replace(R.id.frame_index, new FeedFragment(), FeedFragment.class.getSimpleName()).commit();
                        toolbar.setTitle("關注");
                        item.setChecked(true);
                        break;
                    case R.id.nav_setting:
                        explore_flag = false;
                        timeline_flag = false;
                        skill_flag = false;
                        ft.replace(R.id.frame_index, new SettingFragment(), SettingFragment.class.getSimpleName()).commit();
                        toolbar.setTitle("設置");
                        item.setChecked(true);
                        break;
                    case R.id.nav_skill:
                        explore_flag = false;
                        timeline_flag = false;
                        skill_flag = true;
                        ft.replace(R.id.frame_index, new SkillFragment(), SkillFragment.class.getSimpleName()).commit();
                        toolbar.setTitle("技巧");
                        item.setChecked(true);
                        break;
                    case R.id.nav_logout:
                        BmobUtil.Exit(IndexActivity.this);
                        break;
                }
                invalidateOptionsMenu();
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Log.i("Index", "cache");
            try {
                URL url = new URL(bingPic);
                Uri uri = Uri.parse(url.toURI().toString());
                bingImg.setImageURI(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Index", "network");
            loadBingPic();
        }
        super.onResume();
    }

    /**
     * 初始化本地广播接收器：用于被抢登时关闭所有Activity的广播
     */
    private void initBroadcast() {
        local = LocalBroadcastManager.getInstance(this);
        IntentFilter intent = new IntentFilter();
        intent.addAction("com.linshicong.finish");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
                ActivityCollecter.finishAll();
            }
        };
        local.registerReceiver(broadcastReceiver, intent);
    }


    /**
     * 预加载数据，第一次进入IndexActivity就直接进入个人页面
     */
    public void initView() {
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_index);
        bingImg = (ImageView) headerLayout.findViewById(R.id.bing_img);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        navigationView.setItemIconTintList(null);
        Intent intent = new Intent(this, AutoUpdateBing.class);
        startService(intent);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_index, new TimelineFragment(), TimelineFragment.class.getSimpleName())
                .commit();
        if (getSupportActionBar() == null) {
            return;
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle("個人");
        navigationView.setCheckedItem(R.id.nav_timeline);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }


    /**
     * 加载必应图片
     */
    private void loadBingPic() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://guolin.tech/api/bing_pic").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String urlString = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(IndexActivity.this).edit();
                editor.putString("bing_pic", urlString);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(urlString);
                            Uri uri = Uri.parse(url.toURI().toString());
                            bingImg.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 返回事件处理，如果侧滑栏没打开，则打开侧滑栏
     * 如果已经打开侧滑栏，则双击退出应用
     */
    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.openDrawer(Gravity.LEFT);
        } else {
            long second_time = System.currentTimeMillis();
            if (second_time - first_time > 2000) {
                Snackbar sb = Snackbar.make(frameIndex, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.show();
                first_time = second_time;
            } else {
                finish();
            }
        }
    }

    /**
     * 加载Toolbar Menu菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    /**
     * 不同的页面的菜单不同，通过两个flag来进行标识
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (BmobUtil.GetData(this)) {
            menu.findItem(R.id.menu_scan).setIcon(R.mipmap.ic_scan1);
        } else {
            menu.findItem(R.id.menu_scan).setIcon(R.mipmap.ic_scan2);
        }
        /**如果当前是exploreFragment，则缩略图的图案可以看，否则不可以查看*/
        if (explore_flag) {
            menu.findItem(R.id.menu_scan).setVisible(true);
        } else {
            menu.findItem(R.id.menu_scan).setVisible(false);
        }
        /**如果当前是timelineFragment，则缩略图的图案可以看，否则不可以查看*/
        if (timeline_flag) {
            menu.findItem(R.id.scan_erweima).setVisible(true);
            menu.findItem(R.id.menu_search).setVisible(true);
        } else {
            menu.findItem(R.id.scan_erweima).setVisible(false);
            menu.findItem(R.id.menu_search).setVisible(false);
        }
        if (skill_flag) {
            menu.findItem(R.id.menu_msg).setVisible(true);
        } else {
            menu.findItem(R.id.menu_msg).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * 更换图片查看方式，详情图或缩略图的切换
         * 发送本地广播，通知timelineFragment和exploreFragment刷新布局
         */
        switch (item.getItemId()) {
            case R.id.menu_scan:
                if (BmobUtil.GetData(this)) {
                    BmobUtil.SaveData(this, false);
                    item.setIcon(R.mipmap.ic_scan2);
                } else {
                    BmobUtil.SaveData(this, true);
                    item.setIcon(R.mipmap.ic_scan1);
                }
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                Intent intent = new Intent("com.linshicong.MY");
                localBroadcastManager.sendBroadcast(intent);
                break;
            case R.id.scan_erweima:
                /**进入二维码扫描界面*/
                startActivityForResult(new Intent(IndexActivity.this, CaptureActivity.class), 1);
                break;
            case R.id.menu_search:
                /**用户查找用户界面*/
                startActivity(new Intent(IndexActivity.this, SearchActivity.class));
                break;
            case R.id.menu_msg:
                startActivity(new Intent(IndexActivity.this, SendSkillActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 二维码扫描后获得objid,然后通过此id值跳转到对应user的主界面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String str = bundle.getString("result");
            Intent intent = new Intent(IndexActivity.this, OtherUserActivity.class);
            intent.putExtra("objid", str);
            startActivity(intent);
        }
    }

    /**
     * 监听当前用户的用户表数据更新状态
     * 如果当前用户更新的是设备号，则表示有人在其他地方登陆账号
     * 则弹出对话框提示用户，并退出登录回到登录注册界面
     */
    private void listenerData() {
        bmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                if (bmobRealTimeData.isConnected()) {
                    bmobRealTimeData.subRowUpdate("_User", BmobUtil.GetCurrentUser().getObjectId());
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
                try {
                    String id = jsonObject.getJSONObject("data").getString("deviceid");
                    String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
                    if (!id.equals(ANDROID_ID)) {
                        ActivityCollecter.finishAll();
                        AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this, R.style.AlertDialogCustom);
                        builder.setTitle("警告：");
                        builder.setMessage("您的账号在别处登录，请重新登录");
                        builder.setCancelable(false);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BmobUser.logOut();
                                timeline = true;
                                explore = true;
                                feed = true;
                                skill = true;
                                startActivity(new Intent(IndexActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                        builder.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
