package com.zdt.zyellowpage.bll;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.dao.HotWordDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.HotWord;

/**
 * 获取热门关键词
 * 
 * @author Kevin
 * 
 */
@SuppressLint("DefaultLocale")
public class HotWordBll {

	/**
	 * 下载所有区域
	 * 
	 * @param context
	 */
	public void downHotWord(final int type, final Context context,
			final ZzStringHttpResponseListener respListener) {
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.HOTWORD + type,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(final int statusCode,
							final String content) {
						Log.i("HotWordBll", content);
						List<HotWord> mHotWord = new Gson().fromJson(
								content.toLowerCase(),
								new TypeToken<List<HotWord>>() {
								}.getType());
						for (HotWord hotWord : mHotWord) {
							hotWord.setType(type);
						}

						HotWordDao hotWordDao = new HotWordDao(context);
						hotWordDao.startWritableDatabase(true);
						hotWordDao.delete("Type=?", new String[] { "" + type });
						if (hotWordDao.insertListWithBatch(mHotWord, true) == mHotWord
								.size()) {
							hotWordDao.setTransactionSuccessful();
							hotWordDao.closeDatabase(true);
							respListener.onSuccess(statusCode, "更新成功");
						} else {
							hotWordDao.closeDatabase(true);
							respListener.onErrorData("插入失败");
						}
						// }
						// }).start();
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
