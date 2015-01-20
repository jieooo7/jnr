/*
 * ManageCardActivity.java
 * @author Andrew Lee
 * 2014-10-22 上午10:23:51
 */
package com.jinr.core.bankCard;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import model.BankCardInfo;
import model.BaseListModel;
import model.BaseModel;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.Check;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.RealNameActivity;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * ManageCardActivity.java description:
 * 
 * @author Andrew Lee version 2014-10-22 上午10:23:51
 */
public class FragmentCardList extends Fragment implements
		OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private TextView bank_tv; // 银行
	private TextView bank_card_tv; // 卡号
	private RelativeLayout bank_card_rl; // 银行卡
	private RelativeLayout add_bank_card_rl; // 添加银行卡

	private String user_id = "";
	
	private String mNetError;

	private String bank;
	private String bank_card;

	private LoadingDialog loadingdialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.MANAGE_CARD:
				@SuppressWarnings("unchecked")
				Map<String, String> rev = (Map<String, String>) msg.obj;
				if (Check.has_card(MainActivity.instance)) {
					bank_card_rl.setVisibility(View.VISIBLE);
					add_bank_card_rl.setVisibility(View.GONE);
					bank_tv.setText(rev.get("bank"));
					bank_card_tv.setText(rev.get("bank_card"));
				} else {
					bank_card_rl.setVisibility(View.GONE);
					add_bank_card_rl.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.manage_bank_card, container, false);
		
		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initData()
	 */
	protected void initData() {
		// TODO Auto-generated method stub
		loadingdialog = new LoadingDialog(MainActivity.instance);
		user_id = PreferencesUtils.getValueFromSPMap(MainActivity.instance,
				PreferencesUtils.Keys.UID);

		// if (!Check.has_card(MainActivity.instance)) {//请求网络

		loadingdialog.show();
		send();// 每次进去刷新数据
		// }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#findViewById()
	 */
	protected void findViewById(View n_view) {
		// TODO Auto-generated method stub

		title_left_img = (ImageView) n_view.findViewById(R.id.title_left_img);
		title_center_text = (TextView) n_view.findViewById(R.id.title_center_text);
		bank_tv = (TextView) n_view.findViewById(R.id.manage_bank_tv1);
		bank_card_tv = (TextView) n_view.findViewById(R.id.manage_bank_tv2);
		bank_card_rl = (RelativeLayout) n_view.findViewById(R.id.manage_bank_rl1);
		add_bank_card_rl = (RelativeLayout) n_view.findViewById(R.id.manage_bank_rl2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	protected void initUI() {
		// TODO Auto-generated method stub
		title_left_img.setImageResource(R.drawable.slide_button);
		title_center_text.setText(getResources().getString(
				R.string.bind_bank_card));
		if (Check.has_card(MainActivity.instance)) {
			bank_tv.setText(bank);
			bank_card_tv.setText(bank_card);

			add_bank_card_rl.setVisibility(View.GONE);

		} else {
			bank_card_rl.setVisibility(View.GONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	protected void setListener() {
		// TODO Auto-generated method stub
		add_bank_card_rl.setOnClickListener(this);
		bank_card_rl.setOnClickListener(this);
		title_left_img.setOnClickListener(this);
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(isAdded()){
			
			mNetError=getResources().getString(R.string.default_error);
		}
	}


	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		bank = PreferencesUtils.getValueFromSPMap(MainActivity.instance,
				PreferencesUtils.Keys.BANK_NAME);
		bank_card = PreferencesUtils.getValueFromSPMap(MainActivity.instance,
				PreferencesUtils.Keys.BANK_CARD_NO);

		if ("".equals(bank_card) == false) {

			bank_card = "**** **** **** "
					+ bank_card.substring(bank_card.length() - 4);
		}

		if (Check.has_card(MainActivity.instance)) {
			bank_tv.setText(bank);
			bank_card_tv.setText(bank_card);
			bank_card_rl.setVisibility(View.VISIBLE);
			add_bank_card_rl.setVisibility(View.GONE);

		} else {
			bank_card_rl.setVisibility(View.GONE);
			add_bank_card_rl.setVisibility(View.VISIBLE);
		}
	}

	protected void send() {
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
						ToastUtil.show(MainActivity.instance, mNetError);

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
										MainActivity.instance,
										PreferencesUtils.Keys.BANK_CARD_ID,
										result.getData().get(0).getId());
								PreferencesUtils.putValueToSPMap(
										MainActivity.instance,
										PreferencesUtils.Keys.BANK_CARD_NO,
										result.getData().get(0).getBank_no());
								// PreferencesUtils.putValueToSPMap(MainActivity.instance,
								// PreferencesUtils.Keys.BANK_CARD_NUM,
								// result.getData().get(0).getBank_num());
								PreferencesUtils.putValueToSPMap(
										MainActivity.instance,
										PreferencesUtils.Keys.BANK_NAME, result
												.getData().get(0)
												.getBank_name());
								PreferencesUtils.putValueToSPMap(
										MainActivity.instance,
										PreferencesUtils.Keys.OPEN_BANK, result
												.getData().get(0)
												.getOpen_bank());
								PreferencesUtils.putValueToSPMap(
										MainActivity.instance,
										PreferencesUtils.Keys.BANK_STATUS,
										result.getData().get(0)
												.getBank_status());
								PreferencesUtils
										.putValueToSPMap(
												MainActivity.instance,
												PreferencesUtils.Keys.BANK_CITY,
												result.getData().get(0)
														.getZone_shi());
								PreferencesUtils.putBooleanToSPMap(
										MainActivity.instance,
										PreferencesUtils.Keys.HAS_CARD, true);
								Message msg = Message.obtain();
								Map<String, String> map = new HashMap<String, String>();
								String card_no = result.getData().get(0)
										.getBank_no();
								map.put("bank", result.getData().get(0)
										.getBank_name());
								map.put("bank_card", TextAdjustUtil
										.getInstance().bankCardAdjust(card_no));

								msg.what = MessageWhat.MANAGE_CARD;
								msg.obj = map;
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
			MainActivity.instance.showLeftMenu();
			break;

		case R.id.manage_bank_rl2:
			if (Check.is_identify(MainActivity.instance)) {

				Intent intent = new Intent(MainActivity.instance,
						BindCardActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("is_add", false);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				Intent intent = new Intent(MainActivity.instance,
						RealNameActivity.class);
				startActivity(intent);

			}
			break;
		case R.id.manage_bank_rl1:// 银行卡详情页面
			Intent intent_detail = new Intent(MainActivity.instance,
					CardDetailActivity.class);
			Bundle bundle_rl = new Bundle();
			bundle_rl.putBoolean("is_add", false);
			intent_detail.putExtras(bundle_rl);
			startActivity(intent_detail);
			break;

		default:
			break;
		}

	}

}
