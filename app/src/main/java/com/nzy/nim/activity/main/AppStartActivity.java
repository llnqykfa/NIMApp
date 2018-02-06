package com.nzy.nim.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.helper.FaceImageUtils;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/11/23.
 */
public class AppStartActivity extends BaseActivity {
    public static AppStartActivity appStartActivity;// 本类对象实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        QYApplication.ACTIVITY_NAME="系统宣传主页";
        // 设置用户的唯一标识
        appStartActivity = AppStartActivity.this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tryLogin();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appStartActivity != null)
            appStartActivity = null;
    }

    /**
     * @todo 尝试登陆
     */
    private void tryLogin() {
        UserInfo user = DataSupport.findLast(UserInfo.class);
        if (user != null) {
            QYApplication.getInstance().put("current_userid",
                    user.getPersonId());
            SPHelper.saveCurrentUserId(user.getPersonId());
            MainActivity.actionIntent(appStartActivity);
        } else {
            LoginActivity.actionIntent(appStartActivity);
        }
        FaceImageUtils.getInstace().saveFace(AppStartActivity.this);
        this.finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
