package model;

import java.io.Serializable;


public class RedEnvelopesDetailed implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6102648893788354088L;
	private String title;
	private String s_time;
	private String e_time;
	private String content;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getS_time() {
		return s_time;
	}
	public void setS_time(String s_time) {
		this.s_time = s_time;
	}
	public String getE_time() {
		return e_time;
	}
	public void setE_time(String e_time) {
		this.e_time = e_time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
