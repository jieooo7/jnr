/*
 * GetCashFirstActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:33:21
 */
package com.jinr.core.trade.getCash;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;
import model.GetCash;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jinr.core.bankCard.BindCardActivity;
import com.jinr.core.bankCard.CardDetailActivity;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.Check;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * GetCashFirstActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-25 上午10:33:21
 */
public class GetCashFirstActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private RelativeLayout select_card_rl;// 选择银行卡
	private RelativeLayout the_card_rl;// 银行卡 和卡号

	private TextView the_bank_tv;
	private TextView card_no_tv;
	private TextView can_use_cash_tv;

	private EditText get_cash_et;
	private Button next_bt;

	private String bank;
	private String bank_card;
	private String can_use_money = "";
	private String user_id = "";
	private String input="";

	private LoadingDialog loadingdialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.GET_CASH:
				String cash = (String) msg.obj;
				
				if("".equals(cash)||Double.parseDouble(cash)<=0){
					cash="￥0.00";
				}else{
					cash="￥"+String.format("%.2f", (Double.parseDouble(cash)-0.0049999));
				}
				can_use_cash_tv.setText(cash);// 可用金额
				break;

			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_cash_first);

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
		bank = PreferencesUtils.getValueFromSPMap(GetCashFirstActivity.this,
				PreferencesUtils.Keys.BANK_NAME);
		bank_card = PreferencesUtils.getValueFromSPMap(
				GetCashFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_NO);

		user_id = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.UID);
		loadingdialog.show();
		send();
	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-27 下午7:42:10
	 */
	private void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("uid", user_id);
		// params.put("passwords", submit_password);

		MyhttpClient.get(UrlConfig.GET_MONEY, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(GetCashFirstActivity.this,
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
							response = response.substring(response.indexOf("{"));
							MyLog.i("提现第一步返回", response);
							BaseModel<GetCash> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<GetCash>>() {
									}.getType());
							if (result.isSuccess()) {
								Message msg = Message.obtain();

								can_use_money = ""
										+ result.getData().getSunmoney();
								MyLog.i("can_use_money", can_use_money);
								msg.what = MessageWhat.GET_CASH;
								msg.obj = can_use_money;
								handler.sendMessage(msg);

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

		select_card_rl = (RelativeLayout) findViewById(R.id.get_cash_first_rl1);
		the_card_rl = (RelativeLayout) findViewById(R.id.get_cash_first_rl2);

		the_bank_tv = (TextView) findViewById(R.id.get_cash_first_tv0);
		card_no_tv = (TextView) findViewById(R.id.get_cash_first_card_tv0);
		can_use_cash_tv = (TextView) findViewById(R.id.get_cash_first_tv2);

		get_cash_et = (EditText) findViewById(R.id.get_cash_first_et1);
		next_bt = (Button) findViewById(R.id.get_cash_first_bt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(R.string.turn_out));
		can_use_cash_tv.setText(can_use_money);// 可用金额
		the_bank_tv.setText(bank);// 银行
		card_no_tv.setText(TextAdjustUtil.getInstance().bankCardAdjust(
				bank_card));// 卡号
		
		TextAdjustUtil.getInstance().setPricePoint(get_cash_et);
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
		next_bt.setOnClickListener(this);
		select_card_rl.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// initData();

		if (Check.has_card(GetCashFirstActivity.this)) {
			the_bank_tv.setText(bank);// 银行
			card_no_tv.setText(TextAdjustUtil.getInstance().bankCardAdjust(
					bank_card));// 卡号
			the_card_rl.setVisibility(View.VISIBLE);
		} else {
			the_card_rl.setVisibility(View.GONE);
		}
	}
	
	
	private void sendTips() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("uid", user_id);
		params.put("txmoney", input);
		// params.put("passwords", submit_password);

		MyhttpClient.post(UrlConfig.TIPS_LIMIT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(GetCashFirstActivity.this,
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
							response = response.substring(response.indexOf("{"));
							MyLog.i("提现手续费", response);
							BaseModel<Integer> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<Integer>>() {
									}.getType());
							if (result.isSuccess()) {
								
								int tip=result.getData();
								String money="";
								String tips="";
								
								if(tip==0){
									money=input;
									tips="0.00";
									
								}else{
									money=""+(Double.parseDouble(input)-2.00);
									tips="2.00";
								}
								
								
								Intent intent = new Intent(GetCashFirstActivity.this,
										GetCashSecondActivity.class);
								Bundle bundle=new Bundle();
								bundle.putString("money", money);
								bundle.putString("tips", tips);
								bundle.putString("input", input);
								intent.putExtras(bundle);
								startActivity(intent);
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
		case R.id.get_cash_first_bt:// 下一步
			input=get_cash_et.getText().toString();
			if("".equals(can_use_money)){
				can_use_money="0";
			}
			
			if ("".equals(get_cash_et.getText().toString())) {

				ToastUtil.show(
						this,
						getResources().getString(R.string.please_input)
								+ getResources().getString(
										R.string.get_cash_much));

			} else {
				
				//添加判断可转出金额大小比较
				if(Double.parseDouble(can_use_money)<Double.parseDouble(input)){
					ToastUtil.show(
							this,
							getResources().getString(
											R.string.can_use_enough));
				}else{
					if(Double.parseDouble(input)<=2){
						
						ToastUtil.show(
								this,
								getResources().getString(
												R.string.can_use_limite));
						
					}else{
						
						
						loadingdialog.show();
						sendTips();//请求手续费
					
					
				
					}}
			}

			break;
		case R.id.get_cash_first_rl1:
			if(Check.has_card(GetCashFirstActivity.this)){
				Intent intent_remove = new Intent(GetCashFirstActivity.this,
						CardDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putBoolean("is_add", true);
				intent_remove.putExtras(bundle);
//				intent_remove.putExtra("is_add", true);
				startActivity(intent_remove);
				//传一个参数过去,让解除绑定后直接跳到重新绑定
				
			}else{
				Intent intent_bind = new Intent(GetCashFirstActivity.this,
						BindCardActivity.class);
				startActivity(intent_bind);
			}
			
			break;

		default:
			break;
		}
	}
}
