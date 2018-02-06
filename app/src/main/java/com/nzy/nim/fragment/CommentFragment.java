package com.nzy.nim.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseFragment;
import com.nzy.nim.adapter.RingNotDynamicAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.DynamicList;
import com.nzy.nim.db.bean.GroupMembers;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.OldPersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeDynamicList;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    public static final String TAG = CommentFragment.class.getName();
    private PullToRefreshListView pullRefreshLv;
    private ListView listView;
    private TextView emptyContentTip;
    private View layout;
    private RingNotDynamicAdapter adapter;
    private List<DynamicList> commentAllList = new ArrayList<DynamicList>();
    private ArrayList<String> memberIds = null;
    private String groupId;
    private boolean isInclude;
    private boolean memberIdFlag = false;
    private MyReceiver myReceiver;

    public CommentFragment() {
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
            groupId = getArguments().getString("ringID");
            isInclude = getArguments().getBoolean("isInclude");
            layout = inflater.inflate(R.layout.fragment_comment, container, false);
            intUI();
            initBaseDatas();
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pullRefreshLv.doPullRefreshing(true, 1000);
            commentAllList.clear();
            pages = 1;
            adapter.notifyDataSetChanged();
//            getDynamic(ringId);
        }

    }

    /**
     * @author LIUBO
     * @date 2015-4-17下午3:19:41
     * @TODO 初始化基本数据
     */
    @SuppressWarnings("unchecked")
    private void initBaseDatas() {

        memberIds = getArguments().getStringArrayList("memberIds");
        if (memberIds == null) {// 传来的成员id集合为空时
            memberIds = (ArrayList<String>) QYApplication.getInstance().get(groupId, null);
            if (memberIds == null) {// 缓存中的成员Id集合为空时
                memberIds = new ArrayList<String>();
                getGroupMembers(groupId);
            } else
                memberIdFlag = true;
        } else {
            memberIdFlag = true;
        }
        // 获取评论集合
        getComment(groupId);
    }

    /**
     * @param groupId
     * @Author LIUBO
     * @TODO TODO 从服务器上获取该组的成员
     * @Date 2015-2-10
     * @Return void
     */
    private void getGroupMembers(final String groupId) {
        RequestParams params = new RequestParams();
        params.add("ringId", groupId);
        HttpUtil.post(URLs.LIST_BY_RINGID, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2 != null) {
                            List<OldPersonVO> list = JSON.parseArray(arg2,
                                    OldPersonVO.class);
                            List<GroupMembers> members = DBConversion
                                    .getInstance().getMembers(list, groupId);
                            memberIds.clear();
                            memberIds.addAll(getMemberIds(members));

                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        memberIds.clear();
                        memberIds.addAll(getMemberIds(DBHelper.getInstance()
                                .findAll(GroupMembers.class, "groupid=?",
                                        groupId)));
                    }
                });
    }


    /**
     * @param members
     * @return
     * @author LIUBO
     * @date 2015-4-10下午4:09:17
     * @TODO 获取成员id集合
     */
    private ArrayList<String> getMemberIds(List<GroupMembers> members) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < members.size(); i++) {
            list.add(members.get(i).getMemberId());
        }
        // 将集合放到缓存中
        QYApplication.getInstance().put(groupId, list);
        memberIdFlag = true;
        return list;
    }

    private void intUI() {
        pullRefreshLv = (PullToRefreshListView) layout.findViewById(R.id.fragment_all_groups_list);
        QYApplication.initPullRefulsh(pullRefreshLv, this, false, true, TAG);
        emptyContentTip = (TextView) layout.findViewById(R.id.all_groups_tip);
        listView = pullRefreshLv.getRefreshableView();
        adapter = new RingNotDynamicAdapter(getActivity(), commentAllList);
        listView.setAdapter(adapter);
    }

    private int pages = 1;
    private boolean isOver;

    /**
     * 获取非成员留言列表
     *
     * @param groupId
     */
    private void getComment(String groupId) {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ringId", groupId);
        hashMap.put("pageNow", "" + pages);
        hashMap.put("pageSize", "" + 10);//每页10条
        HTTPUtils.postWithToken(getActivity(), URLs.COMMENT_NEW_LIST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listView.setVisibility(View.GONE);
                ToastUtil.show(getActivity(), "获取失败!", Gravity.TOP);
                QYApplication.refulshComplete(pullRefreshLv, TAG);
//                adapter.notifyDataSetChanged();
                emptyContentTip.setVisibility(View.VISIBLE);
                emptyContentTip.setText("获取数据失败，请检查网络设置！");
            }

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
                            commentAllList.addAll(lists);
                            if(commentAllList.size()>=10){
                                isOver = true;
                                if(lists.size()==0){
                                    isOver=false;
                                }
                            }else{
                                isOver = false;
                            }
                            if (commentAllList.size() == 0) {
                                listView.setVisibility(View.GONE);
                                emptyContentTip.setVisibility(View.VISIBLE);
                                emptyContentTip.setText("该组圈暂无游客留言！");
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        commentAllList.clear();
        pages = 1;
        adapter.notifyDataSetChanged();
        getComment(groupId);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (isOver) {
            pages++;
            getComment(groupId);
            isOver = false;
        } else {
            QYApplication.refulshComplete(pullRefreshLv, TAG);
            Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_SHORT).show();
        }
    }
}
