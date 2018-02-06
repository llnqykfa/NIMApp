package com.nzy.nim.activity.base;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	protected boolean isVisible;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		this.getUserVisibleHint();
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInVisible();
		}
	}

	private void onInVisible() {

	}

	protected void onVisible() {
		lazyLoad();
	}

	protected abstract void lazyLoad();

}
