package com.nzy.nim.db.tmpbean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @TODO 消息通知实体类
 */
public class NotificationBean implements Parcelable {
    public static final int ISGROUP = 0;
    public static final int ISNOTGROUP = 1;
    private String sendId;// 发送id(如果是圈时就为圈Id)
    private String sendUserName;//
    private String title;
    private String content;
    private String msgId;
    private int msgType;
    private int isGroupMsg;

    public int getIsGroupMsg() {
        return isGroupMsg;
    }

    public void setIsGroupMsg(int isGroupMsg) {
        this.isGroupMsg = isGroupMsg;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sendId);
        dest.writeString(sendUserName);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(msgId);
        dest.writeInt(msgType);
        dest.writeInt(isGroupMsg);
    }

    public static final Parcelable.Creator<NotificationBean> CREATOR = new Creator<NotificationBean>() {

        @Override
        public NotificationBean[] newArray(int size) {
            return new NotificationBean[size];
        }

        @Override
        public NotificationBean createFromParcel(Parcel source) {
            NotificationBean bean = new NotificationBean();
            bean.sendId = source.readString();
            bean.sendUserName = source.readString();
            bean.title = source.readString();
            bean.content = source.readString();
            bean.msgId = source.readString();
            bean.msgType = source.readInt();
            bean.isGroupMsg = source.readInt();
            return bean;
        }
    };
}
