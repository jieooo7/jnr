/*
 * UrlConfig.java
 * @author Andrew Lee
 * 2014-10-20 上午10:56:19
 */
package com.jinr.core.config;

/**
 * UrlConfig.java description:
 * 
 * @author Andrew Lee version 2014-10-20 上午10:56:19
 */
public class UrlConfig {

	public static String BASE_IP = "http://www.jinr.com/api/";
//	public static String BASE_IP = "http://121.40.98.228/api/";
//	public static String BASE_IP = "http://test.jingyubank.com/api/";
//	public static String BASE_IP = "http://www.jinr.com/api/";
	public static String APP_VALUE = "a760bf0440aed727a2d6d11e7c9fbb2b";
	public static String APP_KEY = "appkey";
	public static String ADD_USER = "user/add_user";// 注册地址
	public static String GET_USER = "user/get_user";// 登陆地址
	public static String FORGET_PASSWD = "user/forget_password";// 忘记密码
	public static String BANK_CARD_INFO = "yonghu/bank_num";// 获取银行卡信息
	public static String BIND_CARD = "yonghu/bank_banding";// 绑定银行卡
	public static String REMOVE_BIND_CARD = "yonghu/bank_relieve";// 绑定银行卡
	public static String MODIFY_MOBILE = "yonghu/modify_mobile";// 修改手机号
	public static String MODIFY_ID_CARD = "yonghu/add_card_id";// 修改身份信息
	public static String MODIFY_PASSWORD = "yonghu/modify_password";// 修改密码
	public static String MODIFY_DEAL_PASSWORD = "yonghu/set_pwd";// 修改交易密码
	public static String GET_RECORD = "order/get_record";// 交易记录
	public static String SEND_SMS = "message/sendsms";//发送短信
	public static String ADD_ORDER = "order/usinessNotIFy";//请求交易流水号
	public static String GET_ZUHEID = "index/getList";//获取组合id
	public static String GET_MONEY = "order/get_txmoney";//可提现接口
	public static String GET_MONEY_FINAL = "order/add_txorder";//提现接口
	public static String GET_MONEY_NOW = "yonghu/money"; //账户金额
	public static String LIMIT_MONEY = "zuhe/limit_money"; //账户金额
	public static String TIPS_LIMIT = "order/txlimit"; //账户金额
	
	
	public static String PLATFORM = "andmarket"; //渠道
	public static String SMS_IDENTIFY = "561"; //短信新版本区分
	public static String LIMIT_MESS = "zuhe/XianeTishi"; //短信新版本区分
	public static String GIFT_RULE = "Hongbao/hbxize"; //红包规则
	public static String GIFT_LIST = "Hongbao/red_envelopes_getlist"; //红包列表
	public static String GET_RED_DETAILED="/Hongbao/hongbaoxiangqing";
	
	public static String GET_SHARE_URL = "/Hongbao/share_xinxi";
	
}
