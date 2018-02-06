package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.fragment.CommentFragment;
import com.nzy.nim.fragment.DynamicFragment;
import com.nzy.nim.vo.QYApplication;

import java.util.ArrayList;

public class DynamicCommentActivity extends BaseActivity implements View.OnClickListener {
    private View mLastSelectedBtn;
    private Button tab_dynamic;
    private Button tab_comment;
    private String groupId;
    private boolean isInclude;
    private Bundle bundle;
    private static final int DYNAMIC = 0;// 动态
    private static final int COMMENT = 1;// 留言
    private DynamicFragment dynamicfra;
    private CommentFragment commentfra;
    private PopupMenu popup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_comment);
        QYApplication.ACTIVITY_NAME="组圈动态页";
        groupId = getIntent().getStringExtra("groupId");
        isInclude = getIntent().getBooleanExtra("isInclude",false);
        intUI();
        setTabSelection(DYNAMIC);
    }

    private void intUI() {
        final ImageView nextIv = (ImageView) findViewById(R.id.top_bar_next_iv);
        nextIv.setVisibility(View.VISIBLE);
//        nextIv.setImageResource(R.mipmap.ic_publication_comments);
        tab_dynamic = (Button) findViewById(R.id.tab_dynamic);
        tab_dynamic.setOnClickListener(this);
        tab_comment = (Button) findViewById(R.id.tab_comment);
        tab_comment.setOnClickListener(this);
        mLastSelectedBtn= tab_dynamic;
        tab_dynamic.setBackgroundResource(R.drawable.tab_presse);
        tab_dynamic.setSelected(true);
        tab_comment.setSelected(false);
        bundle = new Bundle();
        bundle.putString("ringID", groupId);
        bundle.putBoolean("isInclude", isInclude);

        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String comment = "comment";
//                PublishedActivity.actionIntent(DynamicCommentActivity.this, groupId, comment);
                popup = new PopupMenu(DynamicCommentActivity.this, nextIv);
                popup.getMenuInflater().inflate(R.menu.pop_ring, popup.getMenu());
                onclick();
                popup.show();
            }
        });
    }
        private void onclick() {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                switch (arg0.getItemId()) {
                    case R.id.publish:
                String comment = "comment";
                PublishedActivity.actionIntent(DynamicCommentActivity.this, groupId, comment);
                        break;
                    case R.id.ring_back:
                        RingTeamInfoActivity.actionIntent(DynamicCommentActivity.this, groupId);
                        finish();
                        if(FriendsInfoActivity.friendInfoInstance!=null){
                            FriendsInfoActivity.friendInfoInstance.finish();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
    @Override
    public void onClick(View view) {
//        Fragment fragment=null;
        if (mLastSelectedBtn == view){
            return;
        }
        mLastSelectedBtn.setBackgroundResource(R.drawable.btn_selector);
        switch (view.getId()){
            case R.id.tab_dynamic:
                mLastSelectedBtn=tab_dynamic;
                tab_dynamic.setSelected(true);
                tab_comment.setSelected(false);
                setTabSelection(DYNAMIC);
                break;
            case R.id.tab_comment:
                mLastSelectedBtn=tab_comment;
                tab_comment.setSelected(true);
                tab_dynamic.setSelected(false);
                setTabSelection(COMMENT);
                break;
        }
    }
    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标。0表示书友圈，1表示社版，2表示我的好友。
     */
    private void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index){
            case DYNAMIC:
                if(dynamicfra==null){
                    dynamicfra=new DynamicFragment();
                    dynamicfra.setArguments(bundle);
                    transaction.add(R.id.dynamic_farme, dynamicfra);
                }else{
                    transaction.show(dynamicfra);
                }
                break;
            case COMMENT:
                if(commentfra==null){
                    commentfra=new CommentFragment();
                    commentfra.setArguments(bundle);
                    transaction.add(R.id.dynamic_farme, commentfra);
                }else{
                    transaction.show(commentfra);
                }
                break;
        }
        transaction.commit();
    }
    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (dynamicfra != null) {
            transaction.hide(dynamicfra);
        }
        if (commentfra != null) {
            transaction.hide(commentfra);
        }
    }
    public static void actionIntent(Context context,ArrayList<String> memberIds, String groupId,Boolean isInclude) {
        Intent intent = new Intent(context, DynamicCommentActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("isInclude", isInclude);
        intent.putStringArrayListExtra("memberIds", memberIds);
        context.startActivity(intent);
    }
}
