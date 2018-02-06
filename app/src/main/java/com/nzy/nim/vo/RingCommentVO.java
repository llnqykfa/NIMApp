package com.nzy.nim.vo;

import java.io.Serializable;


@SuppressWarnings("serial")
public class RingCommentVO implements Serializable {

	private String pk_ringComment;//主键

	private String ringId;//圈主题ID
	
	private String content;//评论内容
	
	private String personId;//评论人

	private String addTime;//添加时间
	
	private String photoNames;//上传照片名称，以下划线拼接
	
	private String photoUrl;//评论内容照片根路径

	private String username;//评论用户昵称

	private String photoName;//评论用户照片名称
	
	private String userPath;//评论用户头像路径

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	public String getPk_ringComment() {
		return pk_ringComment;
	}

	public void setPk_ringComment(String pk_ringComment) {
		this.pk_ringComment = pk_ringComment;
	}

	public String getRingId() {
		return ringId;
	}

	public void setRingId(String ringId) {
		this.ringId = ringId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getPhotoNames() {
		return photoNames;
	}

	public void setPhotoNames(String photoNames) {
		this.photoNames = photoNames;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}
	
	

}
