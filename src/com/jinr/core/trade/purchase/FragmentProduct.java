/*
 * FragmentProduct.java
 * @author Andrew Lee
 * 2014-10-16 下午7:15:19
 */
package com.jinr.core.trade.purchase;

import java.io.UnsupportedEncodingException;

import model.AssetsInfo;
import model.BaseListModel;
import model.BaseModel;
import model.ZuheId;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.config.Check;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.regist.RegisterActivity;
import com.jinr.core.trade.getCash.GetCashSecondActivity;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * FragmentProduct.java
 * description:产品页面,用于购买操作
 * @author Andrew Lee
 * version 
 * 2014-10-16 下午7:15:19
 */
public class FragmentProduct extends Fragment  implements OnClickListener{
	private RelativeLayout animLayout = null;
	private RelativeLayout adjust_rl = null;
//	private ImageView adjust_iv;
	private TextView txtRate = null;
	private TextView short_title_tv = null;
//	private ImageView animImg = null;
	private AnimationDrawable animationDrawable = null;
	
	private int screenHeight, screenWidth;
	
	
	private TextView turn_in_tv;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.fragment_product, container, false);
		initData();
		findViewById(view);
		initUI();
		setListener();
		if(MainActivity.first_star){
			MainActivity.first_star = false;
			startAnim();
		}
		return view;
	}


	/**
	 * 动画结束后显示8.41%
	 */
	private void setTxtRate(){
//		animLayout.removeView(animImg);
		animLayout.addView(txtRate);
		txtRate.setText(String.format("%.2f",Double.parseDouble(MainActivity.shouyulv))+"%");
	}
	
	/**
	 * ljs 4.01% 数字上飘
	 */
	private void startAnim(){

	//		txtRate.setText("4.01%");
			txtRate.setText("0.00%");
			//设置淡出效果
	//		Animation alphaLjs = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
	//		txtRate.startAnimation(alphaLjs);
			
			Animation myAnimation1= AnimationUtils.loadAnimation(getActivity(),R.anim.anim_ljs);//加载动画
			myAnimation1.setAnimationListener(new MyAnimListen());
			animLayout.startAnimation(myAnimation1);			

	}


	
	

	/**
	 * description:
	 * @author Andrew Lee
	 * version 
	 * 2014-10-20 下午5:29:20
	 */
	private void initData() {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获取屏幕宽度
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;
		
//		ToastUtil.showLong(getActivity(), dm.toString()+"\n屏幕宽度像素:"+screenWidth+"px,换算dp："+DensityUtil.px2dip(getActivity(), screenWidth));
		
//		MyLog.i("屏幕像素", dm.toString());
	}





	/**
	 * description:
	 * @author Andrew Lee
	 * version 
	 * 2014-10-20 下午5:29:17
	 * @param view
	 */
	private void findViewById(View view) {
		// TODO Auto-generated method stub
		turn_in_tv=(TextView)view.findViewById(R.id.product_turn_in);
//		animImg = (ImageView)view.findViewById(R.id.animImg);
		txtRate = (TextView)view.findViewById(R.id.product_txt_rate);
		short_title_tv = (TextView)view.findViewById(R.id.short_title1);
		animLayout = (RelativeLayout)view.findViewById(R.id.product_ly_anim);
		adjust_rl = (RelativeLayout)view.findViewById(R.id.adjust_rl);
//		adjust_iv=(ImageView)view.findViewById(R.id.adjust_iv);
	}





	/**
	 * description:
	 * @author Andrew Lee
	 * version 
	 * 2014-10-20 下午5:29:15
	 */
	private void initUI() {
		// TODO Auto-generated method stub
		
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				screenWidth, (int) (screenWidth*110/231));
		
		adjust_rl.setLayoutParams(params1);
		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				screenWidth, (int) (screenWidth*120/231));
		txtRate.setText(String.format("%.2f",Double.parseDouble(MainActivity.shouyulv))+"%");
		short_title_tv.setText(short_title_tv.getText() + String.format("%.2f",Double.parseDouble(MainActivity.shouyulv))+"%");
//		adjust_iv.setLayoutParams(params2);
		
	}





	/**
	 * description:
	 * @author Andrew Lee
	 * version 
	 * 2014-10-20 下午5:29:08
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		turn_in_tv.setOnClickListener(this);
	}



	private double tot = 0;
	public Handler myHandler = new Handler(){//用来更新UI线程中的控件
        public void handleMessage(Message msg) {
//    		txtRate.setText(String.valueOf(msg.what));
        	if(msg.what == 9999){
        		txtRate.setText("0.00%");
        		tot = 0;
        		return;
        	}
        	
    		tot += 0.05;
    		
    		if(tot>Double.parseDouble(MainActivity.shouyulv)){
    			tot = Double.parseDouble(MainActivity.shouyulv);
    		}
    		txtRate.setText(String.format("%.2f",tot)+"%");
        }
	}; 	

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.product_turn_in:
			if(!Check.is_login(getActivity())){
				Intent intent_login=new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent_login);
			}else{
				
			Intent intent=new Intent(getActivity(),PurchaseFirstActivity.class);
			getActivity().startActivity(intent);
			}
//			startActivity(intent);
			break;

		default:
			break;
		}
	}

	class MyAnimListen implements AnimationListener{
		@Override
        public void onAnimationStart(Animation arg0) {
        	// TODO 自动生成的方法存根
        }
        
        @Override
        public void onAnimationRepeat(Animation arg0) {
        	// TODO 自动生成的方法存根
        	
        }
        
        @Override
        public void onAnimationEnd(Animation arg0) {
        	// TODO 自动生成的方法存根  同一个txtView 自增
        	myHandler.sendEmptyMessage(9999);
        	
        	
        	Thread t = new Thread(){
        		
        		private double step = 0.04;
        		private double tot = 0;
        		private int index = 0;
        		
        		public void run() {
        			try{
        				Thread.sleep(100);
        				myHandler.sendEmptyMessage(9999);
        				while(tot<Double.parseDouble(MainActivity.shouyulv)){
        					index ++;
        					Thread.sleep(10);
        					myHandler.sendEmptyMessage(index);
            				tot += step;
            			}
        				currentThread().interrupt();
        			}catch(Exception e){
        				Log.i("onAnimationEnd Error","-----");
        			}
        		};
  
        	};
        	t.start();
        }
        
	}
	

}
