package com.nzy.nim.db.tmpbean;

import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.SPUtils;
import com.nzy.nim.vo.QYApplication;

import java.util.Date;

public class SPHelper {
	// 保存当前用户名
	public static void saveUserName(String userName) {
		SPUtils.put(QYApplication.getContext(), "account", userName);
	}

	// 获取用户密码
	public static String getUserPwd() {
		return SPUtils.get(QYApplication.getContext(), "password", "")
				.toString();
	}

	// 保存当前用户密码
	public static void saveUserPwd(String pwd) {
		SPUtils.put(QYApplication.getContext(), "password", pwd);
	}

	// 获取当前用户名
	public static String getUserName() {
		String userName = SPUtils
				.get(QYApplication.getContext(), "account", "").toString();
		return userName;
	}

	// 保存当前用户Id
	public static void saveCurrentUserId(String userId) {
		SPUtils.put(QYApplication.getContext(), "current_userId", userId);
	}
	// 获取当前用户Id
	public static String getCurrentUserId() {
		return (String) SPUtils.get(QYApplication.getContext(),
				"current_userId", "");
	}

	// 保存当前用户AccessToken
	public static void saveCurrentAccessToken(String accessToken) {
		SPUtils.put(QYApplication.getContext(), "current_accessToken", accessToken);
	}

	// 获取当前用户AccessToken
	public static String getCurrentAccessToken() {
		return (String) SPUtils.get(QYApplication.getContext(),
				"current_accessToken", "");
	}

	// 保存退出标志
	public static void saveExitFlag(boolean falg) {
		SPUtils.put(QYApplication.getContext(), "isExit", falg);
	}

	// 获取退出标志
	public static boolean getExitFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "isExit",
				false);
	}

	// 设置强制下线标志
	public static void putForcedOfflineFlag(boolean flag) {
		SPUtils.put(QYApplication.getContext(), "isForcedOffLine", flag);
	}

	// 获取强制下线标志
	public static boolean getForcedOfflineFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(),
				"isForcedOffLine", false);
	}

	// 保存上次更新时间
	public static void setLastUpdateTime(Date date, String key) {
		SPUtils.put(QYApplication.getContext(),
				key + "_" + QYApplication.getPersonId(), date.getTime());
	}

	// 获取上次更新时间
	public static long getLastUpdateTime(String key) {
		return (Long) SPUtils.get(QYApplication.getContext(), key + "_"
				+ QYApplication.getPersonId(), System.currentTimeMillis());
	}

	// 获取密码是否更改标志
	public static boolean getIsModifyPwd(String userCode) {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "pwdIsChange"
				+ userCode, false);
	}

	// 保存密码更改标志
	public static void setIsModifyPwd(boolean isModify, String userCode) {
		SPUtils.put(QYApplication.getContext(), "pwdIsChange" + userCode,
				isModify);
	}

	// 未读评论计数
	public static void setUnReadCommendNum(String userId, String groupId,
										   int num) {
		StringBuffer sb = new StringBuffer();
		sb.append("commend_").append(userId).append("_").append(groupId);
		int count = (Integer) SPUtils.get(QYApplication.getContext(),
				sb.toString(), 0);
		SPUtils.put(QYApplication.getContext(), sb.toString(), count + num);
	}

	// 清空未读评论条数
	public static void clearUnReadCommendNum(String userId, String groupId) {
		StringBuffer sb = new StringBuffer();
		sb.append("commend_").append(userId).append("_").append(groupId);
		SPUtils.put(QYApplication.getContext(), sb.toString(), 0);
	}

	// 获取未读评论的条数
	public static int getUnReadCommendNum(String userId, String groupId) {
		StringBuffer sb = new StringBuffer();
		sb.append("commend_").append(userId).append("_").append(groupId);
		return (Integer) SPUtils.get(QYApplication.getContext(), sb.toString(),
				0);
	}

	/** ####################接收圈消息的配置######################################## */
	// 设置是否接收圈消息标志
	public static void setIsReceRingMsg(String groupId, boolean flag) {
		if (DataUtil.isEmpty(QYApplication.getPersonId())
				|| DataUtil.isEmpty(groupId))
			return;
		SPUtils.put(QYApplication.getContext(),
				"receRingMsg" + QYApplication.getPersonId() + "_" + groupId,
				flag);
	}

	// 获取是否接收圈消息的标志
	public static boolean getReceRingMsg(String groupId) {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "receRingMsg"
				+ QYApplication.getPersonId() + "_" + groupId, true);
	}

	// 设置是否通知新的圈消息到来
	public static void setIsNotifyRingMsg(String groupId, boolean flag) {
		if (DataUtil.isEmpty(QYApplication.getPersonId())
				|| DataUtil.isEmpty(groupId))
			return;
		SPUtils.put(QYApplication.getContext(), "notify_ringMsg"
				+ QYApplication.getPersonId() + "_" + groupId, flag);
	}

	// 获取是否通知圈消息到来标志
	public static boolean getNotifyRingMsg(String groupId) {
		return (Boolean) SPUtils.get(QYApplication.getContext(),
				"notify_ringMsg" + QYApplication.getPersonId() + "_" + groupId,
				true);
	}

	/** ######################所有消息的接收配置################################ */
	// 设置新消息提醒标志
	public static void setNewMsgFlagNotify(String userId, boolean flag) {
		SPUtils.put(QYApplication.getContext(), "newMsg_" + userId, flag);
	}

	// 获取新消息提醒标志
	public static boolean getNewMsgFlagNotify(String userId) {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "newMsg_"
				+ userId, true);
	}

	// 设置新消息到来是否震动
	public static void setNewMsgVibratorFlag(String userId, boolean flag) {
		SPUtils.put(QYApplication.getContext(), "newMsgVibrator_" + userId,
				flag);
	}

	// 获取震动提示标志
	public static boolean getNewMsgVibratorFlag(String userId) {
		return (Boolean) SPUtils.get(QYApplication.getContext(),
				"newMsgVibrator_" + userId, true);
	}

	// 设置新消息到来是否开启声音
	public static void setNewMsgComVoiceFlag(String userId, boolean flag) {
		SPUtils.put(QYApplication.getContext(), "newMsgVoice_" + userId, flag);
	}

	public static boolean getNewMsgComVoiceFlag(String userId) {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "newMsgVoice_"
				+ userId, true);
	}

	/** #######################摇书的配置################################ */
	public static void setShakeBookVoiceFlag(boolean flag) {
		SPUtils.put(QYApplication.getContext(), "shakeBookSetting_Voice_"
				+ QYApplication.getPersonId(), flag);
	}

	public static boolean getShakeBookVoiceFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(),
				"shakeBookSetting_Voice_" + QYApplication.getPersonId(), true);
	}

	public static void setShakeBookVibratorFlag(boolean flag) {
		SPUtils.put(QYApplication.getContext(), "shakeBookSetting_Vibrator_"
				+ QYApplication.getPersonId(), flag);
	}

	public static boolean getShakeBookVibratorFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(),
				"shakeBookSetting_Vibrator_" + QYApplication.getPersonId(),
				true);
	}

	/** ###########################用户预定座位相关数据################################## */
	// 设置预定标识
	public static void setIsBookSeatFlag(boolean flag) {
		SPUtils.put(QYApplication.getContext(),
				"isbook_" + QYApplication.getPersonId(), flag);
	}

	// 获取预定标识
	public static boolean getIsBookSeatFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "isbook_"
				+ QYApplication.getPersonId(), false);
	}

	/** ###########################用户预定座位相关数据################################## */
	// 设置入座标识
	public static void setIsArriveSeatFlag(boolean flag) {
		SPUtils.put(QYApplication.getContext(),
				"isarrive_" + QYApplication.getPersonId(), flag);
	}

	// 获取预定标识
	public static boolean getIsArriveSeatFlag() {
		return (Boolean) SPUtils.get(QYApplication.getContext(), "isarrive_"
				+ QYApplication.getPersonId(), false);
	}

	// 保存上一次的座位的位置
	public static void setLastBookSeatAddr(String addr) {
		SPUtils.put(QYApplication.getContext(),
				"lastSeat_" + QYApplication.getPersonId(), addr);
	}

	// 获取上一次的座位的位置
	public static String getLastBookSeatAddr() {
		return SPUtils.get(QYApplication.getContext(),
				"lastSeat_" + QYApplication.getPersonId(), "无").toString();
	}
}
