/*
 * TradeFinishActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:34:14
 */
package com.jinr.core.trade.getCash;

import java.util.ConcurrentModificationException;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.AppManager;
import com.jinr.core.trade.FragmentIndex;
import com.jinr.core.trade.purchase.PurchaseFirstActivity;
import com.jinr.core.trade.purchase.PurchaseSecondActivity;
import com.jinr.core.trade.record.TradeRecordActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;

/**
 * TradeFinishActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-25 上午10:34:14
 */
public class TradeFinishActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private TextView show_info_tv;

	private Button record_bt;
	private Button back_bt;

	private String show_info;
	private String title;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_cash_finish);

		initData();
		findViewById();
		initUI();
		setListener();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initData()
	 */
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		if (getIntent().getExtras().getBoolean("get_cash")) {
			title = getResources().getString(R.string.get_cash_success);
			show_info = getIntent().getExtras().getString("show_info");// 展示不同的信息
//			try{
				
//			AppManager.getAppManager().finishActivity(GetCashFirstActivity.class);
//			AppManager.getAppManager().finishActivity(GetCashSecondActivity.class);
//			
//			}catch (ConcurrentModificationException e){
//				MyLog.i("stack异常", "stack异常");
//			}
		} else {
			title = getResources().getString(R.string.purchase_success);
			show_info =getIntent().getExtras().getString("show_info");
//			try{
				
//			AppManager.getAppManager().finishActivity(PurchaseSecondActivity.class);
//			AppManager.getAppManager().finishActivity(PurchaseFirstActivity.class);
//			}catch (ConcurrentModificationException e){
//				MyLog.i("stack异常", "stack异常");
//			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#findViewById()
	 */
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		show_info_tv = (TextView) findViewById(R.id.get_cash_finish_tv1);
		record_bt = (Button) findViewById(R.id.get_cash_finish_record);
		back_bt = (Button) findViewById(R.id.get_cash_finish_back);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(title);
		show_info_tv.setText(show_info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
		record_bt.setOnClickListener(this);
		back_bt.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.title_left_img:// 左侧图标
			
			
			finish();

			break;
		case R.id.get_cash_finish_back:// 首页
			Intent intent_back = new Intent(TradeFinishActivity.this,
					MainActivity.class);
			MainActivity.isLock_longin = false;//通过登陆成功防止重新加载产生图形解锁的办法和相同的变量
			FragmentIndex.is_product = true;
			startActivity(intent_back);
			finish();
			break;
		case R.id.get_cash_finish_record:
			Intent intent_record = new Intent(TradeFinishActivity.this,
					TradeRecordActivity.class);
			FragmentIndex.is_product = true;
			startActivity(intent_record);
			finish();
			break;

		default:
			break;
		}
	}

}
