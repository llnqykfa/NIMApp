package com.nzy.nim.db.tmpbean;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.GroupMembers;
import com.nzy.nim.db.bean.Interests;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.RingComments;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.vo.HobbyVO;
import com.nzy.nim.vo.MeetingInfoVo;
import com.nzy.nim.vo.MeetingMsg;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.MyInterests;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.ProjectMeetingInfo;
import com.nzy.nim.vo.ProjectMeetingMsg;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingCommentVO;
import com.nzy.nim.vo.RingList;
import com.nzy.nim.vo.RingThemeVO;
import com.nzy.nim.vo.RingThemesTmp;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.core.bootstrap.MessageType;
import org.core.bootstrap.property.Header;
import org.core.bootstrap.property.Message;
import org.core.bootstrap.property.im.MessageEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * @todo 服务器上的数据转换为本地数据
 */
public class DBConversion {

    private static DBConversion dbConversion;

    private DBConversion() {
    }

    public static DBConversion getInstance() {
        if (dbConversion == null) {
            synchronized (DBConversion.class) {
                if (dbConversion == null)
                    dbConversion = new DBConversion();
            }
        }
        return dbConversion;
    }

    /**
     * @param pv
     * @return
     * @todo 获取登陆用户的信息
     */
    public UserInfo getUser(PersonVO pv) {
        if (DataUtil.isEmpty(pv))
            return null;
        UserInfo userInfo = new UserInfo();
        if (pv.getSex() == null || pv.getSex())
            userInfo.setSex(true);
        else
            userInfo.setSex(false);
        userInfo.setDesignInfo(pv.getDesignInfo());
        userInfo.setUserName(pv.getUserName());
        userInfo.setPersonId(pv.getPersonId());
        userInfo.setSchoolName(pv.getSchoolName());
        userInfo.setCode(pv.getCode());
        userInfo.setPhotoPath(pv.getPhotoPath());
        return userInfo;
    }


    /**
     * @param msg   服务器上发来的消息
     * @param title 标题
     *              头像
     * @return
     * @todo 将从服务器上的消息转换成消息中心表的数据对象(其他人发的)
     */
    public SessionMsg getSessionMsg(Message msg, String title,
                                    boolean isGroupMsg) {
        if (msg == null)
            return null;
        Header header = msg.getHeader();// 获取头部
        MessageEntity body = (MessageEntity) msg.getBody();// 获取主体
        int mainType = setSessionMainType(body.getFlag());// 社版消息的大类
        SessionMsg receMsg = new SessionMsg();
        receMsg.setTitles(title);// 标题
        if (!DataUtil.isEmpty(body.getSendDate()))
            receMsg.setCreatTime(DateUtil.strToDate(body.getSendDate(),
                    DateUtil.DATE_PATTERN_3));// 设置消息时间
        else
            receMsg.setCreatTime(new Date());
        receMsg.setSubType(body.getFlag());// 设置具体消息类型
        receMsg.setMainType(mainType);// 设置大类消息类型
        receMsg.setNewContent(setSessionContent(header.getType(), body,
                isGroupMsg, true));// 内容
        receMsg.setGroup(isGroupMsg);// 群消息标志
        if (mainType == MsgManager.CHAT_TYPE) {// 聊天类型消息
            if (isGroupMsg) {// 发送者Id(群消息时为群Id)
                receMsg.setSendUserId(body.getGroupId());
                receMsg.setImgUrl(getImgUrl(body.getGroupId(), true));
            } else {
                receMsg.setSendUserId(body.getUserId());
                receMsg.setImgUrl(getImgUrl(body.getUserId(), false));
            }
        } else {// 其他消息（如系统消息）
            receMsg.setSendUserId(UUID.randomUUID().toString());
            receMsg.setImgUrl(null);
        }
        receMsg.setUnLookedMsgCount(1);// 设置未读消息数量
        receMsg.setUserId(QYApplication.getPersonId());// 用户

        return receMsg;
    }


    /**
     * @return
     * @TODO 获取头像路经
     */
    public String getImgUrl(final String id, boolean isGroup) {
        String url = "";
        SessionMsg sessionMsg = DBHelper.getInstance().find(SessionMsg.class,
                "senduserid=?", id);
        if (isGroup) {
        } else {
            Contacts contact = DBHelper.getInstance()
                    .find(Contacts.class, "userid=? and contactid=?",
                            QYApplication.getPersonId(), id);
            if (contact != null) {
                url = contact.getPhotoPath();
            } else {
                getHeadImgFromNet(id);
            }
        }
        return url;
    }


    private void getHeadImgFromNet(final String id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("targetPersonId", id);
        HTTPUtils.postWithToken(QYApplication.getMyContexts(), URLs.GET_PERSON_INFO, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                Log.e("UserInfo", s);
                try {
                    PersonVO personVO = new Gson().fromJson(new JSONObject(s).getString("person"), PersonVO.class);
                    ContentValues values = new ContentValues();
                    values.put("imgUrl", personVO.getPhotoPath());
                    DataSupport.updateAll(SessionMsg.class, values, "sendUserId=?", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * @param type 聊天信息的类型
     * @param body
     * @return
     * @TODO(功能) 设置社版中心的消息内容
     * @mark(备注)
     */
    public String setSessionContent(int type, MessageEntity body,
                                    boolean isGroup, boolean isCom) {
        int flag = body.getFlag();
        if (flag == MsgManager.CHAT_WITH_FRIEND) {// 聊天类型的信息
            return setCMContent(setChatMsgType(type), body.getCtx(),
                    body.getSenderName(), isGroup, isCom);
        } else if (flag == MsgManager.FRIEND_BE_AGREED) {
            return (body.getSenderName() + "和你已经是好友啦,快去聊天吧！");

        } else if (flag == MsgManager.RING_APPLY_BE_AGREED) {
            return "你已经是" + body.getGroupName() + "的成员了,快去看看吧！";
        } else if (flag == MsgManager.RING_BE_REJECTED) {
            return body.getSenderName() + "未同意你加入" + body.getGroupName();
        } else if (flag == MsgManager.FRIEND_BE_REJECTED) {
            return body.getSenderName() + "拒绝了你的好友申请";
        } else if (flag == MsgManager.RING_ADD_REQUEST) {
            return body.getSenderName() + "请求加入" + body.getGroupName();
        } else if (flag == MsgManager.RING_INVITE_BE_AGREED) {
            return body.getSenderName() + "同意加入" + body.getGroupName();
        } else if (flag == MsgManager.NEW_MEETING) {
            return "新的会议";
        }
        // 其他通知信息
        return body.getAlert();

    }

    /**
     * @param flag
     * @return
     * @TODO(功能) 设置消息中心的大类消息类型
     * @mark(备注)
     */
    public int setSessionMainType(int flag) {
        switch (flag) {
            // 接收到的通知类型
            case MsgManager.FRIEND_REQUEST:
            case MsgManager.RING_ADD_REQUEST:
            case MsgManager.RING_BE_REJECTED:
            case MsgManager.RING_BE_INVITED:
            case MsgManager.RING_INVITE_BE_AGREED:
                return MsgManager.SYSTEM_TYPE;// 系统消息
            // 和聊天有关的类型
            case MsgManager.CHAT_WITH_FRIEND:
            case MsgManager.FRIEND_BE_AGREED:
            case MsgManager.RING_APPLY_BE_AGREED:
                return MsgManager.CHAT_TYPE;// 聊天消息
            case MsgManager.NEW_MEETING://会议
                return MsgManager.THE_MEETING_TYPE;
            case MsgManager.SYSTEM_NOTIFICATIONS://系统通知
                return MsgManager.SYSTEM_NOTIFICATIONS_TYPE;
            case MsgManager.PROJEST_MEETING://专题会议
                return MsgManager.PROJEST_MEETING_TYPE;
            default:
                return -100;
        }
    }

    /**
     * 将聊天消息更新到社版上，(本人发的)
     *
     * @param chat
     * @param title
     * @return
     */
    public SessionMsg getSessionMsg(ChatRecord chat, String title) {
        if (chat == null || chat.isCom())
            return null;
        SessionMsg receMsg = new SessionMsg();
        receMsg.setCreatTime(chat.getCreatTime());// 时间
        receMsg.setTitles(title);// 消息标题
        receMsg.setSubType(MsgManager.CHAT_WITH_FRIEND);// 具体消息类型
        receMsg.setMainType(MsgManager.CHAT_TYPE);// 大类消息
        receMsg.setUserId(QYApplication.getPersonId());// 用户id
        receMsg.setNewContent(setCMContent(chat.getMsgType(),
                chat.getContent(), null, false, false));// 设置内容
        receMsg.setToDefault("unLookedMsgCount");// 未读条数置零
        if (chat.isGroup())// 聊天对象的Id
            receMsg.setSendUserId(chat.getGroupId());
        else
            receMsg.setSendUserId(chat.getChatUserId());
        receMsg.setGroup(chat.isGroup());
        return receMsg;

    }

    /**
     * @param type     聊天记录的消息类型
     * @param content1 消息内容
     * @return
     * @TODO(功能)消息中心根据消息类型设置不同的消息内容
     * @mark(备注)
     */
    public String setCMContent(int type, String content1, String sendName,
                               boolean isGroup, boolean isCom) {
        String content = "";
        switch (type) {
            case MsgManager.TEXT_TYPE:
                content = content1;
                break;
            case MsgManager.PICTURE_TYPE:
                content = "[图片]";
                break;
            case MsgManager.FILE_TYPE:
                content = "[文件]";
                break;
            case MsgManager.FILE_TXT_TYPE:
                content = "[好友邀请]";
                break;
            case MsgManager.SHARE_TYPE:
                content = "[分享]";
                break;
        }
        return showCMContent(content, sendName, isGroup, isCom);
    }

    private String showCMContent(String content, String sendName,
                                 boolean isGroup, boolean isCom) {
        if (isCom && isGroup) {
            return sendName + ":" + content;
        } else {
            return content;
        }
    }

    /**
     * @param msg           服务器的消息实体
     * @param isCom         消息动向
     *                      消息内容（接收的消息可以设置为空）
     * @param localFilePath 本地文件路径（接收的消息可以设置为空）
     * @return
     * @TODO 获取单个聊天记录
     */
    public ChatRecord getChatMsg(Message msg, boolean isCom,
                                 String localContent, String localFilePath) {
        if (msg == null)
            return null;
        // 获取消息的头部信息
        Header header = msg.getHeader();
        // 获取消息的实体信息
        MessageEntity entity = (MessageEntity) msg.getBody();
        // 聊天记录实例
        ChatRecord chat = new ChatRecord();
        chat.setCom(isCom);// 来源
        if (!DataUtil.isEmpty(entity.getSendDate()))
            chat.setCreatTime(DateUtil.strToDate(entity.getSendDate(),
                    DateUtil.DATE_PATTERN_3));// 时间
        else
            chat.setCreatTime(new Date());
        chat.setUserId(QYApplication.getPersonId());// 用户id
        chat.setMsgId(entity.getMsgId());// 设置消息Id
        chat.setMsgType(setChatMsgType(header.getType()));// 消息类型
        // 文件类型消息的文件名和大小的保存
        if (header.getType() == MessageType.FILE_MESSAGE.value()
                && !TextUtils.isEmpty(entity.getAlert())) {
            String[] str = entity.getAlert().split("\\|");
            if (str != null && str.length == 2) {
                chat.setFileName(str[0]);// 设置文件名
                chat.setFileSize(str[1]);// 设置文件大小
            }
        }
        if (isCom) {// 接收的消息(保存发送者的昵称)
            chat.setChatUserId(entity.getUserId());// 聊天对象id
            chat.setRemoteFilePath(entity.getFilePath());// 远程文件路径
            chat.setLocalFilePath("");// 本地文件路径
            chat.setContent(entity.getCtx());// 内容
            // 判断是不是文件类型的消息
            if (header.getType() == MessageType.FILE_MESSAGE.value()) {
                chat.setMsgState(MsgManager.MSG_FILE_NOT_DOWN);
            } else {
                chat.setMsgState(MsgManager.MSG_NO_STATE);// 设置接收消息状态
            }
        } else {
            if (!DataUtil.isEmpty(entity.getReceivers()))
                chat.setChatUserId(entity.getReceivers()[0]);
            chat.setMsgState(MsgManager.MSG_STATE_ONGOING);// 设置发送消息状态
            chat.setContent(localContent);// 发送的消息内容
            chat.setLocalFilePath(localFilePath);// 本地文件路径
            chat.setRemoteFilePath("");// 远程文件路径
        }
        // 判断圈id是否为空
        if (DataUtil.isEmpty(entity.getGroupId())
                || header.getType() == MessageType.TXT_FILE_MESSAGE.value()) {// 群id为空时
            chat.setGroupId(entity.getGroupId());
            chat.setGroup(false);
        } else {// 群id不为空时
            chat.setGroupId(entity.getGroupId());// 群id
            chat.setGroup(true);
        }
        return chat;
    }

    /**
     * @param i
     * @todo 保存聊天消息类型
     */
    public int setChatMsgType(int i) {
        if (i == MessageType.TXT_MESSAGE.value())// 文本类型
            return MsgManager.TEXT_TYPE;
        if (i == MessageType.PICTURE_MESSAGE.value())// 图片类型
            return MsgManager.PICTURE_TYPE;
        if (i == MessageType.SPEECH_MESSAGE.value())// 语音类型
            return MsgManager.VOICE_TYPE;
        if (i == MessageType.FILE_MESSAGE.value())// 普通文件
            return MsgManager.FILE_TYPE;
        if (i == MessageType.TXT_FILE_MESSAGE.value())// 图文并茂类型
            return MsgManager.FILE_TXT_TYPE;
        if (i == MessageType.SHARE.value())// 分享类型
            return MsgManager.SHARE_TYPE;
        return -1;
    }

    /**
     * @return
     * @todo 获取联系人列表
     */
    @SuppressLint("DefaultLocale")
    public List<Contacts> getContacts(List<PersonVO> myList) {
        if (DataUtil.isEmpty(myList))
            return new ArrayList<Contacts>();
        List<Contacts> listpeo = new ArrayList<Contacts>();
        for (PersonVO pv : myList) {
            Contacts people = new Contacts();
            people.setCode(pv.getCode());
            people.setContactId(pv.getPersonId());
            people.setPhotoPath(pv.getPhotoPath());
            people.setUserName(pv.getUserName());
            people.setRemark(pv.getRemark());
            if(!DataUtil.isEmpty(pv.getGrade())){
                people.setGrade(pv.getGrade());
            }else{
                people.setGrade("");
            }
            if(!DataUtil.isEmpty(pv.getMajor())){
                people.setMajor(pv.getMajor());
            }else{
                people.setMajor("");
            }
            if(!DataUtil.isEmpty(pv.getDepartment())){
                people.setDepartment(pv.getDepartment());
            }else{
                people.setDepartment("");
            }
            if(!DataUtil.isEmpty(pv.getCollege())){
                people.setCollege(pv.getCollege());
            }else{
                people.setCollege("");
            }
            if(!DataUtil.isEmpty(pv.getaClassId())){
                people.setaClassId(pv.getaClassId());
            }else{
                people.setaClassId("");
            }
            if(!DataUtil.isEmpty(pv.getSchoolName())){
                people.setSchoolName(pv.getSchoolName());
            }else{
                people.setSchoolName("");
            }
            if (!DataUtil.isEmpty(pv.getRemark()))
                people.setFirstLetter(StringUtil.converterToFirstSpell(
                        pv.getRemark()).toLowerCase());
            else
                people.setFirstLetter(StringUtil.converterToFirstSpell(
                        pv.getUserName()).toLowerCase());

            people.setDesignInfo(pv.getDesignInfo());
            if (pv.getSex() == null || pv.getSex())
                people.setSex(true);
            else
                people.setSex(true);
            people.setUserId(QYApplication.getPersonId());
            listpeo.add(people);
        }
        return listpeo;
    }
    /**
     * @param pv
     * @return
     * @todo 获取单个联系人信息
     */
    @SuppressLint("DefaultLocale")
    public Contacts getContact(OldPersonVO pv) {
        if (pv == null)
            return new Contacts();
        Contacts people = new Contacts();
        if (!DataUtil.isEmpty(pv.getPhotopath())) {
            people.setPhotoPath(pv.getPhotopath());
        }
        people.setUserId(QYApplication.getPersonId());// 当前登陆用户Id
        if (pv.getPk_person() != null) {
            people.setContactId(pv.getPk_person());// 该好友的Id
        }
        if (pv.getUsername() != null) {
            people.setUserName(pv.getUsername());
            people.setFirstLetter(StringUtil
                    .converterToFirstSpell(pv.getUsername()).toLowerCase());
        }
        if (pv.getSex() == null || pv.getSex())
            people.setSex(true);
        else
            people.setSex(false);
        if (pv.getCode() != null) {
            people.setCode(pv.getCode());
        }
//		if(pv.getSchoolName()!=null){
//			people.setSchoolName(pv.getSchoolName());
//		}
        if (pv.getDesigninfo() != null) {
            people.setDesignInfo(pv.getDesigninfo());
        }
        return people;
    }

    /**
     * @param list
     * @param groupId
     * @return
     * @todo 获取圈内的成员
     */
    public List<GroupMembers> getMembers(List<OldPersonVO> list, String groupId) {
        if (list == null || groupId == null)
            return null;
        List<GroupMembers> members = new ArrayList<GroupMembers>();
        for (OldPersonVO pv : list) {
            GroupMembers member = new GroupMembers();
            member.setGroupId(groupId);
            member.setMemberId(pv.getPk_person());
            member.setCode(pv.getCode());
            if (!DataUtil.isEmpty(pv.getPhotopath()))
                member.setImgUrl(pv.getPhotopath());
            member.setMark(pv.getUsername());
            member.setName(pv.getUsername());
            member.setSign(pv.getDesigninfo());
            if (pv.getSex() == null || pv.getSex())
                member.setSex("男");
            else
                member.setSex("女");
            members.add(member);
        }
        return members;
    }

    /**
     * @param themes
     * @return
     * @todo 获取群的集合
     */
    public List<MyGroups> getGroups(List<RingThemeVO> themes) {
        if (themes == null)
            return new ArrayList<MyGroups>();
        List<MyGroups> groups = new ArrayList<MyGroups>();
        for (RingThemeVO rt : themes) {
            MyGroups group = getGroup(rt);
            groups.add(group);
        }
        return groups;
    }

    /**
     * @param rings
     * @return
     * @TODO 获取组圈的多条展示数据
     */
    public List<RingThemesTmp> getTmpRings(List<RingThemeVO> rings) {
        if (rings == null || rings.size() == 0)
            return new ArrayList<RingThemesTmp>();
        List<RingThemesTmp> themeTmps = new ArrayList<RingThemesTmp>();
        for (RingThemeVO rt : rings) {
            RingThemesTmp tmp = getTmpRing(rt);
            themeTmps.add(tmp);
        }
        return themeTmps;
    }

    /**
     * @param ring
     * @return
     * @TODO 获取组圈单条展示数据
     */
    public RingThemesTmp getTmpRing(RingThemeVO ring) {
        if (ring == null)
            return new RingThemesTmp();
        RingThemesTmp tmp = new RingThemesTmp();
        tmp.setGroupId(ring.getPk_ringtheme());// 圈Id
        tmp.setGroupName(ring.getTheme());// 圈名
        tmp.setGroupImg(ring.getFirstRingPath());// 圈的头像
        tmp.setInitatorId(ring.getInitatorId());// 创建者id
        tmp.setInitator(ring.getInitator());// 创建者昵称
        tmp.setCreateTime(DateUtil.strToDate(ring.getDateTime(),
                DateUtil.DATE_PATTERN_3));
        tmp.setUserPhotoPath(ring.getUserPath());// 发起者的头像
        tmp.setPicPath(spliceUrl(ring.getPhotoPath(), ring.getPhotoNames()));// 所有图片的路径
        if (ring.getTotalNum() != null)// 设置该群的人数
            tmp.setTotalNum(ring.getTotalNum());
        else
            tmp.setTotalNum(0);
        tmp.setIntroduce(ring.getIntroduce());
        if (ring.getIsPraise() != null)
            tmp.setPraise(ring.getIsPraise());
        else
            tmp.setPraise(false);
        if (ring.getTotalComment() != null)
            tmp.setReviewNum(ring.getTotalComment());
        else
            tmp.setReviewNum(0);
        if (ring.getPraiseNum() != null) {
            tmp.setPraiseNum(ring.getPraiseNum());
        } else {
            tmp.setPraiseNum(0);
        }
        return tmp;
    }

    /**
     * @param theme
     * @return
     * @todo 获取单个群信息
     */
    public MyGroups getGroup(RingThemeVO theme) {
        if (theme == null)
            return new MyGroups();
        MyGroups group = new MyGroups();
        group.setUserId(QYApplication.getPersonId());// 用户id
        group.setGroupId(theme.getPk_ringtheme());// 圈Id
        group.setGroupName(theme.getTheme());// 圈名
        group.setGroupImg(theme.getFirstRingPath());// 圈的头像
        if (!DataUtil.isEmpty(theme.getDateTime()))
            group.setJoinTime(DateUtil.strToDate(theme.getDateTime(),
                    DateUtil.DATE_PATTERN_3));// 加入组圈时间
        else
            group.setJoinTime(new Date());
        return group;
    }

    public MyGroups getGroup(RingThemesTmp theme) {
        if (theme == null)
            return new MyGroups();
        MyGroups group = new MyGroups();
        group.setUserId(QYApplication.getPersonId());// 用户id
        group.setGroupId(theme.getGroupId());// 圈Id
        group.setGroupName(theme.getGroupName());// 圈名
        group.setGroupImg(theme.getGroupImg());// 圈的头像
        group.setJoinTime(new Date());
        return group;
    }

    /**
     * @param body
     * @param status
     * @return
     * @TODO 获取邀请通知
     */
    public Invitations getInvites(MessageEntity body, int status) {
        if (body == null)
            return new Invitations();
        final Invitations invite = new Invitations();
        HttpHelper.getPerson(body.getUserId(), new HttpUtil.OnPostListener() {
            @Override
            public void onSuccess(String jsonData) {
                if (!DataUtil.isEmpty(jsonData)) {
                    PersonVO person = JSON
                            .parseObject(jsonData, PersonVO.class);
                    if (!DataUtil.isEmpty(person.getPhotoPath())) {// 保存头像
                        invite.setImgUrl(person.getPhotoPath());
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=body.getSendDate();
        try {
            invite.setReceMsgTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        invite.setSendUserId(body.getUserId());
        invite.setSendUserName(body.getSenderName());
        invite.setStatus(status);
        invite.setMsgType(body.getFlag());
        invite.setUserId(QYApplication.getPersonId());
        invite.setMsgId(body.getMsgId());
        invite.setGroupId(body.getGroupId());
        invite.setGroupName(body.getGroupName());
        invite.setLeaveComment(getInvitationTip(body, status));// 提示内容
        return invite;
    }

    /**
     * @return
     * @TODO 设置系统消息的提示内容
     */
    private String getInvitationTip(MessageEntity body, int status) {
        if (status == MsgManager.INVITE_NOT_AGREE) {// 对方未同意的消息
            if (DataUtil.isEmpty(body.getGroupId())) {
                if (DataUtil.isEmpty(body.getCtx()))
                    return "加个好友呗!";
                else
                    return body.getCtx();
            } else {
                if (body.getFlag() == MsgManager.RING_ADD_REQUEST) {
                    if (DataUtil.isEmpty(body.getCtx()))
                        return "申请加入" + body.getGroupName() + "!";
                    else
                        return body.getCtx();
                } else
                    return "邀请你加入" + body.getGroupName() + "!";
            }
        } else if (status == MsgManager.INVITE_BE_REJECTED) {// 被对方拒绝
            if (DataUtil.isEmpty(body.getGroupId())) {
                return "拒绝了你的好友请求!";
            } else {
                return "未允许你加入" + body.getGroupName() + "!";
            }
        } else if (status == MsgManager.INVITE_BE_AGREE) {// 被对方同意
            if (!DataUtil.isEmpty(body.getGroupId()))
                return "已同意加入" + body.getGroupName() + "!";
        }
        return body.getCtx();
    }

    /**
     * @param commentVo
     * @return
     * @TODO 获取组圈的单条评论
     */
    public RingComments getRingComment(RingCommentVO commentVo) {
        if (commentVo == null)
            return new RingComments();
        RingComments comment = new RingComments();
        comment.setCommentatorId(commentVo.getPersonId());
        comment.setCommentDate(DateUtil.strToDate(commentVo.getAddTime(),
                DateUtil.DATE_PATTERN_3));
        comment.setCommentId(commentVo.getPk_ringComment());
        comment.setContent(commentVo.getContent());
        comment.setGroupId(commentVo.getRingId());
        comment.setImgUrl(commentVo.getUserPath());
        comment.setName(commentVo.getUsername());
        if (!DataUtil.isEmpty(commentVo.getPhotoNames())) {
            comment.setPicsPath(spliceUrl(commentVo.getPhotoUrl(),
                    commentVo.getPhotoNames()));
        }
        return comment;
    }

    /**
     * @param basePath
     * @param names
     * @return
     * @TODO 重组图片路径
     */
    private String spliceUrl(String basePath, String names) {
        StringBuffer sb = new StringBuffer();
        if (names == null || basePath == null)
            return null;
        String[] picNames = names.split("_");
        for (int i = 0; i < picNames.length; i++) {
            if (i != picNames.length - 1) {
                sb.append(basePath).append(picNames[i]).append("_");
            } else {
                sb.append(basePath).append(picNames[i]);
            }
        }
        return sb.toString();

    }

    /**
     * @param comments
     * @return
     * @TODO 批量获取组圈评论
     */
    public List<RingComments> getRingComments(List<RingCommentVO> comments) {
        if (DataUtil.isEmpty(comments))
            return new ArrayList<RingComments>();
        List<RingComments> list = new ArrayList<RingComments>();
        for (RingCommentVO com : comments) {
            list.add(getRingComment(com));
        }
        return list;
    }

    /**
     * @param list
     * @TODO 保存我的个人兴趣
     */
    public void saveMyIntersts(List<Interests> list) {
        if (list != null && list.size() > 0)
            DataSupport.saveAll(list);
    }

    /**
     * @param hobbys
     * @return
     * @TODO
     */
    public List<Interests> getAllInterests(List<HobbyVO> hobbys) {
        if (hobbys == null)
            return null;
        List<Interests> interests = new ArrayList<Interests>();
        for (HobbyVO hv : hobbys) {
            interests.add(getInterest(hv));
        }
        return interests;
    }

    /**
     * @param hobby
     * @return
     * @TODO 获取单个兴趣
     */
    public Interests getInterest(HobbyVO hobby) {
        if (hobby == null)
            return null;
        Interests interest = new Interests();
        interest.setIconUrl(hobby.getPath());
        interest.setInterestId(hobby.getPk_hobby());
        interest.setName(hobby.getHobbyName());
        return interest;
    }

    /**
     * @param hobbys
     * @return
     * @TODO 获取我的兴趣
     */
    public List<MyInterests> getMyInterests(List<HobbyVO> hobbys) {
        if (hobbys == null)
            return null;
        List<MyInterests> myHobbies = new ArrayList<MyInterests>();
        for (HobbyVO hv : hobbys) {
            MyInterests mi = new MyInterests();
            mi.setInterestId(hv.getPk_hobby());
            mi.setUserId(QYApplication.getPersonId());
            myHobbies.add(mi);
        }
        return myHobbies;
    }


    /**
     * 获取会议消息
     *
     * @return
     */
    public MeetingMsg getMeetingPushedMsg(MeetingInfoVo meetingMsg) {
        if (meetingMsg == null)
            return null;
        MeetingMsg meetmsg = new MeetingMsg();
        meetmsg.setUserName(meetingMsg.getUserName());
        meetmsg.setBriefDescription(meetingMsg.getBriefDescription());
        meetmsg.setUserid(QYApplication.getPersonId());
        meetmsg.setContent(meetingMsg.getContent());
        meetmsg.setMeetingId(meetingMsg.getMeetingId());
        meetmsg.setReleaseTime(meetingMsg.getReleaseTime());
        meetmsg.setStatus(meetingMsg.getStatus());
        meetmsg.setTitle(meetingMsg.getTitle());
        meetmsg.setUnLookedMsgCount(meetingMsg.getUnLookedMsgCount());
        return meetmsg;
    }
    /**
     * 获取专题会议消息
     *
     * @return
     */
    public ProjectMeetingMsg getProjectMeetingPushedMsg(ProjectMeetingInfo meetingMsg) {
        if (meetingMsg == null)
            return null;
        ProjectMeetingMsg meetmsg = new ProjectMeetingMsg();
        meetmsg.setUserName(meetingMsg.getUserName());
        meetmsg.setBriefDescription(meetingMsg.getBriefDescription());
        meetmsg.setUserid(QYApplication.getPersonId());
        meetmsg.setContent(meetingMsg.getContent());
        meetmsg.setMeetingId(meetingMsg.getMeetingId());
        meetmsg.setReleaseTime(meetingMsg.getReleaseTime());
        meetmsg.setStatus(meetingMsg.getStatus());
        meetmsg.setTitle(meetingMsg.getTitle());
        meetmsg.setUnLookedMsgCount(meetingMsg.getUnLookedMsgCount());
        return meetmsg;
    }


    /**
     * 组圈
     */
    public List<RingList> getRings(List<RingList> rings) {
        if (rings == null || rings.size() == 0)
            return new ArrayList<RingList>();
        List<RingList> themeTmps = new ArrayList<RingList>();
        for (RingList rt : rings) {
            RingList tmp = getRinglist(rt);
            themeTmps.add(tmp);
        }
        return themeTmps;
    }


    public RingList getRinglist(RingList list) {
        if (list == null) {
            return new RingList();
        }
        RingList ringList = new RingList();
        ringList.setDiggNumber(list.getDiggNumber());
        ringList.setIsDigg(list.getIsDigg());
        ringList.setCategoryName(list.getCategoryName());
        ringList.setCommentNumber(list.getCommentNumber());
        ringList.setCreateTime(list.getCreateTime());
        ringList.setDynamicNumber(list.getDynamicNumber());
        ringList.setImage(list.getImage());
        ringList.setInitatorId(list.getInitatorId());
        ringList.setInitatorName(list.getInitatorName());
        ringList.setIntroduce(list.getIntroduce());
        ringList.setIsInclude(list.getIsInclude());
        ringList.setIsOpenComment(list.getIsOpenComment());
        ringList.setRingId(list.getRingId());
        ringList.setTheme(list.getTheme());
        ringList.setCategoryId(list.getCategoryId());
        return ringList;
    }
}
