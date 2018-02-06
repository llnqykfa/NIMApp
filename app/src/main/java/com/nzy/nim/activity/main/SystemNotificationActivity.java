package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.db.bean.SessionMsg;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.vo.MsgManager;
import com.nzy.nim.vo.QYApplication;

public class SystemNotificationActivity extends BaseActivity {
    private ImageView backBtn;
    private Button nextBtn;
    private TextView title;


    private String TAG=SystemNotificationActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notification);
        QYApplication.ACTIVITY_NAME="系统通知";
        DBHelper.getInstance().clearUnReadTips(SessionMsg.class, null,
                MsgManager.SYSTEM_NOTIFICATIONS);
        nextBtn = (Button) findViewById(R.id.top_bar_next);
        title = (TextView) findViewById(R.id.top_bar_content);
        title.setText("系统通知");
        nextBtn.setVisibility(View.GONE);
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public static void actionIntent(Context context) {
        context.startActivity(new Intent(context,
                SystemNotificationActivity.class));
    }
}
