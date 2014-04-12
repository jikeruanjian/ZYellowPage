package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 相片,该类数据已经缓存
 * @author Kevin
 *
 */
@Table(name = "album")
public class Album {

	@Id
	@Column(name = "_id")
	private int _id;

	/**
	 * 会员编号
	 */
	@Column(name = "member_id")
	private String member_id;

	/**
	 * 相片编号
	 */
	@Column(name = "item_id")
	private String item_id;

	/**
	 * 链接地址
	 */
	@Column(name = "url")
	private String url;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
