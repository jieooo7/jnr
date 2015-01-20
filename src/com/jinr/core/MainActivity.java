package com.jinr.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import model.BaseListModel;
import model.ZuheId;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.config.AppManager;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.GiftFragment;
import com.jinr.core.guide.SplashActivity;
import com.jinr.core.more.FragmentMore;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.FragmentSecurityCenter;
import com.jinr.core.security.lockpanttern.pattern.UnlockGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.trade.FragmentIndex;
import com.jinr.core.trade.getCash.FragmentAssets;
import com.jinr.core.trade.purchase.FragmentProduct;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ScreenObserver;
import com.jinr.core.utils.ScreenObserver.ScreenStateListener;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

@SuppressLint({ "NewApi", "ResourceAsColor" }) public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener {

	HomeKeyEventBroadCastReceiver receiver; //home键监听需要的广播对象
	public static MainActivity instance = null;

	protected SlidingMenu leftRightSlidingMenu;
	protected Fragment leftFrag;		
	private ImageView btnLeft;
	private FragmentTransaction transaction;
	private FragmentAssets assets;
	private FragmentProduct product;
	private FragmentIndex  m_FragmentIndex;
	private FragmentSecurityCenter m_FragmentSecuCenter;
	private FragmentCardList m_FragmentCardList;
	private FragmentMore m_FragmentMore;
	private GiftFragment mGiftFragment;
	private TextView product_tv;
	private TextView assets_tv;
	private static Boolean isExit = false;// 双击退出
	public static boolean isLock = true;//图形解锁开关 ，防止Home持续产生图形解锁界面
	public static boolean isLock_longin = true;
	private boolean is_product = true;// 产品 资产切换标记
	public static boolean homekey = false;
	public static Boolean first_star = false; //首页利率跳转开关

	public static String shouyulv = "8.2";
	private String TAG = "ScreenObserverActivity"; //屏幕休眠监听，日志
	private ScreenObserver mScreenObserver; //屏幕休眠监听对象
	private String datestr;
	
	public LockPatternUtils lockp = new LockPatternUtils(this);
	private String shouyulvFileName = lockp.PATH + "ShouyulvDate";	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		 MobclickAgent.setDebugMode(true);//打开普通调试模式
		// MobclickAgent.updateOnlineConfig(this);//发送策略,分析以versionname 为准
		// 打开调试模式（debug）后，数据实时发送，不受发送策略控制
		UmengUpdateAgent.setChannel(UrlConfig.PLATFORM);
		UmengUpdateAgent.setAppkey("54509b5bfd98c5a643021d81");

		// UmengUpdateAgent.setUpdateOnlyWifi(false);任何情况下更新
		// UmengUpdateAgent.setUpdateCheckConfig(false);//禁用更新集成检测
		UmengUpdateAgent.update(getApplication());// 更新 以version code为准
		FragmentIndex.is_product = true;
		
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
			@Override
			public void onScreenOn() {//屏幕苏醒则执行
				
			}

			@Override
			public void onScreenOff() { //屏幕休眠则执行
				gotoLockPattern(); 
			}
		});

		MyLog.i("MainActive加载","------");
		try {
			upShouyulv();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		gotoLockPattern(); //进入手势密码页		
		gotoGuide(); //进入引导页
		initLeftRightSlidingMenu();
		setContentView(R.layout.main);
		initData();
		
		instance = this;
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(
		Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}
	
	
	
	/**
	 * 1、savedPatternExists()，图形解锁的密码数据文件需要存在，且图形解锁开关打开。
	 * 2、Check.is_login(this)，用户的登录状态需要为已登录。
	 * 3、isLock，当前没有图形解锁页面打开，防止重复打开图形锁
	 * 4、isLock_longin，使图形解锁一次无法打开，在如登录成功，和返回首页跳转，重新加载的情况。
	 * @author fym
	 * 跳转值图形锁页面，内部跳转判断条件为
	 */
	public void gotoLockPattern(){
		MyLog.i("主页跳转图形锁",lockp.savedPatternExists()+"+"+Check.is_login(this)+"+"+isLock +isLock_longin );
		//判断是否进入图形锁，分别是“是否图形解锁文件存在图形解锁开关是否打开”，“是否在登录状态”，“是已有否图形锁”，“是否登录成功或因为返回首页按键重新加载首页”
		if (lockp.savedPatternExists() && Check.is_login(this) && isLock && isLock_longin){
				isLock = false;
				startActivity(new Intent(this,UnlockGesturePasswordActivity.class));			
		}
		isLock_longin = true;
	}
	private void gotoGuide(){
		SharedPreferences preferences = getSharedPreferences(
				"first_pref", MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (preferences.getBoolean("isFirstIn", true)) {
			startActivity(new Intent(this,SplashActivity.class));
		}
	}
	private void upShouyulv() throws IOException{
		int mDay = 0,mMonth = 0;
		String dat = lockp.readSDFile(shouyulvFileName);
		String spDat[] = dat.split(",");
		Calendar c = Calendar.getInstance(); 
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		datestr = "" + mMonth + mDay;
		if(!spDat[0].equals(datestr)){
			send();
		}
		else{
			shouyulv = spDat[1];
			first_star = true;
		}	
	}
	private void initLeftRightSlidingMenu() {
		setBehindContentView(R.layout.left_menu_layout);

		FragmentTransaction leftFragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		leftFrag = new LeftSlidingMenuFragment();
		leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
		leftFragementTransaction.commit();

		leftRightSlidingMenu = getSlidingMenu();

		leftRightSlidingMenu.setMode(SlidingMenu.LEFT);// 设置是左滑还是右滑，还是左右都可以滑，我这里只做了左滑
		leftRightSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置菜单宽度
		leftRightSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例

		leftRightSlidingMenu
				.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置手势模式
		leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左菜单阴影图片
		leftRightSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		leftRightSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
		leftRightSlidingMenu.setShadowWidth(20);
	}

	protected void initData() {
		// TODO Auto-generated method stub
		m_FragmentIndex = new FragmentIndex();
		((LeftSlidingMenuFragment) leftFrag).showindex();
		if (findViewById(R.id.container_fragment) != null) {
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentIndex);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(homekey){ //返回时，如果home键标识打开则打开图形解锁
			homekey = false;
			gotoLockPattern();
		}
	}
	


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
		// 结束Activity
		AppManager.getAppManager().finishActivity(this);
		MyLog.i("MainActivity", "onDestroy");
	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			// Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			ToastUtil.show(this, getResources()
					.getString(R.string.back_to_exit));
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 1000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			MobclickAgent.onKillProcess(this);
			// finish();
			AppManager.getAppManager().finishAllActivity();
			AppManager.getAppManager().AppExit(this);
			// System.exit(0);
		}
	}
	
	public void hideLeftMenu(){
		leftRightSlidingMenu.toggle();
	}
	
	public void showLeftMenu()
	{
		leftRightSlidingMenu.showMenu();
	}
	
	
	public void changeBankView(int index)
	{
		switch (index) {
		case 1:
			if(m_FragmentCardList == null)
			{
				m_FragmentCardList = new FragmentCardList();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentCardList);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			FragmentIndex.is_product = true;
			break;
			}}
	
	public void changeRightView(int index)
	{
		switch (index) {
		case 0:
			if(m_FragmentIndex == null)
			{
				m_FragmentIndex = new FragmentIndex();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentIndex);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			break;
		case 1:
			if(m_FragmentCardList == null)
			{
				m_FragmentCardList = new FragmentCardList();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentCardList);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			break;
		case 2:
			if(m_FragmentSecuCenter == null)
			{
				m_FragmentSecuCenter = new FragmentSecurityCenter();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentSecuCenter);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			break;
		case 3:
			if(m_FragmentMore == null)
			{
				m_FragmentMore = new FragmentMore();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, m_FragmentMore);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			break;
		case 4://添加红包代码
			if(mGiftFragment == null)
			{
				mGiftFragment = new GiftFragment();
			}
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container_fragment, mGiftFragment);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
			break;
		default:
			break;
		}
		leftRightSlidingMenu.toggle();
		FragmentIndex.is_product = true;
	}

	/**
	 * 自定义返回键的效果
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		

		if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回键

			exitBy2Click();
			// overridePendingTransition(R.anim.translate_horizontal_finish_in,
			// R.anim.translate_horizontal_finish_out);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.mainBtnLeft:  
			leftRightSlidingMenu.showMenu();
			((LeftSlidingMenuFragment) leftFrag).showindex();
			break;
		case R.id.main_product_tv:// 左边产品按钮
			if (!is_product) {
				product_tv.setBackground(getResources().getDrawable(R.drawable.rectangle_solid));
				product_tv.setTextColor(R.color.white);
				assets_tv.setBackground(getResources().getDrawable(R.drawable.rectangle));
				product_tv.setTextColor(R.color.main_text_color);
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, product);
				transaction.addToBackStack(null);
				transaction.commit();
//				product_tv.setTextColor(Color.rgb(255, 255, 255));
//				assets_tv.setTextColor(Color.rgb(136, 136, 136));
				is_product = true;
			}
			break;
		case R.id.main_assets_tv:// 右边资产按钮
			if (is_product) {

				if (Check.is_login(MainActivity.this)) {

					product_tv.setBackground(getResources().getDrawable(R.drawable.rectangle_left));
					product_tv.setTextColor(R.color.main_text_color);
					assets_tv.setBackground(getResources().getDrawable(R.drawable.rectangle_solid_right));
					product_tv.setTextColor(R.color.white);
					transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.container, assets);
					transaction.addToBackStack(null);
					transaction.commit();
					is_product = false;
//					assets_tv.setTextColor(Color.rgb(255, 255, 255));
//					product_tv.setTextColor(Color.rgb(136, 136, 136));
				} else {
					Intent intent_login = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent_login);

				}

			}
			break;
		default:

			break;
		}
	}
	/**
	 * 对home广播监听，如果监听到则将标志homekey打开使在resume时打开图形锁，
	 * 如果在图形锁打开的情况下监听到home键则结束所有Active
	 * @author fym
	 */
	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home key处理点
						Log.e("homekey", "home键被点击");
						if(!isLock){//如果存在图形解锁，则结束所有Active
							AppManager.getAppManager().finishAllActivity();
						}
							homekey = true;					
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long homekey处理点
						 Log.e("homekey", "长按home键");
						
					}
				}
			}
		}
	}
	
	protected void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		
		MyhttpClient.post(UrlConfig.GET_ZUHEID, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);

//				ToastUtil.show(getActivity(), getResources().getString(R.string.default_error));
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("获取组合id信息返回", response);
					BaseListModel<ZuheId> result=new Gson().fromJson(response,
							new TypeToken<BaseListModel<ZuheId>>() {
					}.getType());
					if(result.isSuccess()){
						shouyulv=result.getData().get(0).getShouyulv();
						MyLog.i(shouyulvFileName, datestr + shouyulv);
						first_star = true;
						lockp.writeSDFile(datestr + "," + shouyulv, shouyulvFileName);
					}else{

					}					
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			
			
		});
		

	}
}
