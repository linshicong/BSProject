package com.linshicong.bsproject.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.bean
 * @Description: 卡片类
 * @date 2016/9/22 18:34
 */
public class Post extends BmobObject {
    private String contentOne;
    private String contentTwo;
    private String contentThree;
    private String toname;
    private BmobFile image;
    private User user;
    private BmobFile card;

    public boolean isPrivateCard() {
        return privateCard;
    }

    public void setPrivateCard(boolean privateCard) {
        this.privateCard = privateCard;
    }

    private boolean privateCard;

    public String getToname() {
        return toname;
    }

    public void setToname(String toname) {
        this.toname = toname;
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

    public BmobFile getCard() {
        return card;
    }

    public void setCard(BmobFile card) {
        this.card = card;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
