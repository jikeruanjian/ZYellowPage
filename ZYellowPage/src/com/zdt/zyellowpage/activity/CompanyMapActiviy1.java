package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.util.common.CoordinateTransformUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.CompanyMapActiviy.OverlayTest;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.NearCompanyReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class CompanyMapActiviy1 extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	String userCompanyFullName;
	String latitude;
	String Longitude;// 地图显示
		BMapManager mBMapManC = null;
	MapView mMapView = null;
	MapController mMapController = null;

	private List<User> newList = null;
	// 定位相关
	Button btnLoc;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener;
	LocationData locData = null;// 定位出的位置信息
	MyLocationOverlay myLocationOverlay = null;// 定位图层
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;
	GraphicsOverlay graphicsOverlay;// 范围覆盖
	private String mCityName = null;
	// 兴趣点相关
	MKSearch mMKSearch = null;
	public View popView;// 点击兴趣点弹出泡泡
	ImageView popImage;// 泡泡上的图片
	TextView PioName;// 显示兴趣点的名称
	TextView closePop;
	TextView contenPop;
	int poiDistance = 3000;// 兴趣点搜索范围
	GeoPoint poiPoint = null;// 兴趣点经纬度
	List<User> nearsearch;
	List<User> tempUser;
	MyPoiOverlayX poiOverlayx = null;// 自定义
	String userMumber = null;
	
	//驾车线路 
	MKPlanNode start = null;//起始点
	MKPlanNode end = null;//结束点
	
	RouteOverlay routeOverlay = null;//驾车路线覆盖物
	TransitOverlay TransitrouteOverlay=null;//公交线路覆盖物

	//上车下车点提示
	TextOverlay busStationOverlay = null;
	ExpandableListView expandableListView = null;
	//PlanGroupAdapter  mAdapter = null;
	MKTransitRouteResult TransitRouteInfo  = null;
	Button btnShowPlan;
	boolean expandVisable = true;
	TextView textViewDes = null;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 10000) {
			if (resultCode == RESULT_OK) {
				if (intent.getExtras() != null) {
				String	typeid = intent.getExtras().get("TypeId").toString();
				if(typeid.equalsIgnoreCase("all")){
					this.getNearCompanyData(20,3000);
				}
				else{
					this.getNearCompanyDataByType(typeid);
				}
				}
			}
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		// 地图RjlfVWfEcAecRGc5qG8xyLoX\E0vBVTjgP3IvuuZSOSqKmsZu
		mBMapManC = new BMapManager(getApplication());
		// E25ED402F8E85C1714F86CC9042EA1B32BE151B2
		mBMapManC.init(getResources().getString(R.string.BaiDuMap_Key), null);
		setAbContentView(R.layout.fragment_nearmap);
		if (getIntent().getExtras() != null) {
			userCompanyFullName = (String) getIntent().getExtras().get("FUllNAME");
			latitude = (String) getIntent().getExtras().get("LAT");
			Longitude = (String) getIntent().getExtras().get("LON");
			poiPoint = new GeoPoint((int) (Double.valueOf(latitude) * 1e6),
					(int) (Double.valueOf(Longitude) * 1e6));
		}
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(userCompanyFullName);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		//mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		mAbTitleBar.clearRightView();
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.maptypelist); 
		
		imageView.setPadding(0, 0, 20, 0);
		imageView.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivityForResult(new Intent(CompanyMapActiviy1.this,
								CompanytypeActivity.class), 10000);
					}
				});
		mAbTitleBar.addRightView(imageView);
		
		mMapView = (MapView) this.findViewById(R.id.bmapsView);
		mCityName = application.locateCityName;
		this.findViewById(R.id.Layout_PioAllLife).getBackground().setAlpha(150);
		
		myListener = new MyLocationListener();
		
		mMapView = (MapView) this.findViewById(R.id.bmapsView);
		mCityName = ((MyApplication)this.abApplication).locateCityName;
		initPoiDistantsBtn();
		initMapView();
		initMapLoc();
		initMKSearch();
		newList = new ArrayList<User>();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	void initPoiDistantsBtn() {
		this.findViewById(R.id.Layout_PioAllLife).getBackground().setAlpha(150);
		this.findViewById(R.id.btn_PioAllDistance).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						poiDistanceOnClick(v);
					}

				});
		this.findViewById(R.id.btn_Pio500).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});
		this.findViewById(R.id.btn_Pio1000).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});
		this.findViewById(R.id.btn_Pio1500).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});

		this.findViewById(R.id.btnLocStartNear).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mLocationClient.start();
						isRequest = true;
						mLocationClient.requestLocation();
						Toast.makeText(CompanyMapActiviy1.this, "正在定位…",
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	/**
	 * 初始化地图显示
	 */
	void initMapView() {
		mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		mMapController = mMapView.getController();
		OverlayTest itemOverlay = new OverlayTest(getResources()
				.getDrawable(R.drawable.companymap_mark_min), mMapView);
		itemOverlay.addItem(new OverlayItem(poiPoint,userCompanyFullName,userCompanyFullName));
		mMapView.getOverlays().add(itemOverlay);
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(poiPoint);// 设置地图中心点
		mMapView.getController().enableClick(true);
		mMapController.setZoom(14);// 设置地图zoom级别
	}

	/**
	 * 初始化定位功能
	 */
	void initMapLoc() {

		locData = new LocationData();
		// 设置定位数据
		locData.latitude = 25.031753;
		locData.longitude = 102.732342;
		locData.direction = 2.0f;

		mLocationClient = new LocationClient(CompanyMapActiviy1.this); // 声明LocationClient类
		graphicsOverlay = new GraphicsOverlay(mMapView);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		LocationClientOption option = new LocationClientOption();// 设置定位参数
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		/*
		 * option.setPoiNumber(5); //最多返回POI个数 option.setPoiDistance(1000);
		 * //poi查询距离 option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
		 */
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		myLocationOverlay.enableCompass();
		// getNearCompanyData();

	}

	/**
	 * 附近网点选择范围按钮组
	 * 
	 * @param view
	 */
	public void poiDistanceOnClick(View v) {
		this.findViewById(R.id.btn_PioAllDistance).setBackgroundColor(
				getResources().getColor(R.color.transparent));// 用于改变选中的背景颜色
		this.findViewById(R.id.btn_Pio500).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		this.findViewById(R.id.btn_Pio1000).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		this.findViewById(R.id.btn_Pio1500).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		switch (v.getId()) {
		case R.id.btn_PioAllDistance: {
			poiDistance = 3000; // 6km范围内
			this.findViewById(R.id.btn_PioAllDistance).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio500: {
			poiDistance = 500;// 1km范围内
			this.findViewById(R.id.btn_Pio500).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio1000: {
			poiDistance = 1000;// 3km范围内
			this.findViewById(R.id.btn_Pio1000).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio1500: {
			poiDistance = 1500; // 6km范围内
			this.findViewById(R.id.btn_Pio1500).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		default:
			break;
		}
		getNearCompanyData(20,poiDistance);
	}

	// 民生网点的定位牵涉到定位后选择范围，所以定位之后会根据所选择的范围来显示覆盖物（定位接口）
	class MyLocationListener implements BDLocationListener {
		@Override
		// 定位获取经纬度
		public void onReceiveLocation(BDLocation location) {
			Log.e("xxxx", "-------------------开始定位");
			if (location == null)
				return;

			// -------虚拟机测试时注释
			
			  locData.latitude = location.getLatitude(); 
			//如果不显示定位精度圈，将accuracy赋值为0即可
			  locData.longitude = location.getLongitude(); 
			  locData.accuracy = location.getRadius(); 
			  locData.direction = location.getDerect();
			  Log.e("fragmentmap", "-------------------所在城市："+location.getCity());  
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// initPioData(1);
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				mMapView.getOverlays().remove(myLocationOverlay);
				myLocationOverlay.setData(locData);
				mMapView.getOverlays().add(myLocationOverlay);
				myLocationOverlay.enableCompass();
				//mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6),(int) (locData.longitude * 1e6)));
				popView.setVisibility(View.GONE);
				// 将搜索附近点放到定位里，定位根据现有经纬度和选择的范围值进行检索兴趣点，以下是百度接口
				// mMKSearch.poiSearchNearBy("民生银行",new
				// GeoPoint((int)(locData.latitude* 1e6),
				// (int)(locData.longitude * 1e6)), poiDistance);
				mMapView.refresh();
				isRequest = false;
				mLocationClient.stop();
			}
			// 首次定位完成
			getNearCompanyData(20,3000);
			isFirstLoc = false;
		}

		// 获得搜索点
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	/**
	 * 从接口获取信息
	 */

	void getNearCompanyData(int max,int distance) {
		UserBll bll = new UserBll();

		NearCompanyReqEntity nearCompanyReqEntity = new NearCompanyReqEntity(
				max,  Double.valueOf(latitude),Double.valueOf(Longitude), distance);
		Log.e("xxxx", "-------" + locData.latitude + locData.longitude);
		bll.getNearCompany(this, nearCompanyReqEntity,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						
						if (lis == null || lis.size() == 0) {
							return;
						}
						newList.clear();
						Log.e("xxxx", "------" + lis.size());
						for (int i = 0; i < lis.size(); i++) {
							newList.add(lis.get(i));
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						
						CompanyMapActiviy1.this.removeProgressDialog();
						popView.setVisibility(View.GONE);
						CleanRouteOverlay();
						mMapView.getOverlays().remove(poiOverlayx);
						poiOverlayx = new MyPoiOverlayX(getResources()
								.getDrawable(R.drawable.icon_marka), mMapView,
								newList);
						mMapView.getOverlays().add(poiOverlayx);
						mMapView.refresh();
					}
			
				});

	}

	
	/**
	 * 从接口获取信息
	 */

	public void getNearCompanyDataByType(String typeid) {
		UserBll bll = new UserBll();
		NearCompanyReqEntity nearCompanyReqEntity = new NearCompanyReqEntity(
				20, Double.valueOf(latitude),Double.valueOf(Longitude), poiDistance,typeid);
		Log.e("xxxx", "-------" + locData.latitude + locData.longitude);
		bll.getNearCompany(CompanyMapActiviy1.this, nearCompanyReqEntity,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						
						if (lis == null || lis.size() == 0) {
							return;
						}
						newList.clear();
						Log.e("xxxx", "------" + lis.size());
						for (int i = 0; i < lis.size(); i++) {
							newList.add(lis.get(i));
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						CompanyMapActiviy1.this.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						
						CompanyMapActiviy1.this.removeProgressDialog();
						popView.setVisibility(View.GONE);
						CleanRouteOverlay();
						mMapView.getOverlays().remove(poiOverlayx);
						poiOverlayx = new MyPoiOverlayX(getResources()
								.getDrawable(R.drawable.icon_marka), mMapView,
								newList);
						mMapView.getOverlays().add(poiOverlayx);
						mMapView.refresh();
					}
			
				});

	}
	
	/**
	 * 范围内搜索显示
	 */
	@SuppressLint("NewApi")
	void initMKSearch() {
		CompanyMapActiviy1.this.findViewById(R.id.btn_PioAllDistance).setBackgroundColor(
				Color.rgb(255, 127, 0));
		mMKSearch = new MKSearch();  
    	mMKSearch.init(MainActivity.mBMapMan, new  MapMySearchListener());
		// 泡泡初始化
		popView = CompanyMapActiviy1.this.getLayoutInflater().inflate(
				R.layout.overlay_pop, null);
		popImage = (ImageView) popView.findViewById(R.id.imageViewPop);
		//关闭泡泡
		closePop = (TextView) popView.findViewById(R.id.textViewClosePop);
		closePop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popView.getVisibility() == View.VISIBLE) {
					popView.setVisibility(View.GONE);
				}
			}

		});
		//导航
		popView.findViewById(R.id.mapPopTextCotent).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchNavigator();
		//点击公交
				/*公交
				 * start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
				MKPlanNode end = new MKPlanNode();
				if(poiPoint != null){
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);  
				mMKSearch.transitSearch("昆明", start, end);
			}
				//驾车
				start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
				MKPlanNode end = new MKPlanNode();
				if(poiPoint != null){
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);  
				mMKSearch.drivingSearch("昆明", start, "昆明", end);  
				}
				else{
					Toast.makeText(mActivity, "请选择目的地！",Toast.LENGTH_LONG).show();  
				}
				//步行
				start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
				MKPlanNode end = new MKPlanNode();
				if(poiPoint != null){
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);  
				mMKSearch.walkingSearch("昆明", start, "昆明", end);  
				}
				else{
					Toast.makeText(mActivity, "请选择目的地！",Toast.LENGTH_LONG).show();  
				}
				*/
			}

		});
		//查看详细
		popImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popView.getVisibility() == View.VISIBLE) {
					popView.setVisibility(View.GONE);
				}
				CleanRouteOverlay();
				if(userMumber != null){
					Intent intent = new Intent(CompanyMapActiviy1.this,
							BusinessDetailActivity.class);
					intent.putExtra("MEMBER_ID",  userMumber);
					startActivity(intent);
				}
			}

	});
		
		//步行
		popView.findViewById(R.id.mapPopTextWalk).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("FragementNearMap", "-----步行");
				CleanRouteOverlay();
				if(poiPoint != null){
					start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
					MKPlanNode end = new MKPlanNode();
					if(poiPoint != null){
					end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
					mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST); 
					mMKSearch.walkingSearch(mCityName, start,mCityName, end);
					//mMKSearch.walkingSearch("昆明", start, "昆明", end);  
					}
					else{
						Toast.makeText(CompanyMapActiviy1.this, "请选择目的地！",Toast.LENGTH_LONG).show();  
					}
				}
			}
		});
		
		//公交
		popView.findViewById(R.id.mapPopTextBus).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("FragementNearMap", "-----公交");
				CleanRouteOverlay();
				if(poiPoint != null){
					 start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
					MKPlanNode end = new MKPlanNode();
					if(poiPoint != null){
					end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
					mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);  
					mMKSearch.transitSearch(mCityName, start, end);
					//mMKSearch.transitSearch("昆明", start, end);
					}
					else{
						Toast.makeText(CompanyMapActiviy1.this, "请选择目的地！",Toast.LENGTH_LONG).show();  
					}
				}
			}
		});
		
		//驾车
		popView.findViewById(R.id.mapPopTextDrive).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.i("FragementNearMap", "-----驾车");
						CleanRouteOverlay();
						if(poiPoint != null){
							start.pt = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));  
							MKPlanNode end = new MKPlanNode();
							if(poiPoint != null){
							end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短  
							mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);  
							mMKSearch.drivingSearch( mCityName,start,  mCityName, end);
							//mMKSearch.walkingSearch("昆明", start, "昆明", end);  
							}
							else{
								Toast.makeText(CompanyMapActiviy1.this, "请选择目的地！",Toast.LENGTH_LONG).show();  
							}
						}
					}
				});
			
		popView.getBackground().setAlpha(200);
		mMapView.addView(popView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		// 由于我的气泡的尾巴是在下边居中的,因此要设置成MapView.LayoutParams.BOTTOM_CENTER.
		// 这里没有给GeoPoint,在onFocusChangeListener中设置
		popView.setVisibility(View.GONE);// 隐藏泡泡
		
		
		//初始化驾车路线覆盖物
    	routeOverlay = new RouteOverlay(CompanyMapActiviy1.this, mMapView);
    	TransitRouteInfo = new MKTransitRouteResult();
    	//btnShowPlan =  (Button)this.findViewById(R.id.btnShowBusPlan);
    	//btnShowPlan.getBackground().setAlpha(200);
    	//初始化公交线路覆盖物
    	TransitrouteOverlay  = new TransitOverlay (CompanyMapActiviy1.this, mMapView);
    	start =  new MKPlanNode(); 
    	end = new MKPlanNode();
    	busStationOverlay = new TextOverlay(mMapView);
	}

	/**
	 * 来自数据库的自定义兴趣点覆盖物
	 */
	@SuppressWarnings("rawtypes")
	class MyPoiOverlayX extends ItemizedOverlay {
		List<User> mSearch;
		public MyPoiOverlayX(Drawable defaultMarker, MapView mapView,
				List<User> search) {
			super(defaultMarker, mapView);
			Log.e("xxx", "数据库资源有-------" + search.size());
			mSearch = new ArrayList<User>();
			mSearch.addAll(search);
			for (int i = 0; i < mSearch.size(); i++) {
				GeoPoint p = new GeoPoint((int) (Double.valueOf(mSearch.get(i)
						.getLatitude()) * 1e6), (int) (Double.valueOf(mSearch
						.get(i).getLongitude()) * 1e6));
				this.addItem(new OverlayItem(p, mSearch.get(i).getFullname(),
						""));
			}
		}

		//点击后将地图中心移动至点击点，并显示泡泡
		@Override
		protected boolean onTap(int i) {
			// super.onTap(i);
			CleanRouteOverlay();
			User info = mSearch.get(i);
			//初始化泡泡
			popView.setVisibility(View.GONE);
			if (info.getLogo() != null) {
				new AbImageDownloader(CompanyMapActiviy1.this).display(popImage,
						info.getLogo());
			}
			//popImage.setImageResource(R.drawable.ic_launcher);
			MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
					.getLayoutParams();
			geoLP.point = new GeoPoint(
					(int) (Double.valueOf(info.getLatitude()) * 1e6),
					(int) (Double.valueOf(info.getLongitude()) * 1e6));// 这行用于popView的定位
			userMumber = info.getMember_id();
			mMapController.animateTo(geoLP.point);
			TextView title = (TextView) popView
					.findViewById(R.id.map_bubbleTitle);
			poiPoint = geoLP.point;
			title.setText(info.getFullname());
			/*TextView desc = (TextView) popView
					.findViewById(R.id.map_bubbleText);
			if (info.getAddress() == null) {
				desc.setVisibility(View.GONE);
			} else {
				desc.setVisibility(View.VISIBLE);
				desc.setText(info.getAddress());
			}*/
			TextView phone = (TextView) popView
					.findViewById(R.id.map_bubblePhone);
			phone.setText(info.getAddress());
			
			
			mMapView.updateViewLayout(popView, geoLP);
			popView.setVisibility(View.VISIBLE);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			return false;
		}

		
	}
	
	private class MapDetailListener implements OnClickListener{  
        String mMemberId;  
        public MapDetailListener(String memberId){  
        	mMemberId= memberId;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub   
            //Toast.makeText(mContext, mPosition+"", Toast.LENGTH_SHORT).show(); 
            Intent intent = new Intent(CompanyMapActiviy1.this,
					 BusinessDetailActivity.class);
			 intent.putExtra("MEMBER_ID", mMemberId);
			 CompanyMapActiviy1.this.startActivity(intent);
        }  
          
    } 
	
	
	
	public class MapMySearchListener implements MKSearchListener {

    	@Override
    	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
    		// TODO Auto-generated method stub
    		
    	}

    	/**
    	 * 驾车路线搜索
    	 */
    	@Override
    	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
    		// TODO Auto-generated method stub
        if (result == null) {  
                return;  
        }  
          //重新初始化导航线路 
        routeOverlay = null;
        routeOverlay = new RouteOverlay(CompanyMapActiviy1.this, mMapView);
        routeOverlay.setData(result.getPlan(0).getRoute(0));  
        mMapView.getOverlays().add(routeOverlay);  
        if(popView.getVisibility() == View.VISIBLE){
        	  popView.setVisibility(View.GONE);
        	}
        mMapView.refresh();  
    	}

    	@Override
    	public void onGetPoiDetailSearchResult(int arg0, int arg1) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	/**
    	 * 百度兴趣点搜索，在使用百度数据的时候才调用
    	 */
    	public void onGetPoiResult(MKPoiResult res, int type, int error) {    
    		
    	}

    	@Override
    	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	/**
    	 * 公交路线 
    	 */
    	public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(CompanyMapActiviy1.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				TransitRouteInfo = res;
				//此处生成所有方案的信息加入list
				String[] strPlans = new String[res.getNumPlan()];
				String[][] strPlanConents = new String[res.getNumPlan()][];
				for(int i = 0 ;i<res.getNumPlan();i++){
					//循环获取方案
					MKTransitRoutePlan planinfo = new MKTransitRoutePlan();
					planinfo = res.getPlan(i);
					//将方案名称加入方案数组
					strPlans[i]=planinfo.getContent();
					//将方案详细内容加入对应的详细数组
					strPlanConents[i] = new String[planinfo.getNumLines()];
					//循环获取方案的乘车线路
					for(int j = 0;j<planinfo.getNumLines();j++){
						strPlanConents[i][j]= planinfo.getLine(j).getTitle()+planinfo.getLine(j).getGetOnStop().name+"上车---"+
								planinfo.getLine(j).getGetOffStop().name + "下车";
					}
				}
				//将数据加入expandablelistview
			/*	mAdapter = 
						new PlanGroupAdapter (LifeCmbcBMapActivity.this ,strPlans , strPlanConents);
			    expandableListView.setAdapter(mAdapter);
			    textViewDes.setText("目的地:"+PioName.getText().toString());
			    LifeCmbcBMapActivity.this.findViewById(R.id.layoutLines).setVisibility(View.VISIBLE);
			    LifeCmbcBMapActivity.this.findViewById(R.id.layoutViewPlans).setVisibility(View.VISIBLE);
			    //expandableListView.setVisibility(View.VISIBLE);*/
			    DrawTransitRoute(0);	  
    	}
    	
    	@Override
    	public void onGetWalkingRouteResult(MKWalkingRouteResult result, int arg1) {
    		// TODO Auto-generated method stub
    		 if (result == null) {  
                 return;  
         }  
           //重新初始化导航线路 
         routeOverlay = null;
         routeOverlay = new RouteOverlay(CompanyMapActiviy1.this, mMapView);
         routeOverlay.setData(result.getPlan(0).getRoute(0));  
         mMapView.getOverlays().add(routeOverlay);  
         if(popView.getVisibility() == View.VISIBLE){
         	  popView.setVisibility(View.GONE);
         	}
         mMapView.refresh();  
    	}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}        
       
		
		
		
		
}
/**
 * 清除驾车、公交线路覆盖物
 */
void CleanRouteOverlay(){
	if(routeOverlay != null && routeOverlay.size() > 0){
    	mMapView.getOverlays().remove(routeOverlay);
    	//routeOverlay=null;
    	mMapView.refresh();
    }
    if(TransitrouteOverlay != null && TransitrouteOverlay.size() > 0){
    	mMapView.getOverlays().remove(TransitrouteOverlay);
    	busStationOverlay.removeAll();
    	mMapView.getOverlays().remove(busStationOverlay);
    	//routeOverlay=null;
    	mMapView.refresh();
    }
}

/**
 * 在地图上画出第i条公交线方案
 * @param 
 */
public void DrawTransitRoute( int n ){
	CleanRouteOverlay();
	MKTransitRoutePlan busInfo = new MKTransitRoutePlan();
	busInfo = 	TransitRouteInfo.getPlan(n);
    TransitrouteOverlay = null;
	TransitrouteOverlay  = new TransitOverlay (CompanyMapActiviy1.this, mMapView);
    // 此处仅展示一个方案作为示例
	TransitrouteOverlay.setData(busInfo);
	//添加路线图层
    mMapView.getOverlays().add(TransitrouteOverlay);
    if(popView.getVisibility() == View.VISIBLE){
  	  popView.setVisibility(View.GONE);
  	}
	int len = busInfo.getNumLines();
	Symbol symbol = new Symbol();
	Symbol.Color fontColor = symbol.new Color();
	//设置文字着色
	fontColor.alpha = 255;
	fontColor.red = 255;
	fontColor.green = 0;
	fontColor.blue  = 0;
	Symbol.Color bgColor = symbol.new Color();
	//设置文字背景色
	bgColor.red = 0;
	bgColor.blue = 0;
	bgColor.green = 255;
	bgColor.alpha = 150;
	for(int i = 0;i<len;i++){
		TextItem textItemStart = new TextItem();
		TextItem textItemEnd = new TextItem();
		textItemStart.pt=busInfo.getLine(i).getGetOnStop().pt;
		textItemStart.fontSize = 15;
		textItemStart.bgColor = bgColor;
		textItemStart.text = busInfo.getLine(i).getTitle()+":"+busInfo.getLine(i).getGetOnStop().name+"上车";
		textItemStart.align = TextItem.ALIGN_BOTTOM;
		textItemStart.fontColor = fontColor;
		Log.e("xxx", busInfo.getLine(i).getGetOnStop().pt.getLatitudeE6()+"");
		
		textItemEnd.pt = busInfo.getLine(i).getGetOffStop().pt;
		textItemEnd.fontSize =15;
		textItemEnd.bgColor = bgColor;
		textItemEnd.text=busInfo.getLine(i).getGetOffStop().name+"下车";
		textItemEnd.align  = TextItem.ALIGN_TOP;
		textItemEnd.fontColor = fontColor;
		
		busStationOverlay.addText(textItemStart);
		busStationOverlay.addText(textItemEnd);
		/*str.append(info.getLine(i).getTitle()+":"+info.getLine(i).getGetOnStop().name+"上车----"
		+info.getLine(i).getGetOffStop().name+"下车");
		str.append("\n");*/
		
	}
	mMapView.getOverlays().add(busStationOverlay);
	

  //执行刷新使生效
    mMapView.refresh();
    }



/**
 * 启动GPS导航. 前置条件：导航引擎初始化成功
 */
  private void launchNavigator(){
	//这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
	  com.baidu.nplatform.comapi.basestruct.GeoPoint startP =  
			  CoordinateTransformUtil.transferBD09ToGCJ02(locData.latitude,locData.longitude);
	  com.baidu.nplatform.comapi.basestruct.GeoPoint endP = 
			  CoordinateTransformUtil.transferBD09ToGCJ02(poiPoint.getLatitudeE6()/1E6, poiPoint.getLongitudeE6()/1E6);
	BaiduNaviManager.getInstance().launchNavigator(CompanyMapActiviy1.this, 
			startP.getLatitudeE6()/1E6, startP.getLongitudeE6()/1E6,"起点", 
			endP.getLatitudeE6()/1E6, endP.getLongitudeE6()/1E6,"终点",
			//locData.latitude,locData.longitude,"起点", 
			//poiPoint.getLatitudeE6()/1E6, poiPoint.getLongitudeE6()/1E6,"终点",
			NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //算路方式
			true, 									   		 //真实导航
			BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
			new OnStartNavigationListener() {				 //跳转监听
				
				@Override
				public void onJumpToNavigator(Bundle configParams) {
					Intent intent = new Intent(CompanyMapActiviy1.this, BNavigatorActivity.class);
					intent.putExtras(configParams);
			        startActivity(intent);
				}
				
				@Override
				public void onJumpToDownloader() {
				}
			});
  }
  class OverlayTest extends ItemizedOverlay<OverlayItem> {  
	    //用MapView构造ItemizedOverlay  
	    public OverlayTest(Drawable mark,MapView mapView){  
	            super(mark,mapView);  
	    }  
	    @Override
		protected boolean onTap(int index) {  
	        //在此处理item点击事件  
	        System.out.println("item onTap: "+index);  
	        return true;  
	    }  
	        @Override
			public boolean onTap(GeoPoint pt, MapView mapView){  
	                //在此处理MapView的点击事件，当返回 true时  
	                super.onTap(pt,mapView);  
	                return false;  
	        }  
	}          
}
