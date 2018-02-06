package com.nzy.nim.vo;

import java.io.Serializable;
import java.math.BigInteger;


/**
 * 仅旧接口使用
 */
public class OldPersonVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pk_person;// 主键

	private String userId;// Id

	private String username;// 昵称

	private Boolean sex;// 性别

	private String code;// 学生号

	private String bothdate;// 出生年月 1992-04-15

	private String schoolid;// 关联学校id

	private String phone;// 手机号

	private String email;// 邮箱

	private String designinfo;// 个性签名

	private String careroomid;// 关注书库id

	private String realname;// 真实名字

	private String password;// 密码

	private int status;// 状态

	private BigInteger totalscore;// 总计积分

	private BigInteger currentscore;// 当前积分

	private String photopath;// 头像路径

	private String photoName;// 头像文件名

	private String hobby;// 爱好字符串,例如1/2/3/4/5

	private String uuid;

	private String RXNJ;//入学年级

	private String ZY;//专业

	private String XB;//系别

	private String XY;//学院

	private Boolean isFriend;//是否好友

	public String getPk_person() {
		return pk_person;
	}

	public void setPk_person(String pk_person) {
		this.pk_person = pk_person;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDesigninfo() {
		return designinfo;
	}

	public void setDesigninfo(String designinfo) {
		this.designinfo = designinfo;
	}

	public String getCareroomid() {
		return careroomid;
	}

	public void setCareroomid(String careroomid) {
		this.careroomid = careroomid;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public BigInteger getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(BigInteger totalscore) {
		this.totalscore = totalscore;
	}

	public BigInteger getCurrentscore() {
		return currentscore;
	}

	public void setCurrentscore(BigInteger currentscore) {
		this.currentscore = currentscore;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBothdate() {
		return bothdate;
	}

	public void setBothdate(String bothdate) {
		this.bothdate = bothdate;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getRXNJ() {
		return RXNJ;
	}

	public void setRXNJ(String rXNJ) {
		RXNJ = rXNJ;
	}

	public String getZY() {
		return ZY;
	}

	public void setZY(String zY) {
		ZY = zY;
	}

	public String getXB() {
		return XB;
	}

	public void setXB(String xB) {
		XB = xB;
	}

	public String getXY() {
		return XY;
	}

	public void setXY(String xY) {
		XY = xY;
	}

	public Boolean getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(Boolean isFriend) {
		this.isFriend = isFriend;
	}
}
