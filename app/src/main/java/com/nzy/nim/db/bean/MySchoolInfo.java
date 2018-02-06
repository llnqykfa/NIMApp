package com.nzy.nim.db.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/10/27.
 */
public class MySchoolInfo implements Serializable{

    private String commentCount;
    private String createTime;
    private String diggState;
    private String diggCount;

    private String content;
    private String condition;
    private String orderBy;
    private String pk_feed;
    private String pk_person;
    private String photoPath;
    private String sex;
    private String status;
    private String photoName;
    private String username;
    private String userHead;

    private String voicePath;
    private List<String> imageList;//我的大学图片

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }



    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getPk_feed() {
        return pk_feed;
    }
    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
    public void setPk_feed(String pk_feed) {
        this.pk_feed = pk_feed;
    }


    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getPk_person() {
        return pk_person;
    }

    public void setPk_person(String pk_person) {
        this.pk_person = pk_person;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDiggState() {
        return diggState;
    }

    public void setDiggState(String diggState) {
        this.diggState = diggState;
    }

    public String getDiggCount() {
        return diggCount;
    }

    public void setDiggCount(String diggCount) {
        this.diggCount = diggCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
