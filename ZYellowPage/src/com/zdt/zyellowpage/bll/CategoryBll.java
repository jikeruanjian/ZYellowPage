package com.zdt.zyellowpage.bll;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdt.zyellowpage.dao.CategoryDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Category;

/**
 * 获取分类信息
 * 
 * @author Kevin
 * 
 */
@SuppressLint("DefaultLocale")
public class CategoryBll {

	ZzObjectHttpResponseListener<Category> objectResponseListener;
	Context mContext;

	public void downAllCategory(final Context context,
			final ZzStringHttpResponseListener respListener) {
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		mAbHttpUtil.get(Constant.ALLCATEGORY,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(final int statusCode,
							final String content) {
						// new Thread(new Runnable() {
						//
						// @Override
						// public void run() {
						List<Category> mCategorys = new Gson().fromJson(
								content.toLowerCase(),
								new TypeToken<List<Category>>() {
								}.getType());

						CategoryDao categoryDao = new CategoryDao(context);
						categoryDao.startWritableDatabase(true);
						categoryDao.deleteAll();
						if (categoryDao.insertListWithBatch(mCategorys, false) == mCategorys
								.size()) {
							categoryDao.setTransactionSuccessful();
							categoryDao.closeDatabase(true);
							respListener.onSuccess(statusCode, "更新成功");
						} else {
							respListener.onErrorData("数据拆入失败");
							categoryDao.closeDatabase(true);
						}
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

	/**
	 * 获取列表
	 * 
	 * @param context
	 * @param albumParams
	 * @param respListener
	 */
	public void getCategoryist(Context context, final String category_id,
			final String type,
			ZzObjectHttpResponseListener<Category> respListener) {
		this.mContext = context;
		objectResponseListener = respListener;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(context);
		String url = Constant.CATEGORYURL + "?category_id=" + category_id
				+ "&type=" + type;

		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				if (content != null && !content.equals("")) {
					Log.i("CategoryBll", content);
					List<Category> mCategory = new Gson().fromJson(
							content.toLowerCase(),
							new TypeToken<List<Category>>() {
							}.getType());
					for (Category category : mCategory) {
						category.setParent(category_id);
						category.setType(type);
					}

					CategoryDao categoryDao = new CategoryDao(mContext);
					categoryDao.startWritableDatabase(true);

					categoryDao.delete("parent=? and type=?", new String[] {
							category_id, type });

					if (mCategory != null) {
						categoryDao.insertListWithBatch(mCategory, false);
					}
					categoryDao.closeDatabase(true);
					objectResponseListener.onSuccess(statusCode, mCategory);
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

				objectResponseListener.onFailure(statusCode, content, error,
						null);
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				objectResponseListener.onFinish();
			};
		});
	}

}
