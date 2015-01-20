/*
 * ChangePasswdActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:28:55
 */
package com.jinr.core.security;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.ChangeMobile;

import org.apache.http.Header;

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
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * ChangePasswdActivity.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-23 上午10:28:55
 */
public class ChangePasswdActivity extends Base2Activity implements
OnClickListener  {
	
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	
	private EditText old_passwd_et;
	private EditText new_passwd_et;
	private EditText submit_new_passwd_et;
	
	private String user_id="";
	
	private Button submit;
	
	
	private LoadingDialog loadingdialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_login_passwd);

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


	
	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#initData()
	 */
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		loadingdialog = new LoadingDialog(this);
		user_id=PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#findViewById()
	 */
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		
		submit=(Button)findViewById(R.id.change_login_submit);
		
		old_passwd_et=(EditText)findViewById(R.id.change_login_et1);
		new_passwd_et=(EditText)findViewById(R.id.change_login_et2);
		submit_new_passwd_et=(EditText)findViewById(R.id.change_login_et3);
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.change_login_passwd));
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
		submit.setOnClickListener(this);
	}
	
	
	
	protected void submit() {
		
		String old_passwd=old_passwd_et.getText().toString();
		String new_passwd=new_passwd_et.getText().toString();
		String submit_new_passwd=submit_new_passwd_et.getText().toString();
		// 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
        boolean b= CommonUtil.isPwdRegular(new_passwd);
        if(!b){
        	//提示相应信息
        	ToastUtil.show(
					this,
					getResources().getString(
							R.string.pwd_alert));
        	return;
        }
		if("".equals(old_passwd)){
			ToastUtil.show(this,
					getResources().getString(R.string.please_input)+getResources().getString(R.string.old_login_passwd));
		}else{
			if("".equals(new_passwd)||new_passwd.length()<6){
				ToastUtil.show(this,
						getResources().getString(R.string.passwd_limited));
			}else{
				if(("".equals(submit_new_passwd))){
					ToastUtil.show(this,
							getResources().getString(R.string.please_input)+getResources().getString(R.string.register_submit_passwd));
				}else{
					if(new_passwd.equals(submit_new_passwd)!=true){
						ToastUtil.show(this, getResources().getString(R.string.passwd_equal_checked));
					}else{
						loadingdialog.show();
						send(old_passwd,new_passwd,submit_new_passwd);
					}
				}
			}
		}
		
		
	}
	
	
	protected void send(String old_passwd,String new_passwd,String submit_passwd){
		RequestParams params = new RequestParams();  
		params.put("user_password", old_passwd);
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
//		params.put("checksms", code);
		params.put("user_lpassword", new_passwd);
		params.put("user_rpassword", submit_passwd);
		params.put("user_id", user_id);
		
		MyhttpClient.post(UrlConfig.MODIFY_PASSWORD, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(ChangePasswdActivity.this, getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					MyLog.i("修改登录密码返回", response);
					response=response.substring(response.indexOf("{"));
					BaseModel<ChangeMobile> result=new Gson().fromJson(response,
							new TypeToken<BaseModel<ChangeMobile>>() {
					}.getType());
					if(result.isSuccess()){
//						PreferencesUtils.putValueToSPMap(RealNameActivity.this, PreferencesUtils.Keys.NAME, name);
//						PreferencesUtils.putValueToSPMap(RealNameActivity.this, PreferencesUtils.Keys.ID_CARD,id_no);
//						PreferencesUtils.putBooleanToSPMap(RealNameActivity.this, PreferencesUtils.Keys.IS_IDENTIFY, true);
						ToastUtil.show(getApplication(), getResources().getString(R.string.change_passwd_success));
						finish();
					}else{
						ToastUtil.show(getApplication(), result.getMsg());
					}
					
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				}
				
				
			}
			
			
		});
		
		
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		case R.id.change_login_submit:
			submit();
			break;

		default:
			break;
		}
	}


}
