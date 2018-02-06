package com.nzy.nim.vo;

import java.io.Serializable;


/**
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-20 下午5:11:13
 * @classify(类别) 实体类
 * @TODO(功能) 保存用户的基本信息
 * @Param(参数)
 * @Remark(备注)
 * 
 */
public class PersonVO implements Serializable {

	/**
	 * authentication : false
	 * code : S241094609
	 * designInfo : 未来，你好！
	 * friend : true
	 * occupation : 0
	 * personId : 0001SX100000000000RU
	 * photoPath : http://218.85.133.208:8080/resources/USER/201512231838059934.jpg
	 * remark : 你猜猜
	 * schoolName :
	 * sex : false
	 * userName : 小孩子
	 */

	private Boolean authentication;//认证状态
	private String code;//学号
	private Long birthday;//生日
	private String designInfo;//个性签名
	private Boolean friend;//是否为好友
	private Integer occupation;//职业
	private String personId;//用户ID
	private String photoPath;//头像地址
	private String remark;//备注
	private String schoolName;//学校名称
	private Boolean sex;//性别
	private String userName;//用户昵称
	private String email;//邮箱
	private String schoolId;//学校ID
	private String grade;//年级
	private String major;//专业
	private String department;//系别
	private String college;//学院
	private String aClassId;//所属班级

	public String getaClassId() {
		return aClassId;
	}

	public void setaClassId(String aClassId) {
		this.aClassId = aClassId;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public Boolean getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Boolean authentication) {
		this.authentication = authentication;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getDesignInfo() {
		return designInfo;
	}

	public void setDesignInfo(String designInfo) {
		this.designInfo = designInfo;
	}

	public Boolean getFriend() {
		return friend;
	}

	public void setFriend(Boolean friend) {
		this.friend = friend;
	}

	public Integer getOccupation() {
		return occupation;
	}

	public void setOccupation(Integer occupation) {
		this.occupation = occupation;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
