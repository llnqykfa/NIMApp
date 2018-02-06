package com.nzy.nim.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseFragment;
import com.nzy.nim.activity.main.MakeRingFirstActivity;
import com.nzy.nim.activity.main.RingTeamInfoActivity;
import com.nzy.nim.adapter.MyGroupsAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.MyGroups;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.NetUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeVO;

import org.apache.http.Header;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class

MyGroupsFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<ListView> {
    public static final String TAG = MyGroupsFragment.class.getName();
    // 显示数据的listView
    private ListView myGroupLv;
    private PullToRefreshListView pullRefreshListView;
    // List集合数据
    private List<MyGroups> datas = new ArrayList<MyGroups>();
    // 适配器
    private MyGroupsAdapter adapter;
    private ProgressDialog pgDialog;
    private boolean isFirst = true;
    private boolean isPrepare = false;
    private MyReceiver myReceiver;
    private int refresh = 0;//0:初始 1:下拉刷新 2:上拉加载
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_layout_refresh_listview,
                container, false);
        initView(view);
        isPrepare = true;
        //加载组圈信息
        getData();
//        getMyRings(QYApplication.getPersonId());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("addMygroups");
        filter.addAction("Exitmygroups");
        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//			if(NetUtil.checkNetwork(getActivity())){
//				pgDialog.show();
//			}else{
//				pgDialog.dismiss();
//			}
            datas.clear();
            refresh = 0;
            adapter.notifyDataSetChanged();
            if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
                getData();
                getMyRings(QYApplication.getPersonId());
            } else {
                getData();
                ToastUtil.showShort(getActivity(), "无法连接到网络，请检查网络设置");
            }
        }
    }

    /**
     * 获取初始数据
     */
    private void getData(){
        datas.clear();
        datas.addAll(initDatas());
        adapter.notifyDataSetChanged();
        QYApplication.refulshComplete(pullRefreshListView, TAG);
        if(refresh==0){
            pullRefreshListView.doPullRefreshing(true, 1000);
        }
    }
    private List<MyGroups> initDatas() {
        return DBHelper.getInstance().findAllByOrder(MyGroups.class,
                "jointime desc", "userid=?", QYApplication.getPersonId());
    }

    private void initView(View view) {
        LinearLayout topTitle = (LinearLayout) view
                .findViewById(R.id.common_layout_refresh_listView_title);
        topTitle.setVisibility(View.GONE);
        pullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.refresh_listview);
        myGroupLv = pullRefreshListView.getRefreshableView();
        myGroupLv.setDivider(null);
        QYApplication.initPullRefulsh(pullRefreshListView, this, false, false,
                TAG);
        pgDialog = DialogHelper.getSDialog(getActivity(), "加载中···", false);
        adapter = new MyGroupsAdapter(getActivity(), datas);
        myGroupLv.setAdapter(adapter);
        myGroupLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position > 0) {
                    if (position == 1) {
                        MakeRingFirstActivity.actionIntent(getActivity());
                    } else
                        RingTeamInfoActivity.actionIntent(getActivity(),
                                adapter.getItem(position).getGroupId());

                }
            }
        });

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        datas.clear();
        refresh = 1;
        adapter.notifyDataSetChanged();
        if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
            getData();
            getMyRings(QYApplication.getPersonId());
        } else {
            getData();
            ToastUtil.showShort(getActivity(), "无法连接到网络，请检查网络设置");
        }
    }

    /**
     * @param personId
     * @author 刘波
     * @date 2015-2-28上午11:13:26
     * @todo 获取组圈信息
     */
    private void getMyRings(String personId) {
        RequestParams param = new RequestParams();
        param.add("personId", personId);
        HttpUtil.post(URLs.LIST_MY_RING, param, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (arg2 != null) {
                    try {
                        List<RingThemeVO> list = JSON.parseArray(arg2,
                                RingThemeVO.class);
                        datas.clear();
                        DBHelper.getInstance().delete(MyGroups.class, "userid=?",
                                QYApplication.getPersonId());
                        if (list.size() > 0) {
                            List<MyGroups> groupList = DBConversion.getInstance()
                                    .getGroups(list);
                            if (groupList.size() > 0)
                                DataSupport.saveAll(groupList);
                            datas.addAll(initDatas());
                        }
                        adapter.notifyDataSetChanged();
                        QYApplication.refulshComplete(pullRefreshListView, TAG);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                pgDialog.dismiss();
                isFirst = false;
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                if (refresh == 0) {
                    datas.clear();
                }
                getData();
                ToastUtil.show(getActivity(), R.string.server_is_busy, Gravity.TOP);
                pgDialog.dismiss();
            }
        });
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    protected void lazyLoad() {
        if (!isVisible || !isFirst || !isPrepare)
            return;
        datas.clear();
        datas.addAll(initDatas());
        if (datas.size() == 0) {
            if (NetUtil.checkNetwork(getActivity())) {
                pgDialog.show();
            } else {
                pgDialog.dismiss();
            }
            getMyRings(QYApplication.getPersonId());
        } else {
            isFirst = false;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

    }
}
