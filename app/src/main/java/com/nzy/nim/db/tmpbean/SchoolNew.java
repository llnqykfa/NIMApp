package com.nzy.nim.db.tmpbean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/5.
 */
public class SchoolNew extends DataSupport implements Serializable {
    private String new_time;//最新时间
    private String persionId;//用户id

    public String getPersionId() {
        return persionId;
    }

    public void setPersionId(String persionId) {
        this.persionId = persionId;
    }

    public String getNew_time() {
        return new_time;
    }

    public void setNew_time(String new_time) {
        this.new_time = new_time;
    }
}
