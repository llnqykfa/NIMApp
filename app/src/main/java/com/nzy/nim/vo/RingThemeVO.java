package com.nzy.nim.vo;

import java.io.Serializable;


@SuppressWarnings("serial")
public class RingThemeVO implements Serializable {

	private String pk_ringtheme;// 发起的主题的主键

	private String introduce;// 圈简介

	private String theme;// 圈主题

	private String initator;// 圈发起者昵称

	private String initatorId;// 圈发起者id

	private String dateTime;// 发起时间

	private Integer totalNum;// 总人数

	private String photoPath;// 圈主题文件目录路径

	private String photoNames;// 圈主题上传文件拼接字符串(以“_”拼接)
	
	private String firstRingPath;//圈第一张照片路径
	
	private String userPath;//圈主头像路径

	private boolean isOpenComment;//是否打开圈评论
	
	private Integer totalComment;//圈评论总数
	
	private Boolean isPraise;//用户是否已点赞
	
	private Integer praiseNum;//点赞数




	public void setIsOpenComment(boolean isOpenComment) {
		this.isOpenComment = isOpenComment;
	}

	public boolean isOpenComment() {
		return isOpenComment;
	}

	public void setOpenComment(boolean isOpenComment) {
		this.isOpenComment = isOpenComment;
	}

	public Integer getTotalComment() {
		return totalComment;
	}

	public void setTotalComment(Integer totalComment) {
		this.totalComment = totalComment;
	}

	@Override
	public String toString() {
		return "RingThemeVO{" +
				"pk_ringtheme='" + pk_ringtheme + '\'' +
				", introduce='" + introduce + '\'' +
				", theme='" + theme + '\'' +
				", initator='" + initator + '\'' +
				", initatorId='" + initatorId + '\'' +
				", dateTime='" + dateTime + '\'' +
				", totalNum=" + totalNum +
				", photoPath='" + photoPath + '\'' +
				", photoNames='" + photoNames + '\'' +
				", firstRingPath='" + firstRingPath + '\'' +
				", userPath='" + userPath + '\'' +
				", isOpenComment=" + isOpenComment +
				", totalComment=" + totalComment +
				", isPraise=" + isPraise +
				", praiseNum=" + praiseNum +
				'}';
	}

	public String getPk_ringtheme() {
		return pk_ringtheme;
	}

	public void setPk_ringtheme(String pk_ringtheme) {
		this.pk_ringtheme = pk_ringtheme;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
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

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getPhotoNames() {
		return photoNames;
	}

	public void setPhotoNames(String photoNames) {
		this.photoNames = photoNames;
	}

	public String getFirstRingPath() {
		return firstRingPath;
	}

	public void setFirstRingPath(String firstRingPath) {
		this.firstRingPath = firstRingPath;
	}

	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Boolean getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(Boolean isPraise) {
		this.isPraise = isPraise;
	}

	public Integer getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(Integer praiseNum) {
		this.praiseNum = praiseNum;
	}
	
}
