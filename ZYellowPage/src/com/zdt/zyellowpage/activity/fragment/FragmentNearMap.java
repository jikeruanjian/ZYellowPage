package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.MainActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.jsonEntity.NearCompanyReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class FragmentNearMap extends Fragment {

	View view;
	private AbHttpUtil mAbHttpUtil = null;
	private AbActivity mActivity;
	// 地图显示
	MapView mMapView = null;
	MapController mMapController = null;
	private List<User> list = null;
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

	// 兴趣点相关
	MKSearch mMKSearch = null;
	public View popView;// 点击兴趣点弹出泡泡
	ImageView popImage;// 泡泡上的图片
	TextView PioName;// 显示兴趣点的名称
	TextView closePop;
	int poiDistance = 0;// 兴趣点搜索范围
	GeoPoint poiPoint = null;// 兴趣点经纬度
	List<User> nearsearch;
	List<User> tempUser;
	MyPoiOverlayX poiOverlayx = null;// 自定义

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) getActivity();

		view = inflater.inflate(R.layout.fragment_nearmap, container, false);
		view.findViewById(R.id.Layout_PioAllLife).getBackground().setAlpha(150);

		myListener = new MyLocationListener();
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
		mMapView = (MapView) view.findViewById(R.id.bmapsView);
		initPoiDistantsBtn();
		initMapView();
		initMapLoc();
		initMKSearch();
		list = new ArrayList<User>();
		newList = new ArrayList<User>();
		getNearEnterpriseList();

		return view;
	}

	@Override
	public void onDestroy() {
		mMapView.destroy();
		if (MainActivity.mBMapMan != null) {
			MainActivity.mBMapMan.destroy();
			MainActivity.mBMapMan = null;
		}
		if (mLocationClient != null) {
			if (mLocationClient.isStarted()) {
				mLocationClient.stop();
			}
			mLocationClient = null;
		}
		if (mMKSearch != null) {
			mMKSearch = null;
		}
		super.onDestroy();
	}

	/**
	 * 点地图空白点时泡泡消失
	 */

	void initPoiDistantsBtn() {
		view.findViewById(R.id.Layout_PioAllLife).getBackground().setAlpha(150);
		view.findViewById(R.id.btn_Pio1Life).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});
		view.findViewById(R.id.btn_Pio2Life).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});
		view.findViewById(R.id.btn_Pio3Life).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});
		view.findViewById(R.id.btn_PioAllLife).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						poiDistanceOnClick(v);
					}

				});

		mActivity.findViewById(R.id.btn_LocStart).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mLocationClient.start();
						isRequest = true;
						mLocationClient.requestLocation();
						Toast.makeText(getActivity(), "正在定位…",
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
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (25.05177 * 1E6),
				(int) (102.71607 * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
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

		mLocationClient = new LocationClient(getActivity()); // 声明LocationClient类
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
		view.findViewById(R.id.btn_Pio1Life).setBackgroundColor(
				getResources().getColor(R.color.transparent));// 用于改变选中的背景颜色
		view.findViewById(R.id.btn_Pio2Life).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		view.findViewById(R.id.btn_Pio3Life).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		view.findViewById(R.id.btn_PioAllLife).setBackgroundColor(
				getResources().getColor(R.color.transparent));
		switch (v.getId()) {
		case R.id.btn_PioAllLife: {
			poiDistance = 0; // 6km范围内
			view.findViewById(R.id.btn_PioAllLife).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio1Life: {
			poiDistance = 500;// 1km范围内
			view.findViewById(R.id.btn_Pio1Life).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio2Life: {
			poiDistance = 1000;// 3km范围内
			view.findViewById(R.id.btn_Pio2Life).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}
		case R.id.btn_Pio3Life: {
			poiDistance = 1500; // 6km范围内
			view.findViewById(R.id.btn_Pio3Life).setBackgroundColor(
					Color.rgb(255, 127, 0));
			break;
		}

		default:
			break;
		}

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
			/*
			 * locData.latitude = location.getLatitude(); locData.longitude =
			 * location.getLongitude(); //如果不显示定位精度圈，将accuracy赋值为0即可
			 * locData.accuracy = location.getRadius(); locData.direction =
			 * location.getDerect();
			 */
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
				// 将搜索附近点放到定位里，定位根据现有经纬度和选择的范围值进行检索兴趣点，以下是百度接口
				// mMKSearch.poiSearchNearBy("民生银行",new
				// GeoPoint((int)(locData.latitude* 1e6),
				// (int)(locData.longitude * 1e6)), poiDistance);
				mMapView.refresh();
				isRequest = false;
				mLocationClient.stop();
			}
			// 首次定位完成
			getNearCompanyData();
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

	void getNearCompanyData() {
		UserBll bll = new UserBll();

		NearCompanyReqEntity nearCompanyReqEntity = new NearCompanyReqEntity(
				10, locData.latitude, locData.longitude, 2000);
		Log.e("xxxx", "-------" + locData.latitude + locData.longitude);
		bll.getNearCompany(mActivity, nearCompanyReqEntity,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						newList.clear();
						if (lis == null || lis.size() == 0) {
							return;
						}
						Log.e("xxxx", "------" + lis.size());
						for (int i = 0; i < lis.size(); i++) {
							newList.add(lis.get(i));
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						mActivity.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						mActivity.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						mActivity.removeProgressDialog();
						poiOverlayx = new MyPoiOverlayX(getResources()
								.getDrawable(R.drawable.icon_marka), mMapView,
								newList);
						mMapView.getOverlays().add(poiOverlayx);
						mMapView.refresh();
					}
					/*
					 * @Override public void onSuccess(int statusCode,
					 * List<Object> lis) { newList.clear(); if (lis == null ||
					 * lis.size() == 0) { return; } Log.e("xxxx", "------" +
					 * lis.size()); for (int i = 0; i < lis.size(); i++) {
					 * newList.add((User) lis.get(i)); } }
					 * 
					 * @Override public void onStart() {
					 * mActivity.showProgressDialog("同步信息..."); }
					 * 
					 * @Override public void onFinish() {
					 * mActivity.removeProgressDialog(); poiOverlayx = new
					 * MyPoiOverlayX(getResources()
					 * .getDrawable(R.drawable.icon_marka), mMapView, newList);
					 * mMapView.getOverlays().add(poiOverlayx);
					 * mMapView.refresh(); }
					 * 
					 * @Override public void onFailure(int statusCode, String
					 * content, Throwable error) {
					 * mActivity.showToast(error.getMessage()); }
					 * 
					 * @Override public void onErrorData(String
					 * status_description) {
					 * mActivity.showToast(status_description); }
					 */
				});

	}

	/**
	 * 覆盖物数据初始化,并显示
	 */
	void getNearEnterpriseList() {

		nearsearch = new ArrayList<User>();
		User u1 = new User();
		u1.setMember_id("1");
		u1.setFullname("name1");
		u1.setAddress("地址1");
		u1.setLatitude("25.0415");
		u1.setLongitude("102.7098");
		u1.setLogo("");
		User u2 = new User();
		u2.setMember_id("1");
		u2.setFullname("name1");
		u2.setAddress("地址1");
		u2.setLatitude("25.0457");
		u2.setLongitude("102.7099");
		u2.setLogo("");
		User u3 = new User();
		u3.setMember_id("1");
		u3.setFullname("name1");
		u3.setAddress("地址1");
		u3.setLatitude("25.0348");
		u3.setLongitude("102.7188");
		u3.setLogo("");
		nearsearch.add(u1);
		nearsearch.add(u2);
		nearsearch.add(u3);
		poiOverlayx = new MyPoiOverlayX(getResources().getDrawable(
				R.drawable.icon_marka), mMapView, nearsearch);
		mMapView.getOverlays().add(poiOverlayx);
		mMapView.refresh();
	}

	/**
	 * 范围内搜索显示
	 */
	@SuppressLint("NewApi")
	void initMKSearch() {
		view.findViewById(R.id.btn_PioAllLife).setBackgroundColor(
				Color.rgb(255, 127, 0));
		// 泡泡初始化
		popView = getActivity().getLayoutInflater().inflate(
				R.layout.overlay_pop, null);
		popImage = (ImageView) popView.findViewById(R.id.imageViewPop);
		// 关闭泡泡
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
		popView.getBackground().setAlpha(200);
		mMapView.addView(popView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		// 由于我的气泡的尾巴是在下边居中的,因此要设置成MapView.LayoutParams.BOTTOM_CENTER.
		// 这里没有给GeoPoint,在onFocusChangeListener中设置
		popView.setVisibility(View.GONE);// 隐藏泡泡
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

		// 点击后将地图中心移动至点击点，并显示泡泡
		@Override
		protected boolean onTap(int i) {
			// super.onTap(i);
			// CleanRouteOverlay();
			User info = mSearch.get(i);
			// 初始化泡泡
			popView.setVisibility(View.GONE);
			popImage.setImageResource(R.drawable.ic_launcher);
			MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
					.getLayoutParams();
			geoLP.point = new GeoPoint(
					(int) (Double.valueOf(info.getLatitude()) * 1e6),
					(int) (Double.valueOf(info.getLongitude()) * 1e6));// 这行用于popView的定位
			mMapController.animateTo(geoLP.point);
			TextView title = (TextView) popView
					.findViewById(R.id.map_bubbleTitle);
			poiPoint = geoLP.point;
			title.setText(info.getFullname());
			TextView desc = (TextView) popView
					.findViewById(R.id.map_bubbleText);
			if (info.getAddress() == null) {
				desc.setVisibility(View.GONE);
			} else {
				desc.setVisibility(View.VISIBLE);
				desc.setText(info.getAddress());
			}
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

	private class MapDetailListener implements OnClickListener {
		String mMemberId;

		public MapDetailListener(String memberId) {
			mMemberId = memberId;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Toast.makeText(mContext, mPosition+"",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mActivity, BusinessDetailActivity.class);
			intent.putExtra("MEMBER_ID", mMemberId);
			mActivity.startActivity(intent);
		}

	}

}
