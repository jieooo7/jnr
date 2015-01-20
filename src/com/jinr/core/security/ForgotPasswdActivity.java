/*
foff * ForgotPasswdActivity.java
 * @author Andrew Lee
 * 2014-10-21 上午10:31:29
 */
package com.jinr.core.security;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.regist.RegisterActivity;
import com.jinr.core.utils.CommonUtil;
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
 * ForgotPasswdActivity.java description:
 * 
 * @author Andrew Lee version 2014-10-21 上午10:31:29
 */
public class ForgotPasswdActivity extends Base2Activity implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private EditText mobile_et; // 手机号
	private EditText code_et; // 验证码
	private EditText passwd_et; // 密码
	private EditText submit_passwd_et; // 确认密码

	private Button code_bt;// 发送验证码
	private Button submit_bt;// 确认提交

	private LoadingDialog loadingdialog;

	private String tel_no;
	private String tel_no_source;
	
	private String mobileNoSms="";

	private String sms = "";
	
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
		setContentView(R.layout.forgot_passwd);

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

		// String tel_no_sourceqq = "18912345678";
		// if ("".equals(tel_no_sourceqq)!=true) {
		// Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
		// Matcher m = p.matcher(tel_no_sourceqq);
		// String tel_noq = m.replaceAll("$1****$3");
		// MyLog.i("正则", tel_noq);
		// }else{
		// }
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
		mobile_et = (EditText) findViewById(R.id.forgot_passwd_tel);
		code_et = (EditText) findViewById(R.id.forgot_passwd_code);
		passwd_et = (EditText) findViewById(R.id.forgot_passwd_passwd);
		submit_passwd_et = (EditText) findViewById(R.id.forgot_passwd_submit_passwd);
		code_bt = (Button) findViewById(R.id.forgot_passwd_send_code);
		submit_bt = (Button) findViewById(R.id.forgot_passwd_submit);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_left_img.setImageResource(R.drawable.close);
		title_center_text.setText(getResources().getString(
				R.string.forgot_the_passwd));
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
	}

	protected void submit() {
		String mobile = mobile_et.getText().toString();
		String code = code_et.getText().toString();
		String passwd = passwd_et.getText().toString();
		String submit_passwd = submit_passwd_et.getText().toString();
		
		// 注册、忘记密码 只可以输入8-16 位混合(数字，字母)
        boolean b= CommonUtil.isPwdRegular(passwd);
        if(!b){
        	//提示相应信息
        	ToastUtil.show(
					this,
					getResources().getString(
							R.string.pwd_alert));
        	return;
        }
		
		
		if (mobile.length() != 11||mobileNoSms.equals(mobile)!=true) {
			ToastUtil.show(this,
					getResources().getString(R.string.mobile_no_checked));
		} else {
			if ("".equals(code)) {
				ToastUtil.show(this,
						getResources().getString(R.string.code_checked));
			} else {
				if ("".equals(passwd)) {
					ToastUtil.show(this,
							getResources().getString(R.string.passwd_checked));
				} else {
					if ("".equals(submit_passwd)) {
						ToastUtil.show(
								this,
								getResources().getString(
										R.string.submit_passwd_checked));
					} else {
						if (submit_passwd.equals(passwd) != true) {
							ToastUtil.show(
									this,
									getResources().getString(
											R.string.passwd_equal_checked));

						} else {
							loadingdialog.show();
							send(mobile, code, passwd, submit_passwd);
						}
					}
				}
			}

		}

	}

	protected void send(String tel, String code, String passwd,
			String submit_passwd) {

		RequestParams params = new RequestParams();
		params.put("tel", tel);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("checksms", code);
		params.put("password", passwd);
		params.put("verification", UrlConfig.SMS_IDENTIFY);
		params.put("rpassword", submit_passwd);

		MyhttpClient.post(UrlConfig.FORGET_PASSWD, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						MyLog.i("忘记密码Statuscode", "" + arg0);
						loadingdialog.dismiss();
						ToastUtil.show(ForgotPasswdActivity.this,
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
							MyLog.i("忘记密码返回", response);
							BaseModel<?> result = new Gson().fromJson(response,
									BaseModel.class);

							if (result.isSuccess()) {
								ToastUtil
										.show(getApplication(),
												getResources()
														.getString(
																R.string.forgot_passwd_success));
								PreferencesUtils.putBooleanToSPMap(
										ForgotPasswdActivity.this,
										PreferencesUtils.Keys.IS_LOGIN, true);
								PreferencesUtils.clearSPMap(ForgotPasswdActivity.this);
								Intent intent = new Intent(
										ForgotPasswdActivity.this,
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
							e.printStackTrace();
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
		case R.id.forgot_passwd_send_code:// 发送验证码
			mobileNoSms = mobile_et.getText().toString();
			if (mobileNoSms.length() != 11) {
				ToastUtil.show(this,
						getResources().getString(R.string.mobile_no_checked));
			} else {
				SendMobileCode.getInstance().send_code(
						code_bt,ForgotPasswdActivity.this, mobileNoSms,
						MessageType.MESSAGE_MOBILE_XGDLMM,null,new Back(){
							

							@Override
							public void sms(String result) {
								// TODO Auto-generated method stub
								if(result!=null&&"".equals(result)!=true){
								ForgotPasswdActivity.this.sms=result;
								}
							}
						});
			}
			break;
		case R.id.forgot_passwd_submit:// 提交确认
			submit();
			break;

		default:
			break;
		}

	}

}
