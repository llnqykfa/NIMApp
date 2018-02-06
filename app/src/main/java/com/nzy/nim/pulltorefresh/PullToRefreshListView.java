package com.nzy.nim.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListView;

import com.nzy.nim.pulltorefresh.ILoadingLayout.State;

/**
 * 这个类实现了ListView下拉刷新，上加载更多和滑到底部自动加载
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class PullToRefreshListView extends PullToRefreshBase<ListView>
		implements OnScrollListener {
	// 可刷新的ListView
	private ListView mListView;
	// 用于滑到底部自动加载的Footer
	private LoadingLayout mLoadMoreFooterLayout;
	// 滚动的监听器
	private OnScrollListener mScrollListener;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshListView(Context context) {
		this(context, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public PullToRefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 * @param defStyle
	 *            defStyle
	 */
	public PullToRefreshListView(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);

		setPullLoadEnabled(false);
	}

	/**
	 * 创建可刷新的View
	 */
	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView listView = new ListView(context);
		mListView = listView;
		// 设置监听
		listView.setOnScrollListener(this);

		return listView;
	}

	/**
	 * 设置是否有更多数据的标志
	 * 
	 * @param hasMoreData
	 *            true表示还有更多的数据，false表示没有更多数据了
	 */
	public void setHasMoreData(boolean hasMoreData) {
		if (!hasMoreData) {
			// 如果加载更所数据的布局不为空，则状态设置为没有数据
			if (null != mLoadMoreFooterLayout) {
				mLoadMoreFooterLayout.setState(State.NO_MORE_DATA);
			}
			// footer布局同样设置
			LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
			if (null != footerLoadingLayout) {
				footerLoadingLayout.setState(State.NO_MORE_DATA);
			}
		}

	}

	/**
	 * 设置滑动的监听器
	 * 
	 *            监听器
	 */
	public void setOnScrollListener(OnScrollListener listerer) {
		mScrollListener = listerer;
	}

	/**
	 * 是否可以上拉
	 */
	@Override
	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	/**
	 * 是否可以开始下拉
	 */
	@Override
	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	/**
	 * 开始加载
	 */
	@Override
	protected void startLoading() {
		super.startLoading();
		// 如果布局存在，设置为正在刷新
		if (mLoadMoreFooterLayout != null) {
			mLoadMoreFooterLayout.setState(State.REFRESHING);
		}
	}

	/**
	 * 上拉结束
	 */
	@Override
	public void onPullUpRefreshComplete() {
		super.onPullUpRefreshComplete();

		if (null != mLoadMoreFooterLayout) {
			// 重置状态
			mLoadMoreFooterLayout.setState(State.RESET);
		}
	}
/**
 * 设置上拉加载可实现
 */
	@Override
	public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
		super.setScrollLoadEnabled(scrollLoadEnabled);

		if (scrollLoadEnabled) {
			// 设置Footer
			if (mLoadMoreFooterLayout == null) {
				mLoadMoreFooterLayout = new FooterLoadingLayout(getContext());
				// mListView.addFooterView(mLoadMoreFooterLayout, null, false);
			}

			// if (null == mLoadMoreFooterLayout.getParent()) {
			// // mListView.addFooterView(mLoadMoreFooterLayout, null, false);
			// }
			mLoadMoreFooterLayout.show(true);
		} else {
			if (mLoadMoreFooterLayout != null) {
				mLoadMoreFooterLayout.show(false);
			}
		}
	}

	@Override
	public LoadingLayout getFooterLoadingLayout() {
		if (isScrollLoadEnabled()) {
			return mLoadMoreFooterLayout;
		}

		return super.getFooterLoadingLayout();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (isScrollLoadEnabled() && hasMoreData()) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_FLING) {
				if (isReadyForPullUp()) {
					startLoading();
				}
			}
		}

		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	@Override
	protected LoadingLayout createHeaderLoadingLayout(Context context,
			AttributeSet attrs) {
		// return new RotateLoadingLayout(context);
		return new HeaderLoadingLayout(context);
	}

	/**
	 * 表示是否还有更多数据
	 * 
	 * @return true表示还有更多数据
	 */
	private boolean hasMoreData() {
		if ((null != mLoadMoreFooterLayout)
				&& (mLoadMoreFooterLayout.getState() == State.NO_MORE_DATA)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断第一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isFirstItemVisible() {
		final Adapter adapter = mListView.getAdapter();

		if (adapter == null || adapter.isEmpty()) {
			return true;
		}

		int mostTop = (mListView.getChildCount() > 0) ? mListView.getChildAt(0)
				.getTop() : 0;
		if (mostTop >= 0) {
			return true;
		}

		return false;
	}

	/**
	 * 判断最后一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isLastItemVisible() {
		final Adapter adapter = mListView.getAdapter();

		if (adapter == null || adapter.isEmpty()) {
			return true;
		}

		final int lastItemPosition = adapter.getCount() - 1;
		final int lastVisiblePosition = mListView.getLastVisiblePosition();
		/*
		 * 如果lastVisiblePosition
		 * ==lastItemPosition，但是GridView在内部使用一个FooterView，导致position混乱了，
		 * 我们在这里减去1和依赖于内部条件得到的getButtom的值
		 */
		if (lastVisiblePosition >= lastItemPosition - 1) {
			//得到当前页的所有Item
			final int childIndex = lastVisiblePosition
					- mListView.getFirstVisiblePosition();
			//获取Item个数
			final int childCount = mListView.getChildCount();
			final int index = Math.min(childIndex, childCount - 1);
			final View lastVisibleChild = mListView.getChildAt(index);
			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= mListView.getBottom();
			}
		}
		// 这里可能会出错

		if (adapter.getCount() == 0) {
			// 没有item的时候也可以上拉加载
			return true;
		} else if (mListView.getLastVisiblePosition() == (adapter.getCount() - 1)) {
			// 滑到底部了
			if (getChildAt(mListView.getLastVisiblePosition()
					- mListView.getFirstVisiblePosition()) != null
					&& getChildAt(
							mListView.getLastVisiblePosition()
									- mListView.getFirstVisiblePosition())
							.getBottom() <= getMeasuredHeight())
				return true;
		}
		// return false;

		return false;
	}

}
