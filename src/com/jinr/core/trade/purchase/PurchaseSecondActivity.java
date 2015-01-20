/*
 * PurchaseSecondActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:36:15
 */
package com.jinr.core.trade.purchase;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.trade.getCash.TradeFinishActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.unionpay.UPPayAssistEx;

/**
 * PurchaseSecondActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-25 上午10:36:15
 */
public class PurchaseSecondActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private TextView have_purchase_tv;
	private Button submit;
	private String user_id = "";
	private String orderNo="";

	private String have_purchase;
	private String ZuHeId;

	private LoadingDialog loadingdialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase_second);

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
		loadingdialog = new LoadingDialog(this);
		user_id = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.UID);

		have_purchase = getIntent().getExtras().getString("purchase");
		ZuHeId = getIntent().getExtras().getString("ZuHeId");
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
		have_purchase_tv = (TextView) findViewById(R.id.purchase_sec_tv1);
		submit = (Button) findViewById(R.id.purchase_sec_bt1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.submit_order));
		have_purchase_tv.setText(have_purchase);
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
		submit.setOnClickListener(this);
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

		case R.id.purchase_sec_bt1:
			// 确认订单 调用第三方支付
			loadingdialog.show();
			send();
			break;

		default:
			break;
		}
	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-27 下午3:42:53
	 */
	private void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("uid", user_id);
		MyLog.i("user_id", user_id);
		params.put("money", have_purchase);
		params.put("zuhe_id", ZuHeId);
		params.put("type", "4");
		// params.put("passwords", submit_password);

		MyhttpClient.post(UrlConfig.ADD_ORDER, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(PurchaseSecondActivity.this,
								getResources()
										.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("订单流水号返回", response);
							response = response.substring(response.indexOf("{"));
							BaseModel<String> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<String>>() {
									}.getType());
							if (result.isSuccess()) {
								// Message msg = Message.obtain();
								// Map<String, String> map = new HashMap<String,
								// String>();
								// String
								// card_no=result.getData().get(0).getBank_no();
								// map.put("bank",
								// result.getData().get(0).getBank_name());
								// map.put("bank_card",
								// TextAdjustUtil.getInstance().bankCardAdjust(card_no));
								//
								// msg.what = MessageWhat.MANAGE_CARD;
								// msg.obj=map;
								// handler.sendMessage(msg);

								orderNo = result.getData();
//								orderNo = "201410281144274645628";

								int ret = UPPayAssistEx.startPay(
										PurchaseSecondActivity.this, null,
										null, orderNo, "00");
								// 如果支付插件未安装，则请求安装支付插件
								if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
									AlertDialog.Builder builer = new AlertDialog.Builder(
											PurchaseSecondActivity.this);
									builer.setTitle("安装提示");
									builer.setMessage("请先安装支付插件");

									builer.setPositiveButton(
											"确认",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (dialog instanceof AlertDialog) {
														UPPayAssistEx
																.installUPPayPlugin(PurchaseSecondActivity.this);
													}
												}
											});
									builer.setNegativeButton(
											"取消",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													if (dialog instanceof AlertDialog) {
													}
												}
											});

									AlertDialog adlg = builer.create();
									adlg.show();
								}

							} else {
								ToastUtil.show(getApplication(),
										result.getMsg());
							}

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							MyLog.i("json解析错误", "解析错误");
						}

					}

				});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 此方法用来监听支付插件返回结果
		
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}

		String str = data.getExtras().getString("pay_result");
		String msg = "";
		if (str.equalsIgnoreCase("success")) {
			msg = "支付成功";
			Intent intent = new Intent(PurchaseSecondActivity.this,
					TradeFinishActivity.class);
			String show_info = getResources().getString(
					R.string.purchase_text_one)
					+ getResources().getString(R.string.purchase_text_two)
					+ have_purchase
					+ "元\n"
					+ getResources().getString(R.string.purchase_text_three)
					+ orderNo;
			Bundle bundle = new Bundle();
			bundle.putString("show_info", show_info);
			bundle.putBoolean("get_cash", false);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "支付已被取消";
		}
		AlertDialog dialog = new AlertDialog(this) {
		};
		dialog.setMessage(msg);
		dialog.show();
	}
}
