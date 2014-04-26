package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "area")
public class Area {
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
	@Column(name = "parentId", length = 20)
	private String parentId;

	public Area(String i,String n,String pi){
		id = i;
		name = n;
		parentId = pi;
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return name;
	}
}
