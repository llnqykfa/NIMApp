package com.nzy.nim.vo;

import java.io.Serializable;


@SuppressWarnings("serial")
public class HobbyVO implements Serializable {
	
	private String pk_hobby;
	
	private String hobbyName;//爱好名称

	private String path;//对应图片的路径
	
	private String introduce;//简介

	public String getPk_hobby() {
		return pk_hobby;
	}

	public void setPk_hobby(String pk_hobby) {
		this.pk_hobby = pk_hobby;
	}

	public String getHobbyName() {
		return hobbyName;
	}

	public void setHobbyName(String hobbyName) {
		this.hobbyName = hobbyName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	
}
