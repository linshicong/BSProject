package com.linshicong.bsproject.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.bean
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date 2017/2/26 13:25
 */
public class PhotoTable extends BmobObject{
    private User user;
    private List<String> photo;
    private String name;
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }
}
