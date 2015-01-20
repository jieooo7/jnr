package model;

import java.io.Serializable;

/**
 * BankCardInfo.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-22 上午11:48:13
 */
public class AssetsInfo implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6611695161945974751L;
	/**
	 * 
	 */

	private String total;
	private String total_money;
	private String hobao_y_money;//红包收益
	private String hobao_pre_yield_money;//昨日红包收益
	private String money;       //账户总金额
	private String freeze_money;//冻结金额
	private String yield_money;//收益总金额(累计总收益)
	private String pre_yield_money;//昨日收益金额

	public String getTotal_money() {
		return total_money;
	}
	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}
	public String getHobao_y_money() {
		return hobao_y_money;
	}
	public void setHobao_y_money(String hobao_y_money) {
		this.hobao_y_money = hobao_y_money;
	}
	public String getHobao_pre_yield_money() {
		return hobao_pre_yield_money;
	}
	public void setHobao_pre_yield_money(String hobao_pre_yield_money) {
		this.hobao_pre_yield_money = hobao_pre_yield_money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getMoney() {
		return money;
	}
	public void setMoneyy(String money) {
		this.money = money;
	}
	public String getYield_money() {
		return yield_money;
	}

	public void setYield_money(String yield_money) {
		this.yield_money = yield_money;
	}
	public String getPre_yield_money() {
		return pre_yield_money;
	}
	public void setPre_yield_money(String pre_yield_money) {
		this.pre_yield_money = pre_yield_money;
	}
	
	public String getFreeze_money() {
		return freeze_money;
	}
	public void setFreeze_money(String freeze_money) {
		this.freeze_money = freeze_money;
	}
}
