package com.nzy.nim.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseFragment;
import com.nzy.nim.activity.main.CommonInterestActivity;
import com.nzy.nim.activity.main.InterestationsCenterActivity;
import com.nzy.nim.adapter.CommonAdapter;
import com.nzy.nim.adapter.ViewHolder;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.bean.Interests;
import com.nzy.nim.db.tmpbean.DBConversion;
import com.nzy.nim.pulltorefresh.PullToRefreshBase;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.vo.HobbyVO;
import com.nzy.nim.vo.MyInterests;
import com.nzy.nim.vo.QYApplication;

import org.apache.http.Header;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MyInterestFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<ListView> {
    public static final String TAG = MyInterestFragment.class.getName();
    private ListView listView;
    private PullToRefreshListView pullView;
    private CommonAdapter<Interests> adapter;
    private List<Interests> datas = new ArrayList<Interests>();
    private boolean isFirst = true;
    private boolean isRefresh = false;
    private boolean isPrepare = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_layout_refresh_listview,
                container, false);
        initView(view);
        isPrepare = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initDatas();
    }

    /**
     * @param view
     * @author LIUBO
     * @date 2015-4-15下午4:09:54
     * @TODO 初始化布局
     */
    private void initView(View view) {
        LinearLayout topTitle = (LinearLayout) view
                .findViewById(R.id.common_layout_refresh_listView_title);
        topTitle.setVisibility(View.GONE);
        pullView = (PullToRefreshListView) view
                .findViewById(R.id.refresh_listview);
        listView = pullView.getRefreshableView();
        listView.setSelector(R.drawable.listview_selector_bg_1);
        listView.setBackgroundColor(getResources().getColor(R.color.white));
        QYApplication.initPullRefulsh(pullView, this, false, false, TAG);
        setAdapter();
        addHeadView();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    InterestationsCenterActivity.actionIntent(getActivity(),
                            false);
                } else {
                    Interests it = adapter.getItem(position - 1);
                    CommonInterestActivity.actionIntent(getActivity(),
                            it.getName(), it.getInterestId());
                }
            }
        });

    }

    /**
     * @author LIUBO
     * @date 2015-4-15下午5:08:10
     * @TODO 初始化数据
     */
    private void initDatas() {
        if (DBHelper.getInstance().isEmpty(Interests.class)) {
            getAllInterests();
        } else {
            initMyInterestData();
        }
    }

    private void initMyInterestData() {
        List<MyInterests> list = DBHelper.getInstance().findAll(
                MyInterests.class, "userid=?", QYApplication.getPersonId());
        if (list.size() == 0) {// 为空
            getMyHobbys();
        } else {
            datas.clear();
            for (int i = 0; i < list.size(); i++) {
                datas.add(DBHelper.getInstance().find(Interests.class,
                        "interestid=?", list.get(i).getInterestId()));
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @author LIUBO
     * @date 2015-4-15下午4:38:46
     * @TODO 添加头部view
     */
    @SuppressLint("InflateParams")
    private void addHeadView() {
        View view = getActivity().getLayoutInflater().inflate(
                R.layout.activity_childitem_list, null);
        RoundImageView icon = (RoundImageView) view
                .findViewById(R.id.child_image);
        TextView name = (TextView) view.findViewById(R.id.child_name);
        TextView sign = (TextView) view.findViewById(R.id.child_sign);
        sign.setVisibility(View.GONE);
        icon.setImageResource(R.drawable.ic_add_2);
        name.setText("添加新兴趣");
        listView.addHeaderView(view);
    }

    private void setAdapter() {
        adapter = new CommonAdapter<Interests>(getActivity(), datas,
                R.layout.activity_childitem_list) {
            @Override
            public void convert(ViewHolder helper, Interests item) {
                RoundImageView icon = helper.getView(R.id.child_image);
                TextView sign = helper.getView(R.id.child_sign);
                sign.setVisibility(View.GONE);

                if (item != null) {
                    helper.setText(R.id.child_name, item.getName());
                    ImageUtil.displayNetImg(item.getIconUrl(), icon);
                }
            }
        };
    }

    @Override
    protected void lazyLoad() {
        if (!isVisible || !isFirst || !isPrepare)
            return;
        initDatas();
        isFirst = false;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefresh = true;
        getMyHobbys();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    /**
     * @author LIUBO
     * @date 2015-4-15下午4:59:32
     * @TODO 获取我的兴趣
     */
    private void getMyHobbys() {
        RequestParams params = new RequestParams();
        params.add("personId", QYApplication.getPersonId());
        HttpUtil.post(URLs.LIST_OWNER_HOBBY, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2 != null) {
                            List<HobbyVO> list = JSON.parseArray(arg2,
                                    HobbyVO.class);
                            DBHelper.getInstance().delete(MyInterests.class,
                                    "userid=?", QYApplication.getPersonId());
                            // 将个人兴趣保存到本地
                            DataSupport.saveAll(DBConversion.getInstance()
                                    .getMyInterests(list));
                            datas.clear();
                            datas.addAll(DBConversion.getInstance()
                                    .getAllInterests(list));
                            adapter.notifyDataSetChanged();
                            if (isRefresh) {
                                QYApplication.refulshComplete(pullView, TAG);
                                isRefresh = false;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        ToastUtil.show(getActivity(), R.string.server_is_busy,
                                Gravity.CENTER);
                    }
                });
    }

    /**
     * @author LIUBO
     * @date 2015-4-15下午12:32:41
     * @TODO 获取所有兴趣
     */
    private void getAllInterests() {
        RequestParams params = new RequestParams();
        params.add("class", "HobbyVO");
        HttpUtil.post(URLs.LIST_BY_CLASS, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2 != null) {
                            List<HobbyVO> hobbys = JSON.parseArray(arg2,
                                    HobbyVO.class);
                            List<Interests> list = DBConversion.getInstance()
                                    .getAllInterests(hobbys);
                            DataSupport.saveAll(list);
                            initMyInterestData();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        ToastUtil.show(getActivity(), R.string.server_is_busy,
                                Gravity.TOP);
                    }
                });
    }
}
