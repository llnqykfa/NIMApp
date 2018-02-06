package com.nzy.nim.tool.common;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.tmpbean.AppUpdateInfo;
import com.nzy.nim.helper.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateUtil {

	// app的包名
	private static final String packageName = "com.nzy.nim";
	/* 上下文环境 */
	private Context mContext;
	/* app版本信息 */
	private AppUpdateInfo info;
	/* 下载保存路径 */
	private String mSavePath;
	private UpdateOnClicListener updateOnClicListener;
	private PackageInfo packageInfo;// 当前包的信息
	private String downFileName = "";// 下载的文件名
	/* 更新进度条 */
	private ProgressBar mProgress;
	/* 更新进度值 */
	private TextView percentageTv;
	// 下载进度对话框
	private Dialog mDownloadDialog;

	/* 是否取消更新 */
	// private boolean cancelUpdate = false;

	/* 回调接口 */
	public interface UpdateOnClicListener {
		void update();

		void unUpdate();

		void downLoadok();
	}

	public void setUpdateOnClickListener(UpdateOnClicListener listener) {
		this.updateOnClicListener = listener;
	}

	public UpdateUtil(Context context, AppUpdateInfo info) {
		this.mContext = context;
		this.info = info;
		this.downFileName = "FJZQA" + info.getSysVer() + ".apk";
		try {
			if(context.getPackageManager()==null){
				return;
			}
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测软件更新
	 */
	public boolean checkUpdate() {
		if (isUpdate()) {
			// 显示提示对话框
			showNoticeDialog();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private boolean isUpdate() {
		if (DataUtil.isEmpty(info))
			return false;
		// 获取当前软件版本
		int versionCode = packageInfo.versionCode;
		// 获取服务器上的软件版本
		int serviceCode = Integer.valueOf(info.getSysVer());
		// 版本判断
		if (serviceCode > versionCode) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, downFileName);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		Builder builder = new Builder(mContext);
		String versionInfo = "更新内容:"+info.getFixDescripe();
		builder.setTitle("软件更新");// 标题
		builder.setMessage(versionInfo);// 信息
		builder.setCancelable(false);
		// 更新
		builder.setPositiveButton("更新",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// // 显示下载对话框
						showDownloadDialog();
//						download();
						if (updateOnClicListener != null)
							updateOnClicListener.update();
					}
				});
//		// 稍后更新
//		builder.setNegativeButton(R.string.soft_update_later,
//				new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						if (updateOnClicListener != null)
//							updateOnClicListener.unUpdate();
//					}
//				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * @date 2015-3-4下午9:16:39
	 * @todo 判断sd卡的存储空间是否足够
	 * @return
	 */
	private boolean isSDEnough(long size) {
		if (size > CommonUtil.getSDSize(CommonUtil.getSDPath(), false)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 *
	 * @date 2015-3-4下午9:33:07
	 * @todo 获取httpURLConnection连接
	 * @param urlValue
	 * @return
	 */
	private HttpURLConnection getConn(String urlValue) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlValue);
			// 创建连接
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 *
	 * @date 2015-3-4下午9:49:11
	 * @todo 显示下载进度条
	 */
	private void showDownloadDialog() {
		// 构造软件下载对话框
		Builder builder = new Builder(mContext);
		builder.setTitle("正在更新");
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		percentageTv = (TextView) v.findViewById(R.id.tv_progress_percentage);
		percentageTv.setText("0/100");
		mProgress.setProgress(0);
		builder.setView(v);
		builder.setCancelable(false);
		// // 取消更新
		// builder.setNegativeButton(R.string.soft_update_cancel,
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// // 设置取消状态
		// cancelUpdate = true;
		// if (updateOnClicListener != null)
		// updateOnClicListener.unUpdate();
		// }
		// });
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 下载文件
		new DownLoadApk().execute();
	}

	/**
	 * 
	 * @date 2015-3-4下午6:19:57
	 * @todo 下载最新Apk的异步线程
	 */
	private class DownLoadApk extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// 获得存储卡的路径(保存路径)
			mSavePath = MyConstants.BASE_DIR + "/";
			// 获取下载地址连接
			HttpURLConnection conn = getConn(info.getDownloadUrl());
			// 判断存储空间是否足够
			// if (isSDEnough(conn.getContentLength())) {
			try {
				// 向本地写数据
				com.nzy.nim.helper.FileUtils.saveFileLocal(conn.getInputStream(), mSavePath
								+ downFileName, conn.getContentLength(),
						new FileUtils.OnProgressListener() {

							@Override
							public void onProgress(int progress) {
								// TODO Auto-generated method stub
								publishProgress(progress);
							}
						});
			} catch (IOException e) {
				e.printStackTrace();
			}
			// else {
			// Toast.makeText(mContext, "存储空间不足！！！", Toast.LENGTH_LONG).show();
			// }
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			percentageTv.setText(values[0] + "/100");
			mProgress.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO 下载任务完成时
			super.onPostExecute(result);
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
			// 安装文件
			installApk();
			if (updateOnClicListener != null)
				updateOnClicListener.downLoadok();

		}
	}
	private void download(){
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(info.getDownloadUrl()));
		//设置在什么网络情况下进行下载
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		//设置通知栏标题
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		request.setTitle("下载");
		request.setDescription("福建足球APP正在下载");
		request.setAllowedOverRoaming(false);
		//设置文件存放目录
		request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "mydown");
		DownloadManager  downManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		downManager.enqueue(request);
	}
}
