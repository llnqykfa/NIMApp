package com.nzy.nim.vo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.nzy.nim.activity.main.MainActivity;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.HeadTables;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.db.bean.InviteSharedFriends;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.db.tmpbean.NotificationBean;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ToastUtil;

import org.core.bootstrap.MessageType;
import org.core.bootstrap.client.ResultOperation;
import org.core.bootstrap.property.Header;
import org.core.bootstrap.property.Message;
import org.core.bootstrap.property.im.MessageEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-25 下午4:31:39
 * @classify(类别)
 * @TODO(功能) TODO 接收服务器发来的消息并处理（针对即时通信）
 * @Param(参数)
 * @Remark(备注)即时通信中，服务器发来的消息都由该类处理
 */
public class MsgManager {
    public static final String SERVICE_FILE_DIR = MyConstants.REMOTE_SERVER_URL
            + MyConstants.USER_CHAT;// 服务器上的聊天文件保存路径
    /* 聊天记录的消息类型 */
    public static final int TEXT_TYPE = 0;// 文本类型
    public static final int PICTURE_TYPE = 1;// 图片类型
    public static final int FILE_TYPE = 2;// 文件类型
    public static final int VOICE_TYPE = 3;// 语音类型
    public static final int FILE_TXT_TYPE = 4;// 图文并茂信息类型（单类型）
    public static final int SHARE_TXT_TYPE = 5;// 图文并茂信息类型（单类型）
    public static final int SHARE_TYPE = 6;// 分享类型
    public static final int SHARE_BOOKLIST_TYPE = 7;// 书单分享布局
    /* 聊天消息的发送状态 */
    public static final int MSG_STATE_SUCESS = 1;// 消息发送成功
    public static final int MSG_STATE_FAILE = 2;// 消息发送失败
    public static final int MSG_STATE_ONGOING = 0;// 消息正在发送
    public static final int MSG_FILE_IS_DOWN = 3;// 文件下载
    public static final int MSG_FILE_NOT_DOWN = -3;// 文件未下载
    public static final int MSG_NO_STATE = -1;// 消息无状态

    /* 消息中心的大类消息类型 */
    public static final int SYSTEM_TYPE = -1;// 系统消息
    public static final int INTEREST_TYPE = -3;// 兴趣中心
    public static final int CHAT_TYPE = -2;// 聊天消息
    public static final int THE_MEETING_TYPE = -6;// 会议
    public static final int SYSTEM_NOTIFICATIONS_TYPE = -10;// 系统通知
    public static final int PROJEST_MEETING_TYPE = -20;// 专题会议

    /* 从服务器接收到的消息的类型（消息中心的具体类型） */
    public static final int FRIEND_REQUEST = 0;// 接收到添加好友请求
    public static final int FRIEND_BE_AGREED = 1;// 对方同意我的添加好友请求
    public static final int FRIEND_BE_REJECTED = 7;// 对方拒绝我的添加好友
    public static final int FRIEND_BE_DELETED = 6;// 被好友删除

    public static final int CHAT_WITH_FRIEND = 2;// 好友聊天信息
    public static final int RING_ADD_REQUEST = 3;// 接收到圈请求通知
    public static final int RING_APPLY_BE_AGREED = 4;// 圈主同意圈添加请求(申请)
    public static final int RING_BE_REJECTED = 5;// 圈主拒绝圈添加请求(申请)
    public static final int RING_BE_INVITED = 12;// 被对方邀请加入组圈
    public static final int RING_HAS_COMMENDS = 11;// 有未读评论
    public static final int NEW_MEETING = 21;// 会议
    public static final int PROJEST_MEETING = 24;// 专题会议
    public static final int RING_INVITE_BE_AGREED = 14;// 好友同意圈邀请(邀请)
    public static final int RING_INVITE_BE_REJECTED = 15;// 好友拒绝圈邀请(邀请)
    public static final int DISBAND_RING = 16;// 圈主解散圈
    public static final int SYSTEM_NOTIFICATIONS = 88;// 系统通知（如活动）

    /* 邀请与通知的消息状态 */
    // 本人
    public static final int INVITE_NOT_AGREE = 0;// 未同意
    public static final int INVITE_AGREED = 1;// 已同意
    public static final int INVITE_REJECTED = 2;// 已拒绝
    public static final int INVITE_OTHER = 3;// 系统通知
    // 对方
    public static final int INVITE_BE_AGREE = -1;// 被同意
    public static final int INVITE_BE_REJECTED = -2;// 被拒绝
    /* 下拉表的功能标号 */
    public static final int POP_FUNCTION_MODIFY = 1;// 修改备注
    public static final int POP_FUNCTION_ADD_BLACK = 2;// 加入黑名单
    public static final int POP_FUNCTION_DELETE = 3;// 删除好友

    private Context context;
    private HttpHelper httpHelper = null;
    // 将服务器上的数据转换成本地数据
    private DBConversion dbConversion;
    private static MsgManager msgManager;
    private String[] conditions;
    private MsgManager() {
        context = QYApplication.getContext();
        dbConversion = DBConversion.getInstance();
        if (httpHelper == null)
            httpHelper = new HttpHelper();
    }

    public static MsgManager getInstance() {
        if (msgManager == null) {
            synchronized (MsgManager.class) {
                if (msgManager == null)
                    msgManager = new MsgManager();
            }
        }
        return msgManager;
    }

    /**
     * @Author LIUBO
     * @TODO TODO 对从服务器上接收到的消息进行分类
     * @Date 2015-1-25
     * @Return void
     */
    public void classifyMessage(Message message) {
        // 解析从服务器上的消息
        Log.e("IN MSG", message.toString());
        MessageEntity body = (MessageEntity) message.getBody();
        switch (body.getFlag()) {
            case FRIEND_REQUEST:// 接收到添加好友请求
                handleRequestMsg(message);
                break;
            case RING_ADD_REQUEST:// 接收到圈请求通知
                handleRequestMsg(message);
                break;
            case RING_BE_INVITED:// 被对方邀请
                handleRequestMsg(message);
                break;
            case FRIEND_BE_REJECTED:// 被对方拒绝添加请求
                handleRequestMsg(message);
                break;
            case RING_BE_REJECTED:// 被对方拒绝圈请求
                handleRejectMsg(message, INVITE_BE_REJECTED);
                break;
            case CHAT_WITH_FRIEND:// 好友聊天
                handleChatMsg(message);
                break;
            case RING_HAS_COMMENDS:// 有未读评论
                SPHelper.setUnReadCommendNum(QYApplication.getPersonId(),
                        body.getGroupId(), 1);
                sendNewMsgCom(message, true, "无");
                break;
            case FRIEND_BE_AGREED:// 对方同意添加好友请求
                handleFriendAgreeMsg(message);
                break;
            case FRIEND_BE_DELETED:// 被好友删除
                DBHelper.getInstance().clearFriendData(body.getUserId(),
                        body.getSenderName());
                sendNewMsgCom(message, false, "无");
                break;
            case RING_APPLY_BE_AGREED:// 圈主同意圈添加请求
                handleRingApplyAgreed(message, INVITE_BE_AGREE);
                break;
            case RING_INVITE_BE_AGREED:// 好友同意圈邀请
                handleRingInviteAgreed(message, INVITE_BE_AGREE);
                break;
            case PROJEST_MEETING:
                handleProjectMeetingMsg(message);//专题会议
                break;
            case DISBAND_RING:// 解散圈组
                handleDisbandRing(message);
                break;
            case SYSTEM_NOTIFICATIONS:// 系统推送消息
                handleRequestMsg(message);
                break;
            default:
                break;
        }
    }

    /**
     * @param message
     */
    private void handleDisbandRing(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();
        if (body.getGroupId() == null)
            return;
        DBHelper.getInstance().delete(MyGroups.class, "groupid=?",
                body.getGroupId());
        DBHelper.getInstance().delete(RingThemesTmp.class, "groupid=?",
                body.getGroupId());
        DBHelper.getInstance().delete(SessionMsg.class, "senduserid=?",
                body.getGroupId());
        DBHelper.getInstance().delete(ChatRecord.class,
                "groupid=? and isgroup=?", body.getGroupId(), "1");
        sendNewMsgCom(message, false, "无");
    }

    /**
     * 好友同意圈主的邀请
     *
     * @param message
     * @param inviteBeAgree
     */
    private void handleRingInviteAgreed(Message message, int inviteBeAgree) {
//        updateMessageCenter(message, MyConstants.SYSTEM_TITLE, false);
        MessageEntity body = (MessageEntity) message.getBody();
        String[] conditions = {"senduserid=? and userid=? and status=? ",
                body.getUserId(), QYApplication.getPersonId(),
                INVITE_NOT_AGREE + ""};
        // 判断该条通知是否应经存在,且没有处理
        if (!DBHelper.getInstance().isExist(Invitations.class, conditions)) {
            // 保存到邀请与通知表
            DBConversion.getInstance().getInvites(body, inviteBeAgree)
                    .saveThrows();
            updateMessageCenter(message, MyConstants.SYSTEM_TITLE, false);
        }
    }


    /**
     * 会议消息
     *
     * @param message
     */
    private void handleMeetingMsg(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();
        savemeetingPushMsg(body.getCtx(), body.getFlag());
        updateMessageCenter(message, "新的会议", false);
    }
    /**
     * 专题会议消息
     *
     * @param message
     */
    private void handleProjectMeetingMsg(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();
        saveProjectMeetingPushMsg(body.getCtx(), body.getFlag());
        updateMessageCenter(message, "专题会议", false);
    }

    /**
     * 保存会议消息
     *
     * @param jsonString
     * @param msgType
     */
    private void savemeetingPushMsg(String jsonString, int msgType) {
        if (DataUtil.isEmpty(jsonString))
            return;
        MeetingInfoVo pushVo = JSON.parseObject(jsonString, MeetingInfoVo.class);
        MeetingMsg meetingPushedMsg = dbConversion.getMeetingPushedMsg(pushVo);
        if (meetingPushedMsg != null) {
            meetingPushedMsg.saveThrows();
        }
    }
    /**
     * 保存专题会议消息
     *
     * @param jsonString
     * @param msgType
     */
    private void saveProjectMeetingPushMsg(String jsonString, int msgType) {
        if (DataUtil.isEmpty(jsonString))
            return;
        ProjectMeetingInfo projectMeetingMsg= JSON.parseObject(jsonString, ProjectMeetingInfo.class);
        ProjectMeetingMsg projectMeetingPushedMsg = dbConversion.getProjectMeetingPushedMsg(projectMeetingMsg);
//        Gson gson=new Gson();
//        ProjectMeetingMsg projectMeetingMsg = gson.fromJson(jsonString, ProjectMeetingMsg.class);
        if(projectMeetingPushedMsg!=null){
            projectMeetingPushedMsg.saveThrows();
        }
    }

    /**
     * @param message
     * @author LIUBO
     * @date 2015-3-27下午11:48:36
     * @TODO 处理圈请求被圈主同意的消息(群聊天消息)
     */
    private void handleRingApplyAgreed(Message message, int status) {
        MessageEntity body = (MessageEntity) message.getBody();
        HttpHelper.getRingTheme(body.getGroupId(), new HttpUtil.OnPostListener() {
            @Override
            public void onSuccess(String jsonData) {
                if (!DataUtil.isEmpty(jsonData)) {
                    RingThemeVO theme = JSON.parseObject(jsonData,
                            RingThemeVO.class);
                    DBConversion.getInstance().getGroup(theme).saveThrows();
                }
            }

            @Override
            public void onFailure() {
            }
        });
        // 再更新社版界面数据，最后广播通知（顺序不能颠倒，不然会出现空指针）
        updateMessageCenter(message, body.getGroupName(), true);
    }
    /**
     * @author LIUBO
     * @date 2015-3-26下午9:44:47
     * @TODO 处理拒绝请求（系统消息）
     */
    private void handleRejectMsg(Message message, int status) {
        DBConversion.getInstance()
                .getInvites((MessageEntity) message.getBody(), status)
                .saveThrows();
        updateMessageCenter(message, MyConstants.SYSTEM_TITLE, false);
    }

    /**
     * @param message
     * @author 刘波
     * @date 2015-3-7下午4:40:48
     * @todo 处理请求(系统消息)
     */
    private void handleRequestMsg(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();
        if(body.getFlag()==SYSTEM_NOTIFICATIONS){
             conditions= new String[]{"msgid=?",body.getMsgId()};
            // 判断该条通知是否应经存在,且没有处理
            if (!DBHelper.getInstance().isExist(Invitations.class, conditions)) {
                // 保存到邀请与通知表
                DBConversion.getInstance().getInvites(body, INVITE_OTHER)
                        .saveThrows();
                updateMessageCenter(message, MyConstants.SYSTEM_NOTI, false);
            }
            ToastUtil.showShort(QYApplication.getContext(),body.getCtx());
        }else{
            if(body.getGroupId()==null){
                conditions= new String[]{"senduserid=? and userid=? and status=?  and groupId=?", body.getUserId(), QYApplication.getPersonId(),
                        INVITE_NOT_AGREE + "",""};
            }else{
                conditions= new String[]{"senduserid=? and userid=? and status=?  and groupId=?", body.getUserId(), QYApplication.getPersonId(),
                        INVITE_NOT_AGREE + "",body.getGroupId()};
            }
            // 判断该条通知是否应经存在,且没有处理
            if (!DBHelper.getInstance().isExist(Invitations.class, conditions)) {
                // 保存到邀请与通知表
                DBConversion.getInstance().getInvites(body, INVITE_NOT_AGREE)
                        .saveThrows();
                updateMessageCenter(message, MyConstants.SYSTEM_TITLE, false);
            }
        }
    }

    /**
     * @param message
     * @author 刘波
     * @date 2015-3-7下午4:41:08
     * @todo 处理反馈消息
     */
    private void handleFriendAgreeMsg(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();
        HttpHelper.getPerson(body.getUserId(), new HttpUtil.OnPostListener() {
            @Override
            public void onSuccess(String jsonData) {
                if (!DataUtil.isEmpty(jsonData))// 保存联系人
                    dbConversion.getContact(
                            JSON.parseObject(jsonData, OldPersonVO.class))
                            .saveThrows();
                //发送广播更新好友列表
                Intent intent = new Intent();
                intent.setAction("update_contacts_List");
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure() {
            }
        });
        // 再更新社版界面数据，最后广播通知（顺序不能颠倒，不然会出现空指针）
        updateMessageCenter(message, body.getSenderName(), false);

    }

    /**
     * @param message
     * @author 刘波
     * @date 2015-3-5下午1:00:55
     * @todo 处理接收到的聊天消息
     */
    private void handleChatMsg(Message message) {
        MessageEntity body = (MessageEntity) message.getBody();

        // 将聊天记录保存到数据库中，并返回该聊天信息

        if (DataUtil.isEmpty(body.getGroupId())
                || message.getHeader().getType() == MessageType.TXT_FILE_MESSAGE
                .value()) {
            dbConversion.getChatMsg(message, true, null, null).saveThrows();
            // 更新消息中心(单聊消息)
            updateMessageCenter(message, body.getSenderName(), false);
        } else {
            if (SPHelper.getReceRingMsg(body.getGroupId())) {// 接收圈消息标志为true
                saveMemberInfo(body.getUserId());
                dbConversion.getChatMsg(message, true, null, null).saveThrows();
                // 保存圈成员的基本信息
                updateMessageCenter(message, body.getGroupName(), true);
            }
        }

    }

    /**
     * @param userId
     * @author LIUBO
     * @date 2015-4-16下午10:50:40
     * @TODO 保存群聊成员的头像
     */
    private void saveMemberInfo(final String userId) {
        HeadTables head = DBHelper.getInstance().find(HeadTables.class,
                "userid=?", userId);
        if (head == null) {
            handleHeadImg(userId, true);
        } else if (DataUtil.isEmpty(head.getHeadUrl())
                || DateUtil.calculateTimeDifference(head.getTimeStamp()
                .getTime(), System.currentTimeMillis()) > 24 * 60 * 60 * 1000l) {
            handleHeadImg(userId, false);
        }
    }

    /**
     * @param userId
     * @author LIUBO
     * @date 2015-4-16下午10:48:50
     * @TODO 处理头像
     */
    private void handleHeadImg(final String userId, final boolean isNull) {
        // 为空或者时间达到1天时 刷新数据
        HttpHelper.getPerson(userId, new HttpUtil.OnPostListener() {
            @Override
            public void onSuccess(String jsonData) {
                if (!DataUtil.isEmpty(jsonData)) {
                    OldPersonVO person = JSON
                            .parseObject(jsonData, OldPersonVO.class);
                    HeadTables head1 = new HeadTables();
                    head1.setUserId(userId);
                    head1.setHeadUrl(person.getPhotopath());
                    head1.setName(person.getUsername());
                    head1.setTimeStamp(new Date());
                    if (isNull)
                        head1.saveThrows();
                    else
                        head1.updateAll("userid=?", userId);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    /**
     * 更新消息中心的消息
     *
     * @param message
     * @param title
     * @param isGroupMsg
     */
    private void updateMessageCenter(Message message, String title,
                                     boolean isGroupMsg) {
        MessageEntity body = (MessageEntity) message.getBody();
        String[] conditions = DBHelper.getInstance().createUSM(
                DBHelper.getInstance().getSessionObjectId(message),
                body.getFlag());
        // 更新消息中心
        if (!DBHelper.getInstance().isExist(SessionMsg.class, conditions))// 消息中心不存在该消息则保存
            dbConversion.getSessionMsg(message, title, isGroupMsg).saveThrows();
        else
            // 消息中心已存在则更新
            DBHelper.getInstance().updateSessionMsg(message, title, isGroupMsg);
        // 圈，并且不提示
        sendNewMsgCom(message, isGroupMsg, title);

    }

    public static String getNewMessageBroadcastAction() {
        return "com.app.MsgManager.NEW_MESSAGE";
    }

    public static String getFileDownBroadcastAction() {
        return "com.app.MsgManager.FILE_DOWN";
    }

    public static String getForceOffLineAction() {
        return "com.app.broadcastreceiver.FORCE_OFFLINE";
    }

    /**
     * @param message
     * @param isGroupMsg
     * @author LIUBO
     * @date 2015-4-17下午5:20:26
     * @TODO发送新消息到来通知广播
     */
    private void sendNewMsgCom(Message message, boolean isGroupMsg, String title) {
        MessageEntity body = (MessageEntity) message.getBody();
        if (!isNotifyNewMsgCom(body.getFlag())) {
            return;
        }
        Intent intent = new Intent(getNewMessageBroadcastAction());
        NotificationBean notifyBean = new NotificationBean();
        if (isGroupMsg) {
            notifyBean.setSendId(body.getGroupId());
            notifyBean.setIsGroupMsg(NotificationBean.ISGROUP);
        } else {
            notifyBean.setSendId(body.getUserId());
            notifyBean.setIsGroupMsg(NotificationBean.ISNOTGROUP);
        }

        notifyBean.setTitle(title);
        notifyBean.setMsgType(body.getFlag());
        notifyBean.setSendUserName(body.getSenderName());
        notifyBean.setMsgId(body.getMsgId());
        notifyBean.setContent(getNotifyContent(message));
        Bundle bundle = new Bundle();
        bundle.putParcelable("newMsgCom", notifyBean);
        intent.putExtras(bundle);
        context.sendOrderedBroadcast(intent, null);
    }

    /**
     * 获取通知内容
     *
     * @return
     */
    private String getNotifyContent(Message msg) {
        String content = "";
        Header header = msg.getHeader();
        MessageEntity body = (MessageEntity) msg.getBody();
        switch (body.getFlag()) {
            case CHAT_WITH_FRIEND:
                content = DBConversion.getInstance()
                        .setCMContent(
                                DBConversion.getInstance().setChatMsgType(
                                        header.getType()), body.getCtx(), null,
                                false, false);
                break;

            case FRIEND_BE_AGREED:
                content = body.getSenderName() + "同意了你的好友请求!";
                break;
            case FRIEND_BE_REJECTED:
                content = body.getSenderName() + "拒绝了你的好友请求!";
                break;
            case RING_ADD_REQUEST:
                content = body.getSenderName() + "请求加入 " + body.getGroupName()
                        + " 圈";
                break;
            case RING_APPLY_BE_AGREED:
                content = body.getSenderName() + "已允许你加入 " + body.getGroupName()
                        + " 圈";
                break;
            case RING_BE_INVITED:
                content = body.getSenderName() + "邀请你加入 " + body.getGroupName()
                        + " 圈";
                break;
            case RING_BE_REJECTED:
                content = body.getSenderName() + "拒绝你加入" + body.getGroupName()
                        + "圈";
                break;
            case PROJEST_MEETING://专题会议
                content = body.getCtx();
                break;
            default:
                content = body.getAlert();
                break;
        }
        return content;
    }

    /**
     * 是否有新消息的到来
     *
     * @param flag
     * @return
     */
    private boolean isNotifyNewMsgCom(int flag) {
        switch (flag) {
            case RING_HAS_COMMENDS:// 有评论到来不提示
                return false;
            default:
                return true;
        }
    }

    /**
     * 打包转发的消息
     *
     * @param chat
     * @param groupName
     * @return
     */
    public Message packageForwardMsg(ChatRecord chat, String[] memberArr, String forwardToId,
                                     String groupName, boolean isGroup, String filePath) {
        String[] toUserIds = null;
        String groupId = "";
        int msgType = chat.getMsgType();
        Header header = new Header();
        if (msgType == TEXT_TYPE)
            header.setType(MessageType.TXT_MESSAGE.value());
        else if (msgType == PICTURE_TYPE)
            header.setType(MessageType.PICTURE_MESSAGE.value());
        else if (msgType == FILE_TYPE)
            header.setType(MessageType.FILE_MESSAGE.value());
        else if (msgType == FILE_TXT_TYPE)
            header.setType(MessageType.TXT_FILE_MESSAGE.value());
        else if (msgType == MsgManager.SHARE_TYPE)
            header.setType(MessageType.SHARE.value());
        if (isGroup) {
            groupId = forwardToId;
            toUserIds=memberArr;
        } else {
            toUserIds = new String[]{forwardToId};
        }
        Message msg = packageMsg(header, toUserIds, chat.getContent(),
                chat.getFileName() + "|" + chat.getFileSize(), groupId,
                groupName);
        return packageShareMsg(msg, filePath);
    }


    /**
     * @param header   头部信息
     *                 聊天对象id
     * @param content  内容
     * @param fileInfo 文件信息的名字和大小（字符串拼接 以“|”区分）
     * @return
     * @author LIUBO
     * @date 2015-3-23下午7:54:54
     * @TODO 打包要发送到服务器上的信息
     */
    public Message packageMsg(Header header, String[] toUserIds,
                              String content, String fileInfo, String groupId, String groupName) {
        // 向服务器发送的对象
        Message msg = new Message();
        // 设置头部
        msg.setHeader(header);
        // 设置主体内容
        MessageEntity entity = new MessageEntity();
        // 设置文件的名字和大小（聊天信息）
        entity.setAlert(fileInfo);
        UserInfo user = DBHelper.getInstance().find(UserInfo.class, "personId=?",
                QYApplication.getPersonId());
        if (user != null)
            // 设置发送者昵称
            entity.setSenderName(user.getUserName());
        // 设置消息的id
        if (MainActivity.customClient==null){
            entity.setMsgId(UUID.randomUUID().toString());
        }else{
            entity.setMsgId(MainActivity.customClient.getSerialId());
        }
        // 设置消息类型
        entity.setFlag(MsgManager.CHAT_WITH_FRIEND);
        entity.setSendDate(DateUtil.getDate(DateUtil.DATE_PATTERN_3));
        // 设置消息内容
        entity.setCtx(content);
        // 设置发送者Id
        entity.setUserId(QYApplication.getPersonId());
        entity.setReceivers(toUserIds);// 设置接收者Id
        entity.setGroupId(groupId);// 设置圈id
        entity.setGroupName(groupName);
        // 封装要发送的信息
        msg.setBody(entity);
        return msg;
    }

    /**
     * @param header   头部信息
     *                 聊天对象id
     * @return
     * @author LIUBO
     * @date 2015-3-23下午7:54:54
     * @TODO 打包要发送到服务器上的信息
     */
    public Message packageMsg3(Header header, InviteSharedFriends info) {
        // 向服务器发送的对象
        Message msg = new Message();
        // 设置头部
        msg.setHeader(header);
        // 设置主体内容
        MessageEntity entity = new MessageEntity();
        // 设置文件的名字和大小（聊天信息）
        entity.setAlert(null);
        Users user = DBHelper.getInstance().find(Users.class, "userid=?",
                QYApplication.getPersonId());
        if (user != null)
            // 设置发送者昵称
            entity.setSenderName(user.getUserName());
        // 设置消息的id
        entity.setMsgId(MainActivity.customClient.getSerialId());
        // 设置消息类型
        entity.setFlag(MsgManager.CHAT_WITH_FRIEND);
        entity.setSendDate(DateUtil.getDate(DateUtil.DATE_PATTERN_3));
        //转成json发出
        String json = new Gson().toJson(info.getShareMsgInfo());
        // 设置消息内容
        entity.setCtx(json);
        // 设置发送者Id
        entity.setUserId(QYApplication.getPersonId());
        String[] toUserIds = info.getReceiverId();
        entity.setReceivers(toUserIds);// 设置接收者Id
//        entity.setGroupId(groupId);// 设置圈id
//        entity.setGroupName(groupName);
        // 封装要发送的信息
        msg.setBody(entity);
        for (String toUserId : toUserIds) {
            Contacts contacts = DBHelper.getInstance().find(Contacts.class,
                    "contactid=? and userid=?", toUserId,
                    QYApplication.getPersonId());
            //把消息存在本地
            ChatRecord chatRecord = new ChatRecord(toUserId, MainActivity.customClient.getSerialId(), entity.getCtx(), "", "", Calendar.getInstance().getTime(),
                    false, null, QYApplication.getPersonId(), MsgManager.SHARE_TYPE, 1, "", "", false);
            chatRecord.save();
            String conditions[] = DBHelper.getInstance().createUSM(toUserId,
                    MsgManager.CHAT_WITH_FRIEND);
            //根据聊天列表是否创建，创建新的聊天窗口
            if (!DBHelper.getInstance().isExist(SessionMsg.class, conditions)) {// 判断该聊天信息是否出现在社版中心
                DBConversion.getInstance().getSessionMsg(chatRecord, contacts.getUserName()).saveThrows();
            } else {
                DBHelper.getInstance().updateSessionMsg(chatRecord);
            }
        }
        return msg;
    }

    /**
     * @param msg
     * @param fileRemotePath
     * @return
     * @author LIUBO
     * @date 2015-4-12下午1:21:59
     * @TODO 打包分享的消息
     */
    public Message packageShareMsg(Message msg, String fileRemotePath) {
        MessageEntity body = (MessageEntity) msg.getBody();
        body.setFilePath(fileRemotePath);
        msg.setBody(body);
        return msg;
    }

    /**
     * @param info
     * @param operation
     * @TODO 发送分享
     */
    public void sendSharedMsg(InviteSharedFriends info, ResultOperation operation) {
        Header header = new Header();
        header.setType(MessageType.SHARE.value());
        Message msg = packageMsg3(header, info);
        msg = packageShareMsg(msg, "");
        try {
            MainActivity.customClient.send(msg, operation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
