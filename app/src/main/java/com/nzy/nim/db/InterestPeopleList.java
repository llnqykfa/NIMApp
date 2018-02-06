package com.nzy.nim.db;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/9.
 */
public class InterestPeopleList implements Serializable {
    private String code;//学号
    private String designinfo;//个性签名
    private String occupation;//
    private String password;//密码
    private String phone;//手机号
    private String photoName;//照片名
    private String photopath;//头像
    private String pk_person;//用户id
    private String schoolid;//学校id
    private String sex;//性别
    private int status;//状态
    private String username;//好友名

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesigninfo() {
        return designinfo;
    }

    public void setDesigninfo(String designinfo) {
        this.designinfo = designinfo;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public String getPk_person() {
        return pk_person;
    }

    public void setPk_person(String pk_person) {
        this.pk_person = pk_person;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
