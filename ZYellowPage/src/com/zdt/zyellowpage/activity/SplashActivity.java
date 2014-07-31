package com.zdt.zyellowpage.activity;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.global.AbConstant;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AreaBll;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.bll.HotKeyWordBll;
import com.zdt.zyellowpage.bll.HotWordBll;
import com.zdt.zyellowpage.dao.HotWordDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

public class SplashActivity extends AbActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);
		this.isShowAnim = false; // finish的时候不调动画
		downData();

		ImageView iv = (ImageView) findViewById(R.id.ivSplash);
		iv.postDelayed(new Runnable() {

			@Override
			public void run() {
				SplashActivity.this.startActivity(new Intent(
						SplashActivity.this, MainActivity.class));
				SplashActivity.this.finish();
			}
		}, 400);
	}

	private void downData() {
		SharedPreferences sp = getSharedPreferences(AbConstant.SHAREPATH,
				Context.MODE_PRIVATE);
		Long areaLastUpdateTime = sp.getLong(Constant.AREALASTUPDATETIME, 0);
		Long categoryLastUpdateTime = sp.getLong(
				Constant.CATEGORYLASTUPDATETIME, 0);
		Long hotkeyLastUpdateTime = sp
				.getLong(Constant.HOTKEYLASTUPDATETIME, 0);
		Long hotwordLastUpdateTime = sp.getLong(Constant.HOTWordLASTUPDATETIME,
				0);
		if ((new Date().getTime() - areaLastUpdateTime) > 86400000) {
			// 下载区域
			new AreaBll().downAllArea(SplashActivity.this,
					new ZzStringHttpResponseListener() {

						@Override
						public void onSuccess(int statusCode, String content) {
							// 记录时间
							Editor editor = abSharedPreferences.edit();
							editor.putLong(Constant.AREALASTUPDATETIME,
									new Date().getTime());
							editor.commit();
						}

						@Override
						public void onStart() {
						}

						@Override
						public void onFinish() {
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
						}

						@Override
						public void onErrorData(String status_description) {
						}
					});
		}
		if ((new Date().getTime() - categoryLastUpdateTime) > 86400000) {
			// 下载分类
			new CategoryBll().downAllCategory(SplashActivity.this,
					new ZzStringHttpResponseListener() {

						@Override
						public void onSuccess(int statusCode, String content) {
							Editor editor = abSharedPreferences.edit();
							editor.putLong(Constant.CATEGORYLASTUPDATETIME,
									new Date().getTime());
							editor.commit();
						}

						@Override
						public void onStart() {
						}

						@Override
						public void onFinish() {
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
						}

						@Override
						public void onErrorData(String status_description) {
						}
					});
		}
		// 热门关键
		if ((new Date().getTime() - hotkeyLastUpdateTime) > 86400000) {
			new HotKeyWordBll().downAllKeyWord(this,
					new ZzStringHttpResponseListener() {

						@Override
						public void onSuccess(int statusCode, String content) {
							Editor editor = abSharedPreferences.edit();
							editor.putLong(Constant.HOTKEYLASTUPDATETIME,
									new Date().getTime());
							editor.commit();
						}

						@Override
						public void onStart() {
						}

						@Override
						public void onFinish() {
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
						}

						@Override
						public void onErrorData(String status_description) {
						}
					});
		}

		// 热词 ， 间隔1个月
		HotWordDao hotWordDao = new HotWordDao(this);
		hotWordDao.startReadableDatabase(false);
		int count = hotWordDao.queryCount(null, null);
		hotWordDao.closeDatabase(false);
		if (count == 0
				|| (new Date().getTime() - hotwordLastUpdateTime) > 2592000000L) {
			int[] types = new int[] { 1, 2, 3, 5, 6, 7 };
			for (int i : types) {
				new HotWordBll().downHotWord(i, this,
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								Editor editor = abSharedPreferences.edit();
								editor.putLong(Constant.HOTWordLASTUPDATETIME,
										new Date().getTime());
								editor.commit();
							}

							@Override
							public void onStart() {
							}

							@Override
							public void onFinish() {
							}

							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
							}

							@Override
							public void onErrorData(String status_description) {
							}
						});
			}
		}

	}
}
