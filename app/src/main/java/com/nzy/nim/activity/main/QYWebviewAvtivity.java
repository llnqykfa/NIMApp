package com.nzy.nim.activity.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;
import com.nzy.nim.zxing.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 16/1/13.
 */

public class QYWebviewAvtivity extends BaseActivity {
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private WebView webView;
    private ImageView top_bar_next_iv;
    private TextView titleContent;
    private int QYCODE=1;
    private ProgressBar progressbar;
//    private ArrayList<String> loadHistoryUrls = new ArrayList<String>();

    WebChromeClient wvcc = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            setTitle(title);
            titleContent.setText(title);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//            return super.onConsoleMessage(consoleMessage);
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.cancel();
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                if (progressbar.getVisibility() == View.GONE)
                    progressbar.setVisibility(View.VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        /**
         * 2016-11-28修改  弹出input type="file
         */
        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
        {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
        }


        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
        {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try
            {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e)
            {
                uploadMessage = null;
                Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        //For Android 4.1 only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
        {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg)
        {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
    };

    // 创建WebViewClient对象
    WebViewClient wvc = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("qy://"))
            {
                QYUriMatcher.actionUri(QYWebviewAvtivity.this,url);
            }else
            {
                webView.loadUrl(url);
            }
            return true;
        }
        /**
         * 没网络，网络加载失败
         */
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadUrl("");
            ToastUtil.showShort(QYWebviewAvtivity.this,"网络连接失败，请检查网络设置");
        }
    };
    private UserInfo user;
    private String urls;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(webView.canGoBack()){
                webView.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    private void initTopBar() {
        titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        top_bar_next_iv = (ImageView) findViewById(R.id.top_bar_next_iv);
        top_bar_next_iv.setVisibility(View.INVISIBLE);
        titleContent.setText("正在加载");
        next.setVisibility(View.GONE);
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        QYApplication.ACTIVITY_NAME="web页";
        user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
        initTopBar();
        urls = getIntent().getStringExtra("url");
        webView = (WebView)findViewById(R.id.qy_web_view);
        progressbar = (ProgressBar) findViewById(R.id.web_progress);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        String uaAttch = " QuanYou/";
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            uaAttch += pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            uaAttch += "1.0.0";
        }
        setting.setUserAgentString(setting.getUserAgentString() + uaAttch);
        webView.setWebChromeClient(wvcc);
        webView.setWebViewClient(wvc);
        setting.setJavaScriptEnabled(true);//设置WebView支持JavaScript
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setHorizontalScrollbarOverlay(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setDownloadListener(new MyWebViewDownLoadListener());//实现文件下载功能
        webView.requestFocus();
        webView.addJavascriptInterface(new InJavaScript(), "qyandroid");//将一个java对象和网页JS联系起来。
        webView.loadUrl(urls);
    }
     class InJavaScript {
         @JavascriptInterface
        public String qrCode(){//扫二维码签到
//            ToastUtil.showShort(QYWebviewAvtivity.this,"扫一扫");
             Intent intent = new Intent(QYWebviewAvtivity.this, MipcaActivityCapture.class);
//             intent.putExtra("QD_CODE",true);
             startActivityForResult(intent,QYCODE);
            return "qyandroid";
        }
//         @JavascriptInterface
//         public String ringInfo(String json){//进组圈详情
//             RingTeamInfoActivity.actionIntent(QYWebviewAvtivity.this, json);
//             return "qyandroid";
//         }
//         @JavascriptInterface
//         public  String studentInfo(String json){//保存新生信息
//             Gson gson = new Gson();
//             NewStudentMess mess = gson.fromJson(json, NewStudentMess.class);
//             DataSupport.deleteAll(NewStudentMess.class);
//             NewStudentMess newStudentMess=new NewStudentMess();
//             newStudentMess.setStudentName(mess.getStudentName());
//             newStudentMess.setRegistCode(mess.getRegistCode());
//             newStudentMess.setNoticeCode(mess.getNoticeCode());
//             newStudentMess.setRegistPhone(user.getPhone());
//             if(newStudentMess.save()){
//                 ToastUtil.showShort(QYWebviewAvtivity.this,"信息保存成功");
//             }else{
//                 ToastUtil.showShort(QYWebviewAvtivity.this,"信息保存失败");
//             }
////             newStudentMess.saveThrows();
//             return "qyandroid";
//         }
//         @JavascriptInterface
//         public String putPhone(){//传手机号给H5端
//             webView.loadUrl("javascript:putPhoneCallback('"+user.getPhone()+"')");
//             return "qyandroid";
//         }
    }

    /**
     * 实现文件下载功能
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
    public static void loadUrl(Context context,String url)
    {
        Intent intent = new Intent(context,QYWebviewAvtivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 2016-11-28修改  弹出input type="file
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessage = null;
            }
        }else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

    }
    /**
     * 专题会议报道
     */
    private void projectSign(String specMeetingId){
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("metId",specMeetingId);
        hashMap.put("operType","3");
        hashMap.put("mobile",user.getPhone());
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(hashMap);
        String toJson = new Gson().toJson(list);
        String bizParms = toJson.substring(1, toJson.length() - 1);
        HashMap<String, String> newMap = new HashMap<String, String>();
        newMap.put("bizParms",bizParms);
        newMap.put("phone",user.getPhone());
        Log.e("newMap=",bizParms+user.getPhone());
        HTTPUtils.post(QYWebviewAvtivity.this, URLs.MEETING_SIGN, newMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(QYWebviewAvtivity.this, R.string.server_is_busy, Gravity.TOP);
            }

            @Override
            public void onResponse(String s) {
                if (s != null) {
                    JSONObject json = null;
                    try {
                        json=new JSONObject(s);
                        int errcode = json.getInt("errcode");
                        String errmsg = json.getString("errmsg");
                        if(errcode==0){
                            webView.loadUrl("javascript:qrCodeMeetingCallback('"+1+"')");
                            ToastUtil.showShort(QYWebviewAvtivity.this,"报到成功");
                        }else{
                            webView.loadUrl("javascript:qrCodeMeetingCallback('"+0+"')");
                            ToastUtil.showShort(QYWebviewAvtivity.this,errmsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
//        if(!urls.equals("http://222.205.160.22/zuqiu/")){
//            webView.reload();
//        }
        super.onPause();
    }
}
