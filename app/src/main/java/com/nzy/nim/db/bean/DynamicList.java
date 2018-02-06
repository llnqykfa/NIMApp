package com.nzy.nim.db.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class DynamicList implements Serializable{
    private String personName;//发布者名称
    private String personPhoto;//发布者头像
    private Long createTime;//发布时间
    private String personId;//发布者id
    private String id;//动态的id
    private String content;//动态内容
    private List<String> imageList;//动态图片
    private String page;//页码
    private Boolean isShare;//是否为分享
    private String shareContent;//分享内容

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Boolean getIsShare() {
        return isShare;
    }

    public void setIsShare(Boolean isShare) {
        this.isShare = isShare;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getPageNow() {
        return page;
    }

    public void setPageNow(String pageNow) {
        this.page = pageNow;
    }

    public String getPersonPhoto() {
        return personPhoto;
    }

    public void setPersonPhoto(String personPhoto) {
        this.personPhoto = personPhoto;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
