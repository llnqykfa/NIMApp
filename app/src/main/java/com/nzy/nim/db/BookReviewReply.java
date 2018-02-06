package com.nzy.nim.db;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")

public class BookReviewReply implements Serializable {
    private Integer errcode;//错误码
    private Integer totalPage;//回复总页数
    private Integer count;//回复总数
    private String errmsg;//错误内容
    private Integer pageSize;//分页大小
    private Integer pageNow;//当前页码
    private ArrayList<BookReviewReplyList> list;//书评列表

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public ArrayList<BookReviewReplyList> getList() {
        return list;
    }

    public void setList(ArrayList<BookReviewReplyList> list) {
        this.list = list;
    }
}
