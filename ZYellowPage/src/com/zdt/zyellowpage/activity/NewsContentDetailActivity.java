package com.zdt.zyellowpage.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.NewsContentBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

public class NewsContentDetailActivity extends AbActivity {
	MyApplication application;
	String item_id = "1";// 1为关于我们，2为商家服务条款3为个人服务条
	private WebView mWebView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.wb);

		item_id = getIntent().getStringExtra("item_id");
		if (AbStrUtil.isEmpty(item_id)) {
			showToast("参数错误");
			this.finish();
		}
		AbTitleBar mAbTitleBar = this.getTitleBar();
		if ("1".equals(item_id)) {
			mAbTitleBar.setTitleText("关于我们");
		} else {
			mAbTitleBar.setTitleText("服务条款");
		}
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		application = (MyApplication) abApplication;

		mWebView = (WebView) findViewById(R.id.inner_webview);
		// 设置支持JavaScript脚本
		WebSettings webSettings = mWebView.getSettings();

		webSettings.setDefaultTextEncodingName("UTF-8");
//		mWebView.setInitialScale(100);

		new NewsContentBll().getNewsContent(this, item_id,
				new ZzStringHttpResponseListener() {

					@Override
					public void onSuccess(int statusCode, String content) {
						mWebView.loadDataWithBaseURL(null, content,
								"text/html", "utf-8", null);
					}

					@Override
					public void onStart() {
						showProgressDialog("请稍候...");

					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showProgressDialog(content);
					}

					@Override
					public void onErrorData(String status_description) {
						showProgressDialog(status_description);
					}
				});

	}
}
