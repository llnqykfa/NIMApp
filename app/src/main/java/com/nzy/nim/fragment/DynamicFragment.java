package com.nzy.nim.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseFragment;
import com.nzy.nim.adapter.DynamicAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.DynamicList;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeDynamicList;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DynamicFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener {
    public static final String TAG = DynamicFragment.class.getName();
    private View layout;
    private ProgressDialog pgDialog;
    private PullToRefreshListView pullRefreshLv;
    private ListView listView;
    private TextView emptyContentTip;
    private int pages = 1;
    private List<DynamicList> allList = new ArrayList<DynamicList>();
    private boolean isOver;
    private DynamicAdapter adapter;
    private String ringId;//组圈id
    //    private ArrayList<String> memberIds = null;
    private boolean memberIdFlag = false;
    private boolean isInclude;
    private MyReceiver myReceiver;
    private ProgressBar prog_loding;

    public DynamicFragment() {
    }

    @Override
    public void onDestroyView() {
        ViewGroup parent = (ViewGroup) layout.getParent();
        parent.removeView(layout);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            layout = inflater.inflate(R.layout.fragment_dynamic, container, false);
            ringId = getArguments().getString("ringID");
            isInclude = getArguments().getBoolean("isInclude");
            intUI();
            getDynamic(ringId);
        }
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("addDynamic");
        myReceiver = new MyReceiver();
        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        allList.clear();
        pages = 1;
//        adapter.notifyDataSetChanged();
        getDynamic(ringId);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (isOver) {
            pages++;
            getDynamic(ringId);
            isOver = false;
        } else {
            QYApplication.refulshComplete(pullRefreshLv, TAG);
            Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pullRefreshLv.doPullRefreshing(true, 1000);
            allList.clear();
            pages = 1;
            adapter.notifyDataSetChanged();
//            getDynamic(ringId);
        }

    }

    private void intUI() {
        pgDialog = DialogHelper.getSDialog(getActivity(), "获取评论中···", false);
        pullRefreshLv = (PullToRefreshListView) layout.findViewById(R.id.fragment_all_groups_list);
        prog_loding = (ProgressBar) layout.findViewById(R.id.prog_loding);
        prog_loding.setVisibility(View.VISIBLE);
        emptyContentTip = (TextView) layout.findViewById(R.id.all_groups_tip);
        QYApplication.initPullRefulsh(pullRefreshLv, this, false, true, TAG);
        pullRefreshLv.setOnRefreshListener(this);
        listView = pullRefreshLv.getRefreshableView();
        adapter = new DynamicAdapter(getActivity(), allList);
//        adapter = new DynamicAdapter(this, allList,memberIds);
        listView.setAdapter(adapter);
    }

    /**
     * 获取动态列表
     *
     * @param groupId
     */
    private void getDynamic(String groupId) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ringId", groupId);
        hashMap.put("pageNow", "" + pages);
        hashMap.put("pageSize", "" + 10);
        HTTPUtils.postWithToken(getActivity(), URLs.DYNAMIC_NEW_LIST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                prog_loding.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                QYApplication.refulshComplete(pullRefreshLv, TAG);
                adapter.notifyDataSetChanged();
                emptyContentTip.setVisibility(View.VISIBLE);
                emptyContentTip.setText("获取数据失败，请检查网络设置!");
            }

            @Override
            public void onResponse(String s) {
                prog_loding.setVisibility(View.GONE);
                if (s != null) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(s);
                        String errcode = json.getString("errcode");
                        if ("0".equals(errcode)) {
                            Gson gson = new Gson();
                            RingThemeDynamicList ringListVo = gson.fromJson(s, RingThemeDynamicList.class);
                            List<DynamicList> lists = ringListVo.getList();
                            allList.addAll(lists);
                            if(allList.size()>=10){
                                isOver=true;
                                if(lists.size()==0){
                                    isOver=false;
                                }
                            }else{
                                isOver=false;
                            }
                            if (allList.size() == 0) {
                                listView.setVisibility(View.GONE);
                                emptyContentTip.setVisibility(View.VISIBLE);
                                emptyContentTip.setText("该组圈暂无动态!");
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                emptyContentTip.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            isOver = false;
                            QYApplication.refulshComplete(pullRefreshLv, TAG);
                            Toast.makeText(getActivity(), json.getString("errmsg"), Toast.LENGTH_SHORT).show();
                        }
                        QYApplication.refulshComplete(pullRefreshLv, TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }
}
