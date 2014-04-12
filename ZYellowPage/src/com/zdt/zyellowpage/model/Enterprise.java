package com.zdt.zyellowpage.model;

/**
 * 商家类
 * 
 * @author Administrator
 * 
 */
public class Enterprise {
	private String enterprise_id;
	private String enterprise_name;
	private String summary;
	private String image_id;

	public Enterprise(String id, String name, String s, String img) {
		enterprise_id = id;
		enterprise_name = name;
		summary = s;
		image_id = img;
	}

	public String getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public String getEnterprise_name() {
		return enterprise_name;
	}

	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}

	public String getImage_id() {
		return image_id;
	}

	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
