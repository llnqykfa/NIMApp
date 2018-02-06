package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-31 下午4:31:45
 * @classify(类别)
 * @TODO(功能) TODO 添加好友请求的新朋友表
 * @Param(参数)
 * @Remark(备注)
 * 
 */
@SuppressWarnings("serial")
public class Invitations extends DataSupport implements Serializable {
	private String imgUrl;// 发送者头像
	private String sendUserName;// 发送者名称
	private String sendUserId;// 发送者Id
	private String userId;// 消息所属用户Id
	private int status;// 消息状态 : -1,为对方拒绝,0为未同意 ，1为已同意,2为已拒绝
	private String leaveComment;// 留言
	private int msgType;// 消息类型
	private String groupId;// 圈id
	private String msgId;// 消息的唯一id
	private String groupName;// 圈的名字
	private Date receMsgTime;// 收到消息的时间

	public Date getReceMsgTime() {
		return receMsgTime;
	}

	public void setReceMsgTime(Date receMsgTime) {
		this.receMsgTime = receMsgTime;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSendUserName() {
		return sendUserName;
	}

	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLeaveComment() {
		return leaveComment;
	}

	public void setLeaveComment(String leaveComment) {
		this.leaveComment = leaveComment;
	}

}
