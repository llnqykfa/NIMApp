package com.nzy.nim.db.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/3.
 */
public class ShareMsgInfo implements Serializable {
    private String title;
    private String shareContent;
    private String imgPath;
    private String shareType;
    private String actionUri;
    private int style;

    public ShareMsgInfo() {
    }

    public ShareMsgInfo(String title, String shareContent, String imgPath, String shareType, String actionUri, int style) {
        this.title = title;
        this.shareContent = shareContent;
        this.imgPath = imgPath;
        this.shareType = shareType;
        this.actionUri = actionUri;
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getActionUri() {
        return actionUri;
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
