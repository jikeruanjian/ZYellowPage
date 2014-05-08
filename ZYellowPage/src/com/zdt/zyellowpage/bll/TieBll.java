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
import com.zdt.zyellowpage.dao.TieDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.jsonEntity.TieMessageReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Tie;

/**
 * 电子请帖，相关业务，缓存
 * 
 * @author Kevin
 * 
 */
public class TieBll {

	ZzObjectHttpResponseListener<Tie> objectResponseListener;
	ZzStringHttpResponseListener stringResponseListener;
	Context mContext;
	int page_number;
	int max_size;

	/**
	 * 获取,如果从网络中获取到了数据，缓存
	 * 
	 * @param context
	 * @param page_number
	 * @param max_size
	 * @param area_id
	 * @param type
	 *            1为婚庆2乔迁3聚会4开业5庆典
	 * @param respListener
	 */
	public void getTieList(Context context, int page_number, int max_size,
			String area_id, String type,
			ZzObjectHttpResponseListener<Tie> respListener) {
		this.page_number = page_number;
		this.max_size = max_size;
		this.mContext = context;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-tie");
			joData.put("page_number", page_number);
			joData.put("max_size", max_size);
			joData.put("area_id", area_id);
			joData.put("type", type);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		getBasicTieList(context, params, respListener);
	}

	/**
	 * 获取请帖信息的详情
	 * 
	 * @param context
	 * @param item_id
	 * @param respListener
	 */
	public void getDetailOfTie(Context context, final String item_id,
			ZzObjectHttpResponseListener<Tie> respListener) {
		this.mContext = context;
		objectResponseListener = respListener;
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-tie-content");
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
							Log.i("TieBll", content);
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

									Tie tempTie = new Gson().fromJson(
											data.toString(), Tie.class);

									TieDao tieDao = new TieDao(mContext);
									tieDao.startWritableDatabase(false);

									tieDao.delete("item_id=?",
											new String[] { item_id });

									tieDao.insert(tempTie);
									tieDao.closeDatabase(false);
									List<Tie> lis = new ArrayList<Tie>();
									lis.add(tempTie);

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
						TieDao tieDao = new TieDao(mContext);
						List<Tie> tie = tieDao.queryList("item_id=?",
								new String[] { item_id });
						objectResponseListener.onFailure(statusCode, content,
								error, tie);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						objectResponseListener.onFinish();
					};
				});
	}

	/**
	 * 电子请帖签到请求
	 */
	public void TieSign(Context context, String token,
			TieMessageReqEntity data, ZzStringHttpResponseListener respListener) {
		this.mContext = context;
		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "tie-message");
			jo.put("token", token);
			jo.put("data", new Gson().toJson(data).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		basicExcute(context, params, respListener);
	}

	private void getBasicTieList(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<Tie> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("TieBll", content);
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
									JSONArray dataTie;
									dataTie = data.getJSONArray("list");

									List<Tie> tempTie = new Gson().fromJson(
											dataTie.toString(),
											new TypeToken<List<Tie>>() {
											}.getType());

									TieDao tieDao = new TieDao(mContext);
									tieDao.startWritableDatabase(false);

									tieDao.deleteAll(); // 只缓存一页就够了

									if (tempTie != null) {
										tieDao.insertList(tempTie);
									}
									tieDao.closeDatabase(false);
									objectResponseListener.onSuccess(
											statusCode, tempTie);
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
						TieDao tieDao = new TieDao(mContext);
						List<Tie> lis = tieDao
								.rawQuery(
										"select * from tie order by _id desc limit ? offset ?*?",
										new String[] {
												String.valueOf(max_size),
												String.valueOf(page_number) },
										Tie.class);
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
							Log.i("TieBll", content);
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
