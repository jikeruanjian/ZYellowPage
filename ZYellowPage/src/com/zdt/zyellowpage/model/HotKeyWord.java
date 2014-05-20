package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 热门关键词
 * 
 * @author Kevin
 * 
 */
@Table(name = "HotKeyWord")
public class HotKeyWord {
	/**
	 * 地区编号
	 */
	@Id
	@Column(name = "Id")
	private int id;

	/**
	 * 关键词
	 */
	@Column(name = "Name", length = 8)
	private String name;

	/**
	 * 类型 0商家，1个人
	 */
	@Column(name = "Type")
	private String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
