package com.nzy.nim.tool.common;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-25 下午10:50:18
 * @classify(类别) 工具类
 * @TODO(功能) TODO 日期转换成字符串
 * @Param(参数)
 * @Remark(备注)
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
	public static final long DAY_TIME_MILLIS = 24 * 60 * 60 * 1000l;// 一天的毫秒值
	public static final long YEAR_TIME_MILLIS = 365*24 * 60 * 60 * 1000l;// 一年的毫秒值
	public static final long WEEK_TIME_MILLIS = 7 * DAY_TIME_MILLIS;// 一周的毫秒值
	public static final long HOUR_TIME_MILLIS = 60 * 60 * 1000l;// 一小时的毫秒值
	public static final long MINUTE_TIME_MILLIS = 60 * 1000l;// 一分钟的毫秒值
	public static final long SECOND_TIME_MILLIS = 1000l;// 一秒的毫秒值
	public static final String HOUR_PATTERN = "HH:mm";// 小时格式
	public static final String YESTERDAY_1 = "昨天 HH:mm";
	public static final String THE_DAY_BEFORE_YESTERDAY_1 = "前天 HH:mm";
	public static final String WEEK_PATTERN_1 = "EE HH:mm";// 星期几
	public static final String YESTERDAY_2 = "昨天";
	public static final String THE_DAY_BEFORE_YESTERDAY_2 = "前天";
	public static final String WEEK_PATTERN_2 = "EE";// 星期几

	public static final String DATE_PATTERN_1 = "yyyy-MM-dd HH:mm";
	public static final String DATE_PATTERN_2 = "yyyy-MM-dd";
	public static final String DATE_PATTERN_3 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN_4 = "yyyyMMddHHmmss";
	public static final String DATE_PATTERN_5 = "MM-dd HH:mm";
	public static final Integer THIS_YEAR = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis())));

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 将日期转化成字符串格式
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式 （如：yyyy-MM-dd HH:mm）
	 * @return
	 * @Date 2015-1-25
	 * @Return String
	 */
	public static String formatDate(Date date, String pattern) {
		if (DataUtil.isEmpty(date))
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 将long整型的数据转化成时间字符串格式
	 * @param time
	 *            数据
	 * @param pattern
	 *            日期格式 （如：yyyy-MM-dd HH:mm）
	 * @return
	 * @Date 2015-1-25
	 * @Return String
	 */
	public static String formatLong(long time, String pattern) {
		return formatDate(new Date(time), pattern);
	}

	/**
	 * @Author LIUBO
	 * @TODO TODO 获取当前时间
	 * @return
	 * @Date 2015-1-21
	 * @Return String 修改 ：吴君 内容：月份和日期不对，加1放错地方。--->时间格式统一化 日期：2015-1-21
	 */
	public static String getDate(String pattern) {
		return formatDate(Calendar.getInstance().getTime(), pattern);
	}

	/**
	 * @author 刘波
	 * @param data
	 *            日期字符串
	 * @param pattern1
	 *            原始的日期字符串格式
	 * @param pattern2
	 *            要转换成的日期字符串格式
	 * @return
	 */
	public static String resverDate(String data, String pattern1,
									String pattern2) {
		SimpleDateFormat simple = new SimpleDateFormat(pattern1);
		SimpleDateFormat simple2 = new SimpleDateFormat(pattern2);
		Date da = null;
		String getDate = null;
		try {

			da = simple.parse(data);
			getDate = simple2.format(da);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (DataUtil.isEmpty(getDate))
			return data;
		else
			return getDate;
	}

	/**
	 * 
	 * @Author liubo
	 * @date 2015-3-12上午11:56:54
	 * @TODO(功能) 获取时间提示
	 * @mark(备注)
	 * @param oldTime
	 * @param currentDate
	 * @return
	 */
	public static String getStartDate(Date oldTime, Date currentDate) {
		long time = Math.abs(currentDate.getTime() - oldTime.getTime());
		if (time < MINUTE_TIME_MILLIS) {// 小于1分钟
			return "刚刚";
		}
		if (time < HOUR_TIME_MILLIS) {// 小于1小时
			return time / MINUTE_TIME_MILLIS + "分钟前";
		}
		if (time < DAY_TIME_MILLIS) {// 小于1天
			return time / HOUR_TIME_MILLIS + "小时前";
		}
		if (time < WEEK_TIME_MILLIS) {// 少于1周
			return time / DAY_TIME_MILLIS + "天前";
		}
		String data;
		if(getOldTime(oldTime) <THIS_YEAR){
			data=DATE_PATTERN_1;
		}else{
			data=DATE_PATTERN_5;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(data);
		return sdf.format(oldTime);
	}

	public static Integer getOldTime(Date oldTime){
		return Integer.valueOf(new SimpleDateFormat("yyyy").format(oldTime));
	}

	/**
	 * 将字符串转化为时间
	 * 
	 * @param str
	 * @return
	 */
	public static Date strToDate(String str, String pattern) {
		// sample：Tue May 31 17:46:55 +0800 2011
		// E：周 MMM：字符串形式的月，如果只有两个M，表示数值形式的月 Z表示时区（＋0800）
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date result = null;
		try {
			result = sdf.parse(str);
		} catch (Exception e) {
			return new Date();
		}
		return result;
	}

	/**
	 * 
	 * @Author liubo
	 * @date 2015-3-9下午9:03:27
	 * @TODO(功能) 计算时间差
	 * @mark(备注)
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long calculateTimeDifference(long time1, long time2) {
		return Math.abs(time1 - time2);
	}

	/**
	 * @作者： 刘波
	 * @日期：2015-1-11
	 * @功能：倒计时
	 * @param textView
	 *            显示倒计时的view
	 * @param millisInFuture
	 *            倒计时的总时间
	 * @param countDownInterval
	 *            多久时间刷新一次 (即总时间减少一次)（1000=1秒）
	 * @param msg
	 *            时间结束后的显示信息
	 */
	public void downTime(final TextView textView, long millisInFuture,
						 long countDownInterval, final String msg) {
		new CountDownTimer(millisInFuture, countDownInterval) {
			public void onTick(long millisUntilFinished) {
				// 天
				long day = millisUntilFinished / DAY_TIME_MILLIS;
				// 小时
				long hour = millisUntilFinished % DAY_TIME_MILLIS
						/ HOUR_TIME_MILLIS;
				// 分钟
				long minute = millisUntilFinished % DAY_TIME_MILLIS
						% HOUR_TIME_MILLIS / MINUTE_TIME_MILLIS;
				// 秒
				long second = millisUntilFinished % DAY_TIME_MILLIS
						% HOUR_TIME_MILLIS % MINUTE_TIME_MILLIS
						/ SECOND_TIME_MILLIS;
				textView.setText(day + "天" + hour + "小时" + minute + "分钟"
						+ second + "秒");
			}

			public void onFinish() {
				textView.setText(msg);
			}
		}.start();
	}

	/**
	 * 
	 * @Author liubo
	 * @date 2015-3-11下午3:44:14
	 * @TODO(功能)判断当前时间，并根据不同的条件返回不同的时间显示格式
	 * @mark(备注) 默认为格式1
	 * @param date
	 *            日期
	 * @param isPattern2
	 *            是否选择格式2
	 * @return
	 */
	public static String showDate(Date date, boolean isPattern2) {
		SimpleDateFormat df = new SimpleDateFormat();
		long times = calculateDate(new Date(),date);
		if (times < DAY_TIME_MILLIS) {// 时间小于一天
			df.applyPattern(HOUR_PATTERN);
			return df.format(date);
		}
		if (times < 2 * DAY_TIME_MILLIS) {// 昨天
			if (isPattern2)
				df.applyPattern(YESTERDAY_2);
			else
				df.applyPattern(YESTERDAY_1);
			return df.format(date);
		}
		if (times < 3 * DAY_TIME_MILLIS) {// 前天
			if (isPattern2)
				df.applyPattern(THE_DAY_BEFORE_YESTERDAY_2);
			else
				df.applyPattern(THE_DAY_BEFORE_YESTERDAY_1);
			return df.format(date);
		}
		if (times < WEEK_TIME_MILLIS) {// 显示星期几
			if (isPattern2)
				df.applyPattern(WEEK_PATTERN_2);
			else
				df.applyPattern(WEEK_PATTERN_1);
			return df.format(date);
		}
		// 具体日期
		if (isPattern2)
			df.applyPattern(DATE_PATTERN_2);
		else
			df.applyPattern(DATE_PATTERN_1);
		return df.format(date);

	}

	/**
	 * 
	 * @Author liubo
	 * @date 2015-3-12上午10:15:49
	 * @TODO(功能) 计算date的日期到当前日期的相差天数(精确到天)
	 * @mark(备注)
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long calculateDate(Date date1, Date date2) {
		Date currentDate = strToDate(
				formatDate(date1, DATE_PATTERN_2), DATE_PATTERN_2);
		Date oldDate = strToDate(formatDate(date2, DATE_PATTERN_2),
				DATE_PATTERN_2);
		return Math.abs(currentDate.getTime() - oldDate.getTime());
	}
}
