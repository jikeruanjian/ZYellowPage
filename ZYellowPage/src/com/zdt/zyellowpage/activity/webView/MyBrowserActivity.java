package com.zdt.zyellowpage.activity.webView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.webView.IntegratedWebView.OnPressBackListener;
import com.zdt.zyellowpage.activity.webView.IntegratedWebView.OritationChangeActivity;

public class MyBrowserActivity extends AbActivity implements
		OritationChangeActivity {
	private IntegratedWebView contentView;
	String url;
	private String title;

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = new IntegratedWebView(this);
		setAbContentView(contentView);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(title);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		url = getIntent().getStringExtra("url");
		if (AbStrUtil.isEmpty(url)) {
			url = "http://m.321hy.cn/";
		}
		if (!url.toLowerCase().startsWith("http://")) {
			url = "http://" + url;
		}

		contentView.setVideoPlayerClient(this);
		try {
			contentView.loadUrl(url);
		} catch (Exception e) {
			Toast.makeText(this, "无法打开网页", Toast.LENGTH_LONG).show();
			this.finish();
		}
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
		this.finish();
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
	protected void onDestroy() {
		contentView.onDestroy();
		contentView = null;
		super.onDestroy();
		System.gc();
	}
}
