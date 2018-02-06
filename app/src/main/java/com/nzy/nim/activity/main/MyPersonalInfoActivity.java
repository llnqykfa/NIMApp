package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.base.ClipPicActivity;
import com.nzy.nim.activity.base.ShowBigImageActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.http.HttpHelper;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.RoundImageView;
import com.nzy.nim.view.SelectedPicPopupWindow;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;
import com.nzy.nim.zxing.EncodingHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @classify(类别) 界面
 * @TODO(功能) TODO 用户个人信息中心
 * @Param(参数)
 * @Remark(备注)
 */
public class MyPersonalInfoActivity extends BaseActivity implements
        OnClickListener, SelectedPicPopupWindow.OnChoosePicListener {
    final int CUT_PIC_RESULT = 4;
    final int OPEN_CAMERA = 1;
    final int REQUEST_CODE_LOCAL = 2;
    final int MODIFY_USER_NICKNAME = 3;
    final int MODIFY_USER_SIGNATURE = 5;

    private Button btnOther;// 确认
    private TextView tvTitleContent;// 内容
    /**
     * 用户头像
     */
    private RelativeLayout headContainer;// 头像布局容器
    private RoundImageView userHeadImage;
    /**
     * 用户名
     */
    private RelativeLayout rlEditUserName;
    private TextView tvUserName;
    /**
     * 用户性别
     */
    private RelativeLayout rlEditUserSex;
    private TextView tvUserSex;
    /**
     * 个性签名
     */
    private RelativeLayout rlEditSignature;
    private TextView tvUserSignature;
    // 显示数据加载的Dialog
    private ProgressDialog pgUtil;
    private UserInfo user;// 用户信息
    private String photoPath;// 拍照的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypersonal_center);
        QYApplication.ACTIVITY_NAME="个人信息详情";
        if (savedInstanceState != null) {
            photoPath = savedInstanceState.getString("photoPath_flag");
        }
        initTopBar();
        initView();
        getUserInfo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存拍照后的路径，防止屏幕旋转数据丢失
        outState.putString("photoPath_flag", photoPath);
    }

    /**
     * @param user
     * @author LIUBO
     * @date 2015-3-28上午11:18:56
     * @TODO 初始化用户的基本信息
     */
    private void initUserData(UserInfo user) {
        if (user.getPhotoPath() != null)
            // 显示头像
            ImageUtil.displayHeadImg(user.getPhotoPath(), userHeadImage);
        else
            userHeadImage.setImageResource(R.drawable.pic_default_head);
        // 判断用户名对否为空
        if (user.getUserName() != null)
            tvUserName.setText(user.getUserName());
        else
            tvUserName.setText("");
        if (user.getDesignInfo() != null)
            tvUserSignature.setText(user.getDesignInfo());
        else
            tvUserSignature.setText("暂无最新签名!");
        if (user.getSex()) {
            tvUserSex.setText("男");
        } else {
            tvUserSex.setText("女");
        }
    }

    /**
     获取用户信息
     */
    private void getUserInfo() {
        user = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
        if (user == null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("targetPersonId", QYApplication.getPersonId());
            HTTPUtils.postWithToken(MyPersonalInfoActivity.this, URLs.GET_PERSON_INFO, map, new VolleyListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ToastUtil.showShort(MyPersonalInfoActivity.this, R.string.server_is_busy);
                }

                @Override
                public void onResponse(String s) {
                    Log.e("UserInfo", s);
                    try {
                        UserInfo userInfo = new Gson().fromJson(new JSONObject(s).getString("person"), UserInfo.class);
                        userInfo.saveThrows();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            initUserData(user);
        }
    }

    /**
      标题栏
     */

    private void initTopBar() {
        btnOther = (Button) findViewById(R.id.top_bar_next);
        tvTitleContent = (TextView) findViewById(R.id.top_bar_content);
        btnOther.setVisibility(View.GONE);
        tvTitleContent.setText("我的信息");
        findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        headContainer = (RelativeLayout) findViewById(R.id.activity_mypersonal_head_img_container);
        RelativeLayout rl_layout_barCode = (RelativeLayout) findViewById(R.id.rl_layout_barCode);
        userHeadImage = (RoundImageView) findViewById(R.id.activity_mypersonal_center_img);
        rlEditUserName = (RelativeLayout) findViewById(R.id.rl_layout_username);
        tvUserName = (TextView) findViewById(R.id.activity_personal_username);
        rlEditUserSex = (RelativeLayout) findViewById(R.id.rl_layout_usersex);
        tvUserSex = (TextView) findViewById(R.id.activity_personal_usersex);
        rlEditSignature = (RelativeLayout) findViewById(R.id.rl_layout_signature);
        tvUserSignature = (TextView) findViewById(R.id.activity_personal_design);
        headContainer.setOnClickListener(this);
        userHeadImage.setOnClickListener(this);
        rlEditUserName.setOnClickListener(this);
        rlEditUserSex.setOnClickListener(this);
        rlEditSignature.setOnClickListener(this);
        rl_layout_barCode.setOnClickListener(this);
        pgUtil = DialogHelper.getSDialog(MyPersonalInfoActivity.this, "更新中···", false);
    }

    /**
     * @author quanyi
     * @date 2015-3-11上午11:43:58
     * @TODO 异步上传图片并更新数据
     */
    private void uploadImgFile(final String path) {
        if (DataUtil.isEmpty(path)) {
            ToastUtil.show(this, "头像上传失败！", Gravity.TOP);
            return;
        }
        pgUtil.show();
        new Thread() {
            public void run() {
                HttpUtil.upLoadFiles(Arrays.asList(new String[]{path}),
                        MyConstants.USER_FLODER_TYPE, new HttpUtil.OnPostListener() {

                            @SuppressWarnings("unchecked")
                            @Override
                            public void onSuccess(String jsonData) {
                                try {
                                    List<String> names = JSON.parseObject(
                                            jsonData, List.class);
                                    if (!DataUtil.isEmpty(names))
                                        updateFile(QYApplication.getPersonId(),
                                                names.get(0), path);
                                } catch (Exception e) {
                                    onFailure();
                                }

                            }

                            @Override
                            public void onFailure() {
                                handler.sendEmptyMessage(UPDATE_FAILE);
                            }
                        });
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_mypersonal_head_img_container:
                photoPath = MyConstants.BASE_DIR + "/"
                        + Calendar.getInstance().getTimeInMillis() + ".jpg";
                new SelectedPicPopupWindow(this, userHeadImage)
                        .setOnChoosePicListener(this);
                break;
            // 点击选择头像
            case R.id.activity_mypersonal_center_img:
                ShowBigImageActivity.actionIntent(this, user.getPhotoPath());
                break;
            // 选择性别
            case R.id.rl_layout_usersex:
                if (TextUtils.isEmpty(tvUserSex.getText())
                        || tvUserSex.getText().toString().equals("男"))
                    showChoseSexDialog(0);
                else
                    showChoseSexDialog(1);
                break;
            // 修改用户昵称
            case R.id.rl_layout_username:
                EditUserInfoActivity.actionIntent(this, user.getUserName(),
                        MODIFY_USER_NICKNAME,
                        EditUserInfoActivity.MODIFY_USER_NICKNAME_FLAG);
                break;
            // 修改个性签名
            case R.id.rl_layout_signature:
                EditUserInfoActivity.actionIntent(this, user.getDesignInfo(),
                        MODIFY_USER_SIGNATURE,
                        EditUserInfoActivity.MODIFY_USER_SIGNATURE_FLAG);
                break;
            //二维码
            case R.id.rl_layout_barCode:
                createBarCode();
                break;

        }
    }

    private void createBarCode() {
        View view = getLayoutInflater().inflate(R.layout.dialog_bar_code, null);
        ImageView img_barCode = (ImageView) view.findViewById(R.id.img_barCode);
        try {
            String contentString = QYApplication.getPersonId();
            if (contentString != null && contentString.trim().length() > 0) {
                //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode("QYID" + contentString, 300);
                img_barCode.setImageBitmap(qrCodeBitmap);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(MyPersonalInfoActivity.this).setView(view).show();
    }

    /**
     * @author LIUBO
     * @date 2015-3-28上午11:21:58
     * @TODO 性别选择对话框
     */
    private void showChoseSexDialog(int checkedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MyPersonalInfoActivity.this);
        builder.setTitle("性别");
        builder.setSingleChoiceItems(new String[]{"男", "女"}, checkedItem,
                new DialogInterface.OnClickListener() {
                    private String value = "";

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                tvUserSex.setText("男");
                                value = "true";
                                break;
                            case 1:
                                tvUserSex.setText("女");
                                value = "false";
                                break;
                        }
                        // 向服务器提交数据
                        HttpHelper.commitEditUserInfo(MyPersonalInfoActivity.this,URLs.UPDATE_SEX, "sex",
                                value, new HttpHelper.OnSuccessListener() {
                                    @Override
                                    public void onFinish() {
                                        if (value.equals("true"))
                                            user.setSex(true);
                                        else
                                            user.setSex(false);
                                        user.updateAll("personId=?",
                                                user.getPersonId());
                                    }
                                });
                        dialog.dismiss();
                    }
                }).create().show();
    }

    /**
     * 对返回的照片进行处理，然后进行上传的操作
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case OPEN_CAMERA:// 打开相机拍照并显示
                ClipPicActivity.actionIntent(this, photoPath, CUT_PIC_RESULT, true,
                        50);
                break;
            case REQUEST_CODE_LOCAL:// 从本地选择图片
                if (null != data) {
                    ClipPicActivity.actionIntent(this,
                            ImageUtil.getLocalImgPath(this, data.getData()),
                            CUT_PIC_RESULT, true, 50);
                }
                break;
            case CUT_PIC_RESULT:// 上传裁剪后的图片
                if (null != data
                        && !TextUtils.isEmpty(data.getStringExtra("cut_pic"))) {
                    uploadImgFile(data.getStringExtra("cut_pic"));
                }
                break;
            // 获得修改后的昵称
            case MODIFY_USER_NICKNAME:
                if (null != data) {
                    final String newNick = data.getStringExtra("edit_nick")
                            .toString().trim();
                    tvUserName.setText(newNick);
                    HttpHelper.commitEditUserInfo(this,URLs.UPDATE_USER_NAME, "userName",
                            newNick, new HttpHelper.OnSuccessListener() {
                                @Override
                                public void onFinish() {
                                    user.setUserName(newNick);
                                    user.updateAll("personId=?", user.getPersonId());
                                    sendRefresh();
                                }
                            });
                }
                break;
            // 得到修改后的用户个性签名
            case MODIFY_USER_SIGNATURE:
                if (null != data) {
                    final String newSign = data.getStringExtra("edit_sign")
                            .toString().trim();
                    tvUserSignature.setText(newSign);
                    HttpHelper.commitEditUserInfo(this, URLs.UPDATE_DESIGN_INFO, "designInfo",
                            newSign, new HttpHelper.OnSuccessListener() {
                                @Override
                                public void onFinish() {
                                    user.setDesignInfo(newSign);
                                    user.updateAll("personId=?", user.getPersonId());
                                    sendRefresh();
                                }
                            });
                }
                break;
            case 10:
                getUserInfo();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void actionIntent(Context context) {
        context.startActivity(new Intent(context, MyPersonalInfoActivity.class));
    }

    /**
     * @param personId
     * @param fileName
     * @author quanyi
     * @date 2015-3-11上午11:34:57
     * @TODO 修改图片后更新图片
     */
    public void updateFile(String personId, final String fileName,
                           final String localPath) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("photo","USER/" + fileName);
        HTTPUtils.postWithToken(MyPersonalInfoActivity.this, URLs.UPDATE_PHOTO, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                FileUtils.deleteFile(localPath);
                handler.sendEmptyMessage(UPDATE_FAILE);
            }

            @Override
            public void onResponse(String s) {
                user.setPhotoPath("/USER/" + fileName);
                user.updateAll("personId=?", user.getPersonId());
                FileUtils.deleteFile(localPath);
                handler.sendEmptyMessage(UPDATE_SUCCESS);
            }
        });
    }

    final int UPDATE_FAILE = 0;// 更新失败
    final int UPDATE_SUCCESS = 1;// 更新成功
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            pgUtil.dismiss();
            switch (msg.what) {
                case UPDATE_FAILE:
                    ToastUtil.show(MyPersonalInfoActivity.this, "头像上传失败！！！",
                            Gravity.TOP);
                    break;
                case UPDATE_SUCCESS:
                    ToastUtil.show(MyPersonalInfoActivity.this, "头像更新成功！",
                            Gravity.TOP);
                    ImageUtil.displayHeadImg(user.getPhotoPath(), userHeadImage);
                    sendRefresh();
                    break;
            }
        }

        ;
    };

    private void sendRefresh(){
        Intent intent = new Intent("update_myself_fragment");
        sendBroadcast(intent);
    }

    @Override
    public void onTakePic() {
        startActivityForResult(ImageUtil.getTakePicIntent(photoPath),
                OPEN_CAMERA);
    }

    @Override
    public void onSelectFromLocal() {
        startActivityForResult(ImageUtil.getSelectPicIntent(),
                REQUEST_CODE_LOCAL);
    }

}
