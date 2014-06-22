package com.zdt.zyellowpage.activity.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageCache;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbHttpUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.AddPhotoActivity;
import com.zdt.zyellowpage.activity.CertificateListActivity;
import com.zdt.zyellowpage.activity.EditPersonBaseResourceActivity;
import com.zdt.zyellowpage.activity.MainActivity;
import com.zdt.zyellowpage.activity.MyConcernActivity;
import com.zdt.zyellowpage.activity.MyResourceActivity;
import com.zdt.zyellowpage.activity.NewsContentDetailActivity;
import com.zdt.zyellowpage.activity.login.ChangePwdActivity;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.VersionBll;
import com.zdt.zyellowpage.customView.WrapContentListView;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Version;
import com.zdt.zyellowpage.util.DisplayUtil;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class FragmentUser extends Fragment {
	AbActivity mActivity;
	MyApplication application;
	Button btnLogin;
	ImageView imageUserLogo;// 头像
	ImageView imageQr;// 二维码
	TextView tvUserName;
	Button btnLogout;
	View view;
	WrapContentListView lvMenu;
	LinearLayout rllUserInfo; // 用户信息的layout
	RelativeLayout rllLogin;// 未登录layout
	ScrollView svMain;
	List<String> commonMenu = new ArrayList<String>();
	MenuAdapter menuAdapter;
	DisplayUtil displayUtil;
	Bitmap codeBitmap;
	AbImageDownloader imageLoader;

	// 二维码弹出框
	private View mView;
	Version version;// 服务器上最新的版本

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_user, container, false);
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.getApplication();
		btnLogin = (Button) view.findViewById(R.id.buttonlogin);
		imageUserLogo = (ImageView) view.findViewById(R.id.imageHead);
		imageQr = (ImageView) view.findViewById(R.id.imageQr);
		tvUserName = (TextView) view.findViewById(R.id.tvUserName);
		btnLogout = (Button) view.findViewById(R.id.btnLogout);
		lvMenu = (WrapContentListView) view.findViewById(R.id.lvMenu);
		svMain = (ScrollView) view.findViewById(R.id.scMain);
		rllUserInfo = (LinearLayout) view.findViewById(R.id.rllUserInfo);
		rllLogin = (RelativeLayout) view.findViewById(R.id.rllLogin);
		imageLoader = new AbImageDownloader(mActivity);

		// lvMenu.setParentScrollView(svMain);
		// lvMenu.setMaxHeight(700);
		// lvMenu.setMinimumHeight(300);

		commonMenu.add("清除缓存");
		commonMenu.add("版本更新");
		commonMenu.add("关于我们");

		menuAdapter = new MenuAdapter(mActivity);
		lvMenu.setAdapter(menuAdapter);
		imageUserLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = null;
				intent = new Intent(mActivity, AddPhotoActivity.class);
				intent.putExtra("title", "设置Logo");
				mActivity.startActivity(intent);
			}
		});
		imageQr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChosePopWindow();
			}
		});
		// getCodeData();

		lvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				String menuTitle = menuAdapter.getItem(postion);
				if (menuTitle.equals("清除缓存")) {
					AbFileUtil.removeAllFileCache();
					AbImageCache.removeAllBitmapFromCache(); // 先要清空缓存
					mActivity.showToast("缓存清除完成");
				} else if (menuTitle.contains("版本更新")) {
					new VersionBll().getVersion(mActivity,
							new ZzObjectHttpResponseListener<Version>() {

								@Override
								public void onSuccess(int statusCode,
										List<Version> lis) {
									version = lis.get(0);

									if (version != null
											&& !AbStrUtil.isEmpty(version
													.getApp_url())
											&& version.getBuild_number() > getVersion()) {
										Editor editor = mActivity.abSharedPreferences
												.edit();
										editor.putInt(
												Constant.LOCATEVERSIONCODE,
												version.getBuild_number());
										editor.commit();

										((MainActivity) mActivity).version = version;
										SimpleDialogFragment
												.createBuilder(
														mActivity,
														mActivity
																.getSupportFragmentManager())
												.setTitle(
														"发现新版本"
																+ version
																		.getVersion())
												.setMessage(
														"是否更新？\r\n"
																+ version
																		.getVersion_description())
												.setPositiveButtonText("立即更新")
												.setNegativeButtonText("以后再说")
												.setRequestCode(52)
												.setTag(MainActivity.TAG)
												.show();
									} else {
										SimpleDialogFragment
												.createBuilder(
														mActivity,
														mActivity
																.getSupportFragmentManager())
												.setTitle("提示")
												.setMessage("当前已经是最新版本")
												.setPositiveButtonText("确定")
												.setRequestCode(53)
												.setTag(MainActivity.TAG)
												.show();
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
								public void onErrorData(
										String status_description) {
									mActivity.showToast(status_description);
								}
							});
				} else if (menuTitle.equals("关于我们")) {
					Intent intent = new Intent(mActivity,
							NewsContentDetailActivity.class);
					intent.putExtra("item_id", "1");
					mActivity.startActivity(intent);
				} else if (menuTitle.equals("我的资料")) {
					Intent intent = null;
					if (application.mUser.getType() == 0) {
						intent = new Intent(mActivity, MyResourceActivity.class);
					} else {
						intent = new Intent(mActivity,
								EditPersonBaseResourceActivity.class);
					}
					mActivity.startActivity(intent);
				} else if (menuTitle.equals("我的资质")) {
					Intent intent = null;
					intent = new Intent(mActivity,
							CertificateListActivity.class);
					intent.putExtra("MEMBER_ID",
							application.mUser.getMember_id());
					mActivity.startActivity(intent);
				} else if (menuTitle.equals("我的关注")) {
					Intent intent = new Intent(mActivity,
							MyConcernActivity.class);
					mActivity.startActivity(intent);
				} else if (menuTitle.equals("修改密码")) {
					mActivity.startActivity(new Intent(mActivity,
							ChangePwdActivity.class));
				}

			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intent);
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				application.mUser = null;
				FragmentUser.this.onResume();
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (application.mUser != null
				&& !AbStrUtil.isEmpty(application.mUser.getLogo())) {
			imageLoader.display(imageUserLogo, application.mUser.getLogo());

			String url = application.mUser.getQr_code() + "&area="
					+ application.cityid;
			AbHttpUtil.getInstance(mActivity).get(url,
					new AbBinaryHttpResponseListener() {

						// 获取数据成功会调用这里
						@Override
						public void onSuccess(int statusCode, byte[] content) {
							codeBitmap = AbImageUtil.bytes2Bimap(content);
							imageQr.setImageBitmap(codeBitmap);
						}

						// 开始执行前
						@Override
						public void onStart() {
						}

						// 失败，调用
						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
						}

						// 完成后调用，失败，成功
						@Override
						public void onFinish() {
						};
					});
		}
		if (application.mUser != null && application.mUser.getToken() != null) {
			rllUserInfo.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.VISIBLE);
			rllLogin.setVisibility(View.GONE);
			tvUserName.setText(AbStrUtil.isEmpty(application.mUser
					.getFullname()) ? application.mUser.getUsername()
					: application.mUser.getFullname());

			// 增加menu
			List<String> addMenu = new ArrayList<String>();
			if (application.mUser.getType() == 0) {
				addMenu.add("我的资料");
				addMenu.add("我的关注");
				addMenu.add("修改密码");
			} else {
				addMenu.add("我的资料");
				addMenu.add("我的资质");
				addMenu.add("我的关注");
				addMenu.add("修改密码");
			}
			addMenu.addAll(commonMenu);
			menuAdapter.setLisMenu(addMenu);
			// displayUtil = DisplayUtil.getInstance(mActivity);
			// displayUtil.setListViewHeightBasedOnChildren(lvMenu , 7);
		} else {
			rllUserInfo.setVisibility(View.GONE);
			btnLogout.setVisibility(View.GONE);
			rllLogin.setVisibility(View.VISIBLE);
			menuAdapter.setLisMenu(commonMenu);
		}
	}

	/**
	 * 编辑更多电话按钮
	 * 
	 * @author Kevin
	 * 
	 */
	public class MenuAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<String> lisMenu;

		public List<String> getLisMenu() {
			return lisMenu;
		}

		public void setLisMenu(List<String> lisMenu) {
			this.lisMenu = lisMenu;
			this.notifyDataSetChanged();
		}

		public MenuAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public MenuAdapter(Context context, List<String> lisMenu) {
			this.mInflater = LayoutInflater.from(context);
			this.lisMenu = lisMenu;
		}

		@Override
		public int getCount() {
			return lisMenu == null ? 0 : lisMenu.size();
		}

		@Override
		public String getItem(int arg0) {
			return lisMenu.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_menu, null);
				holder.menuTitle = (TextView) convertView
						.findViewById(R.id.menuTitle);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.menuTitle.setText(lisMenu.get(position));
			return convertView;
		}
	}

	public final class ViewHolder {
		public TextView menuTitle;
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
				+ version.getVersion() + ".apk");
		if (file.exists()) {
			file.delete();
		}
		DownloadManager.Request request = new Request(Uri.parse(version
				.getApp_url()));
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS,
				"YellowPage" + version.getVersion() + ".apk");
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
		mActivity.startActivity(intent);
	}

	public void showChosePopWindow() {
		View mChooseView = mActivity.mInflater.inflate(
				R.layout.choose_lookimage, null);
		mActivity.showDialog(1, mChooseView);
		// 查看
		mChooseView.findViewById(R.id.choose_lookimage).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mView = mActivity.mInflater.inflate(R.layout.code_view,
								null);
						ImageView imageUserCode = (ImageView) mView
								.findViewById(R.id.imageViewCodeCP);
						imageUserCode.setImageBitmap(codeBitmap);
						mActivity.removeDialog(1);
						mActivity.showDialog(AbConstant.DIALOGCENTER, mView);

						imageUserCode.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								mActivity.removeDialog(AbConstant.DIALOGCENTER);
							}

						});
					}

				});
		// 保存
		mChooseView.findViewById(R.id.choose_saveimagecode).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.removeDialog(1);
						showPopupWindow();
					}
				});
		// 取消
		mChooseView.findViewById(R.id.choose_cancelimage).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.removeDialog(1);
					}
				});
	}

	public void showPopupWindow() {
		View mAvatarView = mActivity.mInflater.inflate(
				R.layout.choose_saveimage, null);
		mActivity.showDialog(1, mAvatarView);
		mAvatarView.findViewById(R.id.choose_saveimage4).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getStringImageUrl("");

					}
				});
		mAvatarView.findViewById(R.id.choose_saveimage8).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getStringImageUrl("s=20&");
					}
				});
		mAvatarView.findViewById(R.id.choose_saveimage16).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getStringImageUrl("s=40&");
					}
				});
		mAvatarView.findViewById(R.id.choose_saveimage28).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getStringImageUrl("s=70&");
					}
				});

	}

	public String getStringImageUrl(String size) {
		String saveImageUrl = "http://f.321hy.cn/QR/"
				+ application.mUser.getType() + "/"
				+ application.mUser.getMember_id() + "?" + size
				+ "url=http://f.321hy.cn/Upload/"
				+ application.mUser.getMember_id() + "/Logo/Avatar.jpg&area="
				+ application.cityid;
		AbHttpUtil.getInstance(mActivity).get(saveImageUrl,
				new AbBinaryHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, byte[] content) {
						Bitmap codeBitmap = AbImageUtil.bytes2Bimap(content);
						String imgUrl = MediaStore.Images.Media.insertImage(
								mActivity.getContentResolver(), codeBitmap, "",
								"");
						Log.e("save codeimage", imgUrl);
						// mActivity.removeDialog(AbConstant.DIALOGCENTER);
						mActivity.removeDialog(1);
						mActivity.showToast("二维码成功保存到相册！");
					}

					// 开始执行前
					@Override
					public void onStart() {
						// 显示进度框
						mActivity.showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						mActivity.showToast(error.getMessage());
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						// 移除进度框
						mActivity.removeProgressDialog();
					};

				});
		return saveImageUrl;
	}
}
