package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */
public class ProjectMeetingMsg extends DataSupport implements Serializable {
    private int unLookedMsgCount=0;// 消息数量
    private String content;//会议内容
    private String meetingId;//会议ID
    private String briefDescription;//会议简要
    private String status;//消息状态
    private String userName;//发布者
    private String releaseTime;//发布时间
    private String title;//标题
    private String userid;//用户id

    public int getUnLookedMsgCount() {
        return unLookedMsgCount;
    }

    public void setUnLookedMsgCount(int unLookedMsgCount) {
        this.unLookedMsgCount = unLookedMsgCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
