package com.nzy.nim.db.tmpbean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AppUpdateInfo implements Serializable{
    private String sysVer;//版本号
    private String recordVer;
    private String terminal;//手机终端：android 02，ios 01
    private String recordTimeStamp;//更新时间
    private String downloadUrl;//下载地址
    private String schoolCode;//学校
    private String bizId;
    private String fixDescripe;//更新内容
    private String verName;//版本名


    public String getFixDescripe() {
        return fixDescripe;
    }

    public void setFixDescripe(String fixDescripe) {
        this.fixDescripe = fixDescripe;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getSysVer() {
        return sysVer;
    }

    public void setSysVer(String sysVer) {
        this.sysVer = sysVer;
    }

    public String getRecordVer() {
        return recordVer;
    }

    public void setRecordVer(String recordVer) {
        this.recordVer = recordVer;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getRecordTimeStamp() {
        return recordTimeStamp;
    }

    public void setRecordTimeStamp(String recordTimeStamp) {
        this.recordTimeStamp = recordTimeStamp;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
