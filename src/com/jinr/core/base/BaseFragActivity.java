package com.jinr.core.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jinr.core.config.AppManager;
import com.umeng.analytics.MobclickAgent;


public abstract class BaseFragActivity extends FragmentActivity {
	public static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		Log.d("BaseActivity", "BaseActivity");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}
	/**
	 * 
	 * 描述：数据初始化
	 */
	protected abstract void initData();

	/**
	 * 
	 * 描述：渲染界面
	 */
	protected abstract void findViewById();

	/**
	 * 
	 * 描述：界面初始化
	 */
	protected abstract void initUI();

	/**
	 * 
	 * 描述：设置监听
	 */
	protected abstract void setListener();

}
