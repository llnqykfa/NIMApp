package com.nzy.nim.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.db.bean.ChatRecord;
import com.nzy.nim.db.bean.ShareMsgInfo;
import com.nzy.nim.vo.MsgManager;

/**
 * @author liu
 * @聊天界面的消息的删除,分享，复制
 */
public class ContextMenu extends BaseActivity implements OnItemClickListener {
    private String title;
    private int position;
    private int type;
    private TextView titleTv;
    private ListView menuLv;
    private ArrayAdapter<String> adapter;
    String[] menus;
    private Vibrator vibrator;
    private ChatRecord entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_menu);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        title = getIntent().getStringExtra("title");
        position = getIntent().getIntExtra("position", -1);
        entity = (ChatRecord) getIntent().getSerializableExtra("CHATE_RECORD");
        type = entity.getMsgType();
        long[] pattern = {1, 10}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1); // 重复两次上面的pattern如果只想震动一次，index设为-1
        initAdapter(type);
        initView();
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.context_menu_title);
        menuLv = (ListView) findViewById(R.id.context_menu_lv);
        titleTv.setText(title);
        menuLv.setAdapter(adapter);
        menuLv.setOnItemClickListener(this);
    }

    private void initAdapter(int type) {
        switch (type) {
            case MsgManager.TEXT_TYPE:
                menus = new String[]{"复制", "转发", "删除"};
                break;
            case MsgManager.PICTURE_TYPE:
            case MsgManager.FILE_TYPE:
            case MsgManager.FILE_TXT_TYPE:
                menus = new String[]{"转发","保存到本地","删除"};
                break;
            case MsgManager.VOICE_TYPE:
                menus = new String[]{"删除"};
                break;
            case MsgManager.SHARE_TYPE:
                menus = new String[]{"打开","转发", "删除"};
                break;
        }
        adapter = new ArrayAdapter<String>(this, R.layout.listview_item_1,
                R.id.listview_item_tv, menus);
    }

    /**
     * 跳转到菜单页面
     *
     * @param activity
     * @param title
     * @param position
     * @param requestCode
     */
    public static void actionIntent(Activity activity, String title, ChatRecord entity,
                                    int position, int requestCode) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("CHATE_RECORD", entity);
        Intent intent = new Intent(activity, ContextMenu.class);
        intent.putExtra("title", title);
        intent.putExtra("position", position);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String str = menus[position];
        if ("复制".equals(str)) {
            copy();
        } else if ("转发".equals(str)) {
            forward();
        } else if ("删除".equals(str)) {
            delete();
        } else if ("保存".equals(str)) {
            savepic();
        } else if ("保存到本地".equals(str)) {
            downLoadPic();
        } else if ("打开".equals(str)) {
            ShareMsgInfo shareMsgInfo = new Gson().fromJson(entity.getContent(), ShareMsgInfo.class);
            Uri uri = Uri.parse(shareMsgInfo.getActionUri());
            ContextMenu.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            finish();
        }

    }

    private void savepic() {//RESULT_CODE_SAVEPIC
        setResult(ChatActivity.RESULT_CODE_SAVEPIC,
                new Intent().putExtra("position", position));
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setResult(RESULT_OK, null);
        finish();
        return true;
    }

    private void copy() {
        setResult(ChatActivity.RESULT_CODE_COPY,
                new Intent().putExtra("position", position));
        finish();
    }

    private void delete() {
        setResult(ChatActivity.RESULT_CODE_DELETE,
                new Intent().putExtra("position", position));
        finish();
    }

    private void forward() {
        setResult(ChatActivity.RESULT_CODE_FORWARD,
                new Intent().putExtra("position", position));
        finish();
    }
    private void downLoadPic() {
        setResult(ChatActivity.RESULT_CODE_DWONLOAD_PIC,
                new Intent().putExtra("position", position));
        finish();
    }
}
