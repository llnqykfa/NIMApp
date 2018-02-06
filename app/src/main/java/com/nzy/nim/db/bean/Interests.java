package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

public class Interests extends DataSupport {
	private String interestId;// 兴趣的Id
	private int icon;// 图标
	private String name;// 名称
	private String iconUrl;// 图标路径

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getInterestId() {
		return interestId;
	}

	public void setInterestId(String interestId) {
		this.interestId = interestId;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
