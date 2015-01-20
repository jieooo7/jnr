package com.jinr.core.gift;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.GiftRule;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RuleActivity extends Base2Activity implements OnClickListener {
	
	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	
	private TextView mGiftRuleTv;
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.GIFT_RULE_MESS:
				@SuppressWarnings("unchecked")
				String rule=(String)msg.obj;
				mGiftRuleTv.setText(Html.fromHtml(rule));
				
				break;
			}
		}
	};


	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gift_rule);

		initData();
		findViewById();
		initUI();
		setListener();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		send();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		mGiftRuleTv = (TextView) findViewById(R.id.gift_rule_tv);
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.gift_rule_one));
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		title_left_img.setOnClickListener(this);
	}
	
	
	protected void send() {
		RequestParams params = new RequestParams();
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		// params.put("passwords", submit_password);

		MyhttpClient.get(UrlConfig.GIFT_RULE, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						ToastUtil.show(RuleActivity.this, getResources().getString(R.string.default_error));

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);

						try {
							String response = new String(arg2, "UTF-8");
							response = response.substring(response.indexOf("{"));
							MyLog.i("红包规则信息返回", response);
							BaseModel<GiftRule> result = new Gson()
									.fromJson(
											response,
											new TypeToken<BaseModel<GiftRule>>() {
											}.getType());
							if (result.isSuccess()) {
								String rule=result.getData().getContent();
								Message msg = Message.obtain();
								msg.what = MessageWhat.GIFT_RULE_MESS;
								msg.obj = rule;
								handler.sendMessage(msg);

							} else {
								ToastUtil.show(MainActivity.instance.getApplication(),
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
	
	
	
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.title_left_img:
			finish();
			break;

		default:
			break;
		}
	}

}
