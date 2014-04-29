package com.zdt.zyellowpage.bll;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Contact;

/**
 * 更多联系人，相关业务逻辑
 * 
 * @author Kevin
 * 
 */
public class ContactBll {

	ZzObjectHttpResponseListener<Contact> objectResponseListener;
	ZzStringHttpResponseListener stringResponseListener;
	AlbumReqEntity mAlbumParams;
	Context mContext;

	/**
	 * 获取,如果从网络中获取到了数据，就删除本地的，并保存，如果没有获取到，则直接返回背地数据
	 * 
	 * @param context
	 * @param albumParams
	 * @param respListener
	 */
	public void getContactList(Context context, String member_id,
			ZzObjectHttpResponseListener<Contact> respListener) {
		this.mContext = context;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-contact");
			joData.put("member_id", member_id);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		getBasicContactList(context, params, respListener);
	}

	// 2、添加/删除相片
	public void updateAlbum(Context context, String token, String item_id,
			String url, ZzStringHttpResponseListener respListener) {

		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "update-aublm");
			jo.put("token", token);
			joData.put("item_id", item_id);
			joData.put("url", url);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		basicExcute(context, params, respListener);
	}

	private void getBasicContactList(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<Contact> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("ContactBll", content);
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
									JSONArray dataContact;
									dataContact = jo.getJSONArray("data");

									List<Contact> tempContact = new Gson().fromJson(
											dataContact.toString(),
											new TypeToken<List<Contact>>() {
											}.getType());

									objectResponseListener.onSuccess(
											statusCode, tempContact);
								} else {
									objectResponseListener.onErrorData(bre
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
						objectResponseListener.onStart();
					}

					// 失败，调用
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						objectResponseListener.onFailure(statusCode, content,
								error, null);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						objectResponseListener.onFinish();
					};
				});
	}

	private void basicExcute(Context context, AbRequestParams params,
			ZzStringHttpResponseListener respListener) {

		this.stringResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("AlbumBll", content);
							JSONObject jo = null;
							BaseResponseEntity bre = new BaseResponseEntity();
							// 转换数据
							try {
								jo = new JSONObject(content);
								bre.setResult(jo.getString("result"));
								bre.setSuccess(jo.getBoolean("success"));
								bre.setStatus(jo.getInt("status"));
								// bre.setStatus_description(jo
								// .getString("status_description"));

								if (bre.getSuccess()) {
									stringResponseListener.onSuccess(
											statusCode,
											bre.getStatus_description());
								} else {
									stringResponseListener.onErrorData(bre
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
						stringResponseListener.onStart();
					}

					// 失败，调用
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						stringResponseListener.onFailure(statusCode, content,
								error);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						stringResponseListener.onFinish();
					};
				});
	}
}
