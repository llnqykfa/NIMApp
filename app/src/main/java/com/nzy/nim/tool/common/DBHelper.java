package com.nzy.nim.tool.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nzy.nim.api.FileUtils;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.Interests;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.vo.MeetingMsg;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.PersonSession;
import com.nzy.nim.vo.ProjectMeetingMsg;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import org.core.bootstrap.MessageType;
import org.core.bootstrap.property.Header;
import org.core.bootstrap.property.Message;
import org.core.bootstrap.property.im.MessageEntity;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 刘波
 * @date 2015-2-28上午9:11:58
 * @todo 数据库的帮助类（查找数据）
 */
public class DBHelper {
    private static DBHelper dbHelper;

    private DBHelper() {
    }

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper();
                }
            }
        }
        return dbHelper;
    }

    /**
     * @param sql
     * @return
     * @todo 用sql语句查找数据
     */
    public Cursor findBySql(String sql) {
        return DataSupport.findBySQL(sql);
    }

    /**
     * @param clazz
     * @param conditions
     * @return
     * @Author liubo
     * @date 2015-3-10下午5:47:43
     * @TODO(功能) 根据条件查找记录
     * @mark(备注)
     */
    public <T> T find(Class<T> clazz, String... conditions) {
        if (conditions == null) {
            return null;
        }
        List<T> list = DataSupport.where(conditions).find(clazz);
        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }

    public <T> List<T> findAll(Class<T> clazz, String... conditions) {
        return DataSupport.where(conditions).find(clazz);
    }

    public <T> List<T> findAllByOrder(Class<T> clazz, String orderBy,
                                      String... conditions) {
        return DataSupport.where(conditions).order(orderBy).find(clazz);
    }

    /**
     * @param sendUserId
     * @param isGroup
     * @return
     * @TODO 获取社版中心的头像
     */
    public String getHeadImgUri(String sendUserId, boolean isGroup) {
        String uri = "";
        if (isGroup) {
            MyGroups group = find(MyGroups.class, "groupid=?", sendUserId);
            if (group != null)
                uri = group.getGroupImg();
        } else {
            Contacts contact = find(Contacts.class, "contactid=?", sendUserId);
            if (contact != null)
                uri = contact.getPhotoPath();
        }
        return uri;
    }

    /**
     * @param clazz
     * @param conditions (条件为空时，查找所有)
     * @return
     * @author 刘波
     * @date 2015-3-5下午1:19:42
     * @todo 判断本地数据库中是否已经有该条数据
     */
    public <T> boolean isExist(Class<T> clazz, String... conditions) {
        List<T> list = DataSupport.where(conditions).find(clazz);
        if (list.size() > 0)
            return true;
        else
            return false;
    }
    /**
     * @param personId
     * @return
     * @TODO 根据用户id查找用户
     */
    public UserInfo getUserById(String personId) {
        return find(UserInfo.class, "personId=?", personId);
    }

    /**
     * @param usercode
     * @return
     * @author LIUBO
     * @date 2015-3-27下午3:24:28
     * @TODO 根据用户的code查找用户
     */
    public Users getUserByCode(String usercode) {
        return find(Users.class, "usercode=? or phone=?", usercode, usercode);
    }

    /**
     * @TODO 更新用户信息
     */
    public void updateAccessToken(PersonSession session, String personId) {
        ContentValues values = new ContentValues();
        values.put("accessToken", session.getAccessToken());
        values.put("expiresIn", session.getExpiresIn());
        values.put("updateTime", session.getUpdateTime());
        update(PersonSession.class, values, "personId=?", personId);
    }

    /**
     * @return
     * @TODO 获取我的系统通知并根据时间进行排序
     */
    public List<Invitations> getInvitesOrderByTime() {
        return DataSupport.where("userid=? ", QYApplication.getPersonId())
                .order("recemsgtime desc").find(Invitations.class);
    }

    /**
     * @param userId
     * @param toChatId
     * @param offset
     * @param num
     * @return
     * @author 刘波
     * @date 2015-2-28上午9:51:20
     * @todo 获取聊天记录
     */
    public List<ChatRecord> getRecords(String userId, String toChatId,
                                       int offset, int num, boolean isGroup) {
        // 单聊的查询语句
        final String sql = "select * from ChatRecord where userid = '" + userId
                + "' and chatuserid = '" + toChatId + "' and isgroup='0'"
                + " order by id desc limit '" + offset + "','" + num + "'";
        // 群聊的查询语句
        final String sql2 = "select * from ChatRecord where userid = '"
                + userId + "' and groupid='" + toChatId + "' and isgroup = '1'"
                + "  order by id desc limit '" + offset + "','" + num + "'";
        List<ChatRecord> chats = new ArrayList<ChatRecord>();
        Cursor cursor = null;
        if (isGroup) {
            // 查询群聊记录
            cursor = findBySql(sql2);
        } else {
            // 查询单聊记录
            cursor = findBySql(sql);
        }
        try {
            if (cursor != null && cursor.moveToLast()) {
                chats.add(cursorToChatRecord(cursor));
                // 遍历cursor
                while (cursor.moveToPrevious())
                    chats.add(cursorToChatRecord(cursor));
            }
        } finally {
            // 释放资源
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return chats;
    }

    /**
     * @param cursor
     * @return
     * @Author LIUBO
     * @TODO TODO 将cursor数据转换成chatRecord对象集合
     * @Date 2015-1-29
     * @Return ChatRecord
     */
    private ChatRecord cursorToChatRecord(Cursor cursor) {
        if (cursor == null)
            return null;
        ChatRecord chat = new ChatRecord();
        if (cursor.getInt(cursor.getColumnIndex("iscom")) == 0)
            chat.setCom(false);
        else
            chat.setCom(true);
        if (cursor.getInt(cursor.getColumnIndex("isgroup")) == 0)
            chat.setGroup(false);
        else
            chat.setGroup(true);

        chat.setCreatTime(new Date(cursor.getLong(cursor
                .getColumnIndex("creattime"))));
        chat.setChatUserId(cursor.getString(cursor.getColumnIndex("chatuserid")));
        chat.setContent(cursor.getString(cursor.getColumnIndex("content")));
        chat.setGroupId(cursor.getString(cursor.getColumnIndex("groupid")));
        chat.setUserId(cursor.getString(cursor.getColumnIndex("userid")));
        chat.setMsgType(cursor.getInt(cursor.getColumnIndex("msgtype")));
        chat.setMsgId(cursor.getString(cursor.getColumnIndex("msgid")));
        chat.setMsgState(cursor.getInt(cursor.getColumnIndex("msgstate")));
        chat.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
        chat.setFileSize(cursor.getString(cursor.getColumnIndex("filesize")));
        chat.setLocalFilePath(cursor.getString(cursor
                .getColumnIndex("localfilepath")));
        chat.setRemoteFilePath(cursor.getString(cursor
                .getColumnIndex("remotefilepath")));
        return chat;
    }

    /**
     * @param modelClass 实体类
     * @param values     更改的值
     * @param conditions 更改条件
     * @return
     * @Author liubo
     * @date 2015-3-11下午12:02:39
     * @TODO(功能) 根据条件更新本地数据表并返回该对象
     * @mark(备注)
     */
    public <T> T update(Class<T> modelClass, ContentValues values,
                        String... conditions) {
        DataSupport.updateAll(modelClass, values, conditions);
        return find(modelClass, conditions);

    }

    /**
     * @param chat
     * @author 刘波
     * @date 2015-3-3上午10:14:22
     * @todo 更新消息中心的数据(本人发的)
     */
    public void updateSessionMsg(ChatRecord chat) {
        if (chat == null || chat.isCom())
            return;
        String sendUserId = getSessionObjectId(chat);
        // 条件数组
        String[] conditions = {
                "userid = ? and senduserid = ?  and maintype = ?",
                QYApplication.getPersonId(), sendUserId,
                MsgManager.CHAT_TYPE + ""};
        ContentValues values = new ContentValues();

        values.put(
                "newcontent",
                DBConversion.getInstance().setCMContent(chat.getMsgType(),
                        chat.getContent(), null, false, false));
        values.put("creattime", chat.getCreatTime().getTime());
        values.put("imgurl",
                DBConversion.getInstance()
                        .getImgUrl(sendUserId, chat.isGroup()));
        DataSupport.updateAll(SessionMsg.class, values, conditions);
    }

    /**
     * @param <T>
     * @param msg (类型支持ChatRecord，Message )
     * @return
     * @author 刘波
     * @date 2015-3-5上午10:37:16
     * @todo 获取会话对象的Id(消息中心, 群id或者是联系人Id)
     */
    public <T> String getSessionObjectId(T msg) {
        if (msg == null)
            return null;
        String sendUserId = "";
        if (msg instanceof ChatRecord) {// 聊天记录
            ChatRecord chat = (ChatRecord) msg;
            if (DataUtil.isEmpty(chat.getGroupId()))
                sendUserId = chat.getChatUserId();
            else
                sendUserId = chat.getGroupId();
        } else if (msg instanceof Message) {// 服务器的消息主体
            Header header = ((Message) msg).getHeader();
            MessageEntity body = (MessageEntity) ((Message) msg).getBody();
            if (DataUtil.isEmpty(body.getGroupId())
                    || header.getType() == MessageType.TXT_FILE_MESSAGE.value())
                sendUserId = body.getUserId();
            else
                sendUserId = body.getGroupId();
        }
        return sendUserId;
    }


    /**
     * @param subType 具体的消息类型（服务器上的消息类型）
     * @return
     * @Author liubo
     * @date 2015-3-12下午8:50:25
     * @TODO(功能)生成更新消息中心的条件(社版中心)
     * @mark(备注)
     */
    public String[] createUSM(String sendUserId, int subType) {
        String userId = QYApplication.getPersonId();
        int mainType = DBConversion.getInstance().setSessionMainType(subType);
        if (mainType == MsgManager.CHAT_TYPE)// 社版消息中的聊天消息三个条件定位
            return new String[]{"userid=? and senduserid=? and maintype=?",
                    userId, sendUserId, mainType + ""};
        else
            // 不是聊天消息的大类消息两个条件定位
            return new String[]{"userid=? and maintype=?", userId,
                    mainType + ""};
    }


    /**
     * @param message 消息
     * @param title   消息的标题
     * @author 刘波
     * @date 2015-3-5上午10:53:23
     * @TODO 服务器发来的消息更新到社版界面显示
     */
    public void updateSessionMsg(Message message, String title,
                                 boolean isGroupMsg) {
        if (message == null)
            return;
        Header header = message.getHeader();// 获得消息的头部信息
        MessageEntity body = (MessageEntity) message.getBody();// 获得消息的主体信息
        ContentValues values = new ContentValues();
        // 更新未读数量
        values.put("unlookedmsgcount",
                getUnReadCount(message, SessionMsg.class) + 1);
        values.put("titles", title);
        // 更新内容
        values.put(
                "newcontent",
                DBConversion.getInstance().setSessionContent(header.getType(),
                        body, isGroupMsg, true));
        // 更新时间
        values.put("creattime",
                DateUtil.strToDate(body.getSendDate(), DateUtil.DATE_PATTERN_3)
                        .getTime());
        if (DBConversion.getInstance().setSessionMainType(body.getFlag()) == MsgManager.CHAT_TYPE) {// 聊天消息
            if (isGroupMsg)
                values.put(
                        "imgurl",
                        DBConversion.getInstance().getImgUrl(body.getGroupId(),
                                true));
            else
                values.put(
                        "imgurl",
                        DBConversion.getInstance().getImgUrl(body.getUserId(),
                                false));

        }
        DataSupport.updateAll(SessionMsg.class, values,
                createUSM(getSessionObjectId(message), body.getFlag()));

    }


    /**
     * @param <T>
     * @param <T>
     * @param clazz
     * @param msg   消息标题
     * @return
     * @author 刘波
     * @date 2015-3-3下午4:02:01
     * @todo 获取未读信息的条数(社版中的消息)
     */
    private <T> int getUnReadCount(Message msg, Class<T> clazz) {
        MessageEntity body = (MessageEntity) msg.getBody();
        int unReadCount = 0;
        if (body == null)
            return 0;
        if (clazz.getName().equals(SessionMsg.class.getName())) {
            List<SessionMsg> receMsg = DataSupport.select("unlookedmsgcount")
                    .where(createUSM(getSessionObjectId(msg), body.getFlag()))
                    .find(SessionMsg.class);
            if (!DataUtil.isEmpty(receMsg))
                unReadCount = receMsg.get(0).getUnLookedMsgCount();
        }
        return unReadCount;
    }

    /**
     * @param clazz
     * @param chatId
     * @param type
     * @author LIUBO
     * @date 2015-3-27下午6:35:06
     * @TODO 清掉未读消息提示（单条消息）
     */
    public <T> void clearUnReadTips(Class<T> clazz, String chatId, int type) {
        ContentValues values = new ContentValues();
        // 社版消息置零
        if (clazz.getName().equals(SessionMsg.class.getName())) {
            values.clear();
            values.put("unlookedmsgcount", 0);
            DataSupport.updateAll(clazz, values, createUSM(chatId, type));
            return;
        }
    }

    /**
     * @return
     * @author LIUBO
     * @date 2015-3-23下午5:39:19
     * @TODO 统计未读的消息之和
     */
    public int getMsgCenterUnReadSum() {
        return DataSupport.where("userid=?", QYApplication.getPersonId()).sum(
                SessionMsg.class, "unlookedmsgcount", int.class);
    }

    /**
     * @param clazz
     * @param column
     * @return
     * @author LIUBO
     * @date 2015-4-16上午10:55:44
     * @TODO 获取未读消息统计
     */
    public <T> int getUnReadSum(Class<T> clazz, String column) {
        return DataSupport.where("userid=?", QYApplication.getPersonId()).sum(
                clazz, column, int.class);
    }

    /**
     * @param clazz      实体类
     * @param conditions 条件
     * @author 刘波
     * @date 2015-3-3下午7:44:54
     * @todo 删除消息
     */
    public int delete(Class<?> clazz, String... conditions) {
        return DataSupport.deleteAll(clazz, conditions);
    }

    /**
     * @param userId
     * @author LIUBO
     * @date 2015-3-19下午5:41:07
     * @TODO 根据用户id 清空该用户的本地的相关信息
     */
    public void clearAllUserInfo(String userId) {
        String[] conditions = {"userid=?", userId};
        delete(Users.class, conditions);// 删除该用户的用户表记录
        delete(Contacts.class, conditions);// 删除该用户的联系人表记录
        delete(ChatRecord.class, conditions);// 聊天记录表
        delete(Invitations.class, conditions);// 邀请通知表
        delete(MyGroups.class, conditions);// 组圈表
        delete(SessionMsg.class, conditions);// 消息中心表

    }

    /**
     * @param contactId
     * @param contactName
     * @Author liubo
     * @date 2015-3-8上午9:55:28
     * @TODO(功能)清空好友的相关数据
     * @mark(备注)
     */
    public void clearFriendData(String contactId, String contactName) {
        String userId = QYApplication.getPersonId();
        // 清除联系人表
        DataSupport.deleteAll(Contacts.class, "userid=? and contactid=?",
                userId, contactId);
        // 清除聊天记录表
        DataSupport.deleteAll(ChatRecord.class,
                "userid=? and chatUserId=?", userId,
                contactId);
        // 清除消息中心表
        DataSupport.deleteAll(SessionMsg.class, DBHelper.getInstance()
                .createUSM(contactId, MsgManager.CHAT_WITH_FRIEND));
    }

    /**
     * @param chatUserId
     * @Author liubo
     * @date 2015-3-15下午4:57:06
     * @TODO(功能) 删除聊天记录表
     * @mark(备注)
     */
    public void deleteChatRecord(String chatUserId) {
        if (isMyGroup(chatUserId)) {// 删除群聊记录
            delete(ChatRecord.class, "userid=? and groupid=?",
                    QYApplication.getPersonId(), chatUserId);
        } else {// 删除单聊记录
            delete(ChatRecord.class, "userid=? and chatuserid=?",
                    QYApplication.getPersonId(), chatUserId);
        }
    }

    /**
     * @param session
     * @Author liubo
     * @date 2015-3-15下午4:35:33
     * @TODO(功能)删除会话中心的消息
     * @mark(备注)
     */
    public void deleteSession(SessionMsg session) {
        if (session.getSubType() >= 0)
            // 删除会话记录
            delete(SessionMsg.class,
                    createUSM(session.getSendUserId(), session.getSubType()));
        switch (session.getMainType()) {
            case MsgManager.INTEREST_TYPE:// 兴趣中心
                delete(SessionMsg.class, "userid=? and maintype=?",
                        QYApplication.getPersonId(), session.getMainType() + "");
//                ToastUtil.showShort(QYApplication.getContext(),"该消息不能删除");
                break;
            case MsgManager.CHAT_TYPE:// 聊天
                deleteChatRecord(session.getSendUserId());
                break;
            case MsgManager.SYSTEM_TYPE:// 系统消息
                DataSupport.deleteAll(Invitations.class,"userid=? and status=?",QYApplication.getPersonId(),""+MsgManager.INVITE_NOT_AGREE);
                DataSupport.deleteAll(Invitations.class,"userid=? and status=?",QYApplication.getPersonId(),""+MsgManager.INVITE_AGREED);
                break;
            case MsgManager.THE_MEETING_TYPE:// 新的会议
//                DataSupport.deleteAll(SessionMsg.class, "userid=? and titles=?",QYApplication.getPersonId(), "新的会议");
                DataSupport.deleteAll(MeetingMsg.class,"userid=?", QYApplication.getPersonId());
                break;
            case MsgManager.SYSTEM_NOTIFICATIONS_TYPE://系统通知
//                DataSupport.deleteAll(SessionMsg.class, "userid=? and titles=?",QYApplication.getPersonId(), "系统通知");
                DataSupport.deleteAll(Invitations.class,"userid=? and status=?",QYApplication.getPersonId(),""+MsgManager.INVITE_OTHER);
                break;
            case MsgManager.PROJEST_MEETING_TYPE://专题会议
//                DataSupport.deleteAll(SessionMsg.class, "userid=? and titles=?",QYApplication.getPersonId(), "专题会议");
                DataSupport.deleteAll(ProjectMeetingMsg.class,"userid=?",QYApplication.getPersonId());
                break;
        }

    }

    /**
     * @param groupId
     * @return
     * @Author LIUBO
     * @TODO TODO 判断聊天对象是否是群
     * @Date 2015-2-10
     * @Return boolean
     */
    public boolean isMyGroup(String groupId) {
        List<MyGroups> list = DataSupport.where("userid=? and groupid=?",
                QYApplication.getPersonId(), groupId).find(MyGroups.class);
        if (list.size() == 0)
            return false;
        else
            return true;

    }

    /**
     * @param friendId
     * @return
     * @author LIUBO
     * @date 2015-4-5下午9:24:15
     * @TODO 判断是否是我的好友
     */
    public boolean isMyFriend(String friendId) {
        Contacts contact = find(Contacts.class, "userid=? and contactid=?",
                QYApplication.getPersonId(), friendId);
        if (contact == null)
            return false;
        else
            return true;

    }

    /**
     * @param groupId
     * @return
     * @Author liubo
     * @date 2015-3-16下午8:52:59
     * @TODO(功能) 根据groupId查找群
     * @mark(备注)
     */
    public MyGroups getGroup(String groupId) {
        return find(MyGroups.class, "userid=? and groupid=?",
                QYApplication.getPersonId(), groupId);
    }

    /**
     * @param contactId
     * @return
     * @Author liubo
     * @date 2015-3-16下午8:59:52
     * @TODO(功能)根据联系人id查找联系人
     * @mark(备注)
     */
    public Contacts getContact(String contactId) {
        return find(Contacts.class, "userid=? and contactid=?",
                QYApplication.getPersonId(), contactId);
    }

    /**
     * @return
     * @author LIUBO
     * @date 2015-4-13下午6:42:40
     * @TODO 判断所有兴趣是否已经初始化
     */
    public boolean isInitInterestData() {
        List<Interests> list = findAll(Interests.class);
        if (list == null || list.size() == 0)
            return false;
        else
            return true;
    }

    /**
     * @param context
     * @return
     * @author LIUBO
     * @date 2015-4-13下午6:50:35
     * @TODO 保存所有兴趣
     */
    public List<Interests> saveAllInterests(Context context) {
        // 读取配置文件
        List<String> allInterests = FileUtils.getAssetFile(context,
                "interestions");
        if (allInterests == null)
            return null;
        // 临时保存所有的兴趣
        List<Interests> tmpAllInterests = new ArrayList<Interests>();
        try {
            for (String s : allInterests) {
                String[] data = s.split(",");
                String iconName = data[0]
                        .substring(0, data[0].lastIndexOf("."));
                int resID = context.getResources().getIdentifier(iconName,
                        "drawable", context.getPackageName());
                Interests interst = new Interests();
                interst.setIcon(resID);
                interst.setName(data[1]);
                interst.setInterestId(UUID.randomUUID().toString());
                tmpAllInterests.add(interst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataSupport.saveAll(tmpAllInterests);
        return tmpAllInterests;
    }

    /**
     * @param title
     * @param mainType
     * @return
     * @author LIUBO
     * @date 2015-4-14下午4:46:33
     * @TODO 第一次使用的社版初始数据
     */
    public static SessionMsg getFirstSession(String title, String content,
                                             int mainType) {
        SessionMsg session = new SessionMsg();
        session.setCreatTime(new Date());
        session.setImgUrl("");
        session.setNewContent(content);
        session.setMainType(mainType);
        session.setUserId(QYApplication.getPersonId());
        session.setTitles(title);
        session.setUnLookedMsgCount(0);
        return session;
    }

    /**
     * @param clazz
     * @param conditions
     * @author LIUBO
     * @date 2015-4-14下午5:20:45
     * @TODO 判断是否为空
     */
    public <T> boolean isEmpty(Class<T> clazz, String... conditions) {
        List<T> list = findAll(clazz, conditions);
        if (list == null || list.size() == 0) {
            return true;
        } else
            return false;
    }

    /**
     * 获取符合条件的好友
     *
     * @param value
     * @return
     */
    public List<Contacts> getContactsBySql(String value) {
        value = encodeCondition(value);
        StringBuffer sb = new StringBuffer();
        final String sql = sb.append("select * from Contacts where userName like ")
                .append("'%" + value + "%'").append(" or ")
                .append("remark like ").append("'%" + value + "%'").append("or firstLetter like ").append("'%" + value + "%'")
                .append(" and ").append(" userid= ")
                .append("'" + QYApplication.getPersonId() + "'").toString();
        Cursor cursor = findBySql(sql);
        List<Contacts> contactList = new ArrayList<Contacts>();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                contactList.add(cursorToContacts(cursor));
                while (cursor.moveToNext()) {
                    contactList.add(cursorToContacts(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactList;
    }

    /**
     * 获取符合条件的圈
     *
     * @param value
     * @return
     */
    public List<MyGroups> getMyGroupsBySql(String value) {
        value = encodeCondition(value);
        StringBuffer sb = new StringBuffer();
        final String sql = sb
                .append("select * from MyGroups where groupname like ")
                .append("'%" + value + "%'").append(" and ").append("userid=")
                .append("'" + QYApplication.getPersonId() + "'").toString();
        Cursor cursor = findBySql(sql);
        List<MyGroups> groups = new ArrayList<MyGroups>();
        if (cursor != null && cursor.moveToFirst()) {
            groups.add(cursorToMyGroups(cursor));
            while (cursor.moveToNext()) {
                groups.add(cursorToMyGroups(cursor));
            }
        }
        return groups;
    }

    private MyGroups cursorToMyGroups(Cursor cursor) {
        MyGroups group = new MyGroups();
        group.setGroupImg(cursor.getString(cursor.getColumnIndex("groupimg")));
        group.setGroupId(cursor.getString(cursor.getColumnIndex("groupid")));
        group.setGroupName(cursor.getString(cursor.getColumnIndex("groupname")));
        return group;
    }

    private Contacts cursorToContacts(Cursor cursor) {
        Contacts contact = new Contacts();
        contact.setUserName(cursor.getString(cursor.getColumnIndex("username")));
        contact.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
        contact.setPhotoPath(cursor.getString(cursor.getColumnIndex("photopath")));
        contact.setContactId(cursor.getString(cursor
                .getColumnIndex("contactid")));
        return contact;
    }

    /**
     * 处理特殊查询字符
     *
     * @param condition
     * @return
     */
    private String encodeCondition(String condition) {
        return condition.replace("[", "[[]").replace("_", "[_]")
                .replace("%", "[%]").replace("]", "[]]");
    }

}
