package com.nzy.nim.tool.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 获得屏幕相关的辅助类
 * 
 * @author zhy
 * 
 */
public class ScreenUtils {

	public static  int SCREEN_WIDTH = 720;
	public static  int SCREEN_HEIGHT = 1080;
	private ScreenUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics outMetrics = getMetrics(context);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics outMetrics = getMetrics(context);
		return outMetrics.heightPixels;
	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 获取屏幕的密度（屏幕密度（像素比例：0.75/1.0/1.5/2.0） ）
	 * @param context
	 * @return
	 * @Date 2015-2-5
	 * @Return float
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics outMetrics = getMetrics(context);
		return outMetrics.density;
	}

	private static float getDensity(Context ctx){
		return ctx.getResources().getDisplayMetrics().density;
	}
	public static int dip2px(Context ctx,int dip){
		float density = getDensity(ctx);
		return (int)(dip * density + 0.5);
	}

	public static int px2dip(Context ctx,int px){
		float density = getDensity(ctx);
		return (int)((px - 0.5) / density);
	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 获取屏幕的密度（屏幕密度（每寸像素：120/160/240/320））
	 * @param context
	 * @return
	 * @Date 2015-2-5
	 * @Return int
	 */
	public static int getScreenDensityDPI(Context context) {
		DisplayMetrics outMetrics = getMetrics(context);
		return outMetrics.densityDpi;
	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 获取屏幕的
	 * @param context
	 * @return
	 * @Date 2015-2-5
	 * @Return DisplayMetrics
	 */
	private static DisplayMetrics getMetrics(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取标题栏的高度（系统的标题栏）
	 * 
	 * @param context
	 * @return
	 */
	public static int getTitleHeight(Activity activity) {
		return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

}
