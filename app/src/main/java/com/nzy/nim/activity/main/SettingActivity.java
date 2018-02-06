package com.nzy.nim.activity.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.tmpbean.AppUpdateInfo;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.tool.common.AudioRecorder;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.tool.common.UploadUtil;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingActivity extends BaseActivity implements OnClickListener {
    // 退出
    private Button btnExit;
    // 是否接受新消息
    private ImageView ivReceiverMsg;
    // 打开声音
    private ImageView ivOpenSound;
    // 震动
    private ImageView ivOpenVibrate;
    private RelativeLayout soundContainer;
    private RelativeLayout vibratorContainer;
    private boolean notifyFlag;
    private boolean vibratorFlag;
    private boolean soundFlag;

    private Button button3;
    private static int RECORD_NO = 0;  //不在录音
    private static int RECORD_ING = 1;   //正在录音
    private static int RECODE_ED = 2;   //完成录音

    private static int RECODE_STATE = 0;      //录音的状态
    private String date = "";
    private Dialog dialog;
    private AudioRecorder mr;
    private MediaPlayer mediaPlayer;
    private static float recodeTime = 0.0f;    //录音的时间
    private static double voiceValue = 0.0;    //麦克风获取的音量值
    private static int MAX_TIME = 0;    //最长录制时间，单位秒，0为无时间限制
    private static int MIX_TIME = 1;     //最短录制时间，单位秒，0为无时间限制，建议设为1
    private Thread recordThread;
    private ImageView dialog_img;
    private String uploadMessage;
    private String voicePath;
    UploadUtil uploadUtil = UploadUtil.getInstance();
    private ImageView up_img;
    private TextView up_tv;
    private AppUpdateInfo appUpdateInfo=new AppUpdateInfo();
    private int upda=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        QYApplication.ACTIVITY_NAME="设置";
        notifyFlag = SPHelper.getNewMsgFlagNotify(QYApplication.getPersonId());
        vibratorFlag = SPHelper.getNewMsgVibratorFlag(QYApplication.getPersonId());
        soundFlag = SPHelper.getNewMsgComVoiceFlag(QYApplication.getPersonId());
        initTopBar();
        initViews();
        setUpView();
        checkUpdate();
    }

    private void setUpView() {
        if (notifyFlag) {
            ivReceiverMsg.setImageResource(R.drawable.ic_switch_open);
        } else {
            ivReceiverMsg.setImageResource(R.drawable.ic_switch_close);
            soundContainer.setVisibility(View.GONE);
            vibratorContainer.setVisibility(View.GONE);
        }
        if (soundFlag) {
            ivOpenSound.setImageResource(R.drawable.ic_switch_open);
        } else {
            ivOpenSound.setImageResource(R.drawable.ic_switch_close);
        }
        if (vibratorFlag) {
            ivOpenVibrate.setImageResource(R.drawable.ic_switch_open);
        } else {
            ivOpenVibrate.setImageResource(R.drawable.ic_switch_close);
        }
        if (soundFlag == false && vibratorFlag == false) {
            notifyFlag = false;
            ivReceiverMsg.setImageResource(R.drawable.ic_switch_close);
            soundContainer.setVisibility(View.GONE);
            vibratorContainer.setVisibility(View.GONE);
            SPHelper.setNewMsgFlagNotify(QYApplication.getPersonId(), false);
        }
        if (soundFlag == true && vibratorFlag == true) {
            notifyFlag = true;
            ivReceiverMsg.setImageResource(R.drawable.ic_switch_open);
            soundContainer.setVisibility(View.VISIBLE);
            vibratorContainer.setVisibility(View.VISIBLE);
            SPHelper.setNewMsgFlagNotify(QYApplication.getPersonId(), true);
        }
    }

    private void initTopBar() {
        TextView tvContent = (TextView) findViewById(R.id.top_bar_content);
        Button btnNext = (Button) findViewById(R.id.top_bar_next);
        findViewById(R.id.top_back_bg).setOnClickListener(this);
        tvContent.setText("设置");
        btnNext.setVisibility(View.INVISIBLE);
    }

    /**
     * @author quanyi
     * @date 2015-3-27上午10:10:54
     * @TODO 绑定控件Id
     */
    private void initViews() {
        up_img = (ImageView) findViewById(R.id.up_img);
        up_tv = (TextView) findViewById(R.id.up_tv);
        findViewById(R.id.setting_btn_personal_about_qy);
        btnExit = (Button) findViewById(R.id.setting_btn_personal_exit);
        ivReceiverMsg = (ImageView) findViewById(R.id.setting_iv_receiverMsg);
        ivOpenSound = (ImageView) findViewById(R.id.setting_iv_sound);
        ivOpenVibrate = (ImageView) findViewById(R.id.setting_iv_vibrator);
        soundContainer = (RelativeLayout) findViewById(R.id.setting_rl_sound_container);
        vibratorContainer = (RelativeLayout) findViewById(R.id.setting_rl_vibrator_container);
        findViewById(R.id.setting_btn_personal_change_password).setOnClickListener(this);
        findViewById(R.id.rel_up).setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        ivReceiverMsg.setOnClickListener(this);
        ivOpenSound.setOnClickListener(this);
        ivOpenVibrate.setOnClickListener(this);
        btnExit.setOnClickListener(this);


        //录音
        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (RECODE_STATE != RECORD_ING) {
//						scanOldFile();
                            date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                            mr = new AudioRecorder(date);
                            RECODE_STATE = RECORD_ING;
                            showVoiceDialog();
                            try {
                                mr.start();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mythread();
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        if (RECODE_STATE == RECORD_ING) {
                            RECODE_STATE = RECODE_ED;
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            try {
                                mr.stop();
                                voiceValue = 0.0;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            voiceValue = 0.0;
                            if (recodeTime < MIX_TIME) {
                                showWarnToast();
                                RECODE_STATE = RECORD_NO;
                                File file = new File(Environment
                                        .getExternalStorageDirectory(), "my/" + date + ".amr");
                                file.delete();
                            } else {
                                Map<String, String> param = new HashMap<String, String>();
                                param.put("voice", getAmrPath());
                                uploadUtil.uploadFile(new File(getAmrPath()), "voice", URLs.SEND_MY_SCHOOL_VOICE, null);
                                uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {
                                    @Override
                                    public void onUploadDone(int responseCode, String message) {
                                        uploadMessage = message;
//					// 上传完图像的处理
                                        mHandler.sendEmptyMessage(1);

                                    }

                                    @Override
                                    public void onUploadProcess(int uploadSize) {

                                    }

                                    @Override
                                    public void initUpload(int fileSize) {

                                    }
                                });
                            }
                        }

                        break;
                }
                return false;
            }
        });
    }

    /**
     * @TODO 检查更新
     */
    private void checkUpdate() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("terminal", "10");
        map.put("schoolCode","public");
        Log.e("update","update="+URLs.UPDATE+map);
        HTTPUtils.post(SettingActivity.this, URLs.UPDATE, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                if(s!=null){
                    checkVersion(s);
//                checkVersion("{\"message\":\"尚未查询匹配的版本信息\",\"source\":null,\"data\":{\"sysVer\":\"46\",\"recordVer\":1,\"terminal\":\"01\",\"recordTimeStamp\":\"2016-07-19 15:40:30\",\"downloadUrl\":\"http://120.27.159.62:8000/app/release-v1.0.10.apk\",\"schoolCode\":\"10402\",\"verName\":\"1.0.11\",\"fixDescripe\":\"修复bug\",\"bizId\":\"101100000000000000000000000003\"},\"code\":\"1\",\"type\":0}");
                }
            }
        });
    }

    /**
     * @author 刘波
     * @date 2015-3-4下午5:18:21
     * @todo 检测服务器上的最新的软件版本信息
     */
    private void checkVersion(String jsonData) {
//        if (DataUtil.isEmpty(jsonData))
//            return;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");
            if(code ==1){
                Gson gson=new Gson();
                appUpdateInfo = gson.fromJson(data, AppUpdateInfo.class);
                init(appUpdateInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void init(AppUpdateInfo info) {
        if(isUpdate(info)){
            upda=1;
            up_img.setVisibility(View.VISIBLE);
            up_tv.setVisibility(View.VISIBLE);
        }
    }
    private PackageInfo packageInfo;// 当前包的信息
    // app的包名
    private static final String packageName = "com.nzy.nim";
    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private boolean isUpdate(AppUpdateInfo info) {
        try {
            packageInfo = getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (DataUtil.isEmpty(info))
            return false;
        // 获取当前软件版本
        int versionCode = packageInfo.versionCode;
        // 获取服务器上的软件版本
        int serviceCode = Integer.valueOf(info.getSysVer());
        // 版本判断
        if (serviceCode > versionCode) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn_personal_exit:
                Intent intent = new Intent();
                intent.setClass(this, ExitActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_iv_receiverMsg:
                if (notifyFlag) {
                    notifyFlag = false;
                    soundFlag = false;
                    vibratorFlag = false;
                    ivReceiverMsg.setImageResource(R.drawable.ic_switch_close);
                    soundContainer.setVisibility(View.GONE);
                    vibratorContainer.setVisibility(View.GONE);
                    SPHelper.setNewMsgFlagNotify(QYApplication.getPersonId(), false);
                    ivOpenSound.setImageResource(R.drawable.ic_switch_close);
                    SPHelper.setNewMsgComVoiceFlag(QYApplication.getPersonId(), false);
                    ivOpenVibrate.setImageResource(R.drawable.ic_switch_close);
                    SPHelper.setNewMsgVibratorFlag(QYApplication.getPersonId(), false);
                } else {
                    notifyFlag = true;
                    soundFlag = true;
                    vibratorFlag = true;
                    ivReceiverMsg.setImageResource(R.drawable.ic_switch_open);
                    soundContainer.setVisibility(View.VISIBLE);
                    vibratorContainer.setVisibility(View.VISIBLE);
                    SPHelper.setNewMsgFlagNotify(QYApplication.getPersonId(), true);
                    ivOpenSound.setImageResource(R.drawable.ic_switch_open);
                    SPHelper.setNewMsgComVoiceFlag(QYApplication.getPersonId(), true);
                    ivOpenVibrate.setImageResource(R.drawable.ic_switch_open);
                    SPHelper.setNewMsgVibratorFlag(QYApplication.getPersonId(), true);
                }
                break;
            case R.id.setting_iv_sound:
                if (soundFlag) {
                    soundFlag = false;
                    ivOpenSound.setImageResource(R.drawable.ic_switch_close);
                    SPHelper.setNewMsgComVoiceFlag(QYApplication.getPersonId(), false);
                } else {
                    soundFlag = true;
                    ivOpenSound.setImageResource(R.drawable.ic_switch_open);
                    SPHelper.setNewMsgComVoiceFlag(QYApplication.getPersonId(), true);
                }
                break;
            case R.id.setting_iv_vibrator:
                if (vibratorFlag) {
                    vibratorFlag = false;
                    ivOpenVibrate.setImageResource(R.drawable.ic_switch_close);
                    SPHelper.setNewMsgVibratorFlag(QYApplication.getPersonId(), false);
                } else {
                    vibratorFlag = true;
                    ivOpenVibrate.setImageResource(R.drawable.ic_switch_open);
                    SPHelper.setNewMsgVibratorFlag(QYApplication.getPersonId(), true);
                }
                break;
            case R.id.top_back_bg:
                finish();
                break;
            case R.id.rel_up:
                AboutAppActivity.actionIntent(this,upda,appUpdateInfo);
                break;
            case R.id.setting_btn_personal_change_password:
                startActivity(new Intent(this, UpdatePwdActivity.class));
                break;
        }
    }

    /**
     * @param context
     * @author quanyi
     * @date 2015-3-27下午3:35:00
     * @TODO TODO跳转到自身
     */
    public static void actionIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public void play(View view) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("http://218.85.133.208:8080/resources/voices/feed/ada1128a0ecf48718e28e279f23dea39.amr");
            mediaPlayer.prepare();
            mediaPlayer.start();


            //设置播放结束时监听
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    ToastUtil.showShort(SettingActivity.this, "播放完成");
                }
            });
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void send(View view) {

    }


    //获取文件手机路径
    private String getAmrPath() {
        File file = new File(Environment
                .getExternalStorageDirectory(), "my/" + date + ".amr");
        return file.getAbsolutePath();
    }


    //获取文件
    private File getFilePath() {
        File file = new File(Environment
                .getExternalStorageDirectory(), "my/" + date + ".amr");
        return file;
    }

    //录音计时线程
    private void mythread() {
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }


    //录音线程
    private Runnable ImgThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (RECODE_STATE == RECORD_ING) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (RECODE_STATE == RECORD_ING) {
                            voiceValue = mr.getAmplitude();
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Handler imgHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 0:
                        //录音超过15秒自动停止
                        if (RECODE_STATE == RECORD_ING) {
                            RECODE_STATE = RECODE_ED;
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            try {
                                mr.stop();
                                voiceValue = 0.0;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (recodeTime < 1.0) {
                                showWarnToast();
                                button3.setText("按住开始录音");
                                RECODE_STATE = RECORD_NO;
                            } else {
                                button3.setText("录音完成!点击重新录音");

                            }
                        }
                        break;
                    case 1:
                        setDialogImage();
                        break;
                    default:
                        break;
                }

            }
        };
    };

    //录音Dialog图片随声音大小切换
    private void setDialogImage() {
        if (voiceValue < 200.0) {
            dialog_img.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 200.0 && voiceValue < 400) {
            dialog_img.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 400.0 && voiceValue < 800) {
            dialog_img.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 800.0 && voiceValue < 1600) {
            dialog_img.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1600.0 && voiceValue < 3200) {
            dialog_img.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 3200.0 && voiceValue < 5000) {
            dialog_img.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 5000.0 && voiceValue < 7000) {
            dialog_img.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 28000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_14);
        }
    }

    //录音时显示Dialog
    private void showVoiceDialog() {
        dialog = new Dialog(SettingActivity.this, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.my_dialog);
        dialog_img = (ImageView) dialog.findViewById(R.id.dialog_img);
        dialog.show();
    }

    //录音时间太短时Toast显示
    private void showWarnToast() {
        Toast toast = new Toast(SettingActivity.this);
        LinearLayout linearLayout = new LinearLayout(SettingActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(SettingActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short); // 图标

        TextView mTv = new TextView(SettingActivity.this);
        mTv.setText("时间太短   录音失败");
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);//字体颜色
        //mTv.setPadding(0, 10, 0, 0);

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);//内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);//设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);//起点位置为中间     100为向下移100dp
        toast.show();
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 上传完的处理
                case 1:
                    handleUpload();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void handleUpload() {
        if (!isNullOrEmpty(uploadMessage)) {

            try {
                JSONObject object = new JSONObject(uploadMessage);
                String errcode = object.getString("errcode");
                if ("0".equals(errcode)) {
                    ToastUtil.showShort(SettingActivity.this, object.getString("errmsg"));

                    voicePath = object.getString("voicePath");

                } else {

                    ToastUtil.showShort(SettingActivity.this, object.getString("errmsg"));

                }

            } catch (Exception e) {
            }

        } else {
            ToastUtil.showShort(SettingActivity.this, "上传失败，请重试");

        }
    }

    public boolean isNullOrEmpty(String strCode) {
        // TODO Auto-generated method stub
        if (strCode == null)
            return true;
        if ("".equals(strCode))
            return true;
        return false;
    }

}
