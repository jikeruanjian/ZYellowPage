package com.zdt.zyellowpage.activity.webView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ab.util.AbStrUtil;
import com.zdt.zyellowpage.activity.webView.IntegratedWebView.OnPressBackListener;
import com.zdt.zyellowpage.activity.webView.IntegratedWebView.OritationChangeActivity;

public class MyWebViewActivity extends Activity implements
		OritationChangeActivity {
	private IntegratedWebView contentView;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contentView = new IntegratedWebView(this);
		this.setContentView(contentView);

		url = getIntent().getStringExtra("url");
		if (AbStrUtil.isEmpty(url)) {
			Toast.makeText(this, "地址错误", Toast.LENGTH_LONG).show();
			this.finish();
		}
		contentView.setVideoPlayerClient(this);
		contentView.loadUrl(url);

	}

	@Override
	protected void onResume() {
		contentView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		contentView.onPause();
		super.onPause();
	}

	@Override
	public void resetOritation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.setContentView(contentView);
	}

	private OnPressBackListener mPressBackListener;

	@Override
	public void setOppositeOritation(View view,
			OnPressBackListener pressBackListener) {
		this.mPressBackListener = pressBackListener;
		if (isFinishing()) {
			return;
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.setContentView(view);

	}

	@Override
	public void onBackPressed() {
		if (mPressBackListener != null) {
			mPressBackListener.onPressBack();
			mPressBackListener = null;
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		contentView.onDestroy();
		super.onDestroy();
	}
}
