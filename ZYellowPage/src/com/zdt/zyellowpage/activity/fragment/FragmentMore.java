package com.zdt.zyellowpage.activity.fragment;

import java.io.File;
import java.util.List;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageCache;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.TieListActivity;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.VersionBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Version;

public class FragmentMore extends Fragment {

	AbActivity mActivity;
	MyApplication application;

	Button btnTie; // 喜庆专区
	Button btnShao;// 扫一扫
	Button btnClearCache; // 喜庆专区
	Button btnCheckUpdate;// 扫一扫
	Button btnAboutUs; // 喜庆专区

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.getApplication();
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		btnTie = (Button) view.findViewById(R.id.tie);
		btnShao = (Button) view.findViewById(R.id.shao);
		btnClearCache = (Button) view.findViewById(R.id.clearCache);
		btnCheckUpdate = (Button) view.findViewById(R.id.checkUpdate);
		btnAboutUs = (Button) view.findViewById(R.id.aboutUs);

		// 进入喜庆专区
		btnTie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);

				mActivity.startActivity(intent);
			}
		});

		// 打开扫一扫
		btnShao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.showToast("功能开发中...");
			}
		});

		// 关于我们
		btnAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		// 检查新版本
		btnCheckUpdate.setOnClickListener(new OnClickListener() {

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
								version.setVersion("1.0.2");
								version.setVersion_description("普通升级");
								version.setApp_url("http://music.baidu.com/cms/mobile/static/apk/BaiduMusic_musicsybutton.apk");

								final Version tempVersion = version;
								if (version != null
										&& !AbStrUtil.isEmpty(version
												.getApp_url())
										&& version.getBuild_number() > getVersion()) {
									mActivity.showDialog(
											"发现新版本" + version.getVersion(),
											"是否更新？\r\n"
													+ version
															.getVersion_description(),
											new android.content.DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													downApk(tempVersion);
												}
											});
								} else {
									mActivity.showDialog("升级", "当前已是最新版");
								}
							}

							@Override
							public void onStart() {
								mActivity.showProgressDialog("请稍候...");
							}

							@Override
							public void onFinish() {
								mActivity.removeProgressDialog();
							}

							@Override
							public void onFailure(int statusCode,
									String content, Throwable error,
									List<Version> localList) {
								mActivity.showToast(content);
							}

							@Override
							public void onErrorData(String status_description) {
								mActivity.showToast(status_description);
							}
						});
			}
		});

		btnClearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AbFileUtil.removeAllFileCache();
				AbImageCache.removeAllBitmapFromCache(); // 先要清空缓存
				mActivity.showToast("缓存清除完成");
			}
		});

		return view;
	}

	private int getVersion() {
		PackageManager packageManager = mActivity.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(
					mActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// String version = "code" + packInfo.versionCode +
		// packInfo.versionName;
		return packInfo.versionCode;
	}

	private void downApk(Version version) {
		String serviceString = Context.DOWNLOAD_SERVICE;
		final DownloadManager downloadManager = (DownloadManager) mActivity
				.getSystemService(serviceString);
		File file = new File(Environment.DIRECTORY_DOWNLOADS + "YellowPage"
				+ version.getVersion_description() + ".apk");
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
