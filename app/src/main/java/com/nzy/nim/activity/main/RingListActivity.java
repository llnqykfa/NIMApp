package com.nzy.nim.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.NetUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RingListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener{
    public static final String TAG = RingListActivity.class.getName();
    // 显示数据的listView
    private ListView ringLv;
    // List集合数据
    private List<RingThemesTmp> datas = new ArrayList<RingThemesTmp>();
    // 适配器
    private RingAdapter adapter;
    // 刷新的listView
    private PullToRefreshListView pullToRefresh;
    public static boolean isRefresh = false;
    private RelativeLayout rel_search;
    private ProgressBar prog_loding;
    private MyReceiver myReceiver;
    //    private int pages = 1;
    private String lasttime;
    private List<RingList> allList = new ArrayList<RingList>();
    private boolean isOver;
    private View search_header;
    private int position = -1;
    private View holderView;
    private int LIMIT = 20;
    private int add = 20;
    private int refresh = 0;//0:初始 1:下拉刷新 2:上拉加载
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_list);
        initTopBar();
        initView();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("mygroups");
        filter.addAction("addMygroups");
        filter.addAction("Exitmygroups");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    /**
     * @TODO 初始化标题栏
     */
    private void initTopBar() {
        ImageView btnBack = (ImageView)findViewById(R.id.top_bar_back);
        TextView tvTitle = (TextView)findViewById(R.id.top_bar_content);
        Button btnSure = (Button)findViewById(R.id.top_bar_next);
        btnSure.setText("发起组圈");
        btnSure.setTextSize(14);
        tvTitle.setText("组圈部落");
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeRingFirstActivity.actionIntent(RingListActivity.this);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 收到广播更新listview
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("addMygroups") || intent.getAction().equals("Exitmygroups")) {
                refresh = 0;
                pullToRefresh.doPullRefreshing(true, 1000);
                allList.clear();
//				pages = 1;
                LIMIT = 20;
                adapter.notifyDataSetChanged();
                if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
                    getRingThemes();
                } else {
                    getData();
                    ToastUtil.showShort(RingListActivity.this, "无法连接到网络，请检查网络设置");
                }
            } else {
                String praiseCount = intent.getStringExtra("praiseCount");
                boolean isPraised = intent.getBooleanExtra("isPraised", false);
                if (praiseCount != null) {
                    if (position != -1) {
                        //容器中的点赞数量加1
                        allList.get(position).setDiggNumber((Integer.valueOf(praiseCount)));
                        allList.get(position).setIsDigg(isPraised);
                        //更新单个item
                        adapter.updateView(holderView, position);
                    }
                } else {
                    refresh = 0;
                    allList.clear();
//					pages = 1;
                    LIMIT = 20;
                    adapter.notifyDataSetChanged();
                    if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
                        getRingThemes();
                    } else {
                        getData();
                        ToastUtil.showShort(RingListActivity.this, "无法连接到网络，请检查网络设置");
                    }
                }
            }
        }
    }
    /**
     * @TODO 初始化布局
     */
    protected void initView() {
        prog_loding = (ProgressBar)findViewById(R.id.prog_loding);
        prog_loding.setVisibility(View.VISIBLE);
        // 关联布局中的组件
        pullToRefresh = (PullToRefreshListView)findViewById(R.id.fragment_all_groups_list);
        pullToRefresh.setOnRefreshListener(this);
        ringLv = pullToRefresh.getRefreshableView();// 得到实际的ListVIew
        QYApplication.initPullRefulsh(pullToRefresh, this, false, true, TAG);

        search_header = getLayoutInflater().inflate(R.layout.search_ring, null);
        rel_search = (RelativeLayout) search_header.findViewById(R.id.rel_search);
        ringLv.addHeaderView(search_header, null, false);
        adapter = new RingAdapter(RingListActivity.this, allList);
        ringLv.setAdapter(adapter);

        ringLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                RingListActivity.this.position = position - ringLv.getHeaderViewsCount();
                holderView = view;
                RingTeamInfoActivity.actionIntent(RingListActivity.this, allList.get(position - ringLv.getHeaderViewsCount()).getRingId());
            }
        });

        rel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RingListActivity.this, SearchActivity.class));
            }
        });
    }

    private void getData() {
        allList.clear();
        prog_loding.setVisibility(View.GONE);
        List<RingList> ringLists = DataSupport.limit(LIMIT).find(RingList.class);
        if (ringLists != null) {
            allList.addAll(ringLists);
            adapter.setPointPosition(new RingAdapter.OnPointListens() {
                @Override
                public void getPointPosition(String ringId, View view, int itemIndex) {
                    praise(ringId, view, itemIndex);
                }
            });
            adapter.notifyDataSetChanged();
        }

        QYApplication.refulshComplete(pullToRefresh, TAG);
        if(refresh==0){
            pullToRefresh.doPullRefreshing(true, 1000);
//            getRingThemes();
        }
    }

    /**
     * @TODO TODO 从服务器上异步获取数据
     * @Return void
     */
    private void getRingThemes() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (LIMIT > 20) {
            hashMap.put("lastTime", lasttime);
        }
        hashMap.put("size", "" + 20);
        HTTPUtils.postWithToken(RingListActivity.this, URLs.RING_LIST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                prog_loding.setVisibility(View.GONE);
                isOver = true;
                if (refresh == 0) {
                    allList.clear();
                }
                getData();
                ToastUtil.showShort(RingListActivity.this, R.string.server_is_busy);
                QYApplication.refulshComplete(pullToRefresh, TAG);
            }

            @Override
            public void onResponse(String s) {
                // 如果返回的数据不为空
                if (s != null) {
                    isOver = true;
                    prog_loding.setVisibility(View.GONE);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(s);
                        String errcode = json.getString("errcode");
                        if ("0".equals(errcode)) {
                            Gson gson = new Gson();
                            RingListVo ringListVo = gson.fromJson(s, RingListVo.class);
                            List<RingList> lists = ringListVo.getList();
                            add = lists.size();
                            if (lists.size() == 0) {
                                isOver = false;
                                QYApplication.refulshComplete(pullToRefresh, TAG);
                            } else {
                                List<RingList> data = DBConversion.getInstance().getRings(lists);

                                List<RingList> ringLists = DataSupport.findAll(RingList.class);//获取数据库值
                                if (refresh == 2) {
                                    /**
                                     * 判断数据库里数据是否存在
                                     */
                                    for (int i = 0; i < lists.size(); i++) {
                                        RingList ringList = lists.get(i);
                                        String ringId = ringList.getRingId();
                                        for (int j = 0; j < ringLists.size(); j++) {
                                            RingList dataList = ringLists.get(j);
                                            String dataRingId = dataList.getRingId();
                                            if (ringId.equals(dataRingId)) {
                                                DataSupport.deleteAll(RingList.class, "ringId=?", ringId);
                                            }
                                        }
                                    }
                                    DataSupport.saveAll(data);
                                } else {
                                    allList.clear();
                                    DataSupport.deleteAll(RingList.class);
                                    DataSupport.saveAll(data);
                                }
                                lasttime = ringListVo.getLastTime();
                                allList.addAll(lists);
                                adapter.setPointPosition(new RingAdapter.OnPointListens() {
                                    @Override
                                    public void getPointPosition(String ringId, View view, int itemIndex) {
                                        praise(ringId, view, itemIndex);
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }

                            QYApplication.refulshComplete(pullToRefresh, TAG);
                        } else {
                            isOver = false;
                            QYApplication.refulshComplete(pullToRefresh, TAG);
                            Toast.makeText(RingListActivity.this, json.getString("errmsg"), Toast.LENGTH_SHORT).show();
                        }
                        QYApplication.refulshComplete(pullToRefresh, TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
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
        refresh = 1;
        LIMIT = 20;
        adapter.notifyDataSetChanged();
        if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
            getData();
            getRingThemes();
        } else {
            getData();
            ToastUtil.showShort(RingListActivity.this, "无法连接到网络，请检查网络设置");
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

        if (NetUtil.checkNetwork(QYApplication.getMyContexts())) {
            if (isOver) {
//			pages++;
                LIMIT += add;
                refresh = 2;
                getRingThemes();
                isOver = false;
            } else {
                QYApplication.refulshComplete(pullToRefresh, TAG);
                ToastUtil.showShort(RingListActivity.this, "没有更多数据");
            }
        } else {
            isOver = true;
            QYApplication.refulshComplete(pullToRefresh, TAG);
            ToastUtil.showShort(RingListActivity.this, "无法连接到网络，请检查网络设置");
        }
    }

    public static void actionIntent(Context context) {
        context.startActivity(new Intent(context, RingListActivity.class));
    }

}
