/*
 * BindMobileActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:27:31
 */
package com.jinr.core.security;

import java.io.UnsupportedEncodingException;

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
import com.jinr.core.regist.LoginActivity;
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
 * BindMobileActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-23 上午10:27:31
 */
public class BindMobileActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	private Button send_code;// 验证码按钮
	private Button submit;// 确认提交按钮

	private EditText mobile_et;// 手机号
	private EditText code_et;// 填写验证码
	private EditText passwd_et;// 密码

	private String user_id="";
	private String new_mobile="";
	private String sms = "";

	private String tel_no="";
	private String mobileNoSms = "";

	private LoadingDialog loadingdialog;

//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			// Log.d("debug", "handleMessage方法所在的线程："
//			// + Thread.currentThread().getName());
//			// Handler处理消息
//			if (msg.what == MessageWhat.SENDCODE) {
//				if (SendMobileCode.getInstance().time > 0) {
//					send_code.setClickable(false);
//					send_code.setBackgroundResource(R.color.main_text_color);
//					send_code.setText("" + SendMobileCode.getInstance().time
//							+ getResources().getString(R.string.format_f));
//				} else {
//					// timer.cancel();
//
//					send_code
//							.setBackgroundResource(R.color.bind_mob_button_color);
//					send_code.setText(getResources().getString(
//							R.string.send_modify_code));
//					send_code.setClickable(true);
//					// 结束Timer计时器
//				}
//			}
//		}
//	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_mobile);

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
		tel_no = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.TEL);

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

		passwd_et = (EditText) findViewById(R.id.bind_mobile_et1);
		mobile_et = (EditText) findViewById(R.id.bind_mobile_et2);
		code_et = (EditText) findViewById(R.id.bind_mobile_et3);

		send_code = (Button) findViewById(R.id.bind_send_code);
		submit = (Button) findViewById(R.id.bind_mobile_submit);
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
				R.string.change_bind_mobile));
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
		String passwd = passwd_et.getText().toString();
		new_mobile = mobile_et.getText().toString().trim();
		String code = code_et.getText().toString().trim();

		if ("".equals(passwd)) {
			ToastUtil.show(this,
					getResources().getString(R.string.passwd_login_checked));
		} else {
			if (new_mobile.length() != 11) {
				ToastUtil.show(this,
						getResources().getString(R.string.mobile_no_checked));

			} else {
				if (mobileNoSms.equals(new_mobile) != true) {
					ToastUtil.show(
							this,
							getResources().getString(
									R.string.mobile_no_checked_one));

				} else {
					if (new_mobile.equals(tel_no)) {
						ToastUtil.show(
								this,
								getResources().getString(
										R.string.mobile_no_reply_checked_one));

					} else {
						if ("".equals(code)) {
							ToastUtil.show(
									this,
									getResources().getString(
											R.string.code_checked));
						} else {
							loadingdialog.show();
							send(new_mobile, code, passwd);
						}
					}
				}
			}
		}

	}

	protected void send(final String mobileNo, String code, String password) {
		RequestParams params = new RequestParams();
		params.put("user_tel", mobileNo);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("code", code);
		params.put("verification", UrlConfig.SMS_IDENTIFY);
		params.put("user_password", password);
		params.put("user_id", user_id);

		MyhttpClient.post(UrlConfig.MODIFY_MOBILE, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(BindMobileActivity.this, getResources()
								.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("修改手机号返回", response);
							response=response.substring(response.indexOf("{"));
							BaseModel<Integer> result = new Gson()
									.fromJson(
											response,
											new TypeToken<BaseModel<Integer>>() {
											}.getType());
							if (result.isSuccess()) {
								
								if(result.getData()==1){
									
								PreferencesUtils.putValueToSPMap(
										BindMobileActivity.this,
										PreferencesUtils.Keys.TEL, mobileNo);
								BindMobileActivity.this.finish();
								}else{
									ToastUtil.show(getApplication(),getResources()
											.getString(R.string.register_error));
									
								}
							} else {
								ToastUtil.show(getApplication(),
										result.getMsg());
							}

						} catch (StringIndexOutOfBoundsException e){
							MyLog.i("json格式错误", "格式错误");
						}
						catch (UnsupportedEncodingException e) {
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
		case R.id.bind_send_code:
			// 发送验证码
			mobileNoSms = mobile_et.getText().toString();
			if (mobileNoSms.length() != 11) {
				ToastUtil.show(this,
						getResources().getString(R.string.mobile_no_checked));
			} else {
				if (mobileNoSms.equals(tel_no)) {
					ToastUtil.show(
							this,
							getResources().getString(
									R.string.mobile_no_reply_checked_one));
				} else {
					SendMobileCode.getInstance().send_code(
							send_code,BindMobileActivity.this, mobileNoSms,
							MessageType.MESSAGE_MOBILE_XGBDSJHM, null,
							new Back() {

								@Override
								public void sms(String result) {
									// TODO Auto-generated method stub
									if (result != null
											&& "".equals(result) != true) {
										BindMobileActivity.this.sms = result;
									}
								}
							});
				}
			}
			break;
		case R.id.bind_mobile_submit:
			submit();

			break;

		default:
			break;
		}
	}

}
