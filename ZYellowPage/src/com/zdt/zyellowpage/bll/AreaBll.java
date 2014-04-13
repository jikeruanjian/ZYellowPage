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
import com.zdt.zyellowpage.model.Area;

/**
 * 1、获取区域的名称
 * 
 * @author Kevin
 * 
 */
public class AreaBll {

	ZzObjectHttpResponseListener<Area> objectResponseListener;

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
					area.setParentId(parentId);
				}

				AreaDao areaDao = new AreaDao(context);
				areaDao.startWritableDatabase(false);
				areaDao.delete("parentId=?", new String[] { parentId });
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
				// showToast(error.getMessage());
				// removeProgressDialog();
				List<Area> areas;
				AreaDao areaDao = new AreaDao(context);
				areaDao.startReadableDatabase(false);
				areas = areaDao.queryList("parentId=?",
						new String[] { parentId });
				areaDao.closeDatabase(false);
				objectResponseListener.onFailure(statusCode, content, error,
						areas);
			}
		});
	}
}
