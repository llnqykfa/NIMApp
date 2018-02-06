package com.nzy.nim.activity.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.BizConstants;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.PayHttpUtil;
import com.nzy.nim.volley.VolleyListener;
import com.nzy.nim.zxing.MipcaActivityCapture;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class PayWebviewActivity extends BaseActivity {
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int PAY_RESULT_NOTIFY = 999;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private WebView webView;
    private ImageView top_bar_next_iv;
    private TextView titleContent;
    private int QYCODE=1;
    private ProgressBar progressbar;
    private UserInfo user;
    private String urls;
    private String catalog;
    private boolean needPay = true;//需要支付功能
    final int QUERY_FAILE = 0;// 更新失败
    final int QUERY_SUCCESS = 1;// 更新成功
    private Map<String, String> orderMap = null;
    private boolean payEnableFlag = true;

    // Google浏览器客户端
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
                QYUriMatcher.actionUri(PayWebviewActivity.this,url);
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
            ToastUtil.showShort(PayWebviewActivity.this,"网络连接失败，请检查网络设置");
        }
    };

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
        clearWebViewCache();
        webView.destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        QYApplication.ACTIVITY_NAME="web页";
        user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
        initTopBar();
        Intent intent = getIntent();
        urls = intent.getStringExtra("url");
        catalog = intent.getStringExtra("catalog");
        needPay = intent.getBooleanExtra("needPay", true);
        if (needPay) {
            top_bar_next_iv.setVisibility(View.VISIBLE);
        }else{
            top_bar_next_iv.setVisibility(View.GONE);
        }
        webView = (WebView)findViewById(R.id.qy_web_view);
        progressbar = (ProgressBar) findViewById(R.id.web_progress);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        String uaAttch = " Fjzq/";
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
        //不使用缓存：
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setHorizontalScrollbarOverlay(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setDownloadListener(new MyWebViewDownLoadListener());//实现文件下载功能
        webView.requestFocus();
        webView.addJavascriptInterface(new InJavaScript(), "qyandroid");//将一个java对象和网页JS联系起来。
        webView.loadUrl(urls);
    }

    private void initTopBar() {
        titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        top_bar_next_iv = (ImageView) findViewById(R.id.top_bar_next_iv);
        top_bar_next_iv.setImageResource(R.drawable.settle_no);
        top_bar_next_iv.setVisibility(View.VISIBLE);
        titleContent.setText("正在加载");
        next.setText("");
        next.setVisibility(View.VISIBLE);
        top_bar_next_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!payEnableFlag) {
                    return;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("catalog",catalog);
                PayHttpUtil.postWithToken(PayWebviewActivity.this, BizConstants.BIZSERVER_ADDRESS + BizConstants.PAY_NATIVE_URL, map, new VolleyListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        orderMap = null;
                        Message msg = new Message();
                        msg.what = QUERY_FAILE;
                        Bundle bundle = new Bundle();
                        bundle.putString("errmsg","支付查询失败");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObj = new JSONObject(s);
                            if (jsonObj.getInt("errcode") == 0) {
                                if (!jsonObj.getString("find").equals("1")) {
                                    orderMap = null;
                                    Message msg = new Message();
                                    msg.what = QUERY_FAILE;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("errmsg","未填写注册信息");
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }else if (!jsonObj.getString("pay").equals("0")) {
                                    orderMap = null;
                                    Message msg = new Message();
                                    msg.what = QUERY_FAILE;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("errmsg","注册信息已付费");
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }else{
                                    orderMap = new HashMap<String, String>();
                                    orderMap.put("find", jsonObj.getString("find"));
                                    orderMap.put("pay", jsonObj.getString("pay"));
                                    orderMap.put("id", jsonObj.getString("id"));
                                    orderMap.put("rname", jsonObj.getString("rname"));
                                    orderMap.put("cname", jsonObj.getString("cname"));
                                    orderMap.put("fee", jsonObj.getString("fee"));
                                    handler.sendEmptyMessage(QUERY_SUCCESS);
                                }
                            }else{
                                orderMap = null;
                                Message msg = new Message();
                                msg.what = QUERY_FAILE;
                                Bundle bundle = new Bundle();
                                bundle.putString("errmsg",jsonObj.getString("errmsg"));
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }catch(Exception ex){
                            orderMap = null;
                            Message msg = new Message();
                            msg.what = QUERY_FAILE;
                            Bundle bundle = new Bundle();
                            bundle.putString("errmsg","支付查询失败");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                });
            }
        });
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class InJavaScript {
        @JavascriptInterface
        public String qrCode(){//扫二维码签到
            Intent intent = new Intent(PayWebviewActivity.this, MipcaActivityCapture.class);
            startActivityForResult(intent,QYCODE);
            return "qyandroid";
        }

        @JavascriptInterface
        public void payEnable(final String status) {
           try
           {
               if (status.equals("1")) {
                   payEnableFlag = true;
                   top_bar_next_iv.setImageResource(R.drawable.settle_no);
               }else{
                   payEnableFlag = false;
                   top_bar_next_iv.setImageResource(R.drawable.settle_done);
               };
           }catch(Exception ex){
           }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 支付结果判断
         * */
        if (requestCode == 999) {
            if (resultCode==1) {
                webView.reload();
            }
            return;
        }
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
     * 加载页面初始化
     */
    public static void loadUrl(Context context, String url, String catalog, boolean needPay)
    {
        Intent intent = new Intent(context,PayWebviewActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("catalog", catalog);
        intent.putExtra("needPay", needPay);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
    }

    @Override
    protected void onPause() {
//        if(!urls.equals("http://222.205.160.22/zuqiu/")){
//            webView.reload();
//        }
        super.onPause();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case QUERY_FAILE:
                    String errmsg = msg.getData().getString("errmsg");
                    ToastUtil.show(PayWebviewActivity.this, errmsg,
                            Gravity.TOP);
                    break;
                case QUERY_SUCCESS:
                    SettleAcctActivity.actionIntent(PayWebviewActivity.this, orderMap.get("id"),
                            orderMap.get("rname"), catalog, orderMap.get("cname"), orderMap.get("fee"));
                    break;
            }
        }
        ;
    };

    /**
     * 清除WebView缓存  在onDestroy调用这个方法就可以了
     */
    public void clearWebViewCache(){
        //清理Webview缓存数据库
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);
    }
}
