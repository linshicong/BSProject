package com.linshicong.bsproject.db;

import org.litepal.crud.DataSupport;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.db
 * @Description: 卡片缓存类
 * @date 2017/2/27 20:04
 */

public class PostCache extends DataSupport{
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    private String postId;
    private String name;
    private String userImg;
    private String cardUrl;
    private String detailUrl;
    private String contentOne;
    private String contentTwo;
    private String contentThree;
    private String updateTime;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardUrl() {
        return cardUrl;
    }

    public void setCardUrl(String cardUrl) {
        this.cardUrl = cardUrl;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getContentOne() {
        return contentOne;
    }

    public void setContentOne(String contentOne) {
        this.contentOne = contentOne;
    }

    public String getContentTwo() {
        return contentTwo;
    }

    public void setContentTwo(String contentTwo) {
        this.contentTwo = contentTwo;
    }

    public String getContentThree() {
        return contentThree;
    }

    public void setContentThree(String contentThree) {
        this.contentThree = contentThree;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
