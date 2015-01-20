/*
 * GetCash.java
 * @author Andrew Lee
 * 2014-10-27 下午7:46:25
 */
package model;

import java.io.Serializable;

/**
 * GetCash.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-27 下午7:46:25
 */
public class GetCash implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1247940766843714746L;
	
	private String acc_id;
	private String u_id;
	private String money;
	private String buy_money;
	private String freeze_money;
	private String queue_money;
	private String available_money;
	private String yield_money;
	private String yield_money_new;
	private String u_time;
	private double sunmoney;
	private String bank_id;
	private String bank_no;

	
	public String getAcc_id() {
		return acc_id;
	}
	public void setAcc_id(String acc_id) {
		this.acc_id = acc_id;
	}
	public String getU_id() {
		return u_id;
	}
	public void setU_id(String u_id) {
		this.u_id = u_id;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getBuy_money() {
		return buy_money;
	}
	public void setBuy_money(String buy_money) {
		this.buy_money = buy_money;
	}
	public String getFreeze_money() {
		return freeze_money;
	}
	public void setFreeze_money(String freeze_money) {
		this.freeze_money = freeze_money;
	}
	public String getQueue_money() {
		return queue_money;
	}
	public void setQueue_money(String queue_money) {
		this.queue_money = queue_money;
	}
	public String getAvailable_money() {
		return available_money;
	}
	public void setAvailable_money(String available_money) {
		this.available_money = available_money;
	}
	public String getYield_money() {
		return yield_money;
	}
	public void setYield_money(String yield_money) {
		this.yield_money = yield_money;
	}
	public String getYield_money_new() {
		return yield_money_new;
	}
	public void setYield_money_new(String yield_money_new) {
		this.yield_money_new = yield_money_new;
	}
	public String getU_time() {
		return u_time;
	}
	public void setU_time(String u_time) {
		this.u_time = u_time;
	}
	public double getSunmoney() {
		return sunmoney;
	}
	public void setSunmoney(int sunmoney) {
		this.sunmoney = sunmoney;
	}
	public String getBank_id() {
		return bank_id;
	}
	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}
	public String getBank_no() {
		return bank_no;
	}
	public void setBank_no(String bank_no) {
		this.bank_no = bank_no;
	}
	
	

}
