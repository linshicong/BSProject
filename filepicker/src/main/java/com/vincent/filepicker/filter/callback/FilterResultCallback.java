package com.vincent.filepicker.filter.callback;

import com.vincent.filepicker.filter.entity.BaseFile;
import com.vincent.filepicker.filter.entity.Directory;

import java.util.List;


public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
