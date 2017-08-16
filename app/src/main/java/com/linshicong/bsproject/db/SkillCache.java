package com.linshicong.bsproject.db;

import org.litepal.crud.DataSupport;

/**
 * @author lin
 * @Title: BSProject
 * @Package com.linshicong.bsproject.db
 * @Description: 技能缓存类
 * @date 2017/3/2 08:20
 */

public class SkillCache extends DataSupport{
    private String title;
    private String skillImg;
    private String skillUrl;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkillImg() {
        return skillImg;
    }

    public void setSkillImg(String skillImg) {
        this.skillImg = skillImg;
    }

    public String getSkillUrl() {
        return skillUrl;
    }

    public void setSkillUrl(String skillUrl) {
        this.skillUrl = skillUrl;
    }
}
