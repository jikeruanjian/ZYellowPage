package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.TieBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Tie;
import com.zdt.zyellowpage.util.ImageListAdapterP;

public class TieListActivity extends AbActivity {

	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 0;
	private boolean isRefresh = true;
	// private DisplayUtil displayUtil;
	private TextView newOrPoTextView;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	private ImageListAdapterP myListViewAdapter = null;
	private MyApplication application;
	private String type = "1";
	private String[] NewOrPops;
	MyPopupWindowB myPopupWindow;
	AbTitleBar mAbTitleBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.tiepull_list);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("喜庆专区");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initSpinner();
		myPopupWindow = new MyPopupWindowB(TieListActivity.this, "1");
		list = new ArrayList<Map<String, Object>>();
		newList = new ArrayList<Map<String, Object>>();
		initPopBusinessView();
	}

	/**
	 * 初始化下拉框
	 */
	void initSpinner() {

		NewOrPops = new String[] { "婚庆", "乔迁", "聚会", "开业", "庆典" };

		newOrPoTextView = (TextView) this.findViewById(R.id.spinnerNewOrPopP);
		newOrPoTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int y = newOrPoTextView.getBottom() * 3 / 2;
				int x = newOrPoTextView.getLeft() - 10;
				showPopupWindow(x, y);
			}
		});

		// displayUtil = DisplayUtil.getInstance(this);
		// DisplayMetrics metric = new DisplayMetrics();
		// this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		// int width = metric.widthPixels;
		// displayUtil.setViewLayoutParamsByX(newOrPoTextView, 3, width);
	}

	void getData(int i) {
		TieBll tieBll = new TieBll();

		tieBll.getTieList(this, i, 20, application.cityid, type,
				new ZzObjectHttpResponseListener<Tie>() {
					@Override
					public void onSuccess(int statusCode, List<Tie> lis) {
						if (lis == null || lis.size() == 0) {
							showToast("没有更多数据！");
							return;
						}

						Map<String, Object> map;
						for (int i = 0; i < lis.size(); i++) {
							Tie tie = lis.get(i);
							map = new HashMap<String, Object>();
							map.put("item_id", tie.getItem_id());
							map.put("itemsIcon", tie.getLogo());
							map.put("itemsTitle", tie.getTitle());
							map.put("itemsText", "地点：" + tie.getAddress()
									+ "\r\n时间：" + tie.getTime());
							newList.add(map);
						}
					}

					@Override
					public void onStart() {
						showProgressDialog("同步信息...");
					}

					@SuppressWarnings("null")
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Tie> lis) {
						if (lis != null || lis.size() > 0) {
							Map<String, Object> map;
							for (int i = 0; i < lis.size(); i++) {
								Tie tie = lis.get(i);
								map = new HashMap<String, Object>();
								map.put("item_id", tie.getItem_id());
								map.put("itemsIcon", tie.getLogo());
								map.put("itemsTitle", tie.getTitle());
								map.put("itemsText", "地点：" + tie.getAddress()
										+ "\r\n时间：" + tie.getTime());
								newList.add(map);
							}
						}
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						list.addAll(newList);
						myListViewAdapter.notifyDataSetChanged();
						int len = newList.size();
						newList.clear();
						removeProgressDialog();
						if (isRefresh) {
							mAbPullListView.stopRefresh();
						} else {
							if (len == 10) {
								mAbPullListView.stopLoadMore(true);
							} else {
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
		myListViewAdapter = new ImageListAdapterP(this, application, list,
				R.layout.tie_list_items, new String[] { "itemsIcon",
						"itemsTitle", "itemsText" }, new int[] {
						R.id.itemsIcon, R.id.itemsTitle, R.id.itemsText,
						R.id.itemsBtnConcern });
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//sTieListActivity.this.showToast(list.get(position-1).get("item_id").toString());
				Intent intent = new Intent(TieListActivity.this,
						TieDetailActivity.class);
				intent.putExtra("ITEM_ID",
						list.get(position - 1).get("item_id").toString());
				startActivity(intent);
			}
		});

		/**
		 * 添加事件
		 */
		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				// 改写成执行查询
				isRefresh = true;
				list.clear();
				getData(0);

			}

			@Override
			public void onLoadMore() {
				isRefresh = false;
				getData(++currentPage);
			}
		});
		getData(0);
	}

	public void showPopupWindow(int x, int y) {
		if (popupWindow == null) {
			layout = (LinearLayout) LayoutInflater.from(TieListActivity.this)
					.inflate(R.layout.popdialog, null);
			listView = (ListView) layout.findViewById(R.id.listViewPopW);
			listView.setAdapter(new ArrayAdapter<String>(TieListActivity.this,
					R.layout.text_itemselect, R.id.textViewSelectItemName,
					NewOrPops));
			popupWindow = new PopupWindow(TieListActivity.this);
			popupWindow.setWidth(getWindowManager().getDefaultDisplay()
					.getWidth() / 2);
			popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setContentView(layout);

			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					list.clear();
					type = position + 1 + "";
					getData(0);
					popupWindow.dismiss();
					popupWindow = null;
				}
			});

		}
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(newOrPoTextView, x, 10);
	}
}
