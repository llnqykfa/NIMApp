package com.nzy.nim.db;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/14.
 */
public class BookReviewReplyList implements Serializable {
    private String id;//回复ID
    private String personName;//回复人昵称
    private String personId;//回复人ID
    private String content;//评论内容
    private Long createTime;//评论时间
    private Boolean isParent;//是否为父回复
    private String parentPersonId;//父回复ID
    private String parentPersonName;//父回复回复人昵称
    private String personPhotoPath;

    public String getPersonPhotoPath() {
        return personPhotoPath;
    }

    public void setPersonPhotoPath(String personPhotoPath) {
        this.personPhotoPath = personPhotoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    public String getParentPersonId() {
        return parentPersonId;
    }

    public void setParentPersonId(String parentPersonId) {
        this.parentPersonId = parentPersonId;
    }

    public String getParentPersonName() {
        return parentPersonName;
    }

    public void setParentPersonName(String parentPersonName) {
        this.parentPersonName = parentPersonName;
    }
}
