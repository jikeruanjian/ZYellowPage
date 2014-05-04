package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 供求信息
 * 
 * @author Kevin
 * 
 */
@Table(name = "supplyDemand")
public class SupplyDemand {

	@Id
	@Column(name = "_id")
	private Integer _id;

	/**
	 * 会员编号
	 */
	@Column(name = "member_id")
	private String member_id;

	/**
	 * 需求编号
	 */
	@Column(name = "item_id")
	private String item_id;

	/**
	 * 这是供应信息的标题"
	 */
	@Column(name = "title")
	private String title;

	/**
	 * 类别
	 */
	@Column(name = "type")
	private String type;

	/**
	 * 内容
	 */
	@Column(name = "content")
	private String content;

	/**
	 * 发布时间
	 */
	@Column(name = "time")
	private String time;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
