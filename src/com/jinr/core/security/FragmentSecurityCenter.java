/*
 * SecuCenterActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:23:45
 */
package com.jinr.core.security;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.BaseModel;
import model.UserInfo;

import org.apache.http.Header;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.CardDetailActivity;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.lockpanttern.pattern.App;
import com.jinr.core.security.lockpanttern.pattern.CreateGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.pattern.LockPasswordUtils;
import com.jinr.core.security.lockpanttern.pattern.UnlockGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.ui.CustomDialog;
import com.jinr.core.ui.lockpantternDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * SecuCenterActivity.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-23 上午10:23:45
 */
public class FragmentSecurityCenter extends Fragment implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private RelativeLayout bind_mobile_rl;
	private RelativeLayout real_name_rl;
	private RelativeLayout login_passwd_rl;
	private RelativeLayout deal_passwd_rl;
	private RelativeLayout modify_lock_rl;

	private TextView bind_mobile_tv;
	private TextView real_name_tv;
	private TextView deal_passwd_tv;
	private static ImageView lock_panttern_tv;

	private String bind_mobile="";
	private String real_name="";
	private String deal_passwd="";
	private lockpantternDialog dialog;
	public static String user_id;
	private boolean lock_statue = false;
	private boolean send_statue = true;
	
	LockPatternUtils lockp = new LockPatternUtils(MainActivity.instance);
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.secu_center, container, false);
		
		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;
	}
	
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		initData();
		
		bind_mobile_tv.setText(bind_mobile);
		real_name_tv.setText(real_name);
		deal_passwd_tv.setText(deal_passwd);
		
		if(lockp.savedPatternExists()){
			lock_panttern_tv.setImageResource(R.drawable.an_on);
			modify_lock_rl.setVisibility(View.VISIBLE);
		}
			
		else{
			lock_panttern_tv.setImageResource(R.drawable.an_off);
			modify_lock_rl.setVisibility(View.GONE);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initData()
	 */
	protected void initData() {
		// TODO Auto-generated method stub

		bind_mobile = PreferencesUtils.getValueFromSPMap(
				MainActivity.instance, PreferencesUtils.Keys.TEL);
		if ("".equals(bind_mobile) != true) {
			Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
			Matcher m = p.matcher(bind_mobile);
			bind_mobile = m.replaceAll("$1$2****");
			MyLog.i("正则", bind_mobile);
		} else {

		}

		real_name = PreferencesUtils.getValueFromSPMap(MainActivity.instance,
				PreferencesUtils.Keys.NAME);

		if ("".equals(real_name) == false) {
			if (real_name.length() == 2||real_name.length() == 1) {
				real_name = real_name.substring(0, 1) + "*";
			} else{
				int length = real_name.length();
				real_name = real_name.substring(0, 1) + "*"
						+ real_name.substring(length - 1, length);
			}
		} else {
			//留空
			real_name=getResources().getString(R.string.un_name);
		}

		if (Check.has_deal_passwd(MainActivity.instance)) {

			deal_passwd = getResources().getString(R.string.change);
		} else {
			deal_passwd = getResources().getString(R.string.un_set);
		}
		
		user_id=PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.UID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#findViewById()
	 */
	protected void findViewById(View view) {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) view.findViewById(R.id.title_left_img);
		bind_mobile_tv = (TextView) view.findViewById(R.id.secu_center_tv1);
		real_name_tv = (TextView) view.findViewById(R.id.secu_center_tv2);
		deal_passwd_tv = (TextView) view.findViewById(R.id.secu_center_tv3);
		lock_panttern_tv= (ImageView) view.findViewById(R.id.an_off_on);
		
		bind_mobile_rl = (RelativeLayout) view.findViewById(R.id.secu_center_rl1);
		real_name_rl = (RelativeLayout) view.findViewById(R.id.secu_center_rl2);
		login_passwd_rl = (RelativeLayout) view.findViewById(R.id.secu_center_rl3);
		deal_passwd_rl = (RelativeLayout) view.findViewById(R.id.secu_center_rl4);
		modify_lock_rl = (RelativeLayout) view.findViewById(R.id.secu_center_rl6);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	protected void initUI() {
		// TODO Auto-generated method stub
		title_left_img.setImageResource(R.drawable.slide_button);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
		bind_mobile_rl.setOnClickListener(this);
		real_name_rl.setOnClickListener(this);
		login_passwd_rl.setOnClickListener(this);
		deal_passwd_rl.setOnClickListener(this);
		lock_panttern_tv.setOnClickListener(this);
		modify_lock_rl.setOnClickListener(this);		
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
			MainActivity.instance.showLeftMenu();
			break;
		case R.id.secu_center_rl1:
			Intent intent_mobile = new Intent(MainActivity.instance,
					BindMobileActivity.class);
			startActivity(intent_mobile);
			
			break;
		case R.id.secu_center_rl2:
			
			if(!Check.is_identify(MainActivity.instance)){
				
			Intent intent_real_name = new Intent(MainActivity.instance,
					RealNameActivity.class);
			startActivity(intent_real_name);
			
			}else{
				//显示个人信息
				
				Intent intent_real_info = new Intent(MainActivity.instance,
						RealInfoActivity.class);
				startActivity(intent_real_info);
				
			}
			break;
		case R.id.secu_center_rl3:
			Intent intent_login_passwd = new Intent(MainActivity.instance,
					ChangePasswdActivity.class);
			startActivity(intent_login_passwd);
			
			break;
		case R.id.secu_center_rl4:
			Intent intent_deal_passwd = new Intent(MainActivity.instance,
					DealPasswdActivity.class);
			startActivity(intent_deal_passwd);
			
			break;
			
		case R.id.an_off_on:	

				lock_statue = true;
				changeLock();		
			break;
		case R.id.secu_center_rl6:
				lock_statue = false;
				changeLock();
			break;
		default:
			break;
		}
	}
	private void changeLock(){
		dialog = new lockpantternDialog(MainActivity.instance,
				getString(R.string.warning));
		dialog.btn_custom_dialog_ok.setText(getString(R.string.dialog_call_bt_ok));
		dialog.btn_custom_dialog_cancel.setText(getString(R.string.dialog_call_bt_cancel));
		dialog.btn_custom_dialog_ok
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(send_statue){//send控制
						send_statue = false;
						send(PreferencesUtils.getValueFromSPMap(
								MainActivity.instance, PreferencesUtils.Keys.TEL),dialog.dialog_password.getText().toString());
					}
				}
			});
		dialog.btn_custom_dialog_cancel
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.close();
				}
			});
		dialog.show();
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
						ToastUtil.show(getActivity(), getResources()
								.getString(R.string.default_error));
						send_statue = true;
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);

						try {
							String response = new String(arg2, "UTF-8");
							MyLog.i("登陆返回", response);
							BaseModel<UserInfo> result = new Gson().fromJson(
									response,
									new TypeToken<BaseModel<UserInfo>>() {
									}.getType());
							if (result.isSuccess()) {
								
								dialog.close();
								if(lockp.savedPatternExists() && lock_statue){
									lock_panttern_tv.setImageResource(R.drawable.an_off);
									modify_lock_rl.setVisibility(View.GONE);
									lockp.lockPattern_off(user_id);	
									lockp.clearLock();
								}
								else{
									lock_panttern_tv.setImageResource(R.drawable.an_on);
									modify_lock_rl.setVisibility(View.VISIBLE);
									lockp.lockPattern_on(user_id);
									if(!lockp.savedLockPatternExists() || !lock_statue)
										startActivity(new Intent(MainActivity.instance,CreateGesturePasswordActivity.class));//跳转到设置手势密码
								}
							} else {
								ToastUtil.show(getActivity(),
										result.getMsg());
							}

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							MyLog.i("json解析错误", "解析错误");
						}
						send_statue = true;
					}
					
				});

	}
    

}
