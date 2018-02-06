package com.nzy.nim.vo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/25.
 */
public class UserInfo extends DataSupport implements Serializable {

    /**
     * authentication : false
     * birthday : 682527600000
     * code : S241094504
     * designInfo : hello
     * friend : true
     * email : 123@qq.com
     * occupation : 0
     * personId : 0001SX1000000000XRAG
     * photoPath : http://218.85.133.208:8080/resources//USER/201510091519334748.jpg
     * realName : 吴小海
     * remark : 你猜猜
     * schoolName :
     * sex : true
     * userName : 吴小海
     */

    private Boolean authentication;//认证状态
    private Long birthday;//生日
    private String code;//学号
    private String designInfo;//个性签名
    private Boolean friend;//是否为好友
    private String email;//邮箱
    private Integer occupation;//职业
    private String personId;//用户ID
    private String photoPath;//头像地址
    private String realName;//真实姓名
    private String remark;//备注
    private String schoolName;//学校名称
    private Boolean sex;//性别
    private String userName;//用户昵称
    private String schoolId;//学校id
    private String phone;//手机号
    private String grade;//年级
    private String major;//专业
    private String department;//系别
    private String college;//学院
    private String aClassId;//所属班级
    private String startYear;//开始学年
    private String endYear;//
    private String term;//学期
    private String currentWeek;//当前周
    private String majorName;//专业名称
    private String enterYear;//入学年份

    public String getEnterYear() {
        return enterYear;
    }

    public void setEnterYear(String enterYear) {
        this.enterYear = enterYear;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(String currentWeek) {
        this.currentWeek = currentWeek;
    }

    public String getaClassId() {
        return aClassId;
    }

    public void setaClassId(String aClassId) {
        this.aClassId = aClassId;
    }

    //    private String classname;
//
//    public String getClassname() {
//        return classname;
//    }
//
//    public void setClassName(String classname) {
//        this.classname = classname;
//    }
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
}
