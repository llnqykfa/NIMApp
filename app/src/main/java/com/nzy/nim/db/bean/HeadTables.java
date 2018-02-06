package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

public class HeadTables extends DataSupport {
	private String userId;
	private String headUrl;
	private String name;
	private Date timeStamp;// 保存的时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

}
