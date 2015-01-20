/*
 * BankCardInfo.java
 * @author Andrew Lee
 * 2014-10-22 上午11:48:13
 */
package model;

import java.io.Serializable;

/**
 * BankCardInfo.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-22 上午11:48:13
 */
public class BankCardInfo implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8070008576453732289L;
	private String id;
	private String uid;
	private String bank_no;
	private String bank_num;
	private String bank_name;
	private String open_bank;
	private String zone_id;
	private String bank_status;
	private String zone_sheng;
	private String zone_shi;
	private String u_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getBank_no() {
		return bank_no;
	}
	public void setBank_no(String bank_no) {
		this.bank_no = bank_no;
	}
	public String getBank_num() {
		return bank_num;
	}
	public void setBank_num(String bank_num) {
		this.bank_num = bank_num;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getOpen_bank() {
		return open_bank;
	}
	public void setOpen_bank(String open_bank) {
		this.open_bank = open_bank;
	}
	public String getZone_id() {
		return zone_id;
	}
	public void setZone_id(String zone_id) {
		this.zone_id = zone_id;
	}
	public String getBank_status() {
		return bank_status;
	}
	public void setBank_status(String bank_status) {
		this.bank_status = bank_status;
	}
	public String getZone_sheng() {
		return zone_sheng;
	}
	public void setZone_sheng(String zone_sheng) {
		this.zone_sheng = zone_sheng;
	}
	public String getZone_shi() {
		return zone_shi;
	}
	public void setZone_shi(String zone_shi) {
		this.zone_shi = zone_shi;
	}
	public String getU_time() {
		return u_time;
	}
	public void setU_time(String u_time) {
		this.u_time = u_time;
	}
	
	

}
