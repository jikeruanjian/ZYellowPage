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
import com.zdt.zyellowpage.dao.AlbumDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Album;

/**
 * 相册业务逻辑,只有商家才有相册
 * 
 * @author Kevin
 * 
 */
public class AlbumBll {

	ZzObjectHttpResponseListener<Album> objectResponseListener;
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
	public void getAlbumList(Context context, AlbumReqEntity albumParams,
			ZzObjectHttpResponseListener<Album> respListener) {
		this.mAlbumParams = albumParams;
		this.mContext = context;
		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "query-album");
			jo.put("data", new Gson().toJson(albumParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		getBasicAlbumList(context, params, respListener);
	}

	// 2、添加/删除相片
	public void updateAlbum(Context context, String token, Album album,
			ZzStringHttpResponseListener respListener) {

		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "update-album");
			jo.put("token", token);
			jo.put("data", new Gson().toJson(album));
		} catch (Exception e) {
			e.printStackTrace();
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		basicExcute(context, params, respListener);
	}

	private void getBasicAlbumList(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<Album> respListener) {
		this.objectResponseListener = respListener;
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
								bre.setStatus_description(jo
										.getString("status_description"));

								if (bre.getSuccess()) {
									JSONObject data = jo.getJSONObject("data");
									JSONArray dataAlbum;
									dataAlbum = data.getJSONArray("album");

									List<Album> tempAlbum = new Gson().fromJson(
											dataAlbum.toString(),
											new TypeToken<List<Album>>() {
											}.getType());

									AlbumDao albumDao = new AlbumDao(mContext);
									albumDao.startWritableDatabase(false);

									albumDao.delete("member_id=?",
											new String[] { mAlbumParams
													.getMember_id() });

									if (tempAlbum != null) {
										for (Object object : tempAlbum) {
											Album album = (Album) object;
											album.setMember_id(mAlbumParams
													.getMember_id());
										}
										albumDao.insertList(tempAlbum);
									}
									albumDao.closeDatabase(false);
									objectResponseListener.onSuccess(
											statusCode, tempAlbum);
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
						AlbumDao albumDao = new AlbumDao(mContext);
						albumDao.startReadableDatabase(false);
						List<Album> lis = albumDao
								.rawQuery(
										"select * from album order by _id desc limit ? offset ?",
										new String[] {
												String.valueOf(mAlbumParams
														.getMax_size()),
												String.valueOf(mAlbumParams
														.getMax_size()
														* mAlbumParams
																.getPage_number()) },
										Album.class);
						albumDao.closeDatabase(false);
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
							Log.i("AlbumBll", content);
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
