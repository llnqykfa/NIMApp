package com.nzy.nim.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.ChatActivity;
import com.nzy.nim.activity.main.MainActivity;
import com.nzy.nim.activity.main.SystemMessageActivity;
import com.nzy.nim.activity.main.SystemNotificationActivity;
import com.nzy.nim.adapter.MessagesAdapter;
import com.nzy.nim.db.bean.ActionItem;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.manager.AppManager;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.view.TitlePopup;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @classify(类别) fragment(碎片)
 * @TODO(功能) TODO 消息中心（展示各种消息）
 * @Param(参数)
 * @Remark(备注)
 */
public class MessageFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    public static MessageFragment messageFragmentInstance;
    private ListView mListView;
    private MessagesAdapter mAdapter;
    private TextView noNetWork;
    private static List<SessionMsg> msgDatas = new ArrayList<SessionMsg>();
    private View view;
    private PullToRefreshListView ptrListView;
    private String TAG = MessageFragment.class.getName();
    private String phone="";
    //定义标题栏弹窗按钮
    private TitlePopup titlePopup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            UserInfo user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());

            if(user!=null){
                phone = user.getPhone();
                if(phone==null){
                    new AlertDialog.Builder(getActivity()).setTitle("提醒").setMessage("请重新登录！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppManager.getInstance().reLoginApp(getActivity());
                        }
                    }).setCancelable(false).show();
                }else{
                    try {
                        byte[] phone_utf8 = phone.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            view = inflater.inflate(R.layout.fragment_message, container,
                    false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(getView());
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 更新数据
        messageFragmentInstance = this;
        refulsh();
    }

    @Override
    public void onPause() {
        super.onPause();
        messageFragmentInstance = null;
    }
    public void onResume() {
        super.onResume();
    }
    /**
     * @param view
     * @Author LIUBO
     * @TODO TODO 关联布局中的控件并添加监听事件
     * @Date 2015-1-21
     * @Return void
     */
    private void init(View view) {
        initTopBar(view);
        noNetWork = (TextView) view
                .findViewById(R.id.fragment_msg_notify_no_network);
        ptrListView = (PullToRefreshListView) view
                .findViewById(R.id.fragment_message_sdListView);
        mListView = ptrListView.getRefreshableView();
        QYApplication.initPullRefulsh(ptrListView, this, false, false,
                TAG);
        mAdapter = new MessagesAdapter(getActivity(), msgDatas,
                R.layout.fragment_message_list_item);
        mListView.setSelector(R.drawable.listview_selector_bg_1);
        mListView.setAdapter(mAdapter);
        noNetWork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        /**
         * 点击弹出对应的界面
         */
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 处理不同消息的点击事件
                handlerClickItem(position);
            }
        });
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                showResumeDialog(mAdapter.getItem(position));
                return true;
            }

            /**
             * @TODO(功能)
             * @mark(备注)
             * @return
             */
            private void showResumeDialog(final SessionMsg session) {
                String[] items = {"删除该聊天"};
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setTitle(session.getTitles());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                DBHelper.getInstance().deleteSession(session);
                                ((MainActivity) getActivity())
                                        .reflushMsgCenterTips();
                                session.setUnLookedMsgCount(0);
                                refulsh();
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }
    /**
     * 初始化数据
     */
    private void initData(){
        //给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(getActivity(), "扫一扫", R.drawable.qy_sacncode));
        titlePopup.addAction(new ActionItem(getActivity(), "添加好友", R.drawable.qy_addfriend));
        titlePopup.addAction(new ActionItem(getActivity(), "足球圈", R.drawable.qy_group));
        //titlePopup.addAction(new ActionItem(getActivity(), "通讯录", R.drawable.qy_contact));
    }
    /**
     * @param view
     * @TODO 初始化title
     */
    private void initTopBar(View view) {
        ImageView topBarBack = (ImageView) view.findViewById(R.id.top_bar_back);
        TextView topBarContent = (TextView) view
                .findViewById(R.id.top_bar_content);
        Button topBarMore = (Button) view.findViewById(R.id.top_bar_next);
        ImageView topBarMoreIv = (ImageView) view
                .findViewById(R.id.top_bar_next_iv);
        topBarMoreIv.setVisibility(View.VISIBLE);
        topBarMoreIv.setImageResource(R.drawable.qy_add);
        topBarBack.setVisibility(View.INVISIBLE);
        topBarMore.setVisibility(View.INVISIBLE);
        topBarContent.setText("消息中心");
        //实例化标题栏弹窗
        titlePopup = new TitlePopup(getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topBarMoreIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
//                MipcaActivityCapture.actionIntent(getActivity());
            }
        });
    }

    public void noNetworkShow() {
        if (noNetWork!=null){
            noNetWork.setVisibility(View.VISIBLE);
        }
    }

    public void noNetworkHide() {
        if (noNetWork!=null){
            noNetWork.setVisibility(View.GONE);
        }
}

    /**
     * @param position 消息在列表中的位置（listView)
     * @TODO TODO 根据消息的类型进行不同的数据处理
     * @Return void
     */
    private void handlerClickItem(int position) {
        SessionMsg rm = mAdapter.getItem(position);
        ContentValues values = new ContentValues();
        switch (rm.getMainType()) {
            // 系统消息
            case MsgManager.SYSTEM_TYPE:
                SystemMessageActivity.actionIntent(getActivity());
                break;
            // 聊天类型
            case MsgManager.CHAT_TYPE:
                handleChats(rm);
                break;
            case MsgManager.SYSTEM_NOTIFICATIONS_TYPE:// 系统通知
                SystemNotificationActivity.actionIntent(getActivity());
                break;
//            case MsgManager.THE_MEETING_TYPE://会议
//                startActivity(new Intent(getActivity(), MeetingListActivity.class));
//                break;
//            case MsgManager.PROJEST_MEETING_TYPE://专题会议
//                startActivity(new Intent(getActivity(), ProjectMeetingActivity.class));
//                break;
            default:
                break;

        }
    }

    /**
     * @param rm
     * @TODO 处理聊天
     */
    private void handleChats(SessionMsg rm) {
        if (rm.isGroup()
                || DBHelper.getInstance().isMyGroup(rm.getSendUserId())) {
            ChatActivity.actionIntent(getActivity(), rm.getSendUserId(),
                    rm.getTitles(), true, null);
        } else{
            //设置昵称
            String title="";
            String contactMarkName = QYApplication.getContactMarkName(rm.getSendUserId());
            if (!DataUtil.isEmpty(contactMarkName)) {
                title=contactMarkName;
            } else {
                title=rm.getTitles();
            }
            ChatActivity.actionIntent(getActivity(), rm.getSendUserId(),
                    title, false, null);
        }
    }

    /**
     * @author LIUBO
     * @date 2015-4-15上午11:54:14
     * @TODO 刷新数据
     */
    public void refulsh() {
        List<SessionMsg> datas = DataSupport
                .where("userid=?", QYApplication.getPersonId())
                .order("creatTime desc").find(SessionMsg.class);
        if (datas != null) {
            msgDatas.clear();
            // 添加最新数据
            msgDatas.addAll(datas);
            mAdapter.headMap.clear();
            mAdapter.notifyDataSetChanged();
//            mAdapter = new MessagesAdapter(getActivity(), msgDatas,
//                    R.layout.fragment_message_list_item);
        }
        QYApplication.refulshComplete(ptrListView, TAG);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        refulsh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
