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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.AllClassActivity;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.CompanyBuySellActivity;
import com.zdt.zyellowpage.activity.CompanyMapActiviy;
import com.zdt.zyellowpage.activity.MyPopupWindow;
import com.zdt.zyellowpage.activity.PersonDetailActivity;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.activity.PopPersonListActivity;
import com.zdt.zyellowpage.activity.TypeBusinessListActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.jsonEntity.PersonListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;
import com.zdt.zyellowpage.util.ImageListAdapterC;
import com.zdt.zyellowpage.util.ImageListAdapterP;

/**
 * 首页Fragment
 * 
 * @author Administrator
 * 
 */
public class FragmentHomePage extends Fragment implements OnClickListener{
	private View view;
	private AbActivity mActivity;
	private DisplayUtil displayUtil;
	private MyApplication application;
	private User hotUser;
	
	private TextView hotName;

	private TextView hotConent;
	
	private ImageView hotImage;

	MyPopupWindow myPopupWindow;
	
	private ListView mAbPullListViewB = null;
	private ImageListAdapterC myListViewAdapterB = null;
	private List<Map<String, Object>> listB = null;
	
	private ListView mAbPullListViewP = null;
	private ImageListAdapterP myListViewAdapterP = null;
	private List<Map<String, Object>> listP = null;
	

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 获取布局文件
		view = inflater.inflate(R.layout.fragment_homepage, container, false);
		// 获取所属Activity
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.abApplication;
		displayUtil = DisplayUtil.getInstance(mActivity);
		// 各种分类按钮
		ImageButton imgBFood = (ImageButton) view.findViewById(R.id.imageButtonFood);
		imgBFood.setOnClickListener(this);
		
		ImageButton imgBMovie = (ImageButton) view.findViewById(R.id.imageButtonMovie);
		imgBMovie.setOnClickListener(this);
		
		ImageButton imgBHappy = (ImageButton) view.findViewById(R.id.imageButtonHappy);
		imgBHappy.setOnClickListener(this);
		
		ImageButton imgBHotel = (ImageButton) view.findViewById(R.id.imageButtonHotel);
		imgBHotel.setOnClickListener(this);
		
		ImageButton imgBNewInfo = (ImageButton) view.findViewById(R.id.imageButtonNewInfo);
		imgBNewInfo.setOnClickListener(this);
		
		ImageButton imgBCash = (ImageButton) view.findViewById(R.id.imageButtonCash);
		imgBCash.setOnClickListener(this);
		
		ImageButton imgBPeople = (ImageButton) view.findViewById(R.id.imageButtonPeople);
		imgBPeople.setOnClickListener(this);
		
		ImageButton imgBAllClass= (ImageButton) view.findViewById(R.id.imageButtonAll);
		imgBAllClass.setOnClickListener(this);
		
		
		// 热门商家列表
		TextView hotbusiness  = (TextView) view.findViewById(R.id.textViewhotbusiness);
		hotbusiness.setOnClickListener(this);
		
		// 热门个人列表
		TextView textPerson = (TextView) view.findViewById(R.id.textViewhotperson);
		textPerson.setOnClickListener(this);
		
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
		int high = metric.heightPixels / 6;
		
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonFood),
				resouce.getDrawable(R.drawable.food), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonMovie),
				resouce.getDrawable(R.drawable.movie), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHappy),
				resouce.getDrawable(R.drawable.happy), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHotel),
				resouce.getDrawable(R.drawable.hotel), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonNewInfo),
				resouce.getDrawable(R.drawable.newinfo), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonCash),
				resouce.getDrawable(R.drawable.cash), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonPeople),
				resouce.getDrawable(R.drawable.people), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonAll),
				resouce.getDrawable(R.drawable.all), width, width*5/4);

		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewhotbusiness), high);
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewhotperson), high);
	}

	/**
	 * 初始化商家列表显示
	 */
	protected void initPopBusinessView() {
		// 获取ListView对象
		mAbPullListViewB = (ListView) view.findViewById(R.id.mListViewB);

		

		// 使用自定义的Adapter
		myListViewAdapterB = new ImageListAdapterC(mActivity,application,listB,
				R.layout.list_items, new String[] { "itemsIcon", "itemsTitle",
						"itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle, R.id.itemsText, R.id.itemsBtnConcern });
		mAbPullListViewB.setAdapter(myListViewAdapterB);
		mAbPullListViewB.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//PopBusinessListActivity.this.showToast(list.get(position).get("Member_id").toString());
				Intent intent = new Intent(mActivity,
						 BusinessDetailActivity.class);
				 intent.putExtra("MEMBER_ID", listB.get(position).get("Member_id").toString());
				 startActivity(intent);
			}
		});
		

		//getData();
	}
	
	/**
	 * 初始化热门个人
	 */
	protected void initPopPersonView() {
		// 获取ListView对象
		mAbPullListViewP = (ListView) view.findViewById(R.id.mListViewP);

		// 打开关闭下拉刷新加载更多功能
		//mAbPullListViewP.setPullRefreshEnable(true);
		//mAbPullListViewP.setPullLoadEnable(true);

		// 使用自定义的Adapter
		myListViewAdapterP = new ImageListAdapterP(mActivity,application,listP,
				R.layout.list_items, new String[] { "itemsIcon", "itemsTitle",
						"itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle, R.id.itemsText, R.id.itemsBtnConcern });
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
							//PopPersonListActivity.this.showToast(list.get(position).get("Member_id").toString());
						Intent intent = new Intent(mActivity,
								PersonDetailActivity.class);
						intent.putExtra("MEMBER_ID", listP.get(position).get("Member_id").toString());
							startActivity(intent);
						}
					});		
		
		//getData();
	}
	
	public void getData() {
		listB.clear();
		listP.clear();
		UserBll bll = new UserBll();
		hotUser = new User();
		CompanyListReqEntity companyParams = new CompanyListReqEntity(0, 10,
				application.cityid, "list-hot");

		bll.getListCompany(mActivity, companyParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {

							User u = (User) lis.get(i);
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
						// TODO Auto-generated method stub
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						if (localList == null || localList.size() == 0) {
							return;
						}
						hotUser = (User) localList.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						
						if (hotUser.getLogo() != null) {
							hotImage = (ImageView) view.findViewById(R.id.imageViewhot);
							new AbImageDownloader(mActivity).display(hotImage,
									hotUser.getLogo());
						}
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
						myListViewAdapterB.notifyDataSetChanged();
						displayUtil.setListViewHeightBasedOnChildren(mAbPullListViewB , 10);
						
					}
					
				});
		
		PersonListReqEntity personParams = new PersonListReqEntity(0, 10,
				application.cityid, "list-hot");
		new UserBll().getListPerson(mActivity, personParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {

							User u = (User) lis.get(i);
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
						myListViewAdapterP.notifyDataSetChanged();
						displayUtil.setListViewHeightBasedOnChildren(mAbPullListViewP , 10);
						
					}
			
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		//美食
		case R.id.imageButtonFood:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "美食");
			 intent.putExtra("TypeId", "美食");
			 startActivity(intent);
			break;
		case R.id.imageButtonMovie:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "旅游");
			 intent.putExtra("TypeId", "旅游");
			 startActivity(intent);
			break;
		case R.id.imageButtonHappy:
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "休闲娱乐");
			 intent.putExtra("TypeId", "休闲娱乐");
			 startActivity(intent);
			break;
		case R.id.imageButtonHotel:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "酒店");
			 intent.putExtra("TypeId", "酒店");
			 startActivity(intent);
			break;
		case R.id.imageButtonNewInfo:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "生活服务");
			 intent.putExtra("TypeId", "生活服务");
			 startActivity(intent);
			break;
		case R.id.imageButtonCash:
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "运动健身");
			 intent.putExtra("TypeId","运动健身");
			 startActivity(intent);
			break;
		case R.id.imageButtonAll:
			//showPopWindows();
			intent = new Intent(mActivity, AllClassActivity.class);
			 startActivity(intent);
			break;
		case R.id.imageButtonPeople:
			 intent = new Intent(mActivity,
						PopPersonListActivity.class);
			 intent.putExtra("Type", "个人列表");
			 intent.putExtra("TypeId", "list-hot");
				startActivity(intent);
			break;
			//热门商家
		case R.id.textViewhotbusiness:
			 intent = new Intent(mActivity,
					PopBusinessListActivity.class);
			 intent.putExtra("Type", "热门商家");
			 intent.putExtra("TypeId", "list-hot");
			startActivity(intent);
			break;
		case R.id.textViewhotperson:
			 intent = new Intent(mActivity,
					PopPersonListActivity.class);
			 intent.putExtra("Type", "热门关注");
			 intent.putExtra("TypeId", "list-hot");
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	
	void  showPopWindows(){
		//Log.e("fragment", "-----点击了全部分类");
		myPopupWindow = new MyPopupWindow(mActivity);
		myPopupWindow.popupWindow.setWidth(mActivity.getWindowManager().getDefaultDisplay().getWidth()/7*6);
		displayUtil.setViewLayoutParamsR(myPopupWindow.layoutLeft,
				mActivity.getWindowManager().getDefaultDisplay().getWidth()/7*3,0);
		displayUtil.setViewLayoutParamsL(myPopupWindow.listViewClassB,
				0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		//displayUtil.setViewLayoutParamsR(myPopupWindow.layoutRight,0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		displayUtil.setViewLayoutParamsL(myPopupWindow.listViewClassP,
				0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		myPopupWindow.popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		myPopupWindow.popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		
	}

}
