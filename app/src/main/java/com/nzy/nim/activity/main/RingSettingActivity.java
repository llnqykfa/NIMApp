package com.nzy.nim.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingList;
import com.nzy.nim.vo.RingThemesTmp;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;

public class RingSettingActivity extends BaseActivity implements
        OnClickListener {
    public static int MASTER_FLAG = 0;// 圈主标志
    public static int MEMBER_FLAG = 1;// 圈成员标志
    public static int VISITOR_FLAG = 2;// 游客标志

    public static final int RESULT_EDIT_RING = 1;// 编辑圈资料
    public static final int RESULT_DELETE_MEMBER = 2;// 踢出圈成员
    private int identityFlag = -1;
    private String groupId;
    private RelativeLayout managerMembers;// 管理圈成员
    private Button exitOrDisband;// 退出或解散按钮
    boolean notifyFlag = true;
    boolean receFlag = true;
    private boolean isDeleteMember = false;// 是否踢出了成员
    private boolean isEditRing = false;// 是否编辑了圈资料
    private String titles;
    private ProgressDialog pgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_setting);
        identityFlag = getIntent().getIntExtra("identity_flag", -1);
        groupId = getIntent().getStringExtra("groupId");
        titles = getIntent().getStringExtra("titles");
        pgDialog = DialogHelper.getSDialog(RingSettingActivity.this, "加载中···", false);
        initTopBar();
        initView();
        setUpView();
    }

    private void setUpView() {
        if (identityFlag == MASTER_FLAG) {// 群主
            exitOrDisband.setText("解散组圈");
        } else if (identityFlag == MEMBER_FLAG) {// 成员
            managerMembers.setVisibility(View.GONE);
            exitOrDisband.setText("退出组圈");
        } else if (identityFlag == VISITOR_FLAG) {// 游客
            managerMembers.setVisibility(View.GONE);
            exitOrDisband.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    private void initView() {

        exitOrDisband = (Button) findViewById(R.id.ring_setting_exit_or_disband);
        managerMembers = (RelativeLayout) findViewById(R.id.ring_setting_container_manager_members);
        exitOrDisband.setOnClickListener(this);
        managerMembers.setOnClickListener(this);
    }

    private void initTopBar() {
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        next.setVisibility(View.GONE);
        titleContent.setText("设置");
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });
    }

    public static void actionIntent(Activity activity, String groupId, String titles, int flag) {
        Intent intent = new Intent(activity, RingSettingActivity.class);
        intent.putExtra("identity_flag", flag);
        intent.putExtra("groupId", groupId);
        intent.putExtra("titles", titles);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ring_setting_container_manager_members:
                MembersListActivity.actionIntent(this, groupId, true);
                break;
            case R.id.ring_setting_exit_or_disband:
                if (identityFlag == MASTER_FLAG)
                    showDialog(true);
                else
                    showDialog(false);
                break;
        }
    }

    private void showDialog(final boolean isDeleteRing) {
        String msg = "";
        if (isDeleteRing)
            msg = "确定要解散该圈吗？";
        else
            msg = "确定要退出该圈吗?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage(msg).setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDeleteRing) {
                            disbandRing();
                        } else {
                            exitRing();
                        }

                    }
                }).create().show();
    }

    /**
     * 退出组圈
     */
    private void exitRing() {
        pgDialog.show();
        RequestParams params = new RequestParams();
        params.add("ringId", groupId);
        params.add("personId", QYApplication.getPersonId());
        HttpUtil.post(URLs.EXIT_RING, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                pgDialog.dismiss();
                if (arg2 != null && DataUtil.isOk(arg2)) {
                    //发送广播更新我的组圈
                    Intent intent = new Intent();
                    intent.setAction("Exitmygroups");
                    sendBroadcast(intent);
                    DBHelper.getInstance().delete(MyGroups.class, "groupid=?",
                            groupId);
                    DataSupport.deleteAll(SessionMsg.class, "titles=?", titles);
                    setResult(RingTeamInfoActivity.RESULT_EXIT_RING);
                    finish();
                } else {
                    ToastUtil.show(RingSettingActivity.this, "退出圈的操作失败！",
                            Gravity.CENTER);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                pgDialog.dismiss();
                ToastUtil.show(RingSettingActivity.this, "当前网络不给力，请稍后再试!",
                        Gravity.CENTER);
            }
        });
    }

    /**
     * 解散圈组
     */
    private void disbandRing() {
        pgDialog.show();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ringId", groupId);
        HTTPUtils.postWithToken(RingSettingActivity.this, URLs.DELETE_RING_THEME, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pgDialog.dismiss();
                ToastUtil.show(RingSettingActivity.this, "解散组圈失败!", Gravity.CENTER);
            }

            @Override
            public void onResponse(String s) {
                pgDialog.dismiss();
                if (s != null && !"".equals(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 0) {
                            //发送广播更新我的组圈
                            Intent intent = new Intent();
                            intent.setAction("Exitmygroups");
                            sendBroadcast(intent);
                            DBHelper.getInstance().delete(MyGroups.class, "groupid=?",
                                    groupId);
                            DBHelper.getInstance().delete(RingThemesTmp.class,
                                    "groupid=?", groupId);
                            DataSupport.deleteAll(SessionMsg.class, "titles=?", titles);
                            DataSupport.deleteAll(RingList.class, "ringId=?", groupId);
                            setResult(RingTeamInfoActivity.RESULT_DISBAND_RING);
                            finish();
//                            ToastUtil.showShort(RingSettingActivity.this, jsonObject.getString("errmsg"));
                        } else {
                            ToastUtil.showShort(RingSettingActivity.this, jsonObject.getString("errmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    /**
     * 关闭当前的界面
     */
    private void exitActivity() {
        if (isDeleteMember && isEditRing) {
            setResult(RingTeamInfoActivity.RESULT_EDIT_DELETE_FLAG);
        } else if (isDeleteMember) {
            setResult(RingTeamInfoActivity.RESULT_DELETE_MEMBER);
        } else if (isEditRing) {
            setResult(RingTeamInfoActivity.RESULT_EDIT_RING);
        } else {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_DELETE_MEMBER) {
            isDeleteMember = true;
        } else if (resultCode == RESULT_EDIT_RING) {
            isEditRing = true;
        }
    }
}
