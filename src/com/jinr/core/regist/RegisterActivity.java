/*
 * RegisterActivity.java
 * @author Andrew Lee
 * 2014-10-20 上午11:18:19
 */
package com.jinr.core.regist;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.BaseModel;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.jinr.core.more.AgreementActivity;
import com.jinr.core.security.RealNameActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * RegisterActivity.java description:
 * 
 * @author Andrew Lee version 2014-10-20 上午11:18:19
 */
public class RegisterActivity extends Base2Activity implements OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private Button send_code;// 验证码按钮
	private Button register;// 注册按钮
	private EditText mobile_et;// 手机号
	private EditText code_et;// 填写验证码
	private EditText passwd_et;// 密码
	private EditText submit_passwd_et;// 确认密码
	private CheckBox check_box;// 选框
	private TextView agreement_tv;// 同意协议

	private String sms = "";
	private String mobileNoSms = "";

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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

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
		agreement_tv = (TextView) findViewById(R.id.register_agree);

		send_code = (Button) findViewById(R.id.register_send_code);
		register = (Button) findViewById(R.id.register_submit);
		mobile_et = (EditText) findViewById(R.id.register_et1);
		code_et = (EditText) findViewById(R.id.register_et2);
		passwd_et = (EditText) findViewById(R.id.register_et3);
		submit_passwd_et = (EditText) findViewById(R.id.register_et4);
		check_box = (CheckBox) findViewById(R.id.register_check);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub

		title_center_text.setText(getResources().getString(R.string.register));

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
		send_code.setOnClickListener(this);
		register.setOnClickListener(this);
		agreement_tv.setOnClickListener(this);

	}

	protected void submit() {

		String mobileNo = mobile_et.getText().toString();
		String code = code_et.getText().toString();
		String password = passwd_et.getText().toString();
		String submit_password = submit_passwd_et.getText().toString();

		// 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
		boolean b = CommonUtil.isPwdRegular(password);
		if (!b) {
			// 提示相应信息
			ToastUtil.show(this, getResources().getString(R.string.pwd_alert));
			return;
		}

		if (!check_box.isChecked()) {

			ToastUtil.show(this,
					getResources().getString(R.string.i_agree_checked));
		} else {
			if (mobileNo.length() != 11 || mobileNoSms.equals(mobileNo) != true) {
				ToastUtil.show(this,
						getResources().getString(R.string.mobile_no_checked));
			} else {
				if ("".equals(code)) {
					ToastUtil.show(this,
							getResources().getString(R.string.code_checked));
				} else {
					if ("".equals(password) || password.length() < 6) {
						ToastUtil.show(
								this,
								getResources().getString(
										R.string.passwd_limited));
					} else {
						if ("".equals(submit_password)) {
							ToastUtil.show(
									this,
									getResources().getString(
											R.string.submit_passwd_checked));
						} else {
							if (submit_password.equals(password) != true) {
								ToastUtil.show(
										this,
										getResources().getString(
												R.string.passwd_equal_checked));
							} else {// 通过验证,发送网络请求
								loadingdialog.show();
								send(mobileNo, code, password, submit_password);
							}
						}
					}
				}
			}

		}

	}

	protected void send(String mobileNo, String code, String password,
			String submit_password) {
		RequestParams params = new RequestParams();
		params.put("telephone", mobileNo);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("checksms", code);
		params.put("verification", UrlConfig.SMS_IDENTIFY);
		params.put("password", password);
		params.put("passwords", submit_password);
		params.put("type", "4");
		params.put("platform", UrlConfig.PLATFORM);
		MyLog.i("UrlConfig.PLATFORM", UrlConfig.PLATFORM);

		MyhttpClient.post(UrlConfig.ADD_USER, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(RegisterActivity.this, getResources()
								.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("注册返回", response);
							BaseModel<Integer> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<Integer>>() {
									}.getType());
							if (result.isSuccess()) {
								// PreferencesUtils.putValueToSPMap(RegisterActivity.this,
								// PreferencesUtils.Keys.UID,
								// ""+result.getData());
								// PreferencesUtils.putBooleanToSPMap(RegisterActivity.this,
								// PreferencesUtils.Keys.IS_LOGIN, true);
								ToastUtil.show(getApplication(), getResources()
										.getString(R.string.register_success));
								Intent intent = new Intent(
										RegisterActivity.this,
										LoginActivity.class);
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
		case R.id.title_left_img:
			finish();
			break;
		case R.id.register_agree:

			Intent intent = new Intent(RegisterActivity.this,
					AgreementActivity.class);
			startActivity(intent);

			break;
		case R.id.register_send_code:
			// 发送验证码

			mobileNoSms = mobile_et.getText().toString();
			if (mobileNoSms.length() != 11) {
				ToastUtil.show(this,
						getResources().getString(R.string.mobile_no_checked));
			} else {
				SendMobileCode.getInstance().send_code(send_code,RegisterActivity.this,
						 mobileNoSms,
						MessageType.MESSAGE_MOBILE_ZCXX, null,new Back() {

							@Override
							public void sms(String result) {
								// TODO Auto-generated method stub
								if (result != null && "".equals(result) != true) {

									RegisterActivity.this.sms = result;
								}
							}
						});
			}

			break;
		case R.id.register_submit:
			// 提交注册

			submit();

			break;

		default:
			break;
		}

	}

}
