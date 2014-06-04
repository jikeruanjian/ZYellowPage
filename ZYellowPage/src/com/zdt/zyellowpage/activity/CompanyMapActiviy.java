package com.zdt.zyellowpage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.MyApplication;

public class CompanyMapActiviy extends AbActivity {

	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	String userCompanyFullName;
	String latitude;
	String Longitude;
	// 地图显示
	MapView mMapView = null;
	MapController mMapController = null;

	// 定位相关
	Button btnLoc;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener;
	LocationData locData = null;// 定位出的位置信息
	MyLocationOverlay myLocationOverlay = null;// 定位图层
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;
	private String mCityName = null;
	// 兴趣点相关
	MKSearch mMKSearch = null;
	public View popView;// 点击兴趣点弹出泡泡
	ImageView popImage;// 泡泡上的图片
	TextView PioName;// 显示兴趣点的名称
	TextView closePop;
	TextView contenPop;
	int poiDistance = 0;// 兴趣点搜索范围
	GeoPoint poiPoint = null;// 兴趣点经纬度

	// 驾车线路
	MKPlanNode start = null;// 起始点
	MKPlanNode end = null;// 结束点

	RouteOverlay routeOverlay = null;// 驾车路线覆盖物
	TransitOverlay TransitrouteOverlay = null;// 公交线路覆盖物

	// 上车下车点提示
	TextOverlay busStationOverlay = null;
	ExpandableListView expandableListView = null;
	MKTransitRouteResult TransitRouteInfo = null;
	Button btnShowPlan;
	boolean expandVisable = true;
	TextView textViewDes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_companymap);
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
		mMapView = (MapView) this.findViewById(R.id.bmapsView);
		mCityName = application.locateCityName;
		initMapView();
		initMapLoc();
		initMKSearch();
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
		myListener = new MyLocationListener();
		locData = new LocationData();
		// 设置定位数据
		locData.latitude = 25.031753;
		locData.longitude = 102.732342;
		locData.direction = 2.0f;
		mLocationClient = new LocationClient(CompanyMapActiviy.this); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();// 设置定位参数
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		myLocationOverlay.enableCompass();
		
		this.findViewById(R.id.btnCompanyLocStart).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mLocationClient.start();
						isRequest = true;
						mLocationClient.requestLocation();
						Toast.makeText(CompanyMapActiviy.this, "正在定位…",
								Toast.LENGTH_SHORT).show();
					}

				});

	}

	void initMKSearch() {

		mMKSearch = new MKSearch();
		mMKSearch.init(MainActivity.mBMapMan, new MapMySearchListener());
		// 泡泡初始化

		CleanRouteOverlay();
		// 初始化驾车路线覆盖物
		routeOverlay = new RouteOverlay(CompanyMapActiviy.this, mMapView);
		TransitRouteInfo = new MKTransitRouteResult();
		// btnShowPlan = (Button)this.findViewById(R.id.btnShowBusPlan);
		// btnShowPlan.getBackground().setAlpha(200);
		// 初始化公交线路覆盖物
		TransitrouteOverlay = new TransitOverlay(CompanyMapActiviy.this,
				mMapView);
		start = new MKPlanNode();
		end = new MKPlanNode();
		busStationOverlay = new TextOverlay(mMapView);
	}
	/**
	 * 清除驾车、公交线路覆盖物
	 */
	void CleanRouteOverlay() {
		if (routeOverlay != null && routeOverlay.size() > 0) {
			mMapView.getOverlays().remove(routeOverlay);
			// routeOverlay=null;
			mMapView.refresh();
		}
		if (TransitrouteOverlay != null && TransitrouteOverlay.size() > 0) {
			mMapView.getOverlays().remove(TransitrouteOverlay);
			busStationOverlay.removeAll();
			mMapView.getOverlays().remove(busStationOverlay);
			// routeOverlay=null;
			mMapView.refresh();
		}
	}

	/**
	 * 在地图上画出第i条公交线方案
	 * 
	 * @param
	 */
	public void DrawTransitRoute(int n) {
		CleanRouteOverlay();
		MKTransitRoutePlan busInfo = new MKTransitRoutePlan();
		busInfo = TransitRouteInfo.getPlan(n);
		TransitrouteOverlay = null;
		TransitrouteOverlay = new TransitOverlay(CompanyMapActiviy.this,
				mMapView);
		// 此处仅展示一个方案作为示例
		TransitrouteOverlay.setData(busInfo);
		// 添加路线图层
		mMapView.getOverlays().add(TransitrouteOverlay);
		int len = busInfo.getNumLines();
		Symbol symbol = new Symbol();
		Symbol.Color fontColor = symbol.new Color();
		// 设置文字着色
		fontColor.alpha = 255;
		fontColor.red = 255;
		fontColor.green = 0;
		fontColor.blue = 0;
		Symbol.Color bgColor = symbol.new Color();
		// 设置文字背景色
		bgColor.red = 0;
		bgColor.blue = 0;
		bgColor.green = 255;
		bgColor.alpha = 150;
		for (int i = 0; i < len; i++) {
			TextItem textItemStart = new TextItem();
			TextItem textItemEnd = new TextItem();
			textItemStart.pt = busInfo.getLine(i).getGetOnStop().pt;
			textItemStart.fontSize = 15;
			textItemStart.bgColor = bgColor;
			textItemStart.text = busInfo.getLine(i).getTitle() + ":"
					+ busInfo.getLine(i).getGetOnStop().name + "上车";
			textItemStart.align = TextItem.ALIGN_BOTTOM;
			textItemStart.fontColor = fontColor;
			Log.e("xxx", busInfo.getLine(i).getGetOnStop().pt.getLatitudeE6()
					+ "");

			textItemEnd.pt = busInfo.getLine(i).getGetOffStop().pt;
			textItemEnd.fontSize = 15;
			textItemEnd.bgColor = bgColor;
			textItemEnd.text = busInfo.getLine(i).getGetOffStop().name + "下车";
			textItemEnd.align = TextItem.ALIGN_TOP;
			textItemEnd.fontColor = fontColor;

			busStationOverlay.addText(textItemStart);
			busStationOverlay.addText(textItemEnd);
			/*
			 * str.append(info.getLine(i).getTitle()+":"+info.getLine(i).
			 * getGetOnStop().name+"上车----"
			 * +info.getLine(i).getGetOffStop().name+"下车"); str.append("\n");
			 */

		}
		mMapView.getOverlays().add(busStationOverlay);

		// 执行刷新使生效
		mMapView.refresh();
	}

	/**
	 * 附近网点选择范围按钮组
	 * 
	 * @param view
	 */
	public void mKPlanOnClick(View v) {
		this.findViewById(R.id.btn_WalkTo).setBackgroundColor(
				getResources().getColor(R.color.transparent));// 用于改变选中的背景颜色
		this.findViewById(R.id.btn_BusTo).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		this.findViewById(R.id.btn_DriveTo).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		this.findViewById(R.id.btn_BusStation).setBackgroundColor(
				getResources().getColor(R.color.transparent));

		CleanRouteOverlay();
		switch (v.getId()) {
		case R.id.btn_WalkTo: {//步行
			this.findViewById(R.id.btn_WalkTo).setBackgroundColor(
					Color.rgb(255, 127, 0));
			start.pt = new GeoPoint((int) (locData.latitude * 1E6),
					(int) (locData.longitude * 1E6));
			MKPlanNode end = new MKPlanNode();
			if (poiPoint != null) {
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				mMKSearch.walkingSearch(mCityName, start,mCityName, end);
				//mMKSearch.walkingSearch("昆明", start, "昆明", end);
			} else {
				Toast.makeText(CompanyMapActiviy.this, "请选择目的地！",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		case R.id.btn_BusTo: {//公交

			this.findViewById(R.id.btn_BusTo).setBackgroundColor(
					Color.rgb(255, 127, 0));
			start.pt = new GeoPoint((int) (locData.latitude * 1E6),
					(int) (locData.longitude * 1E6));
			MKPlanNode end = new MKPlanNode();
			if (poiPoint != null) {
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
				mMKSearch.setTransitPolicy(MKSearch.ECAR_TIME_FIRST);
				mMKSearch.transitSearch( mCityName, start, end);
				//mMKSearch.transitSearch("昆明", start, end);
			} else {
				Toast.makeText(CompanyMapActiviy.this, "请选择目的地！",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		case R.id.btn_DriveTo: {//驾车
			this.findViewById(R.id.btn_DriveTo).setBackgroundColor(
					Color.rgb(255, 127, 0));
			start.pt = new GeoPoint((int) (locData.latitude * 1E6),
					(int) (locData.longitude * 1E6));
			MKPlanNode end = new MKPlanNode();
			if (poiPoint != null) {
				end.pt = poiPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
				mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				mMKSearch.drivingSearch( mCityName,start,  mCityName, end);
				//mMKSearch.drivingSearch("昆明", start, "昆明", end);
			} else {
				Toast.makeText(CompanyMapActiviy.this, "请选择目的地！",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		case R.id.btn_BusStation: {//附近公交
			this.findViewById(R.id.btn_BusStation).setBackgroundColor(
					Color.rgb(255, 127, 0));
			if (poiPoint != null) {
			launchNavigator();
			}
			break;
		}
		default:
			break;
		}

	}
	/**
	 * 启动GPS导航. 前置条件：导航引擎初始化成功
	 */
	private void launchNavigator(){
		//这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
		BaiduNaviManager.getInstance().launchNavigator(this, 
				locData.latitude,locData.longitude,"起点", 
				Double.valueOf(latitude), Double.valueOf(Longitude),userCompanyFullName,
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //算路方式
				true, 									   		 //真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
				new OnStartNavigationListener() {				 //跳转监听
					
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(CompanyMapActiviy.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
				        startActivity(intent);
					}
					
					@Override
					public void onJumpToDownloader() {
					}
				});
	}
	class MyLocationListener implements BDLocationListener {
		@Override
		// 定位获取经纬度
		public void onReceiveLocation(BDLocation location) {
			Log.e("xxxx", "-------------------开始定位");
			if (location == null)
				return;

			// -------虚拟机测试时注释
			  mCityName	=location.getCity();
			  locData.latitude = location.getLatitude(); locData.longitude =
			  location.getLongitude(); //如果不显示定位精度圈，将accuracy赋值为0即可
			  locData.accuracy = location.getRadius(); locData.direction =
			  location.getDerect();
			 
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
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				mMapView.refresh();
				isRequest = false;
				mLocationClient.stop();
			}
			isFirstLoc = false;
		}

		// 获得搜索点
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

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
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
			// TODO Auto-generated method stub
			if (result == null) {
				return;
			}
			// 重新初始化导航线路
			routeOverlay = null;
			routeOverlay = new RouteOverlay(CompanyMapActiviy.this, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().add(routeOverlay);
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
		public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				// 遍历所有地址
				// ArrayList<MKPoiInfo> stPois =
				// res.getAddrResult().mStartPoiList;
				// ArrayList<MKPoiInfo> enPois =
				// res.getAddrResult().mEndPoiList;
				// ArrayList<MKCityListInfo> stCities =
				// res.getAddrResult().mStartCityList;
				// ArrayList<MKCityListInfo> enCities =
				// res.getAddrResult().mEndCityList;
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(CompanyMapActiviy.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			TransitRouteInfo = res;
			// 此处生成所有方案的信息加入list
			String[] strPlans = new String[res.getNumPlan()];
			String[][] strPlanConents = new String[res.getNumPlan()][];
			for (int i = 0; i < res.getNumPlan(); i++) {
				// 循环获取方案
				MKTransitRoutePlan planinfo = new MKTransitRoutePlan();
				planinfo = res.getPlan(i);
				// 将方案名称加入方案数组
				strPlans[i] = planinfo.getContent();
				// 将方案详细内容加入对应的详细数组
				strPlanConents[i] = new String[planinfo.getNumLines()];
				// 循环获取方案的乘车线路
				for (int j = 0; j < planinfo.getNumLines(); j++) {
					strPlanConents[i][j] = planinfo.getLine(j).getTitle()
							+ planinfo.getLine(j).getGetOnStop().name + "上车---"
							+ planinfo.getLine(j).getGetOffStop().name + "下车";
				}
			}
			// 将数据加入expandablelistview
			/*
			 * mAdapter = new PlanGroupAdapter (LifeCmbcBMapActivity.this
			 * ,strPlans , strPlanConents);
			 * expandableListView.setAdapter(mAdapter);
			 * textViewDes.setText("目的地:"+PioName.getText().toString());
			 * LifeCmbcBMapActivity
			 * .this.findViewById(R.id.layoutLines).setVisibility(View.VISIBLE);
			 * LifeCmbcBMapActivity
			 * .this.findViewById(R.id.layoutViewPlans).setVisibility
			 * (View.VISIBLE); //expandableListView.setVisibility(View.VISIBLE);
			 */
			DrawTransitRoute(0);
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int arg1) {
			// TODO Auto-generated method stub
			if (result == null) {
				return;
			}
			// 重新初始化导航线路
			routeOverlay = null;
			routeOverlay = new RouteOverlay(CompanyMapActiviy.this, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub

		}

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
