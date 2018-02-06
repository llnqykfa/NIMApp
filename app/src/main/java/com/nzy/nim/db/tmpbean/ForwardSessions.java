package com.nzy.nim.db.tmpbean;

//转发的会话
public class ForwardSessions {
	private String forwardToId;
	private String name;
	private String headUri;
	private String nameMask;
	private boolean isGroup;

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public String getNameMask() {
		return nameMask;
	}

	public void setNameMask(String nameMask) {
		this.nameMask = nameMask;
	}

	public String getForwardToId() {
		return forwardToId;
	}

	public void setForwardToId(String forwardToId) {
		this.forwardToId = forwardToId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadUri() {
		return headUri;
	}

	public void setHeadUri(String headUri) {
		this.headUri = headUri;
	}

}
