package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;

public class Video {

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
	@Column(name = "title")
	private String title;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
