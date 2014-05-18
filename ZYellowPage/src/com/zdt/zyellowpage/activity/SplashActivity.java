package com.zdt.zyellowpage.activity;

import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.MyApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends AbActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);

		ImageView iv = (ImageView) findViewById(R.id.ivSplash);
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
				//MainActivity.getCategoryList( "0");
				//MainActivity.getCategoryListP( "0");
				SplashActivity.this.finish();
			}
		}, 2000);
	}
}
