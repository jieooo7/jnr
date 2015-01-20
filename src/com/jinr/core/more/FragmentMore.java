/*
 * MoreActivity.java
 * @author Andrew Lee
 * 2014-10-18 上午11:27:21
 */
package com.jinr.core.more;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.Base2Activity;
import com.jinr.core.config.Check;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.lockpanttern.pattern.LockPasswordUtils;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.ui.CustomDialog;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * MoreActivity.java description:
 * 
 * @author Andrew Lee version 2014-10-18 上午11:27:21
 */
public class FragmentMore extends Fragment implements OnClickListener {

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题
	private TextView more_version_text; // 版本号
	private RelativeLayout about_us; // 关于我们
	private RelativeLayout check_update; // 检查更新
	private RelativeLayout call_us; // 联系我们
	
	private Button logout_bt;
	
	private String login_text="";
	private String version="";
	
	private String mUpdateInfo="";
	
	
	private CustomDialog dialog;
	private LoadingDialog loadingdialog;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.the_more, container, false);
		
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
		if(Check.is_login(MainActivity.instance)){
			login_text=getResources().getString(R.string.logout);
			
		}else{
			
			login_text=getResources().getString(R.string.login_in);
			
		}
		
		try {
			
			 // 获取packagemanager的实例
	        PackageManager packageManager = getActivity().getPackageManager();
	        // getPackageName()是你当前类的包名，0代表是获取版本信息
	        PackageInfo packInfo;

			packInfo = packageManager.getPackageInfo(getActivity().getPackageName(),0);
			
			version = packInfo.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		more_version_text = (TextView) n_view.findViewById(R.id.more_version);
		about_us = (RelativeLayout) n_view.findViewById(R.id.the_more_rl1);
		check_update = (RelativeLayout) n_view.findViewById(R.id.the_more_rl2);
		call_us = (RelativeLayout) n_view.findViewById(R.id.the_more_rl3);
		logout_bt = (Button) n_view.findViewById(R.id.logout_submit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#initUI()
	 */
	protected void initUI() {
		// TODO Auto-generated method stub
		title_left_img.setImageResource(R.drawable.slide_button);
		title_center_text.setText(getResources().getString(R.string.more));
		logout_bt.setText(login_text);
		more_version_text.setText(getResources().getString(R.string.current_version)+version);

	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(isAdded()){
			
			mUpdateInfo=getResources().getString(R.string.update_tips);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jinr.core.base.Base2Activity#setListener()
	 */
	protected void setListener() {
		// TODO Auto-generated method stub
		about_us.setOnClickListener(this);
		
		title_left_img.setOnClickListener(this);
		check_update.setOnClickListener(this);
		call_us.setOnClickListener(this);
		logout_bt.setOnClickListener(this);

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
		case R.id.the_more_rl1: // 关于我们
			Intent intent_rl1 = new Intent(MainActivity.instance,
					AboutUsActivity.class);
			startActivity(intent_rl1);
			break;
		case R.id.the_more_rl2:// 版本检测
			UmengUpdateAgent.forceUpdate(getActivity());
				
			
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			    @Override
			    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			        switch (updateStatus) {
			        case UpdateStatus.No: // has no update
			        	ToastUtil.show(getActivity(), "当前已经是最新版了");
			            break;
			        default:
						break;

			        }
			    }

			});

			break;
		case R.id.the_more_rl3:// 联系客服
			
			
			dialog = new CustomDialog(MainActivity.instance,
					getString(R.string.warning),
					getString(R.string.dialog_call));
			dialog.btn_custom_dialog_sure.setText(getString(R.string.dialog_call_bt));
			dialog.btn_custom_dialog_sure
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:"
											+ getString(R.string.contact_us_tel).trim()));
							startActivity(intent);
							dialog.dismiss();
						}
					});
			

			dialog.show();
			
			break;
		case R.id.title_left_img://title左侧图标
			MainActivity.instance.showLeftMenu();
			break;
		case R.id.logout_submit://退出当前账号
			PreferencesUtils.clearSPMap(MainActivity.instance);
			Intent intent=new Intent(MainActivity.instance,LoginActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
