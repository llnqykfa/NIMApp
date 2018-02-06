package com.nzy.nim.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzy.nim.R;
import com.nzy.nim.adapter.WorkPlatformAdapter;
import com.nzy.nim.view.SquaredGridView;

/**
 * Created by Administrator on 2016/11/28.
 */
public class WorkPlatformFragment extends Fragment implements View.OnClickListener {

    private View view;
    private MyReceiver myReceiver;
    private SquaredGridView squaredGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_workplatform, container, false);
            registerBroadReceiver();
        }
        return view;
    }

    private void registerBroadReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("update_workplatform_fragment");
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

    @Override
    public void onClick(View v) {

    }

    private void initView(View view) {
        squaredGridView = (SquaredGridView) view.findViewById(R.id.index_workplatform_gridview);
        //初始化功能板块
        String[] funTitles = new String[]{"通讯录","考勤","任务","客户","客户联系人","管理驾驶舱","费用","客户拜访","拍照上传","更多","",""};
        int[] funImgs = new int[]{R.drawable.work_addbook, R.drawable.work_duty, R.drawable.work_task, R.drawable.work_cust,
        R.drawable.work_contact, R.drawable.work_manage, R.drawable.work_fee, R.drawable.work_visit, R.drawable.work_pic, R.drawable.work_more, R.drawable.work_blank, R.drawable.work_blank};
        WorkPlatformAdapter workPlatformAdapter = new WorkPlatformAdapter(WorkPlatformFragment.this.getActivity(), funImgs, funTitles);
        squaredGridView.setAdapter(workPlatformAdapter);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (user != null)
                user = DBHelper.getInstance().getUserById(
                        QYApplication.getPersonId());
            if (user != null)
                showNewUserInfo(user);*/
        }
    }
}
