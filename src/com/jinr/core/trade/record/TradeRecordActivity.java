/*
 * TradeRecordActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:36:41
 */
package com.jinr.core.trade.record;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.Record;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
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
 * TradeRecordActivity.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-25 上午10:36:41
 */
public class TradeRecordActivity extends Base2Activity implements OnClickListener{
	
	
	
	
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	
	private TextView record_tv;
	private ListView record_lv;
	private List<Record> record=null;
	private String uid;

	
	
	
	private LoadingDialog loadingdialog;
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.GET_RECARD:
				@SuppressWarnings("unchecked")
				List<Record> record_handle=(List<Record>)msg.obj;
				if (record_handle!=null) {
					record_lv.setVisibility(View.GONE);
					TradeRecordAdapter adapter=new TradeRecordAdapter(TradeRecordActivity.this, record_handle);
					record_lv.setAdapter(adapter);
					if(adapter.getCount() == 0){
						record_tv.setVisibility(View.VISIBLE);
						record_lv.setVisibility(View.GONE);
					}
					else{
						record_tv.setVisibility(View.GONE);
						record_lv.setVisibility(View.VISIBLE);
					}
//					adapter.notifyDataSetChanged();
				} else {
					
				}
				
				break;
			
			}
		}};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_record);

		initData();
		findViewById();
		initUI();
		setListener();
//		if("".equals(uid)!=true){
//			loadingdialog.show();
//			send(uid);
//		}
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
		uid=PreferencesUtils.getValueFromSPMap(TradeRecordActivity.this, PreferencesUtils.Keys.UID);
		if("".equals(uid)!=true){
			loadingdialog.show();
			send(uid);
		}
//		TradeRecordAdapter adapter=new TradeRecordAdapter(TradeRecordActivity.this, record);
//		record_lv.setAdapter(adapter);
	}

	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 
	 * 2014-10-27 上午11:14:49
	 * @param uid
	 */
	private void send(String uid) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("uid", uid);
//		params.put("passwords", submit_password);
		
		MyhttpClient.get(UrlConfig.GET_RECORD, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(TradeRecordActivity.this, getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));

					MyLog.i("交易记录信息返回", response);
					BaseListModel<Record> result=new Gson().fromJson(response,
							new TypeToken<BaseListModel<Record>>() {
					}.getType());
					if(result.isSuccess()){
						Message msg = Message.obtain();
						List<Record> recordSend=result.getData();
						
						msg.what = MessageWhat.GET_RECARD;
						msg.obj=recordSend;
						handler.sendMessage(msg);
						
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
	 * @see com.jinr.core.base.Base2Activity#findViewById()
	 */
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		record_lv = (ListView) findViewById(R.id.trade_record_lv);
		record_tv = (TextView) findViewById(R.id.trade_record_tv);
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.deal_record));
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
	}
	
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		switch (v.getId()) {
		case R.id.title_left_img:// 左侧图标
			finish();
			
			break;

		default:
			break;
		}
	}

}
