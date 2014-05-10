package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 电子请帖
 * 
 * @author Kevin 　　//婚庆：remark表示新郎简介，other表示新娘简介，content表示恋爱史
 *         　　//乔迁：content表示新居简介，remark和other无用。
 *         　　//聚会：content表示回忆录，remark和other无用。
 *         　　//开业：remark表示商企简介，other无用，content表示发展方向
 *         　　//庆典：remark表示发展历程，other无用，content表示经营成果
 * 
 */
@Table(name = "tie")
public class Tie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 418987853355818158L;

	@Id
	@Column(name = "_id")
	private int _id;

	/**
	 * 编号
	 */
	@Column(name = "item_id")
	private String item_id;

	/**
	 * 区域编号
	 */
	@Column(name = "area_id")
	private String area_id;

	/**
	 * 区域编号
	 */
	@Column(name = "logo")
	private String logo;

	/**
	 * 　　多张图片，多图片地址，使用“,”号分割
	 */
	@Column(name = "album")
	private String album;

	/**
	 * 　　1为婚庆2乔迁3聚会4开业5庆典
	 */
	@Column(name = "type")
	private String type;

	/**
	 * 标题
	 */
	@Column(name = "title")
	private String title;

	/**
	 * 详细信息
	 */
	@Column(name = "content ")
	private String content;

	/**
	 * 　内容
	 */
	@Column(name = "remark")
	private String remark;

	/**
	 * 　　内容
	 */
	@Column(name = "other ")
	private String other;

	/**
	 * 　　多张图片，多图片地址，使用“,”号分割
	 */
	@Column(name = "telephone")
	private String telephone;

	/**
	 * 　　1为婚庆2乔迁3聚会4开业5庆典
	 */
	@Column(name = "address")
	private String address;

	/**
	 * 经度
	 */
	@Column(name = "latitude")
	private String latitude;

	/**
	 * 纬度
	 */
	@Column(name = "longitude")
	private String longitude;

	/**
	 * 　内容
	 */
	@Column(name = "more")
	private String more;

	/**
	 * 　　内容
	 */
	@Column(name = "password")
	private String password;

	/**
	 * 　发布时间
	 */
	@Column(name = "time")
	private String time;

	/**
	 * 　发布时间
	 */
	@Column(name = "qr_code")
	private String qr_code;

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
