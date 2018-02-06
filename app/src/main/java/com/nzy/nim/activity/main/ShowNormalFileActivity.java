package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowNormalFileActivity extends BaseActivity {
	private ProgressBar progressBar;
	private ChatRecord record = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_file);
		progressBar = (ProgressBar) findViewById(R.id.show_file_progressBar);
		record = (ChatRecord) getIntent().getSerializableExtra("record");
		// 判断服务器和保存本地的路径是否为空
		if (!TextUtils.isEmpty(record.getRemoteFilePath())) {
			new DownLoadApk().execute(
					record.getRemoteFilePath(),
					QYApplication.createDirPath(MyConstants.FILE_DIR)
							+ record.getFileName());
		}
	}

	/**
	 * @Author liubo
	 * @date 2015-3-13下午7:56:47
	 * @TODO(功能)从其他页面跳转到本页面
	 * @mark(备注)
	 * @param context
	 *            上下文环境
	 *            服务器上的下载地址
	 */
	public static void actionIntent(Context context, ChatRecord record) {
		Intent intent = new Intent(context, ShowNormalFileActivity.class);
		intent.putExtra("record", record);
		context.startActivity(intent);
	}

	/**
	 * @author liubo
	 * @date 2015-3-13下午9:11:43
	 * @TODO(功能) 异步下载文件
	 * @mark(备注)
	 */
	private class DownLoadApk extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpURLConnection conn = null;
			try {
				URL url = new URL(params[0]);
				// 创建连接
				conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				try {
					// 向本地写数据
					FileUtils.saveFileLocal(conn.getInputStream(), params[1],
							conn.getContentLength(), new FileUtils.OnProgressListener() {
								@Override
								public void onProgress(int progress) {
									// TODO 打印文件写入进度
									publishProgress(progress);
								}
							});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (conn != null)
					conn.disconnect();
			}
			return params[1];
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// percentageTv.setText(values[0] + "/100");
			progressBar.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO 下载任务完成时
			super.onPostExecute(result);
			Intent intent = new Intent(MsgManager.getFileDownBroadcastAction());
			intent.putExtra("msgId_file", record.getMsgId());
			intent.putExtra("localPath", result);
			sendBroadcast(intent);
			FileUtils.openFile(ShowNormalFileActivity.this, new File(result));
			ShowNormalFileActivity.this.finish();

		}
	}
}
