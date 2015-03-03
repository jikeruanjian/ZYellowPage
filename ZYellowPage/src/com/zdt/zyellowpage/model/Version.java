package com.zdt.zyellowpage.model;

public class Version {

	/**
	 * android
	 */
	private String system_type;

	/**
	 * 用于客户端与本地的build_number比较，如果本地build_number较小，则提示更新APP。
	 */
	private int build_number;

	/**
	 * version仅用于显示
	 */
	private String version;

	/**
	 * 本次升级描述信息….
	 */
	private String version_description;

	/**
	 * app下载地址
	 */
	private String app_url;

	public String getSystem_type() {
		return system_type;
	}

	public void setSystem_type(String system_type) {
		this.system_type = system_type;
	}

	public int getBuild_number() {
		return build_number;
	}

	public void setBuild_number(int build_number) {
		this.build_number = build_number;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion_description() {
		return version_description;
	}

	public void setVersion_description(String version_description) {
		this.version_description = version_description;
	}

	public String getApp_url() {
		return app_url;
	}

	public void setApp_url(String app_url) {
		this.app_url = app_url;
	}
}
