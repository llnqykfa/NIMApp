package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class RingComments extends DataSupport implements Serializable {
	private String commentId;// 评论的id
	private String commentatorId;// 评论者的Id
	private String groupId;// 圈id
	private Date commentDate;// 评论时间
	private String name;// 评论的昵称
	private String content;// 评论的内容
	private String picsPath;// 图片的路径拼接
	private String imgUrl;// 用户头像路径

	public String getCommentatorId() {
		return commentatorId;
	}

	public void setCommentatorId(String commentatorId) {
		this.commentatorId = commentatorId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicsPath() {
		return picsPath;
	}

	public void setPicsPath(String picsPath) {
		this.picsPath = picsPath;
	}

}
