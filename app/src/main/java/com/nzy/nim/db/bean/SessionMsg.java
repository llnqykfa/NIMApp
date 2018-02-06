package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author 张全艺
 * @DATE 2015-1-29上午11:00:17
 * @TODO
 */
public class SessionMsg extends DataSupport implements Serializable {

	private static final long serialVersionUID = 4530529647164194453L;
	private String imgUrl;// 发送者头像
	private String newContent;// 消息内容
	private Date creatTime;// 创建时间
	private int unLookedMsgCount = 0;// 消息数量
	private int mainType;// 大类消息类型
	private int subType;// 具体的类型
	private String sendUserId;// 发送者的id
	private String userId;// 接收消息的用户Id
	private String titles;// 消息的标题
	private boolean isGroup;// 是否是群消息

	public SessionMsg() {
	}

	public SessionMsg(String imgUrl, String newContent, Date creatTime, int unLookedMsgCount, int mainType, int subType, String sendUserId, String userId, String titles, boolean isGroup) {
		this.imgUrl = imgUrl;
		this.newContent = newContent;
		this.creatTime = creatTime;
		this.unLookedMsgCount = unLookedMsgCount;
		this.mainType = mainType;
		this.subType = subType;
		this.sendUserId = sendUserId;
		this.userId = userId;
		this.titles = titles;
		this.isGroup = isGroup;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public int getMainType() {
		return mainType;
	}

	public void setMainType(int mainType) {
		this.mainType = mainType;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	public int getUnLookedMsgCount() {
		return unLookedMsgCount;
	}

	public void setUnLookedMsgCount(int unLookedMsgCount) {
		this.unLookedMsgCount = unLookedMsgCount;
	}

	public String getTitles() {
		return titles;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getNewContent() {
		return newContent;
	}

	public void setNewContent(String newContent) {
		this.newContent = newContent;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
