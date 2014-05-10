package com.zdt.zyellowpage.bll;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

/**
 * 1、获取区域的名称
 * 
 * @author Kevin
 * 
 */
public class LoginBll {
	Context mContext;

	/**
	 * 
	 * @param context
	 * @param msgno手机号码
	 *            （如果以后开放邮箱注册，此处改为邮箱地址即可）
	 * @param type
	 *            验证码类型（0表示注册，1表示密码找回）
	 * @param respListener
	 */
	public void getCode(Context context, String msgno, String type,
			final ZzStringHttpResponseListener respListener) {
		this.mContext = context;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "get-code");
			joData.put("msgno", msgno);
			joData.put("type", type);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("LoginBll", content);
							JSONObject jo = null;
							BaseResponseEntity bre = new BaseResponseEntity();
							// 转换数据
							try {
								jo = new JSONObject(content);
								bre.setResult(jo.getString("result"));
								bre.setSuccess(jo.getBoolean("success"));
								bre.setStatus(jo.getInt("status"));
								bre.setStatus_description(jo
										.getString("status_description"));

								if (bre.getSuccess()) {
									respListener.onSuccess(statusCode, content);
								} else {
									respListener.onErrorData(bre
											.getStatus_description());
								}

							} catch (JSONException e) {
								e.printStackTrace();
								return;
							}
						}
					};

					// 开始执行前
					@Override
					public void onStart() {
						respListener.onStart();
					}

					// 失败，调用
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						respListener.onFailure(statusCode, content, error);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						respListener.onFinish();
					};
				});
	}

}
