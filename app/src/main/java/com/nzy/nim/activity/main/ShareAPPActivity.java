package com.nzy.nim.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.vo.QYApplication;

public class ShareAPPActivity extends BaseActivity implements View.OnClickListener{
    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app);
        QYApplication.ACTIVITY_NAME="分享APP";
        initTopBar();

    }
    private void initTopBar() {
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        next = (Button) findViewById(R.id.top_bar_next);
        next.setOnClickListener(this);
        titleContent.setText("分享APP");
        next.setText("分享");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.top_bar_next:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "福建足球欢迎您！"+"\n"+" www.fjfootball.cn");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to_share)));
                break;
        }
    }
}
