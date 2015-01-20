/*
 * TextUtil.java
 * @author Andrew Lee
 * 2014-10-24 下午2:38:24
 */
package com.jinr.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


/**
 * TextUtil.java
 * 
 * @description:
 * @author Andrew Lee
 * @version 2014-10-24 下午2:38:24
 */
public class TextAdjustUtil {

	private static TextAdjustUtil instance = null;

	private TextAdjustUtil() {
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new TextAdjustUtil();
		}
	}

	public static TextAdjustUtil getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	
	
	public String formatNum(String str){
		if((Double.parseDouble(str)-0.005)<0){
			
			return "0.00";
		}else{
			return String.format("%.2f", (Double.parseDouble(str)-0.0049999));
		}
		
		
	}
	public String mobileAdjust(String bind_mobile) {
		if ("".equals(bind_mobile) != true) {
			Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
			Matcher m = p.matcher(bind_mobile);
			bind_mobile = m.replaceAll("$1$2****");
			MyLog.i("正则", bind_mobile);
			return bind_mobile;
		} else {
			return "";
		}

	}
	
	
	public boolean onlyChinese(String input){
		
		 
			Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
			Matcher m = p.matcher(input);
		
			return m.matches(); 
	}

	public String bankCardAdjust(String bank_no) {
		if ("".equals(bank_no) == false&&bank_no.length()>4) {

			bank_no = "**** **** **** "
					+ bank_no.substring(bank_no.length() - 4);
			return bank_no;
		} else {
			return "";
		}

	}
	public String idCardAdjust(String id_card_no) {
		if ("".equals(id_card_no) == false&&id_card_no.length()>7) {
			
			id_card_no = id_card_no.substring(0,12)+"**** ";
			return id_card_no;
		} else {
			return "";
		}
		
	}

	public String nameAdjust(String name) {

		if ("".equals(name) == false) {
			if (name.length() == 2 || name.length() == 1) {
				name = name.substring(0, 1) + "*";
			} else {
				int length = name.length();
				name = name.substring(0, 1) + "*"
						+ name.substring(length - 1, length);
			}
			return name;

		} else {

			return "";
		}

	}
	
	
	 /**
	 * @param editText  设置小数点后保留2位
	 */
	public void setPricePoint(final EditText editText) {
	        editText.addTextChangedListener(new TextWatcher() {
	 
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before,
	                    int count) {
	                if (s.toString().contains(".")) {
	                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
	                        s = s.toString().subSequence(0,
	                                s.toString().indexOf(".") + 3);
	                        editText.setText(s);
	                        editText.setSelection(s.length());
	                    }
	                }
	                if (s.toString().trim().substring(0).equals(".")) {
	                    s = "0" + s;
	                    editText.setText(s);
	                    editText.setSelection(2);
	                }
	 
	                if (s.toString().startsWith("0")
	                        && s.toString().trim().length() > 1) {
	                    if (!s.toString().substring(1, 2).equals(".")) {
	                        editText.setText(s.subSequence(0, 1));
	                        editText.setSelection(1);
	                        return;
	                    }
	                }
	            }
	 
	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count,
	                    int after) {
	 
	            }
	 
	            @Override
	            public void afterTextChanged(Editable s) {
	                // TODO Auto-generated method stub
	                 
	            }
	 
	        });
	 
	    }

}