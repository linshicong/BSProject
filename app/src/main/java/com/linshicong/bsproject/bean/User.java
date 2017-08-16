package com.linshicong.bsproject.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.bean
 * @Description: 用户类
 * @date 2016/9/10 22:21
 */
public class User extends BmobUser {
    private String name;
    private String dec;
    private BmobFile image;
    private Integer cardnum;
    private BmobRelation feed;
    private boolean pricard;
    private boolean autosave;
    private String deviceid;

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public boolean isAutosave() {
        return autosave;
    }

    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
    }

    public BmobRelation getFeed() {
        return feed;
    }

    public void setFeed(BmobRelation feed) {
        this.feed = feed;
    }

    public int getCardnum() {
        return cardnum;
    }

    public void setCardnum(Integer cardnum) {
        this.cardnum = cardnum;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    private static final long serialVersionUID = 1L;

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPricard() {
        return pricard;
    }

    public void setPricard(boolean pricard) {
        this.pricard = pricard;
    }
}
