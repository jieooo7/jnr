/*
 * GetCashSecondActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:33:51
 */
package com.jinr.core.trade.getCash;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.BaseListModel;
import model.BaseModel;
import model.ZuheId;

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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.RegisterActivity;
import com.jinr.core.security.BindMobileActivity;
import com.jinr.core.trade.purchase.PurchaseFirstActivity;
import com.jinr.core.trade.purchase.PurchaseSecondActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.SendMobileCode.Back;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * GetCashSecondActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-25 上午10:33:51
 */
public class GetCashSecondActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private String submit_info;
	private String tel;
	private TextView submit_info_tv;
	private TextView the_rules_tv;
	private ScrollView the_rules_sw;

	private EditText deal_passwd_et;
	private EditText code_et;

	private Button code_bt;
	private Button submit_bt;
	private String money;
	private String total_money;
	private String tips="";
	private String deal_passwd;
	private String user_id="";
	private String code;
	private String sms="";
	private boolean is_show=false;
	private LoadingDialog loadingdialog;
	
//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			// Log.d("debug", "handleMessage方法所在的线程："
//			// + Thread.currentThread().getName());
//			// Handler处理消息
//			if(msg.what==MessageWhat.SENDCODE){
//			if (SendMobileCode.getInstance().time> 0) {
//				code_bt.setClickable(false);
//				code_bt.setBackgroundResource(R.color.main_text_color);
//				code_bt.setText("" + SendMobileCode.getInstance().time+getResources()
//						.getString(R.string.format_f));
//			} else {
////				timer.cancel();
//				
//				code_bt.setBackgroundResource(R.color.bind_mob_button_color);
//				code_bt.setText(getResources().getString(
//						R.string.send_modify_code));
//				code_bt.setClickable(true);
//				// 结束Timer计时器
//			}
//			}
//		}
//	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_cash_second);

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
		
		user_id=PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);

		money = getIntent().getExtras().getString("money");
		tips = getIntent().getExtras().getString("tips");
		money=String.format("%.2f", (Double.parseDouble(money)-0.0049999));
		total_money=getIntent().getExtras().getString("input");
		total_money=String.format("%.2f", (Double.parseDouble(total_money)));
		String bank = PreferencesUtils.getValueFromSPMap(
				GetCashSecondActivity.this, PreferencesUtils.Keys.BANK_NAME);
		String bank_card = PreferencesUtils.getValueFromSPMap(
				GetCashSecondActivity.this, PreferencesUtils.Keys.BANK_CARD_NO);
		bank_card = TextAdjustUtil.getInstance().bankCardAdjust(bank_card);
		Date date = new Date(System.currentTimeMillis()+1000 * 60 * 60 * 24);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日",
				Locale.CHINA);

		String time = format.format(date);
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat sp = new SimpleDateFormat("MM月dd日");
		String yesterday = sp.format(d);
		submit_info = getResources().getString(R.string.format_a) + bank + "\n"+bank_card+"\n" + getResources().getString(R.string.format_b) + time
				+getResources().getString(R.string.format_c) + yesterday + getResources().getString(R.string.format_d)
				+ money + getResources().getString(R.string.format_e)+tips+getResources().getString(R.string.format_g);
//		submit_info = "收款银行卡:" + bank + "\n"+bank_card+"\n" + "预计最迟到账时间:" + time
//				+ "前\n转出资金仍有" + yesterday + "收益\n如遇节假日,请往后顺延\n\n提现金额:"
//				+ money + "元\n手续费:2.00元\n\n";
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
		submit_info_tv = (TextView) findViewById(R.id.get_cash_sec_tv0);
		the_rules_tv = (TextView) findViewById(R.id.get_cash_sec_tv11);
		the_rules_sw = (ScrollView) findViewById(R.id.get_cash_sec_sv11);

		deal_passwd_et = (EditText) findViewById(R.id.get_cash_sec_et1);
		code_et = (EditText) findViewById(R.id.get_cash_sec_code);
		code_bt = (Button) findViewById(R.id.get_cash_sec_send_code);
		submit_bt = (Button) findViewById(R.id.get_cash_sec_submit);

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
				R.string.submit_security_identify));
		submit_info_tv.setText(submit_info);
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
		code_bt.setOnClickListener(this);
		submit_bt.setOnClickListener(this);
		the_rules_tv.setOnClickListener(this);
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

		case R.id.get_cash_sec_send_code:
			// 发送验证,新建一个类
			String tel_no=PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.TEL);
			SendMobileCode.getInstance().send_code(code_bt,GetCashSecondActivity.this,tel_no,MessageType.MESSAGE_MOBILE_ZCZJ,total_money,new Back(){
				

				@Override
				public void sms(String result) {
					// TODO Auto-generated method stub
					if(result!=null&&"".equals(result)!=true){
						
					GetCashSecondActivity.this.sms=result;
					}
				}
			});
		
			
			break;
		case R.id.get_cash_sec_submit:
			submit();

			break;
			
		case R.id.get_cash_sec_tv11:
			if(!is_show){
				the_rules_tv.setText(getResources().getString(
						R.string.tips_rule_two));
				the_rules_sw.setVisibility(View.VISIBLE);
				is_show=true;
			}else{
				the_rules_tv.setText(getResources().getString(
						R.string.tips_rule_one));
				the_rules_sw.setVisibility(View.GONE);
				is_show=false;
			}

		default:
			break;
		}
	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-25 下午3:37:30
	 */
	protected void submit() {
		// TODO Auto-generated method stub
		deal_passwd = deal_passwd_et.getText().toString();
		code = code_et.getText().toString();

		if ("".equals(deal_passwd)) {
			ToastUtil.show(this, getResources()
					.getString(R.string.please_input)
					+ getResources().getString(R.string.deal_passwd));
		} else {
			if ("".equals(code)) {

				ToastUtil.show(this,
						getResources().getString(R.string.code_checked));

			} else {
				loadingdialog.show();
				send();
			}
		}

	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 2014-10-25 下午3:48:31
	 */
	protected void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		
		MyhttpClient.post(UrlConfig.GET_ZUHEID, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(GetCashSecondActivity.this, getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("获取组合id信息返回", response);
					BaseListModel<ZuheId> result=new Gson().fromJson(response,
							new TypeToken<BaseListModel<ZuheId>>() {
					}.getType());
					if(result.isSuccess()){
						String zuHeId=result.getData().get(0).getId();
						loadingdialog.show();
						sendAgain(deal_passwd,total_money,zuHeId,code);
					}else{
						ToastUtil.show(getApplication(), result.getMsg());
					}
					
					
				}catch (StringIndexOutOfBoundsException e){
					MyLog.i("json格式错误", "格式错误");
				} 
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				}
				
				
			}
			
			
		});
		

	}
	protected void sendAgain(String buss_pwd,String txmoney,String zuhe_id,String code) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("verification", UrlConfig.SMS_IDENTIFY);
		params.put("buss_pwd", buss_pwd);
		params.put("txmoney", txmoney);
		params.put("zuhe_id", zuhe_id);
		params.put("uid", user_id);
		params.put("type", "4");
		
		params.put("checksms", code);
		
		MyLog.i("提现总金额", txmoney);
		
		MyhttpClient.post(UrlConfig.GET_MONEY_FINAL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(GetCashSecondActivity.this, getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("提现最终信息返回", response);
					BaseModel<Integer> result=new Gson().fromJson(response,
							new TypeToken<BaseModel<Integer>>() {
					}.getType());
					if(result.isSuccess()){
						//intent操作
						
						Intent intent = new Intent(GetCashSecondActivity.this,
								TradeFinishActivity.class);
						String show_info = getResources().getString(
								R.string.get_cash_tips);
						Bundle bundle = new Bundle();
						bundle.putString("show_info", show_info);
						bundle.putBoolean("get_cash", true);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}else{
						ToastUtil.show(getApplication(), result.getMsg());
					}
					
					
				}catch (StringIndexOutOfBoundsException e){
					MyLog.i("json格式错误", "格式错误");
				} 
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				}
				
				
			}
			
			
		});
	}
	
}
