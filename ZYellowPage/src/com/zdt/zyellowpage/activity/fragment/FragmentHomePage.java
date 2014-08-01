package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.AllTypeActivity;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.MainActivity;
import com.zdt.zyellowpage.activity.MyPopupWindow;
import com.zdt.zyellowpage.activity.PersonDetailActivity;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.activity.PopPersonListActivity;
import com.zdt.zyellowpage.activity.webView.MyBrowserActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;
import com.zdt.zyellowpage.util.ImageListAdapterC;

/**
 * 首页Fragment
 * 
 * @author Administrator
 * 
 */
public class FragmentHomePage extends Fragment implements OnClickListener {
	private View view;
	private AbActivity mActivity;
	private DisplayUtil displayUtil;
	private MyApplication application;
	// private User hotUser;
	//
	// private TextView hotName;
	// private TextView hotConent;
	// private ImageView hotImage;
	MyPopupWindow myPopupWindow;

	private ListView mAbPullListViewB = null;
	private ImageListAdapterC myListViewAdapterB = null;
	private List<Map<String, Object>> listB = null;

	private ListView mAbPullListViewP = null;
	private ImageListAdapterC myListViewAdapterP = null;
	private List<Map<String, Object>> listP = null;
	private String currentCityId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 获取布局文件
		view = inflater.inflate(R.layout.fragment_homepage, container, false);
		// 获取所属Activity
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.abApplication;
		displayUtil = DisplayUtil.getInstance(mActivity);
		// 各种分类按钮
		RelativeLayout imgBFood = (RelativeLayout) view
				.findViewById(R.id.imageButtonFood);
		imgBFood.setOnClickListener(this);

		RelativeLayout imgBMovie = (RelativeLayout) view
				.findViewById(R.id.imageButtonMovie);
		imgBMovie.setOnClickListener(this);

		RelativeLayout imgBHappy = (RelativeLayout) view
				.findViewById(R.id.imageButtonHappy);
		imgBHappy.setOnClickListener(this);

		RelativeLayout imgBHotel = (RelativeLayout) view
				.findViewById(R.id.imageButtonHotel);
		imgBHotel.setOnClickListener(this);

		RelativeLayout imgBNewInfo = (RelativeLayout) view
				.findViewById(R.id.imageButtonNewInfo);
		imgBNewInfo.setOnClickListener(this);

		RelativeLayout imgBCash = (RelativeLayout) view
				.findViewById(R.id.imageButtonCash);
		imgBCash.setOnClickListener(this);

		RelativeLayout imgBPeople = (RelativeLayout) view
				.findViewById(R.id.imageButtonPeople);
		imgBPeople.setOnClickListener(this);

		RelativeLayout imgBAllClass = (RelativeLayout) view
				.findViewById(R.id.imageButtonAll);
		imgBAllClass.setOnClickListener(this);
		
		//-----快捷生活
		RelativeLayout LayoutBus = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutBus);
		LayoutBus.setOnClickListener(this);
		
		RelativeLayout LayoutTrain  = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutTrain);
		LayoutTrain.setOnClickListener(this);
		
		RelativeLayout LayoutFlight  = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutFlight);
		LayoutFlight.setOnClickListener(this);
		
		
		RelativeLayout LayoutSubway  = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutSubway);
		LayoutSubway.setOnClickListener(this);
		
		RelativeLayout LayoutWeather  = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutWeather);
		LayoutWeather.setOnClickListener(this);
		
		RelativeLayout LayoutDelivery  = (RelativeLayout) view
				.findViewById(R.id.RelativeLayoutDelivery);
		LayoutDelivery.setOnClickListener(this);

		// 热门商家列表
		view.findViewById(R.id.textViewhotbusiness).setOnClickListener(this);

		// 热门个人列表
		view.findViewById(R.id.textViewhotperson).setOnClickListener(this);

		listB = new ArrayList<Map<String, Object>>();
		listP = new ArrayList<Map<String, Object>>();
		initPopBusinessView();
		initPopPersonView();
		getData();
		initType(view);
		return view;

	}

	/**
	 * 初始化类型选择按钮背景图片布局，主要是为了适应屏幕
	 * 
	 * @param view
	 */
	void initType(View view) {
		Resources resouce = view.getResources();
		/**
		 * 获取当前屏幕的像素值
		 */
		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels / 4;
		// int high = metric.heightPixels / 6;

		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonFood),
				resouce.getDrawable(R.drawable.food), width, 102);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonMovie),
				resouce.getDrawable(R.drawable.movie), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHappy),
				resouce.getDrawable(R.drawable.happy), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHotel),
				resouce.getDrawable(R.drawable.hotel), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonNewInfo),
				resouce.getDrawable(R.drawable.newinfo), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonCash),
				resouce.getDrawable(R.drawable.cash), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonPeople),
				resouce.getDrawable(R.drawable.people), width, 0);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonAll),
				resouce.getDrawable(R.drawable.all), width, 0);

		// displayUtil.setViewLayoutParamsTextView(view.findViewById(R.id.textViewhotbusiness),
		// high);
		// displayUtil.setViewLayoutParamsTextView(view.findViewById(R.id.textViewhotperson),
		// high);
	}

	/**
	 * 初始化商家列表显示
	 */
	protected void initPopBusinessView() {
		// 获取ListView对象
		mAbPullListViewB = (ListView) view.findViewById(R.id.mListViewB);

		// 使用自定义的Adapter
		myListViewAdapterB = new ImageListAdapterC(mActivity, application,
				listB, R.layout.list_items, new String[] { "itemsIcon",
						"itemsTitle", "itemsText" }, new int[] {
						R.id.itemsIcon, R.id.itemsTitle, R.id.itemsText,
						R.id.itemsBtnConcern });
		mAbPullListViewB.setAdapter(myListViewAdapterB);
		mAbPullListViewB.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// PopBusinessListActivity.this.showToast(list.get(position).get("Member_id").toString());
				Intent intent = new Intent(mActivity,
						BusinessDetailActivity.class);
				intent.putExtra("MEMBER_ID",
						listB.get(position).get("Member_id").toString());
				mActivity.startActivity(intent);
			}
		});

		// getData();
	}

	/**
	 * 初始化热门个人
	 */
	protected void initPopPersonView() {
		// 获取ListView对象
		mAbPullListViewP = (ListView) view.findViewById(R.id.mListViewP);

		// 打开关闭下拉刷新加载更多功能
		// mAbPullListViewP.setPullRefreshEnable(true);
		// mAbPullListViewP.setPullLoadEnable(true);

		// 使用自定义的Adapter
		myListViewAdapterP = new ImageListAdapterC(mActivity, application,
				listP, R.layout.list_items, new String[] { "itemsIcon",
						"itemsTitle", "itemsText" }, new int[] {
						R.id.itemsIcon, R.id.itemsTitle, R.id.itemsText,
						R.id.itemsBtnConcern });
		mAbPullListViewP.setAdapter(myListViewAdapterP);
		/**
		 * 添加事件
		 * 
		 */
		// item被点击事件
		mAbPullListViewP.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// PopPersonListActivity.this.showToast(list.get(position).get("Member_id").toString());
				Intent intent = new Intent(mActivity,
						PersonDetailActivity.class);
				intent.putExtra("MEMBER_ID",
						listP.get(position).get("Member_id").toString());
				mActivity.startActivity(intent);
			}
		});

		// getData();
	}

	/**
	 * 获取热门商企
	 */
	public void getData() {

		if (application.cityid.equals(currentCityId)) {
			return;
		}
		currentCityId = application.cityid;
		MainActivity.textViewArea.setText(application.cityName);
		CompanyListReqEntity companyParams = new CompanyListReqEntity(0, 10,
				application.cityid, "list-hot");

		new UserBll().getListCompany(mActivity, companyParams, "list-hot",
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						listB.clear();
						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {

							User u = lis.get(i);
							map = new HashMap<String, Object>();
							map.put("Member_id", u.getMember_id());
							map.put("itemsIcon", u.getLogo());
							map.put("itemsTitle", u.getFullname());
							map.put("itemsText", u.getKeyword());
							listB.add(map);
						}
					}

					@Override
					public void onStart() {
						mActivity.showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						if (localList == null || localList.size() == 0) {
							if (Constant.NOCONNECT.equals(content))
								mActivity.showToast(content);
							return;
						}
						Map<String, Object> map;
						for (int i = 0; i < localList.size(); i++) {
							User u = localList.get(i);
							map = new HashMap<String, Object>();
							map.put("Member_id", u.getMember_id());
							map.put("itemsIcon", u.getLogo());
							map.put("itemsTitle", u.getFullname());
							map.put("itemsText", u.getKeyword());
							listB.add(map);
						}
						myListViewAdapterB.notifyDataSetChanged();
					}

					@Override
					public void onErrorData(String status_description) {
						if (listB.size() <= 0) {
						}
						// mActivity.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// mActivity.removeProgressDialog();
						myListViewAdapterB.notifyDataSetChanged();
						displayUtil.setListViewHeightBasedOnChildren(
								mAbPullListViewB, 5);
					}
				});

		getLatestData();
		mAbPullListViewB.setFocusable(false);
		mAbPullListViewP.setFocusable(false);
	}

	/**
	 * 获取最新商企
	 */
	public void getLatestData() {
		CompanyListReqEntity latestCompanyParams = new CompanyListReqEntity(0,
				10, application.cityid, "list-latest");

		new UserBll().getListCompany(mActivity, latestCompanyParams,
				"list-latest", new ZzObjectHttpResponseListener<User>() {
					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						listP.clear();
						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {

							User u = lis.get(i);
							map = new HashMap<String, Object>();
							map.put("Member_id", u.getMember_id());
							map.put("itemsIcon", u.getLogo());
							map.put("itemsTitle", u.getFullname());
							map.put("itemsText", u.getKeyword());
							listP.add(map);
						}
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						if (localList == null || localList.size() == 0) {
							return;
						}
						Map<String, Object> map;
						for (int i = 0; i < localList.size(); i++) {
							User u = localList.get(i);
							map = new HashMap<String, Object>();
							map.put("Member_id", u.getMember_id());
							map.put("itemsIcon", u.getLogo());
							map.put("itemsTitle", u.getFullname());
							map.put("itemsText", u.getKeyword());
							listP.add(map);
						}
						myListViewAdapterP.notifyDataSetChanged();
					}

					@Override
					public void onErrorData(String status_description) {
						if (listP.size() <= 0) {
						}
						// mActivity.showToast(status_description);
					}

					@Override
					public void onFinish() {
						mActivity.removeProgressDialog();
						myListViewAdapterP.notifyDataSetChanged();
						displayUtil.setListViewHeightBasedOnChildren(
								mAbPullListViewP, 10);
					}
				});
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		// 美食
		case R.id.imageButtonFood:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "美食");
			intent.putExtra("TypeId", "美食");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonMovie:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "旅游");
			intent.putExtra("TypeId", "旅游");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonHappy:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "休闲娱乐");
			intent.putExtra("TypeId", "休闲娱乐");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonHotel:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "酒店");
			intent.putExtra("TypeId", "酒店");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonNewInfo:

			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "生活服务");
			intent.putExtra("TypeId", "生活服务");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonCash:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "运动健身");
			intent.putExtra("TypeId", "运动健身");
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonAll:
			// showPopWindows();
			intent = new Intent(mActivity, AllTypeActivity.class);
			mActivity.startActivity(intent);
			break;
		case R.id.imageButtonPeople:
			 intent = new Intent(mActivity, PopPersonListActivity.class);
			 intent.putExtra("Type", "个人列表");
			 intent.putExtra("TypeId", "list-hot");
			//intent = new Intent(mActivity, PopBusinessListActivity.class);
			//intent.putExtra("Type", "丽人");
			//intent.putExtra("TypeId", "丽人");
			mActivity.startActivity(intent);
			break;
		// 热门商家
		case R.id.textViewhotbusiness:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "热门商企");
			intent.putExtra("TypeId", "list-hot");
			mActivity.startActivity(intent);
			break;
		case R.id.textViewhotperson:
			intent = new Intent(mActivity, PopBusinessListActivity.class);
			intent.putExtra("Type", "热门商企");
			intent.putExtra("TypeId", "list-latest");
			mActivity.startActivity(intent);
			break;
		case R.id.RelativeLayoutBus://公交
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://m.8684.cn/kunming_bus");
			intent.putExtra("title", "公交查询");
			startActivity(intent);
			break;
		case R.id.RelativeLayoutTrain://火车
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://touch.qunar.com/h5/train/?from=touchindex");
			intent.putExtra("title", "火车票查询");
			startActivity(intent);
			break;
		case R.id.RelativeLayoutFlight://机票
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://touch.qunar.com/h5/flight/");
			intent.putExtra("title", "机票查询");
			startActivity(intent);
			break;
		case R.id.RelativeLayoutSubway://地铁
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://m.8684.cn/dt_switch");
			intent.putExtra("title", "地铁查询");
			startActivity(intent);
			break;
		case R.id.RelativeLayoutWeather://天气
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://www.weixinguanjia.cn/links.html?t=6");
			intent.putExtra("title", "天气查询");
			startActivity(intent);
			break;
		case R.id.RelativeLayoutDelivery://快递
			intent = new Intent(mActivity,MyBrowserActivity.class);
			intent.putExtra("url", "http://m.kuaidi100.com/#input");
			intent.putExtra("title", "快递查询");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// void showPopWindows() {
	// // Log.e("fragment", "-----点击了全部分类");
	// myPopupWindow = new MyPopupWindow(mActivity);
	// myPopupWindow.popupWindow.setWidth(mActivity.getWindowManager()
	// .getDefaultDisplay().getWidth() / 7 * 6);
	// displayUtil.setViewLayoutParamsR(myPopupWindow.layoutLeft, mActivity
	// .getWindowManager().getDefaultDisplay().getWidth() / 7 * 3, 0);
	// displayUtil
	// .setViewLayoutParamsL(myPopupWindow.listViewClassB, 0,
	// mActivity.getWindowManager().getDefaultDisplay()
	// .getHeight() / 5 * 3);
	// //
	// displayUtil.setViewLayoutParamsR(myPopupWindow.layoutRight,0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
	// displayUtil
	// .setViewLayoutParamsL(myPopupWindow.listViewClassP, 0,
	// mActivity.getWindowManager().getDefaultDisplay()
	// .getHeight() / 5 * 3);
	// myPopupWindow.popupWindow
	// .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
	// myPopupWindow.popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	// }

	@Override
	public void onResume() {
		super.onResume();

		//getData();
	}
}
