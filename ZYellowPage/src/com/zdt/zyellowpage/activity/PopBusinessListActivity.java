package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ab.activity.AbActivity;
import com.ab.task.AbTaskQueue;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;
import com.zdt.zyellowpage.util.ImageListAdapter;

public class PopBusinessListActivity extends AbActivity {

	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 0;
	private boolean isRefresh = true;
	private AbTaskQueue mAbTaskQueue = null;
	private ArrayList<String> mPhotoList = new ArrayList<String>();
	private AbTitleBar mAbTitleBar = null;
	private ImageListAdapter myListViewAdapter = null;
	DisplayUtil displayUtil;

	private MyApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.popbusinesspull_list);

		// 初始化标题栏
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("热门商家");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initSpinner();

		list = new ArrayList<Map<String, Object>>();
		newList = new ArrayList<Map<String, Object>>();
		initPopBusinessView();

	}

	/**
	 * 初始化三个下拉框
	 */
	void initSpinner() {
		String[] KeyWords = { "美食", "电影", "休闲娱乐", "酒店", "其他" };
		String[] Areas = { "五华区", "官渡区", "西山区", "盘龙区", "呈贡新区" };
		String[] NewOrPops = { "热门", "最新" };
		ArrayAdapter<String> adapter;
		Spinner spinnerKeyWord = (Spinner) this
				.findViewById(R.id.spinnerKeyWord);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, KeyWords);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeyWord.setAdapter(adapter);

		Spinner spinnerKeyArea = (Spinner) this.findViewById(R.id.spinnerArea);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Areas);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeyArea.setAdapter(adapter);

		Spinner spinnerNewOrPop = (Spinner) this
				.findViewById(R.id.spinnerNewOrPop);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, NewOrPops);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNewOrPop.setAdapter(adapter);

		displayUtil = DisplayUtil.getInstance(this);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		displayUtil.setViewLayoutParamsByX(spinnerKeyWord, 3, width);
		displayUtil.setViewLayoutParamsByX(spinnerKeyArea, 3, width);
		displayUtil.setViewLayoutParamsByX(spinnerNewOrPop, 3, width);
	}


	void getData(int i) {
		UserBll bll = new UserBll();

		CompanyListReqEntity companyParams = new CompanyListReqEntity(i, 10,
				application.cityid, "list-hot");
		bll.getListCompany(PopBusinessListActivity.this, companyParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						
						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {

							User u = lis.get(i);
							map = new HashMap<String, Object>();
							map.put("Member_id", u.getMember_id());
							map.put("itemsIcon", u.getLogo());
							map.put("itemsTitle", u.getFullname());
							map.put("itemsText", u.getKeyword());
							newList.add(map);
						}
						
						Log.e("xxxx11", "-----" + newList.size());
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						list.addAll(newList);
						myListViewAdapter.notifyDataSetChanged();
						newList.clear();
						removeProgressDialog();
						if(isRefresh){
							mAbPullListView.stopRefresh();
						}
						else{
							mAbPullListView.stopLoadMore(true);
						}
					}
				/*	@Override
					public void onSuccess(int statusCode, List<Object> lis) {
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
							newList.add(map);
						}
						
						Log.e("xxxx11", "-----" + newList.size());
					}

					@Override
					public void onStart() {
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFinish() {
						
						list.addAll(newList);
						myListViewAdapter.notifyDataSetChanged();
						newList.clear();
						removeProgressDialog();
						if(isRefresh){
							mAbPullListView.stopRefresh();
						}
						else{
							mAbPullListView.stopLoadMore(true);
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}*/
				});

	}

	/**
	 * 初始化商家列表显示
	 */
	protected void initPopBusinessView() {
		// 获取ListView对象
		mAbPullListView = (AbPullListView) this.findViewById(R.id.mListView);

		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(true);

		// ListView数据

		// 使用自定义的Adapter
		myListViewAdapter = new ImageListAdapter(this, list,
				R.layout.list_items, new String[] { "itemsIcon", "itemsTitle",
						"itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle, R.id.itemsText, R.id.itemsBtnConcern });
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		/*mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//PopBusinessListActivity.this.showToast(list.get(position).get("Member_id").toString());
				Intent intent = new Intent(PopBusinessListActivity.this,
						 BusinessDetailActivity.class);
				 intent.putExtra("MEMBER_ID", list.get(position-1).get("Member_id").toString());
				 startActivity(intent);
			}
		});*/

		currentPage= 0;

		/**
		 * 添加事件
		 */
		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				//改写成执行查询
				//mAbTaskQueue.execute(item1);
				isRefresh = true;
				list.clear();
				getData(currentPage);
			}

			@Override
			public void onLoadMore() {
				//mAbTaskQueue.execute(item2);
				isRefresh = false;
				getData(++currentPage);
			}

		});

		getData(0);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

}
