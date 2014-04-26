package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.jsonEntity.PersonListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;
import com.zdt.zyellowpage.util.ImageListAdapterC;

public class PopBusinessListActivity extends AbActivity {

	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 0;
	private boolean isRefresh = true;
	private ImageListAdapterC myListViewAdapter = null;
	private DisplayUtil displayUtil;
	private TextView typeTextView;
	private TextView areaTextView;
	private TextView newOrPoTextView;
	private TextView wordsTextView;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	private MyApplication application;
	private String type="商家";
	private String typeId="商家";
	private String[] KeyWords;
	private String[] Areas;
	private String[] NewOrPops;
	private String[] words;
	private String cityId;
	private String keyId;
	private String condition;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.popbusinesspull_list);
		if (getIntent().getExtras() != null) {
			type = (String) getIntent().getExtras().get("Type");
			typeId = (String) getIntent().getExtras().get("TypeId");
		}
		// 初始化标题栏
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(type);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		cityId = application.cityid;
		keyId = typeId;//"list-hot";
		initSpinner();

		list = new ArrayList<Map<String, Object>>();
		newList = new ArrayList<Map<String, Object>>();
		initPopBusinessView();
		
	}

	/**
	 * 初始化三个下拉框
	 */
	void initSpinner() {
		
		NewOrPops = new String[]{ "热门", "最新" };
		typeTextView = (TextView)this.findViewById(R.id.spinnerKeyWord);
		typeTextView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int y = typeTextView.getBottom() * 3 / 2;
				int x = typeTextView.getLeft()+10;
				words =  MainActivity.listCategoryName.toArray(new String[0]);
				wordsTextView = typeTextView;
				condition = "1";
				showPopupWindow(x, y);
			
			}
			
		});
		areaTextView = (TextView)this.findViewById(R.id.spinnerArea);
		areaTextView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int y = typeTextView.getBottom() * 3 / 2;
				int x = typeTextView.getLeft()-10;
				words =  MainActivity.listAreaName.toArray(new String[0]);
				wordsTextView = areaTextView;
				condition = "2";
				showPopupWindow(x, y);
			}
			
		});
		
		newOrPoTextView = (TextView)this.findViewById(R.id.spinnerNewOrPop);
		newOrPoTextView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int y = typeTextView.getBottom() * 3 / 2;
				int x = typeTextView.getLeft()-10;
				words = NewOrPops;
				wordsTextView = newOrPoTextView;
				condition = "3";
				showPopupWindow(x, y);
			}
		});
		
		displayUtil = DisplayUtil.getInstance(this);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		displayUtil.setViewLayoutParamsByX(typeTextView, 3, width);
		displayUtil.setViewLayoutParamsByX(areaTextView, 3, width);
		displayUtil.setViewLayoutParamsByX(newOrPoTextView, 3, width);
	}


	void getData(int i) {
		UserBll bll = new UserBll();

		CompanyListReqEntity companyParams = new CompanyListReqEntity(i, 10,
				cityId,keyId);//application.cityid, "list-hot"
		bll.getListCompany(PopBusinessListActivity.this, companyParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							showToast("没有更多数据！");
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
						int len = newList.size();
						newList.clear();
						removeProgressDialog();
						if(isRefresh){
							mAbPullListView.stopRefresh();
						}
						else{
							if(len == 10){
								mAbPullListView.stopLoadMore(true);
							}
							else{
								mAbPullListView.stopLoadMore(false);
							}
						}
					}
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
		myListViewAdapter = new ImageListAdapterC(this,application,list,
				R.layout.list_items, new String[] { "itemsIcon", "itemsTitle",
						"itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle, R.id.itemsText, R.id.itemsBtnConcern });
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
	/*	mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
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
				getData(0);
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

	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(PopBusinessListActivity.this).inflate(
				R.layout.popdialog, null);
		listView = (ListView) layout.findViewById(R.id.listViewPopW);
		listView.setAdapter(new ArrayAdapter<String>(PopBusinessListActivity.this,
				R.layout.text_itemselect, R.id.textViewSelectItemName,  words));

		popupWindow = new PopupWindow(PopBusinessListActivity.this);
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);;
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		popupWindow.showAsDropDown(wordsTextView, x, 10);
		//popupWindow.showAtLocation(findViewById(R.id.LinearLayoutpopwindows), Gravity.LEFT
			//	| Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				wordsTextView .setText(words[arg2]);
				if("1".equals(condition)){
					keyId= MainActivity.listCategory.get(arg2).getId();
					Log.e("xxx", "-----分类----"+keyId);
				}
				else if("2".equals(condition)){
					cityId = MainActivity.listArea.get(arg2).getId();
					Log.e("xxx", "-----城市 ----"+cityId);
				}
				
				
				list.clear();
				getData(0);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}
}
