package com.nzy.nim.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.zxing.MipcaActivityCapture;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * Created by Administrator on 2016/11/25.
 */
public class ServerApplication extends LitePalApplication implements
        Thread.UncaughtExceptionHandler {

    public static ServerApplication serverApplication;
    public static Context serverContext;
    private PackageInfo packageInfo;// 当前包的信息
    // app的包名
    private static final String packageName = "com.nzy.nim";
    /**
     * 临时存储数据
     */
    private SparseArray<Object> datas = new SparseArray<Object>();

    // 全局上下文应用环境
    public static Context applicationContext;

    // ZXING扫描结果
    private MipcaActivityCapture.ScanResultHandlerSerilize scanResultHandlerSerilize;

    public ServerApplication() {
    }

    public static ServerApplication getInstance() {
        return serverApplication;
    }

    public static Context getContext() {
        return serverContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serverContext = getApplicationContext();
        serverApplication = this;
        try {
            packageInfo = serverContext.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 配置全局的图片异步缓存
        initConfig(this, MyConstants.BASE_DIR + MyConstants.CACHE_DIR);
    }

    /**
     * @TODO(功能) 图片异步加载的全局配置
     * @mark(备注)
     * @param context
     * @param cachePath
     *            自定义缓存路径
     */
    @SuppressWarnings("deprecation")
    private void initConfig(Context context, String cachePath) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(480, 800)
                .diskCacheExtraOptions(480, 800, null)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(50 * 1024 * 1024)
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                // default
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // 加载机制，后进先加载
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 将保存的时候的URI名称用MD5 加密
                .discCache(new UnlimitedDiscCache(new File(cachePath)))
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     *
     * @TODO TODO 将数据以键值对形式保存在myApplication中
     * @param key
     * @param value
     * @Return void
     */
    public void put(String key, Object value) {
        datas.put(key.hashCode(), value);
    }

    /**
     *
     * @TODO TODO 根据键获取值
     * @param key
     * @return
     * @Return Object
     */
    public Object get(String key) {
        return datas.get(key.hashCode());
    }

    public MipcaActivityCapture.ScanResultHandlerSerilize getScanResultHandlerSerilize() {
        return scanResultHandlerSerilize;
    }

    public void setScanResultHandlerSerilize(MipcaActivityCapture.ScanResultHandlerSerilize scanResultHandlerSerilize) {
        this.scanResultHandlerSerilize = scanResultHandlerSerilize;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

    }
}
