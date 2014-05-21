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
 * 获取热门关键词
 * 
 * @author Kevin
 * 
 */
public class NewsContentBll {

	/**
	 * 获取 关于我们服务条款"// 1为关于我们，2为商家服务条款3为个人服务条,不缓存
	 * 
	 * @param context
	 */
	public void getNewsContent(final Context context, String item_id,
			final ZzStringHttpResponseListener respListener) {
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-news-content");
			joData.put("item_id", item_id);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("NewsContentBll", content);
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
									JSONObject dataNewsContent = jo
											.getJSONObject("data");

									respListener.onSuccess(statusCode,
											dataNewsContent
													.getString("description"));
								} else {
									respListener.onErrorData(bre
											.getStatus_description());
								}

							} catch (JSONException e) {
								e.printStackTrace();
								return;
							}
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						respListener.onFailure(statusCode, content, error);
					}

					@Override
					public void onStart() {
						respListener.onStart();
					}

					@Override
					public void onFinish() {
						respListener.onFinish();
					}
				});

	}
}
