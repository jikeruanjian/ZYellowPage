package com.zdt.zyellowpage.model;

public class NearEnterprise {
	/**
	 * "member_id": "abc234", "fullname": "商家名称", // 单位名称 "address": "昆明市",
	 * "latitude": 25.0426998138428, //数据类型：double "longitude":
	 * 102.706703186035, //数据类型：double “logo": "image123.jpg"//图片地址
	 */
	private String member_id;
	private String fullname;
	private String address;
	private double latitude;
	private double longitude;
	private String logo;

	public NearEnterprise(String id, String name, String ad, double lon,
			double lat, String lo) {
		member_id = id;
		fullname = name;
		address = ad;
		latitude = lat;
		longitude = lon;
		logo = lo;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
