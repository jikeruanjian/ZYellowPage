package com.zdt.zyellowpage.bll;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.dao.HotKeyWorkDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.HotKeyWord;

/**
 * 获取热门关键词
 * 
 * @author Kevin
 * 
 */
@SuppressLint("DefaultLocale")
public class HotKeyWordBll {

	/**
	 * 下载所有区域
	 * 
	 * @param context
	 */
	public void downAllKeyWord(final Context context,
			final ZzStringHttpResponseListener respListener) {
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.ALLHOTKEYWORD, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {

				List<HotKeyWord> mKeyWord = new Gson().fromJson(content.toLowerCase(),
						new TypeToken<List<HotKeyWord>>() {
						}.getType());

						HotKeyWorkDao hotKeyWordDao = new HotKeyWorkDao(context);
				hotKeyWordDao.startWritableDatabase(true);
				hotKeyWordDao.deleteAll();
				hotKeyWordDao.insertList(mKeyWord, true);
				hotKeyWordDao.closeDatabase(true);
				respListener.onSuccess(statusCode, "更新成功");
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
