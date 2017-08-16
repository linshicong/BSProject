package com.linshicong.bsproject.util;

import android.os.Environment;

import java.io.File;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.interfaces.anim
 * @Description: 文件工具类
 * @date 2016/9/26 20:48
 */
public class FileUtil {
    public static final String FOLDER_NAME = "Feilin";
    /**
     * 获取存贮文件的文件夹路径
     *
     * @return
     */
    public static File createFolders() {
        File baseDir;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            baseDir = Environment.getExternalStorageDirectory();
        } else {
            baseDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        if (baseDir == null)
            return Environment.getExternalStorageDirectory();
        File aviaryFolder = new File(baseDir, FOLDER_NAME);
        if (aviaryFolder.exists())
            return aviaryFolder;
        if (aviaryFolder.isFile())
            aviaryFolder.delete();
        if (aviaryFolder.mkdirs())
            return aviaryFolder;
        return Environment.getExternalStorageDirectory();
    }

    public static File genEditFile() {
        return FileUtil.getEmptyFile("tietu"
                + System.currentTimeMillis() + ".jpg");
    }

    public static File getEmptyFile(String name) {
        File folder = FileUtil.createFolders();
        if (folder != null) {
            if (folder.exists()) {
                File file = new File(folder, name);
                return file;
            }
        }
        return null;
    }
}
