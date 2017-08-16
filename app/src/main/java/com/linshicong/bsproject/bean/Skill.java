package com.linshicong.bsproject.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.bean
 * @Description: 技巧类
 * @date 2016/10/24 10:46
 */
public class Skill extends BmobObject {
    private String title;
    private BmobFile img;
    private BmobFile html;

    public BmobFile getHtml() {
        return html;
    }

    public void setHtml(BmobFile html) {
        this.html = html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BmobFile getImg() {
        return img;
    }

    public void setImg(BmobFile img) {
        this.img = img;
    }
}
