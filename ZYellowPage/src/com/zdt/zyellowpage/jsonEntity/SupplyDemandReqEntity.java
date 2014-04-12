package com.zdt.zyellowpage.jsonEntity;

/**
 * 相册列表请求参数
 * 
 * @author Kevin
 * 
 */
public class SupplyDemandReqEntity {

	private int page_number = 0; // 0～n 页码
	private int max_size = 10; // 0～n 每页最大条目数
	private String member_id;// 会员编号

	/**
	 * 0为供应，1为求购，-1为所有,default=-1
	 */
	private String type = "-1";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
}
