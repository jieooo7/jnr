package com.jinr.core.base;

import com.jinr.core.MainActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.security.lockpanttern.pattern.UnlockGesturePasswordActivity;
import com.jinr.core.utils.MyLog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * Base2Activity.java description:activity 基类 主要是对activity的管理
 * 
 * @author Andrew Lee version 2014-10-16 上午8:41:34
 */
public abstract class Base2Activity extends Activity {
	public static final String TAG = "Base2Activity";

	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加到Activity栈中
		AppManager.getAppManager().addActivity(this);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	public void onBackPressed() {
		// scrollToFinishActivity();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
		if(MainActivity.homekey){//如果在监听到home键的情况下进入，则启用图形锁
			MainActivity.instance.gotoLockPattern();
			MainActivity.homekey = false;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 自定义返回键的效果
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MyLog.i("--------", "--------------");
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回键
			finish();
			// overridePendingTransition(R.anim.translate_horizontal_finish_in,
			// R.anim.translate_horizontal_finish_out);
		}

		return true;
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
