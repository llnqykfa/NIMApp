package com.nzy.nim.http.listener;

import com.nzy.nim.vo.PersonVO;

import java.util.List;

public interface HttpDatasListener {
	/**
	 * @Author LIUBO
	 * @TODO TODO 从服务器上拉取我的全部好友
	 * @param list
	 * @Date 2015-1-27
	 * @Return void
	 */

	void pullAllFriends(List<PersonVO> list);
}
