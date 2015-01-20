package com.jinr.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils
{
	
//	ctrl+shift+x   转为大写     ctrl+shift+y   转为小写
	private static SharedPreferences preferences;

	/**
	 * cn.icnt.dinners.utils.Keys
	 * @author Andrew Lee <br/>
	 * create at 2014年7月17日 下午7:39:32
	 */
	public static class Keys
	{
		/**
		 * 版本号
		 */
		public static final String VERSION = "version";
		public static final String TEST = "test";
	
		public static final String UID = "uid";
		public static final String TEL = "tel";
		public static final String EMAIL = "email";
		public static final String NICKNAME = "nickName";
		public static final String PASSWORD = "password";
		public static final String PWD_KEY = "pwd_key";
		public static final String BUSS_PWD = "buss_pwd";
		public static final String NAME = "name";
		public static final String IS_LOCK = "is_lock";
		
		
		public static final String ID_CARD = "id_card";//身份证号
		
		
		
		public static final String IS_IDENTIFY = "is_identify";
		public static final String IS_LOGIN = "is_login";
		public static final String HAS_CARD = "has_card";
		public static final String HAS_DEAL_PASSWD = "has_deal_passwd";
		public static final String IS_GES_LOCKED = "is_ges_locked";
		
		
		
		public static final String BANK_CARD_ID = "bank_card_id";//卡id
		public static final String BANK_CARD_NO = "bank_card_no";//卡号
		public static final String BANK_CARD_NUM = "bank_card_num";//卡编号
		public static final String BANK_NAME = "bank_name";//银行名字
		public static final String OPEN_BANK = "open_bank";//开户行
		public static final String BANK_STATUS = "bank_status";//卡状态
		public static final String BANK_CITY = "bank_city";//所在城市
		
		
		
		
		/**
		 * 用户名
		 */
		/**
		 * sharedPreference 文件名
		 */
		public  static final String USERINFO="userInfo";
		
	
	}

/**
 * 存储布尔值
 * @param mContext
 * @param key
 * @param value
 */
	public static void putBooleanToSPMap(Context mContext, String key, boolean value)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	public static void putIntToSPMap(Context mContext, String key, int value)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	
	public static int getIntFromSPMap(Context mContext, String key)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO,  Context.MODE_PRIVATE);
		int value = preferences.getInt(key, 0);
		return value;
	}

/**
 * 获取布尔值
 * @param mContext
 * @param key
 * @return
 */
	public static Boolean getBooleanFromSPMap(Context mContext, String key)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO,  Context.MODE_PRIVATE);
		boolean value = preferences.getBoolean(key, false);
		return value;
	}

/**
 * 存储String
 * @param mContext
 * @param key  
 * @param value  
 */
	public static void putValueToSPMap(Context mContext, String key, String value)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * 获取String
	 * @param mContext
	 * @param key  
	 * @return value
	 */
	public static String getValueFromSPMap(Context mContext, String key)
	{
		if (null != mContext)
		{
			preferences = mContext.getSharedPreferences(Keys.USERINFO,  Context.MODE_PRIVATE);
			String value = preferences.getString(key, "");
			return value;
		} else
		{
			return null;
		}
	}

/**
 * 获取String
 * @param mContext
 * @param key
 * @param defaults 无值时取defaults
 * @return
 */
	public static String getValueFromSPMap(Context mContext, String key,String defaults )
	{
		if (null != mContext)
		{
			preferences = mContext.getSharedPreferences(Keys.USERINFO,  Context.MODE_PRIVATE);
			String value = preferences.getString(key, defaults);
			return value;
		} else
		{
			return null;
		}
	}
	/**
	 * 清除全部
	 * @param mContext
	 */
	public static void clearSPMap(Context mContext)
	{
		preferences = mContext.getSharedPreferences(Keys.USERINFO, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.clear();
		edit.commit();
	}

	

	/**
	 * 指定key清除
	 * @param mContext
	 * @param key
	 */
	public static void clearSpMap(Context mContext, String key)
	{
		putValueToSPMap(mContext, key, "");
	}
	/**
	 * 用户注销 清除指定key
	 * @param mContext
	 */
	public static void logout_del(Context mContext)
	{
	    putValueToSPMap(mContext, Keys.UID, "-1");
//	    putValueToSPMap(mContext, Keys.ACCOUNT, null);
//	    putValueToSPMap(mContext, Keys.USERINFO,null);
//	    putValueToSPMap(mContext, Keys.NICKNAME, null);
//	    putValueToSPMap(mContext, Keys.ORDERNO_NO, null);
//	    putValueToSPMap(mContext, Keys.COLLECT_NO,null);
//	    putValueToSPMap(mContext, Keys.COUPON_NO, null);
//	    putValueToSPMap(mContext, Keys.CREDITS_NO, null);
//	    putValueToSPMap(mContext, Keys.ACCOUNT_NO,null);
//	    putValueToSPMap(mContext, Keys.TOKEN, null);
//	    putValueToSPMap(mContext, Keys.USER_PORTRAIT,null);
	}
}
