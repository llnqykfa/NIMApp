package com.nzy.nim.tool.common;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

public class DataUtil {
	/**
	 * 
	 * @TODO 判断对象是否为空
	 * @param value
	 * @return
	 * @Return boolean
	 * @Mark 基本类型的不建议使用该方法
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		/**
		 * 刘波 日期：2014-12-18 修改： 添加了判断value是否为"[]"
		 */
		if ((value instanceof String) && (((String) value).equals("[]")))
			return true;
		if ((value instanceof String)
				&& (((String) value).trim().equals("null")))
			return true;
		if ((value instanceof String) && (((String) value).trim().equals("")))
			return true;
		if ((value instanceof String)
				&& (((String) value).trim().length() <= 0)) {
			return true;
		}
		if ((value instanceof Object[]) && (((Object[]) value).length <= 0)) {
			return true;
		}
		// 判断数组中的值是否全部为空null.
		if (value instanceof Object[]) {
			Object[] t = (Object[]) value;
			for (int i = 0; i < t.length; i++) {
				if (t[i] != null) {
					return false;
				}
			}
			return true;
		}
		if ((value instanceof Collection)
				&& ((Collection<?>) value).size() <= 0) {
			return true;
		}
		if ((value instanceof Dictionary)
				&& ((Dictionary<?, ?>) value).size() <= 0) {
			return true;
		}
		if ((value instanceof Map) && ((Map<?, ?>) value).size() <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * @TODO 判断传入信息是否是ok(不区分大小写)
	 * @param message
	 * @return Return:
	 */
	public static boolean isOk(String message) {
		if (message != null && message.toLowerCase().equals("\"message:ok\"")) {
			return true;
		} else
			return false;
	}

	/**
	 * @TODO 判断服务器返回的提示信息是否为空， 不为空显示服务器提示信息 为空设置个人提示信息
	 * @param context
	 * @param msg
	 * @param yourMsg
	 *            Return:
	 */
	public static void showNotification(Context context, String msg,
			String yourMsg) {
		// msg不为空时或者msg为ok时
		if (!isEmpty(msg) && !isOk(dealMessage(msg))) {
			ToastUtil.showShort(context, dealMessage(msg));
		} else {
			ToastUtil.showShort(context, yourMsg);
		}
	}

	/**
	 *
	 * @param context
	 *            类
	 * @param msg
	 *            服务器返回信息
	 * @param yourMsg
	 *            需要提示的信息
	 * @return 服务器返回状态
	 */
	public static boolean isNotificationOK(Context context, String msg) {
		// msg不为空时或者msg为ok时
		if ((!isEmpty(msg)) && isThisOk(dealMessage(msg))) {

			return true;
		} else {

			return false;
		}
	}

	/**
	 * 
	 * @param message
	 * @return 是否为OK
	 */
	private static boolean isThisOk(String message) {
		if (message != null && message.toLowerCase().equals("ok")) {
			return true;
		} else
			return false;
	}

	/**
	 * @TODO 去掉服务器返回来的信息的引号 2014-12-19
	 * @param msg
	 * @return Return:
	 */
	public static String dealMessage(String msg) {
		String[] str = msg.split("[\"|(message:)]");
		return str[str.length - 1];
	}

	public static String dealMessage1(String msg) {
		
		return msg.replace("message:", "").replace("\"", "") ;
	}

	/**
	 * @TODO(功能)数据大小的显示格式
	 * @mark(备注)
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat(".00");// 保留两位小数
		if (size < 1024)
			return size + "B";
		if (size < 1024 * 1024)
			return df.format(size / 1024.0) + "KB";
		if (size < 1024 * 1024 * 1024l)
			return df.format(size / (1024.0 * 1024.0)) + "MB";
		return df.format(size / (1024.0 * 1024.0 * 1024.0)) + "TB";
	}
}
