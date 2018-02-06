package com.nzy.nim.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nzy.nim.R;
import com.nzy.nim.adapter.SystemMsgAdapter;
import com.nzy.nim.db.bean.Invitations;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SystemNotifyFragment extends Fragment {
    private ListView listView;// 添加的好友的ListView
    private SystemMsgAdapter adapter; // 新朋友的适配器
    private List<Invitations> mDatas = new ArrayList<Invitations>();// 适配器的 数据源


    public SystemNotifyFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_newfriends, container,
                false);
        listView = (ListView) view.findViewById(R.id.lv_newFriends);
        getData();

        adapter = new SystemMsgAdapter(getActivity(), mDatas,
                R.layout.activity_newfriends_item);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Invitations invitations = mDatas.get(position);
                showRemindDialog(invitations);
                return false;
            }
        });
        return view;
    }

    private void getData() {
        // 获取新朋友表中的数据
        List<Invitations> temp = DataSupport.where("userid=? and status=?", QYApplication.getPersonId(),""+ MsgManager.INVITE_OTHER).order("recemsgtime desc").find(Invitations.class);
        mDatas.clear();
        mDatas.addAll(temp);
    }

    private void showRemindDialog(final Invitations invitations){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("您确定要取消关注吗?")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DataSupport.deleteAll(Invitations.class,"userid=? and msgid=?",QYApplication.getPersonId(),invitations.getMsgId());
                        getData();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
        AlertDialog alert = builder.create();
    }
}
