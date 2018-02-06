package com.nzy.nim.activity.main;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.base.ImagePagerActivity;
import com.nzy.nim.adapter.PicsGridListAdapter;
import com.nzy.nim.adapter.ReplyBookCommentAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.BookReviewReply;
import com.nzy.nim.db.BookReviewReplyList;
import com.nzy.nim.db.ThemeNoteInfo;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.ClickableTextview;
import com.nzy.nim.view.NoScrollGridView;
import com.nzy.nim.view.QYUriMatcher;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CommentInfoActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {

    private ArrayList<String> picList;
    //    private DynamicList dynamicList;
    private UserInfo user;
    private ThemeNoteInfo.DataBean data;
    private Button next;
    private RoundImageView head;
    private TextView floor;
    private TextView name;
    private ClickableTextview content;
    private TextView time;
    private NoScrollGridView pics;
    private String comment_id;
    private TextView titleContent;
    private PullToRefreshListView ptrListView;
    private String TAG = CommentInfoActivity.class.getName();
    private ListView mListView;
    private boolean isOver;
    private ArrayList<BookReviewReplyList> reviewReplyLists = new ArrayList<BookReviewReplyList>();
    private ReplyBookCommentAdapter commentAdapter;
    private int page = 1;
    private TextView tvCommentCount;
    private TextView toRing;
    private LinearLayout layout_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_info);
        QYApplication.ACTIVITY_NAME="动态详情页";
        user = DBHelper.getInstance().getUserById(
                QYApplication.getPersonId());
        Intent intent = getIntent();
        comment_id = intent.getStringExtra("comment_id");
        initTopBar();
        intUI();
        getThemeNoteInfo();
        getReplyComment();
    }

    private void intUI() {
        initListView();
    }

    /**
     * 加载listView
     */
    private void initListView() {
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        layout_bottom.setVisibility(View.GONE);
        ptrListView = (PullToRefreshListView) findViewById(R.id.pull_list);
        ptrListView.setVisibility(View.GONE);
        ptrListView.setOnRefreshListener(this);
        QYApplication.initPullRefulsh(ptrListView, this, false, true,
                TAG);
        mListView = ptrListView.getRefreshableView();
        View headerView = initHeaderView();
        mListView.addHeaderView(headerView, null, false);//加载listView头布局
        commentAdapter = new ReplyBookCommentAdapter(CommentInfoActivity.this, reviewReplyLists);
        mListView.setAdapter(commentAdapter);
//        item点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = i - mListView.getHeaderViewsCount();
                showResumeDialog(index);
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isListViewReachTopEdge(mListView)) {
                    ptrListView.setPullRefreshEnabled(true);
                } else {
                    ptrListView.setPullRefreshEnabled(false);
                }
                if (isListViewReachBottomEdge(mListView)) {
                    ptrListView.setPullLoadEnabled(true);
                } else {
                    ptrListView.setPullLoadEnabled(false);
                }
                return false;
            }
        });
    }

    //    回复弹出框
    private void showResumeDialog(final int index) {
        final String[] items = {"回复"};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //跳转到输入界面，此处为书评回复列表间的回复
                        BookReviewReplyList replyList = reviewReplyLists.get(index);
                        String id = replyList.getId();
                        BookCommentInputActivity.actionIntent(CommentInfoActivity.this, comment_id, id, true, 0, true);
                        setResult(1);
                        break;
                }
            }
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                reviewReplyLists.clear();
                page = 1;
                commentAdapter.notifyDataSetInvalidated();//解决下拉刷新headerView越界问题
                getReplyComment();
                break;
        }
    }

    private View initHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.header_comment_info, null);
        head = (RoundImageView) view.findViewById(R.id.listview_comment_item_head);
        floor = (TextView) view.findViewById(R.id.listview_comment_item_floor);
        tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
        name = (TextView) view.findViewById(R.id.listview_comment_item_name);
        content = (ClickableTextview) view.findViewById(R.id.listview_comment_item_content);
        ImageView flag = (ImageView) view.findViewById(R.id.listview_comment_item_flag);
        time = (TextView) view.findViewById(R.id.listview_comment_item_time);
        toRing = (TextView) view.findViewById(R.id.toRing);
        pics = (NoScrollGridView) view.findViewById(R.id.listview_comment_item_pics);
//        if(flags==true){
//            flag.setImageResource(R.drawable.ic_master_flag);
//        }else{
//            flag.setImageResource(R.drawable.ic_member_flag);
//        }
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.getPersonId() == null) {
                    return;
                }
                FriendsInfoActivity.actionIntent(CommentInfoActivity.this, data.getPersonId());
            }
        });
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(content.getText());
                ToastUtil.showShort(CommentInfoActivity.this, "已复制");
                content.setEnabled(false);
                content.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        content.setEnabled(true);
                    }
                },1000);
                return true;
            }
        });
        toRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getRingThemeId()!=null) {
                    RingTeamInfoActivity.actionIntent(CommentInfoActivity.this,data.getRingThemeId());
                }

            }
        });
        return view;
    }

    public boolean isListViewReachTopEdge(final ListView listView) {
        boolean result = false;
        if (listView.getFirstVisiblePosition() == 0) {
            final View topChildView = listView.getChildAt(0);
            result = topChildView.getTop() == 0;
        }
        return result;
    }

    public boolean isListViewReachBottomEdge(final ListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        return result;
    }

    private void getThemeNoteInfo() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("noteId", comment_id);
        HTTPUtils.postToken(CommentInfoActivity.this, URLs.DYNAMIC_INFO, hashMap, new VolleyListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(CommentInfoActivity.this, R.string.server_is_busy);
            }

            @Override
            public void onResponse(String s) {
                if (s != null) {
                    layout_bottom.setVisibility(View.VISIBLE);
                    ptrListView.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    ThemeNoteInfo themeNoteInfo = gson.fromJson(s, ThemeNoteInfo.class);
                    data = themeNoteInfo.getData();
                    if (data.getPersonId().equals(user.getPersonId()) || data.getRingThemeInitatorId().equals(user.getPersonId())) {
                        next.setVisibility(View.VISIBLE);
                    } else {
                        next.setVisibility(View.GONE);
                    }
                    ImageUtil.displayHeadImg(data.getPersonPhoto(), head);
                    floor.setVisibility(View.VISIBLE);
//                        if(comment_pageNow!=null){
//                            floor.setText("第" + comment_pageNow + "页");
//                        }else{
//                            floor.setVisibility(View.GONE);
//                        }
                    if (data.getCreateTime() != null) {
                        Long aLong = Long.valueOf(data.getCreateTime());
                        final Date date = new Date(aLong);
                        time.setText(DateUtil.getStartDate(date, new Date()));
                    }
                    if (data.getContent() != null) {
                        content.setText(data.getContent());
                    }
                    if (data.getPersonName() != null) {
                        name.setText(data.getPersonName());
                    }
                    if (data.getRingThemeName() != null) {
                        titleContent.setText(data.getRingThemeName());
                        toRing.setText(data.getRingThemeName());
                    }
                    picList = (ArrayList<String>) data.getImageList();
                    if (picList != null) {
                        if(picList.size()==1){
                            pics.setNumColumns(1);
                        }else if(picList.size()==2||picList.size()==3||picList.size()==4){
                            pics.setNumColumns(2);
                        }else {
                            pics.setNumColumns(3);
                        }
                            pics.setVisibility(View.VISIBLE);
                        pics.setAdapter(new PicsGridListAdapter(CommentInfoActivity.this, picList));
                        pics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                ArrayList<String> list = new ArrayList<String>();
                                list.addAll(picList);
                                ImagePagerActivity.actionIntent(CommentInfoActivity.this, list);
                            }
                        });
                    } else {
                        pics.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * 获取回复列表
     */
    private void getReplyComment() {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("noteId", comment_id);
        hashMap.put("pageNow", String.valueOf(page));//可选，默认为第一页
        HTTPUtils.postWithToken(CommentInfoActivity.this, URLs.RINGTHEME_NOTE_REPLYLIST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                QYApplication.refulshComplete(ptrListView, TAG);
            }

            @Override
            public void onResponse(String s) {
                if (s != null && !"".equals(s)) {
                    try {
                        Gson gson = new Gson();
                        BookReviewReply bookReviewReply = gson.fromJson(s, BookReviewReply.class);
                        if (bookReviewReply.getErrcode() == 0) {
                            isOver = true;
                            ArrayList<BookReviewReplyList> list = bookReviewReply.getList();
//                            reviewReplyLists.clear();
                            reviewReplyLists.addAll(list);
                            if (bookReviewReply.getCount() != null) {
                                tvCommentCount.setText("评论 "+bookReviewReply.getCount());
                            }
                            commentAdapter.notifyDataSetChanged();
                            if (list.size() == 0) {
                                isOver = false;
                            }
                            QYApplication.refulshComplete(ptrListView, TAG);
                        } else {
                            isOver = false;
                            QYApplication.refulshComplete(ptrListView, TAG);
                            ToastUtil.showShort(CommentInfoActivity.this, bookReviewReply.getErrmsg());
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        });
    }

    private void initTopBar() {
        titleContent = (TextView) findViewById(R.id.top_bar_content);
        next = (Button) findViewById(R.id.top_bar_next);
        next.setVisibility(View.GONE);
        next.setText("删除");
        titleContent.setText("详情");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResumeDialog();
            }
        });

    }

    /**
     * 删除
     */
    private void showResumeDialog() {
        String[] items = {"删除"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentInfoActivity.this);
        builder.setTitle("确认删除吗？");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        getDel();
                        break;

                }
            }
        }).create().show();
    }

    private void getDel() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("noteId", comment_id);
//        hashMap.put("personId",user.getPersonId());
        HTTPUtils.postToken(CommentInfoActivity.this, URLs.ADD_DYNAMIC_DEL_LIST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(CommentInfoActivity.this, R.string.server_is_busy);
            }

            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jb = new JSONObject(s);
                    int errCode = jb.getInt("errCode");
                    String errMsg = jb.getString("errMsg");
                    if (errCode == 0) {
                        Intent intent = new Intent();
                        intent.setAction("addDynamic");
                        sendBroadcast(intent);
                        finish();
                        ToastUtil.showShort(CommentInfoActivity.this, "删除成功");
                    } else {
                        ToastUtil.showShort(CommentInfoActivity.this, errMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 添加书评回复
     *
     * @param v
     */
    public void addComment(View v) {
        if (comment_id == null) {
            return;
        }
        //跳转到输入界面，此处为回复该书评
        BookCommentInputActivity.actionIntent(this, comment_id, null, true, 0, true);
    }

    public void share(View view) {
        ShareMsgInfo shareMsgInfo = new ShareMsgInfo();
        shareMsgInfo.setTitle("来自组圈<" + data.getRingThemeName() + ">的评论分享");
        shareMsgInfo.setShareContent(data.getContent());
        shareMsgInfo.setActionUri(QYUriMatcher.getUriScheme("ringcomment/" + data.getNoteId()));
        shareMsgInfo.setShareType(MyConstants.RING_COMMENT_TYPE);
        shareMsgInfo.setStyle(MyConstants.SHARE_ONLY_TEXT);//无图
        ForwardMessageActivity.actionIntent(this, shareMsgInfo);
    }

    public static void actionIntent(Context context, String id) {
        Intent intent = new Intent();
        intent.putExtra("comment_id", id);
        intent.setClass(context, CommentInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        reviewReplyLists.clear();
        page = 1;
        commentAdapter.notifyDataSetInvalidated();//解决下拉刷新headerView越界问题
        getReplyComment();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (isOver) {
            page++;
            getReplyComment();
            isOver = false;
        } else {
            QYApplication.refulshComplete(ptrListView, TAG);
            Toast.makeText(CommentInfoActivity.this, "没有可以显示的数据", Toast.LENGTH_SHORT).show();
        }
    }
}
