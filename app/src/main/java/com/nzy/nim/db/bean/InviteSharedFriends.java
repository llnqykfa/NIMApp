package com.nzy.nim.db.bean;

import java.io.Serializable;

/**
 * @author LIUBO
 * @date 2015-3-23下午11:01:47
 * @TODO 邀请好友加入组圈的信息实体
 */
public class InviteSharedFriends implements Serializable {
	private String[] receiverId;// 接受者的id
	private ShareMsgInfo shareMsgInfo;

	public InviteSharedFriends(String[] receiverId, ShareMsgInfo shareMsgInfo) {
		this.receiverId = receiverId;
		this.shareMsgInfo = shareMsgInfo;
	}

	public String[] getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String[] receiverId) {
		this.receiverId = receiverId;
	}

	public ShareMsgInfo getShareMsgInfo() {
		return shareMsgInfo;
	}

	public void setShareMsgInfo(ShareMsgInfo shareMsgInfo) {
		this.shareMsgInfo = shareMsgInfo;
	}
}
