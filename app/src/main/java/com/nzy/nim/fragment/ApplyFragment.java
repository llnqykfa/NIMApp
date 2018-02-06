package com.nzy.nim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.PayWebviewActivity;
import com.nzy.nim.activity.main.QYWebviewAvtivity;
import com.nzy.nim.activity.main.RingListActivity;
import com.nzy.nim.constant.BizConstants;
import com.nzy.nim.convenientbanner.CBViewHolderCreator;
import com.nzy.nim.convenientbanner.ConvenientBanner;
import com.nzy.nim.convenientbanner.OnItemClickListener;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.view.LocalImageHolderView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @project Fjzq
 */
public class ApplyFragment extends Fragment implements View.OnClickListener, OnItemClickListener {
    private UserInfo user;
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private String userId;
    private String userPhone;
    private String userName;
    private String accessToken;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_fragment, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTopBar(getView());
        initView(getView());
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
        if (user!=null) {
            userId = user.getPersonId();
            userPhone = user.getPhone();
            userName = user.getUserName();
            accessToken = StringUtil.getMD5ofStrNormal(userId + BizConstants.ACCESS_TOKEN_SALT);
        }
    }

    private void init(){
        loadTestDatas();
        //本地图片例子
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                //设置指示器的方向
                .setOnItemClickListener(this);


    }
    /*
    加入测试Views
    * */
    private void loadTestDatas() {
        //本地图片集合
        for (int position = 1; position < 5; position++)
            localImages.add(getResId("foot_banner_" + position, R.drawable.class));

    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
        /**
         * @param view
         * @TODO 初始化标题栏
         */
    private void initTopBar(View view) {
        ImageView back = (ImageView) view.findViewById(R.id.top_bar_back);
        final Button next = (Button) view.findViewById(R.id.top_bar_next);
        TextView title = (TextView) view.findViewById(R.id.top_bar_content);
        ImageView topBarMoreIv = (ImageView) view.findViewById(R.id.top_bar_next_iv);
        topBarMoreIv.setVisibility(View.GONE);
        title.setText("福建足球注册中心");
        back.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
    }
    /**
     * @param view
     * @TODO 初始化布局空间
     */
    private void initView(View view) {
        convenientBanner = (ConvenientBanner)view.findViewById(R.id.convenientBanner);
        view.findViewById(R.id.rel_huiyuanxiehui).setOnClickListener(this);
        view.findViewById(R.id.rel_julebu).setOnClickListener(this);
        view.findViewById(R.id.rel_gerenhuiyuan).setOnClickListener(this);
        view.findViewById(R.id.rel_jiaolianyuan).setOnClickListener(this);
        view.findViewById(R.id.rel_caipanyuan).setOnClickListener(this);
        view.findViewById(R.id.rel_yundongyuan).setOnClickListener(this);
        view.findViewById(R.id.rel_saishi).setOnClickListener(this);
        view.findViewById(R.id.rel_shangjiazhuce).setOnClickListener(this);
        view.findViewById(R.id.rel_shangjiazhanshi).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_huiyuanxiehui:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.ASS_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "1", true);
                break;
            case R.id.rel_julebu:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.CLUB_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "2", true);
                break;
            case R.id.rel_gerenhuiyuan:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.MEMBER_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "6", false);
                break;
            case R.id.rel_jiaolianyuan:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.COACH_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "3", true);
                break;
            case R.id.rel_caipanyuan:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.REFEREE_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "5", true);
                break;
            case R.id.rel_yundongyuan:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.PLAYER_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "4", false);
                break;
            case R.id.rel_saishi:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.MATCH_RECORD_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "", false);
                break;
            case R.id.rel_shangjiazhuce:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.MERCH_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "", false);
                break;
            case R.id.rel_shangjiazhanshi:
                PayWebviewActivity.loadUrl(getActivity(), BizConstants.BIZSERVER_ADDRESS + BizConstants.PROD_BIZ_URL + "?reqUserId=" + userId + "&accessToken=" + accessToken + "&reqUserName=" + userName + "&reqUserPhone=" + userPhone, "", false);
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(),"点击了第"+position+"个",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(2000);//设置两秒刷新
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }
}
