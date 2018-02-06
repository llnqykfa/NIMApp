package com.nzy.nim.activity.base;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.helper.NotificationHelper;
import com.nzy.nim.manager.AppManager;
import com.nzy.nim.vo.QYApplication;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by Administrator on 2016/11/23.
 */
public class BaseActivity extends FragmentActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // 去掉标题栏（注意必须在setContentView()方法之前调用）
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);
            // 获取app管理者实例
		/* 将当前的活动添加到管理活动的栈中（this指向继承BaseActivity的子类）*/
            AppManager.getInstance().addActivity(this);
        }//13806063565 a111111

        @Override
        protected void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            // 获取通知管理者
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String name = this.getClass().getName();
            if (NotificationHelper.CHATACTIVITY_NAME.equals(name)
                    || NotificationHelper.CHATACTIVITY_NAME.equals(name)) {
                nm.cancelAll();
                List<SessionMsg> sendersList = DataSupport.where("userid=?",
                        QYApplication.getPersonId()).find(SessionMsg.class);
                for (SessionMsg rm : sendersList) {
                    QYApplication.getInstance().remove(
                            "notify_" + rm.getSendUserId());
                }
            }else{
                nm.cancelAll();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
        }
    }
