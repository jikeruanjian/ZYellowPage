package com.zdt.zyellowpage.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Table;

/**
 * 热词，用于百度百科的识别
 * 
 * @author Kevin
 * 
 */
@Table(name = "HotWord")
public class HotWord {
	/**
	 * 关键词
	 */
	@Column(name = "Hotword", length = 15)
	private String hotword;

	/**
	 * 类型 为1表示商家名称或个人姓名热词，5表示地址热词，7表示民族热词，2表示学校热词，3表示专业热词，6表示证书热词
	 */
	@Column(name = "Type")
	private Integer type;

	public String getHotword() {
		return hotword;
	}

	public void setHotword(String hotword) {
		this.hotword = hotword;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
