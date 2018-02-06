package com.nzy.nim.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.EditUserInfoActivity;
import com.nzy.nim.activity.main.ExitActivity;
import com.nzy.nim.activity.main.MyPersonalInfoActivity;
import com.nzy.nim.activity.main.SettingActivity;
import com.nzy.nim.activity.main.ShareAPPActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.tmpbean.SchoolNew;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

public class MySelfFragment extends Fragment implements OnClickListener {
    // 顶部信息
    private TextView topBarContent;
    public static final int RESULT_OK = -1;
    // 返回键，下一步
    private ImageView topBarBack;
    private Button topBarMore;
    // 个人信息的父布局

    // 用户个人头像
    private RoundImageView userHeadImage;
    // 用户名称
    private TextView tvMyName;
    // 我的课表
    private RelativeLayout myCurriculum;

    private UserInfo user;
    private TextView tv_signature;
    final int MODIFY_USER_SIGNATURE = 15;
    private View view;
    private MyReceiver myReceiver;
    private SchoolNew schoolNew;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_me_new, container, false);
            registerBroadReceiver();
        }
        return view;
    }

    private void registerBroadReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("update_myself_fragment");
        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(myReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
    }


    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (user != null)
                user = DBHelper.getInstance().getUserById(
                        QYApplication.getPersonId());
            if (user != null)
                showNewUserInfo(user);
        }
    }

    /**
     * @param view
     * @author quanyi
     * @date 2015-3-25下午4:52:00
     * @TODO TODO初始化数据
     */
    private void initView(View view) {
        tv_signature = (TextView) view.findViewById(R.id.tv_signature);
        topBarBack = (ImageView) view.findViewById(R.id.top_bar_back);
        topBarContent = (TextView) view.findViewById(R.id.top_bar_content);
        topBarMore = (Button) view.findViewById(R.id.top_bar_next);
        RelativeLayout rl_personality = (RelativeLayout) view.findViewById(R.id.rl_personality);
        RelativeLayout rl_to_setting = (RelativeLayout) view.findViewById(R.id.rl_to_setting);
        myCurriculum = (RelativeLayout) view
                .findViewById(R.id.fragment_me_ll_Curriculum);
        userHeadImage = (RoundImageView) view
                .findViewById(R.id.fragment_me_head);
        tvMyName = (TextView) view.findViewById(R.id.fragment_me_nike);
        tvMyName.setShadowLayer(1F, 0F, 2F, Color.rgb(7, 0, 2));
        userHeadImage.setOnClickListener(this);
        myCurriculum.setOnClickListener(this);
        rl_to_setting.setOnClickListener(this);
        rl_personality.setOnClickListener(this);
        topBarMore.setOnClickListener(this);
        topBarBack.setVisibility(View.INVISIBLE);
        topBarMore.setVisibility(View.VISIBLE);
        topBarMore.setText("设置");
        topBarMore.setVisibility(View.INVISIBLE);
        topBarContent.setText("个人中心");

    }

    @Override
    public void onStart() {
        super.onStart();
        user = DBHelper.getInstance().getUserById(
                QYApplication.getPersonId());
        if (user != null)
            showNewUserInfo(user);
    }

    /**
     * @author quanyi
     * @date 2015-3-25下午5:12:07
     * @TODO TODO初始化用户信息
     */
    private void showNewUserInfo(UserInfo user) {
        if (!DataUtil.isEmpty(user.getPhotoPath())) {
            // 显示头像
            ImageUtil.displayHeadImg(user.getPhotoPath(), userHeadImage);
        }
        if (!DataUtil.isEmpty(user.getUserName())) {
            tvMyName.setText(user.getUserName());
            tv_signature.setText(user.getDesignInfo());
        } else {
            tvMyName.setText("");
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.fragment_me_head:
                MyPersonalInfoActivity.actionIntent(getActivity());
                break;
            case R.id.rl_to_setting:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_me_ll_Curriculum:
                startActivity(new Intent(getActivity(), ShareAPPActivity.class));
                break;
            case R.id.rl_personality:
			EditUserInfoActivity.actionIntent(getActivity(), user.getDesignInfo(),MODIFY_USER_SIGNATURE,
					EditUserInfoActivity.MODIFY_USER_SIGNATURE_FRAGMENT_FLAG);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            if (requestCode == MODIFY_USER_SIGNATURE) {
                if (null != data) {
                    final String newSign = data.getStringExtra("edit_sign")
                            .toString().trim();
                    HttpHelper.commitEditUserInfo(getActivity(), URLs.UPDATE_DESIGN_INFO, "designInfo",
                            newSign, new HttpHelper.OnSuccessListener() {
                                @Override
                                public void onFinish() {
                                    user.setDesignInfo(newSign);
                                    user.updateAll("personId=?", user.getPersonId());
                                }
                            });
                }
            }else if(requestCode == 3){
                user = DBHelper.getInstance().find(UserInfo.class, "personId=?", QYApplication.getPersonId());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
