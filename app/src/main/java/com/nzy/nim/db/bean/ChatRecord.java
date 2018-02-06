/**
 * 
 */
package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ChatRecord extends DataSupport implements Serializable {
	private String chatUserId;// 聊天对象的Id
	private String msgId;// 消息的唯一id
	private String content;// 消息内容
	private String localFilePath;// 本地文件路径
	private String remoteFilePath;// 远程文件路径
	private Date creatTime;// 创建时间
	private boolean isCom;// 是否接收消息
	private String groupId;// 圈Id
	private String userId;// 所属用户
	private int msgType;// 聊天消息类型
	// 消息的状态 0为正在发送 ，1为发送成功，2为发送失败(-1,为无状态的消息),-3，文件未下载，3文件已下载
	private int msgState;
	private String fileName;// 一般文件的名字
	private String fileSize;// 文件的大小
	private boolean isGroup;// 是否是群聊消息

	public ChatRecord(String chatUserId, String msgId, String content, String localFilePath, String remoteFilePath, Date creatTime, boolean isCom, String groupId, String userId, int msgType, int msgState, String fileName, String fileSize, boolean isGroup) {
		this.chatUserId = chatUserId;
		this.msgId = msgId;
		this.content = content;
		this.localFilePath = localFilePath;
		this.remoteFilePath = remoteFilePath;
		this.creatTime = creatTime;
		this.isCom = isCom;
		this.groupId = groupId;
		this.userId = userId;
		this.msgType = msgType;
		this.msgState = msgState;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.isGroup = isGroup;
	}

	public ChatRecord() {
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getMsgState() {
		return msgState;
	}

	public void setMsgState(int msgState) {
		this.msgState = msgState;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getChatUserId() {
		return chatUserId;
	}

	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isCom() {
		return isCom;
	}

	public void setCom(boolean isCom) {
		this.isCom = isCom;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

}
