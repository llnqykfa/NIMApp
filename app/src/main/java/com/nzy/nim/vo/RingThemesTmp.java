package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LIUBO
 * @date 2015-3-29下午8:06:20
 * @TODO 圈展示的最新临时数据
 */
public class RingThemesTmp extends DataSupport implements Serializable {
	private static final long serialVersionUID = 1L;
	private String groupId;// 发起的主题的主键
	private String introduce;// 圈简介
	private String groupImg;// 圈的图标
	private String groupName;// 圈目标
	private String initator;// 圈发起者昵称
	private String initatorId;// 圈发起者id
	private int totalNum;// 总人数
	private Date createTime;// 创建时间
	private String userPhotoPath;// 用户头像路径
	private String picPath;// 组圈图片路径
	private int reviewNum;// 评论总数
	private boolean isPraise;// 是否点赞
	private int praiseNum;// 点赞总数
	private String categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	public int getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(int reviewNum) {
		this.reviewNum = reviewNum;
	}

	public boolean isPraise() {
		return isPraise;
	}

	public void setPraise(boolean isPraise) {
		this.isPraise = isPraise;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getGroupImg() {
		return groupImg;
	}

	public void setGroupImg(String groupImg) {
		this.groupImg = groupImg;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getInitator() {
		return initator;
	}

	public void setInitator(String initator) {
		this.initator = initator;
	}

	public String getInitatorId() {
		return initatorId;
	}

	public void setInitatorId(String initatorId) {
		this.initatorId = initatorId;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserPhotoPath() {
		return userPhotoPath;
	}

	public void setUserPhotoPath(String userPhotoPath) {
		this.userPhotoPath = userPhotoPath;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

}
