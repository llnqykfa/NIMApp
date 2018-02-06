package com.nzy.nim.pulltorefresh;

/**
 * 下拉刷新和上拉加载更多的状态接口
 * 
 * @author 张全艺
 * @since 2015-1-23
 */
public interface ILoadingLayout {
	/**
	 * 当前的状态
	 */
	public enum State {

		/**
		 * 初始状态，什么也没有操作
		 */
		NONE,

		/**
		 * 用户下拉后没有操作
		 */
		RESET,

		/**
		 * 用户下拉操作，还没松开时的状态
		 */
		PULL_TO_REFRESH,

		/**
		 * 用户下拉到底后，松开手时的状态
		 */
		RELEASE_TO_REFRESH,

		/**
		 * 正在刷新
		 */
		REFRESHING,

		/**
		 * 正在加载
		 */
		@Deprecated
		LOADING,

		/**
		 * 没有更多刷具
		 */
		NO_MORE_DATA,
	}

	/**
	 * 设置当前状态，派生类应该根据这个状态的变化来改变View的变化
	 * 
	 * @param state
	 *            状态
	 */
	public void setState(State state);

	/**
	 * 得到当前的状态
	 * 
	 * @return 状态
	 */
	public State getState();

	/**
	 * 得到当前Layout的内容大小，它将作为一个刷新的临界点
	 * 
	 * @return 高度
	 */
	public int getContentSize();

	/**
	 * 在拉动时调用
	 * 
	 * @param scale
	 *            拉动的比例
	 */
	public void onPull(float scale);
}
