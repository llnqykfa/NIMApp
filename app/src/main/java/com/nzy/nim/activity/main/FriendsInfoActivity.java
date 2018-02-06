package com.nzy.nim.activity.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.base.DialogTools;
import com.nzy.nim.activity.base.ShowBigImageActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.Interests;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.FlowLayout;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.HobbyVO;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeVO;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * @author: 刘波
 * @date:2015年1月27日下午2:32:44
 * @todo:好友的个人信息界面
 */
public class FriendsInfoActivity extends BaseActivity implements OnClickListener, DialogTools.DialogOnClickListens {
    public static FriendsInfoActivity friendInfoInstance = null;
    final int MODIFY_MARK = 1;
    private RoundImageView contactHead;// 好友头像
    private TextView nameMark;// 好友备注
    //	private TextView nick;// 昵称
    private ImageView sex;// 好友性别
    private TextView sign;// 个性签名
    private Button sendBtn;// 给好友发消息
    private PopupMenu popup;

    private ImageView top_bar_right;
    private String contactId = "";// 联系人Id
    private TextView mTitleTv;// 标题
    private boolean isFriend = false;
    private RelativeLayout rl_remarks;
    private LinearLayout ll_tv_ringinfo;
    private TextView tv_ring_ignore;
    private FlowLayout tvHobby;
    private PersonVO personVO;
    private TextView tvNickName;
    private List<String> historyRecords;
    private TextView tv_my_oldbook;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsinfo_new);
        QYApplication.ACTIVITY_NAME="好友详情";
        Intent intent = getIntent();
        contactId = intent.getStringExtra("contactId");
        if (contactId == null) {
            return;
        }
        friendInfoInstance = this;


        initView();
        getUserInfo();
    }

    /**
     * @author LIUBO
     * @date 2015-4-5下午8:01:52
     * @TODO 获取用户信息
     */
    private void getUserInfo() {
        if (contactId == null) {
            return;
        }
        getPerson(contactId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MODIFY_MARK:
                if (data != null&& personVO!=null) {
                    modifyMark(contactId, data.getStringExtra("edit_mark"));
                    setName(data.getStringExtra("edit_mark"), personVO.getUserName());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param personId
     * @author LIUBO
     * @date 2015-4-5下午8:04:10
     * @TODO 获取用户信息
     */
    private void getPerson(String personId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("targetPersonId", personId);
        HTTPUtils.postWithToken(FriendsInfoActivity.this, URLs.GET_PERSON_INFO, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(FriendsInfoActivity.this, R.string.server_is_busy);
            }

            @Override
            public void onResponse(String s) {
                Log.e("UserInfo", s);
                try {
                    personVO = new Gson().fromJson(new JSONObject(s).getString("person"), PersonVO.class);
                    initContactsInfo(personVO);
                    getMyRings(personVO);
                    getAllInterests();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        friendInfoInstance = null;
    }

    /**
     * @作者：吴君
     * @时间：2015年1月27日
     * @作用：关联组件
     */
    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.top_bar_content);
        contactHead = (RoundImageView) this
                .findViewById(R.id.friendsinfo_iv_friendsimg);
        nameMark = (TextView) this.findViewById(R.id.friendinfo_tv_nameMark);
        tvNickName = (TextView) this.findViewById(R.id.nickName);
        sex = (ImageView) findViewById(R.id.friendinfo_iv_sex);
        sign = (TextView) this.findViewById(R.id.friendsinfo_tv_signature);
        rl_remarks = (RelativeLayout) findViewById(R.id.rl_remarks);
        rl_remarks.setOnClickListener(this);
        sendBtn = (Button) this.findViewById(R.id.friendsinfo_btn_sendMsg);
        top_bar_right = (ImageView) findViewById(R.id.top_bar_right);
        findViewById(R.id.top_back_bg).setOnClickListener(this);
        top_bar_right.setOnClickListener(this);
        //如果联系人为自己，则隐藏发送按钮
        if (contactId.equals(QYApplication.getPersonId())) {
            sendBtn.setVisibility(View.GONE);
        }
        //得到控件
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        tv_ring_ignore = (TextView) findViewById(R.id.tv_ring_ignore);
        ll_tv_ringinfo = (LinearLayout) findViewById(R.id.ll_tv_ringinfo);
        tvHobby = (FlowLayout) findViewById(R.id.friendsinfo_tv_hobby);
    }


    /**
     * 获取好友相关组圈
     * @param personVO
     */
    private void getMyRings(PersonVO personVO) {
        RequestParams param = new RequestParams();
        param.add("personId", personVO.getPersonId());
        HttpUtil.post(URLs.LIST_MY_RING, param, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (arg2 != null) {
                    try {
                        final List<RingThemeVO> list = JSON.parseArray(arg2,
                                RingThemeVO.class);
                        ll_tv_ringinfo.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        if (list.size() == 0) {
                            TextView text = new TextView(FriendsInfoActivity.this);
                            text.setTextSize(15);
                            text.setPadding(0, 0, 6, 0);
                            text.setSingleLine();
                            text.setEllipsize(TextUtils.TruncateAt.END);
                            text.setText("没有加入组圈！");
                            ll_tv_ringinfo.addView(text);
                        }
                        GalleryAdapter galleryAdapter = new GalleryAdapter(list);
                        mRecyclerView.setAdapter(galleryAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
            }
        });
    }

    /**
     * @author LIUBO
     * @date 2015-4-15下午12:32:41
     * @TODO 获取所有兴趣
     */
    private void getAllInterests() {
        RequestParams params = new RequestParams();
        params.add("personId", contactId);
        HttpUtil.post(URLs.LIST_OWNER_HOBBY, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2 != null) {
                            List<HobbyVO> hobbys = JSON.parseArray(arg2,
                                    HobbyVO.class);
                            List<Interests> list = DBConversion.getInstance()
                                    .getAllInterests(hobbys);
                            for (int i = 0; i < list.size(); i++) {
                                TextView text = new TextView(FriendsInfoActivity.this);
                                text.setTextSize(18);
                                text.setPadding(0, 0, 24, 6);
                                text.setSingleLine();
                                text.setText(list.get(i).getName());
                                text.setEllipsize(TextUtils.TruncateAt.END);
                                tvHobby.addView(text);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                    }
                });
    }

    /**
     * 初始化用户信息
     */
    private void initContactsInfo(final PersonVO personVO) {
        isFriend = personVO.getFriend();
        mTitleTv.setText("详细资料");
        // 加载头像
        ImageUtil.displayHeadImg(personVO.getPhotoPath(), contactHead);
        contactHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBigImageActivity.actionIntent(FriendsInfoActivity.this,
                        personVO.getPhotoPath());
            }
        });
        setName(personVO.getRemark(), personVO.getUserName());
        if (!DataUtil.isEmpty(personVO.getDesignInfo())) {
            sign.setText(personVO.getDesignInfo());
        } else {
            sign.setText("无最新签名");
        }
        if (personVO.getSex()) {
            sex.setImageResource(R.mipmap.ic_male);
        } else {
            sex.setImageResource(R.mipmap.ic_female);
        }

        if (isFriend) {
            sendBtn.setText("发起对话");
            top_bar_right.setVisibility(View.VISIBLE);
//            rl_remarks.setVisibility(View.VISIBLE);
        } else {
            sendBtn.setText("加为好友");
            top_bar_right.setVisibility(View.GONE);
//            rl_remarks.setVisibility(View.INVISIBLE);
        }
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFriend) {
                    String title;
                    if (personVO.getRemark() != null && !"".equals(personVO.getRemark())) {
                        title = personVO.getRemark();
                    } else {
                        title = personVO.getUserName();
                    }
                    ChatActivity.actionIntent(friendInfoInstance,
                            personVO.getPersonId(), title, false,
                            null);
                    FriendsInfoActivity.this.finish();
                } else {
                    PersonVerificationInfo.actionIntent(friendInfoInstance,
                            personVO.getPersonId());
                }

            }
        });
    }

    private void setName(String mark, String nickName) {
        if (!DataUtil.isEmpty(mark)) {
            nameMark.setText(mark);
            rl_remarks.setVisibility(View.VISIBLE);
            tvNickName.setText(nickName);
        } else if (!DataUtil.isEmpty(nickName)) {
            nameMark.setText(nickName);
        }
    }


    /**
     * 显示删除好友的对话框
     */
    private void showIsDeleteDialog() {
        DialogTools.refuseShow(this).show();
        DialogTools.setDialogOnClick(this);

    }

    /**
     * @param contactId
     * @param mark
     * @author LIUBO
     * @date 2015-4-8下午7:20:13
     * @TODO 修改备注
     */
    private void modifyMark(final String contactId, final String mark) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("remark", mark);
        map.put("friendId", contactId);
        HTTPUtils.postWithToken(FriendsInfoActivity.this, URLs.UPDATE_FRIEND_REMARK, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(FriendsInfoActivity.this, "修改失败");
            }

            @Override
            public void onResponse(String s) {
                if (!DataUtil.isEmpty(s)) {
                    // 修改成功后更新本地
                    ContentValues values = new ContentValues();
                    if (DataUtil.isEmpty(mark)) {//备注为空，即删除备注，以用户名作为好友列表首字母
                        Contacts contacts = DBHelper.getInstance().find(Contacts.class, "userid=? and contactid=?",
                                QYApplication.getPersonId(), contactId);
                        values.put("firstLetter", StringUtil
                                .converterToFirstSpell(contacts.getUserName()).toLowerCase());
                    } else {//修改备注 以备注作为好友列表首字母
                        values.put("firstLetter", StringUtil
                                .converterToFirstSpell(mark).toLowerCase());
                    }
                    values.put("remark", mark);
                    DBHelper.getInstance().update(Contacts.class,
                            values, "userid=? and contactid=?",
                            QYApplication.getPersonId(), contactId);
                    //发送广播更新好友列表
                    Intent intent = new Intent();
                    intent.setAction("update_contacts_List");
                    sendBroadcast(intent);
//                    ToastUtil.showShort(FriendsInfoActivity.this, "修改成功");
                }
            }

        });
    }

    /**
     * @param userId
     * @param friendId
     * @author 刘波
     * @date 2015-3-5下午3:46:01
     * @todo 执行删除好友操作
     */

    private void deleteFriend(String userId, String friendId) {
        RequestParams params = new RequestParams();
        params.add("personId", userId);
        params.add("friendId", friendId);
        HttpUtil.post(URLs.DELETE_FRIEND, params,
                new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (DataUtil.isOk(arg2)) {// 成功删除好友
                            clearFriendData(personVO.getPersonId(),
                                    personVO.getUserName());
                            //发送广播更新好友列表
                            Intent intent = new Intent();
                            intent.setAction("update_contacts_List");
                            sendBroadcast(intent);
                        } else {
                            ToastUtil.show(FriendsInfoActivity.this,
                                    "删除好友失败！！！", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        DialogHelper.showMsgDialog(FriendsInfoActivity.this,
                                "删除失败！！！");
                    }
                });
    }

    /**
     * 清空好友的相关数据
     *
     * @param contactId
     * @param contactName
     */
    public void clearFriendData(String contactId, String contactName) {
        DBHelper.getInstance().clearFriendData(contactId, contactName);
        FriendsInfoActivity.this.finish();
        if(ChatActivity.instance!=null){
            ChatActivity.instance.finish();
        }
    }

    /**
     * @param context
     * @作者：吴君
     * @时间：2015年1月27日
     * @作用：点击跳转到该页面
     */
    public static void actionIntent(Context context, String contactId) {
        if (DataUtil.isEmpty(contactId)) {
            return;
        }
        Intent intent = new Intent(context, FriendsInfoActivity.class);
        intent.putExtra("contactId", contactId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_bg:
                finish();
                break;
            case R.id.top_bar_right:
                popup = new PopupMenu(FriendsInfoActivity.this, top_bar_right);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                onclick();
                popup.show();
                break;
        }
    }

    private void onclick() {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                switch (arg0.getItemId()) {
                    case R.id.special_topic:
                        showIsDeleteDialog();
                        break;
                    case R.id.remark:
                        EditUserInfoActivity.actionIntent(FriendsInfoActivity.this,
                                nameMark.getText().toString(), MODIFY_MARK,
                                EditUserInfoActivity.MODIFY_MARK_FLAG);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDialogClick() {
        deleteFriend(QYApplication.getPersonId(),
                personVO.getPersonId());
    }
    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
        private List<RingThemeVO> list;
        public GalleryAdapter(List<RingThemeVO> list){
            this.list=list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recyclerview_ring_item,parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.rel_friend_ring= (RelativeLayout) view.findViewById(R.id.rel_friend_ring);
            viewHolder.mTxt  = (TextView) view.findViewById(R.id.tv_ring_name);
            viewHolder.mImg = (ImageView) view.findViewById(R.id.img_ring);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final RingThemeVO ringThemeVO = list.get(position);
            ImageUtil.displayNetImg(ringThemeVO.getFirstRingPath(), holder.mImg);
            holder.mTxt.setText(ringThemeVO.getTheme());
            holder.rel_friend_ring.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isIncludu=false;
                    String initatorId = ringThemeVO.getInitatorId();//组圈发起者id
                    String pk_ringtheme = ringThemeVO.getPk_ringtheme();
                    if(initatorId.equals(QYApplication.getPersonId())){
                            isIncludu=true;
                     }
                     if (pk_ringtheme != null) {
                            DynamicCommentActivity.actionIntent(FriendsInfoActivity.this, null, pk_ringtheme, isIncludu);
                     }

                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(View arg0){
                super(arg0);
            }
            ImageView mImg;
            TextView mTxt;
            RelativeLayout rel_friend_ring;
        }

    }
}
