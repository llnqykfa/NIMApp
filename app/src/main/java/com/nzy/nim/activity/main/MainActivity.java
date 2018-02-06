package com.nzy.nim.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.AppUpdateInfo;
import com.nzy.nim.db.tmpbean.NotificationBean;
import com.nzy.nim.fragment.ApplyFragment;
import com.nzy.nim.fragment.ContactsContainerFragment;
import com.nzy.nim.fragment.MessageFragment;
import com.nzy.nim.fragment.MySelfFragment;
import com.nzy.nim.helper.NotificationHelper;
import com.nzy.nim.manager.AppManager;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.FaceConversionUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ScreenUtils;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.tool.common.UpdateUtil;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.core.bootstrap.client.ClientManager;
import org.core.bootstrap.client.ClientOperation;
import org.core.bootstrap.client.CustomClient;
import org.core.bootstrap.client.MessageListener;
import org.core.bootstrap.property.Message;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;

/**
 * @classify(类别) 主界面
 * @TODO(功能) TODO
 * @Param(参数)
 * @Remark(备注)
 */
public class MainActivity extends BaseActivity implements OnClickListener {
    public static MainActivity mainActivity;

    private static final int MSG_CENTER = 0;// 社版界面
    private static final int MY_CONTACTS = 1;// 联系人界面
    private static final int APPLE = 2;// 工具界面
    private static final int ME = 3;// 个人中心

    private MessageFragment messageFragment;
    private ContactsContainerFragment containerFragment;
    private ApplyFragment mainFragment;
    private MySelfFragment mySelfFragment;
    private ImageView contactsTips;
    private ImageView meTips;
    private TextView msgTips;// 社版中心未读消息提示
    public static CustomClient customClient;
    private NewMessageBroadcastReceiver receiver;
    private BroadcastReceiver netWorkReceiver;
    private Vibrator vibrator;// 震动服务
    private boolean isPause;// 是否是暂停
    private boolean isDestory;// 是否被销毁
    private String uuid = "";
    private String userId = "";
    //    private DownLoadCompleteReceiver dowReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        initView();
        int screenWidth = ScreenUtils.getScreenWidth(this);
        ScreenUtils.SCREEN_WIDTH = screenWidth;
        ScreenUtils.SCREEN_HEIGHT = ScreenUtils.getScreenHeight(this);
        userId = QYApplication.getPersonId();
        UserInfo users = DataSupport.findLast(UserInfo.class);
        if (users == null) {
            finish();
            ToastUtil.showShort(this, "登入失败，请重新登入");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        initConfig();
        /**
         * 版本更新检测
         */
        if(AppManager.isUpdate){
            checkUpdate();
        }

        registerBoradcast();
        // 第一次启动时选中第0个tab
        setTabSelection(APPLE);
        if (QYApplication.getUri() != null) {
            QYUriMatcher.actionUri(this, QYApplication.getUri());
            QYApplication.getInstance().remove("uri");
        }
        QYApplication.getInstance().put("isRunning", true);
    }

    /**
     * @TODO 初始化一些配置
     */
    private void initConfig() {
        // 获取震动服务
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initClient();
        ClientManager.getInstance().start(customClient, new ClientOperation() {
            @Override
            public void restart() {
                initClient();
            }
        });
        // 添加表情包
        FaceConversionUtil.getInstace().getFileText(getApplication());
    }

    /**
     * @TODO 注册连接
     */
    private void initClient() {
        // 注册连接
        customClient = new CustomClient(true);
        customClient.rootFilePath = URLs.UPLOAD_URL + MyConstants.CHAT_PARAM;
        customClient.fileServerPath = MsgManager.SERVICE_FILE_DIR;
        customClient.register(MyConstants.MAILING_ADDRESS,
                MyConstants.MAILING_PORT, userId);
        customClient.addListener(new MessageListener() {
            @Override
            public void read(Message message) {
                MsgManager.getInstance().classifyMessage(message);
            }
        });
        customClient.init();
    }

    /**
     * @TODO 循环检测是否被迫下线
     */
    private void checkIsForceOffLine() {
        // 开启检测账号是否被其他设备登陆
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isLoginOut || !isNetAvailable || isDestory) {
                        break;
                    } else {
                        if (!DataUtil.isEmpty(uuid)
                                && !DataUtil.isEmpty(userId))
                            scanLoginOut(QYApplication.getContext(), userId,
                                    uuid);
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainActivity = this;
        isPause = false;
        reflushMsgCenterTips();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 注释掉这句话，防止应用未退出，清理内存后，页面重新初始化失败
        // super.onSaveInstanceState(outState);
    }

    private TextView tabs_message;
    private TextView tabs_contacts;
    private TextView tabs_circle;
    private TextView tabs_me;

    /**
     * @TODO TODO 关联布局中组件和添加监听事件
     * @Return void
     */
    private void initView() {
        msgTips = (TextView) findViewById(R.id.mian_message_tip);
        tabs_message = (TextView) findViewById(R.id.tabs_message);
        tabs_contacts = (TextView) findViewById(R.id.tabs_contacts);
        tabs_circle = (TextView) findViewById(R.id.tabs_circle);
        tabs_me = (TextView) findViewById(R.id.tabs_me);
        findViewById(R.id.main_message).setOnClickListener(this);
        findViewById(R.id.main_contacts).setOnClickListener(this);
        findViewById(R.id.main_apply).setOnClickListener(this);
        findViewById(R.id.main_mine).setOnClickListener(this);
        contactsTips = (ImageView) findViewById(R.id.main_contacts_tip);
        meTips = (ImageView) findViewById(R.id.main_me_tip);
    }

    @Override
    public void onClick(View view) {
        reseact();
        switch (view.getId()) {
            case R.id.main_message:
                Drawable icon_main_tab_message = getResources().getDrawable(R.mipmap.icon_main_tab_message_true);
                icon_main_tab_message.setBounds(0, 0, icon_main_tab_message.getMinimumWidth(), icon_main_tab_message.getMinimumHeight());
                tabs_message.setCompoundDrawables(null, icon_main_tab_message, null, null);
                tabs_message.setTextColor(getResources().getColor(R.color.tabs_true));
                setTabSelection(MSG_CENTER);
                break;
            case R.id.main_contacts:
                Drawable icon_main_tab_contacts = getResources().getDrawable(R.mipmap.icon_main_tab_contacts_true);
                icon_main_tab_contacts.setBounds(0, 0, icon_main_tab_contacts.getMinimumWidth(), icon_main_tab_contacts.getMinimumHeight());
                tabs_contacts.setCompoundDrawables(null, icon_main_tab_contacts, null, null);
                tabs_contacts.setTextColor(getResources().getColor(R.color.tabs_true));
                setTabSelection(MY_CONTACTS);
                break;
            case R.id.main_apply:
                Drawable icon_main_tab_circle3 = getResources().getDrawable(R.mipmap.icon_main_tab_circle3_true);
                icon_main_tab_circle3.setBounds(0, 0, icon_main_tab_circle3.getMinimumWidth(), icon_main_tab_circle3.getMinimumHeight());
                tabs_circle.setCompoundDrawables(null, icon_main_tab_circle3, null, null);
                tabs_circle.setTextColor(getResources().getColor(R.color.tabs_true));
                setTabSelection(APPLE);
                break;
            case R.id.main_mine:
                Drawable icon_main_tab_me = getResources().getDrawable(R.mipmap.icon_main_tab_me_true);
                icon_main_tab_me.setBounds(0, 0, icon_main_tab_me.getMinimumWidth(), icon_main_tab_me.getMinimumHeight());
                tabs_me.setCompoundDrawables(null, icon_main_tab_me, null, null);
                tabs_me.setTextColor(getResources().getColor(R.color.tabs_true));
                setTabSelection(ME);
                break;
        }
    }

    private void reseact() {
        Drawable icon_main_tab_message = getResources().getDrawable(R.mipmap.icon_main_tab_message);
        Drawable icon_main_tab_contacts = getResources().getDrawable(R.mipmap.icon_main_tab_contacts);
        Drawable icon_main_tab_circle3 = getResources().getDrawable(R.mipmap.icon_main_tab_circle3);
        Drawable icon_main_tab_me = getResources().getDrawable(R.mipmap.icon_main_tab_me);

        icon_main_tab_message.setBounds(0, 0, icon_main_tab_message.getMinimumWidth(), icon_main_tab_message.getMinimumHeight());
        tabs_message.setCompoundDrawables(null, icon_main_tab_message, null, null);
        tabs_message.setTextColor(getResources().getColor(R.color.tabs_false));

        icon_main_tab_contacts.setBounds(0, 0, icon_main_tab_contacts.getMinimumWidth(), icon_main_tab_contacts.getMinimumHeight());
        tabs_contacts.setCompoundDrawables(null, icon_main_tab_contacts, null, null);
        tabs_contacts.setTextColor(getResources().getColor(R.color.tabs_false));

        icon_main_tab_circle3.setBounds(0, 0, icon_main_tab_circle3.getMinimumWidth(), icon_main_tab_circle3.getMinimumHeight());
        tabs_circle.setCompoundDrawables(null, icon_main_tab_circle3, null, null);
        tabs_circle.setTextColor(getResources().getColor(R.color.tabs_false));

        icon_main_tab_me.setBounds(0, 0, icon_main_tab_me.getMinimumWidth(), icon_main_tab_me.getMinimumHeight());
        tabs_me.setCompoundDrawables(null, icon_main_tab_me, null, null);
        tabs_me.setTextColor(getResources().getColor(R.color.tabs_false));
    }


    /**
     * @TODO 注册广播
     */
    private void registerBoradcast() {
        receiver = new NewMessageBroadcastReceiver();
        netWorkReceiver = new NetWorkStateReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MsgManager.getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(receiver, intentFilter);
        // 注册网络状态检测
        registerReceiver(netWorkReceiver, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));

    }

    /**
     * @TODO 显示未读消息的提示
     */
    public void reflushMsgCenterTips() {
        // 未读消息统计
        int msgCenterUnReadNum = DBHelper.getInstance().getMsgCenterUnReadSum();
        if (msgCenterUnReadNum == 0) {
//			msgTips.hide();
            msgTips.setVisibility(View.INVISIBLE);
        } else if (msgCenterUnReadNum <= 99) {
            msgTips.setText(msgCenterUnReadNum + "");
//			msgTips.show();
            msgTips.setVisibility(View.VISIBLE);
        } else {
            msgTips.setText("99+");
//			msgTips.show();
            msgTips.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param context
     * @TODO TODO 从其他界面跳转到MainActivity界面
     * @Return void
     */
    public static void actionIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setTabSelection(MSG_CENTER);
    }


    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示书友圈，1表示社版，2表示我的好友。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case APPLE:// 应用
//			applyIndicator.setIconAlpha(1.0f);
                if (mainFragment == null) {
                    mainFragment = new ApplyFragment();
                    transaction.add(R.id.main_content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case MSG_CENTER:// 消息中心


//			messageIndicator.setIconAlpha(1.0f);
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.main_content, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                break;
            case MY_CONTACTS:// 交友
//			contactsIndicator.setIconAlpha(1.0f);
                if (containerFragment == null) {
                    containerFragment = new ContactsContainerFragment();
                    transaction.add(R.id.main_content, containerFragment);
                } else {
                    transaction.show(containerFragment);
                }
                break;
            case ME:
//			meIndicator.setIconAlpha(1.0f);
                if (mySelfFragment == null) {
                    mySelfFragment = new MySelfFragment();
                    transaction.add(R.id.main_content, mySelfFragment);
                } else {
                    transaction.show(mySelfFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (containerFragment != null) {
            transaction.hide(containerFragment);
        }
        if (mySelfFragment != null) {
            transaction.hide(mySelfFragment);
        }
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
//		messageIndicator.setIconAlpha(0.0f);
//		contactsIndicator.setIconAlpha(0.0f);
//		applyIndicator.setIconAlpha(0.0f);
//		meIndicator.setIconAlpha(0.0f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
        release();

    }

    /**
     * @TODO 释放资源
     */
    private void release() {
        if (customClient != null) {
            customClient.release();
            customClient.clear();
            customClient = null;
        }
        if (mainActivity != null) {
            mainActivity = null;
        }
        // 注销广播
        unregisterReceiver(receiver);
        unregisterReceiver(netWorkReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按下返回键时不移除任务
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 强制下线标志
    private boolean isLoginOut = false;
    // 网络是否可用
    private boolean isNetAvailable = true;

    /**
     * @TODO TODO 检测该账号是否在其他地方登陆
     * @Return void
     */
    @SuppressWarnings({"deprecation"})
    private void scanLoginOut(final Context context, final String userId,
                              final String uuid) {
        RequestParams params = new RequestParams();
        params.add("personId", userId);
        params.add("uuid", uuid);
        HttpUtil.syncPost(URLs.SCAN_LOGOUT, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (DataUtil.isOk(arg2)) {
//                            CrashReport.postCatchedException(new Throwable(
//                                    "personId:" + userId + "uuid:" + uuid));
                            // 被迫强制下线标志置为true
                            isLoginOut = true;
                            // 释放连接
                            customClient.release();
                            customClient.clear();
                            customClient = null;
                            // 发送强制下线广播
                            DataSupport.deleteAll(Users.class, "userid=?", QYApplication.getPersonId());
                            Intent intent = new Intent(MsgManager
                                    .getForceOffLineAction());
                            context.sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                    }
                });

    }

    /**
     * @TODO(功能)
     * @mark(备注)
     */
    class NewMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 截断广播
            abortBroadcast();
            NotificationBean bean = intent.getParcelableExtra("newMsgCom");
            reflushMsgCenterTips();
            // 社版中心不为空
            if (messageFragment != null) {
                messageFragment.refulsh();
                if (messageFragment.isHidden()
                        && MsgManager.FRIEND_BE_DELETED != bean.getMsgType()) {// 界面隐藏时震动
                    long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, -1); // 重复两次上面的pattern如果只想震动一次，index设为-1
                }
            }
            // if (containerFragment != null)// 刷新联系人列表
            // containerFragment.refresh();
            if (isPause && MsgManager.FRIEND_BE_DELETED != bean.getMsgType())// 当暂停时,通知有新消息到来
                NotificationHelper.getInstance().notifyUser(MainActivity.this,
                        bean);
        }

    }

    /**
     * @author Yi 下午1:05:57 TODO 网络状态检测 QuanYou
     */
    class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager conn = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = conn.getActiveNetworkInfo();
                if (activeInfo != null && activeInfo.isAvailable()) {
                    if (messageFragment != null) {
                        messageFragment.noNetworkHide();
                        isNetAvailable = true;
                    }
                } else {
                    if (messageFragment != null) {
                        messageFragment.noNetworkShow();
                        isNetAvailable = false;
                    }
                }
            }
        }
    }

    /**
     * @TODO 检查更新
     */
    private void checkUpdate() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("terminal", "10");
        map.put("schoolCode","public");
//        Log.e("update","update="+URLs.UPDATE+map);
        HTTPUtils.post(MainActivity.this, URLs.UPDATE, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                if(s!=null){
                    checkVersion(s);
//                checkVersion("{\"message\":\"尚未查询匹配的版本信息\",\"source\":null,\"data\":{\"sysVer\":\"46\",\"recordVer\":1,\"terminal\":\"01\",\"recordTimeStamp\":\"2016-07-19 15:40:30\",\"downloadUrl\":\"http://120.27.159.62:8000/app/release-v1.0.10.apk\",\"schoolCode\":\"10402\",\"verName\":\"1.0.11\",\"fixDescripe\":\"修复bug\",\"bizId\":\"101100000000000000000000000003\"},\"code\":\"1\",\"type\":0}");
                }
            }
        });
    }

    /**
     * @todo 检测服务器上的最新的软件版本信息
     */
    private void checkVersion(String jsonData) {
//        if (DataUtil.isEmpty(jsonData))
//            return;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");
            if(code==1){
                Gson gson=new Gson();
                AppUpdateInfo appUpdateInfo = gson.fromJson(data, AppUpdateInfo.class);
                init(appUpdateInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void init(AppUpdateInfo info) {
        UpdateUtil update = new UpdateUtil(MainActivity.this, info);
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
}
