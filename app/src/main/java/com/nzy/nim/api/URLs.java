package com.nzy.nim.api;

public class URLs {
    public final static String HTTP_HOST="http://218.85.133.208:8080";//正式环境
//    public final static String HTTP_HOST="http://192.168.1.154:8080";//测试环境
//    public final static String HTTP_HOST="http://218.85.133.208:12000";//测试环境
//    public final static String HTTP_HOST="http://218.85.133.208:9091";//测试环境
//    public final static String HTTP_HOST="http://192.168.1.120:8020";//测试环境
//    public final static String HTTP_HOST="http://218.85.133.208:9091";//测试环境

    public final static String HOST = "218.85.133.208:8080/quanyouserver";
//    public final static String HOST = "27.151.115.57:9001/quanyouserver";//阳光

//外网接口*************************************************************************
    public final static String MEETING_HOST = HTTP_HOST+"/userCenter";
    public final static String USER_CENTER_HOST = HTTP_HOST+"/userCenter/API/v3";
    //会议接口*******************************************************************
    public final static String USER_CENTER_MEET = HTTP_HOST+"/userCenter/API";

    public final static String USER_CENTER2_HOST = HTTP_HOST+"/userCenter/API/Book/v2";

    //	书本************************************************************************
//    public final static String BOOK_URL = "http:// 218.85.133.208:8080/LibDataApp";//外网
//    public final static String BOOK_URL = "http://library.quanyoo.com:9001";//阳光
    public final static String BOOK_URL = "http://27.151.115.57:9001/LibDataApp";//阳光

    public final static String NEW_BOOK_URL="http://218.85.133.208:8080/library-dispatch";

    //public final static String HOST = "server.inside.wondersix.com/api";
    public final static String IMG_HEAD = "http://218.85.133.208:8080/resources";
    //    public final static String IMG_HEAD = "http://27.151.115.57:9001/resources";//阳光
//    public final static String HTTP = "http://";
    public final static String URL_HOST = HTTP_HOST+"/quanyouserver";
//    public final static String URL_HOST = "http://192.168.1.222:8020/quanyouserver";//测试环境

    public final static String UPLOAD_URL = HTTP_HOST+"/quanyouserver/upload";// 文件上传路径
//    public final static String UPLOAD_URL = URL_HOST + "/upload";//测试环境

    /**
     * 公用API接口****************************************************************************
     */
    public final static String GET_BY_PK = URL_HOST + "/common/getByPk.do";// 根据主键获取对象
    public final static String LIST_BY_CLASS = URL_HOST + "/common/getList.do";// 根据类名获取集合
    public final static String UPDATE_OBJECT = URL_HOST + "/common/update.do";// 更新对象
    public final static String LOG_OUT = URL_HOST + "/person/logout.do";// 退出操作
    public final static String GET_SERVER_URL = URL_HOST + "/book/getServerURL.do?";// 获取流通业务远程服务地址
    public final static String SCAN_LOGOUT = URL_HOST + "/system/scanceLogOut.do";// 客户端轮询接口，检测是否强制退出
    public final static String LIST_BY_CAUSE = URL_HOST + "/common/getListByCause.do";// 根据类名和条件获取集合
    public final static String DELETE_BY_PK = URL_HOST + "/common/deleteByPk.do";// 根据主键删除对象
    public final static String SAVE_OBJECT = URL_HOST + "/common/save.do";// 保存对象
    public final static String GET_APP_VERSION_CONFIG = URL_HOST + "/system/getAppConfig.do";// 获取APP更新配置

    /**
     * 兴趣相关地址****************************************************************************
     */
    public final static String ADD_HOBBY = URL_HOST + "/hobby/addHobby.do?";// 批量添加兴趣
    public final static String LIST_RADOM_HOBBY_PERSON = URL_HOST + "/hobby/radomListHobby.do?";// 随机查询某类兴趣用户,默认10条
    public final static String REMOVE_HOBBY = URL_HOST + "/hobby/removeHobby.do?";// 批量移除兴趣
    public final static String LIST_OWNER_HOBBY = URL_HOST + "/hobby/listOwnerHobby.do?";// 获取已选择兴趣列表

    /**
     * 组圈相关地址*********************************************************************************************
     */
    public final static String LIST_BY_RINGID = URL_HOST + "/ringbusi/getRingMenber.do"; // 根据圈id查找圈成员
    public final static String CREATE_RING = URL_HOST + "/ringbusi/createRing.do";// 组圈发起
    public final static String LIST_MY_RING = URL_HOST + "/ringbusi/listMyRing.do";// 获取我的组圈
    public final static String LIST_RINGCOMMENTS = URL_HOST + "/ringbusi/listRingComments.do?";// 根据圈ID获取圈评论数据,参数ringId
    public final static String ACCEPT_RING_REQUEST = URL_HOST + "/ringbusi/acceptRingRequset.do";// 接受用户添加圈成员
    public final static String REJECT_RING_REQUEST = URL_HOST + "/ringbusi/rejectRingRequest.do";// 拒绝圈请求
    public final static String REQUEST_ADD_RING = URL_HOST + "/ringbusi/requestAddRing.do";// 用户请求加入组圈
    public final static String RINGER_DELETE_PERSON = URL_HOST + "/ringbusi/deleteRingPerson.do?";// 圈主剔除圈用户,ringId：圈主题ID,ownerId:圈主用户号，personId:用户号
    public final static String EXIT_RING = URL_HOST + "/ringbusi/exitRing.do?";// 用户退出圈,ringId：圈主题ID,personId：用户号
    public final static String ADD_RING_PRAISE = URL_HOST + "/ringbusi/addRingPraise.do?";// 用户对圈主题点赞
    public final static String USER_AOR_ADDRING = URL_HOST + "/ringbusi/yOrNRingerReq.do?";// 用户同意或者接受圈主邀请加入组圈
    public final static String REQ_FRIENDS_ADDRING = URL_HOST + "/ringbusi/reqFriendsAddRing.do?";// 批量邀请好友加入组圈
    public final static String LIST_MY_ADD_RING = USER_CENTER_HOST + "/ringTheme/getListByPerson.do?";// 获取我创建的组圈
    public final static String LIST_RINGID = USER_CENTER_HOST + "/ringTheme/getRingThemeMemberByRingId.do";// 根据圈id查找圈成员（新）
    public final static String GET_RING_THEME_DETAIL = USER_CENTER_HOST + "/ringTheme/getRingThemeInfo.do";//获取组圈详情
    public final static String DELETE_RING_THEME = USER_CENTER_HOST + "/ringTheme/deleteRingThemeByRingId.do";//删除组圈
    public final static String ADD_RING = USER_CENTER_HOST + "/ringTheme/addRingTheme.do?";//创建组圈
    public final static String RING_LIST_BY_SEARCH = USER_CENTER_HOST + "/ringTheme/getRingThemeByFuzzyAndPersonId.do?";//组圈搜索
    public final static String RING_LIST = USER_CENTER_HOST + "/ringTheme/getRingThemeList.do?";//组圈列表
    public final static String DYNAMIC_NEW_LIST = USER_CENTER_HOST + "/ringTheme/getRingThemeNoteListWichIsInclude.do?";//动态列表（成员列表）改2016-01-31
    public final static String COMMENT_NEW_LIST = USER_CENTER_HOST + "/ringTheme/getRingThemeNoteListWichIsNotInclude.do?";//留言列表列表（非成员列表）改2016-01-31
    public final static String ADD_DYNAMIC_NEW_LIST = USER_CENTER_HOST + "/ringTheme/addRingThemeNote.do?";//添加动态或留言改2016-01-31
    public final static String ADD_DYNAMIC_DEL_LIST = USER_CENTER_HOST + "/ringTheme/deleteRingThemeNoteByNoteId.do?";//删除动态或留言
    public final static String DYNAMIC_INFO = USER_CENTER_HOST + "/ringTheme/getRingThemeNoteByNoteId.do?";//动态或留言详情
    public final static String RINGTHEME_NOTE_REPLYLIST = USER_CENTER_HOST + "/ringTheme/getRingThemeNoteReplyList.do?";//获取组圈动态或留言的回复列表
    public final static String ADD_RINGTHEME_NOTEREPLY = USER_CENTER_HOST + "/ringTheme/addRingThemeNoteReply.do?";//添加组圈动态或留言的回复

    /**
     * 我的书单相关地址****************************************************************************
     */
    public final static String ADD_BOOKLIST = USER_CENTER_HOST + "/Book/addBookList.do?";//创建书单
    public final static String DEL_BOOKLIST = USER_CENTER_HOST + "/Book/deleteBookList.do?";//删除书单
    public final static String BOOKLIST = USER_CENTER_HOST + "/Book/getCollectionBookListList.do?";//书单列表
    public final static String BOOKLIST_INFO = USER_CENTER_HOST + "/Book/getBookListByBookListId.do?";//书单详情
    public final static String COLLECTION_BOOKlIST = USER_CENTER_HOST + "/Book/collectionBookList.do?";//书单详情

    /**
     * 图书搜索****************************************************************************
     */
    public final static String SEARCH_BOOKLIST = USER_CENTER_HOST + "/Book/getBookByFuzzy.do?";//图书搜索

    /**
     * 我的大学相关地址*********************************************************************************************
     */
    public final static String GET_MY_SCHOOLE = URL_HOST + "/feed/getList.do?";//大学动态列表
    public final static String GET_MY_ZONE_INFO = URL_HOST + "/feed/getFeedListByPersonId.do?";//个人动态列表获取
    public final static String GET_MY_SCHOOLE_POINT = URL_HOST + "/feed/doFeedDigg.do?";//我的大学点赞
    public final static String GET_MY_SCHOOLE_REVIEW = URL_HOST + "/feed/addFeedComment.do?";//我的大学点评
    public final static String SEND_MY_SCHOOLE_IMAGE = URL_HOST + "/upload/uploadFeedImage.do?";//我的大学发表图片
    public final static String SEND_MY_SCHOOLE_TEXT = URL_HOST + "/feed/addFeed.do?";//我的大学发布新的动态
    public final static String SEND_MY_SCHOOLE_Inf = URL_HOST + "/feed/getFeedById.do?";//我的大学详情
    public final static String SEND_MY_SCHOOL_VOICE = URL_HOST + "/upload/uploadFeedVoice.do?";//上传录音文件

    /**
     * 会议相关地址*********************************************************************************************
     */
    public final static String MEETING_DETAIL = USER_CENTER_HOST + "/meeting/getMeetingInfo.do?";//会议详情
    public final static String MEETING_RELATERING = USER_CENTER_HOST + "/meeting/relateRingTheme.do?";//关联组圈
    public final static String MEETING_REGISTRATION = USER_CENTER_HOST + "/meeting/registration.do?";//会议报名
    public final static String MEETING_MEETINGSIGNIN = USER_CENTER_HOST + "/meeting/signIn.do?";//会议签到

    public final static String MEETING_AGENDA_LIST = MEETING_HOST + "/API/specMet/getMetAgendaList.do?";//专题会议议程
    public final static String MEETING_SIGN = MEETING_HOST + "/API/specMet/saveMetSign.do?";//专题会议签到

    /**
     * 书本相关地址*********************************************************************************************
     */
    public final static String BOOK_INFO_BY_ID = USER_CENTER_HOST + "/Book/getBookInfoById.do";//通过BookId获取书本详情
    public final static String BOOK_INFO_BY_ISBN = USER_CENTER_HOST + "/Book/getBookInfoByIsbn.do?";//通过ISBN获取书本详情
    public final static String BOOK_COLLECTION_BY_ID = USER_CENTER_HOST + "/Book/collectionBook.do";//收藏
    public final static String BOOK_COLLECTION_LIST = USER_CENTER_HOST + "/Book/getCollectionBookList.do";//收藏列表
    public final static String NEW_BOOK_COMMENT_LIST = USER_CENTER_HOST + "/Book/getReviewList.do";//获取书评列表
    public final static String NEW_BOOK_COMMENT_PRAISE = USER_CENTER_HOST + "/Book/addReviewDigg.do";//书评点赞
    public final static String BOOK_COMMENT_POST = USER_CENTER_HOST + "/Book/addReviewByIsbn.do";//提交书评
    public final static String BOOK_COMMENT_REPlY = USER_CENTER_HOST + "/Book/addBookReviewReply.do";//添加书评回复
    public final static String BOOK_REVIEW_REPLYLIST = USER_CENTER_HOST + "/Book/getBookReviewReplyList.do";//获取书评回复列表
    public final static String MY_BOOKCOMMENT_LIST = USER_CENTER_HOST + "/Book/getBookReviewPageByPersonId.do";//根据用户ID获取书评分页
    public final static String NEW_BOOK_LIST_RANDOM = USER_CENTER_HOST + "/Book/getNewBookListByRandom.do";//新书
    public final static String TOP_BOOK_LIST = USER_CENTER_HOST + "/Book/getTopBook.do";//热门图书
    public final static String GET_HOT_BOOK_REVIEW = USER_CENTER2_HOST+"/getHotBookReview.do";//获取热门书评
    public final static String GET_BOOK_REVIEW = USER_CENTER2_HOST+"/getBookReview.do";//获取书评列表
    public final static String NEW_BOOK_SHELF = BOOK_URL + "/findByRandom";//新书
    public final static String HOT_BOOK_SHELF = BOOK_URL + "/findByReadCount?";//热门图书
    public final static String BOOK_INFOS_DETIALS = BOOK_URL + "/findBookInfo?";//馆藏
    public final static String BOOK_COVER_URL = BOOK_URL + "/findImgUrlByIsbn?isbn=";//封面
    public final static String COLLECTION_BOOK = BOOK_URL + "/collectionBook?";//收藏
    public final static String CANCEL_COLLECTION_BOOK = BOOK_URL + "/cancelCollection?";//取消收藏
    public final static String BORROW_BOOK = BOOK_URL + "/borrowBookByPropNo?";//自助借阅
    public final static String LENT_APPLY_BOOK = BOOK_URL + "/lentApply?";//发起转借申请
    public final static String LENT_BOOK = BOOK_URL + "/lent?";//确认转借
    public final static String SEARCH_BOOK_PROP_NO = BOOK_URL + "/getBookInfoByPropNo?";//根据借阅号检索
    public final static String SHAKE_BOOK = BOOK_URL + "/findByShake";// 从书池摇到一本书
    public final static String SEARCH_BOOK_AnyCode = BOOK_URL + "/findBookDetailsByAnyCode?";//根据任意字段检索
    public final static String SEARCH_BOOK_ISBN = BOOK_URL + "/findBookDetailsByIsbn?";//根据ISBN检索
    public final static String SEARCH_BOOK_NAME = BOOK_URL + "/findBookDetailsByBookName?";//根据书名检索
    public final static String SEARCH_BOOK_AUTHOR = BOOK_URL + "/findBookDetailsByAuthor?";//根据作者检索
    public final static String BOOK_LENT_DETAIL = BOOK_URL + "/findLendHistoryAndBookInfoByCertIdAndPropNoAndLendDate?";//借书记录详情

    /**
     * 图书类（新）****************************************************************************
     */
    public final static String BOOK_HISTORY_BORROWING = NEW_BOOK_URL + "/api/v1/lendHistory/getReturnHistorys?";// 借阅历史
    public final static String BOOK_NOW_BORROWING = NEW_BOOK_URL + "/api/v1/lendHistory/getCurrentHistorys?";// 再借记录
    public final static String BOOK_LENDINFO = NEW_BOOK_URL + "/api/v1/lendHistory/getHistoryInfo?";// 借阅详情

    /**
     * 登陆相关************************************************************************************
     */
    public final static String SEND_PHONE_VERIFY = MEETING_HOST + "/API/commen/sendPhoneVerify.do";//发送验证短信
    public final static String FORGOT_PASSWORD = MEETING_HOST + "/API/commen/forgotPassword.do";//修改密码
    public final static String REGIST_WITH_PHONE = MEETING_HOST + "/API/commen/registWithPhone.do";//注册
    public final static String DO_LOGIN_BY_PHONE = MEETING_HOST + "/API/commen/doLoginByAppWithPhone.do";//登陆
    public final static String REFRESH_ACCESSTOKEN = MEETING_HOST + "/API/commen/refreshAccessToken.do";//刷新AccessToken
    public final static String REGISTRABLE_PHONE = MEETING_HOST + "/API/commen/registrablePhone.do";//验证手机号是否已注册

    /**
     * 用户个人相关************************************************************************************
     */
    public final static String UPDATE_USER_NAME = USER_CENTER_HOST + "/person/updateUserName.do";//修改用户昵称
    public final static String UPDATE_DESIGN_INFO = USER_CENTER_HOST + "/person/updateDesignInfo.do";//修改用户签名
    public final static String UPDATE_SEX = USER_CENTER_HOST + "/person/updateSex.do";//修改用户签名
    public final static String UPDATE_PASSWORD = USER_CENTER_HOST + "/person/updatePassword.do";//修改用户密码
    public final static String UPDATE_PHOTO = USER_CENTER_HOST + "/person/updatePhoto.do";//修改用户头像
    public final static String GET_SCHOOL_LIST = USER_CENTER_HOST + "/school/findSchools.do?";//获得学校列表
    public final static String GET_MAJOR_LIST = USER_CENTER_HOST + "/school/findComMajors.do?";//获得学校列表
    public final static String GET_CLASS_LIST = USER_CENTER_HOST + "/school/findSchoolClassList.do?";//获得班级列表
    public final static String SCHOOL_YEAR_TERM = USER_CENTER_HOST + "/school/createSchoolYearTerm.do?";//创建学年与学期
    public final static String SCHOOL_CURREN_WEEK = USER_CENTER_HOST + "/school/changeTermCurrentWeek.do?";//修改当前周
    public final static String TABLE_LIST = USER_CENTER_HOST + "/school/findSchoolCourseList.do?";//获取课表
    public final static String TABLE_BYWEEK_LIST = USER_CENTER_HOST + "/school/findSchoolCourseListByWeek.do?";//获取课表
    public final static String GET_PERSON_INFO = USER_CENTER_HOST + "/person/getPersonInfo.do";//获取用户信息
    public final static String SENDPHONENUM = URL_HOST + "/system/sendPhoneNum.do";// 忘记密码手机修改,phoneNum：用户手机号码
    public final static String AUTHCODE_SURE = URL_HOST + "/system/authCodeSure.do?";// 验证码确认
    public final static String GET_SCHOO_CLASS_INFO = USER_CENTER_HOST + "/person/saveShoolInfo.do?";//更新用户的学校/班级信息
    public final static String GET_MY_CLASS_INFO = USER_CENTER_HOST + "/person/getUserListByMyClass.do?";//获取我的班级列表信息
    public final static String UPLOAD_USER_PHOTO = URL_HOST + "/system/uploadUserPhoto.do"; // 用户头像上传
    public final static String DO_AUTHENTICATION = USER_CENTER_HOST + "/person/authentication.do";//学号认证

    /**
     * 用户好友相关************************************************************************************
     */
    public final static String UPDATE_FRIEND_REMARK = USER_CENTER_HOST + "/person/updateFriendRemark.do";//修改好友备注
    public final static String GET_FRIENDS_INFO = USER_CENTER_HOST + "/person/getFriends.do";//获取用户好友列表
    public final static String IS_FRIEND = URL_HOST + "/person/isFriend.do";// 判断某个用户是否是当前用户的好友
    public final static String GET_PERSON_LIST = URL_HOST + "/person/getPersonList.do";// 根据条件查询用户列表
    public final static String DELETE_FRIEND = URL_HOST + "/person/deleteFriend.do";// 删除好友
    public final static String REQUEST_FRIEND = URL_HOST + "/person/reqFriend.do";// 请求好友
    public final static String GET_FRIENDS = URL_HOST + "/query/getFriends.do"; // 获取好友列表,参数personId
    public final static String ACCEPT_FRIEND = URL_HOST + "/person/receive.do";// 接受好友请求

    /**
     * 图书馆相关地址***************************************************************
     */
    public final static String CANCLE_SCHEDULE = URL_HOST + "/action/cancelSchedule.do";// 根据座位pk和人员pk取消预定,参数pk_SeatInfo、pk_Person，成功返回message值为OK，否则ERROR
    public final static String CHANGE_SEAT = URL_HOST + "/action/changeSeat.do";// 座位更换，更换新座位，参数SeatID、NewSeatID、pk_Person，成功返回message值为OK，否则ERROR
    public final static String SCHEDULE_SEAT = URL_HOST + "/action/schedule.do";// 根据座位pk和人员pk预定座位,参数pk_SeatInfo、pk_Person，成功返回message值为OK,否则ERROR
    public final static String ARRIVE_SEAT = URL_HOST + "/action/arriveSeat.do";// 预约后到达入座，参数SeatCode、pk_Person，成功返回message值为OK，否则ERROR
    public final static String LEAVE_SEAT = URL_HOST + "/action/leaveSeat.do";// 离开座位,参数SeatCode、pk_Person，成功返回message值为OK，否则ERROR
    public final static String GET_PERSON = URL_HOST + "/query/getPerson.do";// 根据personId查询人员信息
    public final static String GET_SEAT = URL_HOST + "/query/get_vSeat.do";// 根据用户号查询用户是否有座位，参数personId,有返回v_SeatVO
    public final static String GET_ROOM_STAT_INFO = URL_HOST + "/query/getRoomSeatStatInfo.do";// 获取统计信息
    public final static String GET_TABLES = URL_HOST + "/query/getTable.do";// 根据房间教室生成桌子列表，并且包含座位列表
    public final static String GET_MYTABLLE_INFO = URL_HOST + "/query/getMyTable.do?";//查询用户预定的座位及所在桌子的信息(参数personId)
    public final static String GET_LIBINFO_BY_ROOMID = URL_HOST + "/query/getLibraryInfo.do?";//根据roomId查询图书馆信息
    public final static String ADD_CARE = URL_HOST + "/person/addCare.do";// 添加关注
    public final static String GET_FLOORS = URL_HOST + "/query/getFloor.do";// 根据图书馆pk获取楼层资源列表,参数PK_LIBRARY
    public final static String GET_ROOMS = URL_HOST + "/query/getRoom.do";// 根据楼层pk获取楼层书库列表,参数PK_FLOOR
    public final static String GET_SEATS = URL_HOST + "/query/getSeat.do";// 根据书库pk获取书库的座位列表,参数PK_ROOMINFO
    public final static String GET_TRANSFER_SEATS = URL_HOST + "/query/getTransSeatInfo.do"; // 根据教室pk获取转让座位列表,参数RoomID，返回List<TransferSeatVO>
    public final static String RENEW_SEAT = URL_HOST + "/action/renewSeat.do";// 根据座位pk和人员pk续订座位，参数pk_SeatInfo、pk_Person,成功返回message值为OK，否则ERROR
    public final static String TRANS_SEAT = URL_HOST + "/action/transSeat.do"; // 发布座位转让信息,参数SeatID、pk_Person、message、FriendID，成功返回message值为OK，否则ERROR
    public final static String SET_ATTINTION = URL_HOST + "/action/setAttention.do"; // 设置关注教室剩余多少个通知时通知用户，参数PK_ROOMINFO、pk_Person、num（剩余座位数），成功返回message值为OK，否则ERROR
    public final static String ACCEPT_SEAT = URL_HOST + "/action/acceptSeat.do";// 接受转让座位,参数PK_TRANSFERSEAT、seatId、personId、toPersonId，成功返回message值为OK，否则ERROR
    public final static String GET_SEATHISTORY = URL_HOST + "/query/getSeatHistory.do";// 查询历史座位使用记录
    public final static String GET_REQUEST_FRIENDS = URL_HOST + "/person/getRequestFriends.do";// 获取待确认的好友请求列表
    public final static String GET_OBJECT = URL_HOST + "/query/get.do";// 获取对象
    public final static String CANCLE_ATTINTION = URL_HOST + "/query/cancelCareRoom.do";// 取消关注教室
    public final static String IS_FRIEND_IN_SEAT = URL_HOST + "/person/isFriendInSeat.do";// 判断某个座位的用户是否好友
    public final static String HAS_FRIEND = URL_HOST + "/person/hasFriend.do";// 判断是否存在好友
    public final static String GET_TRANSFER_SEATS_OWN = URL_HOST + "/query/getMyTransferSeat.do";// 获取别人转让给我的座位信息列表
    //	public final static String GET_LIST = URL_HOST + "/query/getList.do";// 根据类名获取单表集合
    public final static String CANCLE_CARE = URL_HOST + "/person/cancleCare.do";// 取消关注
    public final static String IS_CARED = URL_HOST + "/person/isCared.do";// 判断是否已关注过当前用户
    public final static String NOTICE = URL_HOST + "/system/notice.do";// 预定通知,转让通知，参数frompersonId,toPersonId,type(1:预定通知,2：转让通知)
    public final static String CREATE_SEAT = URL_HOST + "/action/createSeat.do"; // 添加桌子，生成座位

    /**
     * 新生
     */
    public final static String NEW_URL="http://120.27.159.62/services";
    public final static String UPDATE=NEW_URL+"/student/new/querySysVer?";
    public final static String NEW_STUDENT=NEW_URL+"/student/new/queryByPhone?";

    /**
     * 学校图书
     */
    public final static String ADD_OLD_BOOK=USER_CENTER_HOST+"/Book/saveOldBookInfo.do?";//添加二手书
    public final static String ISBN_BOOK_DIEALS=USER_CENTER_MEET+"/Book/getBookInfoByIsbn.do";//根据isbn获取图书详情
    public final static String OLD_BOOK_LIST=USER_CENTER_MEET+"/Book/getOldBook.do";//获取二手图书列表
    public final static String SEARCH_OLDBOOK=USER_CENTER_HOST+"/Book/getOldBookByFuzzy.do";//搜索二手书
    public final static String UPDATE_OLDBOOK=USER_CENTER_HOST+"/Book/updateBookStatus.do";//修改图书状态
    public final static String HISTORY_OLDBOOK=USER_CENTER_HOST+"/Book/getBookLend.do";//二手书借阅历史
    public final static String SAVE_LENT_OLDBOOK=USER_CENTER_HOST+"/Book/saveBookLendReq.do";//保存借书申请记录。
    public final static String LENTLIST_OLDBOOK=USER_CENTER_HOST+"/Book/getBookLendReq.do";//申请列表
    public final static String NEW_BOOK_RECOMM=USER_CENTER_MEET+"/Book/queryOldbookOfLastUpload.do";//获取新书推荐单条
}
