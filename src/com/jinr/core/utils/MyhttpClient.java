/*
 * MyhttpClient.java
 * @author Andrew Lee
 * 2014-10-20 上午11:30:12
 */
package com.jinr.core.utils;

import com.jinr.core.config.AppManager;
import com.jinr.core.config.UrlConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * MyhttpClient.java 
 * description:
 * 
 * @author Andrew Lee version 2014-10-20 上午11:30:12
 */
public class MyhttpClient {

	private static final String BASE_URL = UrlConfig.BASE_IP;

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		
//		String BASE_URL="";
//		switch(PreferencesUtils.getIntFromSPMap(AppManager.getAppManager().currentActivity(), PreferencesUtils.Keys.TEST)){
//		case 0:
//			BASE_URL="http://121.40.98.228/api/";
//			break;
//		case 1:
//			BASE_URL="http://test.jingyubank.com/api/";
//
//			break;
//		case 2:
//			BASE_URL="http://www.jinr.com/api/";
//
//			break;
//		
//		
//		
//		}
		MyLog.i("访问地址", BASE_URL + relativeUrl);
		return BASE_URL + relativeUrl;
	}

}
