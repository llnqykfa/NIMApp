package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/25.
 */
public class PersonSession extends DataSupport implements Serializable {

    private String accessToken; //访问凭证
    private int expiresIn;//访问凭证有效时间
    private String personId;//用户ID
    private String phone;//手机号
    private String refreshToken;//刷新凭证
    private boolean sex;//性别
    private long updateTime;//刷新的时间
    private String userName;//用户昵称

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPhone() {
        return phone;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isSex() {
        return sex;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public String getUserName() {
        return userName;
    }
}
