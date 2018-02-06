package com.nzy.nim.db.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 用户信息表
 * 
 * @author Yi
 * 
 */
@SuppressWarnings("serial")
public class Users extends DataSupport implements Serializable {
	private String userName;// 用户名
	private String userId;// 用户id
	private String userPaw;// 用户密码
	private String schoolId;// 学校id
	private String userCode;// 学生号
	private String imgUrl;// 用户头像路径
	private String Uuid;// 用户标识
	private String sex;// 性别
	private String phone;// 手机号
	private String specialty;// 专业
	private String grade;// 年级
	private String signature;// 个性签名

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	private boolean isRemeberPwd;// 是否记住密码

	public String getUuid() {
		return Uuid;
	}

	public void setUuid(String uuid) {
		Uuid = uuid;
	}

	public boolean isRemeberPwd() {
		return isRemeberPwd;
	}

	public void setRemeberPwd(boolean isRemeberPwd) {
		this.isRemeberPwd = isRemeberPwd;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPaw() {
		return userPaw;
	}

	public void setUserPaw(String userPaw) {
		this.userPaw = userPaw;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

}
