package com.zdt.zyellowpage.bll;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Version;

/**
 * 版本更新
 * 
 * @author Kevin
 * 
 */
public class VersionBll {

	ZzObjectHttpResponseListener<Version> objectResponseListener;

	public void getVersion(final Context context,
			ZzObjectHttpResponseListener<Version> respListener) {
		this.objectResponseListener = respListener;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "update-version");
			joData.put("system_type", "android");
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.AREAURL, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.i("VersionBll", content);
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
						JSONObject data = jo.getJSONObject("data");

						Version tempVersion = new Gson().fromJson(
								data.toString(), Version.class);
						List<Version> lis = new ArrayList<Version>();
						lis.add(tempVersion);
						objectResponseListener.onSuccess(statusCode, lis);
					}
				} catch (JSONException e) {
					objectResponseListener.onErrorData("数据请求错误");
				}

			}

			@Override
			public void onStart() {
				objectResponseListener.onStart();
			}

			@Override
			public void onFinish() {
				objectResponseListener.onFinish();
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				objectResponseListener.onFailure(statusCode, content, error,
						null);
			}
		});
	}
}
