package com.vincent.filepicker.adapter;



public interface OnSelectStateListener<T> {
    void OnSelectStateChanged(boolean state, T file);
}
