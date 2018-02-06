package com.nzy.nim.http;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nzy.nim.R;
import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.activity.login.TransitionActivity;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.http.listener.HttpDatasListener;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.vo.PersonSession;
import com.nzy.nim.vo.PersonVO;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.vo.RingThemeDetailVO;
import com.nzy.nim.vo.UserInfo;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author(作者) LIUBO
 * @Date(日期) 2015-2-1 下午2:27:23
 * @classify(类别)
 * @TODO(功能) TODO 当前用户和服务器交互的实现类
 * @Param(参数)
 * @Remark(备注)
 */
public class HttpHelper {

    /**
     * 从服务器上拉取好友的监听器
     */
    private HttpDatasListener myHttpDatasListener;

    public void setMyHttpDatasListener(HttpDatasListener myHttpDatasListener) {
        this.myHttpDatasListener = myHttpDatasListener;
    }

    public void login(Context context, String name, String password) {
        doLoginAppByPhone(context, name, password);
        // 向服务器发送登陆请求
//		sendLoginInfo(context, name, password, type);
    }

    /**
     * @param context
     * @param phone
     * @param password
     * @Author LIUBO
     * @TODO TODO 向服务器发送登陆请求
     * @Return void
     */
    public void doLoginAppByPhone(final Context context, final String phone,
                                  final String password) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phone", phone);
        map.put("password", password);
        HTTPUtils.post(context, URLs.DO_LOGIN_BY_PHONE, map, new VolleyListener() {
            @Override
            public void onResponse(String s) {
                if (!DataUtil.isEmpty(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 0) {
                            PersonSession personSession = new Gson().fromJson(jsonObject.getString("personSession"), PersonSession.class);
                            //如果存在旧token，则删除
                            PersonSession oldPersonSession = DBHelper.getInstance().
                                    find(PersonSession.class, "personId=?", personSession.getPersonId());
                            if (oldPersonSession != null) {
                                oldPersonSession.delete();
                            }
                            personSession.saveThrows();//保存新的Token

                            // 保存用户Id
                            QYApplication.getInstance().put("current_userid",
                                    personSession.getPersonId());
                            SPHelper.saveCurrentUserId(personSession.getPersonId());
                            // 保存用户Token
                            QYApplication.getInstance().put("current_accessToken",
                                    personSession.getAccessToken());
                            SPHelper.saveCurrentAccessToken(personSession.getAccessToken());
                            saveLoginInfo(context, phone);//保存用户名
                            loginJump(context, phone, personSession.getPersonId());
                        } else {
                            if(LoginActivity.pgDialog!=null){
                                LoginActivity.pgDialog.dismiss();
                            }
                            ToastUtil.showShort(context, jsonObject.getString("errmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showShort(context, R.string.server_is_busy);
                if (LoginActivity.loginActivity != null)
                    LoginActivity.pgDialog.dismiss();
            }
        });
    }




    private void loginJump(final Context context, final String phone,
                           String personId) {
        // 标志恢复默认
        SPHelper.setIsModifyPwd(false, phone);
        SPHelper.saveExitFlag(false);
        SPHelper.putForcedOfflineFlag(false);
        TransitionActivity.actionIntent(context, phone,personId);
        if(LoginActivity.pgDialog!=null){
            LoginActivity.pgDialog.dismiss();
        }
        ((Activity) context).finish();
        // 判断是否是第一次登陆
//        if (!QYApplication.isExist(phone)) {
//        getUserInfoNew(context, phone, personId);
//            getUserInfo(context, phone, personId);
//        } else {
////            // 更新用户数据
////            DBHelper.getInstance().updateUser(personVO,
////                    QYApplication.getPersonId());
////            // 跳转的主界面
//            MainActivity.actionIntent(context);
//        }
    }

    /**
     * 保存用户名
     *
     * @param userName
     */
    private void saveLoginInfo(Context context, String userName) {
        SharedPreferences sp = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        HashSet<String> spLoginSet = (HashSet<String>) sp.getStringSet("userName", new HashSet<String>());
        //不保留原来的旧登录信息，只保留最新登录用户名
        spLoginSet.clear();
        spLoginSet.add(userName);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear().putStringSet("userName", spLoginSet);
        edit.apply();
    }

    public void refresh(Context context, final String personId, final OnSuccessListener listener) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("targetPersonId", personId);
        HTTPUtils.postWithToken(context, URLs.GET_PERSON_INFO, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                try {
                    UserInfo info = DBHelper.getInstance().find(UserInfo.class, "personId=?", personId);
                    if (info != null) {
                        info.delete();
                    }
                    UserInfo userInfo = new Gson().fromJson(new JSONObject(s).getString("person"), UserInfo.class);
                    userInfo.saveThrows();
                    listener.onFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void getAllMyFriends(final Context context, String personId) {
        HTTPUtils.postWithToken(context, URLs.GET_FRIENDS_INFO, null, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }

            @Override
            public void onResponse(String s) {
                List<PersonVO> datas = new ArrayList<PersonVO>();
                if (!DataUtil.isEmpty(s)) {
                    try {
                        datas = JSON.parseArray(new JSONObject(s).getString("friends"), PersonVO.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (myHttpDatasListener != null) {
                    myHttpDatasListener.pullAllFriends(datas);
                }
            }
        });
    }

    /**
     * @Author LIUBO
     * @TODO TODO 同步获取好友添加请求信息
     * @Date 2015-2-4
     * @Return void
     */

    public static void getPerson(String userId, HttpUtil.OnPostListener listener) {
        NameValuePair pairs = new NameValuePair("personId", userId);
        HttpUtil.syncPost(URLs.GET_PERSON, listener, pairs);
    }

    /**
     * @param groupId
     * @param listener
     * @author LIUBO
     * @date 2015-4-10上午11:20:33
     * @TODO 获取圈主题的信息 ，信息会以JSON格式返回到listener的onSucess方法中
     */
    public static void getRingTheme(String groupId, HttpUtil.OnPostListener listener) {
        NameValuePair pair1 = new NameValuePair("KEY", groupId);
        NameValuePair pair2 = new NameValuePair("class", "RingThemeVO");
        NameValuePair[] pairs = {pair1, pair2};
        HttpUtil.syncPost(URLs.GET_BY_PK, listener, pairs);
    }

    // 成功监听接口
    public interface OnSuccessListener {
        void onFinish();
    }

    /**
     * @param key   用户表的字段属性
     * @param value 该字段对应的值
     *              提交用户的修改的信息
     */
    public static void commitEditUserInfo(Context context, String url, String key,
                                          String value, final OnSuccessListener listener) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        if (key.equals("userName") && ("".equals(value) || value == null)) {
            ToastUtil.showShort(context, "昵称不能为空");
            return;
        }
        HTTPUtils.postWithToken(context, url, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }

            @Override
            public void onResponse(String s) {
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });
    }

    // *********************获取服务端deURL********************
    private OnLoadFinishListener listener;

    public interface OnLoadFinishListener {
        void onFinish(String data);
    }

    public void setOnLoadFinishListener(OnLoadFinishListener listener) {
        this.listener = listener;
    }

    public void getServerUrl(String serverType) {
        Log.e("", "schoolId==" + QYApplication.getSchoolId() + "serverType==" + serverType);
        if (QYApplication.getSchoolId() != null)
            getServerUrl(serverType, QYApplication.getSchoolId());
    }

    /**
     * 从服务端获取url
     *
     * @param schoolId
     * @param serverType
     *///0001SX1000000000OL15
    public void getServerUrl(String serverType, String schoolId) {
        if (schoolId == null)
            return;
        RequestParams params = new RequestParams();
        params.add("schoolId", schoolId);
        params.add("serverType", serverType);

        HttpUtil.post(URLs.GET_SERVER_URL, params,
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        if (arg2 != null && listener != null) {
                            listener.onFinish(DataUtil.dealMessage1(arg2));
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2,
                                          Throwable arg3) {
                        if (listener != null) {
                            listener.onFinish(null);
                        }
                    }
                });
    }

    // 新书推荐
    public static void pushNewBook(final String personId) {
        final HttpHelper helper = new HttpHelper();
        helper.getServerUrl(MyConstants.SINGLE_NEW_BOOK_PUSH);
        helper.setOnLoadFinishListener(new OnLoadFinishListener() {
            @Override
            public void onFinish(String data) {
                pushBook(data, personId);
            }
        });
    }

    // 推书
    private static void pushBook(String url, String personId) {
        RequestParams params = new RequestParams();
        params.add("personId", personId);
        HttpUtil.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
            }
        });
    }

    // 热门图书推荐
    public static void pushHotBook(final String personId) {
        final HttpHelper helper = new HttpHelper();
        helper.getServerUrl(MyConstants.SINGLE_HOT_BOOK_PUSH);
        helper.setOnLoadFinishListener(new OnLoadFinishListener() {
            @Override
            public void onFinish(String data) {
                pushBook(data, personId);
            }
        });
    }

    /**
     * 获取组圈头像
     *
     * @param id
     * @param listener
     */
    public static void getRingImgFromNet(Context context, final String id, final OnLoadFinishListener listener) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ringId", id);
        HTTPUtils.postWithToken(context, URLs.GET_RING_THEME_DETAIL, map, new VolleyListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }

                    @Override
                    public void onResponse(String s) {
                        Log.e("RingThemeDetailVO", s);
                        try {
                            Gson gson = new Gson();
                            RingThemeDetailVO themeDetailVO = gson.fromJson(s, RingThemeDetailVO.class);
                            int errcode = themeDetailVO.getErrcode();
                            if (errcode == 0) {
                                RingThemeDetailVO.RingThemeEntity ringTheme = themeDetailVO.getRingTheme();
                                listener.onFinish(ringTheme.getImage());
                            }
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                }
        );
    }

    public static void getUserImgFromNet(Context context, final String id, final OnLoadFinishListener listener) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("targetPersonId", id);
        HTTPUtils.postWithToken(context, URLs.GET_PERSON_INFO, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                try {
                    PersonVO personVO = new Gson().fromJson(new JSONObject(s).getString("person"), PersonVO.class);
                    listener.onFinish(personVO.getPhotoPath());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     * @param context
     * @param ringId
     * @param shareContent
     * @param listener
     */
    public static void shareToRingDynamic(Context context, final String ringId, String shareContent, final OnLoadFinishListener listener) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ringId", ringId);
        map.put("isShare", true + "");
        map.put("shareContent", shareContent);
        HTTPUtils.postWithToken(context, URLs.ADD_DYNAMIC_NEW_LIST, map, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

            @Override
            public void onResponse(String s) {
                try {
                    listener.onFinish(new JSONObject(s).getString("errmsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
