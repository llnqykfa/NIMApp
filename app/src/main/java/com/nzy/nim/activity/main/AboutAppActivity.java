package com.nzy.nim.activity.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.tmpbean.AppUpdateInfo;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.tool.common.UpdateUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import com.nzy.nim.R;

/**
 * Created by Administrator on 2016/12/21.
 */

public class AboutAppActivity extends BaseActivity {
    private RelativeLayout update;
    private ImageView newSign;
    private TextView currentVersion;
    private ProgressDialog pgDialog;
    private int code;
    private AppUpdateInfo info;
    private int click=0;
    private TextView copyrightName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        QYApplication.ACTIVITY_NAME="关于福建足球";
        Intent intent = getIntent();
        code = intent.getExtras().getInt("code");
        info = (AppUpdateInfo) intent.getSerializableExtra("update");
        initTopBar();
        update = (RelativeLayout) findViewById(R.id.about_qy_update);
        newSign = (ImageView) findViewById(R.id.about_qy_new_sign);
        TextView tv_up_name = (TextView) findViewById(R.id.tv_up_name);
        currentVersion = (TextView) findViewById(R.id.about_qy_current_version);
        copyrightName = (TextView) findViewById(R.id.copyright_name);
        if (QYApplication.getPackageInfo() != null)
            currentVersion.setText("版本号 V"
                    + QYApplication.getPackageInfo().versionName);
        if(code==1){
            tv_up_name.setText("新版本更新");
        }else{
            tv_up_name.setText("已是新版本");
        }
        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(code==1){
                    showDownloadDialog();
                }else{
                    click++;
                    if(click>5){
                        ToastUtil.showShort(AboutAppActivity.this,"请稍后再重试");
                        click=0;
                    }
                }
//				checkUpdate();
            }
        });
        String s = DateUtil.formatDate(new Date(), "yyyy");
        if (s.compareTo("2016")<=0) {
            copyrightName.setText("Copyright©2016 fjfootball.cn");
        }else{
            copyrightName.setText("Copyright©2016-" + s + " fjfootball.cn");
        }
    }

    private void initTopBar() {
        pgDialog= DialogHelper.getSDialog(AboutAppActivity.this, "检测更新中···", false);
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        titleContent.setText("关于福建足球");
        next.setVisibility(View.GONE);
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static void actionIntent(Context context, int code, AppUpdateInfo info) {
        Intent intent = new Intent(context, AboutAppActivity.class);
        intent.putExtra("update",info);
        intent.putExtra("code",code);
        context.startActivity(intent);
    }

    /**
     * @author LIUBO
     * @date 2015-4-7下午9:12:13
     * @TODO 检查更新
     */
    private void checkUpdate() {
        pgDialog.show();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("terminal", "10");
        map.put("schoolCode","public");
        Log.e("update","update="+ URLs.UPDATE+map);
        HTTPUtils.post(AboutAppActivity.this, URLs.UPDATE, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                if(s!=null){
                    checkVersion(s);
                    pgDialog.dismiss();
                }
            }
        });
    }

    /**
      * @todo 检测服务器上的最新的软件版本信息
     */
    private void checkVersion(String jsonData) {
//		if (DataUtil.isEmpty(jsonData))
//			return;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");
            if(code==1){
                Gson gson=new Gson();
                AppUpdateInfo appUpdateInfo = gson.fromJson(data, AppUpdateInfo.class);
                init(appUpdateInfo);
            }else{
                ToastUtil.showShort(AboutAppActivity.this, message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(AppUpdateInfo info) {
        UpdateUtil update = new UpdateUtil(this, info);
        update.setUpdateOnClickListener(new UpdateUtil.UpdateOnClicListener() {
            @Override
            public void update() {
            }

            @Override
            public void unUpdate() {
            }

            @Override
            public void downLoadok() {
            }
        });
        update.checkUpdate();
    }
    /* 更新进度条 */
    private ProgressBar mProgress;
    /* 更新进度值 */
    private TextView percentageTv;
    // 下载进度对话框
    private Dialog mDownloadDialog;
    /* 下载保存路径 */
    private String mSavePath;
    private UpdateUtil.UpdateOnClicListener updateOnClicListener;
    private String downFileName = "";// 下载的文件名
    /**
     *
     * @date 2015-3-4下午9:49:11
     * @todo 显示下载进度条
     */
    private void showDownloadDialog() {
        downFileName = "FJZQA" + info.getSysVer() + ".apk";
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutAppActivity.this);
        builder.setTitle("正在更新");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(AboutAppActivity.this);
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
                FileUtils.saveFileLocal(conn.getInputStream(), mSavePath
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
        startActivity(i);
    }
}
