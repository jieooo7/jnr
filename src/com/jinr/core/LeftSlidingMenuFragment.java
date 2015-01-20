/**
 * 
 */
package com.jinr.core;


import com.jinr.core.bankCard.FragmentCardList;
import com.jinr.core.config.Check;
import com.jinr.core.more.AboutUsActivity;
import com.jinr.core.more.FragmentMore;
import com.jinr.core.regist.LoginActivity;
import com.jinr.core.regist.RegisterActivity;
import com.jinr.core.security.FragmentSecurityCenter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class LeftSlidingMenuFragment extends Fragment implements OnClickListener{

	private View toolBtnIndex;
	private View toolBtnIdcard;
	private View toolBtnSec;
	private View toolBtnMore;
	private View toolBtnGift;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    }
    
    
    
    public void showindex(){
    	Log.i("================show index","xxxxxxxxxxxxxx");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO 自动生成的方法存根
    	
    	View view = inflater.inflate(R.layout.left_menu, container,false);
   	 
	   	toolBtnIndex = view.findViewById(R.id.jinrBtnIndex);
	   	toolBtnIndex.setOnClickListener(this);
	   	
	   	toolBtnIdcard = view.findViewById(R.id.jinrBtnIdcrad);
	   	toolBtnIdcard.setOnClickListener(this);
	   	
	   	toolBtnSec = view.findViewById(R.id.jinrBtnSec);
	   	toolBtnSec.setOnClickListener(this);
	   	
	   	toolBtnMore = view.findViewById(R.id.jinrBtnMore);
	   	toolBtnMore.setOnClickListener(this);
	   	
	   	
	   	toolBtnGift = view.findViewById(R.id.jinrBtnGift);
	   	toolBtnGift.setOnClickListener(this);
    	
	   	return view;
    }
    
	private void setTxtColor(View v, Boolean isSelect){
		TextView txt = (TextView)v.findViewById(R.id.toolbox_title);
		ImageView imag = (ImageView)v.findViewById(R.id.notification_indicator);
		if(isSelect){
			txt.setTextColor(Color.parseColor("#62bce3"));
		}else{
			txt.setTextColor(Color.parseColor("#999999"));
		}
		
		switch (v.getId()) {
		case R.id.jinrBtnIndex:
			if(isSelect){
				imag.setImageResource(R.drawable.tool_box_index_icon_select);
			}else{
				imag.setImageResource(R.drawable.tool_box_index_icon);
			}
			break;
			
		case R.id.jinrBtnIdcrad:
			if(isSelect){
				imag.setImageResource(R.drawable.tool_box_idcard_icon_select);
			}else{
				imag.setImageResource(R.drawable.tool_box_idcard_icon);
			}
			break;
			
		case R.id.jinrBtnSec:
			if(isSelect){
				imag.setImageResource(R.drawable.tool_box_sec_icon_select);
			}else{
				imag.setImageResource(R.drawable.tool_box_sec_icon);
			}
			break;
			
		case R.id.jinrBtnMore:
			if(isSelect){
				imag.setImageResource(R.drawable.tool_box_more_icon_select);
			}else{
				imag.setImageResource(R.drawable.tool_box_more_icon);
			}
			break;
		case R.id.jinrBtnGift:
			if(isSelect){
				imag.setImageResource(R.drawable.tool_box_gift_icon_select);
			}else{
				imag.setImageResource(R.drawable.tool_box_gift_icon);
			}
			break;
		}
	}
	
	public void setTextColor()
	{
		setTxtColor(toolBtnIndex,toolBtnIndex.isSelected());
		setTxtColor(toolBtnIdcard,toolBtnIdcard.isSelected());
		setTxtColor(toolBtnSec,toolBtnSec.isSelected());
		setTxtColor(toolBtnMore,toolBtnMore.isSelected());
		setTxtColor(toolBtnGift,toolBtnGift.isSelected());
	}
	
	public void setSelectFalse()
	{
		toolBtnIndex.setSelected(false);
		toolBtnIdcard.setSelected(false);
		toolBtnSec.setSelected(false);
		toolBtnMore.setSelected(false);
		toolBtnGift.setSelected(false);
	}
    
	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		TextView txt = null;
		switch (v.getId()) {
			case R.id.jinrBtnIndex:
				setSelectFalse();
				toolBtnIndex.setSelected(true);
				setTextColor();
				
				MainActivity.instance.changeRightView(0);
				break;
				
			case R.id.jinrBtnIdcrad:
				setSelectFalse();
				toolBtnIdcard.setSelected(true);
				setTextColor();
				if(!Check.is_login(getActivity()))
				{
					Intent intent_login=new Intent(getActivity(),LoginActivity.class);
					startActivity(intent_login);
					return;
				}
				MainActivity.instance.changeRightView(1);
				break;
				
			case R.id.jinrBtnSec:
				setSelectFalse();
				toolBtnSec.setSelected(true);
				setTextColor();
				if(!Check.is_login(getActivity()))
				{
					Intent intent_login=new Intent(getActivity(),LoginActivity.class);
					startActivity(intent_login);
					return;
				}
				MainActivity.instance.changeRightView(2);
				
				break;
				
			case R.id.jinrBtnMore:
				setSelectFalse();
				toolBtnMore.setSelected(true);
				setTextColor();
				MainActivity.instance.changeRightView(3);
				break;
			case R.id.jinrBtnGift:
				setSelectFalse();
				toolBtnGift.setSelected(true);
				setTextColor();
				
				if(!Check.is_login(getActivity()))
				{
					Intent intent_login=new Intent(getActivity(),LoginActivity.class);
					startActivity(intent_login);
					return;
				}
				MainActivity.instance.changeRightView(4);
				break;
		}
	}
}
