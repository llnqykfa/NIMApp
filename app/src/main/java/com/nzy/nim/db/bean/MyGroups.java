package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class MyGroups extends DataSupport implements Serializable {

	private String groupId;// 发起的主题的主键
	private String userId;// 所属用户的id
	private String groupImg;// 圈的图标
	private String groupName;// 圈目标
	private Date joinTime;// 加入时间

	public Date getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public String getGroupImg() {
		return groupImg;
	}

	public void setGroupImg(String groupImg) {
		this.groupImg = groupImg;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
