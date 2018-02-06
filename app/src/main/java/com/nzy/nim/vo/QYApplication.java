package com.nzy.nim.vo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.SparseArray;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.db.bean.Contacts;
import com.nzy.nim.db.bean.Users;
import com.nzy.nim.db.tmpbean.SPHelper;
import com.nzy.nim.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.nzy.nim.pulltorefresh.PullToRefreshListView;
import com.nzy.nim.tool.common.DBHelper;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.DateUtil;
import com.nzy.nim.tool.common.StringUtil;
import com.nzy.nim.zxing.MipcaActivityCapture;

import org.litepal.LitePalApplication;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/**
 * 
 * @author(作者) LIUBO
 * @Date(日期) 2015-1-22 下午10:33:42
 * @classify(类别)
 * @TODO(功能) TODO 保存一些临时共享数据
 * @Param(参数)
 * @Remark(备注)
 * 
 */
public class QYApplication extends LitePalApplication implements
		UncaughtExceptionHandler {
	public static QYApplication myApplication;
	public static Context myContext;
	private PackageInfo packageInfo;// 当前包的信息
	// app的包名
	private static final String packageName = "com.nzy.nim";
	// 签名串
	private static final String payServerToken = "fjzq@quanyou";
	/**
	 * 临时存储数据
	 */
	private SparseArray<Object> datas = new SparseArray<Object>();

	/**
	 * @Author LIUBO
	 * @TODO TODO 获取myapplication的单一实例
	 * @return
	 * @Date 2015-1-22
	 * @Return MyApplication
	 */
	public static QYApplication getInstance() {
		return myApplication;
	}

	public static Context getContext() {
		return myContext;
	}
	public static Context applicationContext;
	/**
	 * 初始化myApplication的实例
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		myApplication = this;
		myContext = this;
		applicationContext= getApplicationContext();
		try {
			packageInfo = myContext.getPackageManager().getPackageInfo(
                    packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Thread.setDefaultUncaughtExceptionHandler(this);
		// 配置全局的图片异步缓存
		initConfig(this, MyConstants.BASE_DIR + MyConstants.CACHE_DIR);
//		initBuglyConfig();
	}
	public static Context getMyContexts(){
		return applicationContext;
	}
//	/**
//	 *
//	 * @Author liubo
//	 * @date 2015-3-17下午1:25:14
//	 * @TODO(功能) 初始化bugly的bug监控
//	 * @mark(备注)
//	 */
//	private void initBuglyConfig() {
//		Context appContext = this.getApplicationContext();
//		String appId = "6e39ec06fb"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
//		boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段
//		UserStrategy strategy = new UserStrategy(getApplicationContext()); // App的策略Bean(可选)
//		strategy.setAppChannel("QY"); // 设置渠道
//		strategy.setAppVersion(packageInfo.versionName); // App的版本
//		// strategy.setAppReportDelay(5000); // 设置SDK处理延时，毫秒
//		CrashReport.initCrashReport(appContext, appId, isDebug, strategy);
//	}

	/**
	 * @Author liubo
	 * @date 2015-3-10下午11:26:48
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
	 * @Author LIUBO
	 * @TODO TODO 将数据以键值对形式保存在myApplication中
	 * @param key
	 * @param value
	 * @Date 2015-1-23
	 * @Return void
	 */
	public void put(String key, Object value) {
		datas.put(key.hashCode(), value);
	}

	/**
	 * 
	 * @Author LIUBO
	 * @TODO TODO 根据键获取值
	 * @param key
	 * @return
	 * @Date 2015-1-23
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

	private MipcaActivityCapture.ScanResultHandlerSerilize scanResultHandlerSerilize;
	/**
	 * @Author LIUBO
	 * @TODO TODO 根据键获取值，可以设置默认值
	 * @param key
	 * @param defvalue
	 *            默认值
	 * @return
	 * @Date 2015-1-23
	 * @Return Object
	 */
	public Object get(String key, Object defvalue) {
		if(DataUtil.isEmpty(key)){
			return null;
		}
		if (datas.get(key.hashCode()) == null)
			datas.put(key.hashCode(), defvalue);
		return datas.get(key.hashCode());
	}

	public void remove(String key) {
		datas.remove(key.hashCode());
	}

	/**
	 * 
	 * @author 刘波
	 * @date 2015-2-28下午12:55:36
	 * @todo 判断用户是否已经存在F
	 * @param name
	 * @return
	 */
	public static boolean isExist(String name) {
		Users user = DBHelper.getInstance().getUserByCode(name);
		if (user == null)
			return false;
		else
			return true;
	}

	/**
	 * @Author LIUBO
	 * @TODO TODO 返回登陆用户的Id
	 * @return personId
	 * @Date 2015-1-24
	 * @Return String
	 */
	public static String getPersonId() {
		String userId = (String) myApplication.get("current_userid", null);
		if (userId == null) {
			userId = SPHelper.getCurrentUserId();
			myApplication.put("current_userid", userId);
		}
		return userId;
	}
	public static String getAccessToken() {
		String accessToken = (String) myApplication.get("current_accessToken", null);
		if (accessToken == null) {
			accessToken = SPHelper.getCurrentAccessToken();
			myApplication.put("current_accessToken", accessToken);
		}
		return accessToken;
	}
	public static String getPayToken() {
		String info = StringUtil.getMD5ofStrNormal(getPersonId() + payServerToken);;
		return info;
	}
	public static String getUri(){
		return (String) myApplication.get("uri", null);
	}
	/**
	 * 获取学校的Id
	 * 
	 * @return
	 */
	public static String getSchoolId() {
//		UserInfo user = DBHelper.getInstance().getUserById(getPersonId());
//		if (user != null) {
//			return user.getSchoolId();
//		} else
			return null;
	}

	/**
	 * 获取好友昵称
	 * @param contactId
	 * @return
	 */
	public static String getContactMarkName(String contactId){
		Contacts contacts = DBHelper.getInstance().find(Contacts.class, "contactId=? and userid=?", contactId,QYApplication.getPersonId());
		if (contacts!=null&&!DataUtil.isEmpty(contacts.getRemark())) {
			return contacts.getRemark();
		}else{
			if(contacts!=null&&!DataUtil.isEmpty(contacts.getRemark())){
				return contacts.getUserName();
			}else{
				return null;
			}
		}
	}

	/**
	 * 获取用户的学号
	 * 
	 * @return
	 */
	public static String getUserCode() {
		return SPHelper.getUserName();
	}

	/**
	 * 生成文件保存路径
	 * 
	 * @return
	 */
	public static String createDirPath(String childPath) {
		return MyConstants.BASE_DIR + getUserCode() + childPath;
	}

	/**
	 * 下拉刷新的配置信息
	 *
	 * @param pullRefresh
	 * @param listener
	 * @param isAutoRefresh
	 * @param ispullDown
	 * @param key
	 *            唯一标示（根据标示从本地取数据）
	 */
	public static void initPullRefulshs(PullToRefreshListView pullRefresh,
										OnRefreshListener<ListView> listener, boolean isAutoRefresh,
										boolean ispullDown, String key) {
		pullRefresh.setPullRefreshEnabled(true);// true可下拉
		pullRefresh.setPullLoadEnabled(ispullDown);// true可上拉加载
		pullRefresh.setOnRefreshListener(listener);
		if (key == null)
			pullRefresh.setLastUpdatedLabel(null);
		else {
			pullRefresh.setLastUpdatedLabel(DateUtil.showDate(
					new Date(SPHelper.getLastUpdateTime(key)), false));
		}
		if (isAutoRefresh)
			pullRefresh.doPullRefreshing(true, 2000);// 第一次进来自动刷新

	}

	/**
	 * 下拉刷新的配置信息
	 *
	 * @param pullRefresh
	 * @param listener
	 * @param isAutoRefresh
	 * @param ispullDown
	 * @param key
	 *            唯一标示（根据标示从本地取数据）
	 */
	public static void initPullRefulsh(PullToRefreshListView pullRefresh,
									   OnRefreshListener<ListView> listener, boolean isAutoRefresh,
									   boolean ispullDown, String key) {
		pullRefresh.setPullRefreshEnabled(true);// true可下拉
		pullRefresh.setPullLoadEnabled(ispullDown);// true可上拉加载
		pullRefresh.setOnRefreshListener(listener);
		if (key == null)
			pullRefresh.setLastUpdatedLabel(null);
		else {
			pullRefresh.setLastUpdatedLabel(DateUtil.showDate(
					new Date(SPHelper.getLastUpdateTime(key)), false));
		}
		if (isAutoRefresh)
			pullRefresh.doPullRefreshing(true, 1000);// 第一次进来自动刷新

	}
	/**
	 * @author LIUBO
	 * @date 2015-4-8上午12:03:33
	 * @TODO 刷新完成的设置
	 * @param pullToRefresh
	 */
	public static void refulshComplete(PullToRefreshListView pullToRefresh,
			String key) {
		pullToRefresh.onPullDownRefreshComplete();// 下拉结束
		pullToRefresh.onPullUpRefreshComplete();// 上拉结束
		pullToRefresh.setHasMoreData(false);
		if (key == null)
			pullToRefresh.setLastUpdatedLabel(null);
		else {
			// 设置刷新的时间
			pullToRefresh.setLastUpdatedLabel(DateUtil.showDate(new Date(),
					false));
			SPHelper.setLastUpdateTime(new Date(), key);
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-8上午12:03:33
	 * @TODO 刷新完成的设置
	 * @param pullToRefresh
	 */
	public static void refulshCompletes(PullToRefreshListView pullToRefresh,
									   String key) {
		pullToRefresh.onPullDownRefreshComplete();// 下拉结束
		pullToRefresh.onPullUpRefreshComplete();// 上拉结束
		pullToRefresh.setHasMoreData(false);
		if (key == null)
			pullToRefresh.setLastUpdatedLabel(null);
		else {
			// 设置刷新的时间
			pullToRefresh.setLastUpdatedLabel(DateUtil.showDate(new Date(),
					false));
			SPHelper.setLastUpdateTime(new Date(), key);
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
//		AppManager.getInstance().reStartApp(this);
	}

	/**
	 * 获取本应用的信息
	 * 
	 * @return
	 */
	public static PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = myContext.getPackageManager().getPackageInfo("com.nzy.nim",
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 判断用户是否认证学号
	 * @return
     */
	public static boolean isAuthenticated(){
		UserInfo userInfo = DBHelper.getInstance().getUserById(QYApplication.getPersonId());
		if (userInfo!=null&&userInfo.getAuthentication()!=null){
			return userInfo.getAuthentication();
		}else{
			return false;
		}
	}
	public static String ACTIVITY_NAME="";
}
