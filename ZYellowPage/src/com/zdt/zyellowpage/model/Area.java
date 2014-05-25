package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "area")
public class Area implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1053525376387246471L;

	/**
	 * 地区编号
	 */
	@Id
	@Column(name = "Id")
	private String id;

	// 地区名称
	@Column(name = "Name", length = 28)
	private String name;

	/**
	 * 父地区的Id
	 */
	@Column(name = "Parent", length = 20)
	private String parent;

	public Area(String i, String n, String pi) {
		id = i;
		name = n;
		parent = pi;
	}

	public Area() {
		// TODO Auto-generated constructor stub
	}

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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return name;
	}
}
