/*
 * BindCardActivity.java
 * @author Andrew Lee
 * 2014-10-22 上午10:27:06
 */
package com.jinr.core.bankCard;

import java.io.UnsupportedEncodingException;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;

import org.apache.http.Header;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.bankCard.banklist.main.BankList;
import com.jinr.core.bankCard.citylist.main.CityList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.RealInfoActivity;
import com.jinr.core.security.RealNameActivity;
import com.jinr.core.trade.getCash.GetCashFirstActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * BindCardActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-22 上午10:27:06
 */
public class BindCardActivity extends Base2Activity implements OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private TextView bank_select_tv; // 银行文本
	private TextView city_select_tv; // 城市文本

	private TextView name_et;
	private EditText card_no_et;
	private EditText open_bank_et;

	private String user_id = "";

	private RelativeLayout bank_select_rl; // 选择银行
	private RelativeLayout city_select_rl; // 选择城市

	private Button submit;

	private String bank_select = "";
	private String city_select = "";

	private String bank_select_int = "-1";
	private String city_select_int = "";

	private static String cityName = "厦门";// 存放城市名
	private static String bankName = "中国工商银行";// 存放银行名
	private static String cityNum = "350200";// 存放城市编号
	private static String bankNum = "1";// 存放银行编号

	private String name;

	private boolean is_add = false;

	private LoadingDialog loadingdialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_bank_card);

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

		name = PreferencesUtils.getValueFromSPMap(BindCardActivity.this,
				PreferencesUtils.Keys.NAME);
		name = TextAdjustUtil.getInstance().nameAdjust(name);
		bank_select_int = cityNum;
		city_select_int = bankNum;
		is_add = getIntent().getExtras().getBoolean("is_add");
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

		bank_select_tv = (TextView) findViewById(R.id.bind_select_bank_tv);
		city_select_tv = (TextView) findViewById(R.id.bind_bank_city_tv);

		name_et = (TextView) findViewById(R.id.bind_bank_et1);
		card_no_et = (EditText) findViewById(R.id.bind_bank_et2);
		open_bank_et = (EditText) findViewById(R.id.bind_bank_et3);

		bank_select_rl = (RelativeLayout) findViewById(R.id.bind_bank_rl1);
		city_select_rl = (RelativeLayout) findViewById(R.id.bind_bank_rl2);

		submit = (Button) findViewById(R.id.bind_bank_submit);
	}

	/*
	 * (non-Javadoc) 2014年10月28日，被fym修改
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.bind_bank_card));
		city_select_tv.setText(cityName);
		bank_select_tv.setText(bankName);
		name_et.setText(name);
		MyWatcher myWatcher = new MyWatcher();
		card_no_et.addTextChangedListener(myWatcher);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		bank_select_rl.setOnClickListener(this);
		city_select_rl.setOnClickListener(this);
		submit.setOnClickListener(this);
		title_left_img.setOnClickListener(this);
	}

	protected void submit() {
		String card_no = card_no_et.getText().toString().trim();
		String open_bank = open_bank_et.getText().toString().trim();
		MyLog.i("卡号", ""+card_no.length());

		if ("-1".equals(bank_select_int)) {
			ToastUtil.show(this,
					getResources().getString(R.string.open_bank_checked));
		} else {
			if (card_no.length() < 18||card_no.length() > 23 ) {
				ToastUtil.show(this,
						getResources().getString(R.string.bank_card_checked));
			} else {
				if ("-1".equals(city_select_int)) {
					ToastUtil.show(this,
							getResources()
									.getString(R.string.bank_city_checked));
				} else {

					if ("".equals(open_bank)
							|| !TextAdjustUtil.getInstance().onlyChinese(
									open_bank)) {

						ToastUtil.show(
								this,
								getResources().getString(
										R.string.bank_branch_checked));
					} else {
						loadingdialog.show();

						// 逻辑待处理
						send(user_id, bankNum, card_no, cityNum, open_bank);

					}
				}
			}}
//		}

	}

	protected void send(String user_id, String bank_num, String bank_no,
			String zone_id, String open_bank) {
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		// params.put("checksms", code);
		params.put("bank_num", bank_num);
		params.put("bank_no", bank_no);
		params.put("zone_id", zone_id);

		params.put("open_bank", open_bank);

		MyhttpClient.post(UrlConfig.BIND_CARD, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						MyLog.i("Statuscode", "" + arg0);
						loadingdialog.dismiss();
						ToastUtil.show(BindCardActivity.this, getResources()
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
							MyLog.i("绑定银行卡返回", response);
							BaseModel<Integer> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<Integer>>() {
									}.getType());
							if (result.isSuccess()) {
								// PreferencesUtils.putValueToSPMap(RegisterActivity.this,
								// PreferencesUtils.Keys.UID,
								// ""+result.getData());
								// PreferencesUtils.putBooleanToSPMap(RegisterActivity.this,
								// PreferencesUtils.Keys.IS_LOGIN,
								// true);不要设置参数,以服务器为准
								// PreferencesUtils.putBooleanToSPMap(BindCardActivity.this,
								// PreferencesUtils.Keys.HAS_CARD, true);
								loadingdialog.show();

								sendBank();
								// if(is_add){
								//
								// // Intent intent = new
								// Intent(BindCardActivity.this,
								// // GetCashFirstActivity.class);
								// //
								// // startActivity(intent);
								// finish();
								//
								// }else{
								//
								// finish();
								// }
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

	protected void sendBank() {
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("user_id", user_id);
		// params.put("passwords", submit_password);

		MyhttpClient.get(UrlConfig.BANK_CARD_INFO, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(BindCardActivity.this, getResources()
								.getString(R.string.default_error));
						finish();

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							response = response.substring(response.indexOf("{"));
							MyLog.i("银行卡信息返回", response);
							BaseListModel<BankCardInfo> result = new Gson()
									.fromJson(
											response,
											new TypeToken<BaseListModel<BankCardInfo>>() {
											}.getType());
							if (result.isSuccess()) {
								PreferencesUtils.putValueToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.BANK_CARD_ID,
										result.getData().get(0).getId());
								PreferencesUtils.putValueToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.BANK_CARD_NO,
										result.getData().get(0).getBank_no());
								// PreferencesUtils.putValueToSPMap(ManageCardActivity.this,
								// PreferencesUtils.Keys.BANK_CARD_NUM,
								// result.getData().get(0).getBank_num());
								PreferencesUtils.putValueToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.BANK_NAME, result
												.getData().get(0)
												.getBank_name());
								PreferencesUtils.putValueToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.OPEN_BANK, result
												.getData().get(0)
												.getOpen_bank());
								PreferencesUtils.putValueToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.BANK_STATUS,
										result.getData().get(0)
												.getBank_status());
								PreferencesUtils
										.putValueToSPMap(
												BindCardActivity.this,
												PreferencesUtils.Keys.BANK_CITY,
												result.getData().get(0)
														.getZone_shi());

								PreferencesUtils.putBooleanToSPMap(
										BindCardActivity.this,
										PreferencesUtils.Keys.HAS_CARD, true);
								ToastUtil.show(getApplication(), getResources()
										.getString(R.string.bind_bank_success));

								finish();
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

							} else {

								ToastUtil.show(getApplication(),
										result.getMsg());
								finish();
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

		case R.id.bind_bank_rl1:// 银行选择
			Intent intentBankList = new Intent();
			intentBankList.setComponent(new ComponentName("com.jinr.core",
					"com.jinr.core.bankCard.banklist.main.BankList"));
			intentBankList.putExtra("name", name_et.getText().toString());
			intentBankList.putExtra("card_no", card_no_et.getText().toString());
			intentBankList.putExtra("open_bank", open_bank_et.getText()
					.toString());
			intentBankList.setClass(BindCardActivity.this, BankList.class);
			startActivityForResult(intentBankList, 0);
			break;
		case R.id.bind_bank_rl2:// 城市选择
			Intent intentCityList = new Intent();
			intentCityList.setComponent(new ComponentName("com.jinr.core",
					"com.jinr.core.bankCard.citylist.main.CityList"));
			intentCityList.setAction(Intent.ACTION_VIEW);
			intentCityList.putExtra("name", name_et.getText().toString());
			intentCityList.putExtra("card_no", card_no_et.getText().toString());
			intentCityList.putExtra("open_bank", open_bank_et.getText()
					.toString());
			startActivityForResult(intentCityList, 0);
			break;
		case R.id.bind_bank_submit:
			// if(Check.is_identify(BindCardActivity.this)){
			if (Check.is_identify(BindCardActivity.this)) {// 实名认证过的,登录时有返回姓名

				submit();
			} else {

				Intent intent_real = new Intent();
				intent_real.setClass(BindCardActivity.this,
						RealNameActivity.class);
				startActivity(intent_real);

				// 实名认证
			}

			break;
		// case R.id.manage_bank_rl2:
		// Intent intent = new Intent(ManageCardActivity.this,
		// BindCardActivity.class);
		// startActivity(intent);
		// break;
		// case R.id.manage_bank_rl1:
		// Intent intent_detail = new Intent(ManageCardActivity.this,
		// CardDetailActivity.class);
		// startActivity(intent_detail);
		// break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (data.getStringExtra("cityName") != null) {
				cityName = data.getStringExtra("cityName");
				cityNum = data.getStringExtra("cityNum");
			}

			if (data.getStringExtra("bankName") != null) {
				bankName = data.getStringExtra("bankName");
				bankNum = data.getStringExtra("bankNum");
			}
			name_et.setText(data.getStringExtra("name"));
			card_no_et.setText(data.getStringExtra("card_no"));
			open_bank_et.setText(data.getStringExtra("open_bank"));

			if (cityName != null)
				city_select_tv.setText(cityName);

			if (bankName != null)
				bank_select_tv.setText(bankName);
		}
	}

	class MyWatcher implements TextWatcher {
		int beforeTextLength = 0;
		int onTextLength = 0;
		boolean isChanged = false;

		int location = 0;// 记录光标的位置
		private char[] tempChar;
		private StringBuffer buffer = new StringBuffer();
		int konggeNumberB = 0;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			onTextLength = s.length();
			buffer.append(s.toString());
			if (onTextLength == beforeTextLength || onTextLength <= 3
					|| isChanged) {
				isChanged = false;
				return;
			}
			isChanged = true;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			beforeTextLength = s.length();
			if (buffer.length() > 0) {
				buffer.delete(0, buffer.length());
			}
			konggeNumberB = 0;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ' ') {
					konggeNumberB++;
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (isChanged) {
				location = card_no_et.getSelectionEnd();
				int index = 0;
				while (index < buffer.length()) {
					if (buffer.charAt(index) == ' ') {
						buffer.deleteCharAt(index);
					} else {
						index++;
					}
				}

				index = 0;
				int konggeNumberC = 0;
				while (index < buffer.length()) {
					if ((index == 4 || index == 9 || index == 14 || index == 19)) {
						buffer.insert(index, ' ');
						konggeNumberC++;
					}
					index++;
				}

				if (konggeNumberC > konggeNumberB) {
					location += (konggeNumberC - konggeNumberB);
				}

				tempChar = new char[buffer.length()];
				buffer.getChars(0, buffer.length(), tempChar, 0);
				String str = buffer.toString();
				if (location > str.length()) {
					location = str.length();
				} else if (location < 0) {
					location = 0;
				}

				card_no_et.setText(str);
				Editable etable = card_no_et.getText();
				Selection.setSelection(etable, location);
				isChanged = false;
			}
		}

	}

}
