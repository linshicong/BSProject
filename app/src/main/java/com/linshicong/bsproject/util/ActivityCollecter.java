package com.linshicong.bsproject.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.interfaces.anim
 * @Description: 活动管理类
 * @date 2016/9/26 20:48
 */
public class ActivityCollecter {
    private static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
