package com.jinr.core.gift;

import java.util.ArrayList;
import java.util.List;

import com.jinr.core.R;

import model.GiftListContent;
import model.Record;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GiftFragmentAdapter extends BaseAdapter{
	
	
	private List<GiftListContent> mList;
	private Context mContext;
	
	
	public GiftFragmentAdapter(Context context,List list){
		
		this.mContext=context;
		this.mList=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.gift_item, null);
			viewHolder = new ViewHolder();
			
			viewHolder.leftIv = (ImageView) convertView
					.findViewById(R.id.gift_item_iv);
			viewHolder.type_tv = (TextView) convertView
					.findViewById(R.id.gift_item_type_tv);
			viewHolder.time_tv = (TextView) convertView
					.findViewById(R.id.gift_item_time_tv);
			viewHolder.money_tv = (TextView) convertView
					.findViewById(R.id.gift_item_money_tv);
			viewHolder.detail_tv = (TextView) convertView
					.findViewById(R.id.gift_item_detail_tv);
			convertView.setTag(viewHolder);//setTag的作用才是把查找的view通过ViewHolder封装好缓存起来方便多次重用，当需要时可以getTag拿出来 
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.type_tv.setText(mList.get(position).getChenhu());
		viewHolder.time_tv.setText("有效日期至 "+mList.get(position).getE_time());
		viewHolder.money_tv.setText(mList.get(position).getMoney()+"元");
		viewHolder.detail_tv.setText(mList.get(position).getSource());
		
		
		
		return convertView;
	}
	
	protected static class ViewHolder {
		ImageView leftIv;
		TextView money_tv;
		TextView time_tv;
		TextView type_tv;
		TextView detail_tv;
		
	}

}
