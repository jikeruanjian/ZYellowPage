package com.zdt.zyellowpage.activity.webView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ab.R;
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
		// url = "http://player.youku.com/embed/XNzI5MTk2OTYw";
		if (AbStrUtil.isEmpty(url)) {
			Toast.makeText(this, "视频地址错误", Toast.LENGTH_LONG).show();
			this.finish();
		}
		// http://player.youku.com/player.php/sid/XNzI5MTk2OTYw/v.swf
		String[] tempIDs = url.split("/");
		if (tempIDs.length > 2) {
			String tempID = tempIDs[tempIDs.length - 2];
			url = "http://player.youku.com/embed/" + tempID;
		} else {
			Toast.makeText(this, "不能解析该视频地址", Toast.LENGTH_LONG).show();
			this.finish();
		}
		contentView.setVideoPlayerClient(this);
		try {
			contentView.loadUrl(url);
		} catch (Exception e) {
			Toast.makeText(this, "无法播放该视频!", Toast.LENGTH_LONG).show();
			this.finish();
		}

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
		this.finish();
	}

	@Override
	protected void onDestroy() {
		contentView.onDestroy();
		contentView = null;
		super.onDestroy();
		System.gc();
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.push_right_out,
				R.anim.push_right_in);
	}
}
