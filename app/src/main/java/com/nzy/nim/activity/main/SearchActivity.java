package com.nzy.nim.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.RingAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.RingListVo;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.ClearEditText;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingList;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private PullToRefreshListView ptrListView;
    private ListView mlistListView;
    private List<RingList> allList = new ArrayList<RingList>();
    private RingAdapter adapter;
    private int pages = 1;
    private boolean isOver;
    private int position = -1;
    private View holderView;
    public static final String TAG = SearchActivity.class.getName();
    private String fuzzy;
    private TextView noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initTopBar();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 20://更新点赞数量
                String praiseCount = data.getStringExtra("praiseCount");
                boolean isPraised = data.getBooleanExtra("isPraised", false);
                if (praiseCount != null) {
                    if (position != -1) {
                        //容器中的点赞数量加1
                        allList.get(position).setDiggNumber((Integer.valueOf(praiseCount)));
                        allList.get(position).setIsDigg(isPraised);
                        //更新单个item
                        adapter.updateView(holderView, position);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initView() {
        ptrListView = (PullToRefreshListView) findViewById(R.id.fragment_all_groups_list);

        noResult = (TextView) findViewById(R.id.no_result);
        QYApplication.initPullRefulsh(ptrListView, this, false, true, TAG);
        ptrListView.setPullRefreshEnabled(false);
        mlistListView = ptrListView.getRefreshableView();
        adapter = new RingAdapter(this, allList);
        mlistListView.setAdapter(adapter);
        mlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchActivity.this.position = i - mlistListView.getHeaderViewsCount();
                holderView = view;
                Intent intent = new Intent(SearchActivity.this, RingTeamInfoActivity.class);
                intent.putExtra("groupId", allList.get(i - mlistListView.getHeaderViewsCount()).getRingId());
                startActivityForResult(intent, 0);
            }
        });

        final ClearEditText edit_search = (ClearEditText) findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edit_search.getText() != null && !"".equals(edit_search.getText().toString())) {
                    fuzzy = edit_search.getText().toString();
                    allList.clear();
                    pages = 1;
                    adapter.notifyDataSetChanged();
                    getRingThemes();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initTopBar() {
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        next.setVisibility(View.GONE);
        titleContent.setText("搜索");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * @Author LIUBO
     * @TODO TODO 从服务器上异步获取数据
     * @Date 2015-2-9
     * @Return void
     */
    private void getRingThemes() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("fuzzy", fuzzy);
        hashMap.put("pageNow", pages+"");
        Log.e("search ring", URLs.RING_LIST_BY_SEARCH + hashMap);
        HTTPUtils.postWithToken(SearchActivity.this, URLs.RING_LIST_BY_SEARCH, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(SearchActivity.this, "刷新失败!", Gravity.TOP);
                QYApplication.refulshComplete(ptrListView, TAG);
            }

            @Override
            public void onResponse(String s) {
                // 如果返回的数据不为空
                if (s != null) {
                    isOver = true;
                    JSONObject json = null;
                    try {
                        json = new JSONObject(s);
                        String errcode = json.getString("errcode");
                        if ("0".equals(errcode)) {
                            Gson gson = new Gson();
                            RingListVo ringListVo = gson.fromJson(s, RingListVo.class);
                            List<RingList> lists = ringListVo.getList();
                            allList.addAll(lists);
                            isOver = true;
                            adapter.setPointPosition(new RingAdapter.OnPointListens() {
                                @Override
                                public void getPointPosition(String ringId, View view, int itemIndex) {
                                    praise(ringId, view, itemIndex);
                                }
                            });
                            adapter.notifyDataSetChanged();
                            if (lists.size() == 0) {
                                isOver = false;
                                QYApplication.refulshComplete(ptrListView, TAG);
                            }
                            if (allList.size()==0){
                                noResult.setVisibility(View.VISIBLE);
                            }else{
                                noResult.setVisibility(View.GONE);
                            }
                        } else {
                            isOver = false;
                            QYApplication.refulshComplete(ptrListView, TAG);
                            Toast.makeText(SearchActivity.this, json.getString("errmsg"), Toast.LENGTH_SHORT).show();
                        }
                        QYApplication.refulshComplete(ptrListView, TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * @author LIUBO
     * @date 2015-4-5下午4:14:03
     * @TODO 点赞操作
     */
    private void praise(final String ringId, final View view, final int position) {
        RequestParams params = new RequestParams();
        params.add("ringId", ringId);
        params.add("personId", QYApplication.getPersonId());
        HttpUtil.post(URLs.ADD_RING_PRAISE, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
//						Log.e("","+s++s+s+"+arg2);
                        // 如果返回的数据不为空
                        if (!DataUtil.isEmpty(arg2)) {
                            if ("0".equals(DataUtil.dealMessage(arg2))) {// 点赞成功
                                //容器中的点赞数量加1
                                Integer diggNumber = allList.get(position).getDiggNumber();
                                allList.get(position).setDiggNumber((diggNumber + 1));
                                allList.get(position).setIsDigg(true);
                                //更新单个item
                                adapter.updateView(view, position);
                            } else if ("1".equals(DataUtil.dealMessage(arg2))) {// 取消点赞成功
                                //容器中的点赞数量减1
                                allList.get(position).setIsDigg(false);
                                Integer diggNumber = allList.get(position).getDiggNumber();
                                allList.get(position).setDiggNumber((diggNumber - 1));
                                //更新单个item
                                adapter.updateView(view, position);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {

                    }
                });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        allList.clear();
        pages = 1;
        adapter.notifyDataSetChanged();
        getRingThemes();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (isOver) {
            pages++;
            getRingThemes();
            isOver = false;
        } else {
            QYApplication.refulshComplete(ptrListView, TAG);
            ToastUtil.showShort(this, "没有更多数据");
        }
    }
}
