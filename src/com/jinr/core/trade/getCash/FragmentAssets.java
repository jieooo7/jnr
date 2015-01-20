/*
 * Fragment.java
 * @author Andrew Lee
 * 2014-10-16 下午7:11:50
 */
package com.jinr.core.trade.getCash;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import model.AssetsInfo;
import model.BaseModel;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.BindCardActivity;
import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.config.Check;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.security.DealPasswdActivity;
import com.jinr.core.security.RealNameActivity;
import com.jinr.core.security.FragmentSecurityCenter;
import com.jinr.core.trade.record.TradeRecordActivity;
import com.jinr.core.ui.CustomDialog;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Fragment.java
 * description:我的资产界面,可以进行转出操作
 * @author Andrew Lee
 * version 
 * 2014-10-16 下午7:11:50
 */
public class FragmentAssets extends Fragment implements OnClickListener{
	
	
	private TextView turn_out_tv;
	private TextView total_assets_tv;
	private TextView total_revenue_tv;
	private TextView yestday_money_tv;
	private TextView all_yield_money_tv;
	private TextView cheak_record_tv;
	private TextView freeze_money_tv;
	private int j=0;
	private int startx;  
    private int starty; 
    
//    private LoadingDialog loadingdialog;
    
    private int screenWidth = 0;
    private int screenHeigh = 0;
    
    // 取得对应数据后，才可托动
    private boolean canDrag = false;
    private boolean ly2JustOk = false;
    
    
    //托动中间点的高度值
    private int midNum = 0;
    private int ly2Height = 0;
    private int dragHeight = 0;
    private int preY = 0;
    
	private ImageView dragImg = null;
	
	
	private RelativeLayout ly_assets;
	private LinearLayout ly_assets_1;
	private LinearLayout ly_assets_2;
	
	private HorizontalScrollView hScroll;
	
	private String mNetError="";
	
	private boolean[] arrayBool=new boolean[]{false,false,false,false,false,false,false};
//	private boolean[] arrayBool={false,false,false,false,false,false,false};
	private TextView[] arrayTextCircle=new TextView[7];
	private TextView[] arrayTextNo=new TextView[7];
	private TextView[] arrayTextDate=new TextView[7];
	
	private TextView line_tv;
	
	private int[] arrayTextNoInt={R.id.ly_asset20_1,R.id.ly_asset20_2,R.id.ly_asset20_3,R.id.ly_asset20_4,R.id.ly_asset20_5,R.id.ly_asset20_6,R.id.ly_asset20_7};
	private int[] arrayTextInt={R.id.ly_asset21_1,R.id.ly_asset21_2,R.id.ly_asset21_3,R.id.ly_asset21_4,R.id.ly_asset21_5,R.id.ly_asset21_6,R.id.ly_asset21_7};
	private int[] arrayTextDateInt={R.id.ly_asset22_1,R.id.ly_asset22_2,R.id.ly_asset22_3,R.id.ly_asset22_4,R.id.ly_asset22_5,R.id.ly_asset22_6,R.id.ly_asset22_7};
	
	private String[] time=new String[7];
	private String user_id = "";

	private String money = "";
	private String yield_money = "";
	private String pre_yield_money = "";
	private String all_yield_money = "";
	private String freeze_money = "";
	private String mYestGift = "";
	private String mGift = "";
	private String mTotalMoney = "";

	private CustomDialog dialog;
	
	private Runnable runScroll=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			hScroll.scrollTo(line_tv.getMeasuredWidth() - hScroll.getWidth(), 0);
		}
	};
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageWhat.ASSET:
				@SuppressWarnings("unchecked")
				Bundle result = (Bundle) msg.obj;
				BaseModel<AssetsInfo> assetInfo=(BaseModel<AssetsInfo>) result.getSerializable("asset");
				money = assetInfo.getData().getMoney();
				yield_money = assetInfo.getData().getYield_money();
				pre_yield_money = assetInfo.getData().getPre_yield_money();
				freeze_money = assetInfo.getData().getFreeze_money();
				mYestGift=assetInfo.getData().getHobao_pre_yield_money();
				mGift=assetInfo.getData().getHobao_y_money();
				mTotalMoney=assetInfo.getData().getTotal_money();
					
				if(money == null||"".equals(money))
					money = "0.00";
				if(freeze_money == null||"".equals(freeze_money))
					freeze_money = "0.00";
				if(yield_money == null||"".equals(yield_money))
					yield_money = "0.00";
				if(pre_yield_money == null||"".equals(pre_yield_money))
					pre_yield_money = "0.00";			
				if(mYestGift == null||"".equals(mYestGift))
					mYestGift = "0.00";			
				if(mGift == null||"".equals(mGift))
					mGift = "0.00";	
				if(mTotalMoney == null||"".equals(mTotalMoney))
					mTotalMoney = "0.00";	
				mYestGift="\n("+TextAdjustUtil.getInstance().formatNum(mYestGift)+")";
				mGift="\n("+TextAdjustUtil.getInstance().formatNum(mGift)+")";
				mTotalMoney="\n"+TextAdjustUtil.getInstance().formatNum(mTotalMoney);	
				
				
				total_assets_tv.setText(TextAdjustUtil.getInstance().formatNum(money)+mTotalMoney);
				
				
				total_revenue_tv.setText(TextAdjustUtil.getInstance().formatNum(yield_money)+mGift);
				yestday_money_tv.setText(TextAdjustUtil.getInstance().formatNum(pre_yield_money)+mYestGift);
				all_yield_money_tv.setText(TextAdjustUtil.getInstance().formatNum(yield_money)+mGift);
				freeze_money_tv.setText(TextAdjustUtil.getInstance().formatNum(freeze_money));

				
				
				break;
			}
		}
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.fragment_assets, container, false);
		
		initData();
		findViewById(view);
		initUI();
		initTimeUi();
		setListener();
		return view;
	}
	
	
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(isAdded()){
			
			mNetError=getResources().getString(R.string.default_error);
		}
	}




	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		if(!Check.is_login(getActivity())){//判断是否登录
//			Intent intent_login=new Intent(getActivity(),LoginActivity.class);
//			getActivity().startActivity(intent_login);
//		
//		}else{
//			
//		}
		total_assets_tv.setText(money);
		total_revenue_tv.setText(yield_money);
		yestday_money_tv.setText(pre_yield_money);
		all_yield_money_tv.setText(yield_money);
		freeze_money_tv.setText(freeze_money);
		send();
		
	}




	private void initTime(){
		
		time[6]="今天";
		
		
		for(int i=6;i>0;i--){
			Date date = new Date(System.currentTimeMillis()-1000 * 60 * 60 * 24*i);
			SimpleDateFormat sp = new SimpleDateFormat("MM月dd日");
			time[6-i]=sp.format(date);
		}
	}
	
	
	private void initTimeUi(){
		for(int i=0;i<arrayTextDate.length;i++){
			
			arrayTextDate[i].setText(time[i]);
			
		}
		for(int i=0;i<arrayTextNo.length;i++){
			
			arrayTextNo[i].setText(MainActivity.instance.shouyulv);
			
		}
//		arrayTextCircle[6].setFocusable(true);
		
	}
	
	
	private void initData() {
		// TODO Auto-generated method stub
		
		initTime();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获取屏幕宽度
		screenHeigh = dm.heightPixels;
		screenWidth = dm.widthPixels;
		user_id=PreferencesUtils.getValueFromSPMap(this.getActivity(), PreferencesUtils.Keys.UID);
		MyLog.i("screenWidth === ",String.valueOf(screenWidth));
		
	}
	
	private void findViewById(View view){
		ly_assets = (RelativeLayout)view.findViewById(R.id.ly_assets_tmp);
		ly_assets_1 = (LinearLayout)view.findViewById(R.id.ly_assets_1);
		ly_assets_2 = (LinearLayout)view.findViewById(R.id.ly_2);
		dragImg = (ImageView)view.findViewById(R.id.dragImg);
		turn_out_tv = (TextView)view.findViewById(R.id.fragment_turn_out);
		total_assets_tv = (TextView)view.findViewById(R.id.total_assets);
		total_revenue_tv = (TextView)view.findViewById(R.id.total_revenue);
		yestday_money_tv = (TextView)view.findViewById(R.id.yestday_money);
		all_yield_money_tv = (TextView)view.findViewById(R.id.all_yield_money);
		cheak_record_tv = (TextView)view.findViewById(R.id.cheak_record);

		freeze_money_tv = (TextView)view.findViewById(R.id.get_cash_now);
		line_tv = (TextView)view.findViewById(R.id.ly_asset2_line);

		
		hScroll=(HorizontalScrollView)view.findViewById(R.id.ly_assets2_scroll);
		
		for(int i=0;i<arrayTextCircle.length;i++){
			arrayTextNo[i]=(TextView)view.findViewById(arrayTextNoInt[i]);
			arrayTextCircle[i]=(TextView)view.findViewById(arrayTextInt[i]);
			arrayTextDate[i]=(TextView)view.findViewById(arrayTextDateInt[i]);
			
		}
	}

	private void initUI(){
		ly_assets.removeView(ly_assets_2);
		
		Thread t = new Thread(){
    		public void run() {
    			try{
//    				hScroll.scrollTo(line_tv.getMeasuredWidth() - hScroll.getWidth(), 0);
    				
    				Thread.sleep(300);
    				myHandler.sendEmptyMessage(0);
    				myHandler.postDelayed(runScroll, 150);
    				Thread.sleep(50);
    				myHandler.sendEmptyMessage(1);
    				Thread.sleep(250);
    				myHandler.sendEmptyMessage(99);
    			}catch(Exception e){
    				MyLog.i("onAnimationEnd Error","-----");
    			}
    		};

    	};
    	t.start();
	}

	//托动前设置基础数据
	public Handler myHandler = new Handler(){//用来更新UI线程中的控件
        public void handleMessage(Message msg) {
        	if(msg.what == 0){
        		ly_assets.addView(ly_assets_2);
//        		hScroll.scrollTo(hScroll.getWidth(), 0);
        	}else if(msg.what == 99){
        		ly2JustOk = true;
        		MyLog.i("ly2JustOk","==================ok");
        	}else{
        		preY = dragImg.getTop();
            	MyLog.i("------->preY = ",String.valueOf(preY));
            	dragHeight = dragImg.getHeight();
            	MyLog.i("------->preY height = ",String.valueOf(dragHeight));
            	int ll = ly_assets_2.getLeft();  
                int rr = ly_assets_2.getRight();  
                int tt = ly_assets_2.getTop();  
                int bb = ly_assets_2.getBottom(); 
                MyLog.i("ll -- ", String.valueOf(ll));
                MyLog.i("rr -- ", String.valueOf(rr));
                MyLog.i("tt -- ", String.valueOf(tt));
                MyLog.i("bb -- ", String.valueOf(bb));
                ly2Height = ly_assets_2.getHeight();
        		MyLog.i("-------> height = ",String.valueOf(ly2Height));
        		ly_assets_2.layout(ll, preY-ly2Height - dragHeight*2, rr, preY - dragHeight*2);
        	}
        }
	}; 	
	
	private void setListener(){
		dragImg.setOnTouchListener(new DragOnTouch());
		turn_out_tv.setOnClickListener(this);
		cheak_record_tv.setOnClickListener(this);
		
		for(int i=0;i<arrayTextCircle.length;i++){
			
			arrayTextCircle[i].setOnClickListener(this);
					
//					if(arrayBool[j]==false){
//						arrayTextNo[j].setVisibility(View.VISIBLE);
//						
//						arrayBool[j]=true;
//						
//					}else{
//						arrayTextNo[j].setVisibility(View.INVISIBLE);
//						arrayBool[j]=false;
//					}
//					
//				}
//			});
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO 自动生成的方法存根
		switch (arg0.getId()){
		case R.id.fragment_turn_out:
			
			if(!Check.is_login(getActivity())){//判断是否登录,实名 银行卡 交易密码
				Intent intent_login=new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent_login);
			
			}else{
				if(!Check.is_identify(getActivity())){
					if(!Check.has_deal_passwd(getActivity())){
						
						dialog = new CustomDialog(getActivity(),
								getString(R.string.warning),
								getString(R.string.dialog_real_name_one));
					}else{
						dialog = new CustomDialog(getActivity(),
								getString(R.string.warning),
								getString(R.string.dialog_real_name));
					}
					dialog.btn_custom_dialog_sure
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									
									Intent intent_real=new Intent(getActivity(),RealNameActivity.class);
									getActivity().startActivity(intent_real);
									dialog.dismiss();
								}
							});
					
					dialog.show();
					
					
					
				}else{
					if(!Check.has_card(getActivity())){
						if(!Check.has_deal_passwd(getActivity())){
							dialog = new CustomDialog(getActivity(),
									getString(R.string.warning),
									getString(R.string.dialog_bind_card_two));
						}else{
							dialog = new CustomDialog(getActivity(),
									getString(R.string.warning),
									getString(R.string.dialog_bind_card));
						}
						dialog.btn_custom_dialog_sure
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
					
										MainActivity.instance.changeBankView(1);
//										Intent intent_card=new Intent(getActivity(),BindCardActivity.class);
//										getActivity().startActivity(intent_card);
										dialog.dismiss();
									}
								});
						dialog.show();
					}else{
						if(!Check.has_deal_passwd(getActivity())){
							
							dialog = new CustomDialog(getActivity(),
									getString(R.string.warning),
									getString(R.string.dialog_deal_passwd));
							dialog.btn_custom_dialog_sure
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
						
											Intent intent_deal=new Intent(getActivity(),DealPasswdActivity.class);
											getActivity().startActivity(intent_deal);
											dialog.dismiss();
										}
									});
							dialog.show();
						}else{
							
							Intent intent=new Intent(getActivity(),GetCashFirstActivity.class);
							getActivity().startActivity(intent);
							
							
						}
					}
				}
			}
			
//			Intent intent=new Intent(getActivity(),GetCashFirstActivity.class);
//			getActivity().startActivity(intent);
			
			break;
		case R.id.cheak_record:
			startActivity(new Intent(this.getActivity(),TradeRecordActivity.class));
//			MainActivity.instance.changeBankView(1);
			
		case R.id.ly_asset21_1:
			
			if(arrayBool[0]==false){
				arrayTextNo[0].setVisibility(View.VISIBLE);
				
				arrayBool[0]=true;
				
			}else{
				arrayTextNo[0].setVisibility(View.INVISIBLE);
				arrayBool[0]=false;
			}
			
			break;
		case R.id.ly_asset21_2:
			
			if(arrayBool[1]==false){
				arrayTextNo[1].setVisibility(View.VISIBLE);
				
				arrayBool[1]=true;
				
			}else{
				arrayTextNo[1].setVisibility(View.INVISIBLE);
				arrayBool[1]=false;
			}
			
			break;
		case R.id.ly_asset21_3:
			
			if(arrayBool[2]==false){
				arrayTextNo[2].setVisibility(View.VISIBLE);
				
				arrayBool[2]=true;
				
			}else{
				arrayTextNo[2].setVisibility(View.INVISIBLE);
				arrayBool[2]=false;
			}
			
			break;
		case R.id.ly_asset21_4:
			
			if(arrayBool[3]==false){
				arrayTextNo[3].setVisibility(View.VISIBLE);
				
				arrayBool[3]=true;
				
			}else{
				arrayTextNo[3].setVisibility(View.INVISIBLE);
				arrayBool[3]=false;
			}
			
			break;
		case R.id.ly_asset21_5:
			
			if(arrayBool[4]==false){
				arrayTextNo[4].setVisibility(View.VISIBLE);
				
				arrayBool[4]=true;
				
			}else{
				arrayTextNo[4].setVisibility(View.INVISIBLE);
				arrayBool[4]=false;
			}
			
			break;
		case R.id.ly_asset21_6:
			
			if(arrayBool[5]==false){
				arrayTextNo[5].setVisibility(View.VISIBLE);
				
				arrayBool[5]=true;
				
			}else{
				arrayTextNo[5].setVisibility(View.INVISIBLE);
				arrayBool[5]=false;
			}
			
			break;
		case R.id.ly_asset21_7:
			
			if(arrayBool[6]==false){
				arrayTextNo[6].setVisibility(View.VISIBLE);
				
				arrayBool[6]=true;
				
			}else{
				arrayTextNo[6].setVisibility(View.INVISIBLE);
				arrayBool[6]=false;
			}
			
			break;
		default:
			break;
		
		
		}
		
	}
	
	class DragOnTouch implements OnTouchListener{
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO 自动生成的方法存根
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕  
				if(!canDrag || !ly2JustOk){
					return false;
				}
				
				if(preY == 0){
					preY = dragImg.getTop();
					midNum = (screenHeigh - preY * 2) / 2;
				}
				
                int ll = ly_assets_2.getLeft();  
                int rr = ly_assets_2.getRight();  
                int tt = ly_assets_2.getTop();  
                int bb = ly_assets_2.getBottom(); 
                
				startx = (int) event.getRawX();  
                starty = (int) event.getRawY();  
				break;

			case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件  
				if(!canDrag || !ly2JustOk){
					return false;
				}
				
				int x = (int) event.getRawX();  
                int y = (int) event.getRawY();  
                
                // 获取手指移动的距离  
                int dx = x - startx;  
                int dy = y - starty;  
                // 得到imageView最开始的各顶点的坐标  
                int l = dragImg.getLeft();  
                int r = dragImg.getRight();  
                int t = dragImg.getTop();  
                int b = dragImg.getBottom();  
  
                if(t + dy < preY){
                	dy = t - preY;
                }
                
                // 更改imageView在窗体的位置  
                dragImg.layout(l, t + dy, r, b + dy);
                
                ll = ly_assets_2.getLeft();  
                rr = ly_assets_2.getRight();  
                tt = ly_assets_2.getTop();  
                bb = ly_assets_2.getBottom(); 
                
                t = dragImg.getTop();  
                
                int tmpY = t;
                if( tmpY >= (preY + ly2Height + dragHeight-50)){
                	tmpY = preY + ly2Height + dragHeight-50;
                }
                
                ly_assets_2.layout(ll, tmpY-ly2Height - dragHeight*2+19, rr, tmpY - dragHeight*2+19);
                
                // 获取移动后的位置  
                startx = (int) event.getRawX();  
                starty = (int) event.getRawY();  
  
				break;
				
			case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件  
				MyLog.i("ACTION_UP", "手指离开屏幕");  
				break;
			}
			
			return true;
		}
	}
	

	
	private void send() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();  
		params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
		params.put("yohid", user_id);
//		params.put("passwords", submit_password);
		MyLog.i("yohid", "--"+user_id);
		MyhttpClient.get(UrlConfig.GET_MONEY_NOW, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
//				loadingdialog.dismiss();
				ToastUtil.show(getActivity() ,"网络错误,请重试" );
		
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
		//		loadingdialog.dismiss();
				
				try {
					String response=new String(arg2,"UTF-8");
					response=response.substring(response.indexOf("{"));
					MyLog.i("账户金额", response);
					BaseModel<AssetsInfo> result=new Gson().fromJson(response,
							new TypeToken<BaseModel<AssetsInfo>>() {
					}.getType());
					if(result.isSuccess()){
						Message msg = Message.obtain();
						Bundle bundle=new Bundle();
						bundle.putSerializable("asset", result);
						
						msg.what = MessageWhat.ASSET;
						msg.obj = bundle;
						handler.sendMessage(msg);
						//						MyLog.i("can_use_money", can_use_money);
//						msg.what = MessageWhat.GET_CASH;
//						msg.obj=can_use_money;
//						handler.sendMessage(msg);
						
						
						//数据获取成功
						canDrag = true;
						
						Log.i("canDrag","==================ok");
					}else{

//						ToastUtil.show(getApplication(), result.getMsg());
					}
					
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					MyLog.i("json解析错误", "解析错误");
				}catch(NumberFormatException e){
					
					
				}catch(IndexOutOfBoundsException e){
					
					
				}
				
				
			}
			
			
		});
		
	}
	
}
