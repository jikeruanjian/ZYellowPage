package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Table;

/**
 * 资质证书，该类数据不错保存
 * 
 * @author Kevin
 * 
 */
@Table(name = "certificate")
public class Certificate implements Serializable {

	/**
	 * 项目编号，服务端的主键
	 */
	private String item_id;

	/**
	 * 证书编号
	 */
	private String certificate_no;

	/**
	 * 证书名称
	 */
	private String certificate_name;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getCertificate_no() {
		return certificate_no;
	}

	public void setCertificate_no(String certificate_no) {
		this.certificate_no = certificate_no;
	}

	public String getCertificate_name() {
		return certificate_name;
	}

	public void setCertificate_name(String certificate_name) {
		this.certificate_name = certificate_name;
	}

}
