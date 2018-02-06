package com.nzy.nim.vo;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("serial")
public class RingThemeDetailVO implements Serializable {

    /**
     * errcode : 0
     * errmsg : 成功！
     * ringTheme : {"image":"http://218.85.133.208:8080/resources/RINGTHEME/201601271009566904.jpg","commentNumber":1,"isInclude":false,"menbers":[{"personPhoto":"http://218.85.133.208:8080/resources/USER/201511151057072980.jpg","personId":"0001SX1000000000X3DF"}],"Integerroduce":"嗨起来\u2026\u2026","ringId":"0001SX10000000001B42","dynamicNumber":1,"initatorName":"福州虎豹骑","latestComment":{"personName":"福州虎豹骑","content":"嗨起来\u2026\u2026"},"latestDynamic":{"personName":"王梅芳","content":"快发红包"},"isDigg":false,"tags":["红包","新年"],"initatorId":"0001SX1000000000X3DF","diggNumber":0,"isOpenComment":true,"createTime":1453860596000,"isManager":false,"theme":"春节活动","menberNumber":1,"categoryName":"未分类","initatorPhoto":"http://218.85.133.208:8080/resources/USER/201511151057072980.jpg"}
     */

    private Integer errcode;
    private String errmsg;
    /**
     * image : http://218.85.133.208:8080/resources/RINGTHEME/201601271009566904.jpg
     * commentNumber : 1
     * isInclude : false
     * menbers : [{"personPhoto":"http://218.85.133.208:8080/resources/USER/201511151057072980.jpg","personId":"0001SX1000000000X3DF"}]
     * introduce : 嗨起来……
     * ringId : 0001SX10000000001B42
     * dynamicNumber : 1
     * initatorName : 福州虎豹骑
     * latestComment : {"personName":"福州虎豹骑","content":"嗨起来\u2026\u2026"}
     * latestDynamic : {"personName":"王梅芳","content":"快发红包"}
     * isDigg : false
     * tags : ["红包","新年"]
     * initatorId : 0001SX1000000000X3DF
     * diggNumber : 0
     * isOpenComment : true
     * createTime : 1453860596000
     * isManager : false
     * theme : 春节活动
     * menberNumber : 1
     * categoryName : 未分类
     * initatorPhoto : http://218.85.133.208:8080/resources/USER/201511151057072980.jpg
     */

    private RingThemeEntity ringTheme;

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setRingTheme(RingThemeEntity ringTheme) {
        this.ringTheme = ringTheme;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public RingThemeEntity getRingTheme() {
        return ringTheme;
    }

    public static class RingThemeEntity {
        private String image;
        private Integer commentNumber;
        private Boolean isInclude;
        private String introduce;
        private String ringId;
        private Integer dynamicNumber;
        private String initatorName;
        /**
         * personName : 福州虎豹骑
         * content : 嗨起来……
         */

        private LatestCommentEntity latestComment;
        /**
         * personName : 王梅芳
         * content : 快发红包
         */

        private LatestDynamicEntity latestDynamic;
        private Boolean isDigg;
        private String initatorId;
        private Integer diggNumber;
        private Boolean isOpenComment;
        private Long createTime;
        private Boolean isManager;
        private String theme;
        private Integer menberNumber;
        private String categoryName;
        private String initatorPhoto;
        /**
         * personPhoto : http://218.85.133.208:8080/resources/USER/201511151057072980.jpg
         * personId : 0001SX1000000000X3DF
         */

        private List<MenbersEntity> menbers;
        private List<String> tags;

        public void setImage(String image) {
            this.image = image;
        }

        public void setCommentNumber(Integer commentNumber) {
            this.commentNumber = commentNumber;
        }

        public void setIsInclude(Boolean isInclude) {
            this.isInclude = isInclude;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public void setRingId(String ringId) {
            this.ringId = ringId;
        }

        public void setDynamicNumber(Integer dynamicNumber) {
            this.dynamicNumber = dynamicNumber;
        }

        public void setInitatorName(String initatorName) {
            this.initatorName = initatorName;
        }

        public void setLatestComment(LatestCommentEntity latestComment) {
            this.latestComment = latestComment;
        }

        public void setLatestDynamic(LatestDynamicEntity latestDynamic) {
            this.latestDynamic = latestDynamic;
        }

        public void setIsDigg(Boolean isDigg) {
            this.isDigg = isDigg;
        }

        public void setInitatorId(String initatorId) {
            this.initatorId = initatorId;
        }

        public void setDiggNumber(Integer diggNumber) {
            this.diggNumber = diggNumber;
        }

        public void setIsOpenComment(Boolean isOpenComment) {
            this.isOpenComment = isOpenComment;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public void setIsManager(Boolean isManager) {
            this.isManager = isManager;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public void setMenberNumber(Integer menberNumber) {
            this.menberNumber = menberNumber;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public void setInitatorPhoto(String initatorPhoto) {
            this.initatorPhoto = initatorPhoto;
        }

        public void setMenbers(List<MenbersEntity> menbers) {
            this.menbers = menbers;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getImage() {
            return image;
        }

        public Integer getCommentNumber() {
            return commentNumber;
        }

        public Boolean isIsInclude() {
            return isInclude;
        }

        public String getIntroduce() {
            return introduce;
        }

        public String getRingId() {
            return ringId;
        }

        public Integer getDynamicNumber() {
            return dynamicNumber;
        }

        public String getInitatorName() {
            return initatorName;
        }

        public LatestCommentEntity getLatestComment() {
            return latestComment;
        }

        public LatestDynamicEntity getLatestDynamic() {
            return latestDynamic;
        }

        public Boolean isIsDigg() {
            return isDigg;
        }

        public String getInitatorId() {
            return initatorId;
        }

        public Integer getDiggNumber() {
            return diggNumber;
        }

        public Boolean isIsOpenComment() {
            return isOpenComment;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public Boolean isIsManager() {
            return isManager;
        }

        public String getTheme() {
            return theme;
        }

        public Integer getMenberNumber() {
            return menberNumber;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getInitatorPhoto() {
            return initatorPhoto;
        }

        public List<MenbersEntity> getMenbers() {
            return menbers;
        }

        public List<String> getTags() {
            return tags;
        }

        public static class LatestCommentEntity {
            private String personName;
            private String content;

            public void setPersonName(String personName) {
                this.personName = personName;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPersonName() {
                return personName;
            }

            public String getContent() {
                return content;
            }
        }

        public static class LatestDynamicEntity {
            private String personName;
            private String content;

            public void setPersonName(String personName) {
                this.personName = personName;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPersonName() {
                return personName;
            }

            public String getContent() {
                return content;
            }
        }

        public static class MenbersEntity {
            private String personPhoto;
            private String personId;

            public void setPersonPhoto(String personPhoto) {
                this.personPhoto = personPhoto;
            }

            public void setPersonId(String personId) {
                this.personId = personId;
            }

            public String getPersonPhoto() {
                return personPhoto;
            }

            public String getPersonId() {
                return personId;
            }
        }
    }
}
