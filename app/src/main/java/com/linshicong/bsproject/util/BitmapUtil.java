package com.linshicong.bsproject.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.xinlan.imageeditlibrary.picchooser.SelectPictureActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.linshicong.bsproject.util.Constant.SELECT_GALLERY_IMAGE_CODE;
import static com.linshicong.bsproject.util.Constant.TAKE_PHOTO_CODE;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.interfaces.anim
 * @Description: 图片帮助类
 * @date 2016/9/26 20:48
 */

public class BitmapUtil {
    public static String[] names = new String[]{"本地相冊", "相機拍照"};
    public static Uri photoURI = null;

    public static Bitmap decode(final int width, Bitmap bitmap) {
        /**
         * 获得手机屏幕的屏幕宽度，把获得的照片解压成适配屏幕宽度
         */
        float scaleWidth = ((float) width) / bitmap.getWidth();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bitmap;
    }

    public static void saveImage(Bitmap bmp, String name) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Feilin");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDialogCustom(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photoURI = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity);
                builder.setTitle("選擇照片：");
                builder.setItems(names,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (0 == which) {
                                    /**从相册中选择图片，跳转到选择图片界面*/
                                    activity.startActivityForResult(new Intent(
                                                    activity, SelectPictureActivity.class),
                                            SELECT_GALLERY_IMAGE_CODE);
                                } else if (1 == which) {
                                    /**通过拍照获得图片*/
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                                        File photoFile = FileUtil.genEditFile();
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        if (Build.VERSION.SDK_INT > 22) {
                                            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
                                            }
                                        }
                                        if (photoFile != null) {
                                           /* if (Build.VERSION.SDK_INT >= 24) {
                                                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                photoURI = FileProvider.getUriForFile(activity, "com.linshicong.bsproject.fileprovider", photoFile);
                                            } else {
                                                photoURI = Uri.fromFile(photoFile);
                                            }*/
                                            photoURI = Uri.fromFile(photoFile);
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                            activity.startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE);
                                        }
                                    }
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }
}
