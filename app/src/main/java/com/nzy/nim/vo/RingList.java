package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/26.
 */
public class RingList extends DataSupport implements Serializable {
    private String image;//组圈图片
    private Integer commentNumber;//留言数量
    private String introduce;//组圈详情
    private String initatorName;//创建者名称
    private String categoryName;//分类名
    private Boolean isDigg;//是否点赞
    private String initatorId;//创建者id
    private Boolean isOpenComment;//是否开放留言
    private Integer diggNumber;//点赞数量
    private Integer dynamicNumber;//动态数量
    private String createTime;//组圈创建时间
//    private List<String > tagsList;//标签列表
    private String theme;//组圈主题
    private String ringId;//组圈id
    private Boolean isInclude;
    private String categoryId;//类别id
    private Integer tabType;//0为默认；1为新组圈；2为热门组圈；3为置顶组圈

    public Integer getTabType() {
        return tabType;
    }

    public void setTabType(Integer tabType) {
        this.tabType = tabType;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsInclude() {
        return isInclude;
    }

    public Integer getDynamicNumber() {
        return dynamicNumber;
    }

    public void setDynamicNumber(Integer dynamicNumber) {
        this.dynamicNumber = dynamicNumber;
    }

    public void setIsInclude(Boolean isInclude) {
        this.isInclude = isInclude;
    }

    public Boolean getIsOpenComment() {
        return isOpenComment;
    }

    public void setIsOpenComment(Boolean isOpenComment) {
        this.isOpenComment = isOpenComment;
    }

    public Boolean getIsDigg() {
        return isDigg;
    }

    public void setIsDigg(Boolean isDigg) {
        this.isDigg = isDigg;
    }

    public String getRingId() {
        return ringId;
    }

    public void setRingId(String ringId) {
        this.ringId = ringId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(Integer commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getInitatorName() {
        return initatorName;
    }

    public void setInitatorName(String initatorName) {
        this.initatorName = initatorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getInitatorId() {
        return initatorId;
    }

    public void setInitatorId(String initatorId) {
        this.initatorId = initatorId;
    }

    public Integer getDiggNumber() {
        return diggNumber;
    }

    public void setDiggNumber(Integer diggNumber) {
        this.diggNumber = diggNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

//    public List<String> getTagsList() {
//        return tagsList;
//    }
//
//    public void setTagsList(List<String> tagsList) {
//        this.tagsList = tagsList;
//    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
