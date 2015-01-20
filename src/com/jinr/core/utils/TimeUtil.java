package com.jinr.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.jinr.pulltorefresh.PullToRefreshGridView;
import com.jinr.pulltorefresh.PullToRefreshListView;
import com.jinr.pulltorefresh.PullToRefreshScrollView;


/**
 * @名称：TimeUtil.java
 * @描述：时间转化类
 * @author danding 2014-8-11
 */
public class TimeUtil {
	private static SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	/**
	 * 设置帖子发表时间
	 * @param oldTime
	 * @param currentDate
	 * @return
	 */
	public static String getTimeStr(Date oldTime, Date currentDate) {
		long time1 = currentDate.getTime();

		long time2 = oldTime.getTime();

		long time = (time1 - time2) / 1000;

		if (time >= 0 && time < 60) {
			return "刚刚";
		} else if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		} else if (time >= 3600 && time < 3600 * 24) {
			return time / 3600 + "小时前";
		} else if(time > 3600 * 24){
			return  time / (3600*24) + "天前";
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(oldTime);
		}
	}
	/**
	 * 将字符串转化为时间
	 * @param str
	 * @return
	 */
	public static Date strToDate(String str) {
		// sample：Tue May 31 17:46:55 +0800 2011
		// E：周 MMM：字符串形式的月，如果只有两个M，表示数值形式的月 Z表示时区（＋0800）
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date result = null;
		try {
			result = sdf.parse(str);
		} catch (Exception e) {
		}
		return result;

	}
	/**
	 * @描述：将毫秒值转为时间
	 * @param ld
	 * @return
	 * 2014-9-19
	 */
	public static String second2Time(long ld){
		Date date = new Date(ld);  
		//标准日历系统类  
		GregorianCalendar gc = new GregorianCalendar();  
		gc.setTime(date);  
		//java.text.SimpleDateFormat，设置时间格式  
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");  
		//得到毫秒值转化的时间  
		String time = format.format(gc.getTime());
		return time;
	}
	/**
	 * 
	 * @描述：设置刷新时间
	 * @date：2014-6-26
	 */
	public static void setLastUpdateTime(PullToRefreshListView pulllistview) {
		String text = formatDateTime(System.currentTimeMillis());
		pulllistview.setLastUpdatedLabel(text);
	}
	/**
	 * 
	 * @描述：设置刷新时间
	 * @date：2014-6-26
	 */
	public static void setLastUpdateTime2(PullToRefreshGridView pull) {
		String text = formatDateTime(System.currentTimeMillis());
		pull.setLastUpdatedLabel(text);
	}
	/**
	 * @描述：设置刷新时间
	 * @param pull
	 * 2014-9-17
	 */
	public static void setLastUpdateTime3(PullToRefreshScrollView pull) {
		String text = formatDateTime(System.currentTimeMillis());
		pull.setLastUpdatedLabel(text);
	}
	/**
	 * 
	 * @描述：时间格式化
	 * @date：2014-6-26
	 * @param time
	 * @return
	 */
	public static String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}
		return mDateFormat.format(new Date(time));
	}
}
