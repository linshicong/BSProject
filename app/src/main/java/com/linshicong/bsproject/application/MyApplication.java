package com.linshicong.bsproject.application;

import android.app.Application;
import android.os.Environment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.linshicong.bsproject.util.BmobUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.litepal.LitePal;

import java.io.File;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject
 * @Description: 初始化ImageLoader
 * @date 2016/9/19 18:17
 */
public class MyApplication extends Application {
    private static MyApplication myApplication = null;

    public static MyApplication getInstance() {
        return myApplication;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*//自动检测内存溢出
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        myApplication = this;
        LitePal.initialize(this);
        Fresco.initialize(this);
        initImageLoader();
        BmobUtil.initBmob(this);
    }


    public void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCache(new UnlimitedDiskCache(new File(Environment.getExternalStorageDirectory() + "Feilin")))
                .memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(config);
    }

}
