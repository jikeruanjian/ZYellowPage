package com.zdt.zyellowpage.activity;

import com.zdt.zyellowpage.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);

		ImageView iv = (ImageView) findViewById(R.id.ivSplash);
		iv.postDelayed(new Runnable() {

			@Override
			public void run() {
				SplashActivity.this.startActivity(new Intent(
						SplashActivity.this, MainActivity.class));
				SplashActivity.this.finish();
			}
		}, 2000);
	}
}
