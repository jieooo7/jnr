package model;

import java.io.Serializable;

public class GiftListContent implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5033253780016237158L;
	private int type;
	private String id;
	private String chenhu;
	private String source;
	private String money;
	private String c_time;
	private String e_time;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getChenhu() {
		return chenhu;
	}
	public void setChenhu(String chenhu) {
		this.chenhu = chenhu;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getC_time() {
		return c_time;
	}
	public void setC_time(String c_time) {
		this.c_time = c_time;
	}
	public String getE_time() {
		return e_time;
	}
	public void setE_time(String e_time) {
		this.e_time = e_time;
	}
	
	
	

}
