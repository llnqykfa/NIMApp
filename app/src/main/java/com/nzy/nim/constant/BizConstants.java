package com.nzy.nim.constant;

/**
 * Created by Administrator on 2016/12/06.
 */
public class BizConstants {

    public static final String ACCESS_TOKEN_SALT = "fjzq@quanyou"; //加密令牌
    public static final String BIZSERVER_ADDRESS = "http://api.fjfootball.cn"; //服务器地址
    //public static final String BIZSERVER_ADDRESS = "http://192.168.1.106:8080/FootSport"; //服务器地址
    public static final String ASS_BIZ_URL = "/h5/ass.html"; //会员协会的功能
    public static final String CLUB_BIZ_URL = "/h5/club.html"; //俱乐部的功能
    public static final String COACH_BIZ_URL = "/h5/coach.html"; //教练员的功能
    public static final String REFEREE_BIZ_URL = "/h5/referee.html"; //裁判员的功能
    public static final String PLAYER_BIZ_URL = "/h5/player.html"; //运动员的功能
    public static final String MEMBER_BIZ_URL = "/h5/member.html"; //个人会员的功能
    public static final String MATCH_RECORD_BIZ_URL = "/h5/matchrecord.html"; //赛事备案的功能
    public static final String MERCH_BIZ_URL = "/h5/merchregist.html"; //商家入驻的功能
    public static final String PROD_BIZ_URL = "/h5/merchprod.html"; //品牌展示的功能

    public static final String PAY_NATIVE_URL = "/API/pay/ali/getOrderNative.do"; //订单详情
    public static final String PAY_SIGN_URL = "/API/pay/ali/makeOrder.do"; //订单加签

    public static final String WEIXIN_APP_ID = "wx098f888e2400ed97"; //微信应用ID
    public static final String WEIXIN_SIGN_URL = "/API/pay/weixin/makeOrder.do"; //订单加签
}
