package com.xinlan.imageeditlibrary.editimage.utils;

import android.text.TextUtils;

import java.io.File;

public class FileUtil {
    public static boolean checkFileExist(final String path){
        if(TextUtils.isEmpty(path))
            return false;

        File file = new File(path);
        return file.exists();
    }
}//end class
