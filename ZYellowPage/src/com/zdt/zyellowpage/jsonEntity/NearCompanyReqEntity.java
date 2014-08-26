package com.zdt.zyellowpage.jsonEntity;

import com.ab.util.AbStrUtil;

/**
 * 附近商家请求参数类
 * 
 * @author Kevin
 * 
 */
public class NearCompanyReqEntity {

	private int page_number = 0;

	private int max_size = 10;

	private Double latitude;

	private Double longitude;

	private int distance = 1000;

	private String keyWord;

	public NearCompanyReqEntity(int max, Double lat, Double lon, int dis) {
		max_size = max;
		latitude = lat;
		longitude = lon;
		distance = dis;
	}

	/**
	 * 按照分类来获取周围商家
	 * @param max
	 * @param lat
	 * @param lon
	 * @param dis
	 * @param typeID 商家分类编号
	 */
	public NearCompanyReqEntity(int max, Double lat, Double lon, int dis,
			String typeID) {
		max_size = max;
		latitude = lat;
		longitude = lon;
		distance = dis;
		if (!AbStrUtil.isEmpty(typeID)) {
			this.keyWord = "list-" + typeID;
		}
	}

	public int getPage_number() {
		return page_number;
	}

	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}

	public int getMax_size() {
		return max_size;
	}

	public void setMax_size(int max_size) {
		this.max_size = max_size;
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

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
}
