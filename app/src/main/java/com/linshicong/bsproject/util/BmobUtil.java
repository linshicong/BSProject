package com.linshicong.bsproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.linshicong.bsproject.activity.MainActivity;
import com.linshicong.bsproject.bean.User;
import com.linshicong.bsproject.db.FeedCache;
import com.linshicong.bsproject.db.GalleyCache;
import com.linshicong.bsproject.db.PostCache;
import com.linshicong.bsproject.db.SkillCache;
import com.linshicong.bsproject.db.UserCache;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import taobe.tec.jcc.JChineseConvertor;

import static com.linshicong.bsproject.util.Constant.explore;
import static com.linshicong.bsproject.util.Constant.feed;
import static com.linshicong.bsproject.util.Constant.isUpdate;
import static com.linshicong.bsproject.util.Constant.skill;
import static com.linshicong.bsproject.util.Constant.timeline;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.Util
 * @Description: Bmob工具类
 * @date 2016/9/24 13:56
 */
public class BmobUtil {
    public static boolean isFeed = false;
    //Bmob申请账号替换APP_KEY
    private static final String APP_KEY = "";

    /**
     * 初始化Bmob
     */
    public static void initBmob(Context context) {
        Bmob.initialize(context, APP_KEY);
    }

    /**
     * 获得当前用户
     */
    public static User GetCurrentUser() {
        return BmobUser.getCurrentUser(User.class);
    }

    /**
     * 判断是否已经登录
     */
    public static boolean isLogin() {
        if (GetCurrentUser() == null) {
            return false;
        } else return true;
    }


    /**
     * 保存查看方式的偏好设置到SharedPrefences中
     *
     * @param context
     * @param flag
     */
    public static void SaveData(Context context, boolean flag) {
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("scan", flag);
        editor.apply();
    }

    /**
     * 获得SharedPrefences中查看方式
     *
     * @param context
     * @return
     */
    public static boolean GetData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean flag = preferences.getBoolean("scan", true);
        return flag;
    }

    /**
     * 把文字改为繁体字
     *
     * @param changeText
     * @return
     */
    public static String change(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor
                    .getInstance();
            changeText = jChineseConvertor.s2t(changeText);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    /**
     * 添加关注
     *
     * @param context
     * @param us
     */
    public static void addToFeed(final Activity context, final User us) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User bu = BmobUser.getCurrentUser(User.class);
                User user = new User();
                user.setObjectId(us.getObjectId());
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                bu.setFeed(relation);
                bu.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(context, "關注成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("feed", e.toString());
                        }
                    }
                });
                FeedCache feedCache = new FeedCache();
                feedCache.setObjectId(us.getObjectId());
                feedCache.setUserImg(us.getImage().getFileUrl());
                feedCache.setCardNum(us.getCardnum());
                feedCache.setDesc(us.getDec());
                feedCache.setName(us.getName());
                feedCache.save();
            }
        });
    }

    /**
     * 取消关注
     *
     * @param context
     * @param us
     */
    public static void removeToFeed(final Activity context, final User us) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User bu = BmobUser.getCurrentUser(User.class);
                User user = new User();
                user.setObjectId(us.getObjectId());
                BmobRelation relation = new BmobRelation();
                relation.remove(user);
                bu.setFeed(relation);
                bu.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(context, "取消關注成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("removeFeed", e.toString());
                        }
                    }
                });
                DataSupport.deleteAll(FeedCache.class, "objectId=?", us.getObjectId());
            }
        });

    }

    /**
     * 检查是否有关注
     *
     * @param context
     * @param userid
     * @return
     */
    public static boolean checkFeed(final Activity context, final String userid) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<User> query = new BmobQuery<User>();
                User bu = BmobUser.getCurrentUser(User.class);
                query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
                query.addWhereRelatedTo("feed", new BmobPointer(bu));
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            for (int i = 0; i < list.size(); i++) {
                                String temp = list.get(i).getObjectId();
                                if (temp.equals(userid)) {
                                    isFeed = true;
                                    break;
                                } else {
                                    isFeed = false;
                                }
                            }
                            if (list.size() == 0) {
                                isFeed = false;
                            }
                        } else {
                            Log.i("check", e.toString());
                        }
                    }
                });
            }
        });
        return isFeed;
    }

    /**
     * 检查是否公开卡片
     *
     * @return
     */
    public static boolean checkPrivate() {
        User user = BmobUtil.GetCurrentUser();
        return user.isPricard();
    }

    /**
     * 设置是否不公开卡片
     *
     * @param flag
     */
    public static void savePrivate(final Activity activity, final boolean flag) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User user = BmobUtil.GetCurrentUser();
                user.setPricard(flag);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            if (flag) {
                                Toast.makeText(activity, "公開照片保存成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "不公開照片保存成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("private", e.toString());
                        }
                    }
                });
            }
        });
    }

    /**
     * 检查是否自动保存卡片到相册
     *
     * @return
     */
    public static boolean checkAutoSave() {
        User user = BmobUtil.GetCurrentUser();
        return user.isAutosave();
    }

    /**
     * 是否自动保存卡片到相册
     *
     * @param flag
     */
    public static void saveAutoSave(final Activity activity, final boolean flag) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User user = BmobUtil.GetCurrentUser();
                user.setAutosave(flag);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            if (flag) {
                                Toast.makeText(activity, "自動保存到相冊成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "取消自動保存到相冊成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i("auto_save", e.toString());
                        }
                    }
                });
            }
        });
    }

    public static String getDistanceTime(String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = df.format(new Date());
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day > 0) {
            return day + "天前";
        } else if (hour > 0) {
            return hour + "小時前";
        } else if (min > 0) {
            return min + "分鐘前";
        } else return sec + "秒前";
    }

    public static void Exit(final Activity activity) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("菲林CAMERA");
        dialog.setMessage("是否註銷該賬號？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BmobUser.logOut();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                timeline = true;
                explore = true;
                feed = true;
                skill = true;
                clearCache(activity);
                activity.finish();
            }
        });
        dialog.setNegativeButton("暫不", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        dialog.show();
    }

    public static void clearCache(Context context) {
        BmobQuery.clearAllCachedResults();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
        DataSupport.deleteAll(UserCache.class);
        DataSupport.deleteAll(PostCache.class);
        DataSupport.deleteAll(SkillCache.class);
        DataSupport.deleteAll(FeedCache.class);
        DataSupport.deleteAll(GalleyCache.class);
        isUpdate = true;
        timeline = true;
        explore = true;
        feed = true;
        skill = true;
    }
}
