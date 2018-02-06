package com.nzy.nim.tool.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 * 
 */
public class ToastUtil {

	private ToastUtil() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static Toast toast;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */

	public static void showShort(Context context, CharSequence message) {
		if (toast==null){
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}else{
			toast.setText(message);
		}
			toast.show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		if (toast==null){
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}else{
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		if (toast==null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}else{
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		if (toast==null){
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}else{
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
			toast = Toast.makeText(context, message, duration);
			toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
			toast = Toast.makeText(context, message, duration);
			toast.show();
	}

	/**
	 * 
	 * @author 刘波
	 * @date 2015-3-1上午11:19:27
	 * @todo 取消toast
	 */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}

//	public static void show(Context context, String msg, int gravity) {
//		if (toast==null){
//			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//			float offY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
//					context.getResources().getDisplayMetrics());
//			toast.setGravity(gravity, 0, (int) offY);
//		}else{
//			toast.setText(msg);
//		}
//
//		toast.show();
//	}
}