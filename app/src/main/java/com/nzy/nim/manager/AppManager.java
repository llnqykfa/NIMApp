package com.nzy.nim.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nzy.nim.activity.login.LoginActivity;
import com.nzy.nim.activity.main.AppStartActivity;

import java.util.Stack;

public class AppManager {

    /**
     * 临时存放活动的栈（Stack继承Vector 线程安全）
     */
    private static Stack<Activity> mActivityStack;
    /**
     * AppManager对象
     */
    private static AppManager mAppManager;

    private AppManager() {
    }

    /**
     * 单一实例 懒汉模式（防止多线程并发是始终只有单一实例）
     */
    public static AppManager getInstance() {
        if (mAppManager == null) {
            synchronized (AppManager.class) {
                if (mAppManager == null)
                    mAppManager = new AppManager();
            }

        }
        return mAppManager;
    }

    /**
     * @param activity
     * @TODO 添加活动到堆栈中
     * @Return void
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void killTopActivity() {
        Activity activity = mActivityStack.lastElement();
        killActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void killActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void killActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                killActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void killAllActivity() {
        for (Activity activity : mActivityStack) {
            if (null != activity)
                activity.finish();
        }
        mActivityStack.clear();
    }

    public void exit() {
        killAllActivity();
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * @param context
     * @TODO 重启app
     */
    public void reStartApp(final Context context) {
        Intent intent = new Intent(context, AppStartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    /**
     * 重新登录
     */
    public void reLoginApp(Context context){
        context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        AppManager.getInstance().killAllActivity();
    }

    public static boolean isUpdate=true;
}
