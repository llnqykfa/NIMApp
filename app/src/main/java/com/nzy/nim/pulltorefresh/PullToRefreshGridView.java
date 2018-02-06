package com.nzy.nim.pulltorefresh;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.GridView;
import com.nzy.nim.pulltorefresh.ILoadingLayout.State;

/**
 * 这个类实现了GridView下拉刷新，上加载更多和滑到底部自动加载
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public class PullToRefreshGridView extends PullToRefreshBase<GridView>
		implements OnScrollListener {

	private GridView mGridView;//

	private LoadingLayout mFooterLayout;// 用于滑到底部自动加载的Footer

	private OnScrollListener mScrollListener;// 滚动的监听器

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshGridView(Context context) {
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
	public PullToRefreshGridView(Context context, AttributeSet attrs) {
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
	public PullToRefreshGridView(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);
		setPullLoadEnabled(false);
	}

	/**
	 * 创建可刷新的GridView对象
	 */
	@Override
	protected GridView createRefreshableView(Context context, AttributeSet attrs) {
		GridView gridView = new GridView(context);
		mGridView = gridView;
		gridView.setOnScrollListener(this);

		return gridView;
	}

	/**
	 * 设置是否有更多数据的标志
	 * 
	 * @param hasMoreData
	 *            true表示还有更多的数据，false表示没有更多数据了
	 */
	public void setHasMoreData(boolean hasMoreData) {
		if (mFooterLayout != null) {
			if (!hasMoreData) {
				mFooterLayout.setState(State.NO_MORE_DATA);
			}
		}
	}

	/**
	 * 设置滑动的监听器
	 * 
	 * @param l
	 *            监听器
	 */
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	@Override
	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	@Override
	protected void startLoading() {
		super.startLoading();

		if (mFooterLayout != null) {
			mFooterLayout.setState(State.REFRESHING);
		}
	}

	@Override
	public void onPullUpRefreshComplete() {
		super.onPullUpRefreshComplete();

		if (mFooterLayout != null) {
			mFooterLayout.setState(State.RESET);
		}
	}

	/**
	 * 滑动到底部加载更多是否可用
	 */
	@Override
	public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
		super.setScrollLoadEnabled(scrollLoadEnabled);

		if (scrollLoadEnabled) {
			// 设置Footer
			if (mFooterLayout == null) {
				mFooterLayout = new FooterLoadingLayout(getContext());
			}

			// mGridView.removeFooterView(mFooterLayout);
			// mGridView.addFooterView(mFooterLayout, null, false);
		} else {
			if (mFooterLayout != null) {
				// mGridView.removeFooterView(mFooterLayout);
			}
		}
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

	/**
	 * 正在滚动
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	/**
	 * 表示是否还有更多数据
	 * 
	 * @return true表示还有更多数据
	 */
	private boolean hasMoreData() {
		if ((mFooterLayout != null)
				&& (mFooterLayout.getState() == State.NO_MORE_DATA)) {
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
		final Adapter adapter = mGridView.getAdapter();

		if (adapter == null || adapter.isEmpty()) {
			return true;
		}
		// 获取第一个Item的高度
		int mostTop = (mGridView.getChildCount() > 0) ? mGridView.getChildAt(0)
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
		final Adapter adapter = mGridView.getAdapter();

		if (adapter == null || adapter.isEmpty()) {
			return true;
		}
		final int lastItemPosition = adapter.getCount() - 1;
		final int lastVisiblePosition = mGridView.getLastVisiblePosition();

		/*
		 * 如果lastVisiblePosition
		 * ==lastItemPosition，但是GridView在内部使用一个FooterView，导致position混乱了，
		 * 我们在这里减去1和依赖于内部条件得到的getButtom的值
		 */
		if (lastVisiblePosition >= lastItemPosition - 1) {
			final int childIndex = lastVisiblePosition
					- mGridView.getFirstVisiblePosition();
			final int childCount = mGridView.getChildCount();
			final int index = Math.min(childIndex, childCount - 1);
			final View lastVisibleChild = mGridView.getChildAt(index);
			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= mGridView.getBottom();
			}
		}

		return false;
	}
}
