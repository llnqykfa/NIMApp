package com.nzy.nim.activity.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.main.AppStartActivity;
import com.nzy.nim.activity.main.RegisterActivity;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DensityUtil;
import com.nzy.nim.tool.common.NetUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.ClearEditText;
import com.nzy.nim.vo.QYApplication;
import com.ta.utdid2.android.utils.StringUtils;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/11/23.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static LoginActivity loginActivity = null;

    private ImageView topBarBack;
    private TextView topBarTitle;
    private Button topBarNext;

    private Button loginBtn;
    public static ProgressDialog pgDialog;
    private ClearEditText per_mobile_txt;
    private ClearEditText per_passwd_txt;
    private boolean isFirst = true;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        loginActivity = this;
        if (AppStartActivity.appStartActivity != null)
            AppStartActivity.appStartActivity.finish();
        initUI();
        pgDialog = DialogHelper.getSDialog(LoginActivity.this, "登录中···", false);
        initLogin();
    }

    private void initUI() {
        topBarTitle = (TextView)findViewById(R.id.top_bar_content);
        topBarTitle.setText("登录");
        topBarBack = (ImageView)findViewById(R.id.top_bar_back);
        topBarBack.setVisibility(View.GONE);
        topBarNext = (Button)findViewById(R.id.top_bar_next);
        topBarNext.setVisibility(View.GONE);
        per_mobile_txt = (ClearEditText) findViewById(R.id.per_mobile_txt);
        per_passwd_txt = (ClearEditText) findViewById(R.id.per_passwd_txt);
        loginBtn = (Button)findViewById(R.id.login_btn);
        findViewById(R.id.ent_reg_link).setOnClickListener(this);
        findViewById(R.id.find_passwd_link).setOnClickListener(this);
        setClearEditTextListener();
        loginBtn.setOnClickListener(this);
    }

    private void initLogin() {
        sp = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        HashSet<String> spLoginSet = (HashSet<String>) sp.getStringSet("userName", new HashSet<String>());
        if (spLoginSet!=null && spLoginSet.size()==1) {
            Iterator<String> iterator=spLoginSet.iterator();
            while(iterator.hasNext()){
                per_mobile_txt.setText(iterator.next());
            }
        }
    }

    private void setClearEditTextListener() {
        per_mobile_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    per_passwd_txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
//					 showUserHead(userHeadImg);
                }
            }
        });

        per_mobile_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isFirst) {
                        per_mobile_txt.setClearIconVisible(false);
                        isFirst = false;
                    } else {
                        per_mobile_txt.setClearIconVisible(per_mobile_txt.getText()
                                .length() > 0);
                    }
                } else {
                    per_mobile_txt.setClearIconVisible(false);
                }
            }
        });
    }
    /**
     * @param context 是否是第一次登陆标志
     * @TODO TODO 从其他界面跳转到登陆界面
     * @Return void
     */
    public static void actionIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                String userName = per_mobile_txt.getText().toString();
                String password = per_passwd_txt.getText().toString();
                if (NetUtil.checkNetwork(this)) {
                    doLogin(userName, password);
                } else {
                    ToastUtil.show(this, "无法连接到网络，请检查网络设置", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.ent_reg_link:
                QYApplication.getInstance().put("isInSchool", true);
                RegisterActivity.actionIntent(loginActivity, true);
                break;
            case R.id.find_passwd_link:
                RegisterActivity.actionIntent(loginActivity, false);
                break;
        }
    }
    /**
     * @param userName
     * @param password
     * @return
     * @TODO TODO 验证登陆信息
     * @Return boolean
     */
    private boolean checkLogin(Context context, String userName, String password) {
        if (userName.equals("")) {
            ToastUtil.showLong(context, R.string.username_is_empty);
            return false;
        }
        if (password.equals("")) {
            ToastUtil.showLong(context, R.string.pwd_is_empty);
            return false;
        }
        if (!DensityUtil.isPassWord(password)) {
            ToastUtil.showLong(context, "请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成");
            return false;
        }
        if (password.length() < 6 || password.length() > 15) {
            ToastUtil.showLong(context, "请输入合法密码，由6-15位字母（区分大小写）、数字、符号组成");
            return false;
        }
        return true;
    }
    private void doLogin(String userName, String password) {
        // 验证登陆信息
        if (checkLogin(this, userName, password)) {
            pgDialog.show();
            String pwd2 = StringUtil.getMD5ofStr(password);
            new HttpHelper().login(loginActivity, userName, pwd2);
        }
    }
}
