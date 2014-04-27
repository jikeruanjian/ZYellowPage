package com.zdt.zyellowpage.activity.fragment;

import java.io.File;
import java.util.List;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbStrUtil;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.MyConcernActivity;
import com.zdt.zyellowpage.activity.MyResourceActivity;
import com.zdt.zyellowpage.activity.login.ChangePwdActivity;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.VersionBll;
import com.zdt.zyellowpage.customView.CircularImage;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Version;
import com.zdt.zyellowpage.util.DisplayUtil;

public class FragmentUser extends Fragment {
	AbActivity mActivity;
	MyApplication application;
	TextView btnLogin;
	CircularImage imageUserLogo;
	Button btnVersionCode;
	Button btnChangePwd;
	Button btnMyFellow;
	Button btnMyResource;
	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_user, container, false);
		mActivity = (AbActivity) getActivity();
		DisplayUtil displayUtil = DisplayUtil.getInstance(mActivity);
		application = (MyApplication) mActivity.getApplication();
		btnLogin = (TextView) view.findViewById(R.id.buttonlogin);
		imageUserLogo = (CircularImage) view.findViewById(R.id.imageHead);
		btnVersionCode = (Button) view.findViewById(R.id.tvVersionCode);
		btnChangePwd = (Button) view.findViewById(R.id.changePwd);
		btnMyFellow = (Button) view.findViewById(R.id.myFellow);
		btnMyResource = (Button) view.findViewById(R.id.myResource);

		btnVersionCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new VersionBll().getVersion(mActivity,
						new ZzObjectHttpResponseListener<Version>() {

							@Override
							public void onSuccess(int statusCode,
									List<Version> lis) {
								Version version = lis.get(0);
								// 模拟一个
								version = new Version();
								version.setApp_url("http://music.baidu.com/cms/mobile/static/apk/BaiduMusic_musicsybutton.apk");
								if (version != null
										&& !AbStrUtil.isEmpty(version
												.getApp_url())) {
									Intent i = new Intent(Intent.ACTION_VIEW,
											Uri.parse(version.getApp_url()));
									mActivity.startActivity(i);
								}
							}

							@Override
							public void onStart() {
								mActivity.showProgressDialog("请稍候...");
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								mActivity.removeProgressDialog();
							}

							@Override
							public void onFailure(int statusCode,
									String content, Throwable error,
									List<Version> localList) {
								// TODO Auto-generated method stub
								mActivity.showToast(content);
							}

							@Override
							public void onErrorData(String status_description) {
								// TODO Auto-generated method stub
								Version version = new Version();
								version.setApp_url("http://music.baidu.com/cms/mobile/static/apk/BaiduMusic_musicsybutton.apk");
								if (version != null
										&& !AbStrUtil.isEmpty(version
												.getApp_url())) {
									downApk(version);
								}
							}
						});
			}
		});

		btnChangePwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.startActivity(new Intent(mActivity,
						ChangePwdActivity.class));
			}
		});

		btnMyResource.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MyResourceActivity.class);
				startActivity(intent);
			}
		});

		btnMyFellow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MyConcernActivity.class);
				startActivity(intent);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, LoginActivity.class);
				startActivity(intent);
			}
		});

		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.RelativeLayoutLogin), width);
		view.findViewById(R.id.layoutChangeUserInfo).setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (application.mUser != null
				&& !AbStrUtil.isEmpty(application.mUser.getLogo())) {
			new AbImageDownloader(mActivity).display(imageUserLogo,
					application.mUser.getLogo());
		}
		if (application.mUser != null && application.mUser.getToken() != null) {
			btnLogin.setVisibility(View.GONE);
			view.findViewById(R.id.layoutChangeUserInfo).setVisibility(
					View.VISIBLE);

		}
	}

	private void getVersion() {
		PackageManager packageManager = mActivity.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(
					mActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = "code" + packInfo.versionCode + packInfo.versionName;
		mActivity.showToast(version);
	}

	public void downApk(Version version) {
		String serviceString = Context.DOWNLOAD_SERVICE;
		final DownloadManager downloadManager = (DownloadManager) mActivity
				.getSystemService(serviceString);
		File file = new File(Environment.DIRECTORY_DOWNLOADS
				+ "/YellowPage.apk");
		if (file.exists()) {
			file.delete();
		}
		DownloadManager.Request request = new Request(Uri.parse(version
				.getApp_url()));
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS,
				"YellowPage" + version.getVersion_description() + ".apk");
		final long reference = downloadManager.enqueue(request);

		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (reference == intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
					Query myQuery = new Query();
					myQuery.setFilterById(reference);
					Cursor myDownload = downloadManager.query(myQuery);
					String uri = null;
					if (myDownload.moveToFirst()) {
						int fileUriIndex = myDownload
								.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
						uri = myDownload.getString(fileUriIndex);
					}
					installApk(Uri.parse(uri));
				}
			}
		};
		mActivity.registerReceiver(receiver, filter);
	}

	protected void installApk(Uri uri) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
		intent.setData(uri);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}
}
