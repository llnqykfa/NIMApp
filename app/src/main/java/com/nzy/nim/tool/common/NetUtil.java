package com.nzy.nim.tool.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetUtil {
	/**
	 * 判断是否有网络可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isHasNetAvailable(Context context) {
		if (isWifiAvailable(context)) {
			return true;
		} else if (isNetAvailable(context)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断wifi是否可用
	 * 
	 * @param context
	 *            上下文
	 * @return true可用，false不可用
	 */
	public static boolean isWifiAvailable(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		int wifiIp;
		if (info == null) {
			wifiIp = 0;
		} else {
			wifiIp = info.getIpAddress();
		}
		if (manager.isWifiEnabled() && wifiIp != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否有除了wifi之外的网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == manager) {
			return false;
		} else {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (null == info) {
				return false;
			} else {
				if (info.isAvailable()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检测网络是否链接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		if (net != null && net.isConnected()) {
			return true;
		}
		return false;
	}
}
