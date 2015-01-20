package com.jinr.core.utils;



import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class CommonUtil {

//	private static final CommonLog log = LogFactory.createLog();
	private static String PACKAGE_NAME = "com.jinr.core";
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		} 
		return true;
	}
	
	public static String getRootFilePath() {
//		if (hasSDCard()) {
//			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/date/"+PACKAGE_NAME;// filePath:/sdcard/
//		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"+PACKAGE_NAME; // filePath: /data/data/
//		}
	}
	
	
	public static boolean isPwdRegular(String pwd){
		// 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
		Pattern p = Pattern.compile("^(?![0-9]+$).{6,16}$");  
//		Pattern p = Pattern.compile("^(?=.*[^\\d]).{6,16}$");  
        Matcher m = p.matcher(pwd);   
        boolean b= m.matches();
        return b;
	}
	
	
	public static boolean checkNetState(Context context){
    	boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
    }
	
	public static void showToask(Context context, String tip){
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}
	
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
}
