package com.nzy.nim.vo;

import com.nzy.nim.db.bean.DynamicList;

import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class RingThemeDynamicList {

    /**
     * errcode : 0
     * totalPage : 1
     * count : 2
     * errmsg : 成功！
     * pageSize : 10
     * list : [{"personName":"圈1231","createTime":1453872711000,"personId":"0001SX1000000000XRAG","id":"e9dfc49d-cd0f-4d25-81e0-92d04b2820cf","imageList":[],"content":"首胜2"},{"personName":"圈1231","createTime":1453872535000,"personId":"0001SX1000000000XRAG","id":"bcb781c5-f262-412e-9e3c-17e7fd9aaaa0","imageList":["http://218.85.133.208:8080/resources/RINGTHEME/201601211422482829.jpeg"],"content":"首胜1"}]
     * pageNow : 1
     */

    private int errcode;
    private int totalPage;
    private int count;
    private String errmsg;
    private int pageSize;
    private int pageNow;
    /**
     * personName : 圈1231
     * createTime : 1453872711000
     * personId : 0001SX1000000000XRAG
     * id : e9dfc49d-cd0f-4d25-81e0-92d04b2820cf
     * imageList : []
     * content : 首胜2
     */

    private List<DynamicList> list;

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public void setList(List<DynamicList> list) {
        this.list = list;
    }

    public int getErrcode() {
        return errcode;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCount() {
        return count;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNow() {
        return pageNow;
    }

    public List<DynamicList> getList() {
        return list;
    }

}
