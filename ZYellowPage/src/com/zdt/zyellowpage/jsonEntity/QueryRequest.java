package com.zdt.zyellowpage.jsonEntity;

import com.google.gson.Gson;

/**
 * 查询请求
 * @author Administrator
 *
 */
public class QueryRequest {
	private String method;
	private String data;

	public QueryRequest(String m,String d){
		method = m;
		data = d;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	/**
	 * 获取请求data值
	 * @param methodName
	 * @param o
	 * @return
	 */
	static public  String getQueryRequestJson(String methodName,Object o) {
		Gson g = new Gson();
		return  g.toJson(new QueryRequest(methodName,g.toJson(o)));
	}
	
	
}
