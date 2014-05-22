package com.zdt.zyellowpage.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.util.verify.BNKeyVerifyListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.fragment.FragmentHomePage;
import com.zdt.zyellowpage.activity.fragment.FragmentNearMap;
import com.zdt.zyellowpage.activity.fragment.FragmentTie;
import com.zdt.zyellowpage.activity.fragment.FragmentUser;
import com.zdt.zyellowpage.barcode.CaptureActivity;
import com.zdt.zyellowpage.bll.AreaBll;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.bll.VersionBll;
import com.zdt.zyellowpage.dao.AreaDao;
import com.zdt.zyellowpage.dao.CategoryDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Area;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.model.Version;
import com.zdt.zyellowpage.util.DisplayUtil;

public class MainActivity extends AbActivity implements OnCheckedChangeListener {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private FragmentHomePage newFragmentHome = null;
	private FragmentNearMap newFragmentNearMap = null;
	private FragmentUser newFragmentUser = null;
	private FragmentTie newFragmentTie = null;
	public static BMapManager mBMapMan = null;
	private TextView textViewArea;
	private MyApplication application;
	private EditText editRearch;
	private TextView textVSearch;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	private String types[] = { "商家", "个人" };
	boolean isFirst = false;
	// 区域列表
	public static List<Area> listArea;
	public static List<String> listAreaName;
	// 商家分类列表
	public static List<Category> listCategory;
	public static List<String> listCategoryName;
	// 个人分类列表
	public static List<Category> listCategoryP;
	public static List<String> listCategoryNameP;
	// 个人子分类列表
	public static List<List<Category>> listChildCategoryP;
	// 商家子分类列表
	public static List<List<Category>> listChildCategory;
	public static Context mContext;

	private long mExitTime;
	private boolean mIsEngineInitSuccess = false;
	private String mCityName = null;

	LocationClient mLocationClient;
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				break;

			}
		}

		private void showDialog(String title, String msg,
				DialogInterface.OnClickListener mOkOnClickListener) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage(msg);
			builder.setTitle(title);
			builder.setPositiveButton("确认", mOkOnClickListener);
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}

	};

	private Thread myThread = new Thread(new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			MainActivity.this.myHandler.sendMessage(msg);

		}

	});

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 10000) {
			if (resultCode == RESULT_OK) {
				textViewArea.setText(application.cityName);
				MainActivity.getAreaList(MainActivity.this, application.cityid);
				newFragmentHome.getData();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (MyApplication) abApplication;
		initView();
		// initHomePagePullView();
		// 地图
		mBMapMan = new BMapManager(getApplication());
		// E25ED402F8E85C1714F86CC9042EA1B32BE151B2
		mBMapMan.init("RjlfVWfEcAecRGc5qG8xyLoX", null);
		// 导航
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
				mNaviEngineInitListener, "RjlfVWfEcAecRGc5qG8xyLoX",
				mKeyVerifyListener);

		fragmentManager = this.getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		newFragmentHome = new FragmentHomePage();
		newFragmentUser = new FragmentUser();
		newFragmentTie = new FragmentTie();

		fragmentTransaction.add(R.id.fragmentViewHome, newFragmentHome, "home");
		fragmentTransaction.add(R.id.fragmentViewUser, newFragmentUser, "user");
		fragmentTransaction.add(R.id.fragmentViewMore, newFragmentTie, "more");

		fragmentTransaction.commit();
		listArea = new ArrayList<Area>();
		listCategory = new ArrayList<Category>();
		listAreaName = new ArrayList<String>();
		listCategoryName = new ArrayList<String>();
		listCategoryP = new ArrayList<Category>();
		listCategoryNameP = new ArrayList<String>();
		listChildCategoryP = new ArrayList<List<Category>>();
		listChildCategory = new ArrayList<List<Category>>();
		mContext = this;
		initChangeEvent();

		MainActivity.getAreaList(MainActivity.this, application.cityid);
		MainActivity.getCategoryData(MainActivity.this, "0");
		MainActivity.getCategoryDataP(MainActivity.this, "1");

		textVSearch = (TextView) findViewById(R.id.textViewSearchType);
		textVSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int y = textVSearch.getBottom() * 3 / 2;
				int x = textVSearch.getRight() + 10;
				showPopupWindow(x, y);
			}
		});

		this.findViewById(R.id.imageViewSaoyisao).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MainActivity.this,
								CaptureActivity.class));
					}
				});
		getCityNameByLoc();
		checkUpdate();
		// myThread.start();

	}

	@Override
	public void onResume() {
		super.onResume();
		editRearch.setText("");
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				showToast("再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());

				// finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void initOtherFragment() {

		fragmentTransaction = fragmentManager.beginTransaction();
		newFragmentNearMap = new FragmentNearMap();
		fragmentTransaction.add(R.id.fragmentViewNear, newFragmentNearMap,
				"near");
		fragmentTransaction.commit();

	}

	protected void initView() {
		DisplayUtil displayUtil = DisplayUtil.getInstance(this);

		/**
		 * 获取当前屏幕的像素值
		 */
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		int high = metric.heightPixels / 6;
		displayUtil.setViewLayoutParamsR(
				this.findViewById(R.id.titileLinearLayout), 0, high / 2);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.titileLinearLayout),
		// 0, 150);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.LinearLayoutAllXX),0,
		// 5*high);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.main_radio),
		// 0,high / 2);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.main_radio),
		// 0,160);
		textViewArea = (TextView) this.findViewById(R.id.textViewarea);
		textViewArea.setText(application.cityName);
		textViewArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SelectAreaActivity.class);
				startActivityForResult(intent, 10000);
			}

		});
		editRearch = (EditText) this.findViewById(R.id.et_searchtext_search);
		editRearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(MainActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					Intent in;
					if (textVSearch.getText().toString().equals("商家")) {
						in = new Intent(MainActivity.this,
								PopBusinessListActivity.class);
						in.putExtra("Type", editRearch.getText().toString());
						in.putExtra("TypeId", editRearch.getText().toString());
						startActivity(in);
					} else {
						in = new Intent(MainActivity.this,
								PopPersonListActivity.class);
						in.putExtra("Type", editRearch.getText().toString());
						in.putExtra("TypeId", editRearch.getText().toString());
						startActivity(in);
					}
				}
				return false;
			}

		});

	}

	protected void onDestroy() {
		super.onDestroy();
		if (mBMapMan != null) {
			mBMapMan.stop();
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}

	void initChangeEvent() {
		((RadioButton) this.findViewById(R.id.radio_buttonHome))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonNear))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonUser))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonMore))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonHome))
				.setChecked(true);
		isFirst = true;

	}

	void goneTitileView() {
		this.findViewById(R.id.fragmentViewHome).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewNear).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewUser).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewMore).setVisibility(View.GONE);
		this.findViewById(R.id.homePageTitileLinearLayou).setVisibility(
				View.GONE);
		this.findViewById(R.id.mapTitileLinearLayou).setVisibility(View.GONE);
		this.findViewById(R.id.userTitileLinearLayout).setVisibility(View.GONE);
		this.findViewById(R.id.moreTitileLinearLayout).setVisibility(View.GONE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_buttonHome: {
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(true);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewHome).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.homePageTitileLinearLayou)
						.setVisibility(View.VISIBLE);

				// Log.e("xxxx", "-----------+1");
				break;
			}
			case R.id.radio_buttonNear: {
				if (isFirst) {
					initOtherFragment();
					isFirst = false;
				}
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(true);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewNear).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.mapTitileLinearLayou).setVisibility(
						View.VISIBLE);

				// Log.e("xxxx", "-----------+2");
				break;
			}
			case R.id.radio_buttonUser: {
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(true);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewUser).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.userTitileLinearLayout).setVisibility(
						View.VISIBLE);

				// Log.e("xxxx", "-----------+3");
				break;
			}
			case R.id.radio_buttonMore: {
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(true);
				this.findViewById(R.id.fragmentViewMore).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.moreTitileLinearLayout).setVisibility(
						View.VISIBLE);

				// Log.e("xxxx", "-----------+4");
				break;
			}
			default:
				break;
			}
		}

	}

	/**
	 * 获取市级下属的区县
	 * 
	 * @param context
	 * @param id
	 *            ---城市id
	 */
	public static void getAreaList(Context context, String id) {
		AreaDao areaDao = new AreaDao(context);
		listArea.clear();
		areaDao.startReadableDatabase(false);
		List<Area> list = areaDao.rawQuery(
				"select * from area where Parent = ?", new String[] { id },
				Area.class);
		areaDao.closeDatabase(false);
		if (list != null) {
			listArea.addAll(list);
			listAreaName.clear();
			for (Area a : listArea) {
				listAreaName.add(a.getName());
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param id
	 *            主分类id，为0时获取第一级分类
	 * @param type
	 *            0 为商家 1为个人
	 */
	public static void getCategoryData(Context context, String id) {
		listCategory.clear();
		CategoryDao categoryDao = new CategoryDao(context);
		categoryDao.startReadableDatabase(false);
		// List<Category> list =
		// categoryDao.rawQuery("select * from category where Type = ?", new
		// String[]{"0"}, Category.class);
		List<Category> list = categoryDao.queryList("Type = ?",
				new String[] { "0" });
		categoryDao.closeDatabase(false);
		if (list != null) {
			Log.e("company", "-----" + list.size());
			listCategoryName.clear();
			listChildCategory.clear();
			for (Category c : list) {
				// 如果是二级分类
				if (c.getParent().equals("0")) {
					// 加入二级分类表
					listCategory.add(c);
					listCategoryName.add(c.getName());
					// 获取子项
					List<Category> listc = new ArrayList<Category>();
					for (Category ca : list) {
						if (ca.getParent().equals(c.getId())) {
							listc.add(ca);
						}
					}
					listChildCategory.add(listc);
				}
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param id
	 *            主分类id，为0时获取第一级分类
	 * @param type
	 *            0 为商家 1为个人
	 */
	public static void getCategoryDataP(Context context, String id) {
		listCategoryP.clear();
		CategoryDao categoryDao = new CategoryDao(context);
		categoryDao.startReadableDatabase(false);
		// List<Category> list =
		// categoryDao.rawQuery("select * from category where Type =?", new
		// String[]{"1"}, Category.class);
		List<Category> list = categoryDao.queryList("Type = ?",
				new String[] { "1" });
		categoryDao.closeDatabase(false);
		if (list != null) {
			Log.e("person", "-----" + list.size());
			listCategoryNameP.clear();
			listChildCategoryP.clear();
			for (Category c : list) {
				// 如果是二级分类
				if (c.getParent().equals("0")) {
					// 加入二级分类表
					listCategoryP.add(c);
					listCategoryNameP.add(c.getName());
					// 获取子项
					List<Category> listc = new ArrayList<Category>();
					for (Category ca : list) {
						if (ca.getParent().equals(c.getId())) {
							listc.add(ca);
						}
					}
					listChildCategoryP.add(listc);
					Log.e("person", "-----" + listc.size());
				}
			}
		}

	}

	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(
				R.layout.popdialog, null);
		listView = (ListView) layout.findViewById(R.id.listViewPopW);
		listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.text_itemselect, R.id.textViewSelectItemName, types));

		popupWindow = new PopupWindow(MainActivity.this);

		popupWindow.setBackgroundDrawable(new ColorDrawable(
				android.R.color.transparent));
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 3);
		// popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		popupWindow.showAsDropDown(textVSearch, x + 5, 10);

		// popupWindow.showAtLocation(findViewById(R.id.LinearLayoutpopwindows),
		// Gravity.LEFT
		// | Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				textVSearch.setText(types[arg2]);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	private void checkUpdate() {
		new VersionBll().getVersion(this,
				new ZzObjectHttpResponseListener<Version>() {

					@Override
					public void onSuccess(int statusCode, List<Version> lis) {
						Version version = lis.get(0);
						if (version == null)
							return;

						final Version tempVersion = version;
						if (version != null
								&& !AbStrUtil.isEmpty(version.getApp_url())
								&& version.getBuild_number() > getVersion()) {
							MainActivity.this.showDialog(
									"发现新版本" + version.getVersion(),
									"是否更新？\r\n"
											+ version.getVersion_description(),
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											downApk(tempVersion);
										}
									});
						}
					}

					@Override
					public void onStart() {
						// MainActivity.this.showProgressDialog("请稍候...");
					}

					@Override
					public void onFinish() {
						// MainActivity.this.removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Version> localList) {
						// MainActivity.this.showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
					}
				});
	}

	private int getVersion() {
		PackageManager packageManager = MainActivity.this.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(
					MainActivity.this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// String version = "code" + packInfo.versionCode +
		// packInfo.versionName;
		return packInfo.versionCode;
	}

	private void downApk(Version version) {
		String serviceString = Context.DOWNLOAD_SERVICE;
		final DownloadManager downloadManager = (DownloadManager) MainActivity.this
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
		MainActivity.this.registerReceiver(receiver, filter);
	}

	private void installApk(Uri uri) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
		intent.setData(uri);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 导航初始化
	 */
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			mIsEngineInitSuccess = true;
			// Toast.makeText(MainActivity.this, "初始化导航成功",
			// Toast.LENGTH_LONG).show();
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
			// Toast.makeText(MainActivity.this, "初始化导航失败",
			// Toast.LENGTH_LONG).show();
		}
	};

	private BNKeyVerifyListener mKeyVerifyListener = new BNKeyVerifyListener() {

		@Override
		public void onVerifySucc() {
			// TODO Auto-generated method stub
			// Toast.makeText(MainActivity.this, "  ",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onVerifyFailed(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(MainActivity.this, "校验失败，无法提供导航功能！",
					Toast.LENGTH_LONG).show();
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private String getCityNameByLoc() {
		MyLocationListener myListener = new MyLocationListener();
		mLocationClient = new LocationClient(MainActivity.this); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();// 设置定位参数
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		return null;

	}

	// 民生网点的定位牵涉到定位后选择范围，所以定位之后会根据所选择的范围来显示覆盖物（定位接口）
	class MyLocationListener implements BDLocationListener {
		@Override
		// 定位获取经纬度
		public void onReceiveLocation(BDLocation location) {
			Log.e("xxxx", "-------------------开始定位");
			if (location == null)
				return;

			mCityName = location.getCity();
			if (mCityName == null) {
				mLocationClient.stop();
				return;
			}
			String showCityName = mCityName.substring(0, 2);
			if (showCityName.equals(textViewArea.getText().toString()
					.substring(0, 2))) {
				mLocationClient.stop();
				return;
			}
			Log.e("fragmentmap", "-------------------所在城市：" + showCityName);
			if (mCityName != null) {
				mLocationClient.stop();
				MainActivity.this.showDialog("位置提醒", "当前定位到您所在的城市是" + mCityName
						+ ",是否切换城市？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						showToast("正在切换……");
						application.cityName = mCityName;
						new AreaBll().getAreaIdByAreaName(MainActivity.this,
								mCityName.substring(0, 2),
								new ZzStringHttpResponseListener() {

									@Override
									public void onSuccess(int statusCode,
											String content) {
										// TODO Auto-generated method stub
										if (content == null)
											return;
										application.cityid = content;
										textViewArea.setText(mCityName
												.substring(0, 2));
										Editor editor = abSharedPreferences
												.edit();
										editor.putString(Constant.CITYID,
												content);
										editor.putString(Constant.CITYNAME,
												mCityName.substring(0, 2));
										editor.commit();
										newFragmentHome.getData();

									}

									@Override
									public void onStart() {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFailure(int statusCode,
											String content, Throwable error) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onErrorData(
											String status_description) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFinish() {
										// TODO Auto-generated method stub

									}

								});
					}

				});
			}

		}

		// 获得搜索点
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

}
