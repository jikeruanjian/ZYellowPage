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
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

public class SplashActivity extends AbActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);

		ImageView iv = (ImageView) findViewById(R.id.ivSplash);
		SharedPreferences sp = getSharedPreferences(AbConstant.SHAREPATH,
				Context.MODE_PRIVATE);
		Long lastUpdateTime = sp.getLong(Constant.LASTUPDATETIME, 0);
		if ((new Date().getTime() - lastUpdateTime) > 86400000) {
			downData();
		} else {
			iv.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (((MyApplication) SplashActivity.this.getApplication()).firstStart) {
						SplashActivity.this.startActivity(new Intent(
								SplashActivity.this, SelectAreaActivity.class));
					} else {
						SplashActivity.this.startActivity(new Intent(
								SplashActivity.this, MainActivity.class));
					}
					SplashActivity.this.finish();
				}
			}, 1500);
		}

	}

	private void downData() {
		new AreaBll().downAllArea(SplashActivity.this,
				new ZzStringHttpResponseListener() {

					@Override
					public void onSuccess(int statusCode, String content) {
						// 记录时间
						Editor editor = abSharedPreferences.edit();
						editor.putLong(Constant.LASTUPDATETIME,
								new Date().getTime());
						editor.commit();
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFinish() {
						if (((MyApplication) SplashActivity.this
								.getApplication()).firstStart) {
							SplashActivity.this.startActivity(new Intent(
									SplashActivity.this,
									SelectAreaActivity.class));
						} else {
							SplashActivity.this.startActivity(new Intent(
									SplashActivity.this, MainActivity.class));
						}
						SplashActivity.this.finish();
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
}
