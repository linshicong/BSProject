package com.linshicong.bsproject.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.bean
 * @Description: 用户反馈类
 * @date 2016/10/24 10:46
 */
public class FeedBack extends BmobObject{
    private String userid;
    private String phonenumber;
    private String name;
    private String content;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
