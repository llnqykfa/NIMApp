package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

public class MyInterests extends DataSupport {
	private String interestId;// 兴趣Id
	private String userId;// 用户Id

	public String getInterestId() {
		return interestId;
	}

	public void setInterestId(String interestId) {
		this.interestId = interestId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
