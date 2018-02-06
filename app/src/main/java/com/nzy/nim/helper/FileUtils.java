package com.nzy.nim.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	// 文件写入本地的接口
	public interface OnProgressListener {
		void onProgress(int progress);
	}

	/**
	 * @Author liubo
	 * @date 2015-3-14下午5:13:15
	 * @TODO(功能) 根据绝对路径创建文件夹或者文件
	 * @mark(备注)
	 * @param filePath
	 * @return
	 */
	public static File createFile(String filePath) {
		File file = new File(filePath);
		try {
			// 获取标志路径格式的文件
			file = file.getCanonicalFile();
			// 判断文件是否是文件夹
			if (file.isDirectory() && !file.exists())// 是文件夹
				file.mkdirs();
			else if (!file.getParentFile().exists()) {// 不是文件夹则判断该文件的父路径是否存在
				file.getParentFile().mkdirs();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * @Author liubo
	 * @date 2015-3-13下午8:44:31
	 * @TODO(功能) 将数据保存到本地
	 * @mark(备注)
	 * @param data
	 * @param filePath
	 * @return
	 */
	public static String saveFileLocal(byte[] data, String filePath) {
		if (data == null || data.length <= 0)
			try {
				throw new Exception("data数据为空！！！");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filePath));
			fos.write(data);
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}

	/**
	 * @Author liubo
	 * @date 2015-3-14下午7:06:41
	 * @TODO(功能) 将字节流保存到本地
	 * @mark(备注)
	 * @param is
	 * @param filePath
	 * @param length
	 * @param listener
	 */
	public static void saveFileLocal(InputStream is, String filePath,
			long length, OnProgressListener listener) {
		int count = 0;// 写入进度
		FileOutputStream fos = null;
		try {
			// 设置下载的文件的路径名
			fos = new FileOutputStream(createFile(filePath));
			// 缓存
			byte buf[] = new byte[1024];// 1KB
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				count += len;
				if (listener != null) {
					listener.onProgress((int) (((float) count / length) * 100));
				}
				// 写入文件
				fos.write(buf, 0, len);
				fos.flush();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭读写操作
			try {
				if (is != null)
					is.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-29上午9:43:03
	 * @TODO 删除文件
	 * @param path
	 */
	public static boolean deleteFile(String path) {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-3-29下午12:26:20
	 * @TODO 获取文件扩展名
	 * @param filePath
	 * @return
	 */
	public static String getExtensions(String filePath) {
		if(DataUtil.isEmpty(filePath)){
			return null;
		}
		String[] path = filePath.split("\\.");
		if (path != null && path.length >= 2) {
			return path[path.length - 1];
		} else
			return null;
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	public static void openFile(Context context, File file) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		try {
			// 跳转
			context.startActivity(intent);
		} catch (Exception e) {
			ToastUtil.showShort(context, "找不到打开该文件的应用!");
		}

	}

	/**
	 * @author LIUBO
	 * @date 2015-4-5上午11:45:01
	 * @TODO 获取 asset文件夹下的文件内容
	 * @param context
	 *            上下文环境
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static List<String> getAssetFile(Context context, String fileName) {
		List<String> list = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = context.getResources().getAssets().open(fileName);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;

	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	private static String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	private static final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}
