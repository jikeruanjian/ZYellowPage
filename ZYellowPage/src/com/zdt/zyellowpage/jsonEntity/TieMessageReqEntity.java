package com.zdt.zyellowpage.jsonEntity;

/**
 * 电子请帖签到请求
 * 
 * @author Kevin
 * 
 */
public class TieMessageReqEntity {

	/**
	 * 请帖ID
	 */
	private String item_id;

	/**
	 * 姓名
	 */
	private String fullname;

	/**
	 * 手机号
	 */
	private String telephone;

	/**
	 * 嘉宾（除了喜帖有这项必填，其它类型的请帖均无此项1表示新郎嘉宾，2表示新娘嘉宾）
	 */
	private String friend;

	/**
	 * 参加（1表示参加，2表示不参加）
	 */
	private String attend;

	/**
	 * 参加人数
	 */
	private String amount;

	/**
	 * 祝福
	 */
	private String message;

	/**
	 * 验证码（获取到请帖的password为空，则不用输入验证码，如果不为空，则要求用户填写验证码）
	 */
	private String code;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFriend() {
		return friend;
	}

	public void setFriend(String friend) {
		this.friend = friend;
	}

	public String getAttend() {
		return attend;
	}

	public void setAttend(String attend) {
		this.attend = attend;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
