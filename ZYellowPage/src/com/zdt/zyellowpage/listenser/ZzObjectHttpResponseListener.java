package com.zdt.zyellowpage.listenser;

import java.util.List;

public interface ZzObjectHttpResponseListener<T> {

	/**
	 * 描述：获取数据成功会调用这里.
	 */
	public void onSuccess(int statusCode, List<T> lis);

	/**
	 * 开始执行前
	 */
	public void onStart();

	/**
	 * 失败，调用：当调用
	 * 
	 * @param statusCode
	 * @param content
	 * @param error
	 */
	public void onFailure(int statusCode, String content, Throwable error,
			List<T> localList);

	/**
	 * 数据获取异常
	 * 
	 * @param status_description异常原因
	 */
	public void onErrorData(String status_description);

	/**
	 * 完成后调用，失败，成功
	 */
	public void onFinish();
}
