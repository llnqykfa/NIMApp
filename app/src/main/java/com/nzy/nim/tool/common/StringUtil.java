package com.nzy.nim.tool.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 名称：字符串处理工具 性质：工具类 参数：
 * 
 * @author 刘波 日期：2014-12-20 TODO 对字符串进行处理 备注：
 * 
 */
public class StringUtil {
	public static final String regex_number_letter = "(\\d|\\w)+";// 数字或字母

	/**
	 * 电话号码匹配(包括固话和手机号码)
	 */
	public static final String regex_phone = "(0\\d{2,3}\\d{7,8})|(1[3458]\\d{9})";
	/**
	 * 邮政编码匹配
	 */
	public static final String regex_ems = "\\d{6}";
	/**
	 * 邮箱匹配
	 */
	public static final String regex_email = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)"
			+ "+[a-zA-Z]{2,}$";
	/**
	 * 密码匹配
	 */
	public static final String regex_pwd = "^[a-zA-Z0-9]\\w{5,16}$";// 密码长度在6~16之间，只能包含字符、数字和下划线

	/**
	 * @author 刘波 TODO 2014-12-20
	 * @param str
	 *            要拆分的字符串
	 * @param pattern
	 *            正则表达式
	 * @return Return: 字符串数组
	 */
	public static String[] splitString(String str, String pattern) {
		return str.split(pattern);
	}

	/**
	 * 作者：刘波 日期：2014-12-28 功能： 获得座位编号
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String getSeatName(String str, String pattern) {
		String[] str1 = str.split(pattern);
		String str2 = str1[str1.length - 1];
		StringBuffer sb = new StringBuffer();
		char[] c = str2.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] != '0') {
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}
	

	/**
	 * @param <T>
	 * @Author LIUBO
	 * @TODO TODO 判断两个数据中的元素是否相等，不区分顺序
	 * @param arr1
	 *            数组1
	 * @param arr2
	 *            数组 2
	 * @return
	 * @Date 2015-1-27
	 * @Return boolean
	 */
	public static <T> boolean isEquals(T[] arr1, T[] arr2) {
		if (!DataUtil.isEmpty(arr1) && !DataUtil.isEmpty(arr2)) {
			Arrays.sort(arr1);
			Arrays.sort(arr2);
			if (arr1.equals(arr2))
				return true;
		}
		return false;
	}

	/**
	 * 对字符串数组进行倒序排列
	 * 
	 * @param str
	 * @return
	 */
	public static String[] reserveString(String[] str) {
		List<String> strList = new ArrayList<String>();
		for (int i = 0; i < str.length; i++) {
			strList.add(str[i]);
		}
		Collections.reverse(strList);
		String[] str2 = new String[] {};
		for (int j = 0; j < strList.size(); j++) {
			str2[j] = strList.get(j);
		}
		return str2;

	}

	/**
	 * 
	 * @author 刘波
	 * @date 2015-2-28下午5:54:58
	 * @todo 判断输入是否符合给定的格式
	 * @param pattern
	 *            格式
	 * @param input
	 *            输入的数据
	 * @return
	 */
	public static boolean patternRegexString(String pattern, String input) {
		Pattern patt = Pattern.compile(pattern);
		Matcher matcher = patt.matcher(input);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 将“_”转换成 “\0” Yi 2015-3-9下午8:25:25 TODO
	 * 
	 * @param pattern
	 * @param input
	 * @return*String
	 */
	public static String patternRegex2NewString(String pattern, String input) {
		String str = input.replaceAll(pattern, "#");
		return str;
	}

	/**
	 * 拼接“file://”
	 * 
	 * @author quanyi
	 * @date 2015-3-13上午11:13:05
	 * @TODO TODO
	 * @param list
	 * @return
	 */
	public static ArrayList<String> getSubString(List<String> list) {
		ArrayList<String> newList = new ArrayList<String>();
		if (!DataUtil.isEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				String uri = list.get(i);
				newList.add(uri);
			}
		}
		return newList;
	}

	/**
	 * @author quanyi
	 * @date 2015-3-13下午3:38:01
	 * @TODO TODO 数据源反转
	 * @param datas
	 * @return
	 */
	public static <T> List<T> reverseList(List<T> datas) {
		List<T> reverseDatas = new ArrayList<T>();
		for (int i = datas.size() - 1; i >= 0; i--) {
			reverseDatas.add(datas.get(i));
		}
		return reverseDatas;
	}

	/**
	 * @author quanyi
	 * @date 2015-3-27下午2:59:36
	 * @TODO TODO对拼接的字符串进行拆分
	 * @param splite
	 */
	public static String[] spliteString(String splite) {
		String str = splite.substring(0, splite.length());
		String[] Str = null;
		if (!DataUtil.isEmpty(str)) {
			Str = splite.split("_");
		}
		return Str;
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-10下午1:05:24
	 * @TODO 字符串集合转换成字符串数组
	 * @param list
	 * @return
	 */
	public static String[] listToString(List<String> list) {
		String[] strs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			strs[i] = list.get(i);
		}
		return strs;
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-30下午5:40:04
	 * @TODO 汉字转换成拼音，返回拼音的首字母
	 * @param chines
	 *            汉字
	 * @mark 中文的标点符号无法识别
	 * @return
	 */
	public static String converterToFirstSpell(String chines) {
		StringBuffer buffer = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					buffer.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				buffer.append(nameChar[i]);
			}
		}
		return buffer.toString();
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-30下午5:41:13
	 * @TODO 汉字转换成拼音，返回全拼音
	 * @param chines
	 * @mark 中文的标点符号无法识别
	 * @return
	 */
	public static String converterToSpell(String chines) {
		StringBuffer buffer = new StringBuffer();
		// String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					buffer.append(PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				buffer.append(nameChar[i]);
			}
		}
		return buffer.toString();
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-7下午12:48:00
	 * @TODO 拼接字符串
	 * @param source
	 *            要拼接的字符串
	 * @param spliceFlag
	 *            连接标志
	 * @return
	 */
	public static String spliceString(List<String> source, String spliceFlag) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < source.size(); i++) {
			if (i == source.size() - 1) {
				sb.append(source.get(i));
			} else {
				sb.append(source.get(i)).append(spliceFlag);
			}
		}
		return sb.toString();
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^(((13[0-9])|(14([5-7]))|(15([0-3]|[5-9]))|(17[6-8])|(18[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean checkInfo(String data, String regex) {
		boolean flag = false;
		try {
			Matcher matcher = Pattern.compile(regex).matcher(data);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-15下午1:42:59
	 * @TODO 获取MD5加密后的字符串
	 * @param origString
	 * @return
	 */
	public static String getMD5ofStr(String origString) {
		String origMD5 = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			// md5.update(origString.getBytes());
			byte[] result = md5.digest(origString.getBytes());
			origMD5 = byteArray2HexStr(result);
			// if ("123".equals(origString)) {
			// System.out.println(new String(result));
			// System.out.println(new BigInteger(result).toString(16));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return origMD5;
	}

	private static String byteArray2HexStr(byte[] bs) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bs) {
			sb.append(byte2HexStr(b));
		}
		return sb.toString();
	}

	private static String byte2HexStr(byte b) {
		String hexStr = null;
		int n = b;
		if (n < 0) {
			// 若需要自定义加密,请修改这个移位算法即可
			n = b & 0x7F + 128;
		}
		hexStr = Integer.toHexString(n / 16) + Integer.toHexString(n % 16);
		return hexStr.toUpperCase();
	}

	/**
	 * @author ZHUOYI
	 * @date 2016-12-06下午16:06:59
	 * @TODO 获取MD5加密后的字符串
	 * @param origString
	 * @return
	 */
	public static String getMD5ofStrNormal(String origString) {
		String origMD5 = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			// md5.update(origString.getBytes());
			byte[] result = md5.digest(origString.getBytes());
			origMD5 = byteArray2HexStrNormal(result);
			// if ("123".equals(origString)) {
			// System.out.println(new String(result));
			// System.out.println(new BigInteger(result).toString(16));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return origMD5;
	}

	private static String byteArray2HexStrNormal(byte[] bs) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bs) {
			sb.append(byte2HexStrNormal(b));
		}
		return sb.toString();
	}

	private static String byte2HexStrNormal(byte b) {
		String hexStr = null;
		int n = b;
		if (n < 0) {
			// 若需要自定义加密,请修改这个移位算法即可
			n = b & 0x7F + 128;
		}
		hexStr = Integer.toHexString(n / 16) + Integer.toHexString(n % 16);
		return hexStr;
	}

}
