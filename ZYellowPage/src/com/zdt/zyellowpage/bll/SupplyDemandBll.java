package com.zdt.zyellowpage.bll;

import java.util.ArrayList;
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
import com.zdt.zyellowpage.dao.SupplyDemandDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.jsonEntity.SupplyDemandReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;

/**
 * 供求关系
 * 
 * @author Kevin
 * 
 */
public class SupplyDemandBll {

	ZzObjectHttpResponseListener<SupplyDemand> objectResponseListener;
	ZzStringHttpResponseListener stringResponseListener;
	SupplyDemandReqEntity mSupplyDemandParams;
	Context mContext;

	// 1、获取
	// 如果从网络中获取到了数据，就删除本地的，并保存，如果没有获取到，则直接返回本地数据
	public void getSupplyDemandList(Context context,
			SupplyDemandReqEntity supplyDemandParams,
			ZzObjectHttpResponseListener<SupplyDemand> respListener) {
		this.mSupplyDemandParams = supplyDemandParams;
		this.mContext = context;
		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "query-supply-demand");
			jo.put("data", new Gson().toJson(supplyDemandParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		getBasicSupplyDemandList(context, params, respListener);
	}

	/**
	 * 获取供求信息的详情
	 * 
	 * @param context
	 * @param item_id
	 * @param respListener
	 */
	public void getDetailOfSupplyDemand(Context context, final String item_id,
			ZzObjectHttpResponseListener<SupplyDemand> respListener) {
		this.mContext = context;
		objectResponseListener = respListener;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-supply-demand-content");
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
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("SupplyDemandBll", content);
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

									SupplyDemand tempSupplyDemand = new Gson()
											.fromJson(data.toString(),
													SupplyDemand.class);

									SupplyDemandDao supplyDemandDao = new SupplyDemandDao(
											mContext);
									supplyDemandDao
											.startWritableDatabase(false);

									supplyDemandDao.delete("item_id=?",
											new String[] { item_id });

									supplyDemandDao.insert(tempSupplyDemand);
									supplyDemandDao.closeDatabase(false);
									List<SupplyDemand> lis = new ArrayList<SupplyDemand>();
									lis.add(tempSupplyDemand);

									objectResponseListener.onSuccess(
											statusCode, lis);
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
						System.out.println("数据请求异常" + content);
						SupplyDemandDao supplyDemandDao = new SupplyDemandDao(
								mContext);
						List<SupplyDemand> supplyDemand = supplyDemandDao
								.queryList("item_id=?",
										new String[] { item_id });
						objectResponseListener.onFailure(statusCode, content,
								error, supplyDemand);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						objectResponseListener.onFinish();
					};
				});
	}

	/**
	 * 更新供求信息
	 * 
	 * @param context
	 * @param token
	 * @param item_id
	 * @param url
	 * @param respListener
	 */
	public void updateSupplyDemand(Context context, String token,
			SupplyDemand entity, ZzStringHttpResponseListener respListener) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "update-supply-demand");
			jo.put("data", new Gson().toJson(entity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		basicExcute(context, params, respListener);
	}

	private void getBasicSupplyDemandList(Context context,
			AbRequestParams params,
			ZzObjectHttpResponseListener<SupplyDemand> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("SupplyDemandBll", content);
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
									JSONArray dataSupplyDemand;
									dataSupplyDemand = data
											.getJSONArray("list");

									List<SupplyDemand> tempSupplyDemand = new Gson().fromJson(
											dataSupplyDemand.toString(),
											new TypeToken<List<SupplyDemand>>() {
											}.getType());

									SupplyDemandDao supplyDemandDao = new SupplyDemandDao(
											mContext);
									supplyDemandDao
											.startWritableDatabase(false);

									supplyDemandDao.delete(
											"member_id=? and type=?",
											new String[] {
													mSupplyDemandParams
															.getMember_id(),
													mSupplyDemandParams
															.getType() });

									if (tempSupplyDemand != null) {
										for (Object object : tempSupplyDemand) {
											SupplyDemand supplyDemand = (SupplyDemand) object;
											supplyDemand
													.setMember_id(mSupplyDemandParams
															.getMember_id());
										}
										supplyDemandDao
												.insertList(tempSupplyDemand);
									}
									supplyDemandDao.closeDatabase(false);
									objectResponseListener.onSuccess(
											statusCode, tempSupplyDemand);
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
						System.out.println("数据请求异常" + content);
						SupplyDemandDao supplyDemandDao = new SupplyDemandDao(
								mContext);
						supplyDemandDao.startReadableDatabase(false);
						List<SupplyDemand> lis = supplyDemandDao
								.rawQuery(
										"select * from supplyDemand order by _id desc limit ? offset ?*?",
										new String[] {
												String.valueOf(mSupplyDemandParams
														.getMax_size()),
												String.valueOf(mSupplyDemandParams
														.getPage_number()) },
										SupplyDemand.class);
						supplyDemandDao.closeDatabase(false);
						objectResponseListener.onFailure(statusCode, content,
								error, lis);
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
							Log.i("SupplyDemandBll", content);
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
