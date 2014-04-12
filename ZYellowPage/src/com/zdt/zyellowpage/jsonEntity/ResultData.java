package com.zdt.zyellowpage.jsonEntity;

import com.google.gson.Gson;

/**
 * 统一返回数据接收对象
 * @author Administrator
 *
 */
public class ResultData {
	/**
	 * 
	 */
	private String result;
	/**
	 * 返回的数据可能是列表，也可能是单个值
	 */
	private String data;
	public ResultData(String r,String d){
		result = r;
		data = d;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	static public  ResultData getResultData(String strJson) {
		Gson g = new Gson();
		return  g.fromJson(strJson, ResultData.class);
	}
	
}
