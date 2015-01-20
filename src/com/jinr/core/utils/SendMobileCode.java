/*
 * SendMobileCode.java
 * @author Andrew Lee
 * 2014-10-25 下午3:52:03
 */
package com.jinr.core.utils;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import model.BaseModel;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.bankCard.BindCardActivity;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

;

/**
 * SendMobileCode.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-25 下午3:52:03
 */
public class SendMobileCode {

	private static SendMobileCode instance = null;
	
	public volatile int time=60;
	
	private String code="";
	private Thread thread;

	private SendMobileCode() {
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new SendMobileCode();
		}
	}

	public static SendMobileCode getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}
	
	public interface Back{
		void sms(String result);
		
	}  
	

	@SuppressLint("HandlerLeak") public synchronized void send_code(final View v,final Context context, String tel,
			String type,String money,final Back back) {
		
		((Button) v).setClickable(false);
		

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// Log.d("debug", "handleMessage方法所在的线程："
				// + Thread.currentThread().getName());
				// Handler处理消息
				if(msg.what==MessageWhat.SENDCODE){
				if (time > 0) {
					((Button) v).setClickable(false);
					((Button) v).setBackgroundResource(R.color.main_text_color);
					((Button) v).setText("" + time+context.getResources()
							.getString(R.string.format_f));
				} else {
					
					((Button) v).setBackgroundResource(R.color.bind_mob_button_color);
					((Button) v).setText(context.getResources().getString(
							R.string.send_modify_code));
					((Button) v).setClickable(true);
				}
				}
			}
		};

		if (tel.length() == 11&&time==60) {
			
			

			RequestParams params = new RequestParams();
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
			params.put("tel", tel);
			params.put("kw", type);
			params.put("verification", UrlConfig.SMS_IDENTIFY);
			params.put("vim", telephonyManager.getDeviceId());
			
			
			if(type.equals(MessageType.MESSAGE_MOBILE_ZCZJ)){
				
				params.put("money", money);
			}
			// params.put("passwords", submit_password);
			MyhttpClient.post(UrlConfig.SEND_SMS, params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1, arg2, arg3);
							ToastUtil.show(context, context.getResources()
									.getString(R.string.default_error));
							((Button) v).setClickable(true);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1, arg2);
							
							String response;
							try {
								response = new String(arg2,"UTF-8");
								response=response.substring(response.indexOf("{"));
								MyLog.i("发送短信信息返回", response);
								
								BaseModel<String> result = new Gson().fromJson(
										response,
										new TypeToken<BaseModel<String>>() {
										}.getType());
								if (result.isSuccess()) {
									
									new Thread(){ 

										public void run(){ 
										    while(time>0){ 
										    	Message msg =Message.obtain();
												time--;
												handler.sendEmptyMessage(MessageWhat.SENDCODE);
										      try {
												Thread.sleep(1000);
												
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
										      
										   } 
										    currentThread().interrupt();
										    time=60;
										  } 
										}.start();
									
									code=result.getData();
									
									back.sms(code);
									
									
								}else{
									
									ToastUtil.show(context,result.getMsg());
									((Button) v).setClickable(true);
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


		} else {
			if(tel.length() != 11){
			ToastUtil.show(context,
					context.getResources()
							.getString(R.string.mobile_no_checked));
			((Button) v).setClickable(true);
			
			}else{
				
				if(time!=60){
					ToastUtil.show(context,""+(time+1)+"s后再次发送");
					((Button) v).setClickable(true);
				}
			}
		}
	}
	

	

}
