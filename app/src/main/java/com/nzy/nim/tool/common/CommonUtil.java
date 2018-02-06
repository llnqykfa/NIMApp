package com.nzy.nim.tool.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.nzy.nim.db.bean.GroupMembers;
import com.nzy.nim.vo.OldPersonVO;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @todo 一些公共的工具类
 */
public class CommonUtil {

	/**
	 * 
	 * @todo 隐藏软件盘
	 * @param activity
	 *            上下文环境
	 * @param v
	 *            视图
	 */
	public static void hideKeyboard(Activity activity, View v) {
		if (activity == null || v == null)
			return;
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * 
	 * @todo 弹出软键盘
	 * @param activity
	 * @param v
	 */
	public static void showKeyboard(Activity activity, View v) {
		((InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(
				v, 0);
	}

	/**
	 * 将格式为 2015-11-1101:10:12 的时间格式化
	 * @param datatime
	 */
	public static String formatDateString(String datatime)
	{
		return datatime.substring(0,10);
	}

	/**
	 * 
	 * @todo 隐藏软键盘
	 * @param activity
	 */
	public static void hideKeyboard(Activity activity) {
		if (activity == null)
			return;
		InputMethodManager manager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() != null)
			manager.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * @TODO 获取默认存储的路径
	 * @return
	 * @Return String
	 * @mark 手机默认的存储路径，可能是手机存储也可能是外部存储
	 */
	public static String getSDPath() {
		if (isExitsSdcard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * @TODO(功能) 判断sd卡是否存在
	 * @mark(备注)
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @todo 获取手机默认的内部存储的大小
	 * @param sdPath
	 * @flag 标志 true 为总大小，false 为空余大小
	 * @return
	 * @mark 单位：bit（比特）
	 */

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getSDSize(String sdPath, boolean flag) {
		if (sdPath == null)
			return 0;
		StatFs statfs = new StatFs(sdPath);
		// api小于18的用旧的方法，用新方法会报找不到该方法的异常
		if (isLowVersion(18)) {
			if (flag)
				// 返回总存储大小
				return statfs.getBlockCount() * statfs.getBlockSize();
			else
				// 返回空闲的存储大小
				return statfs.getAvailableBlocks() * statfs.getBlockSize();
		} else {
			if (flag)
				// 返回总存储大小
				return statfs.getBlockCountLong() * statfs.getBlockSizeLong();
			else
				// 返回空闲的存储大小
				return statfs.getAvailableBlocksLong()
						* statfs.getBlockSizeLong();
		}

	}

	/**
	 * @todo 判断sd卡的存储空间是否足够
	 * @param size
	 * @return
	 */
	public static boolean isSDEnough(long size) {
		if (size > getSDSize(getSDPath(), false)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @todo 判断是否小于当前api是否小于输入的api
	 * @param apiVersion
	 * @return
	 */
	public static boolean isLowVersion(int apiVersion) {
		if ((Build.VERSION.SDK_INT < apiVersion)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将scrollview向上滚动显示出所有控件
	 * 
	 * @param scroll
	 *            外部scrollview控件
	 * @param inner
	 *            scrollview内部控件
	 */
	public static void scrollToBottom(final View scroll, final View inner) {
		Handler mHandler = new Handler();

		mHandler.postDelayed(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		}, 200);// 200ms是表示200ms后再执行滚动，页面布局有一个时间差
	}

	/**
	 * 将scrollview向上滚动显示出所有控件
	 * 
	 * @param scroll
	 */
	public static void scrollToBottom(final ScrollView scroll) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				scroll.fullScroll(ScrollView.FOCUS_DOWN);
			}

		}, 200);
	}

	/**
	 * @TODO 获取成员id集合
	 * @param members
	 * @return
	 */
	public static ArrayList<String> getMemberIds(List<GroupMembers> members) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < members.size(); i++) {
			list.add(members.get(i).getMemberId());
		}
		return list;
	}

	/**
	 * @TODO 获取用户id集合
	 * @param persons
	 * @return
	 */
	public static ArrayList<String> getPersonIds(List<OldPersonVO> persons) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < persons.size(); i++) {
			list.add(persons.get(i).getPk_person());
		}
		return list;
	}

		/**
		 * 但是当我们没在AndroidManifest.xml中设置其debug属性时:
		 * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
		 * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
		 * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
		 *
		 * @param context
		 * @return
		 */
		public static boolean isApkDebugable(Context context) {
			try {
				ApplicationInfo info= context.getApplicationInfo();
				return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
			} catch (Exception e) {

			}
			return false;
		}
}