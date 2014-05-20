package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "category")
public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 418987853355818158L;

	@Id
	@Column(name = "Id")
	private String id;

	/**
	 * 分类名称
	 */
	@Column(name = "Name")
	private String name;

	/**
	 * 商家0/个人1
	 */
	@Column(name = "Type")
	private String type;

	/**
	 * 父分类
	 */
	@Column(name = "Parent")
	private String parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getParent() {
		return parent;
	}

	public void setParentId(String parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return name;
	}
}
