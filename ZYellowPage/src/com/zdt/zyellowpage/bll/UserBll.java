package com.zdt.zyellowpage.bll;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.jsonEntity.NearCompanyReqEntity;
import com.zdt.zyellowpage.jsonEntity.PersonListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.User;

/**
 * 1、获取附近商家
 * 
 * @author Kevin
 * 
 */
public class UserBll {

	private ZzObjectHttpResponseListener<User> objectResponseListener;
	private ZzStringHttpResponseListener stringResponseListener;
	Context mContext;
	String mMember_id;

	/**
	 * 获取附近的商家列表
	 * 
	 * @param context
	 * @param companyParams
	 * @param respListener
	 */
	public void getNearCompany(Context context,
			NearCompanyReqEntity companyParams,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		try {
			jo.put("method", "query-enterprise-nearby");
			jo.put("data", new Gson().toJson(companyParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		getBasicUserList(context, params, respListener);
	}

	/**
	 * 获取商家列表
	 * 
	 * @param context
	 * @param companyParams
	 * @param respListener
	 */
	public void getListCompany(Context context,
			CompanyListReqEntity companyParams,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		try {
			jo.put("method", "query-enterprise");
			jo.put("data", new Gson().toJson(companyParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString().replaceAll("////", ""));
		getBasicUserList(context, params, respListener);
	}

	/**
	 * 获取个人列表
	 * 
	 * @param context
	 * @param companyParams
	 * @param respListener
	 */
	public void getListPerson(Context context,
			PersonListReqEntity companyParams,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		try {
			jo.put("method", "query-person");
			jo.put("data", new Gson().toJson(companyParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		getBasicUserList(context, params, respListener);
	}

	/**
	 * 获取商家详情
	 * 
	 * @param context
	 * @param member_id
	 * @param respListener
	 */
	public void getDetailCompany(Context context, String member_id,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		mMember_id = member_id;
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "query-enterprise-content");
			joData.put("member_id", member_id);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		getBasicUser(context, params, respListener);
	}

	/**
	 * 获取个人详情
	 * 
	 * @param context
	 * @param member_id
	 * @param respListener
	 */
	public void getDetailPerson(Context context, String member_id,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		JSONObject joData = new JSONObject();
		mMember_id = member_id;
		try {
			jo.put("method", "query-person-content");
			joData.put("member_id", member_id);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		getBasicUser(context, params, respListener);
	}

	/**
	 * 关注/取消关注
	 * 
	 * @param context
	 * @param token
	 * @param member_id
	 * @param cancel是否为取消关注
	 * @param respListener
	 */
	public void followUser(Context context, String token, String member_id,
			boolean cancel, ZzStringHttpResponseListener respListener) {
		JSONObject jo = new JSONObject();
		mContext = context;
		JSONObject joData = new JSONObject();
		try {
			jo.put("method", "follow");
			jo.put("token", token);
			joData.put("member_id", member_id);
			joData.put("cancel", cancel);
			jo.put("data", joData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());
		basicExcute(context, params, respListener);
	}

	/**
	 * 获取关注列表
	 * 
	 * @param context
	 * @param page_number
	 * @param max_size
	 * @param type
	 *            0为商家，1为个人，2为全部关注 ，3为粉丝商家，4为粉丝个人，5为所有粉丝
	 * @param token
	 * @param respListener
	 */
	public void getFollowList(Context context, int page_number, int max_size,
			final String type, String token,
			ZzObjectHttpResponseListener<User> respListener) {
		JSONObject jo = new JSONObject();
		JSONObject joData = new JSONObject();
		mContext = context;
		try {
			jo.put("method", "query-following");
			jo.put("token", token);
			joData.put("page_number", page_number);
			joData.put("max_size", max_size);
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
							System.out.println("-----------" + content);
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
									// int page_number = data
									// .getInt("page_number");
									JSONArray dataUser;
									dataUser = data.getJSONArray("following");

									List<User> tempUser = new Gson().fromJson(
											dataUser.toString(),
											new TypeToken<List<User>>() {
											}.getType());

									UserInsideDao userDao = new UserInsideDao(
											mContext);
									userDao.startWritableDatabase(false);
									String dataType = Constant.DataType.MYFOLLOWING;
									if (Integer.valueOf(type) > 2) {
										dataType = Constant.DataType.MYFANS;
									}
									String whereClause = "";
									String[] whereArgs = null;
									if (type.equals("2") || type.equals("5")) {
										whereClause = "dataType=?";
										whereArgs = new String[] { dataType };
									} else if (type.equals("0")
											|| type.equals("3")) {
										whereClause = "dataType=? and type=?";
										whereArgs = new String[] { dataType,
												"0" };
									} else if (type.equals("1")
											|| type.equals("4")) {
										whereClause = "dataType=? and type=?";
										whereArgs = new String[] { dataType,
												"1" };
									}

									userDao.delete(whereClause, whereArgs);

									userDao.insertList(tempUser);
									userDao.closeDatabase(false);

									objectResponseListener.onSuccess(
											statusCode, tempUser);
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
						UserInsideDao userDao = new UserInsideDao(mContext);
						userDao.startReadableDatabase(false);
						List<User> lis = userDao.queryList();
						userDao.closeDatabase(false);
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

	private void getBasicUser(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<User> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							System.out.println("-----------" + content);
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

									User tempUser = new Gson().fromJson(
											data.toString(), User.class);
									UserInsideDao userDao = new UserInsideDao(
											mContext);

									userDao.startReadableDatabase(false);
									List<User> localUser = userDao.queryList(
											"member_id=?",
											new String[] { mMember_id });
									userDao.closeDatabase(false);
									if (localUser != null
											&& localUser.size() > 0) {
										tempUser.setDataType(localUser.get(0)
												.getDataType());
									}

									if (localUser != null
											&& localUser.size() > 0
											&& !localUser.get(0).isLoginUser()) {
										userDao.startWritableDatabase(false);
										userDao.delete("member_id=?",
												new String[] { mMember_id });
										userDao.insert(tempUser);
										userDao.closeDatabase(false);
									}

									List<User> lisUser = new ArrayList<User>();
									lisUser.add(tempUser);
									objectResponseListener.onSuccess(
											statusCode, lisUser);
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
						UserInsideDao userDao = new UserInsideDao(mContext);
						userDao.startReadableDatabase(false);
						List<User> lis = userDao.queryList("member_id=?",
								new String[] { mMember_id });
						userDao.closeDatabase(false);
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

	private void getBasicUserList(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<User> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							System.out.println("-----------" + content);
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
									// int page_number = data
									// .getInt("page_number");
									JSONArray dataUser;
									try {
										dataUser = data
												.getJSONArray("enterprise");
									} catch (JSONException ex) {
										try {
											dataUser = data
													.getJSONArray("person");
										} catch (JSONException exe) {
											dataUser = data
													.getJSONArray("following");
										}
									}

									List<User> tempUser = new Gson().fromJson(
											dataUser.toString(),
											new TypeToken<List<User>>() {
											}.getType());

									objectResponseListener.onSuccess(
											statusCode, tempUser);
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
							System.out.println("-----------" + content);
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
