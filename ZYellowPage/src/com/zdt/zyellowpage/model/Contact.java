package com.zdt.zyellowpage.model;

import java.io.Serializable;


/**
 * 更多联系人
 * 
 * @author Kevin
 * 
 */
public class Contact implements Serializable 
{

	/**
	 * 记录编号
	 */
	private String item_id;

	/**
	 * 联系人
	 */
	private String contacter;

	/**
	 * 电话
	 */
	private String telephone;

	/**
	 * 部门
	 */
	private String department;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

}
