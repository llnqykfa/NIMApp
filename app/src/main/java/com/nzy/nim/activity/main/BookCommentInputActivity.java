package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * @TODO 发表界面
 */
public class BookCommentInputActivity extends BaseActivity {
    private String photoPath = "";
    private TextView title;
    private Button next;
    private EditText editText;
    private TextView countTv;
    private ProgressDialog PgDialog;
    private String isbn;
    private int COMMENT_SUCCESS = 0;
    private int COMMENT_FAILED = 1;
    private String bookReviewId;
    private String parentReplyId;
    private boolean isSending;
    private boolean iscomment;
    private boolean isFromRing;//是否来自组圈   组圈动态或留言回复
    private String noteId;//组圈动态或留言id
    private boolean isSucess=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        PgDialog = DialogHelper.getSDialog(BookCommentInputActivity.this, "发表评论中···", false);

        Intent intent = getIntent();
        isbn = intent.getStringExtra("isbn");
        bookReviewId = intent.getStringExtra("bookReviewId");
        noteId = intent.getStringExtra("noteId");
        parentReplyId = intent.getStringExtra("parentReplyId");
        iscomment = intent.getExtras().getBoolean("iscomment");
        if(DataUtil.isEmpty(iscomment)){
            return;
        }
        isFromRing = intent.getExtras().getBoolean("isFromRing");
        initView();
    }

    @Override
    public void finish() {
        setResult(1);
        super.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photoPath", photoPath);
    }

    public void initView() {
        editText = (EditText) findViewById(R.id.select_img_editContent);
        countTv = (TextView) findViewById(R.id.select_img_num_count);
        next = (Button) findViewById(R.id.top_bar_next);
        title = (TextView) findViewById(R.id.top_bar_content);
        if (iscomment) {
            title.setText("写评论");
        } else {
            title.setText("回复");
        }
        next.setText("发表");
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                countTv.setText(s.length() + "/3000");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSending) {
                    isSending = true;
                    PgDialog.show();
                    onSendComment();
                }

            }
        });
    }

    private void onSendComment() {

        String content = editText.getText().toString().trim();
        if (content.length() < 1) {
            PgDialog.dismiss();
            ToastUtil.showShort(BookCommentInputActivity.this, "内容不能为空");
            isSending = false;
            return;
        }
        if (content.length() > 3000) {
            PgDialog.dismiss();
            ToastUtil.showShort(BookCommentInputActivity.this, "超出字数限制！");
            isSending = false;
            return;
        }
        if (isFromRing) {
            addRingComment(content);//组圈动态留言回复 或 回复组圈动态留言的回复
        } else {
            //判断回复书评还是添加书评
            if (bookReviewId == null) {
                publishedComments(content);//添加书评
            } else {
                replyBookComments(content);//回复书评
            }
        }
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PgDialog.isShowing()) {
                    PgDialog.dismiss();
                }
            }
        }, 5000);

    }

    private void addRingComment(String content) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("noteId", noteId);
        hashMap.put("personId", QYApplication.getPersonId());
        hashMap.put("content", content);
        //可选，在直接回复书评的情况下不需要该参数
        if (parentReplyId != null) {
            hashMap.put("parentReplyId", parentReplyId);
        }
        HTTPUtils.postToken(BookCommentInputActivity.this, URLs.ADD_RINGTHEME_NOTEREPLY, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(COMMENT_FAILED);
            }

            @Override
            public void onResponse(String s) {
                if (s != null && !"".equals(s)) {
                    try {
                        JSONObject object = new JSONObject(s);
                        int errcode = object.getInt("errCode");
                        String errmsg = object.getString("errMsg");
                        handler.sendEmptyMessage(COMMENT_SUCCESS);
                        if (errcode == 0) {
                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        } else {
                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isSucess){
            String data="";
            FileOutputStream out=null;
            BufferedWriter writer=null;
            try{
                out=openFileOutput("data",Context.MODE_PRIVATE);
                writer=new BufferedWriter(new OutputStreamWriter(out));
                writer.write(data);
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(writer!=null){
                        writer.close();
                    }
                }catch(IOException e){}
            }
//            try{
//                OutputStream os=openFileOutput("file-bkcommentio.txt",Context.MODE_PRIVATE);
//                String str="";
//                os.write(str.getBytes("utf-8"));
//                os.close();
//            }catch(Exception e){
//
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!editText.getText().toString().trim().equals("")){
            String data=editText.getText().toString();
            FileOutputStream out=null;
            BufferedWriter writer=null;
            try{
                out=openFileOutput("data",Context.MODE_PRIVATE);
                writer=new BufferedWriter(new OutputStreamWriter(out));
                writer.write(data);
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(writer!=null){
                        writer.close();
                    }
                }catch(IOException e){}
            }
//            try{
//                OutputStream os=openFileOutput("file-bkcommentio.txt",Context.MODE_PRIVATE);
//                String str=editText.getText().toString();
//                os.write(str.getBytes("utf-8"));
//                os.close();
//            }catch(Exception e){
//
//            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FileInputStream in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                content.append(line);
            }
            editText.setText(content);
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
//        try {
//            InputStream is = openFileInput("file-bkcommentio.txt");
//            byte[] buffer = new byte[100];
//            int byteLength = is.read(buffer);
//            String str2 = new String(buffer, 0, byteLength, "utf-8");
//            editText.setText(str2.toString());
//            is.close();
//        }  catch (Exception e) {
//        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isSending = false;
//                    ToastUtil.showShort(BookCommentInputActivity.this, "评论成功！");
                    BookCommentInputActivity.this.finish();
                    if (isFromRing) {
                    }else{
                        Intent intent = new Intent();
                        intent.setAction("update_comment_count");
                        sendBroadcast(intent);
                    }
                    isSucess=true;
                    PgDialog.dismiss();
                    break;
                case 1:
                    isSending = false;
                    ToastUtil.showShort(BookCommentInputActivity.this, "评论失败！");
                    PgDialog.dismiss();
                    break;
            }
        }
    };


    /**
     * 添加书评
     */
    private void publishedComments(String content) {
        String s = isbn.replaceAll("-", "");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("isbn", s);
        hashMap.put("content", content);
        Log.e("content", content);
        HTTPUtils.postWithToken(BookCommentInputActivity.this, URLs.BOOK_COMMENT_POST, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(COMMENT_FAILED);
            }

            @Override
            public void onResponse(String s) {
                if (s != null && !"".equals(s)) {
                    try {
                        JSONObject object = new JSONObject(s);
                        int errcode = object.getInt("errcode");
                        String errmsg = object.getString("errmsg");
                        handler.sendEmptyMessage(COMMENT_SUCCESS);
                        if (errcode == 0) {
//                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        } else {
                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        }
                    } catch (Exception e) {
//
                    }
                }
            }
        });

    }

    /**
     * 添加书评回复
     */
    private void replyBookComments(String content) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("bookReviewId", bookReviewId);
        hashMap.put("personId", QYApplication.getPersonId());
        hashMap.put("content", content);
        //可选，在直接回复书评的情况下不需要该参数
        if (parentReplyId != null) {
            hashMap.put("parentReplyId", parentReplyId);
        }
        HTTPUtils.postWithToken(BookCommentInputActivity.this, URLs.BOOK_COMMENT_REPlY, hashMap, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(COMMENT_FAILED);
            }

            @Override
            public void onResponse(String s) {
                if (s != null && !"".equals(s)) {
                    try {
                        JSONObject object = new JSONObject(s);
                        int errcode = object.getInt("errcode");
                        String errmsg = object.getString("errmsg");
                        handler.sendEmptyMessage(COMMENT_SUCCESS);
                        if (errcode == 0) {
                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        } else {
                            ToastUtil.showShort(BookCommentInputActivity.this, errmsg);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        });

    }

    public static void actionIntent(Context context, String isbn) {
        Intent intent = new Intent(context, BookCommentInputActivity.class);
        intent.putExtra("isbn", isbn);
        context.startActivity(intent);
    }

    public static void actionIntent(Context context, String bookReviewId, String parentReplyId, boolean iscomment) {
        Intent intent = new Intent(context, BookCommentInputActivity.class);
        intent.putExtra("bookReviewId", bookReviewId);
        intent.putExtra("parentReplyId", parentReplyId);
        intent.putExtra("iscomment", iscomment);
        context.startActivity(intent);
    }

    public static void actionIntent(Context context, String noteId, String parentReplyId, boolean iscomment, int requestCode,boolean isFromRing) {
        Intent intent = new Intent(context, BookCommentInputActivity.class);
        intent.putExtra("noteId", noteId);
        intent.putExtra("parentReplyId", parentReplyId);
        intent.putExtra("iscomment", iscomment);
        intent.putExtra("isFromRing", isFromRing);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
