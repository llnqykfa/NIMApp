package com.nzy.nim.db;

import com.nzy.nim.vo.RingList;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/1/26.
 */
public class RingListVo implements Serializable {
    private String errcode;//错误码
    private String errmsg;//错误内容
    private String lastTime;//获取时间
    private List<RingList> list;//列表

    public List<RingList> getList() {
        return list;
    }

    public void setList(List<RingList> list) {
        this.list = list;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
