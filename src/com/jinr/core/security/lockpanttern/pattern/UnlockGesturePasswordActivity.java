package com.jinr.core.security.lockpanttern.pattern;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.CardDetailActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.more.FragmentMore;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.FragmentSecurityCenter;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.security.lockpanttern.view.LockPatternView;
import com.jinr.core.security.lockpanttern.view.LockPatternView.Cell;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

public class UnlockGesturePasswordActivity extends Activity implements OnClickListener {
	private static final String TIMEOUT_FILE = LockPatternUtils.PATH + "timoutfile.key";
	private LockPatternView mLockPatternView;
	private File timeout_file = new File(TIMEOUT_FILE);
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;
	private TextView mForgetPassword;
	private TextView unlock_username;

	private Toast mToast;
	private String localCancel = "";
	private  LockPatternUtils locku = new LockPatternUtils(this);

	private static int mFailedPatternAttemptsSinceLastTimeout = 0;
	
	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);
		AppManager.getAppManager().addActivity(this);
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		mForgetPassword = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
		unlock_username = (TextView) findViewById(R.id.unlock_username);
		mForgetPassword.setOnClickListener(this);

		Intent intent = getIntent();
		localCancel = intent.getStringExtra("LockCancel");
		
		initDate();
		
		if(localCancel == null)
			localCancel = "";

		try {
			if(timeout_file.exists())
				mFailedPatternAttemptsSinceLastTimeout = Integer.parseInt(locku.readSDFile(TIMEOUT_FILE));
			else 
				mFailedPatternAttemptsSinceLastTimeout = 0;
		} catch (NumberFormatException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}

	private void initDate(){
		String name;
		name = PreferencesUtils.getValueFromSPMap(UnlockGesturePasswordActivity.this,
				PreferencesUtils.Keys.NAME);
		
		if ("".equals(name) == false) {
			if (name.length() == 2 || name.length() == 1) {
				name = name.substring(0, 1) + "*";
			} else {
				int length = name.length();
				name = name.substring(0, 1) + "*"
						+ name.substring(length - 1, length);
			}

		}
		if(name.equals(""))
			name = "您尚未进行实名认证";
			
		unlock_username.setText(name);
	}
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (!LockPatternUtils.savedPatternExists()) {
//		//	startActivity(new Intent(this, GuideGesturePasswordActivity.class));
//			finish();
//		}
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
		MainActivity.isLock = true; //如果图形解锁退出则将图形解锁是否存在isLock 标志位记为不存在。
	
	}
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};
	
	

	/**
	 * 自定义返回键的效果
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {		
				AppManager.getAppManager().finishAllActivity();
				AppManager.getAppManager().AppExit(this);		
		}

		return true;
	}
	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			
			if (LockPatternUtils.checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
//				Intent intent = new Intent(UnlockGesturePasswordActivity.this,
//						GuideGesturePasswordActivity.class);
//				// 注释掉原来的弹出界面
//				startActivity(intent);
				
//				MainActivity.lock_off_on = false;
				

					showToast("解锁成功");
					
					try {
						locku.writeSDFile("0",TIMEOUT_FILE);
					} catch (NumberFormatException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}

				finish();
			} else {
				
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					try {
						locku.writeSDFile(mFailedPatternAttemptsSinceLastTimeout + "",TIMEOUT_FILE);
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					
					if(retry  < 0){
						retry = 0;
					}
					if (retry  >= 0) {
						if (retry == 0){
							showToast("您已5次输错密码，请重新登录");
							try {
								locku.writeSDFile("0",TIMEOUT_FILE);
							} catch (IOException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
							PreferencesUtils.clearSPMap(UnlockGesturePasswordActivity.this);
							Intent intent_LoginActivity=new Intent();	   
							intent_LoginActivity.setComponent(new ComponentName("com.jinr.core", "com.jinr.core.regist.LoginActivity"));    
							intent_LoginActivity.setAction(Intent.ACTION_VIEW);    
				            intent_LoginActivity.putExtra("unlock_forget","unlock_forget");
				            startActivityForResult(intent_LoginActivity, 0);
				            
				            
				            
						}
							
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				}else{
					showToast("输入长度不够，请重试");
				}
				
				mHandler.postDelayed(mClearPatternRunnable, 2000);
				
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};



	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		if(v.getId() == R.id.gesturepwd_unlock_forget){
			Intent intent_LoginActivity=new Intent();	   
			intent_LoginActivity.setComponent(new ComponentName("com.jinr.core", "com.jinr.core.regist.LoginActivity"));    
			intent_LoginActivity.setAction(Intent.ACTION_VIEW);    
            intent_LoginActivity.putExtra("unlock_forget","unlock_forget");
            startActivityForResult(intent_LoginActivity, 0);
            finish();
		}
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode == 0){  
            if(data != null && data.getStringExtra("return") != null){
            	MyLog.i("onActivityResult", data.getStringExtra("return"));
            	if(data.getStringExtra("return").equals("T"))
            		finish();
            }  
        }  
    }  
	
}
