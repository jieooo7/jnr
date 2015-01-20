/*
 * CardDetailActivity.java
 * @author Andrew Lee
 * 2014-10-22 上午10:26:04
 */
package com.jinr.core.bankCard;

import java.io.UnsupportedEncodingException;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.trade.getCash.GetCashFirstActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * CardDetailActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-22 上午10:26:04
 */
public class CardDetailActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private TextView name_tv;
	private TextView bank_tv;
	private TextView bank_no_tv;
	private TextView city_tv;
	private TextView open_bank_tv;

	private String bank_card_id;
	private String name;
	private String bank;
	private String bank_no;
	private String city;
	private String open_bank;

	private boolean is_add = false;

	private Button submit;

	private LoadingDialog loadingdialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_card_detail);

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
		is_add = getIntent().getExtras().getBoolean("is_add");
		name = PreferencesUtils.getValueFromSPMap(CardDetailActivity.this,
				PreferencesUtils.Keys.NAME);

		bank = PreferencesUtils.getValueFromSPMap(CardDetailActivity.this,
				PreferencesUtils.Keys.BANK_NAME);
		city = PreferencesUtils.getValueFromSPMap(CardDetailActivity.this,
				PreferencesUtils.Keys.BANK_CITY);
		bank_no = PreferencesUtils.getValueFromSPMap(CardDetailActivity.this,
				PreferencesUtils.Keys.BANK_CARD_NO);
		open_bank = PreferencesUtils.getValueFromSPMap(CardDetailActivity.this,
				PreferencesUtils.Keys.OPEN_BANK);
		bank_card_id = PreferencesUtils.getValueFromSPMap(
				CardDetailActivity.this, PreferencesUtils.Keys.BANK_CARD_ID);

		if ("".equals(bank_no) == false) {

			bank_no = "**** **** **** "
					+ bank_no.substring(bank_no.length() - 4);
		}

		if ("".equals(name) == false) {
			if (name.length() == 2 || name.length() == 1) {
				name = name.substring(0, 1) + "*";
			} else {
				int length = name.length();
				name = name.substring(0, 1) + "*"
						+ name.substring(length - 1, length);
			}

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

		name_tv = (TextView) findViewById(R.id.bank_detail_tv1);
		bank_tv = (TextView) findViewById(R.id.bank_detail_tv2);
		bank_no_tv = (TextView) findViewById(R.id.bank_detail_tv3);
		city_tv = (TextView) findViewById(R.id.bank_detail_tv4);
		open_bank_tv = (TextView) findViewById(R.id.bank_detail_tv5);

		submit = (Button) findViewById(R.id.bank_detail_submit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources()
				.getString(R.string.card_detail));
		name_tv.setText(name);
		bank_tv.setText(bank);
		bank_no_tv.setText(bank_no);
		city_tv.setText(city);
		open_bank_tv.setText(open_bank);
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

		case R.id.bank_detail_submit:// 解除绑定按钮
			// submit();

			Intent intent_bind = new Intent(CardDetailActivity.this,
					BindCardActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("is_add", is_add);

			intent_bind.putExtras(bundle);

			startActivity(intent_bind);

			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-23 上午9:48:24
	 */
	private void submit() {
		// TODO Auto-generated method stub
		loadingdialog.show();
		send();
	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-23 上午9:49:06
	 */
	private void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("bank_id", bank_card_id);
		MyLog.i("银行卡id", bank_card_id);
		// params.put("passwords", submit_password);

		MyhttpClient.get(UrlConfig.REMOVE_BIND_CARD, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(CardDetailActivity.this, getResources()
								.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							response = response.substring(response.indexOf("{"));
							MyLog.i("解除绑定信息返回", response);
							BaseModel<Integer> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<Integer>>() {
									}.getType());
							if (result.isSuccess()) {
								PreferencesUtils.putBooleanToSPMap(
										CardDetailActivity.this,
										PreferencesUtils.Keys.HAS_CARD, false);
								ToastUtil
										.show(getApplication(),
												getResources()
														.getString(
																R.string.card_detail_remove));

								Intent intent_bind = new Intent(
										CardDetailActivity.this,
										BindCardActivity.class);
								Bundle bundle = new Bundle();
								bundle.putBoolean("is_add", is_add);

								intent_bind.putExtras(bundle);

								startActivity(intent_bind);

								finish();

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

}
