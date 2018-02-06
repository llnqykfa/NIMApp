package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.base.ImagePagerActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.DynamicList;
import com.nzy.nim.db.bean.GroupMembers;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.CommonUtil;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.FlowLayout;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeDetailVO;
import com.nzy.nim.vo.RingThemeDynamicList;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @TODO 圈信息的详细展示
 */
public class RingTeamInfoActivity extends BaseActivity implements
        OnClickListener {
    public static final int RESULT_EXIT_RING = 1;// 退出圈
    public static final int RESULT_DISBAND_RING = 2;// 解散圈
    public static final int RESULT_DELETE_MEMBER = 3;// 踢出圈成员
    public static final int RESULT_EDIT_RING = 4;// 编辑圈资料
    public static final int RESULT_EDIT_DELETE_FLAG = 5;// 编辑圈资料并删除圈成员
    public static final int RESULT_NEW_COMMENT_FLAG = 6;// 新的留言评论

    private static final int VISITOR_FLAG = 0;    //游客
    private static final int MASTER_FLAG = 1;//群主
    private static final int GROUP_MEMBER_FLAG = 2;//成员
    private String groupId = "";
    private GridView photoWall;// 照片墙
    private TextView creatTime;// 发起时间
    private TextView ringContent;// 圈内容
    private TextView memberCount;// 成员数
    private GridView members;// 成员
    private RoundImageView authorHead;// 发起者头像
    private Button btnFunction1, btnFunction2;
    //    private RingThemeInfo ringThemeInfo = null;
    private TextView title;
    private TextView commentNum;// 评论数
    private int currentFlag = -1;// 访问该页面的用户
    //    private List<GroupMembers> memberData = new ArrayList<GroupMembers>();// 当前页面展示的圈成员
    private ArrayList<String> memberIds = new ArrayList<String>();// 所有圈成员的id集合
    private ArrayList<String> photos = new ArrayList<String>();// 照片墙的图片集合
    private ArrayList<RingThemeDetailVO.RingThemeEntity.MenbersEntity> memberInfos = new ArrayList<RingThemeDetailVO.RingThemeEntity.MenbersEntity>();// 当前页面展示的圈成员
    private ImageView nextIv;
    private CommonAdapter<GroupMembers> memberAdapter;
    private CommonAdapter<RingThemeDetailVO.RingThemeEntity.MenbersEntity> ringMemberAdapter;
    private CommonAdapter<String> photoAdapter;// 照片墙适配器
    private boolean isTextChanged;//文字是否同步
    private RingThemeDetailVO.RingThemeEntity ringTheme;
    private TextView dynamicNum;
    private FlowLayout themeTag;
    private TextView tvPraise;
    private ImageView imgPraise;
    private boolean isPraised;
    private final int DOWNLOAD_SUCCESS = 0;
    private final int DOWNLOAD_FAILED = 1;
    private boolean isLoadding = true;
    private int number;
    private Integer diggNumber;
    private TextView tv_ringinfo_moren;
    private boolean isclick = false;
    private LinearLayout linear_content;
    private LinearLayout ring_theme_info_members_container;
    private LinearLayout ring_re_img_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_theme_info);
        QYApplication.ACTIVITY_NAME="组圈详情页";
        initTopBar();
        initView();
        // 获取从其他页面传递过来的数据
        groupId = getIntent().getStringExtra("groupId");
        getRingThemeInfoNew();
    }


    /**
     * @TODO 初始化标题栏
     */
    private void initTopBar() {
        title = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        nextIv = (ImageView) findViewById(R.id.top_bar_next_iv);
        nextIv.setImageResource(R.mipmap.circle_setting);
        next.setVisibility(View.GONE);
        nextIv.setVisibility(View.INVISIBLE);
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFlag == -1)
                    return;
                if (currentFlag == VISITOR_FLAG) {
                    RingSettingActivity.actionIntent(RingTeamInfoActivity.this,
                            groupId, title.getText().toString(), RingSettingActivity.VISITOR_FLAG);
                } else if (currentFlag == MASTER_FLAG) {
                    RingSettingActivity.actionIntent(RingTeamInfoActivity.this,
                            groupId, title.getText().toString(), RingSettingActivity.MASTER_FLAG);
                } else {
                    RingSettingActivity.actionIntent(RingTeamInfoActivity.this,
                            groupId, title.getText().toString(), RingSettingActivity.MEMBER_FLAG);
                }
            }
        });
    }


    /**
     * @TODO 关联布局
     */
    private void initView() {
        photoWall = (GridView) findViewById(R.id.ring_theme_info_photo_wall);
        creatTime = (TextView) findViewById(R.id.ring_theme_info_create_time);
        ringContent = (TextView) findViewById(R.id.ring_theme_info_content);
        tv_ringinfo_moren = (TextView) findViewById(R.id.tv_ringinf_moren);
        memberCount = (TextView) findViewById(R.id.ring_theme_info_member_num);
        members = (GridView) findViewById(R.id.ring_theme_info_members);
        authorHead = (RoundImageView) findViewById(R.id.ring_theme_info_author_head);
        btnFunction1 = (Button) findViewById(R.id.ring_theme_info_btn_function_1);
        btnFunction2 = (Button) findViewById(R.id.ring_theme_info_btn_function_2);
        commentNum = (TextView) findViewById(R.id.ring_theme_info_comment_num);
        ring_theme_info_members_container = (LinearLayout) findViewById(R.id.ring_theme_info_members_container);
        dynamicNum = (TextView) findViewById(R.id.ring_theme_info_dynamic_num);
        linear_content = (LinearLayout) findViewById(R.id.linear_content);
        themeTag = (FlowLayout) findViewById(R.id.ring_theme_tag);
        ring_re_img_pic = (LinearLayout) findViewById(R.id.ring_re_img_pic);
        findViewById(R.id.ring_theme_info_dynamic_container).setOnClickListener(this);

        findViewById(R.id.layout_praise).setOnClickListener(this);
        tvPraise = (TextView) findViewById(R.id.tv_praiseCount);
        imgPraise = (ImageView) findViewById(R.id.img_praise);

        ring_theme_info_members_container.setOnClickListener(this);
        btnFunction1.setOnClickListener(this);
        btnFunction2.setOnClickListener(this);
        tv_ringinfo_moren.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isclick = !isclick;
                if (isclick) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1.0f;
                    linear_content.setLayoutParams(params);
                    ringContent.setMaxLines(Integer.MAX_VALUE);
                    tv_ringinfo_moren.setText("收起");
                    tv_ringinfo_moren.setTextColor(Color.rgb(250, 166, 96));
                } else {
                    LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) linear_content.getLayoutParams(); //取控件当前的布局参数
                    linearParams.height = 75;// 控件的高强制设成75
                    linear_content.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

                    ringContent.setMaxLines(2);
                    ringContent.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    tv_ringinfo_moren.setText("更多");
                    tv_ringinfo_moren.setTextColor(Color.rgb(250, 166, 96));
                }
            }
        });
    }

    private void getRingThemeInfoNew() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ringId", groupId);
        HTTPUtils.postWithToken(RingTeamInfoActivity.this, URLs.GET_RING_THEME_DETAIL, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(DOWNLOAD_FAILED);
            }

            @Override
            public void onResponse(String s) {
                if (s != null) {
                    try {
                        Gson gson = new Gson();
                        RingThemeDetailVO themeDetailVO = gson.fromJson(s, RingThemeDetailVO.class);
                        int errcode = themeDetailVO.getErrcode();
                        String errmsg = themeDetailVO.getErrmsg();
                        if (errcode == 0) {
                            ringTheme = themeDetailVO.getRingTheme();
                            updateViews(ringTheme);
                            handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                        } else {
                            ToastUtil.showShort(RingTeamInfoActivity.this, "该组圈不存在，或已被删除！");
                            title.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RingTeamInfoActivity.this.finish();
                                }
                            }, 500);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        });
    }

    /**
     * 更新控件显示
     *
     * @param themeInfo
     */
    private void updateViews(RingThemeDetailVO.RingThemeEntity themeInfo) {
        setThemebaseInfo(themeInfo);//设置组圈基本显示内容
        getNewComment(themeInfo);//显示最新留言
        setBtnText(themeInfo);//根据不同身份，设置按键显示内容
        setPhotoWall(themeInfo);//显示组圈照片
        showMembersHead(themeInfo);//显示成员头像
    }

    /**
     * 设置组圈基本显示内容
     *
     * @param themeInfo
     */
    private void setThemebaseInfo(RingThemeDetailVO.RingThemeEntity themeInfo) {
        if (themeInfo.getTheme() != null) {
            title.setText(themeInfo.getTheme());
        }
        if (themeInfo.getCreateTime() != null) {
            String date = DateUtil.formatDate(new Date(themeInfo.getCreateTime()), "yyyy-MM-dd HH:mm:ss");
            creatTime.setText(date);
        }
        if (themeInfo.getIntroduce() != null) {
            ringContent.setText(themeInfo.getIntroduce());
            getTvLines();
        }
        if (themeInfo.getMenberNumber() != null) {
            memberCount.setText(themeInfo.getMenberNumber().toString());
        }
        if (themeInfo.isIsDigg()) {
            isPraised = true;
//            imgPraise.setImageResource(R.drawable.attention);
//        } else {
//            imgPraise.setImageResource(R.drawable.no_attention);
        }
        if (themeInfo.getDynamicNumber() != null) {
            dynamicNum.setText(themeInfo.getDynamicNumber().toString());
        }
        if (themeInfo.getDiggNumber() != null) {
            diggNumber = themeInfo.getDiggNumber();
            tvPraise.setText(themeInfo.getDiggNumber().toString());
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    isLoadding = false;
                    break;
                case DOWNLOAD_FAILED:
                    isLoadding = false;
                    break;
            }
        }
    };

    private void getTvLines() {
        ringContent.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = ringContent.getLineCount();
                if (lineCount <= 2) {
                    tv_ringinfo_moren.setVisibility(View.GONE);
                } else {
                    ringContent.setMaxLines(2);
                    ringContent.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    tv_ringinfo_moren.setVisibility(View.VISIBLE);
                    tv_ringinfo_moren.setTextColor(Color.rgb(250, 166, 96));
                }
            }
        });
    }

    /**
     * 显示成员头像，最多显示四个
     *
     * @param themeInfo
     */
    private void showMembersHead(RingThemeDetailVO.RingThemeEntity themeInfo) {
        ImageUtil.displayHeadImg(themeInfo.getInitatorPhoto(), authorHead);
        List<RingThemeDetailVO.RingThemeEntity.MenbersEntity> menbers = themeInfo.getMenbers();
        if (menbers != null && menbers.size() > 0) {
            memberInfos.clear();
            if (menbers.size() > 4) {
                for (int i = 0; i < 4; i++) {
                    memberInfos.add(menbers.get(i));
                }
            } else {
                memberInfos.addAll(menbers);
            }
            initMemberAdapter();//成员头像适配器
            members.setAdapter(ringMemberAdapter);
            ringMemberAdapter.notifyDataSetChanged();
            members.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    MembersListActivity.actionIntent(RingTeamInfoActivity.this,
                            groupId, false);
                }
            });
        }
    }
    /**
     * 设置按键显示内容
     *
     * @param themeInfo
     */
    private void setBtnText(RingThemeDetailVO.RingThemeEntity themeInfo) {
        if (themeInfo.isIsManager()) {//圈主
            currentFlag = MASTER_FLAG;
            btnFunction1.setText(R.string.into_group_chat);
            btnFunction2.setText(R.string.share_friends);
//            btnFunction2.setText(R.string.invite_friends);
            nextIv.setVisibility(View.VISIBLE);
        } else if (themeInfo.isIsInclude()) {//群成员
            currentFlag = GROUP_MEMBER_FLAG;
            btnFunction1.setText(R.string.into_group_chat);
            btnFunction2.setText(R.string.share_friends);
            nextIv.setVisibility(View.VISIBLE);
        } else {//游客
            currentFlag = VISITOR_FLAG;
            btnFunction1.setText(R.string.apply_join);
            btnFunction2.setText(R.string.share_friends);
        }
        isTextChanged = true;
    }

    /**
     * 设置背景墙
     *
     * @param themeInfo
     */
    private void setPhotoWall(RingThemeDetailVO.RingThemeEntity themeInfo) {
        photos.clear();
        if (themeInfo.getImage() != null) {
            this.photos.add(themeInfo.getImage());
            if (photos != null && photos.size() > 0) {
                initPhotoAdapter(this.photos);
                photoWall.setAdapter(photoAdapter);
                photoAdapter.notifyDataSetChanged();
            }
            photoWall.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    ImagePagerActivity.actionIntent(RingTeamInfoActivity.this,
                            RingTeamInfoActivity.this.photos);
                }
            });
        }
    }

    /**
     * 显示最新留言
     *
     * @param themeInfo
     */
    private void getNewComment(final RingThemeDetailVO.RingThemeEntity themeInfo) {
        //组圈动态
        final RingThemeDetailVO.RingThemeEntity.LatestDynamicEntity latestDynamic = themeInfo.getLatestDynamic();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ringId", groupId);
        hashMap.put("pageNow", "" + 1);
        hashMap.put("pageSize", "" + 99);
        HTTPUtils.postWithToken(RingTeamInfoActivity.this, URLs.DYNAMIC_NEW_LIST, hashMap, new VolleyListener() {
            @Override
            public void onResponse(String s) {
                if (s != null) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(s);
                        String errcode = json.getString("errcode");
                        if ("0".equals(errcode)) {
                            Gson gson = new Gson();
                            RingThemeDynamicList ringListVo = gson.fromJson(s, RingThemeDynamicList.class);
                            List<DynamicList> lists = ringListVo.getList();
                            if(lists.size()!=0){
                                List<String> imgList=new ArrayList<String>();
                                for (int i = 0; i <lists.size() ; i++) {
                                    List<String> imageList = lists.get(i).getImageList();
                                    for (int j = 0; j < imageList.size(); j++) {
                                        String str_photo = imageList.get(j);
                                        if (imgList.contains(str_photo)) {
                                            continue;
                                        }
                                        imgList.add(str_photo);
                                    }
                                }
                                ring_re_img_pic.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                if(imgList.size()==0){
                                    TextView text = new TextView(RingTeamInfoActivity.this);
                                    text.setTextSize(15);
                                    text.setPadding(0, 0, 6, 0);
//                                    text.setSingleLine();
                                    text.setEllipsize(TextUtils.TruncateAt.END);
                                    if(latestDynamic!=null){
                                        if(latestDynamic.getContent() != null&&latestDynamic.getPersonName()!= null){
                                            text.setText(latestDynamic.getPersonName()+"\n"+"\n"+latestDynamic.getContent());
                                        }
                                    }else{
                                        text.setText("该组圈活跃度低！");
                                    }
                                    ring_re_img_pic.addView(text);
                                }
                                for (int img_i2 = 0; img_i2 < imgList.size(); img_i2++) {
                                    if (img_i2 > 2) {
                                        break;
                                    }
                                    View view = getLayoutInflater().inflate(R.layout.ring_photo_img_item, null);
                                    ImageView imgRing = (ImageView) view.findViewById(R.id.item_grida_image);
                                    imgRing.setPadding(0,5,10,5);
                                    String s1 = imgList.get(img_i2);
                                    ImageUtil.displayNetImg(s1, imgRing);
                                    ring_re_img_pic.addView(view);
                                }
                            }else{
                                TextView text = new TextView(RingTeamInfoActivity.this);
                                text.setTextSize(15);
                                text.setPadding(0, 0, 6, 0);
                                text.setSingleLine();
                                text.setEllipsize(TextUtils.TruncateAt.END);
                                text.setText("该组圈活跃度低！");
                                ring_re_img_pic.addView(text);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        //组圈标签
        List<String> tags = themeInfo.getTags();
        for (int i = 0; i < tags.size(); i++) {
            TextView text = new TextView(this);
            text.setTextSize(14);
            text.setPadding(0, 0, 24, 6);
            text.setSingleLine();
            text.setText(tags.get(i));
            text.setTextColor(Color.rgb(32, 32, 32));
            text.setEllipsize(TextUtils.TruncateAt.END);
            themeTag.addView(text);
        }
    }

    /**
     * 成员头像适配器
     */
    private void initMemberAdapter() {
        ringMemberAdapter = new CommonAdapter<RingThemeDetailVO.RingThemeEntity.MenbersEntity>(this, memberInfos,
                R.layout.list_some_group_member_item) {
            @Override
            public void convert(ViewHolder helper, RingThemeDetailVO.RingThemeEntity.MenbersEntity item) {
                RoundImageView iv = helper
                        .getView(R.id.list_some_group_member_item_img);
                ImageUtil.displayHeadImg(item.getPersonPhoto(), iv);
            }
        };
    }

    /**
     * @TODO 申请加入组圈
     */

    private void applyJoin(String msgContent) {
        RequestParams params = new RequestParams();
        params.add("themeId", groupId);
        params.add("personId", QYApplication.getPersonId());
        params.add("msg", msgContent);
        UserInfo user = DBHelper.getInstance().getUserById(
                QYApplication.getPersonId());
        params.add("username", user.getUserName());
        HttpUtil.post(URLs.REQUEST_ADD_RING, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        ToastUtil.showShort(RingTeamInfoActivity.this,
                                "请求发送成功!");
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        ToastUtil.showShort(RingTeamInfoActivity.this,
                                R.string.server_is_busy);
                    }
                });
    }


    /**
     * 初始化照片墙
     */
    private void initPhotoAdapter(List<String> photos) {
        photoAdapter = new CommonAdapter<String>(this, photos,
                R.layout.listview_ring_theme_photo_wall_item) {
            @Override
            public void convert(ViewHolder helper, String item) {
                ImageView iv = helper.getView(R.id.ring_theme_photo_wall_img);
                ImageUtil.displayImg(item, iv);
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * @param context 上下文环境
     * @TODO TODO 从其他页面跳转到当前页面
     * @Return void
     */
    public static void actionIntent(Context context, String groupId) {
        Intent intent = new Intent(context, RingTeamInfoActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    public static void actionIntent(Activity activity, String groupId, int requestCode) {
        Intent intent = new Intent(activity, RingTeamInfoActivity.class);
        intent.putExtra("groupId", groupId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {
        if (ringTheme == null) {
            return;
        }
        if(ringTheme.isIsInclude()==null){
            return;
        }
        Boolean isInclude = ringTheme.isIsInclude();
        if (isInclude == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.ring_theme_info_master_container:// 群主
                if (ringTheme != null && ringTheme.getInitatorId() != null) {
                    FriendsInfoActivity.actionIntent(this, ringTheme.getInitatorId());
                }
                break;
            case R.id.ring_theme_info_leave_comments_container:// 动态
                if(memberIds!=null&&groupId!=null){
                    DynamicCommentActivity.actionIntent(this, memberIds, groupId, isInclude);
                }
                break;
            case R.id.ring_theme_info_members_container:// 群成员
                if (!groupId.equals("")&&groupId!=null) {
                    MembersListActivity.actionIntent(this, groupId, false);
                }
                break;
            case R.id.ring_theme_info_btn_function_1://进入群里或者申请加入
                handleBtnFunction1Click();
                break;
            case R.id.ring_theme_info_btn_function_2:// 邀请好友
                if (isTextChanged) {//判断是否已经加载完毕
                    ShareMsgInfo shareMsgInfo = new ShareMsgInfo();
                    shareMsgInfo.setTitle(ringTheme.getTheme());
                    shareMsgInfo.setShareContent(ringTheme.getIntroduce());
                    shareMsgInfo.setImgPath(ringTheme.getImage());
                    shareMsgInfo.setActionUri(QYUriMatcher.getUriScheme("ringtheme/" + ringTheme.getRingId()));
                    shareMsgInfo.setShareType(MyConstants.RING_SHARE_TYPE);
                    shareMsgInfo.setStyle(MyConstants.SHARE_WITH_PICTURE);//有图
                    ForwardMessageActivity.actionIntent(RingTeamInfoActivity.this, shareMsgInfo);
                } else {
                    DialogHelper.showMsgDialog(this, "当前组圈尚未加载完毕，请返回重试");
                }
                break;
            case R.id.layout_praise:
                isPraised = !isPraised;
                if (isPraised) {
                    diggNumber += 1;
                    tvPraise.setText((diggNumber) + "");
                    ToastUtil.showShort(RingTeamInfoActivity.this, "赞");
                } else {
                    diggNumber -= 1;
                    tvPraise.setText((diggNumber) + "");
                    ToastUtil.showShort(RingTeamInfoActivity.this, "取消赞");
                }
                number++;
                break;
            case R.id.ring_theme_info_dynamic_container:
                DynamicCommentActivity.actionIntent(this, memberIds, groupId, isInclude);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //点赞有变化
        if (number % 2 != 0) {
            praise();
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void praise() {
        if (groupId == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.add("ringId", groupId);
        params.add("personId", QYApplication.getPersonId());
        HttpUtil.post(URLs.ADD_RING_PRAISE, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {

                        // 如果返回的数据不为空
                        if (!DataUtil.isEmpty(arg2)) {
                            Intent intent = new Intent("mygroups");
                            intent.putExtra("isPraised", isPraised);
                            intent.putExtra("praiseCount", diggNumber + "");
                            sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {

                    }
                });
    }

    private void handleBtnFunction1Click() {
        if (isTextChanged) {  //判断是否加载完毕
            if (currentFlag == VISITOR_FLAG) {
                showApplyJoinDialog();
            } else {
                ChatActivity.actionIntent(this, groupId, ringTheme.getTheme(),
                        true, null);
            }
        } else {
            DialogHelper.showMsgDialog(this, "当前组圈尚未加载完毕，请返回重试");
            refreshTheme();
        }
    }

    @SuppressLint("InflateParams")
    private void showApplyJoinDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("留言");
        View view = getLayoutInflater()
                .inflate(R.layout.dialog_view_edit, null);
        final EditText edit = (EditText) view
                .findViewById(R.id.dialog_view_edit_et);
        dialog.setView(view);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommonUtil.hideKeyboard(RingTeamInfoActivity.this, edit);
            }
        }).setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(edit.getText()))
                    applyJoin(edit.getText().toString());
                else
                    applyJoin("");
                CommonUtil.hideKeyboard(RingTeamInfoActivity.this, edit);
            }
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_DELETE_MEMBER) {// 删除圈成员
            refreshTheme();
        } else if (resultCode == RESULT_EXIT_RING) {// 退出圈
            finish();
        } else if (resultCode == RESULT_DISBAND_RING) {// 解散圈
            RingListActivity.isRefresh = true;
            finish();
        } else if (resultCode == RESULT_EDIT_RING) {// 编辑圈
            refreshTheme();
        } else if (resultCode == RESULT_EDIT_DELETE_FLAG) {
            refreshTheme();
        } else if (resultCode == RESULT_NEW_COMMENT_FLAG) {
            refreshTheme();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshTheme() {
        getRingThemeInfoNew();
    }



}