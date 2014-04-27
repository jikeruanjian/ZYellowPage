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

}
