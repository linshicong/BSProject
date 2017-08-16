package com.linshicong.bsproject.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.FeedCache;
import com.linshicong.bsproject.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linshicong on 2017/3/3.
 */

public class AutoUpdateBing extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        updateFeed();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 12 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent in = new Intent(this, AutoUpdateBing.class);
        PendingIntent pi = PendingIntent.getService(this, 0, in, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateBing.this).edit();
                editor.putString("bing_pic", urlString);
                editor.apply();
            }
        });
    }

    private void updateFeed() {
        if (HttpUtil.isConnected(AutoUpdateBing.this)) {
            DataSupport.deleteAll(FeedCache.class);
            final BmobQuery<User> query = new BmobQuery<User>();
            User bu = BmobUser.getCurrentUser(User.class);
            query.addWhereRelatedTo("feed", new BmobPointer(bu));
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            FeedCache feedCache = new FeedCache();
                            feedCache.setObjectId(list.get(i).getObjectId());
                            feedCache.setName(list.get(i).getName());
                            feedCache.setDesc(list.get(i).getDec());
                            feedCache.setCardNum(list.get(i).getCardnum());
                            feedCache.setUserImg(list.get(i).getImage().getFileUrl());
                            feedCache.save();
                        }
                    }
                }
            });
        }
    }
}
