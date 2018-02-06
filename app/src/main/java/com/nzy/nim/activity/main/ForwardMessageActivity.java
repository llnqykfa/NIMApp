package com.nzy.nim.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.AlertDialogActivity;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.db.tmpbean.ForwardSessions;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.ClearEditText;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeVO;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 转发界面
 *
 * @author liu
 */
public class ForwardMessageActivity extends BaseActivity {
    private static final String ACTION_NAME = "ccom.nzy.nim.ForwardMessageActivity";
    private ListView listView;
    private ClearEditText editText;
    private TextView noResultTv;
    private CommonAdapter<ForwardSessions> adapter;
    private List<ForwardSessions> datas = new ArrayList<ForwardSessions>();
    private boolean isSearch = false;
    private int contactsCount = 0;// 搜索到的联系人的总数
    public static final int REQUEST_FORWARD = 1;// 转发请求码
    private static ForwardSessions chooseSession = null;
    private String msgId = "";
    private List<Contacts> contactsList;
    private List<MyGroups> groupList;
    private ShareMsgInfo shareMsgInfo;
    private ArrayList<String> recentChatUser = new ArrayList<String>();
    private ArrayList<String> recentChatRing = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);// 复用消息中心的布局
        msgId = getIntent().getStringExtra("msgId");
        shareMsgInfo = (ShareMsgInfo) getIntent().getSerializableExtra("shareMsgInfo");
        initTopBar();
        initView();
        initDefaultDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_NAME)) {
                if (chooseSession != null) {
                    boolean isRingDynamic = intent.getBooleanExtra("isRingDynamic", false);

                    String title = "";
                    if (!DataUtil.isEmpty(chooseSession.getNameMask())) {
                        title = chooseSession.getNameMask();
                    } else if (!DataUtil.isEmpty(chooseSession.getName())) {
                        title = chooseSession.getName();
                    }
                    if (shareMsgInfo != null) {
                        if (isRingDynamic) {//是否分享到组圈动态
                            String shareContent = shareMsgInfo.getShareContent();
                            if (shareContent.length()>200) {
                                shareMsgInfo.setShareContent(shareContent.substring(0, 200 - 1));
                            }
                            String s = new Gson().toJson(shareMsgInfo);
                            HttpHelper.shareToRingDynamic(ForwardMessageActivity.this, chooseSession.getForwardToId(), s, new HttpHelper.OnLoadFinishListener() {
                                @Override
                                public void onFinish(String data) {
                                    ToastUtil.showShort(ForwardMessageActivity.this, data);
                                    finish();
                                }
                            });
                        } else {
                            ChatActivity.actionIntent(ForwardMessageActivity.this, chooseSession.getForwardToId(),
                                    title, chooseSession.isGroup(), shareMsgInfo, true);
                        }
                        finish();
                    } else {
                        ChatActivity.actionIntent(ForwardMessageActivity.this, chooseSession.getForwardToId(),
                                title, chooseSession.isGroup(), msgId);
                        finish();
                    }
                }

            }
        }

    };

    private void initView() {
        listView = (ListView) findViewById(R.id.forward_listview);
        editText = (ClearEditText) findViewById(R.id.forward_edit);
        noResultTv = (TextView) findViewById(R.id.forward_tv_noresult);
        initAdapter();
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    doSearch(s.toString());
                    isSearch = true;
                } else {
                    initDefaultDatas();
                    isSearch = false;
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
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                chooseSession = adapter.getItem(position);

                String nick = "";
                if (!DataUtil.isEmpty(chooseSession.getNameMask())) {
                    nick = chooseSession.getNameMask();
                } else if (!DataUtil.isEmpty(chooseSession.getName())) {
                    nick = chooseSession.getName();
                }
                if (chooseSession.isGroup() && shareMsgInfo != null&&
                        (shareMsgInfo.getShareType().equals(MyConstants.BOOK_REVIEW_TYPE)||shareMsgInfo.getShareType().equals(MyConstants.MEETING_TYPE))) {
                    AlertDialogActivity.actionForwardIntent(
                            ForwardMessageActivity.this,
                            AlertDialogActivity.FORWARD_FLAG,
                            chooseSession.getHeadUri(), nick, REQUEST_FORWARD, true);
                } else {
                    AlertDialogActivity.actionForwardIntent(
                            ForwardMessageActivity.this,
                            AlertDialogActivity.FORWARD_FLAG,
                            chooseSession.getHeadUri(), nick, REQUEST_FORWARD);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FORWARD && data != null) {
            boolean sendFlag = data.getBooleanExtra("send_flag", false);
            if (sendFlag && chooseSession != null) {
                String title = "";
                if (!DataUtil.isEmpty(chooseSession.getNameMask()) && !"".equals(chooseSession.getNameMask()))
                    title = chooseSession.getNameMask();
                else if (!DataUtil.isEmpty(chooseSession.getName()))
                    title = chooseSession.getName();
                ChatActivity.actionIntent(this, chooseSession.getForwardToId(),
                        title, chooseSession.isGroup(), msgId);
            }
        }
    }

    /**
     * @author LIUBO
     * @date 2015-4-15上午10:49:04
     * @TODO 初始化title
     */
    private void initTopBar() {
        TextView topBarContent = (TextView) findViewById(R.id.top_bar_content);
        Button topBarMore = (Button) findViewById(R.id.top_bar_next);
        topBarMore.setVisibility(View.INVISIBLE);
        topBarContent.setText("选择");
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new CommonAdapter<ForwardSessions>(this, datas,
                R.layout.activity_childitem_list) {

            @Override
            public void convert(ViewHolder helper, ForwardSessions item) {
                final int position = helper.getPosition();
                TextView sign = helper.getView(R.id.child_sign);
                sign.setVisibility(View.GONE);

                TextView header = helper.getView(R.id.childitem_header);
                RoundImageView headImg = helper.getView(R.id.child_image);
                //显示昵称
                if (!DataUtil.isEmpty(item.getNameMask())) {
                    helper.setText(R.id.child_name, item.getNameMask());
                } else {
                    helper.setText(R.id.child_name, item.getName());
                }
                ImageUtil.displayHeadImg(item.getHeadUri(), headImg);
                header.setTextColor(Color.GRAY);
                if (!isSearch) {// 不是搜索的
                    if (position == 0) {
                        header.setVisibility(View.VISIBLE);
                        header.setText("好友");
                    } else if (position < contactsList.size()) {
                        header.setVisibility(View.GONE);
                    }
                    if (position == contactsList.size() && groupList.size() != 0) {
                        header.setVisibility(View.VISIBLE);
                        header.setText("组圈");
                    } else if (position > contactsList.size() && position < datas.size()) {
                        header.setVisibility(View.GONE);
                    }
                } else {// 是搜索的
                    if (contactsCount > 0 && position == 0) {
                        header.setText("好友");
                        header.setVisibility(View.VISIBLE);
                    } else if (datas.size() > contactsCount
                            && position == contactsCount) {
                        header.setText("圈");
                        header.setVisibility(View.VISIBLE);
                    } else {
                        header.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    /**
     * 搜索想要转发的对象
     */
    private void doSearch(String value) {
        List<Contacts> contactList = DBHelper.getInstance().getContactsBySql(
                value);
        List<MyGroups> myGroupList = DBHelper.getInstance().getMyGroupsBySql(
                value);
        contactsCount = contactList.size();
        datas.clear();
        datas.addAll(searchContactsResult(contactList));
        datas.addAll(searchMyGroupsResult(myGroupList));
        if (datas.size() == 0) {
            noResultTv.setText("无结果");
            noResultTv.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
            noResultTv.setVisibility(View.GONE);
        }
    }

    private List<ForwardSessions> searchMyGroupsResult(
            List<MyGroups> myGroupList) {
        List<ForwardSessions> sessions = new ArrayList<ForwardSessions>();
        for (MyGroups mg : myGroupList) {
            ForwardSessions fs = new ForwardSessions();
            fs.setForwardToId(mg.getGroupId());
            fs.setHeadUri(mg.getGroupImg());
            fs.setName(mg.getGroupName());
            fs.setGroup(true);
            sessions.add(fs);
        }
        for (int i = 0; i < recentChatRing.size(); i++) {
            for (int i1 = 0; i1 < sessions.size(); i1++) {
                ForwardSessions forwardSessions = sessions.get(i1);
                if (forwardSessions.getForwardToId().equals(recentChatRing.get(i))) {
                    sessions.remove(forwardSessions);
                    sessions.add(0, forwardSessions);
                }
            }
        }
        return sessions;
    }

    private List<ForwardSessions> searchContactsResult(
            List<Contacts> contactList) {
        List<ForwardSessions> sessions = new ArrayList<ForwardSessions>();
        for (Contacts ct : contactList) {
            ForwardSessions fs = new ForwardSessions();
            fs.setForwardToId(ct.getContactId());
            fs.setHeadUri(ct.getPhotoPath());
            fs.setName(ct.getUserName());
            fs.setNameMask(ct.getRemark());
            fs.setGroup(false);
            sessions.add(fs);
        }
        for (int i = 0; i < recentChatUser.size(); i++) {
            for (int i1 = 0; i1 < contactList.size(); i1++) {
                ForwardSessions forwardSessions = sessions.get(i1);
                if (forwardSessions.getForwardToId().equals(recentChatUser.get(i))) {
                    sessions.remove(forwardSessions);
                    sessions.add(0, forwardSessions);
                }
            }
        }
        return sessions;
    }

    private List<ForwardSessions> searchSessionMsgResult(List<SessionMsg> list) {
        List<ForwardSessions> sessions = new ArrayList<ForwardSessions>();
        for (SessionMsg sm : list) {
            ForwardSessions fs = new ForwardSessions();
            fs.setForwardToId(sm.getSendUserId());
            fs.setHeadUri(sm.getImgUrl());
            fs.setName(sm.getTitles());
            fs.setGroup(sm.isGroup());
            sessions.add(fs);
        }
        return sessions;
    }

    /**
     * 初始化默认数据
     */
    private void initDefaultDatas() {
        datas.clear();
        contactsList = DBHelper.getInstance().findAll(
                Contacts.class, "userid=?", QYApplication.getPersonId());
        groupList = DBHelper.getInstance().findAll(
                MyGroups.class, "userid=?", QYApplication.getPersonId());
        contactsCount = contactsList.size();
        List<SessionMsg> recentContactsList = DBHelper.getInstance().findAllByOrder(
                SessionMsg.class, "creattime asc", "maintype=?", MsgManager.CHAT_TYPE + "");
        if (recentContactsList != null) {
            for (int i = 0; i < recentContactsList.size(); i++) {
                if (recentContactsList.get(i).isGroup()) {
                    recentChatRing.add(recentContactsList.get(i).getSendUserId());
                } else {
                    recentChatUser.add(recentContactsList.get(i).getSendUserId());
                }
            }
        }

        if (contactsList != null && contactsList.size() != 0) {
            datas.addAll(searchContactsResult(contactsList));
        } else {
            getAllMyFriends();
        }
        if (groupList != null && groupList.size() != 0) {
            datas.addAll(searchMyGroupsResult(groupList));
        } else {
            getMyRings();
        }
        if (datas.size() == 0) {
            noResultTv.setText("未找到要转发的对象!");
            noResultTv.setVisibility(View.VISIBLE);
        } else {
            noResultTv.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    public void getAllMyFriends() {
        HTTPUtils.postWithToken(ForwardMessageActivity.this, URLs.GET_FRIENDS_INFO, null, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                if (!DataUtil.isEmpty(s)) {
                    try {
                        List<PersonVO> data = JSON.parseArray(new JSONObject(s).getString("friends"), PersonVO.class);
                        // 删除本地
                        DataSupport.deleteAll(Contacts.class, "userid=?",
                                QYApplication.getPersonId());
                        // 保存服务器上的最新联系人
                        List<Contacts> tmpList;
                        if (!DataUtil.isEmpty(data)) {
                            tmpList = DBConversion.getInstance().getContacts(data);
                            contactsList.clear();
                            contactsList.addAll(tmpList);
                            DataSupport.saveAll(tmpList);
                            datas.addAll(searchContactsResult(tmpList));
                            if (datas.size() == 0) {
                                noResultTv.setText("未找到要转发的对象!");
                                noResultTv.setVisibility(View.VISIBLE);
                            } else {
                                noResultTv.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getMyRings() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("personId", QYApplication.getPersonId());
        HTTPUtils.post(ForwardMessageActivity.this, URLs.LIST_MY_RING, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                try {
                    List<RingThemeVO> list = JSON.parseArray(s,
                            RingThemeVO.class);
                    DBHelper.getInstance().delete(MyGroups.class, "userid=?",
                            QYApplication.getPersonId());
                    if (list.size() > 0) {
                        List<MyGroups> groupList = DBConversion.getInstance()
                                .getGroups(list);
                        if (groupList.size() > 0) {
                            ForwardMessageActivity.this.groupList.clear();
                            ForwardMessageActivity.this.groupList.addAll(groupList);
                            DataSupport.saveAll(groupList);
                            datas.addAll(searchMyGroupsResult(groupList));
                            if (datas.size() == 0) {
                                noResultTv.setText("未找到要转发的对象!");
                                noResultTv.setVisibility(View.VISIBLE);
                            } else {
                                noResultTv.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void actionIntent(Context context, String msgId) {
        Intent intent = new Intent(context, ForwardMessageActivity.class);
        intent.putExtra("msgId", msgId);
        context.startActivity(intent);
    }

    public static void actionIntent(Context context, ShareMsgInfo shareMsgInfo) {
        Intent intent = new Intent(context, ForwardMessageActivity.class);
        intent.putExtra("shareMsgInfo", shareMsgInfo);
        context.startActivity(intent);
    }
}
