package com.linshicong.bsproject.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.facebook.drawee.view.SimpleDraweeView;
import com.linshicong.bsproject.R;

import java.net.URL;
import java.util.List;

import static com.linshicong.bsproject.util.Constant.isAll;
import static com.linshicong.bsproject.util.Constant.isCheck;

/**
 * Created by linshicong on 2017/2/28.
 */

public class PhotoRecycleViewAdapter extends RecyclerView.Adapter<PhotoRecycleViewAdapter.NewsViewHolder> {

    private List<String> photos;
    private Activity context;

    public PhotoRecycleViewAdapter(List<String> photos, Activity context) {
        this.photos = photos;
        this.context = context;
    }

    public PhotoRecycleViewAdapter(Activity context) {
        this.context = context;

    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView photoImg;
        CheckBox checkBox;
        public NewsViewHolder(final View itemView) {
            super(itemView);
            photoImg=(SimpleDraweeView) itemView.findViewById(R.id.photo_img);
            checkBox=(CheckBox)itemView.findViewById(R.id.check_box);
            if (isCheck){
                checkBox.setVisibility(View.VISIBLE);
            }else{
                checkBox.setVisibility(View.GONE);
            }
            if(isAll){
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }

        }
    }

    @Override
    public PhotoRecycleViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.photo_item, viewGroup, false);
        PhotoRecycleViewAdapter.NewsViewHolder nvh = new PhotoRecycleViewAdapter.NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(final PhotoRecycleViewAdapter.NewsViewHolder personViewHolder, final int i) {
        try {
            URL url = new URL(photos.get(i));
            Uri uri = Uri.parse(url.toURI().toString());
            personViewHolder.photoImg.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}

