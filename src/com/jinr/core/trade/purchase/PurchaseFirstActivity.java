/*
 * PurchaseFirstActivity.java
 * @author Andrew Lee
 * 2014-10-25 上午10:35:43
 */
package com.jinr.core.trade.purchase;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;
import model.ZuheId;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.BindCardActivity;
import com.jinr.core.bankCard.CardDetailActivity;
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
 * PurchaseFirstActivity.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-25 上午10:35:43
 */
public class PurchaseFirstActivity extends Base2Activity implements OnClickListener{
	
	
	
	
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private TextView puchase_tips_tv;//50000限额字符串
	private EditText purchase_et;
	private Button pay_bt;
	
	private String purchase="0";
	private String user_id="";
	private String money="";
	
	
	private LoadingDialog loadingdialog;

	public static PurchaseFirstActivity instance = null;
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.LIMIT_MESS:
				@SuppressWarnings("unchecked")
				String rev = (String) msg.obj;
				puchase_tips_tv.setText(Html.fromHtml(rev));
				
				break;
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase_first);

		initData();
		findViewById();
		limitMess();
		initUI();
		setListener();
		
		instance = this;
//		setColorTV(puchase_tips_tv,7,12,Color.parseColor("#0096da"));//对50000上色
//		setColorTV(puchase_tips_tv,17,18,Color.parseColor("#0096da"));//对1上色
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
		loadingdialog = new LoadingDialog(PurchaseFirstActivity.this);
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
		
		purchase_et=(EditText)findViewById(R.id.puchase_et1);
		pay_bt=(Button)findViewById(R.id.purchase_submit_bt);
		puchase_tips_tv = (TextView)findViewById(R.id.puchase_tips_two);
	}
	protected void setColorTV(TextView tips_tv, int star,int end,int color){
		SpannableString ss = new SpannableString(tips_tv.getText());
		ss.setSpan(new ForegroundColorSpan(color), star, end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tips_tv.setText(ss);
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.turn_in));
	}

	/* (non-Javadoc)
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
		pay_bt.setOnClickListener(this);
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
			
		case R.id.purchase_submit_bt:
			purchase=purchase_et.getText().toString();
			if("".equals(purchase)){
				purchase="0";
			}
			if(purchase.equals("0")){
				ToastUtil.show(this,
						getResources().getString(R.string.please_input)+getResources().getString(R.string.purchase_no));
			}else{
				if(Double.parseDouble(purchase)>=1){
					
					loadingdialog.show();
					sendLimitMoney();
			

				}else{
					
					
					ToastUtil.show(this,
							getResources().getString(R.string.input_int));
				}
			}
							
				
			break;

		default:
			break;
		}
	}
	
	
	protected void limitMess(){
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		
		MyhttpClient.get(UrlConfig.LIMIT_MESS, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(PurchaseFirstActivity.this, getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("限额提示信息返回", response);
					BaseModel<String> result=new Gson().fromJson(response,
							new TypeToken<BaseModel<String>>() {
					}.getType());
					if(result.isSuccess()){
						Message msg = Message.obtain();

						msg.what = MessageWhat.LIMIT_MESS;
						msg.obj = result.getData();
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






	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 
	 * 2014-10-25 下午5:16:01
	 */
	private void submit() {
		// TODO Auto-generated method stub
				
				loadingdialog.show();
				send();
				//跳转下一个页面
	}






	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 
	 * 2014-10-27 下午5:34:59
	 */
	private void send() {
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
				ToastUtil.show(PurchaseFirstActivity.this, getResources().getString(R.string.default_error));
				
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
						Intent intent = new Intent(PurchaseFirstActivity.this,
								PurchaseSecondActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("purchase", String.format("%.2f", Double.parseDouble(purchase)));
						bundle.putString("zuHeId", zuHeId);
						intent.putExtras(bundle);
						startActivity(intent);
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
	
	/**
	 * @description:
	 * @author Andrew Lee
	 * @version 
	 * 2014-10-27 下午5:34:59
	 */
	private void sendLimitMoney() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("uid", user_id);

		
		money = purchase_et.getText().toString();
		MyLog.i("money--uid",money+"----"+user_id);
		params.put("money", money);
		
		MyhttpClient.get(UrlConfig.LIMIT_MONEY, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				loadingdialog.dismiss();
				ToastUtil.show(PurchaseFirstActivity.this, getResources()
						.getString(R.string.default_error));
		
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				loadingdialog.dismiss();
				String code = "";
				String msg = "";
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("是否超出限额", response);

					BaseModel<?> result = new Gson().fromJson(
							response,
							BaseModel.class);
					if (result.isSuccess()) {
						
//						code=result.getData();
//						MyLog.i("code", code);
//						ToastUtil.show(context,code);
						submit();
						
					}					
					else{
						msg=result.getMsg();
						MyLog.i("msg", msg);	
						ToastUtil.show(PurchaseFirstActivity.this,msg);
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
	
}
