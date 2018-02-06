package com.nzy.nim.activity.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.nzy.nim.R;
import com.nzy.nim.activity.base.BaseActivity;
import com.nzy.nim.adapter.ImageGridAdapter;
import com.nzy.nim.adapter.ListSelectedPicAdapter;
import com.nzy.nim.api.URLs;
import com.nzy.nim.constant.MyConstants;
import com.nzy.nim.helper.DialogHelper;
import com.nzy.nim.tool.common.CommonUtil;
import com.nzy.nim.tool.common.DataUtil;
import com.nzy.nim.tool.common.HttpUtil;
import com.nzy.nim.tool.common.ImageUtil;
import com.nzy.nim.tool.common.ToastUtil;
import com.nzy.nim.view.SelectedPicPopupWindow;
import com.nzy.nim.volley.HTTPUtils;
import com.nzy.nim.volley.VolleyListener;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author LIUBO
 * @date 2015-4-6下午9:12:09
 * @TODO 发表界面
 */
public class PublishedActivity extends BaseActivity implements SelectedPicPopupWindow.OnChoosePicListener {
	private static final int TAKE_PICTURE = 2;
	private static final int SELECT_LOCAL_PIC = 1;
	private static final int MODIFY_MY_SELECT = 3;
	public static final int MAXPICNUM = 9;
	private String photoPath = "";

	private GridView noScrollgridview;
	private ListSelectedPicAdapter adapter;
	private TextView title;
	private Button next;
	private EditText editText;
	private TextView countTv;
	private ProgressDialog PgDialog;
	private String groupId;
	private String dynamic;
	private boolean isSucess=false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectimg);
		PgDialog = DialogHelper.getSDialog(PublishedActivity.this, "发表评论中···", true);
		groupId = getIntent().getStringExtra("groupId");
		dynamic = getIntent().getStringExtra("dynamic");
		if (savedInstanceState != null) {
			photoPath = savedInstanceState.getString("photoPath");
		}
		initView();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void initView() {
		editText = (EditText) findViewById(R.id.select_img_editContent);
		countTv = (TextView) findViewById(R.id.select_img_num_count);
		next = (Button) findViewById(R.id.top_bar_next);
		title = (TextView) findViewById(R.id.top_bar_content);

		title.setText("写评论");
		next.setText("发表");
		findViewById(R.id.top_back_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				countTv.setText(s.length() + "/10000");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		noScrollgridview = (GridView) findViewById(R.id.activity_selectimg_noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ListSelectedPicAdapter(this,
				ImageGridAdapter.selectPicPaths, MAXPICNUM);
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				CommonUtil.hideKeyboard(PublishedActivity.this);
				if (position == ImageGridAdapter.selectPicPaths.size()) {
					new SelectedPicPopupWindow(PublishedActivity.this,
							noScrollgridview)
							.setOnChoosePicListener(PublishedActivity.this);
				} else {
					PhotoActivity.actionIntent(PublishedActivity.this,
							position, MODIFY_MY_SELECT);
				}
			}
		});
		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (TextUtils.isEmpty(editText.getText())
						&& DataUtil.isEmpty(ImageGridAdapter.selectPicPaths)) {
					ToastUtil.show(getApplicationContext(), "请输入内容或选择图片!", Gravity.TOP);
					return;
				}
				if (editText.getText().length() > 10000) {
					ToastUtil.show(getApplicationContext(), "内容长度不能超过10000个字!", Gravity.TOP);
					return;
				}
				if(editText.getText().toString().trim().length()==0){
					ToastUtil.show(getApplicationContext(), "内容不能为空!", Gravity.TOP);
					return;
				}
				PgDialog.show();
				// 发表图片
				new Thread(new Runnable() {
					@Override
					public void run() {
						ArrayList<String> list=new ArrayList<String>();
						for (int i = 0; i < ImageGridAdapter.selectPicPaths.size(); i++) {
							Log.e("selectPicPaths"+(i),ImageGridAdapter.selectPicPaths.get(i));
							String imgPath = ImageUtil.getCompressImgPath(ImageGridAdapter.selectPicPaths.get(i), 480, 600);
							Log.e("imgpath"+(i),imgPath);
							list.add(imgPath);
							Log.e("list"+(i),list.get(i));
						}
						uploadPics(list);
					}
				}).start();

			}
		});
	}

	final int PUBLISH_OK = 0;
	final int PUBLISH_FAILE = 1;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			PgDialog.dismiss();
			switch (msg.what) {
			case PUBLISH_OK:
				ToastUtil.show(getApplicationContext(), "评论发表成功!", Gravity.TOP);
				PublishedActivity.this.finish();
				break;

			case PUBLISH_FAILE:
				ToastUtil.show(getApplicationContext(), "评论发表失败!", Gravity.TOP);
				break;
			}
		}
	};

	/**
	 * @author LIUBO
	 * @date 2015-4-6下午10:40:27
	 * @TODO 向服务器上传图片
	 * @param listStr
	 */
	private void uploadPics(List<String> listStr) {
		if (listStr == null || listStr.size() == 0) {
			publishedComments(null);
		} else {
			HttpUtil.upLoadFiles(listStr, MyConstants.RING_FLODER_TYPE,
					new HttpUtil.OnPostListener() {
						@Override
						public void onSuccess(String jsonData) {
//							@SuppressWarnings("unchecked")
									Log.e("","jsonData=="+jsonData.toString());
							List<String> ls = (List<String>) JSON
									.parse(jsonData);
							if (ls != null && ls.size() > 0) {
								StringBuffer sb = new StringBuffer();
								for (int i = 0; i < ls.size(); i++) {
									if (i == ls.size() - 1)
										sb.append(ls.get(i));
									else
										sb.append(ls.get(i) + "_");
								}
								publishedComments(sb.toString());
							}

						}

						@Override
						public void onFailure() {
							handler.sendEmptyMessage(PUBLISH_FAILE);
						}
					});
		}
	}

	/**
	 * @author LIUBO
	 * @date 2015-4-6下午10:38:44
	 * @TODO 向服务器提交数据
	 * @param photoNames
	 */
	private void publishedComments(String photoNames) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("ringId", groupId);
		if(photoNames!=null){
			hashMap.put("imageNames", photoNames);
		}
		if (!DataUtil.isEmpty(editText.getText())) {
			hashMap.put("content", editText.getText().toString());
		} else {
			hashMap.put("content", "");
		}
		hashMap.put("isShare",""+false);
		HTTPUtils.postWithToken(PublishedActivity.this, URLs.ADD_DYNAMIC_NEW_LIST, hashMap, new VolleyListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				handler.sendEmptyMessage(PUBLISH_FAILE);
			}

			@Override
			public void onResponse(String s) {
				if (s != null) {
					try {
						JSONObject jb = new JSONObject(s);
						int errcode = jb.getInt("errcode");
						String errmsg = jb.getString("errmsg");
						if (errcode == 0) {
							isSucess=true;
							Intent intent = new Intent();
							intent.setAction("addDynamic");
							sendBroadcast(intent);
							handler.sendEmptyMessage(PUBLISH_OK);
						} else {
							ToastUtil.showShort(PublishedActivity.this, errmsg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});


	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (ImageGridAdapter.selectPicPaths.size() < MAXPICNUM
					&& new File(photoPath).exists()) {
				ImageGridAdapter.selectPicPaths.add(photoPath);
				adapter.notifyDataSetChanged();
			}
			break;
		case SELECT_LOCAL_PIC:
			adapter.notifyDataSetChanged();
			break;
		case MODIFY_MY_SELECT:
			adapter.notifyDataSetChanged();
			break;
		}
	}

	public static void actionIntent(Context context, String groupId,String dynamic) {
		Intent intent = new Intent(context, PublishedActivity.class);
		intent.putExtra("groupId", groupId);
		intent.putExtra("dynamic",dynamic);
		context.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageGridAdapter.selectPicPaths.clear();
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
//				OutputStream os=openFileOutput("file-publishio.txt",Context.MODE_PRIVATE);
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
//			try{
//				OutputStream os=openFileOutput("file-publishio.txt",Context.MODE_PRIVATE);
//				String str=editText.getText().toString();
//				os.write(str.getBytes("utf-8"));
//				os.close();
//			}catch(Exception e){
//
//			}
		}
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
//			InputStream is = openFileInput("file-publishio.txt");
//			byte[] buffer = new byte[100];
//			int byteLength = is.read(buffer);
//			String str2 = new String(buffer, 0, byteLength, "utf-8");
//			editText.setText(str2.toString());
//			is.close();
//		}  catch (Exception e) {
//		}
	}
	@Override
	public void onTakePic() {
		photoPath = CommonUtil.getSDPath() + "/"
				+ Calendar.getInstance().getTimeInMillis() + ".jpg";
		startActivityForResult(ImageUtil.getTakePicIntent(photoPath),
				TAKE_PICTURE);
	}

	@Override
	public void onSelectFromLocal() {
		ImageGridActivity
				.actionIntent(PublishedActivity.this, SELECT_LOCAL_PIC);
	}
}
