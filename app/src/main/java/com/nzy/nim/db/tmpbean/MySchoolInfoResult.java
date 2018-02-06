package com.nzy.nim.db.tmpbean;

import com.nzy.nim.db.bean.MySchoolInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/10/27.
 */
public class MySchoolInfoResult {
    private int errcode;
    private String errmsg;

    private String lasttime;
    private List<MySchoolInfo> feedList;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }



    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public List<MySchoolInfo> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<MySchoolInfo> feedList) {
        this.feedList = feedList;
    }
}
