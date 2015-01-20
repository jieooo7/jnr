package com.jinr.core.trade;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.config.Check;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.trade.getCash.FragmentAssets;
import com.jinr.core.trade.purchase.FragmentProduct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class FragmentIndex extends Fragment implements OnClickListener{
	
	private FragmentTransaction transaction;
	private FragmentAssets assets;
	private FragmentProduct product;
	
	private TextView product_tv;
	private TextView assets_tv;
	private ImageView btnLeft;
	
	public static boolean is_product = true;// 产品 资产切换标记
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		
		View view =inflater.inflate(R.layout.fragment_index, container, false);
				
		initData(view);
		findViewById(view);
		setListener();
		return view;
	}
	
	private void findViewById(View view){
		btnLeft = (ImageView) view.findViewById(R.id.mainBtnLeft);
		product_tv = (TextView) view.findViewById(R.id.main_product_tv);
		assets_tv = (TextView) view.findViewById(R.id.main_assets_tv);
	}
	
	protected void setListener() {
		// TODO Auto-generated method stub
		btnLeft.setOnClickListener(this);
		product_tv.setOnClickListener(this);
		assets_tv.setOnClickListener(this);
	}
	
	protected void initData(View view) {
		// TODO Auto-generated method stub
		assets = new FragmentAssets();
		product = new FragmentProduct();
		if (view.findViewById(R.id.container) != null) {
			transaction = this.getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, product);
			transaction.addToBackStack(null);// 加入回退栈,保存状态 back键 回退
			transaction.commit();
		}
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v)	{
		switch (v.getId()) {
		case R.id.mainBtnLeft:
			MainActivity.instance.showLeftMenu();
			break;
		case R.id.main_product_tv:// 左边产品按钮
			if (!is_product) {
				product_tv.setBackgroundResource(R.drawable.rectangle_solid);
				product_tv.setTextColor(R.color.white);
				assets_tv.setBackgroundResource(R.drawable.rectangle);
				product_tv.setTextColor(R.color.main_text_color);
				transaction = this.getFragmentManager().beginTransaction();
				transaction.replace(R.id.container, product);
				transaction.addToBackStack(null);
				transaction.commit();
				product_tv.setTextColor(Color.rgb(255, 255, 255));
				assets_tv.setTextColor(Color.rgb(136, 136, 136));
				is_product = true;
			}
			break;
		case R.id.main_assets_tv:// 右边资产按钮
			if (is_product) {

				if (Check.is_login(MainActivity.instance)) 
				{

					// product_tv.setBackgroundResource(0);
					product_tv.setBackgroundResource(R.drawable.rectangle_left);
					product_tv.setTextColor(R.color.main_text_color);
					assets_tv.setBackgroundResource(R.drawable.rectangle_solid_right);
					product_tv.setTextColor(R.color.white);
					transaction = this.getFragmentManager().beginTransaction();
					transaction.replace(R.id.container, assets);
					transaction.addToBackStack(null);
					transaction.commit();
					is_product = false;
					assets_tv.setTextColor(Color.rgb(255, 255, 255));
					product_tv.setTextColor(Color.rgb(136, 136, 136));
				} else {
					Intent intent_login = new Intent(MainActivity.instance,
							LoginActivity.class);
					startActivity(intent_login);

				}
			}
			break;
		default:

			break;
		}
	}

}
