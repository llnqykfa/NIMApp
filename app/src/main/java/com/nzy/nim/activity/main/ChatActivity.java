
package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.ChatMsgViewAdapter;
import com.nzy.nim.adapter.FaceAdapter;
import com.nzy.nim.adapter.ViewPagerAdapter;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.FaceImage;
import com.nzy.nim.db.bean.ChatEmoji;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.db.tmpbean.NotificationBean;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.helper.NotificationHelper;
import com.nzy.nim.manager.GridViewGallery;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.CommonUtil;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.FaceConversionUtil;
import com.nzy.nim.tool.common.FaceHelper;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageCache;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.FloatingView;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.core.bootstrap.MessageType;
import org.core.bootstrap.client.ResultOperation;
import org.core.bootstrap.property.Header;
import org.core.bootstrap.property.Message;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author(作者) LIUBO
 * @Date(日期) 2015-2-10 下午7:24:03
 * @classify(类别)
 * @TODO(功能) TODO 聊天界面
 * @Param(参数)
 * @Remark(备注)
 */
public class ChatActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, ChatMsgViewAdapter.OnRepeatListener, PullToRefreshBase.OnRefreshListener<ListView> {
    // handle
    private static final int REFRESH_NEW_MSG_COM = 1;// 新消息到来
    private static final int REFRESH_LOAD_MORE = 2;// 加载更多消息记录
    // 请求码
    public static final int REQUEST_CODE_LOCAL = 0;// 选择本地图片
    public static final int REQUEST_CODE_SELECT_FILE = 1;// 选择本地文件
    public static final int REQUEST_CODE_CAMERA = 2;// 照片
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;// 菜单
    public static final int REQUEST_REFRESH = 4;// 刷新

    // 返回码
    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_SAVEPIC = 8;

    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD_PIC = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;
    // 聊天对象标题
    private TextView mTitle;
    private TextView mBtnMore;
    private RelativeLayout topContainer;
    // 编辑内容
    private EditText mEditTextContent;
    // 显示聊天消息
    private ListView mListView;
    private PullToRefreshListView pullListView;
    private ClipboardManager clipborad;
    // 评论按钮
    private FloatingView floatingView;
    private View fView;// 评论按钮的布局
    private LinearLayout fContainer;
    private ImageView fIcon;
    private TextView fContent;

    private File cameraFile = null;
    // 聊天消息适配器
    private ChatMsgViewAdapter mAdapter;
    // 消息数据存储
    private List<ChatRecord> mDataArrays = new ArrayList<ChatRecord>();
    // 数据指针(从数据库拉取信息)
    private int i = 0;
    // 聊天对象的信息
    private String title = "";// 聊天的title
    private boolean isGroup = false;
    private String forwardMsgId = "";// 转发消息的Id
    /* 新消息到来广播 */
    private NewMessageBroadcastReceiver receiver;
    /* 文件下载改变广播 */
    private FileDownBroadcastReceiver fileDownReceiver;
    // 聊天对象的Id(单聊为给人Id,群聊为qunId)
    private String mcontactId = null;
    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;
    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;
    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;
    private LinearLayout mLayout;
    private GridViewGallery mGallery;//存放布局的视图容器
    private List<FaceImage> list;//数据
    /**
     * 表情界面
     */
    private View viewFace;
    /**
     * 功能界面
     */
    private View viewFunction;
    private Button emoBtn, sendBtn;// 功能按钮和发送按钮
    private ImageView faceBtnNor, faceBtnPre;// 表情按钮
    private LinearLayout more;// 更多功能页
    /**
     * 表情数据填充器
     */
    private ArrayList<FaceAdapter> faceAdapters;
    int current = 0;
    private boolean hasMoreData = true;
    int pageSize = 20;
    private ProgressDialog pgDialog;
    private String[] memberArr;
    private FinishBrocasd finishBrocasd;
    private ShareMsgInfo shareMsgInfo;
    public static ChatActivity instance;
    private RelativeLayout rel_face;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_interface);
        instance=this;
        QYApplication.ACTIVITY_NAME="聊天页";
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("take_photo");
            if (path != null)
                cameraFile = new File(path);
        }
        // 获取聊天对象的Id
        Intent intent = getIntent();
        mcontactId = intent.getStringExtra("contactId");
        isGroup = intent.getBooleanExtra("isGroup", false);
        title = intent.getStringExtra("title");
        forwardMsgId = intent.getStringExtra("forwardMsgId");
        shareMsgInfo = (ShareMsgInfo) getIntent().getSerializableExtra("shareMsgInfo");
        pgDialog = DialogHelper.getSDialog(ChatActivity.this, "保存中···", false);
        list = getData();
        // 关联布局中组件
        initView();
        // 初始化表情和功能显示数据
        createFaceAndFunction();
        // 注册广播
        registeBoradcast();
        setUpView();
        initFloatBtn();
//        initMemberListFromLocal();
        initMemberList();
    }


    /**
     * 初始化Ring成員列表
     */
    private void initMemberList() {
        RequestParams params = new RequestParams();
        params.add("ringId", mcontactId);
        HttpUtil.post(URLs.LIST_BY_RINGID, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, org.apache.http.Header[] arg1, String arg2) {
                        if (arg2 != null) {
                            List<OldPersonVO> PersonIds = JSON.parseArray(arg2, OldPersonVO.class);
                            List<String> memberIds = CommonUtil.getPersonIds(PersonIds);
                            memberIds.remove(QYApplication.getPersonId());
                            memberArr = new String[memberIds.size()];
                            for (int i = 0; i < memberIds.size(); i++)
                                memberArr[i] = memberIds.get(i);

                            if (forwardMsgId != null) {
                                if (!forwardMsgId.endsWith(".jpg")) {
                                    forwardMsg(forwardMsgId, memberArr);
                                } else {
                                    shareMsg(forwardMsgId);
                                }
                            }
                            //用于分享
                            if (shareMsgInfo != null) {
                                String s = new Gson().toJson(shareMsgInfo);
                                commonSendAssess(MessageType.SHARE.value(), s, memberArr, "", null);
                            }

                        }
                    }

                    @Override
                    public void onFailure(int arg0, org.apache.http.Header[] arg1, String arg2,
                                          Throwable arg3) {

//						List<GroupMembers> members = DBHelper.getInstance().findAll(
//								GroupMembers.class, "groupid=?", mcontactId);
//						List<String> memberIds = CommonUtil.getMemberIds(members);
//						memberIds.remove(QYApplication.getPersonId());
//						memberArr = new String[memberIds.size()];
//						for (int i = 0; i < memberIds.size(); i++)
//							memberArr[i] = memberIds.get(i);
                    }
                });
    }

//    private void initMemberListFromLocal() {
//        List<GroupMembers> members = DBHelper.getInstance().findAll(
//                GroupMembers.class, "groupid=?", mcontactId);
//        List<String> memberIds = CommonUtil.getMemberIds(members);
//        memberIds.remove(QYApplication.getPersonId());
//        memberArr = new String[memberIds.size()];
//        for (int i = 0; i < memberIds.size(); i++)
//            memberArr[i] = memberIds.get(i);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        initFloatDatas();
        ringDelFinish();
    }

    private void ringDelFinish() {
        finishBrocasd = new FinishBrocasd();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Exitmygroups");
        registerReceiver(finishBrocasd, intentFilter);
    }

    class FinishBrocasd extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private void registeBoradcast() {
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MsgManager.getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
        // 注册文件已下载监听广播
        fileDownReceiver = new FileDownBroadcastReceiver();
        registerReceiver(fileDownReceiver,
                new IntentFilter(MsgManager.getFileDownBroadcastAction()));
    }

    /**
     * @Author liubo
     * @date 2015-3-11下午5:17:35
     * @TODO(功能) 初始化界面数据
     * @mark(备注)
     */
    @SuppressWarnings("deprecation")
    private void setUpView() {
        mListView = pullListView.getRefreshableView();
        mListView.setSelector(new BitmapDrawable());
        mListView.setDivider(null);
        QYApplication.initPullRefulsh(pullListView, this, false, false, null);
        mTitle.setText(title);
        // 初始化是否是组圈的标志
        if (isGroup) {
            initGroupChat(mcontactId);
            mBtnMore.setVisibility(View.VISIBLE);
            mBtnMore.setText("组圈");
        } else {
            initSingleChat(mcontactId);
            mBtnMore.setVisibility(View.GONE);
        }

        if (mAdapter == null)
            mAdapter = new ChatMsgViewAdapter(this, mDataArrays, isGroup,
                    mcontactId, title);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mListView.getCount() - 1);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        // 设置重发的监听事件
        mAdapter.setOnRepeatListener(this);
        // 点击外边隐藏软键盘
        mListView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                CommonUtil.hideKeyboard(ChatActivity.this);
                more.setVisibility(View.GONE);
                // 隐藏发送按钮
                faceBtnNor.setVisibility(View.VISIBLE);
                faceBtnPre.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * @author 刘波
     * @date 2015-3-1下午7:20:18
     * @todo 初始化表情和功能显示数据
     */
    private void createFaceAndFunction() {
        // 初始化表情页面
        FaceHelper fh = FaceHelper.getInstance();
        faceAdapters = fh.initFaceAdapter(this);
        ArrayList<View> pageViews = fh.Init_viewPager(this,
                fh.initGridView(this, faceAdapters, this));
        // 初始化游标指针
        ArrayList<ImageView> pointViews = fh.Init_Point(this, pageViews,
                layout_point);
        // 初始化表情数据
        Init_Data(vp_face, pointViews, pageViews);
    }

    /**
     * 填充表情数据
     */
    public void Init_Data(final ViewPager vp_face,
                          final List<ImageView> pointViews, List<View> pageViews) {
        if (vp_face != null) {
            vp_face.setAdapter(new ViewPagerAdapter(pageViews));
            vp_face.setCurrentItem(1);
            current = 0;
            vp_face.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    current = arg0 - 1;
                    // 描绘分页点
                    FaceHelper.getInstance().draw_Point(arg0, pointViews);
                    // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                    if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                        if (arg0 == 0) {
                            vp_face.setCurrentItem(arg0 + 1);// 第二屏
                            // 会再次实现该回调方法实现跳转.
                            pointViews.get(1).setBackgroundResource(
                                    R.drawable.d2);
                        } else {
                            vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                            pointViews.get(arg0 - 1).setBackgroundResource(
                                    R.drawable.d2);
                        }
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }
    }

    /**
     * @param contactId
     * @Author LIUBO
     * @TODO TODO 初始化群聊数据
     * @Date 2015-2-10
     * @Return void
     */
    private void initGroupChat(String contactId) {
        mDataArrays.clear();
        mDataArrays.addAll(DBHelper.getInstance().getRecords(
                QYApplication.getPersonId(), contactId, 0, pageSize, true));
    }

    /**
     * 分享图片信息
     *
     * @param msgId
     */
    private void shareMsg(String msgId) {
        sendPicByUri(Uri.fromFile(new File(msgId)));
    }

    /**
     * 转发消息
     *
     * @param msgId
     */
    private void forwardMsg(String msgId, String[] memberArr) {
        String groupName = "";
        String filePath = "";
        File file = null;
        ChatRecord chat = DBHelper.getInstance().find(ChatRecord.class,
                "msgid=?", msgId);
        if (isGroup)
            groupName = title;
        if (chat.isCom()) {// 转发的信息为别人发的时
            filePath = chat.getRemoteFilePath();
        } else {
            filePath = chat.getLocalFilePath();
            if (chat.getMsgType() == MsgManager.PICTURE_TYPE) {
                if (!DataUtil.isEmpty(filePath))
                    file = new File(filePath.substring(7));
            } else {
                if (!DataUtil.isEmpty(filePath))
                    file = new File(filePath);
            }

        }
        Message msg = MsgManager.getInstance().packageForwardMsg(chat, memberArr,
                mcontactId, groupName, isGroup, filePath);
        ChatRecord sendRecord = null;
        sendRecord = DBConversion.getInstance().getChatMsg(msg, false,
                chat.getContent(), filePath);
        sendRecord.saveThrows();
        mDataArrays.add(sendRecord);
        handler.sendEmptyMessage(REFRESH_NEW_MSG_COM);
        sendToService(msg, file);
    }

    /**
     * @param contactId
     * @Author LIUBO
     * @TODO TODO 初始化单聊的数据
     * @Date 2015-2-10
     * @Return void
     */
    private void initSingleChat(String contactId) {
        mDataArrays.clear();
        mDataArrays.addAll(DBHelper.getInstance().getRecords(
                QYApplication.getPersonId(), contactId, 0, pageSize, false));

    }

    /**
     * @Author LIUBO
     * @TODO TODO 关联布局中的组件
     * @Date 2015-1-21
     * @Return void
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        clipborad = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        findViewById(R.id.top_back_bg).setOnClickListener(this);
        topContainer = (RelativeLayout) findViewById(R.id.chat_interface_rl_top_bar);
        // 聊天对象的详细信息
        mBtnMore = (Button) this.findViewById(R.id.top_bar_next);
        mBtnMore.setOnClickListener(this);
        // 消息列表
        pullListView = (PullToRefreshListView) findViewById(R.id.chat_interface_lv);
        // 功能按钮
        emoBtn = (Button) findViewById(R.id.custom_face_btn_emo);
        // 发送按钮
        sendBtn = (Button) findViewById(R.id.custom_face_btn_send);
        // 表情正常按钮
        faceBtnNor = (ImageView) findViewById(R.id.custom_face_btn_face_normal);
        faceBtnPre = (ImageView) findViewById(R.id.custom_face_btn_face_press);
        // 表情按下的按钮
        // 聊天对象title
        mTitle = (TextView) findViewById(R.id.top_bar_content);
        more = (LinearLayout) findViewById(R.id.custom_face_more);
        vp_face = (ViewPager) findViewById(R.id.custom_face_vp_contains);
        layout_point = (LinearLayout) findViewById(R.id.custom_face_iv_image);
        rel_face = (RelativeLayout) findViewById(R.id.rel_face);
        mGallery = new GridViewGallery(ChatActivity.this, list);
        mLayout = (LinearLayout) findViewById(R.id.ll_gallery);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        mLayout.addView(mGallery, params);
        // 表情的显示页面
        viewFace = findViewById(R.id.custom_face_ll_facechoose);
        // 功能显示页面
        viewFunction = findViewById(R.id.custom_face_ll_functionchoose);
        // 编辑框
        mEditTextContent = (EditText) findViewById(R.id.custom_face_et_content);
        findViewById(R.id.image_face).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFace.setVisibility(View.VISIBLE);
                mLayout.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.image_pic).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFace.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);
            }
        });

        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.GONE);
                rel_face.setVisibility(View.GONE);
                viewFunction.setVisibility(View.GONE);
                // 隐藏发送按钮
                faceBtnNor.setVisibility(View.VISIBLE);
                faceBtnPre.setVisibility(View.GONE);

            }
        });
        // 文本输入框的文本变化事件
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    sendBtn.setVisibility(View.VISIBLE);
                    emoBtn.setVisibility(View.GONE);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    emoBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public List<FaceImage> getData(){
        list = new ArrayList<FaceImage>();
        List<FaceImage> all = DataSupport.findAll(FaceImage.class);
        list.addAll(all);
        for(int i= 0;i<list.size();i++){
            final FaceImage faceImage = list.get(i);
            faceImage.setOnClickListener(new FaceImage.onGridViewItemClickListener(){

                @Override
                public void ongvItemClickListener(int position) {
                    commonSendAssess(MessageType.PICTURE_MESSAGE.value(), "", faceImage.getFaceImage(), null);
                }});
        }
        return list;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_face_btn_send: // 发送消息
                sendText();
                break;
            case R.id.top_back_bg:
                this.finish();
                break;
            case R.id.top_bar_next:// 更多
                if (isGroup)
                    RingTeamInfoActivity
                            .actionIntent(ChatActivity.this, mcontactId);
                break;
            // 发送拍照图片
            case R.id.custom_face_take_pic:
                selectPicFromCamera();
                break;
            // 点击上传本地图片
            case R.id.custom_face_select_pic_local:
                // 从本地选择图片
                selectPicFromLocal();
                break;
            // 点击上传文件
            case R.id.custom_face_select_file_local:
                // 从本地选择文件
                selectFileFromLocal();
                break;
            // 点击正常表情按钮
            case R.id.custom_face_btn_face_normal:
                CommonUtil.hideKeyboard(this, v);// 隐藏软键盘
                viewFunction.setVisibility(View.GONE);// 隐藏功能框
                more.setVisibility(View.VISIBLE);
                rel_face.setVisibility(View.VISIBLE);
                faceBtnNor.setVisibility(View.GONE);
                faceBtnPre.setVisibility(View.VISIBLE);
                break;
            case R.id.custom_face_btn_face_press:
                more.setVisibility(View.GONE);
                faceBtnNor.setVisibility(View.VISIBLE);
                faceBtnPre.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    /*
     * 处理从其他页面返回的数据
     */
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CAMERA == requestCode && cameraFile != null
                && cameraFile.exists()) {// 发送拍照图片
            commonSendAssess(MessageType.PICTURE_MESSAGE.value(), "",
                    cameraFile.getAbsolutePath(), null);
        }
        if (REQUEST_CODE_LOCAL == requestCode && data != null) {// 发送本地图片
            sendPicByUri(data.getData());
            return;
        }
        if (REQUEST_CODE_SELECT_FILE == requestCode && data != null) {// 发送本地文件
            sendFile(data.getData());
            return;
        }
        if (REQUEST_REFRESH == requestCode) {// 刷新
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (REQUEST_CODE_CONTEXT_MENU == requestCode && data != null) {// 长按内容显示菜单
            ChatRecord chat = (ChatRecord) mAdapter.getItem(data.getIntExtra(
                    "position", -1));
            switch (resultCode) {
                case RESULT_CODE_COPY:// 复制
                    clipborad.setPrimaryClip(ClipData.newPlainText(null,
                            chat.getContent()));
                    break;
                case RESULT_CODE_DELETE:// 删除
                    mDataArrays.remove(data.getIntExtra("position", -1));
                    DBHelper.getInstance().delete(ChatRecord.class, "msgid=?",
                            chat.getMsgId());
                    mAdapter.notifyDataSetChanged();
                    break;
                case RESULT_CODE_FORWARD:// 转发
                    ForwardMessageActivity.actionIntent(this, chat.getMsgId());
                    break;
                case RESULT_CODE_SAVEPIC:
                    if (chat.getRemoteFilePath().contains("http")) {
//					savePic(chat.getRemoteFilePath());
                        new MYTask().execute(chat.getRemoteFilePath());
                    }
                case RESULT_CODE_DWONLOAD_PIC:
                    if (!DataUtil.isEmpty(chat.getRemoteFilePath())) {
                        ToastUtil.showShort(this, chat.getRemoteFilePath());
                        Bitmap bitmap = ImageCache.getInstance().get(chat.getRemoteFilePath());
                        ImageUtil.saveImageToGallery(this, bitmap, MyConstants.BASE_DIR);
                    }else if (!DataUtil.isEmpty(chat.getLocalFilePath())){
                        ToastUtil.showShort(this, chat.getRemoteFilePath());
                        Bitmap bitmap = ImageCache.getInstance().get(chat.getLocalFilePath());
                        ImageUtil.saveImageToGallery(this, bitmap, MyConstants.BASE_DIR);
                    }
                    break;

            }

        }
    }


    public class MYTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 表示任务执行之前的操作
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pgDialog.show();
        }

        /**
         * 主要是完成耗时的操作
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // 使用网络连接类HttpClient类王城对网络数据的提取
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(arg0[0]);
            Bitmap bitmap = null;
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    byte[] data = EntityUtils.toByteArray(httpEntity);
                    bitmap = BitmapFactory
                            .decodeByteArray(data, 0, data.length);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return bitmap;
        }

        /**
         * 主要是更新UI的操作
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            saveImageToGallery(ChatActivity.this, result);
            pgDialog.dismiss();
        }

    }

    private void savePic(String imageUrl) {


        //httpGet连接对象
        HttpGet httpRequest = new HttpGet(imageUrl);
        //取得HttpClient 对象
        HttpClient httpclient = new DefaultHttpClient();
        try {
            //请求httpClient ，取得HttpRestponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //取得相关信息 取得HttpEntiy
                HttpEntity httpEntity = httpResponse.getEntity();
                //获得一个输入流
                InputStream is = httpEntity.getContent();
                System.out.println(is.available());
                System.out.println("Get, Yes!");
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                saveImageToGallery(ChatActivity.this, bitmap);
                is.close();

            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(),
                "Msoso");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())));
    }


    /**
     * @author 刘波
     * @date 2015-3-1下午3:37:14
     * @todo 从本地图库获取图片
     */
    private void selectPicFromLocal() {
        startActivityForResult(ImageUtil.getSelectPicIntent(),
                REQUEST_CODE_LOCAL);
    }

    /**
     * @Author liubo
     * @date 2015-3-16上午9:31:04
     * @TODO(功能)从相册中选择照片
     * @mark(备注)
     */
    private void selectPicFromCamera() {
        if (!CommonUtil.isExitsSdcard()) {
            Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = FileUtils.createFile(CommonUtil.getSDPath()
                + MyConstants.CAMERA_DIR + System.currentTimeMillis() + ".jpg");
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (cameraFile != null)
            outState.putString("take_photo", cameraFile.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    /**
     * @param selectedImage
     * @author 刘波
     * @date 2015-3-1下午4:17:05
     * @todo 根据图库图片uri发送图片
     */
    private void sendPicByUri(Uri selectedImage) {
        String path = "";
        Cursor cursor = getContentResolver().query(selectedImage, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            path = picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            path = file.getAbsolutePath();
        }
        commonSendAssess(MessageType.PICTURE_MESSAGE.value(), "", path, null);
    }

    /**
     * @param msgType  消息类型
     * @param content  消息内容
     * @param filePath 文件路径
     * @param fileInfo 文件信息
     * @author LIUBO
     * @date 2015-3-24上午12:00:17
     * @TODO 消息的发送出口
     */
    private void commonSendAssess(byte msgType, String content, String[] memberArr,
                                  String filePath, String fileInfo) {
        Header header = new Header();
        header.setType(msgType);
//		if (isGroup)// 如果是群消息设置优先级
//		{	header.setPriority(MessageType.RING_FORWORD.value());
//		}
        Message msg = packageMsgAndSaveLocal(header, content, memberArr, filePath,
                fileInfo);
        if (msgType == MessageType.TXT_MESSAGE.value())// 文本消息
            sendToService(msg);
        else if (msgType == MessageType.PICTURE_MESSAGE.value()) {// 图片
            sendToService(msg,
                    new File(ImageUtil.getCompressImgPath(filePath, 480, 800)));
        } else if (msgType == MessageType.FILE_MESSAGE.value()) {// 文件
            sendToService(msg, new File(filePath));
        } else if (msgType == MessageType.SHARE.value()) {
            sendToService(msg);
        }
    }

    /**
     * @param msgType  消息类型
     * @param content  消息内容
     * @param filePath 文件路径
     * @param fileInfo 文件信息
     * @author LIUBO
     * @date 2015-3-24上午12:00:17
     * @TODO 消息的发送出口
     */
    private void commonSendAssess(byte msgType, String content,
                                  String filePath, String fileInfo) {
        Header header = new Header();
        header.setType(msgType);
//		if (isGroup)// 如果是群消息设置优先级
//		{	header.setPriority(MessageType.RING_FORWORD.value());
//		}
        Message msg = packageMsgAndSaveLocal(header, content, memberArr, filePath,
                fileInfo);
        if (msgType == MessageType.TXT_MESSAGE.value())// 文本消息
            sendToService(msg);
        else if (msgType == MessageType.PICTURE_MESSAGE.value()) {// 图片
            sendToService(msg,
                    new File(ImageUtil.getCompressImgPath(filePath, 480, 800)));
        } else if (msgType == MessageType.FILE_MESSAGE.value()) {// 文件
            sendToService(msg, new File(filePath));
        } else if (msgType == MessageType.SHARE.value()) {
            sendToService(msg);
        }
    }

    /**
     * @author 刘波
     * @date 2015-3-1下午4:12:56
     * @todo 选择本地文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * @param uri
     * @Author liubo
     * @date 2015-3-13下午4:20:45
     * @TODO(功能) 发送一般文件
     * @mark(备注)
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {// 从数据库中查找
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, null,
                        null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// 从sd卡中查找
            filePath = uri.getPath();
        }
        if(filePath!=null){
            File file = new File(filePath);
            if (file == null || !file.exists()) {
                Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (file.length() > 10 * 1024 * 1024) {
                Toast.makeText(getApplicationContext(), "文件不能大于10M",
                        Toast.LENGTH_LONG).show();
                return;
            }
            commonSendAssess(MessageType.FILE_MESSAGE.value(), "",
                    file.getAbsolutePath(),
                    file.getName() + "|" + DataUtil.formatFileSize(file.length()));
        }else{
            Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * @Author LIUBO
     * @TODO TODO 发送文本消息
     * @Date 2015-1-21
     * @Return void
     */
    private void sendText() {
        String localContent = mEditTextContent.getText().toString();
        if (TextUtils.isEmpty(localContent))
            return;
        commonSendAssess(MessageType.TXT_MESSAGE.value(), localContent, "",
                null);
        // 重置发送框
        mEditTextContent.setText("");

    }

    /**
     * @param header   头部信息
     * @param content  内容
     * @param fileInfo 文件信息的名字和大小（字符串拼接 以“|”区分）
     * @return
     * @author LIUBO
     * @date 2015-3-23下午8:04:28
     * @TODO 打包信息并保存到本地
     */
    private Message packageMsgAndSaveLocal(Header header, String content, String[] memberArr,
                                           String filePath, String fileInfo) {
        Message msg = null;
        if (isGroup) {
            msg = MsgManager.getInstance().packageMsg(header, memberArr, content,
                    fileInfo, mcontactId, title);
        } else {
            msg = MsgManager.getInstance().packageMsg(header,
                    new String[]{mcontactId}, content, fileInfo, null, null);
        }

        // 图片类型的本地地址要加上uri标志
        if (header.getType() == MessageType.PICTURE_MESSAGE.value()) {
            filePath = "file://" + filePath;
        }
        // 获取并保存到本地数据库中
        ChatRecord chat = DBConversion.getInstance().getChatMsg(msg, false,
                content, filePath);
        chat.saveThrows();
        mDataArrays.add(chat);
        handler.sendEmptyMessage(REFRESH_NEW_MSG_COM);
        return msg;
    }

    /**
     * @param msg
     * @Author liubo
     * @date 2015-3-10下午8:38:39
     * @TODO(功能) 将消息保存本地并发送到服务器上
     * @mark(备注)
     */
    private void sendToService(Message msg, File... files) {
        // 向对方发送聊天消息
        try {
            MainActivity.customClient.send(msg, operation, files);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 消息发送后的消息状态处理
    private ResultOperation operation = new ResultOperation() {
        @Override
        public void doInFailed(String msgId) {
            ContentValues values = new ContentValues();
            values.put("msgstate", MsgManager.MSG_STATE_FAILE);
            updateMsgState(msgId, values);
        }

        ;

        @Override
        public void doInSuccess(String msgId) {
            ContentValues values = new ContentValues();
            values.put("msgstate", MsgManager.MSG_STATE_SUCESS);
            updateMsgState(msgId, values);
        }
    };

    /**
     * @param msgId
     * @param values
     * @Author liubo
     * @date 2015-3-11下午12:21:14
     * @TODO(功能)
     * @mark(备注)
     */
    private void updateMsgState(String msgId, ContentValues values) {
        if (DataUtil.isEmpty(msgId))
            return;
        // 更新聊天记录表的数据
        ChatRecord record = DBHelper.getInstance().update(ChatRecord.class,
                values, "msgid=?", msgId);
        for (int i = mDataArrays.size() - 1; i >= 0; i--) {
            if (msgId.equals(mDataArrays.get(i).getMsgId())) {
                mDataArrays.set(i, record);
                break;
            }
        }
        handler.sendEmptyMessage(REFRESH_NEW_MSG_COM);
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    /**
     * @param context
     * @param contactId 聊天对象Id,群聊时为群Id
     * @param title     标题
     * @param isGroup   是否是群聊
     * @author LIUBO
     * @date 2015-4-16上午11:56:20
     * @TODO 跳转到聊天界面
     */
    public static void actionIntent(Context context, String contactId,
                                    String title, boolean isGroup, String forwardMsgId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("contactId", contactId);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("title", title);
        intent.putExtra("forwardMsgId", forwardMsgId);
        context.startActivity(intent);
    }

    public static void actionIntent(Context context, String contactId,
                                    String title, boolean isGroup, ShareMsgInfo shareMsgInfo, boolean isShare) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("contactId", contactId);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("title", title);
        intent.putExtra("shareMsgInfo", shareMsgInfo);
        context.startActivity(intent);
    }

    protected void onPause() {
        super.onPause();
        CommonUtil.hideKeyboard(this);
        // 更新单聊的消息中心的最新记录
        updateSessionMsg(mcontactId, title);
    }

    public void emoClick(View view) {
        if (more.getVisibility() == View.GONE) {
            CommonUtil.hideKeyboard(this);
            more.setVisibility(View.VISIBLE);
            faceBtnNor.setVisibility(View.VISIBLE);
            rel_face.setVisibility(View.GONE);
            viewFunction.setVisibility(View.VISIBLE);
        } else {
            if (rel_face.getVisibility() == View.VISIBLE) {
                rel_face.setVisibility(View.GONE);
                viewFunction.setVisibility(View.VISIBLE);
                faceBtnNor.setVisibility(View.VISIBLE);
                faceBtnPre.setVisibility(View.GONE);
            } else {
                more.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param chatUserId 聊天对象id
     * @param name       聊天对象昵称
     * @author 刘波
     * @date 2015-3-3上午10:35:50
     * @todo 更新消息中心的记录
     */
    private void updateSessionMsg(String chatUserId, String name) {
        // 社版中心的未读消息归零（单条）
        DBHelper.getInstance().clearUnReadTips(SessionMsg.class, chatUserId,
                MsgManager.CHAT_WITH_FRIEND);
        if (DataUtil.isEmpty(mDataArrays))
            return;
        ChatRecord chat = mDataArrays.get(mDataArrays.size() - 1);
        String conditions[] = DBHelper.getInstance().createUSM(chatUserId,
                MsgManager.CHAT_WITH_FRIEND);

        if (!DBHelper.getInstance().isExist(SessionMsg.class, conditions)) {// 判断该聊天信息是否出现在社版中心
            DBConversion.getInstance().getSessionMsg(chat, name).saveThrows();
        } else {
            DBHelper.getInstance().updateSessionMsg(chat);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
        mcontactId = null;
        // 注销广播
        try {
            unregisterReceiver(receiver);
            unregisterReceiver(fileDownReceiver);
            unregisterReceiver(finishBrocasd);
            receiver = null;
        } catch (Exception e) {
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_NEW_MSG_COM:
                    mAdapter.notifyDataSetChanged();
                    // 将信息显示在最新信息
                    mListView.setSelection(mListView.getCount() - 1);
                    break;
                case REFRESH_LOAD_MORE:
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(msg.arg1 - 1);
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        intent = getIntent();
        String str = intent.getStringExtra("contactId");
//        if (!mcontactId.equals(str)) {
            finish();
        overridePendingTransition(0,0);
            startActivity(intent);
//        } else
//            super.onNewIntent(intent);
    }

    /**
     * @author 林茂华
     * @功能 表情选择监听
     * @日期 2015-1-7
     * @Param
     */
    public interface OnCorpusSelectedListener {
        void onCorpusSelected(ChatEmoji emoji);

        void onCorpusDeleted();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = mEditTextContent.getSelectionStart();
            String text = mEditTextContent.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    mEditTextContent.getText().delete(start, end);
                    return;
                }
                mEditTextContent.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .addFace(this, emoji.getId(), emoji.getCharacter());
            mEditTextContent.append(spannableString);
        }

    }

    /*
     * 重发消息
     */
    @Override
    public void onRepeatClick(String msgId) {
        showRepeatDialog(msgId);
    }

    /**
     * @param msgId
     * @author LIUBO
     * @date 2015-3-19下午6:24:11
     * @TODO 显示重发对话框
     */
    private void showRepeatDialog(final String msgId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("重发消息");
        dialog.setMessage("是否重发该条消息!");
        dialog.setNegativeButton("取消", null)
                .setPositiveButton("重发", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("msgstate", MsgManager.MSG_STATE_ONGOING);
                            updateMsgState(msgId, values);
                            MainActivity.customClient.reSend(msgId, operation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).create().show();

    }

    /**
     * @author liubo
     * @date 2015-3-10上午9:16:24
     * @TODO(功能) 新消息通知广播
     * @mark(备注)
     */
    class NewMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 记得把广播给终结掉
            abortBroadcast();
            NotificationBean bean = intent.getParcelableExtra("newMsgCom");
            if (mcontactId.equals(bean.getSendId())) {// 是本聊天页面的消息
                handleCurrentPageMsg(bean.getMsgId(), bean.getMsgType());
            } else {// 不是本聊天页面的消息,状态栏通知
                NotificationHelper.getInstance().notifyUser(ChatActivity.this,
                        bean);
            }
        }

        /**
         * @param msgId
         * @param msgType
         * @author LIUBO
         * @date 2015-4-16下午12:11:29
         * @TODO 处理当前页面的消息
         */
        private void handleCurrentPageMsg(String msgId, int msgType) {
            // 该好友被删除时(或者被踢出群时未加入该标志)，强制关闭该页面
            if (msgType == MsgManager.FRIEND_BE_DELETED
                    || msgType == MsgManager.DISBAND_RING) {
                ChatActivity.this.finish();
                return;
            }

            if (msgType == MsgManager.CHAT_WITH_FRIEND) {// 聊天消息
                ChatRecord chat = DBHelper.getInstance().find(ChatRecord.class,
                        "msgid=?", msgId);
                mDataArrays.add(chat);
                handler.sendEmptyMessage(REFRESH_NEW_MSG_COM);
                return;
            }
            if (msgType == MsgManager.RING_HAS_COMMENDS && isGroup) {// 新评论
                initFloatDatas();
                return;
            }

        }
    }

    class FileDownBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgId = intent.getStringExtra("msgId_file");
            String localPath = intent.getStringExtra("localPath");
            ContentValues values = new ContentValues();
            values.put("msgstate", 3);
            values.put("localfilepath", localPath);
            updateMsgState(msgId, values);
        }
    }

    /**
     * @author LIUBO
     * @date 2015-4-16上午11:11:23
     * @TODO 初始化浮动按钮
     */
    @SuppressLint("InflateParams")
    private void initFloatBtn() {
        if (!isGroup)
            return;
        floatingView = new FloatingView(this);
        fView = getLayoutInflater().inflate(R.layout.common_layout_btn_1, null);
        fContainer = (LinearLayout) fView
                .findViewById(R.id.layout_btn_1_container);
        fIcon = (ImageView) fView.findViewById(R.id.layout_btn__1_icon);
        fContent = (TextView) fView.findViewById(R.id.layout_btn_1_content);
        topContainer.post(new Runnable() {// 避免获取的高度为0
            @Override
            public void run() {
                floatingView.setView(fView, 10,
                        topContainer.getHeight() * 2 + 10);
            }
        });

        floatingView.setFloatBtnClickListener(new FloatingView.OnFloatBtnClickListener() {
            @Override
            public void onClick() {
                DynamicCommentActivity.actionIntent(ChatActivity.this, null,
                        mcontactId, true);
                SPHelper.clearUnReadCommendNum(QYApplication.getPersonId(), mcontactId);
                fContainer.setVisibility(View.GONE);
            }
        });
    }

    /**
     * @author LIUBO
     * @date 2015-4-16上午11:35:07
     * @TODO 绑定悬浮按钮的数据
     */
    private void initFloatDatas() {
        if (!isGroup)
            return;
        fContainer.setVisibility(View.VISIBLE);
        int unReadNum = SPHelper.getUnReadCommendNum(
                QYApplication.getPersonId(), mcontactId);
        if (unReadNum == 0) {
            fContainer.setBackgroundResource(R.drawable.ic_bg_no_recommend);
            fIcon.setImageResource(R.drawable.ic_no_recommend);
            fContent.setText("无未读评论");
            fContent.setTextColor(getResources().getColor(R.color.gray));
        } else {
            fContainer.setBackgroundResource(R.drawable.ic_bg_has_recommend);
            fIcon.setImageResource(R.drawable.ic_has_recommend);
            fContent.setTextColor(getResources().getColor(
                    android.R.color.holo_red_light));
            if (unReadNum < 99)
                fContent.setText(unReadNum + "条未读评论");
            else
                fContent.setText("99+条未读评论");
        }
    }

    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            faceBtnNor.setVisibility(View.VISIBLE);
            faceBtnPre.setVisibility(View.GONE);
        } else
            super.onBackPressed();

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                loadMoreDatas();
            }
        }, 1000);

    }

    private void loadMoreDatas() {
        if (!hasMoreData) {
            ToastUtil.show(this, "已经到顶啦！", Gravity.TOP);
            QYApplication.refulshComplete(pullListView, null);
        }
        List<ChatRecord> moreDatas = new ArrayList<ChatRecord>();
        try {
            if (!isGroup) {
                moreDatas = DBHelper.getInstance().getRecords(
                        QYApplication.getPersonId(), mcontactId,
                        (++i) * pageSize, pageSize, false);
            } else {
                // （群聊记录）
                moreDatas = DBHelper.getInstance().getRecords(
                        QYApplication.getPersonId(), mcontactId,
                        (++i) * pageSize, pageSize, true);
            }
        } catch (Exception e) {
            return;
        }
        android.os.Message msg = android.os.Message.obtain(handler);
        msg.what = REFRESH_LOAD_MORE;// 设置加载更多标志
        if (moreDatas.size() > 0) {
            mDataArrays.addAll(0, moreDatas);
            msg.arg1 = moreDatas.size();
            handler.sendMessage(msg);
            if (moreDatas.size() != pageSize) {
                hasMoreData = false;
            } else {
                hasMoreData = true;
            }
        } else {
            hasMoreData = false;
        }
        QYApplication.refulshComplete(pullListView, null);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
