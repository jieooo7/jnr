/*
 * BaseListModel.java
 * @author Andrew Lee
 * 2014-10-22 上午11:44:17
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * BaseListModel.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-22 上午11:44:17
 */
public class BaseListModel<T> implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3343545611391559197L;
	
	private int code;
	private String msg;
	private ArrayList<T> data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public ArrayList<T> getData() {
		return data;
	}
	public void setData(ArrayList<T> data) {
		this.data = data;
	}
	public boolean isSuccess() {
		if (this.getCode() == 1000) {
			
			return true;

		} else {

			return false;

		}

	}

}
