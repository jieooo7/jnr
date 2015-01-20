package com.jinr.core.base;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {
	
	/**
	 * 
	 * 描述：数据初始化
	 */
	protected abstract void initData();
	/**
	 * 
	 * 描述：渲染界面
	 */
	protected abstract void findViewById(View view);
	/**
	 * 
	 * 描述：界面初始化
	 * @param view
	 */
	protected abstract void initUI();
	/**
	 * 
	 * 描述：设置监听
	 */
	protected abstract void setListener();
}

