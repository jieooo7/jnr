/*
 * TradeRecordAdapter.java
 * @author Andrew Lee
 * 2014-10-27 上午9:10:06
 */
package com.jinr.core.trade.record;

import java.util.List;

import model.Record;

import com.jinr.core.R;
import com.jinr.core.utils.TextAdjustUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * TradeRecordAdapter.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-27 上午9:10:06
 */
public class TradeRecordAdapter extends BaseAdapter{
	
	private List<Record> list;
	private Context mContext;
	
	
	public TradeRecordAdapter(Context context,List list){
		
		this.mContext=context;
		this.list=list;
	}


	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.trade_record_item, null);
			viewHolder = new ViewHolder();
			viewHolder.turn_tv = (TextView) convertView
					.findViewById(R.id.trade_item_tv1);
			viewHolder.time_tv = (TextView) convertView
					.findViewById(R.id.trade_item_tv2);
			viewHolder.money_tv = (TextView) convertView
					.findViewById(R.id.trade_item_tv3);
			viewHolder.status_tv = (TextView) convertView
					.findViewById(R.id.trade_item_tv4);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if("1".equals(list.get(position).getAction_type())){
			
			viewHolder.turn_tv.setText(mContext.getResources().getString(R.string.turn_in));
		}else{
			viewHolder.turn_tv.setText(mContext.getResources().getString(R.string.turn_out));
		}
		viewHolder.time_tv.setText(list.get(position).getU_time());
		viewHolder.money_tv.setText(TextAdjustUtil.getInstance().formatNum(list.get(position).getMoney()));
		switch (Integer.valueOf(list.get(position).getStatus())) {
		case 1:
			viewHolder.status_tv.setText("等待付款");
			break;
		case 2:
			viewHolder.status_tv.setText("付款成功  ");
			break;
		case 3:
			viewHolder.status_tv.setText("购买成功");
			break;
		case 4:
			viewHolder.status_tv.setText("审核中");
			break;
		case 5:
			viewHolder.status_tv.setText("提现成功");
			break;
		case 7:
			viewHolder.status_tv.setText("取消订单");
			break;
		default:
			break;
		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView turn_tv;
		TextView time_tv;
		TextView money_tv;
		TextView status_tv;
		
	}

}
