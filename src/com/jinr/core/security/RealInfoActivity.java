package com.jinr.core.security;

import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class RealInfoActivity extends Base2Activity implements OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	
	private TextView name_tv;
	private TextView mobile_tv;
	private TextView id_card_tv;
	
	private String name;
	private String mobile;
	private String id_card;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_info);

		initData();
		findViewById();
		initUI();
		setListener();
	}

	
	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		name=PreferencesUtils.getValueFromSPMap(RealInfoActivity.this,
				PreferencesUtils.Keys.NAME);
		mobile=PreferencesUtils.getValueFromSPMap(RealInfoActivity.this,
				PreferencesUtils.Keys.TEL);
		id_card=PreferencesUtils.getValueFromSPMap(RealInfoActivity.this,
				PreferencesUtils.Keys.ID_CARD);
		
		name=TextAdjustUtil.getInstance().nameAdjust(name);
		mobile=TextAdjustUtil.getInstance().mobileAdjust(mobile);
		id_card=TextAdjustUtil.getInstance().idCardAdjust(id_card);
		
		
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		name_tv = (TextView) findViewById(R.id.real_info_name_tv);
		mobile_tv = (TextView) findViewById(R.id.real_info_mobile_tv);
		id_card_tv = (TextView) findViewById(R.id.real_info_id_tv);
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		
		title_center_text.setText(getResources().getString(
				R.string.person_info_title));
		name_tv.setText(name);
		mobile_tv.setText(mobile);
		id_card_tv.setText(id_card);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
		title_left_img.setOnClickListener(this);
		
		
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
