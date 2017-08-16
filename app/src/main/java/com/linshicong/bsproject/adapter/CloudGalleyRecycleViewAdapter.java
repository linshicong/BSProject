package com.linshicong.bsproject.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linshicong.bsproject.R;
import com.linshicong.bsproject.bean.PhotoTable;
import com.linshicong.bsproject.db.GalleyCache;

import java.util.List;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.adapter
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date 2017/2/27 20:04
 */
public class CloudGalleyRecycleViewAdapter extends RecyclerView.Adapter<CloudGalleyRecycleViewAdapter.NewsViewHolder> {

    private List<PhotoTable> photoTables;
    private Activity context;
    private List<GalleyCache> galleyCaches;
    private boolean flag;

    public List<GalleyCache> getGalleyCaches() {
        return galleyCaches;
    }

    public void setGalleyCaches(List<GalleyCache> galleyCaches) {
        this.galleyCaches = galleyCaches;
    }

    public CloudGalleyRecycleViewAdapter(List<PhotoTable> photoTables, Activity context) {
        this.photoTables = photoTables;
        this.context = context;
    }

    public CloudGalleyRecycleViewAdapter(Activity context, boolean flag) {
        this.context = context;
        this.flag = flag;
    }

    public List<PhotoTable> getPhotoTables() {
        return photoTables;
    }

    public void setPhotoTables(List<PhotoTable> photoTables) {
        this.photoTables = photoTables;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView galleyItemText;
        TextView galleyNumText;


        public NewsViewHolder(final View itemView) {
            super(itemView);
            galleyItemText = (TextView) itemView.findViewById(R.id.galley_item_text);
            galleyNumText = (TextView) itemView.findViewById(R.id.galley_num_text);
        }
    }

    @Override
    public CloudGalleyRecycleViewAdapter.NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.galley_item, viewGroup, false);
        NewsViewHolder nvh = new NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(final CloudGalleyRecycleViewAdapter.NewsViewHolder personViewHolder, final int i) {
        if (flag) {
            personViewHolder.galleyItemText.setText(photoTables.get(i).getName());
            personViewHolder.galleyNumText.setText(photoTables.get(i).getNum() + "张");
        } else {
            personViewHolder.galleyItemText.setText(galleyCaches.get(i).getName());
            personViewHolder.galleyNumText.setText(galleyCaches.get(i).getNum() + "张");
        }
    }

    @Override
    public int getItemCount() {
        if (flag) {
            return photoTables.size();
        } else {
            return galleyCaches.size();
        }
    }
}

