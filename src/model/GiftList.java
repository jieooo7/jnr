package model;

import java.io.Serializable;
import java.util.ArrayList;




public class GiftList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6035100048716995462L;
	private int user_type;
	private int countpage;
	private String url;
	private ArrayList<GiftListContent> detal;
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getUser_type() {
		return user_type;
	}
	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}
	
	public int getCountpage() {
		return countpage;
	}
	public void setCountpage(int countpage) {
		this.countpage = countpage;
	}
	public ArrayList<GiftListContent> getDetal() {
		return detal;
	}
	public void setDetal(ArrayList<GiftListContent> detal) {
		this.detal = detal;
	}
	
	
	

}
