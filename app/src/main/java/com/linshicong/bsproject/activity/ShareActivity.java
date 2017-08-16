package com.linshicong.bsproject.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.index.activity
 * @Description: 分享界面逻辑处理
 * @date 2016/9/28 14:53
 */
public class ShareActivity {
    /**
     * 首先下载要分享的图片，下载完成后加载分享界面
     */
    public static void shareSingleImage(Context context) {
        String imagePath = "sdcard/Feilin/download.png";
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
