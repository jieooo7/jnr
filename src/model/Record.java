/*
 * Record.java
 * @author Andrew Lee
 * 2014-10-27 上午9:42:48
 */
package model;

import java.io.Serializable;

/**
 * Record.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-27 上午9:42:48
 */
public class Record implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1513335788630033618L;
	
	
	private String id;
	private String c_time;
	private String action_type;
	private String money;
	private String poundage;
	private String status;
	private String opmoney;
	private String zuhe_id;
	private String u_time;
	private String num_id;
	private String code_id;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getC_time() {
		return c_time;
	}
	public void setC_time(String c_time) {
		this.c_time = c_time;
	}
	public String getAction_type() {
		return action_type;
	}
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	public String getMoney() {
		return String.format("%.2f", Double.valueOf(money));//保留两位小数处理
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getPoundage() {
		return poundage;
	}
	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOpmoney() {
		return opmoney;
	}
	public void setOpmoney(String opmoney) {
		this.opmoney = opmoney;
	}
	public String getZuhe_id() {
		return zuhe_id;
	}
	public void setZuhe_id(String zuhe_id) {
		this.zuhe_id = zuhe_id;
	}
	public String getU_time() {
		return u_time;
	}
	public void setU_time(String u_time) {
		this.u_time = u_time;
	}
	public String getNum_id() {
		return num_id;
	}
	public void setNum_id(String num_id) {
		this.num_id = num_id;
	}
	public String getCode_id() {
		return code_id;
	}
	public void setCode_id(String code_id) {
		this.code_id = code_id;
	}
	
	
	
	

}
