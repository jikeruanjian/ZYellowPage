package com.zdt.zyellowpage.activity.webView;

import java.io.File;
import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Browser;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.zdt.zyellowpage.R;

public class IntegratedWebView extends LinearLayout implements DownloadListener {
	WebView wb;
	LinearLayout layMain;
	private ProgressBar pb;
	private VideoEnabledWebChromeClient chromeClient;

	public IntegratedWebView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View wbContainer = inflater.inflate(R.layout.wb, null);
		this.addView(wbContainer, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		layMain = (LinearLayout) this.findViewById(R.id.layMain);
		wb = (WebView) this.findViewById(R.id.inner_webview);
		wb.getSettings().setJavaScriptEnabled(true);
		this.pb = (ProgressBar) this.findViewById(R.id.webview_progressbar);
		wb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		wb.getSettings().setRenderPriority(RenderPriority.HIGH);
		WebSettings setting = wb.getSettings();
		setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		setting.setNeedInitialFocus(false);
		setting.setSaveFormData(true);
		setting.setSavePassword(false);
		wb.setDownloadListener(this);
		wb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && !url.toLowerCase().startsWith("http")) {
					Uri uri = Uri.parse(url);
					Context context = wb.getContext();
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
					return true;
				}
				wb.loadUrl(url);
				return true;
			}
		});
		wb.requestFocus();
	}

	public void setVideoPlayerClient(OritationChangeActivity activity) {
		chromeClient = new VideoEnabledWebChromeClient(activity);
		if (Build.VERSION.SDK_INT >= 14) {
			// 以VideoComplete引用名注入chromeClient对象 ，在下边的js中，回调playVideoEnd方法
			wb.addJavascriptInterface(chromeClient, "VideoComplete");
		}
		wb.setWebChromeClient(chromeClient);
	}

	@SuppressLint("NewApi")
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		Context context = getContext();
		if (context == null) {
			return;
		}
		wb.stopLoading();
		Uri uri = Uri.parse(url);
		if (Build.VERSION.SDK_INT >= 9) {
			DownloadManager dm = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Request request = new Request(uri);
			request.setMimeType(mimetype);
			request.setDescription("下载数据");
			String path = uri.getPath();
			int indexOf = path.lastIndexOf("/");
			request.setDestinationUri(Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), path.substring(indexOf + 1,
					path.length()))));

			request.setTitle("下载数据");
			if (Build.VERSION.SDK_INT >= 11) {
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			} else {
				request.setShowRunningNotification(true);
			}
			dm.enqueue(request);
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
		context.startActivity(intent);
	}

	public void onResume() {
		if (wb != null) {
			wb.resumeTimers();
			wb.onResume();
		}
	}

	public void onPause() {
		if (wb != null) {
			wb.pauseTimers();
			wb.onPause();
		}
	}

	public void onDestroy() {
		wb.stopLoading();
		wb.loadData("<a></a>", "text/html", "utf-8");
		layMain.removeView(wb);
		if (chromeClient != null) {
			chromeClient.onHideCustomView();
		}
		wb.removeAllViews();
		wb.destroy();
	}

	public class VideoEnabledWebChromeClient extends WebChromeClient implements
			OnCompletionListener, OnErrorListener {

		private boolean isVideoFullscreen; // Indicates if the video is being
											// displayed using a custom view
											// (typically full-screen)
		private FrameLayout videoViewContainer;
		private CustomViewCallback videoViewCallback;

		private WeakReference<OritationChangeActivity> activityRef;

		public VideoEnabledWebChromeClient(OritationChangeActivity activity) {
			this.activityRef = new WeakReference<OritationChangeActivity>(
					activity);
		}

		@Override
		public void onShowCustomView(View customView,
				CustomViewCallback callback) {
			if (isVideoFullscreen) {
				onHideCustomView();
			}

			if (customView instanceof FrameLayout) {
				// A video wants to be shown
				this.videoViewContainer = (FrameLayout) customView;
				this.videoViewContainer.setBackgroundColor(0xff000000);
				View focusedChild = this.videoViewContainer.getFocusedChild();
				// Save video related variables
				this.isVideoFullscreen = false;
				this.videoViewCallback = callback;
				if (activityRef.get() == null) {
					IntegratedWebView.this.removeAllViews();
					IntegratedWebView.this.addView(this.videoViewContainer,
							new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.WRAP_CONTENT));
					this.videoViewContainer
							.setOnKeyListener(new OnKeyListener() {
								@Override
								public boolean onKey(View v, int keyCode,
										KeyEvent event) {
									if (isVideoFullscreen
											&& KeyEvent.KEYCODE_BACK == keyCode) {
										onHideCustomView();
										return true;
									}
									return false;
								}
							});
				} else {
					activityRef.get().setOppositeOritation(
							this.videoViewContainer, new OnPressBackListener() {

								@Override
								public void onPressBack() {
									wb.loadData("", "text/html; charset=UTF-8",
											null);
									onHideCustomView();
								}
							});
				}
				if (focusedChild instanceof VideoView) {
					// VideoView (typically API level <11)
					VideoView videoView = (VideoView) focusedChild;
					// Handle all the required events
					videoView.setOnCompletionListener(this);
					videoView.setOnErrorListener(this);
				} else // Usually
						// android.webkit.HTML5VideoFullScreen$VideoSurfaceView,
						// sometimes
						// android.webkit.HTML5VideoFullScreen$VideoTextureView
				{
					// HTML5VideoFullScreen (typically API level 11+)
					// Handle HTML5 video ended event
					String js = "javascript:";
					js += "_ytrp_html5_video = document.getElementsByTagName('video')[0];";
					js += "if (_ytrp_html5_video !== undefined) {";
					{
						js += "function _ytrp_html5_video_ended() {";
						{
							js += "_ytrp_html5_video.removeEventListener('ended', _ytrp_html5_video_ended);";
							js += "VideoComplete.playVideoEnd();"; // Must match
																	// Javascript
																	// interface
																	// name and
																	// method of
																	// VideoEnableWebView
						}
						js += "}";
						js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
					}
					js += "}";
					wb.loadUrl(js);

				}
			}
		}

		public void playVideoEnd() {
			IntegratedWebView.this.post(new Runnable() {
				@Override
				public void run() {
					onHideCustomView();
				}
			});
		}

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) // Only available in API level 14+
		{
			onShowCustomView(view, callback);
		}

		@Override
		public synchronized void onHideCustomView() {

			// This method must be manually (internally) called on video end in
			// the case of HTML5VideoFullScreen (typically API level 11+)
			// because it's not always called automatically
			// This method must be manually (internally) called on back key
			// press (from this class' onBackPressed() method)
			if (isVideoFullscreen) {
				// Hide the video view, remove it, and show the non-video view
				// Call back
				isVideoFullscreen = false;
				if (activityRef.get() != null) {
					activityRef.get().resetOritation();
				}
				if (videoViewCallback != null)
					videoViewCallback.onCustomViewHidden();
				// Reset video related variables
				videoViewContainer = null;
				videoViewCallback = null;

			}
		}

		@Override
		public void onCompletion(MediaPlayer mp) // Video finished playing, only
													// called in the case of
													// VideoView (typically API
													// level <11)
		{
			onHideCustomView();
		}

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) // Error
																	// while
																	// playing
																	// video,
																	// only
																	// called in
																	// the case
																	// of
																	// VideoView
																	// (typically
																	// API level
																	// <11)
		{
			return false; // By returning false, onCompletion() will be called
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress != 100) {
				pb.setVisibility(View.VISIBLE);
				pb.setProgress(newProgress);
			} else {
				pb.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	public static interface OritationChangeActivity {
		public void setOppositeOritation(View view,
				OnPressBackListener pressBackListener);

		public void resetOritation();
	}

	public static interface OnPressBackListener {
		void onPressBack();
	}

	public void loadUrl(String url) {
		wb.loadUrl(url);
	}
}
