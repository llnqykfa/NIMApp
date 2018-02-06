package com.nzy.nim.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;

import java.util.regex.Pattern;

/**
 * @author quanyi
 * @time 下午9:19:28
 * @TODO TODO编辑用户信息界面
 * @project QuanYou
 */
public class EditUserInfoActivity extends BaseActivity implements
        OnClickListener {
    public static final int MODIFY_MARK_FLAG = 1;// 修改备注
    // 修改用户信息
    public static final int MODIFY_USER_NICKNAME_FLAG = 2;// 修改姓名
    // 修改用户个性签名
    public static final int MODIFY_USER_SIGNATURE_FLAG = 3;// 修改个性签名

    public static final int MODIFY_USER_SIGNATURE_FRAGMENT_FLAG = 4;// 修改个性签名
    // 编辑信息
    private EditText edtUserInfo;
    // 信息提示
    private TextView tvUserInfo;
    // 保存
    private Button btnSave;
    // 返回
    private ImageView btnBack;
    // 显示标题
    private TextView tvTitleContent;
    // 编辑内容
    private String editContent = "";
    private int flag;
    private UserInfo user;// 用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        editContent = getIntent().getStringExtra("edit_content");
        flag = getIntent().getIntExtra("flag", -1);
        initView();
        initEditContent();
    }

    /*
     * 初始化数据
     */
    private void initView() {
        edtUserInfo = (EditText) findViewById(R.id.edt_editUserInfo);
        tvUserInfo = (TextView) findViewById(R.id.tv_editUserInfo_note);
        btnSave = (Button) findViewById(R.id.top_bar_next);
        findViewById(R.id.top_back_bg).setOnClickListener(this);
        tvTitleContent = (TextView) findViewById(R.id.top_bar_content);
        btnSave.setText("保存");
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this);
        btnSave.setEnabled(false);


        user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
    }

    /**
     * @author LIUBO
     * @date 2015-3-28下午12:03:45
     * @TODO 初始化编辑内容
     */
    private void initEditContent() {
        edtUserInfo.setText(editContent);
        edtUserInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (editContent != null && editContent.equals(s)) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (flag == MODIFY_USER_NICKNAME_FLAG) {// 修改昵称
            tvTitleContent.setText("更改名字");
            edtUserInfo.setSingleLine();
            edtUserInfo.setMaxWidth(10);
            tvUserInfo.setText("好名字可以让你的朋友更容易记住你");
        } else if (flag == MODIFY_USER_SIGNATURE_FLAG || flag == MODIFY_USER_SIGNATURE_FRAGMENT_FLAG) {// 修改签名
            edtUserInfo.setMaxLines(2);
            edtUserInfo.setMaxWidth(30);
            tvTitleContent.setText("个性签名");
            tvUserInfo.setText("编辑属于自己的个性签名，让自己更有个性（30）");
        } else if (flag == MODIFY_MARK_FLAG) {// 设置备注
            tvTitleContent.setText("设置备注");
            edtUserInfo.setSingleLine(true);
        }

    }

    public void saveMsg(View view) {
        saveEditContent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_back_bg:// 返回
                finish();
                break;
            case R.id.top_bar_next:// 保存
                saveEditContent();
                break;
        }
    }

    /**
     * @author LIUBO
     * @date 2015-3-28下午12:14:06
     * @TODO 保存编辑后的内容
     */
    private void saveEditContent() {
        if (TextUtils.isEmpty(edtUserInfo.getText()))
            editContent = "";
        else
            editContent = edtUserInfo.getText().toString();
        if (flag == MODIFY_USER_NICKNAME_FLAG) {// 修改后的昵称
            if ("".equals(editContent)||editContent==null){
                    ToastUtil.showShort(this,"昵称不能为空");
                return;
            }
            String all  = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$";//{2,10}表示字符的长度是2-10
            Pattern pattern = Pattern.compile(all);
            boolean tf = pattern.matcher(editContent).matches();
            if(!tf){
                ToastUtil.showShort(this,"请输入合法名称，名称由中文、字母、数字组成");
                return;
            }
            resultBack("edit_nick", editContent, MyPersonalInfoActivity.class);
        }
        if (flag == MODIFY_USER_SIGNATURE_FLAG) {// 修改后的签名
            resultBack("edit_sign", editContent, MyPersonalInfoActivity.class);
        }
        if (flag == MODIFY_MARK_FLAG) {// 修改备注
            resultBack("edit_mark", editContent, FriendsInfoActivity.class);
        }

        if (flag == MODIFY_USER_SIGNATURE_FRAGMENT_FLAG) {
            HttpHelper.commitEditUserInfo(this, URLs.UPDATE_DESIGN_INFO, "designInfo",
                    editContent, new HttpHelper.OnSuccessListener() {
                        @Override
                        public void onFinish() {
                            user.setDesignInfo(editContent);
                            user.updateAll("personId=?", user.getPersonId());
                            finish();
                        }
                    });
        }
    }

    /**
     * @param key
     * @param value
     * @param clazz 要返回的页面
     * @author LIUBO
     * @date 2015-3-28下午12:16:19
     * @TODO 修改后返回结果
     */
    private <T> void resultBack(String key, String value, Class<T> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(key, value);
        this.setResult(RESULT_OK, intent);
        this.finish();

    }

    /**
     * @param context
     * @param editContent 编辑内容
     * @param requestCode 页面请求的标志
     * @author LIUBO
     * @date 2015-3-28上午11:40:28
     * @TODO 从其他页面跳转到用户信息编辑页面
     */
    public static void actionIntent(Context context, String editContent,
                                    int requestCode, int flag) {
        Intent intent = new Intent(context, EditUserInfoActivity.class);
        intent.putExtra("edit_content", editContent);
        intent.putExtra("flag", flag);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
