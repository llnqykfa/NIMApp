package com.nzy.nim.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.nzy.nim.R;


public class SelectedPicPopupWindow extends PopupWindow implements
		OnClickListener {

	private Button takePic, selectLocalPic, cancle;
	private OnChoosePicListener listener;

	public void setOnChoosePicListener(OnChoosePicListener listener) {
		this.listener = listener;
	}

	public interface OnChoosePicListener {
		void onTakePic();

		void onSelectFromLocal();
	}

	@SuppressLint("ClickableViewAccessibility")
	@SuppressWarnings("deprecation")
	public SelectedPicPopupWindow(Context mContext, View parent) {
		View view = View.inflate(mContext, R.layout.pop_select_pic, null);
		takePic = (Button) view.findViewById(R.id.pop_select_pic_take);
		selectLocalPic = (Button) view.findViewById(R.id.pop_select_pic_local);
		cancle = (Button) view.findViewById(R.id.pop_select_pic_cancle);
		takePic.setOnClickListener(this);
		selectLocalPic.setOnClickListener(this);
		cancle.setOnClickListener(this);

		this.setAnimationStyle(android.R.style.Animation_InputMethod);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		setContentView(view);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dismiss();
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.pop_select_pic_cancle) {
			dismiss();
			return;
		}
		if (id == R.id.pop_select_pic_take && listener != null) {
			listener.onTakePic();
			dismiss();
			return;
		}
		if (id == R.id.pop_select_pic_local && listener != null) {
			listener.onSelectFromLocal();
			dismiss();
			return;
		}
	}
}
