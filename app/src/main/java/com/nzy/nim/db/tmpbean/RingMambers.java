package com.nzy.nim.db.tmpbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class RingMambers implements Serializable{

    /**
     * list : [{"isInitator":false,"id":"0001SX10000000001MO5","sex":true,"designInfo":"吴建聪","college":"外国语","grade":"2013","userName":"寂静","className":"13国际","code":"5896","photo":"http://218.85.133.208:8080/resources//USER/201604270700092212.jpg","major":"英语"},{"isInitator":true,"id":"247523bc-016a-42e7-9df2-60386920d3ba","sex":false,"college":"外国语","grade":"2013","userName":"大象","className":"13国际2","code":"S240390526","photo":"http://218.85.133.208:8080/resources//USER/201607250939109777.jpg","major":"英语2"}]
     * errmsg : 成功！
     * errcode : 0
     */

    private String errmsg;
    private int errcode;
    /**
     * isInitator : false
     * id : 0001SX10000000001MO5
     * sex : true
     * designInfo : 吴建聪
     * college : 外国语
     * grade : 2013
     * userName : 寂静
     * className : 13国际
     * code : 5896
     * photo : http://218.85.133.208:8080/resources//USER/201604270700092212.jpg
     * major : 英语
     */

    private List<ListEntity> list;

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public static class ListEntity {
        private boolean isInitator;//是否为圈主
        private String id;//用户id
        private Boolean sex;//性别 ，true为男  false 女
        private String designInfo;//个性签名
        private String college;//学院
        private String grade;//入学年级
        private String userName;//昵称
        private String aClassId;//所属班级
        private String code;////学号
        private String photo;//头像路径
        private String major;//专业
        private String schoolName;//学校名称

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getaClassId() {
            return aClassId;
        }

        public void setaClassId(String aClassId) {
            this.aClassId = aClassId;
        }

        public boolean isIsInitator() {
            return isInitator;
        }

        public void setIsInitator(boolean isInitator) {
            this.isInitator = isInitator;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean isSex() {
            return sex;
        }

        public void setSex(Boolean sex) {
            this.sex = sex;
        }

        public String getDesignInfo() {
            return designInfo;
        }

        public void setDesignInfo(String designInfo) {
            this.designInfo = designInfo;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }
    }
}
