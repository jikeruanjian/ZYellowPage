package com.zdt.zyellowpage.bll;

import java.util.List;

import android.content.Context;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.dao.AreaDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Area;

/**
 * 1、获取区域的名称
 * 
 * @author Kevin
 * 
 */
public class AreaBll {

	ZzObjectHttpResponseListener<Area> objectResponseListener;

	/**
	 * 下载所有区域
	 * 
	 * @param context
	 */
	public void downAllArea(final Context context,
			final ZzStringHttpResponseListener respListener) {
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.ALLAREA, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(final int statusCode, final String content) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						List<Area> mAreas = new Gson().fromJson(
								content.toLowerCase(),
								new TypeToken<List<Area>>() {
								}.getType());

						AreaDao areaDao = new AreaDao(context);
						areaDao.startWritableDatabase(true);
						areaDao.deleteAll();
						areaDao.insertList(mAreas, false);
						areaDao.closeDatabase(true);
						respListener.onSuccess(statusCode, "更新成功");
					}
				}).start();
			}

			@Override
			public void onStart() {
				respListener.onStart();
			}

			@Override
			public void onFinish() {
				respListener.onFinish();
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				respListener.onFailure(statusCode, content, error);
			}
		});
	}

	public void getAreaList(final Context context, final String parentId,
			ZzObjectHttpResponseListener<Area> respListener) {
		this.objectResponseListener = respListener;
		String url = Constant.AREAURL + "?area_id=" + parentId;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {

				List<Area> mAreas = new Gson().fromJson(content.toLowerCase(),
						new TypeToken<List<Area>>() {
						}.getType());
				for (Area area : mAreas) {
					area.setParent(parentId);
				}

				AreaDao areaDao = new AreaDao(context);
				areaDao.startWritableDatabase(false);
				areaDao.delete("parent=?", new String[] { parentId });
				areaDao.insertList(mAreas, false);
				areaDao.closeDatabase(false);
				objectResponseListener.onSuccess(statusCode, mAreas);
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
				List<Area> areas;
				AreaDao areaDao = new AreaDao(context);
				areaDao.startReadableDatabase(false);
				areas = areaDao
						.queryList("parent=?", new String[] { parentId });
				areaDao.closeDatabase(false);
				objectResponseListener.onFailure(statusCode, content, error,
						areas);
			}
		});
	}

	/**
	 * 反查区域
	 * 
	 * @param context
	 * @param areaName
	 * @param respListener
	 */
	public void getAreaIdByAreaName(final Context context,
			final String areaName,
			final ZzStringHttpResponseListener respListener) {
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.AREANAME + areaName,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						respListener.onSuccess(statusCode, content);
					}

					@Override
					public void onStart() {
						respListener.onStart();
					}

					@Override
					public void onFinish() {
						respListener.onFinish();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						respListener.onFailure(statusCode, content, error);
					}
				});
	}
}
