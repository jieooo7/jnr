/*
 * LoginActivity.java
 * @author Andrew Lee
 * 2014-10-21 上午8:43:03
 */
package com.jinr.core.regist;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;
import model.UserInfo;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.FragmentMore;
import com.jinr.core.security.ForgotPasswdActivity;
import com.jinr.core.security.lockpanttern.pattern.CreateGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.pattern.UnlockGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * LoginActivity.java description:
 * 
 * @author Andrew Lee version 2014-10-21 上午8:43:03
 */
public class LoginActivity extends Base2Activity implements OnClickListener {

	private TextView title_center_text; // title标题
	private ImageView title_left_img; // title左边图片
	private EditText mobile_et;// 手机号输入
	private EditText passwd_et;// 密码
	private TextView passwd_fogot;// 忘记密码
	private Button login_bt;// 登录按钮
	private Button register_bt;// 注册按钮
	
	private String user_id = "";

	private LoadingDialog loadingdialog;
	LockPatternUtils lockp = new LockPatternUtils(this);
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_in);
//		select();

		initData();
		findViewById();
		initUI();
		setListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){			
			MainActivity.isLock = true;
			Intent intent=new Intent(LoginActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
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
		mobile_et = (EditText) findViewById(R.id.login_in_et1);
		passwd_et = (EditText) findViewById(R.id.login_in_et2);
		passwd_fogot = (TextView) findViewById(R.id.login_in_fogot);
		login_bt = (Button) findViewById(R.id.login_in_bt1);
		register_bt = (Button) findViewById(R.id.login_in_bt2);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(R.string.login_in));
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
		passwd_fogot.setOnClickListener(this);
		login_bt.setOnClickListener(this);
		register_bt.setOnClickListener(this);
	}
	
protected void select(){
		
		final String[] items = new String[]{"开发机","测试机","正式机"}; 
		new AlertDialog.Builder(LoginActivity.this).setTitle("单选框").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					PreferencesUtils.putIntToSPMap(
							LoginActivity.this,
							PreferencesUtils.Keys.TEST,
							which);

					
					break;
				case 1:
					PreferencesUtils.putIntToSPMap(
							LoginActivity.this,
							PreferencesUtils.Keys.TEST,
							which);
					break;
				case 2:
					PreferencesUtils.putIntToSPMap(
							LoginActivity.this,
							PreferencesUtils.Keys.TEST,
							which);
					break;
				}
			}
		}).setNegativeButton("确认", null).show();
	}

	protected void submit() {

		String mobile = mobile_et.getText().toString();
		String passwd = passwd_et.getText().toString();
		if ("".equals(mobile)) {
			ToastUtil.show(this,
					getResources().getString(R.string.mobile_email_checked));
		} else {
			if ("".equals(passwd)) {
				ToastUtil.show(this,
						getResources().getString(R.string.passwd_checked));
			} else {
				loadingdialog.show();
				send(mobile, passwd);
			}
		}

	}

	protected void send(String mobile, String passwd) {
		RequestParams params = new RequestParams();
		params.put("emailortel", mobile);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("password", passwd);

		MyhttpClient.post(UrlConfig.GET_USER, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(LoginActivity.this, getResources()
								.getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("登陆返回", response);
							BaseModel<UserInfo> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<UserInfo>>() {
									}.getType());
							if (result.isSuccess()) {
								
								// 图形解锁忘记密码跳转到重设图形解锁密码界面
								Intent intent = getIntent();   
								String str = intent          
										.getStringExtra("unlock_forget");
								if (str != null && str.equals("unlock_forget")) {  //如果在忘记密码跳转到登录界面的情况下
										lockp.clear_lock_off_on();
										lockp.clearLock();
										PreferencesUtils.clearSPMap(MainActivity.instance);
						                Intent intent_return = new Intent();  
						                intent_return.putExtra("return", "T");  
						                setResult(RESULT_OK, intent_return); 
									}
									
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.UID, result
												.getData().getId());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.EMAIL, result
												.getData().getEmail());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.NICKNAME, result
						 						.getData().getNikename());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.TEL, result
												.getData().getTel());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.PASSWORD, result
												.getData().getPassword());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.PWD_KEY, result
												.getData().getPwd_key());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.NAME, result
												.getData().getName());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.BUSS_PWD, result
												.getData().getBuss_pwd());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.ID_CARD, result
												.getData().getCard_id());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.IS_LOCK, result
												.getData().getIs_lock());
								PreferencesUtils.putBooleanToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.IS_LOGIN, true);
				
								// 根据网络获取 判断 是否实名 有无支付密码
								if ("".equals(PreferencesUtils
										.getValueFromSPMap(LoginActivity.this,
												PreferencesUtils.Keys.NAME)) != true
										&& "".equals(PreferencesUtils
												.getValueFromSPMap(
														LoginActivity.this,
														PreferencesUtils.Keys.ID_CARD)) != true) {// 实名
									PreferencesUtils.putBooleanToSPMap(
											LoginActivity.this,
											PreferencesUtils.Keys.IS_IDENTIFY,
											true);
								}
								if ("".equals(PreferencesUtils
										.getValueFromSPMap(LoginActivity.this,
												PreferencesUtils.Keys.BUSS_PWD)) != true) {// 交易密码
									PreferencesUtils
											.putBooleanToSPMap(
													LoginActivity.this,
													PreferencesUtils.Keys.HAS_DEAL_PASSWD,
													true);
								}

								user_id = PreferencesUtils.getValueFromSPMap(LoginActivity.this,
										PreferencesUtils.Keys.UID);
								loadingdialog.show();
								sendBank();//请求银行卡信息  
								isSetLock();//判断是否弹出图形解锁设置提示框

								
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
//						 Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//							startActivity(intent);//lj与图形解锁冲突
						 
						// ToastUtil.show(LoginActivity.this,
						// getResources().getString(R.string.default_error));

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
										LoginActivity.this,
										PreferencesUtils.Keys.BANK_CARD_ID,
										result.getData().get(0).getId());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.BANK_CARD_NO,
										result.getData().get(0).getBank_no());
								// PreferencesUtils.putValueToSPMap(ManageCardActivity.this,
								// PreferencesUtils.Keys.BANK_CARD_NUM,
								// result.getData().get(0).getBank_num());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.BANK_NAME, result
												.getData().get(0)
												.getBank_name());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.OPEN_BANK, result
												.getData().get(0)
												.getOpen_bank());
								PreferencesUtils.putValueToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.BANK_STATUS,
										result.getData().get(0)
												.getBank_status());
								PreferencesUtils
										.putValueToSPMap(
												LoginActivity.this,
												PreferencesUtils.Keys.BANK_CITY,
												result.getData().get(0)
														.getZone_shi());

								PreferencesUtils.putBooleanToSPMap(
										LoginActivity.this,
										PreferencesUtils.Keys.HAS_CARD, true);
								ToastUtil.show(getApplication(), getResources()
										.getString(R.string.login_in_success));
//								Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//								startActivity(intent);//lj与图形解锁冲突
								
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
								// ToastUtil.show(getApplication(),
								// result.getMsg());
//								Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//								startActivity(intent);//lj与图形解锁冲突
								
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
/** 在用户登录进入主页面前，先弹出弹窗询问是否需要设置密码，所以将sendBank()放在了这个函数中。
 * by fym
 *
 */
	public void isSetLock(){
		user_id=PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
		
		MainActivity.isLock = true;
		MyLog.i("isSetLock", user_id );
		if(!user_id.equals(lockp.userID()) || !lockp.savedPatternExists()){
			lockp.clear_lock_off_on();
			lockp.clearLock();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("您尚未设置手势密码，是否去设置？")
			       .setCancelable(false)
			       .setPositiveButton("取消", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   lockp.clearLock();
			        	   lockp.lockPattern_off(user_id);
							Intent intent = getIntent();   
							String str = intent          
									.getStringExtra("unlock_forget");
							if (!(str != null && str.equals("unlock_forget"))) {  //如果不是在忘记密码跳转到登录界面的情况下
					        	   Intent intentMainActivity=new Intent(LoginActivity.this,MainActivity.class);
					        	   MainActivity.isLock_longin = false;
					        	   startActivity(intentMainActivity);
								}
							finish();
			           }
			       })
			       .setNegativeButton("设置", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                 
			                Intent intent_CreateGesturePasswordActivity = new Intent(
									LoginActivity.this,
									CreateGesturePasswordActivity.class);
							Intent intent = getIntent();   
							String str = intent          
									.getStringExtra("unlock_forget");
//							if (!(str != null && str.equals("unlock_forget"))) {  //如果不是在忘记密码跳转到登录界面的情况下
				                intent_CreateGesturePasswordActivity.putExtra("gotoMainActive", "gotoMainActive");
				                	MainActivity.isLock_longin = false;
//							}	
							startActivity(intent_CreateGesturePasswordActivity);    
							finish();
			           }
			       }).show();
			
			AlertDialog alert = builder.create();
		}
		else{
			
			Intent intent = getIntent();   
			String str = intent          
					.getStringExtra("unlock_forget");
			if (!(str != null && str.equals("unlock_forget"))) {  //如果不是在忘记密码跳转到登录界面的情况下
	        	   MainActivity.isLock_longin = false;
				}
     	   Intent intentMainActivity=new Intent(LoginActivity.this,MainActivity.class);
     	   startActivity(intentMainActivity);
     	  finish();
		}
			
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
			MainActivity.isLock = true;
			Intent intent_back=new Intent(LoginActivity.this,MainActivity.class);
			startActivity(intent_back);			
		finish();
			break;
		case R.id.login_in_fogot:// 忘记密码
			Intent intentForgot = new Intent(LoginActivity.this,
					ForgotPasswdActivity.class);
			startActivity(intentForgot);
			break;
		case R.id.login_in_bt1:// 登陆
			submit();

			break;
		case R.id.login_in_bt2:// 注册
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
