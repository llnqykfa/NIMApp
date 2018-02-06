package com.nzy.nim.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;

public class SacnResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sacn_result);
        String sacn_result1 = getIntent().getStringExtra("sacn_result");
        if(sacn_result1==null){
            return;
        }
        initTopBar();
        TextView tv_sacn_result = (TextView) findViewById(R.id.tv_sacn_result);
        tv_sacn_result.setText(sacn_result1);
    }
    public static void actionIntent(Context context, String sacn_result) {
        if (sacn_result == null) {
            return;
        }
        Intent intent = new Intent(context, SacnResultActivity.class);
        intent.putExtra("sacn_result", sacn_result);
        context.startActivity(intent);
    }
    private void initTopBar() {
        TextView titleContent = (TextView) findViewById(R.id.top_bar_content);
        Button next = (Button) findViewById(R.id.top_bar_next);
        next.setVisibility(View.GONE);
        titleContent.setText("详情");
        findViewById(R.id.top_back_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
