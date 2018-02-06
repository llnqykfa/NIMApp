package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.activity.base.ClipPicActivity;
import com.nzy.nim.activity.base.ShowBigImageActivity;
import com.nzy.nim.api.FileUtils;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.AudioRecorder;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.FileImageUpload;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.tool.common.UploadUtil;
import com.nzy.nim.view.SelectedPicPopupWindow;
import com.nzy.nim.vo.QYApplication;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import java.net.URI;

/**
 * @author LIUBO
 * @date 2015-4-3上午10:17:03
 * @TODO 发起组圈第二步
 */
public class MakeRingSecondActivity extends BaseActivity implements
		OnClickListener, SelectedPicPopupWindow.OnChoosePicListener, View.OnTouchListener {

	private static int RECORD_NO = 0;  //不在录音
	private static int RECORD_ING = 1;   //正在录音
	private static int RECODE_ED = 2;   //完成录音

	private static int RECODE_STATE = 0;      //录音的状态
	private String date="";
	private Dialog dialog;
	private AudioRecorder mr;
	private static float recodeTime=0.0f;    //录音的时间
	private static double voiceValue=0.0;    //麦克风获取的音量值
	private static int MAX_TIME = 0;    //最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1;     //最短录制时间，单位秒，0为无时间限制，建议设为1
	private Thread recordThread;
	private ImageView dialog_img;
	private String voicePath;
	private Button img_voice;
	final int CUT_RETANGLE_PIC = 1;// 裁剪后的图片
	final int OPEN_CAMERA = 2;// 打开相机
	final int REQUEST_CODE_LOCAL = 3;// 本地选择图片
	final int CREATE_OK = 0;
	final int CREATE_FAIL = 1;
	private String titleContent = "";
	private ImageView imgIcon;
	private ImageView imgContent;
	private EditText editText;
	private TextView characterCount;
	private String photoPath;
	private String picPath;
//	ProgressDialog pDialog;// 进度条
	private ScrollView scroll;
	private String marek;
	private boolean isSucess=false;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CREATE_OK:
				pDialog.dismiss();
//				ToastUtil.show(MakeRingSecondActivity.this, "创建成功！",
//						Gravity.TOP);
				break;
			case CREATE_FAIL:
				pDialog.dismiss();
				ToastUtil.show(MakeRingSecondActivity.this, R.string.server_is_busy,
						Gravity.TOP);
				break;
			}

		};
	};
	private ProgressDialog pDialog;
	private TextView tv_class1;
	private TextView tv_class2;
	private TextView tv_class3;
	private TextView tv_class4;
	private int ringclass;
	private TextView edit_tag;
	private StringBuffer sb=new StringBuffer();
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("photoPath_data", photoPath);
	};
	TextView content_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_ring_second);
		pDialog = DialogHelper.getSDialog(MakeRingSecondActivity.this, "发送中···", false);
//		ProgressDialog sDialog = DialogHelper.getSDialog(this, "发表中···", false);
		if (savedInstanceState != null) {
			photoPath = savedInstanceState.getString("photoPath_data");
		}
		marek = getIntent().getStringExtra("mark");
		initTopBar();
		imgIcon = (ImageView) findViewById(R.id.make_ring_second_img_icon);
		imgContent = (ImageView) findViewById(R.id.make_ring_second_img_content);
		editText = (EditText) findViewById(R.id.make_ring_second_content);
		characterCount = (TextView) findViewById(R.id.make_ring_second_character_count);
		content_title = (TextView) findViewById(R.id.content_title);
		TextView content_class = (TextView) findViewById(R.id.content_class);
		LinearLayout line_class = (LinearLayout) findViewById(R.id.line_class);
		TextView content_label = (TextView) findViewById(R.id.content_label);
		RelativeLayout rel_lable = (RelativeLayout) findViewById(R.id.rel_lable);
		findViewById(R.id.tv_addleble).setOnClickListener(this);
		img_voice = (Button) findViewById(R.id.img_voice);
		tv_class1 = (TextView) findViewById(R.id.tv_class1);
		tv_class1.setOnClickListener(this);
		tv_class2 = (TextView) findViewById(R.id.tv_class2);
		tv_class2.setOnClickListener(this);
		tv_class3 = (TextView) findViewById(R.id.tv_class3);
		tv_class3.setOnClickListener(this);
		tv_class4 = (TextView) findViewById(R.id.tv_class4);
		tv_class4.setOnClickListener(this);
		ringclass=0;
		edit_tag = (TextView) findViewById(R.id.edit_tag);
		img_voice.setOnTouchListener(this);
		scroll = (ScrollView) findViewById(R.id.make_ring_second_scroll);
		if("MySchoolActivity".equals(marek)){

			content_title.setText("内容动态");
			editText.setHint("写点什么吧~~");
			img_voice.setVisibility(View.VISIBLE);
			content_class.setVisibility(View.GONE);
			line_class.setVisibility(View.GONE);
			rel_lable.setVisibility(View.GONE);
			content_label.setVisibility(View.GONE);
//			edit_tag.setVisibility(View.GONE);
		}else {
//			pDialog = DialogHelper.getSDialog(this, "发起组圈中···", false);
		}

		imgIcon.setOnClickListener(this);
		imgContent.setOnClickListener(this);
		characterCount.setText("0/180");
		imgContent.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showDeleteDialog();
				return false;
			}
		});
//		edit_tag.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CommonUtil.scrollToBottom(scroll);
//			}
//		});
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 180) {
					editText.setText(s.subSequence(0, 180));
					editText.setSelection(editText.getText().length());
					ToastUtil.show(MakeRingSecondActivity.this, "字数已达到上限！",
							Gravity.TOP);
					characterCount.setText("180/180");
				} else {
					characterCount.setText(s.length() + "/180");
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
//			try{
//				OutputStream os=openFileOutput("file-makeio.txt",Context.MODE_PRIVATE);
//				String str="";
//				os.write(str.getBytes("utf-8"));
//				os.close();
//			}catch(Exception e){
//
//			}
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
		}
//			try{
//				OutputStream os=openFileOutput("file-makeio.txt",Context.MODE_PRIVATE);
//				String str=editText.getText().toString();
//				os.write(str.getBytes("utf-8"));
//				os.close();
//			}catch(Exception e){
//
//			}
//		}
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
//		try {
//			InputStream is = openFileInput("file-makeio.txt");
//			byte[] buffer = new byte[100];
//			int byteLength = is.read(buffer);
//			String str2 = new String(buffer, 0, byteLength, "utf-8");
//			editText.setText(str2.toString());
//			is.close();
//		}  catch (Exception e) {
//		}
	}

	private void showDeleteDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("确定要删除该图片吗？").setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						imgContent.setVisibility(View.INVISIBLE);
						imgIcon.setVisibility(View.VISIBLE);
						picPath = "";
					}
				}).create().show();

	}

	private void showCustomDialog(){
		//定义Dialog对象，/res/values/下的styles.xml文件十分重要R.style.CustomDialog
		dialog = new Dialog(MakeRingSecondActivity.this, R.style.CustomDialogs);
		dialog.setContentView(R.layout.ring_leble);// 为对话框设置自定义布局
		TextView okBtn = (TextView) dialog.findViewById(R.id.tv_group_ok);
		TextView canclebtn = (TextView) dialog.findViewById(R.id.tv_group_esc);
		final EditText ed_group = (EditText)dialog.findViewById(R.id.ed_group);

		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){

				String str=ed_group.getText().toString().trim();
				if(str.length()==0){
					Toast.makeText(getApplicationContext(), "标签名称不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}else if(str.length()>5){
					Toast.makeText(getApplicationContext(), "标签长度过长，请输入5个字符以内",
							Toast.LENGTH_SHORT).show();
					return;
				}
				sb.append(str + "_");
				String[] split = sb.substring(0, sb.toString().length() - 1).split("_");
				if(split.length>5){
					Toast.makeText(getApplicationContext(), "最多只能5个标签",Toast.LENGTH_SHORT).show();
					return;
				}
				edit_tag.setVisibility(View.VISIBLE);
				edit_tag.append(str + "   ");
				/**隐藏软键盘**/
				View view = dialog.getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				dialog.dismiss();
			}
		});

		canclebtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				/**隐藏软键盘**/
				View view = dialog.getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void handleDatas() {
		if (DataUtil.isEmpty(picPath)) {
			pDialog.dismiss();
			ToastUtil.show(this, "亲,请先选择一张图片哦！", Gravity.TOP);
			return;
		}
		if (TextUtils.isEmpty(editText.getText())) {
			pDialog.dismiss();
			ToastUtil.show(this, "圈介绍不能为空！", Gravity.TOP);
			return;
		}
//		pDialog.show();
		// 开启子线程发起组圈
		new Thread(new Runnable() {
			@Override
			public void run() {
				if("MySchoolActivity".equals(marek)){
//					upSchoolPic();
					uploadIcon();
//					RequestParams params=new RequestParams();
////
//					params.addBodyParameter("image", new File(photoPath));
//					uploadMethod(params, URLs.SEND_MY_SCHOOLE_IMAGE);
				}else {
////					upSchoolPic();
//					Toast.makeText(MakeRingSecondActivity.this,"圈介绍不能",1).show();
					uploadPic();
				}
			}
		}).start();

	}

	public  void uploadMethod(final RequestParams params, final String uploadHost) {
		new HttpUtils().send(HttpRequest.HttpMethod.POST, uploadHost, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
//                      msgTextview.setText("conn...");
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
//                          msgTextview.setText("upload: " + current + "/"+ total);
				} else {
//                          msgTextview.setText("reply: " + current + "/"+ total);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
//                      msgTextview.setText("reply: " + responseInfo.result);
				Toast.makeText(MakeRingSecondActivity.this, "服务器繁忙，请重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
//                      msgTextview.setText(error.getExceptionCode() + ":" + msg);
				Toast.makeText(MakeRingSecondActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	//发布新动态 我的大学
	private void point() {
		com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
		params.add("personId", QYApplication.getPersonId());
		params.add("photoPath", photoPathS);
		params.add("photoName", photoName);
		params.add("content", editText.getText().toString());
		params.add("voicePath",voicePath);
		HttpUtil.post(URLs.SEND_MY_SCHOOLE_TEXT, params,
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// 如果返回的数据不为空
						pDialog.dismiss();
						try {
							JSONObject json = new JSONObject(arg2);
							String errcode = json.getString("errcode");
							if (errcode.equals("0")) {
								isSucess=true;
//								Toast.makeText(MakeRingSecondActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
								Intent mIntent = new Intent();
								mIntent.setAction("release");
								//发送广播
								sendBroadcast(mIntent);
								finish();
							} else {
								Toast.makeText(MakeRingSecondActivity.this, json.getString("errmsg"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2,
										  Throwable arg3) {
						pDialog.dismiss();
					}
				});
	}


	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				// 上传完的处理
				case 1:
					handleUpload();
					break;
				case 2:
					handleUploadVoice();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}

	};
	String photoName;
	String photoPathS;
	private void handleUpload() {
		if (!isNullOrEmpty(uploadMessage)) {

			try {
				JSONObject object = new JSONObject(uploadMessage);
				String errcode = object.getString("errcode");
				if ("0".equals(errcode)) {
//					Toast.makeText(MakeRingSecondActivity.this, object.getString("errmsg"),1).show();
					 photoName = object.getString("photoName");

					 photoPathS = object.getString("photoPath");
					point();
				} else {

//					Toast.makeText(MakeRingSecondActivity.this,object.getString("errmsg"),1).show();

				}

			} catch (Exception e) {
			}

		} else {
			Toast.makeText(MakeRingSecondActivity.this, "服务器繁忙，请重试",Toast.LENGTH_SHORT).show();

		}
	}
	public  boolean isNullOrEmpty(String strCode) {
		// TODO Auto-generated method stub
		if (strCode == null)
			return true;
		if ("".equals(strCode))
			return true;
		return false;
	}

	private String uploadMessage;
	private UploadUtil uploadUtil = UploadUtil.getInstance();;
	private void uploadIcon() {
//		pDialog.show();
			File file = new File(picPath);
			Map<String, String> param = new HashMap<String, String>();
			uploadUtil.uploadFile(file, "image", URLs.SEND_MY_SCHOOLE_IMAGE, param);
			uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {

				@Override
				public void onUploadProcess(int uploadSize) {

				}

				@Override
				public void onUploadDone(int responseCode, String message) {

					uploadMessage = message;
//					// 上传完图像的处理
					mHandler.sendEmptyMessage(1);

				}

				@Override
				public void initUpload(int fileSize) {

				}
			});



	}

	private void upSchoolPic() {


		String result = FileImageUpload.uploadFile(new File(picPath), URLs.SEND_MY_SCHOOLE_IMAGE);

	}

	private void initTopBar() {
		TextView title = (TextView) findViewById(R.id.top_bar_content);
		Button next = (Button) findViewById(R.id.top_bar_next);

		next.setText("完成");
		titleContent = getIntent().getStringExtra("title");
		if("MySchoolActivity".equals(marek)){
			title.setText("我的大学");
		}else {
			title.setText(titleContent);
		}
		title.setText(titleContent);
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = getWindow().peekDecorView();//关闭软键盘
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				pDialog.show();
				handleDatas();
			}
		});
	}

	public static void actionIntent(Context context, String title) {
		Intent intent = new Intent(context, MakeRingSecondActivity.class);
		intent.putExtra("title", title);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.make_ring_second_img_icon:
			photoPath = MyConstants.BASE_DIR + "/"
					+ Calendar.getInstance().getTimeInMillis() + ".jpg";
			new SelectedPicPopupWindow(this, v).setOnChoosePicListener(this);
			break;
		// case R.id.make_ring_second_previous:
		// finish();
		// break;
		// case R.id.make_ring_second_finish:
		// handleDatas();
		// break;
		case R.id.make_ring_second_img_content:
			if (picPath != null)
				ShowBigImageActivity.actionIntent(this, "file://" + picPath);
			break;
			case R.id.tv_class1://默认分类
				ringclass=0;
				tv_class1.setTextColor(getResources().getColor(R.color.category_true));
				tv_class2.setTextColor(getResources().getColor(R.color.category_false));
				tv_class3.setTextColor(getResources().getColor(R.color.category_false));
				tv_class4.setTextColor(getResources().getColor(R.color.category_false));
				break;
			case R.id.tv_class2://创业
				ringclass=2;
				tv_class1.setTextColor(getResources().getColor(R.color.category_false));
				tv_class2.setTextColor(getResources().getColor(R.color.category_true));
				tv_class3.setTextColor(getResources().getColor(R.color.category_false));
				tv_class4.setTextColor(getResources().getColor(R.color.category_false));
				break;
			case R.id.tv_class3://生活
				ringclass=3;
				tv_class1.setTextColor(getResources().getColor(R.color.category_false));
				tv_class2.setTextColor(getResources().getColor(R.color.category_false));
				tv_class3.setTextColor(getResources().getColor(R.color.category_true));
				tv_class4.setTextColor(getResources().getColor(R.color.category_false));
				break;
			case R.id.tv_class4://读书
				ringclass=4;
				tv_class1.setTextColor(getResources().getColor(R.color.category_false));
				tv_class2.setTextColor(getResources().getColor(R.color.category_false));
				tv_class3.setTextColor(getResources().getColor(R.color.category_false));
				tv_class4.setTextColor(getResources().getColor(R.color.category_true));
				break;
			case R.id.tv_addleble:
				showCustomDialog();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OPEN_CAMERA:// 拍照
			if (photoPath != null && new File(photoPath).exists()) {
				ClipPicActivity.actionIntent(this, photoPath, CUT_RETANGLE_PIC,
						false, 30);
			} else {
				ToastUtil.show(this, "图片不存在！", Gravity.TOP);
			}
			break;
		case REQUEST_CODE_LOCAL:// 本地图片
			if (data != null) {
				ClipPicActivity.actionIntent(this,
						ImageUtil.getLocalImgPath(this, data.getData()),
						CUT_RETANGLE_PIC, false, 30);
			}
			break;
		case CUT_RETANGLE_PIC:// 返回裁剪后的图片
			if (data != null) {
				imgIcon.setVisibility(View.INVISIBLE);
				imgContent.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						"file://" + data.getStringExtra("cut_pic"), imgContent);
				picPath = data.getStringExtra("cut_pic");
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-3下午12:42:12
	 * @TODO 上传图片
	 * @param
	 */
	private void uploadPic() {
		// 上传图片,上传成功要把裁剪后的图片删掉
//		pDialog.show();
		HttpUtil.upLoadFiles(Arrays.asList(new String[]{picPath}),
				MyConstants.RING_FLODER_TYPE, new HttpUtil.OnPostListener() {
					@Override
					public void onSuccess(String jsonData) {
						addRing(jsonData.substring(2, jsonData.length() - 2));
//						Log.e("", "jsonData=" + jsonData.toString());
//						Toast.makeText(MakeRingSecondActivity.this,"",Toast.LENGTH_LONG).show();
//						try {
//							JSONArray array = new JSONArray(jsonData);
//							String o = (String) array.get(0);
//							createRing(jsonData.replace("[","").replace("]",""));
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
////						List<String> path = (List<String>) JSON.parse(jsonData);
////						if (path != null && path.size() > 0)
////							createRing(o);
//						finish();
//
					}

					@Override
					public void onFailure() {
						handler.sendEmptyMessage(CREATE_FAIL);
					}
				});
	}

	/**
	 * 添加组圈
	 * @param name
	 */
	private  void addRing(String name) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("theme", titleContent);
		hashMap.put("introduce", editText.getText().toString().trim());
		hashMap.put("photoName", name);
		String category = String.valueOf(ringclass);
		hashMap.put("category", category);
		if(sb.length()!=0&&!sb.equals("")){
			hashMap.put("tags",sb.substring(0, sb.toString().length() - 1));
		}
		HTTPUtils.postWithToken(MakeRingSecondActivity.this, URLs.ADD_RING, hashMap, new VolleyListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				handler.sendEmptyMessage(CREATE_FAIL);
			}

			@Override
			public void onResponse(String s) {
				FileUtils.deleteFile(picPath);
				handler.sendEmptyMessage(CREATE_OK);
				pDialog.dismiss();
//				// 跳转到组圈详细界面
				if (s != null) {
//					RingThemeVO themeVo = JSON.parseObject(s,
//							RingThemeVO.class);
//					if (themeVo != null) {
//						DBConversion.getInstance().getGroup(themeVo)
//								.saveThrows();
//						DBConversion.getInstance().getTmpRing(themeVo)
//								.saveThrows();
//					}
//					RingTeamInfoActivity.actionIntent(MakeRingSecondActivity.this, themeVo.getPk_ringtheme());
					//发送广播更新我的组圈
					try {
						JSONObject jb = new JSONObject(s);
						int errcode = jb.getInt("errcode");
						String errmsg = jb.getString("errmsg");
						if(errcode==0){
							isSucess=true;
							Intent intent = new Intent();
							intent.setAction("addMygroups");
							sendBroadcast(intent);
							MakeRingSecondActivity.this.finish();
							if (MakeRingFirstActivity.instance != null)
								MakeRingFirstActivity.instance.finish();
						}else{
							ToastUtil.showShort(MakeRingSecondActivity.this,errmsg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});

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

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
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
			case MotionEvent.ACTION_MOVE:

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
						showVoiceHintDialog();

					}
				}

				break;
		}
		return false;
	}

	private void showVoiceHintDialog() {

		new AlertDialog.Builder(MakeRingSecondActivity.this).setTitle("系统提示")//设置对话框标题

				.setMessage("录音成功是否上传")//设置显示的内容

				.setPositiveButton("发送", new DialogInterface.OnClickListener() {//添加确定按钮


					@Override

					public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

						// TODO Auto-generated method stub

						uploadUtil.uploadFile(getFilePath(), "voice", URLs.SEND_MY_SCHOOL_VOICE, null);
						uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {
							@Override
							public void onUploadDone(int responseCode, String message) {
								uploadMessage = message;
//								// 上传完图像的处理
								mHandler.sendEmptyMessage(2);

							}

							@Override
							public void onUploadProcess(int uploadSize) {

							}

							@Override
							public void initUpload(int fileSize) {

							}
						});

					}

				}).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮



			@Override

			public void onClick(DialogInterface dialog, int which) {//响应事件

				// TODO Auto-generated method stub

				Log.i("alertdialog"," 请保存数据！");

			}

		}).show();//在按键响应事件中显示此对话框
	}


	private void handleUploadVoice() {
		if (!isNullOrEmpty(uploadMessage)) {

			try {
				JSONObject object = new JSONObject(uploadMessage);
				String errcode = object.getString("errcode");
				if ("0".equals(errcode)) {
					Toast.makeText(MakeRingSecondActivity.this, object.getString("errmsg"),Toast.LENGTH_SHORT).show();

					voicePath = object.getString("voicePath");
					img_voice.setVisibility(View.INVISIBLE);
				} else {

					Toast.makeText(MakeRingSecondActivity.this,object.getString("errmsg"),Toast.LENGTH_SHORT).show();

				}

			} catch (Exception e) {
			}

		} else {
			Toast.makeText(MakeRingSecondActivity.this, "服务器繁忙，请重试",Toast.LENGTH_SHORT).show();

		}
	}








	//获取文件手机路径
	private String getAmrPath(){
		File file = new File(Environment
				.getExternalStorageDirectory(), "my/"+date+".amr");
		return file.getAbsolutePath();
	}


	//获取文件
	private File getFilePath(){
		File file = new File(Environment
				.getExternalStorageDirectory(), "my/"+date+".amr");
		return file;
	}
	//录音计时线程
	private void mythread(){
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
							RECODE_STATE=RECODE_ED;
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
								img_voice.setText("按住开始录音");
								RECODE_STATE=RECORD_NO;
							}else{
								img_voice.setText("录音完成!点击重新录音");

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
	private void setDialogImage(){
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		}else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		}else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		}else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		}else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		}else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		}else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		}else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		}else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		}else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		}else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		}else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		}else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		}else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}
	//录音时显示Dialog
	private void showVoiceDialog(){
		dialog = new Dialog(MakeRingSecondActivity.this,R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img=(ImageView)dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}

	//录音时间太短时Toast显示
	private void showWarnToast(){
		Toast toast = new Toast(MakeRingSecondActivity.this);
		LinearLayout linearLayout = new LinearLayout(MakeRingSecondActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(MakeRingSecondActivity.this);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(MakeRingSecondActivity.this);
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
		toast.setGravity(Gravity.CENTER, 0,0);//起点位置为中间     100为向下移100dp
		toast.show();
	}
}
