package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "user")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4072847311848975287L;

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private Integer _id;

	/**
	 * 会员编号
	 */
	@Column(name = "member_id")
	private String member_id;

	// 登录用户名 length=20数据字段的长度是20
	@Column(name = "username", length = 28)
	private String username;

	/**
	 * 用户密码,长度为6到20
	 */
	@Column(name = "password", length = 20)
	private String password;

	// 昵称
	@Column(name = "telephone")
	private String telephone;

	// 用户性别
	@Column(name = "sex")
	private Integer sex;
	// 邮箱
	@Column(name = "email")
	private String email;

	// 全名
	@Column(name = "fullname")
	private String fullname;

	// 假设您开始时没有此属性,程序开发中才想到此属性,也不用卸载程序.
	@Column(name = "address")
	private String address;

	// 城市
	@Column(name = "city")
	private String city;

	/**
	 * 0为商家，1为个人
	 */
	@Column(name = "type")
	private Integer type;

	// 用户问题
	@Column(name = "password_question")
	private String password_question;

	// 用户答案
	@Column(name = "password_answer")
	private String password_answer;

	// 注册的区域ID
	@Column(name = "area_id")
	private String area_id;

	// 有些字段您可能不希望保存到数据库中,不用@Column注释就不会映射到数据库.
	private String remark;

	// 登录授权
	@Column(name = "token")
	private String token;

	// 是否为当前登录
	@Column(name = "is_login_user")
	private Boolean isLoginUser;

	/**
	 * 网址
	 */
	@Column(name = "website")
	private String website;

	/**
	 * 关键字
	 */
	@Column(name = "keyword")
	private String keyword;

	/**
	 * 简介
	 */
	@Column(name = "summary")
	private String summary;

	/**
	 * 所属分类
	 */
	@Column(name = "category_id")
	private String category_id;

	/**
	 * qq
	 */
	@Column(name = "qq")
	private String qq;

	/**
	 * 优惠信息
	 */
	@Column(name = "discount")
	private String discount;

	/**
	 * 经营范围
	 */
	@Column(name = "scope")
	private String scope;

	/**
	 * 经度
	 */
	@Column(name = "latitude")
	private Double latitude;

	/**
	 * 纬度
	 */
	@Column(name = "longitude")
	private Double longitude;

	/**
	 * 网络名称
	 */
	@Column(name = "wifi_username")
	private String wifi_username;

	/**
	 * 网络密码
	 */
	@Column(name = "wifi_password")
	private String wifi_password;

	/**
	 * 年龄
	 */
	@Column(name = "age")
	private String age;

	/**
	 * 专业
	 */
	@Column(name = "professional")
	private String professional;

	/**
	 * 学校
	 */
	@Column(name = "school")
	private String school;

	/**
	 * 特长
	 */
	@Column(name = "specialty")
	private String specialty;

	/**
	 * 案例经历
	 */
	@Column(name = "experience")
	private String experience;

	/**
	 * 民族
	 */
	@Column(name = "nation")
	private String nation;

	/**
	 * logo
	 */
	@Column(name = "logo")
	private String logo;

	/**
	 * 二维码地址,调用时，需要在地址后边加一个参数area,表示当前所选择的城市编号
	 */
	@Column(name = "qr_code")
	private String qr_code;

	/**
	 * 数据类型
	 */
	@Column(name = "dataType")
	private String dataType;

	/**
	 * 数据类型
	 */
	@Column(name = "category_name")
	private String category_name;

	/**
	 * 联系人
	 */
	@Column(name = "contact")
	private String contact;

	/**
	 * 区域名称
	 */
	@Column(name = "area_name")
	private String area_name;

	/**
	 * 性别
	 */
	@Column(name = "sex_name")
	private String sex_name;

	/**
	 * ”从事计算机方面的工作”//求职意向
	 */
	@Column(name = "job")
	private String job;

	/**
	 * 多图片地址，使用“,”号分割,图片是绝对地址
	 */
	@Column(name = "album")
	private String album;
	
	/**
	 * 点击量
	 */
	@Column(name = "click")
	private int click;

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getSex_name() {
		return sex_name;
	}

	public void setSex_name(String sex_name) {
		this.sex_name = sex_name;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getType() {
		return type;
	}

	public String getPassword_question() {
		return password_question;
	}

	public void setPassword_question(String password_question) {
		this.password_question = password_question;
	}

	public String getPassword_answer() {
		return password_answer;
	}

	public void setPassword_answer(String password_answer) {
		this.password_answer = password_answer;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean isLoginUser() {
		return isLoginUser;
	}

	public void setLoginUser(Boolean isLoginUser) {
		this.isLoginUser = isLoginUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getWifi_username() {
		return wifi_username;
	}

	public void setWifi_username(String wifi_username) {
		this.wifi_username = wifi_username;
	}

	public String getWifi_password() {
		return wifi_password;
	}

	public void setWifi_password(String wifi_password) {
		this.wifi_password = wifi_password;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}
}
