package com.nzy.nim.view;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

/**
 * @author LIUBO
 * @date 2015-4-15下午8:36:41
 * @TODO 浮动按钮 (默认靠右)
 */
public class FloatingView extends View {
	private Context mContext;
	private WindowManager mWindowManager; // WindowManager
	private OnCtrlViewTouchListener mCtrlViewTouchListener;
	private View mCtrlView;
	private OnFloatBtnClickListener listener;
	private int offSetY;
	private int offSetX;

	public interface OnFloatBtnClickListener {
		void onClick();
	}

	public void setFloatBtnClickListener(OnFloatBtnClickListener listener) {
		this.listener = listener;
	}

	public FloatingView(Context context) {
		super(context);
		mContext = context;
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
	}

	public void setView(View view, int offSetx, int offSety) {
		offSetY = offSety;
		offSetX = offSetx;
		mCtrlView = view;
		mCtrlViewTouchListener = new OnCtrlViewTouchListener(mWindowManager,
				showCtrlView(mWindowManager, offSetx, offSety, Gravity.RIGHT
						| Gravity.TOP));
		mCtrlView.setOnTouchListener(mCtrlViewTouchListener);

	}

	public void setView(View view, int grivaty, int offSetx, int offSety) {
		mCtrlView = view;
		mCtrlViewTouchListener = new OnCtrlViewTouchListener(mWindowManager,
				showCtrlView(mWindowManager, offSetx, offSety, grivaty));
		mCtrlView.setOnTouchListener(mCtrlViewTouchListener);
	}

	private class OnCtrlViewTouchListener implements OnTouchListener {
		private static final long MAX_MILLI_TREAT_AS_CLICK = 100; // 当用户触控控制按钮的时间小于该常量毫秒时，就算控制按钮的位置没发生了变化，也认为这是一次点击事件
		private WindowManager mWindowManager;
		private WindowManager.LayoutParams mLayoutParams;
		// 触屏监听
		float mLastX, mLastY;
		int mOldOffsetX, mOldOffsetY;
		int mRecordFlag = 0; // 用于重新记录CtrlView位置的标志
		long mTouchDur; // 记录用户触控控制按钮的时间

		public OnCtrlViewTouchListener(WindowManager windowManager,
				WindowManager.LayoutParams layoutParams) {
			mWindowManager = windowManager;
			mLayoutParams = layoutParams;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();
			float x = event.getX();
			float y = event.getY();
			// 初始化原始偏移量
			if (mRecordFlag == 0) {
				mOldOffsetX = mLayoutParams.x; // 偏移量
				mOldOffsetY = mLayoutParams.y; // 偏移量
			}

			if (action == MotionEvent.ACTION_DOWN) {// 按下按钮
				mLastX = x;
				mLastY = y;
				mTouchDur = System.currentTimeMillis();

			} else if (action == MotionEvent.ACTION_MOVE) {// 移动按钮
				mLayoutParams.x -= (int) (x - mLastX); // 偏移量
				mLayoutParams.y += (int) (y - mLastY); // 偏移量

				mRecordFlag = 1;
				mWindowManager.updateViewLayout(mCtrlView, mLayoutParams);
			}

			else if (action == MotionEvent.ACTION_UP) {// 释放按钮
				mTouchDur = System.currentTimeMillis() - mTouchDur;
				int newOffsetX = mLayoutParams.x;
				int newOffsetY = mLayoutParams.y;
				// 根据用户的操作判断是否是点击还是移动
				if (mTouchDur < MAX_MILLI_TREAT_AS_CLICK
						|| (mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY)) {
					// 点击
					if (listener != null) {
						listener.onClick();
					}
				} else {
					mRecordFlag = 0;
					mLayoutParams.x = offSetX;
					mLayoutParams.y = offSetY;
					mWindowManager.updateViewLayout(mCtrlView, mLayoutParams);
				}
			}
			return true;
		}
	}

	/**
	 * 用于显示控制按钮，该控制按钮可以移动。当点击一次之后会弹出另一个浮动界面，本例中是详情界面
	 * 
	 * @param windowManager
	 *            用于控制通话状态标示出现的初始位置、大小以及属性
	 * @return 会返回所创建的通话状态标示的WindowManager.LayoutParams型对象，该对象会在移动过程中修改。
	 */
	private WindowManager.LayoutParams showCtrlView(
			WindowManager windowManager, int x, int y, int gravity) {
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = 1002; // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
		layoutParams.flags = 40;// 这句设置桌面可控
		layoutParams.gravity = gravity;
		layoutParams.x = x;
		layoutParams.y = y;
		layoutParams.width = LayoutParams.WRAP_CONTENT;
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		layoutParams.format = -3; // 透明
		try {
			windowManager.addView(mCtrlView, layoutParams);// 这句是重点
			// 给WindowManager中丢入刚才设置的值
			// 只有addview后才能显示到页面上去。
			// 注册到WindowManager win是要刚才随便载入的layout，
			// wmParams是刚才设置的WindowManager参数集
			// 效果是将win注册到WindowManager中并且它的参数是wmParams中设置饿
		}catch (Exception e){
			e.fillInStackTrace();
		}
		return layoutParams;
	}
}
