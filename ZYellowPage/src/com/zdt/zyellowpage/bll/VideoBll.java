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
import com.zdt.zyellowpage.dao.VideoDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Video;

/**
 * 相册业务逻辑,只有商家才有视频，数据已缓存
 * 
 * @author Kevin
 * 
 */
public class VideoBll {

	ZzObjectHttpResponseListener<Video> objectResponseListener;
	ZzStringHttpResponseListener stringResponseListener;
	AlbumReqEntity mVideoParams;
	Context mContext;

	// 1、获取
	// 如果从网络中获取到了数据，就删除本地的，并保存，如果没有获取到，则直接返回本地数据
	public void getVideoList(Context context, AlbumReqEntity videoParams,
			ZzObjectHttpResponseListener<Video> respListener) {
		this.mVideoParams = videoParams;
		this.mContext = context;
		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "query-video");
			jo.put("data", new Gson().toJson(videoParams).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AbRequestParams params = new AbRequestParams();
		params.put("id", jo.toString());

		getBasicVideoList(context, params, respListener);
	}

	private void getBasicVideoList(Context context, AbRequestParams params,
			ZzObjectHttpResponseListener<Video> respListener) {
		this.objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.post(Constant.BASEURL, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						if (content != null && !content.equals("")) {
							Log.i("VideoBll", content);
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
									dataAlbum = data.getJSONArray("video");

									List<Video> tempAlbum = new Gson().fromJson(
											dataAlbum.toString(),
											new TypeToken<List<Video>>() {
											}.getType());

									VideoDao videoDao = new VideoDao(mContext);
									videoDao.startWritableDatabase(false);

									videoDao.delete("member_id=?",
											new String[] { mVideoParams
													.getMember_id() });

									if (tempAlbum != null) {
										for (Object object : tempAlbum) {
											Video album = (Video) object;
											album.setMember_id(mVideoParams
													.getMember_id());
										}
										videoDao.insertList(tempAlbum);
									}
									videoDao.closeDatabase(false);
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
						VideoDao videoDao = new VideoDao(mContext);
						videoDao.startReadableDatabase(false);
						List<Video> localList = videoDao
								.rawQuery(
										"select * from video order by _id desc limit ? offset ?*?",
										new String[] {
												String.valueOf(mVideoParams
														.getMax_size()),
												String.valueOf(mVideoParams
														.getPage_number()) },
										Video.class);
						videoDao.closeDatabase(false);
						objectResponseListener.onFailure(statusCode, content,
								error, localList);
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						objectResponseListener.onFinish();
					};
				});
	}
}
