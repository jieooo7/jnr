/*
 * AboutUsActivity.java
 * @author Andrew Lee
 * 2014-10-18 下午12:20:07
 */
package com.jinr.core.more;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;


/**
 * AboutUsActivity.java
 * description:
 * @author Andrew Lee
 * vesion v 1.0
 * Oct 18, 2014 3:22:37 PM
 */
public class AgreementActivity extends Base2Activity implements OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agreement);

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		title_center_text.setText(getResources().getString(
				R.string.jinr_title_service));
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

		default:
			break;
		}
	}

}

