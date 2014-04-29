package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;

/**
 * 更多联系人
 * 
 * @author Kevin
 * 
 */
public class Contact {

	/**
	 * 自增
	 */
	@Id
	@Column(name = "_id")
	private Integer _id;

	/**
	 * 记录编号
	 */
	@Column(name = "item_id")
	private String item_id;

	/**
	 * 联系人
	 */
	@Column(name = "contacter")
	private String contacter;

	/**
	 * 电话
	 */
	@Column(name = "telephone")
	private String telephone;

	/**
	 * 部门
	 */
	@Column(name = "department")
	private String department;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

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
