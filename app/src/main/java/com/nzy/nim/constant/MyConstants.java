package com.nzy.nim.constant;


import com.nzy.nim.tool.common.CommonUtil;

public class MyConstants {
//	public static final String MAILING_ADDRESS = "218.85.133.208";//正式环境
//	public static final String MAILING_ADDRESS = "27.151.115.57";//阳光
//	public static final String MAILING_ADDRESS = "218.85.133.208";

	 public static final String MAILING_ADDRESS = "114.55.10.210";//测试环境
//	 public static final String MAILING_ADDRESS = "192.168.1.119";//内网
//	public static final int MAILING_PORT = 8085;//正式环境
	public static final int MAILING_PORT = 9185;//测试环境
	public static final String SYSTEM_TITLE = "系统消息";
	public static final String SYSTEM_NOTI = "系统通知";
	public static final String NEW_BOOK_RECOMMEND_TITLE = "新书推荐";
	public static final String SYSTEM_NOTIFICATIONS_TITLE = "系统通知";
	public static final String SHEDULE_FAILE = "预定失败,请重新预定！！！";
	public static final String SHEDULE_SUCCESS = "恭喜你,预定成功！！！";
	public static final String GET_SEAT_INFO_FAILE = "加载座位信息失败！！";
	public static final String MODIFY_USERNAME_NOTE = "好名字可以让你的朋友更容易记住你";
	public static final String MODIFY_SIGNATURE_NOTE = "编辑属于自己的个性签名，让自己更有个性";
	public static final String BOOK_RECOMMENED = "图书推荐";
	public static final String MY_BARCODE = "我的二维码";
	public static final String REBOOK_SEAT = "座位预定";
	public static final String MY_SEAT = "我的座位";
	/** 文件保存路径 */
	public static final String BASE_DIR = CommonUtil.getSDPath() + "/Android/data/com.nzy.nim/"; // 基本路径
	public static final String IMAGE_DIR = "/chat/image/";// 聊天图片
	public static final String FILE_DIR = "/chat/file/";// 一般文件
	public static final String VOICE_DIR = "/chat/audio/";// 聊天视频
	public static final String VIDEO_DIR = "/chat/video/";// 聊天语音
	public static final String CAMERA_DIR = "/DCIM/Camera/";// 拍照图片
	// 图片缓存目录
	public static final String CACHE_DIR = "cache/";
	// 文件上传常量
	public static final String REMOTE_SERVER_URL = "http://" + "218.85.133.208"  //正式环境
			+ ":8080/resources/";
//	public static final String REMOTE_SERVER_URL = "http://" + "192.168.1.222"  //测试环境
//			+ ":8020/resources/";

	public static final String RING_FLODER_TYPE = "RINGTHEME";
	public static final String MY_SCHOOL_TYPE = "feed";
	public static final String USER_FLODER_TYPE = "USER";
	public static final String HEAD_PATH = REMOTE_SERVER_URL + "USER/";
	public static final String USER_CHAT = "CHAT/";
	public static final String CHAT_PARAM = "?type=CHAT";
	public static final String NEW_BOOK_PUSH_FLAG = "0";// 新书推荐
	public static final String HOT_BOOK_PUSH_FLAG = "1";// 热门图书推荐
	public static final String BOOK_DETAIL_FALG = "2";// 图书详情
	public static final String SEARCH_BOOK_FLAG = "3";// 检索书籍
	public static final String HOT_BOOK_PUSH_DETAILE = "4";// 热门图书的具体展示
	public static final String STUDENT_VERIFICATION = "5";// 身份验证
	public static final String SINGLE_NEW_BOOK_PUSH = "6";// 新书推荐（单条）
	public static final String SINGLE_HOT_BOOK_PUSH = "7";// 热门图书推荐（单条）

	//分享****************************************
	public static final int SHARE_WITH_PICTURE=0;//有图分享
	public static final int SHARE_ONLY_TEXT=1;//无图分享
	public static final String BOOK_SHARE_TYPE="图书分享";
	public static final String BOOKLIST_SHARE_TYPE="书单分享";
	public static final String RING_SHARE_TYPE="组圈分享";
	public static final String BOOK_REVIEW_TYPE="书评分享";
	public static final String RING_COMMENT_TYPE="组圈评论分享";
	public static final String MEETING_TYPE="会议分享";

}
