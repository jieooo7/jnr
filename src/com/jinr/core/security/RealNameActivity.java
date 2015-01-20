/*
 * RealNameActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:27:53
 */
package com.jinr.core.security;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.BaseModel;
import model.ChangeMobile;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jinr.core.config.MessageType;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.RegisterActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.SendMobileCode.Back;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * RealNameActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-23 上午10:27:53
 */
public class RealNameActivity extends Base2Activity implements OnClickListener {
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private TextView mobile_tv; // 手机号码

	private EditText name_et;
	private EditText id_no_et;
	private EditText code_et;

	private Button send_code;// 验证码按钮
	private Button submit;// 确认提交按钮

	private String bind_mobile = "";
	private String user_id = "";
	private String name = "";
	private String id_no = "";
	private String code = "";
	private String sms = "";

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
//				send_code.setClickable(false);
//				send_code.setBackgroundResource(R.color.main_text_color);
//				send_code.setText("" + SendMobileCode.getInstance().time+getResources()
//						.getString(R.string.format_f));
//			} else {
////				timer.cancel();
//				
//				send_code.setBackgroundResource(R.color.bind_mob_button_color);
//				send_code.setText(getResources().getString(
//						R.string.send_modify_code));
//				send_code.setClickable(true);
//				// 结束Timer计时器
//			}
//			}
//		}
//	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_name);

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
		bind_mobile = PreferencesUtils.getValueFromSPMap(RealNameActivity.this,
				PreferencesUtils.Keys.TEL);
		if ("".equals(bind_mobile) != true) {
			Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
			Matcher m = p.matcher(bind_mobile);
			bind_mobile = m.replaceAll("$1$2****");
			MyLog.i("正则", bind_mobile);
		} else {

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

		mobile_tv = (TextView) findViewById(R.id.real_name_mobile_et); // 手机号码

		name_et = (EditText) findViewById(R.id.real_name_et1);
		id_no_et = (EditText) findViewById(R.id.real_name_et2);
		code_et = (EditText) findViewById(R.id.real_name_et3);

		send_code = (Button) findViewById(R.id.real_name_send_code);// 验证码按钮
		submit = (Button) findViewById(R.id.real_name_submit);// 确认提交按钮
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
				R.string.identify_title));
		mobile_tv.setText(bind_mobile);
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
		send_code.setOnClickListener(this);
	}

	protected void submit() {
		name = name_et.getText().toString().trim();
		id_no = id_no_et.getText().toString().trim();
		code = code_et.getText().toString().trim();

		if ("".equals(name)) {
			ToastUtil.show(this, getResources()
					.getString(R.string.name_checked));
		} else {
			if (id_no.length() != 18 && id_no.length() != 15) {
				ToastUtil.show(this,
						getResources().getString(R.string.id_no_checked));
			} else {
				if ("".equals(code)) {
					ToastUtil.show(this,
							getResources().getString(R.string.code_checked));
				} else {
					loadingdialog.show();
					send(name, code, id_no);
				}
			}
		}

	}

	protected void send(String names, String code, String cardId) {
		RequestParams params = new RequestParams();
		params.put("name", name);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("checksms", code);
		params.put("verification", UrlConfig.SMS_IDENTIFY);
		params.put("card_id", cardId);
		params.put("id", user_id);

		MyhttpClient.post(UrlConfig.MODIFY_ID_CARD, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(RealNameActivity.this, getResources()
								.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("实名认证返回", response);
							response = response.substring(response.indexOf("{"));
							BaseModel<Integer> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<Integer>>() {
									}.getType());
							if (result.isSuccess()) {
								PreferencesUtils.putValueToSPMap(
										RealNameActivity.this,
										PreferencesUtils.Keys.NAME, name);
								PreferencesUtils.putValueToSPMap(
										RealNameActivity.this,
										PreferencesUtils.Keys.ID_CARD, id_no);
								PreferencesUtils
										.putBooleanToSPMap(
												RealNameActivity.this,
												PreferencesUtils.Keys.IS_IDENTIFY,
												true);
								ToastUtil.show(getApplication(), getResources()
										.getString(R.string.identify_success));
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
		case R.id.title_left_img:
			finish();

			break;
		case R.id.real_name_send_code:
			// 发送验证码
			String tel_no = PreferencesUtils.getValueFromSPMap(this,
					PreferencesUtils.Keys.TEL);
			SendMobileCode.getInstance().send_code(send_code,RealNameActivity.this,
					 tel_no,MessageType.MESSAGE_MOBILE_IDENTITY, null,
					new Back() {

						@Override
						public void sms(String result) {
							// TODO Auto-generated method stub
							if(result!=null&&"".equals(result)!=true){
							RealNameActivity.this.sms = result;
							}
						}
					});
			break;
		case R.id.real_name_submit:
			submit();

			break;

		default:
			break;
		}

	}

}
