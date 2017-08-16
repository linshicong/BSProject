package com.linshicong.bsproject.db;

import org.litepal.crud.DataSupport;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.db
 * @Description: 关注用户缓存类
 * @date 2017/3/2 08:20
 */

public class FeedCache extends DataSupport{
    private String objectId;
    private int cardNum;
    private String name;
    private String desc;
    private String userImg;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
