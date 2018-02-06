package com.nzy.nim.db;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class ThemeNoteInfo {

    /**
     * content : 杨梅
     别名：山杨梅、朱红、珠蓉、树梅
     科属：杨梅科杨梅属
     花期：4月
     分布：产于我国江苏、浙江、台湾、贵州、四川、云南、广西或广东等地区
     识别要点
     外观：常绿乔木，树冠圆球形。茎：树皮灰色，老时纵向浅裂。叶：叶革质，无毛，簇于小枝上端；全缘或偶有少数锐锯齿。花：雌雄异株，雄花序圆柱状，常不分枝呈单穗状；雌花序较雄花序短而细瘦。果：核果球状，表面有乳头状凸起；外果皮肉质，味酸甜，成熟时深红色或紫红色。
     功效
     果实可制成药酒，能生津解渴、和胃消食；树皮捣敷能治刀伤出血、跌打损伤。
     食用价值
     果实成熟后酸甜多汁，可生食，或加工成糖水杨梅罐头、果酱、蜜饯、果汁、果干、果酒等食品。

     * createTime : 1463987246000
     * imageList : ["http://218.85.133.208:8080/resources/RINGTHEME/201605231507263068.jpg","http://218.85.133.208:8080/resources/RINGTHEME/201605231507268625.jpg","http://218.85.133.208:8080/resources/RINGTHEME/201605231507264035.jpg"]
     * noteId : 18bb2547-f6ab-46b1-b901-235abc5ee958
     * personId : 0001SX10000000001STD
     * personName : 莹火虫
     * personPhoto : http://218.85.133.208:8080/resources/USER/201603221102068827.jpg
     * ringThemeId : a0512d2a-0d10-4b6b-9d6a-66b6a179f8b5
     * ringThemeInitatorId : 测试内容786f
     * ringThemeName : 植物我们的朋友
     * share : false
     * shareContent : 测试内容e207
     * type : 0
     */

    private DataBean data;
    /**
     * data : {"content":"杨梅\n别名：山杨梅、朱红、珠蓉、树梅\n科属：杨梅科杨梅属\n花期：4月\n分布：产于我国江苏、浙江、台湾、贵州、四川、云南、广西或广东等地区\n识别要点\n外观：常绿乔木，树冠圆球形。茎：树皮灰色，老时纵向浅裂。叶：叶革质，无毛，簇于小枝上端；全缘或偶有少数锐锯齿。花：雌雄异株，雄花序圆柱状，常不分枝呈单穗状；雌花序较雄花序短而细瘦。果：核果球状，表面有乳头状凸起；外果皮肉质，味酸甜，成熟时深红色或紫红色。\n功效\n果实可制成药酒，能生津解渴、和胃消食；树皮捣敷能治刀伤出血、跌打损伤。\n食用价值\n果实成熟后酸甜多汁，可生食，或加工成糖水杨梅罐头、果酱、蜜饯、果汁、果干、果酒等食品。\n","createTime":1463987246000,"imageList":["http://218.85.133.208:8080/resources/RINGTHEME/201605231507263068.jpg","http://218.85.133.208:8080/resources/RINGTHEME/201605231507268625.jpg","http://218.85.133.208:8080/resources/RINGTHEME/201605231507264035.jpg"],"noteId":"18bb2547-f6ab-46b1-b901-235abc5ee958","personId":"0001SX10000000001STD","personName":"莹火虫","personPhoto":"http://218.85.133.208:8080/resources/USER/201603221102068827.jpg","ringThemeId":"a0512d2a-0d10-4b6b-9d6a-66b6a179f8b5","ringThemeInitatorId":"测试内容786f","ringThemeName":"植物我们的朋友","share":false,"shareContent":"测试内容e207","type":0}
     * errCode : 0
     * errMsg :
     */

    private int errCode;
    private String errMsg;
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static class DataBean {
        private String content;//动态或留言内容
        private Long createTime;//创建时间
        private String noteId;//动态ID或留言ID
        private String personId;//创建者ID
        private String personName;//创建者名称
        private String personPhoto;//创建者头像
        private String ringThemeId;//组圈ID
        private String ringThemeInitatorId;//组圈创建者ID
        private String ringThemeName;//组圈名称
        private boolean share;//是否为分享类型，true为分享
        private String shareContent;//分享内容，JSON格式数据
        private int type;//类型，0为动态，1为留言
        private List<String> imageList;//图片列表

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public String getNoteId() {
            return noteId;
        }

        public void setNoteId(String noteId) {
            this.noteId = noteId;
        }

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public String getPersonPhoto() {
            return personPhoto;
        }

        public void setPersonPhoto(String personPhoto) {
            this.personPhoto = personPhoto;
        }

        public String getRingThemeId() {
            return ringThemeId;
        }

        public void setRingThemeId(String ringThemeId) {
            this.ringThemeId = ringThemeId;
        }

        public String getRingThemeInitatorId() {
            return ringThemeInitatorId;
        }

        public void setRingThemeInitatorId(String ringThemeInitatorId) {
            this.ringThemeInitatorId = ringThemeInitatorId;
        }

        public String getRingThemeName() {
            return ringThemeName;
        }

        public void setRingThemeName(String ringThemeName) {
            this.ringThemeName = ringThemeName;
        }

        public boolean isShare() {
            return share;
        }

        public void setShare(boolean share) {
            this.share = share;
        }

        public String getShareContent() {
            return shareContent;
        }

        public void setShareContent(String shareContent) {
            this.shareContent = shareContent;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getImageList() {
            return imageList;
        }

        public void setImageList(List<String> imageList) {
            this.imageList = imageList;
        }
    }
}
