package com.nzy.nim.tool.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.manager.AppManager;
import com.nzy.nim.vo.PersonSession;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/17.
 */
public class TokenUtils {
    private static TokenUtils tokenUtils;
    private  boolean isRequesting;//判断当前是否正在请求刷新token
    private ArrayList<WaitingQueue> list=new ArrayList<WaitingQueue>();

    private TokenUtils() {
    }

    public static TokenUtils getInstance() {
        if (tokenUtils == null) {
            synchronized (TokenUtils.class) {
                if (tokenUtils == null) {
                    tokenUtils = new TokenUtils();
                }
            }
        }
        return tokenUtils;
    }

    public void refreshAccessToken(final Context context, final String url, final Map<String, String> params, final VolleyListener listener) {
        final PersonSession personSession = DBHelper.getInstance().
                find(PersonSession.class, "personId=?", QYApplication.getPersonId());
        if (personSession == null) {
            ((Activity) context).finish();
            ToastUtil.showShort(context, "请重新登入");
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }

        if (isRequesting) {//判断当前是否正在进行刷新Token请求，如果是则加入等待队列
            WaitingQueue waitingQueue = new WaitingQueue(context, url, params, listener);
            if (!list.contains(waitingQueue)){
                list.add(waitingQueue);
            }
            return;
        }
        isRequesting=true;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("personId", QYApplication.getPersonId());
        map.put("refreshToken", personSession.getRefreshToken());
        HTTPUtils.post(context, URLs.REFRESH_ACCESSTOKEN, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(context, "访问失败，请检查网络设置！");
            }

            @Override
            public void onResponse(String s) {
                if (!DataUtil.isEmpty(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 0) {//刷新accessToken
                            PersonSession personSession = new Gson().fromJson(jsonObject.getString("personSession"), PersonSession.class);
                            DBHelper.getInstance().updateAccessToken(personSession, QYApplication.getPersonId());
                            // 保存用户Token
                            QYApplication.getInstance().put("current_accessToken",
                                    personSession.getAccessToken());
                            SPHelper.saveCurrentAccessToken(personSession.getAccessToken());
                            isRequesting=false;
                            HTTPUtils.postWithToken(context, url, params, listener);//继续完成请求操作
                            for (int i = 0; i < list.size(); i++) {//处理等待的网络请求的队列
                                WaitingQueue waitingQueue = list.get(i);
                                HTTPUtils.postWithToken(waitingQueue.getContext(),waitingQueue.getUrl(),waitingQueue.getParams(),waitingQueue.getListener());
                                list.remove(waitingQueue);//请求后，从等待队列中移除
                            }
                        } else if (errcode == 100) {//需要重新登录
                            DataSupport.deleteAll(UserInfo.class, "personId=?", QYApplication.getPersonId());
                            new AlertDialog.Builder(context).setTitle("提醒").setMessage("请重新登录！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AppManager.getInstance().reLoginApp(context);
                                }
                            }).setCancelable(false).show();
                            isRequesting=false;
                            list.clear();//清空队列
                        } else {
                            isRequesting=false;
                            list.clear();//清空队列
                            ToastUtil.showShort(context, jsonObject.getString("errmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 同时请求刷新Token的类
     */
    class WaitingQueue{
        Context context;
        String url;
        Map<String, String> params;
        VolleyListener listener;

        public WaitingQueue(Context context, String url, Map<String, String> params, VolleyListener listener) {
            this.context = context;
            this.url = url;
            this.params = params;
            this.listener = listener;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public VolleyListener getListener() {
            return listener;
        }

        public void setListener(VolleyListener listener) {
            this.listener = listener;
        }
    }
}
