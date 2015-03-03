package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BuySellContentActivity;
import com.zdt.zyellowpage.activity.EditSupplyDemandActivity;
import com.zdt.zyellowpage.bll.SupplyDemandBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.SupplyDemandReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class FragmentSell extends Fragment implements ISimpleDialogListener {
	private AbActivity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<SupplyDemand> SupplyDemandList = null;
	private List<SupplyDemand> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 1;
	private boolean isRefresh = true;
	private String member_Id;
	SimpleAdapter adapter;
	private boolean isEdit = false;
	private MyApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		member_Id = getArguments().getString("MEMBERID");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) this.getActivity();
		application = (MyApplication) mActivity.getApplication();

		isEdit = mActivity.getIntent().getBooleanExtra("isEdit", false);
		View view = inflater.inflate(R.layout.pull_list, null);
		// 获取ListView对象
		mAbPullListView = (AbPullListView) view.findViewById(R.id.mListView);
		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(true);

		// ListView数据
		list = new ArrayList<Map<String, Object>>();
		SupplyDemandList = new ArrayList<SupplyDemand>();
		newList = new ArrayList<SupplyDemand>();

		if (isEdit) {
			adapter = new SimpleAdapter(mActivity, list, R.layout.text_item,
					new String[] { "textViewSellBuyItemNames", "time" },
					new int[] { R.id.textViewSellBuyItemName, R.id.tvTime });
		} else
			adapter = new SimpleAdapter(mActivity, list, R.layout.text_item,
					new String[] { "textViewSellBuyItemNames", "time" },
					new int[] { R.id.textViewSellBuyItemName, R.id.tvTime });
		mAbPullListView.setAdapter(adapter);

		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				if (isEdit) {
					intent = new Intent(mActivity,
							EditSupplyDemandActivity.class);
				} else {
					intent = new Intent(mActivity, BuySellContentActivity.class);
				}
				intent.putExtra("ITEMID", SupplyDemandList.get(position - 1)
						.getItem_id());
				startActivity(intent);
			}
		});

		if (isEdit) {
			mAbPullListView
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, final int position, long arg3) {
							mActivity.showDialog("确认", "确认删除？",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											SupplyDemand sd = new SupplyDemand();
											sd.setItem_id("-"
													+ SupplyDemandList.get(
															position - 1)
															.getItem_id());
											new SupplyDemandBll()
													.updateSupplyDemand(
															mActivity,
															application.mUser
																	.getToken(),
															sd,
															new ZzStringHttpResponseListener() {

																@Override
																public void onSuccess(
																		int statusCode,
																		String content) {
																	list.remove(position - 1);
																	SupplyDemandList
																			.remove(position - 1);
																	adapter.notifyDataSetChanged();
																	mActivity
																			.showToast(content);
																}

																@Override
																public void onStart() {
																	mActivity
																			.showProgressDialog("正在删除...");
																}

																@Override
																public void onFinish() {
																	mActivity
																			.removeProgressDialog();

																}

																@Override
																public void onFailure(
																		int statusCode,
																		String content,
																		Throwable error) {
																	mActivity
																			.showToast(content);
																}

																@Override
																public void onErrorData(
																		String status_description) {
																	mActivity
																			.showToast(status_description);
																}
															});

										}
									});
							SimpleDialogFragment
									.createBuilder(
											mActivity,
											mActivity
													.getSupportFragmentManager())
									.setTitle("确认").setMessage("删除该条记录吗？")
									.setPositiveButtonText("删除")
									.setNegativeButtonText("取消")
									.setRequestCode(position).show();
							return false;
						}
					});
		}

		/**
		 * 添加事件
		 */
		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				// 改写成执行查询
				// mAbTaskQueue.execute(item1);
				isRefresh = true;
				SupplyDemandList.clear();
				getData(0);
			}

			@Override
			public void onLoadMore() {
				// mAbTaskQueue.execute(item2);
				isRefresh = false;
				getData(++currentPage);
			}

		});

		getData(0);

		return view;

	}

	/**
	 * 获取数据
	 */
	private void getData(int i) {
		SupplyDemandBll bll = new SupplyDemandBll();
		SupplyDemandReqEntity supplyDemandParams = new SupplyDemandReqEntity(i,
				10, member_Id, "0");
		bll.getSupplyDemandList(mActivity, supplyDemandParams,
				new ZzObjectHttpResponseListener<SupplyDemand>() {

					@Override
					public void onSuccess(int statusCode, List<SupplyDemand> lis) {
						if (lis == null || lis.size() == 0) {
							mActivity.showToast("没有更多数据！");
							return;
						}
						newList.addAll(lis);
					}

					@Override
					public void onStart() {
						mActivity.showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<SupplyDemand> localList) {
						mActivity.showToast(content == null ? "数据获取发生错误"
								: content);
					}

					@Override
					public void onErrorData(String status_description) {
					}

					@Override
					public void onFinish() {
						mActivity.removeProgressDialog();
						SupplyDemandList.addAll(newList);
						int len = newList.size();
						// mAbPullListView.removeAllViews();
						newList.clear();
						list.clear();
						for (SupplyDemand s : SupplyDemandList) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("textViewSellBuyItemNames", s.getTitle());
							map.put("time", "发布时间:" + s.getTime());
							list.add(map);
						}
						adapter.notifyDataSetChanged();
						// item被点击事件

						mActivity.removeProgressDialog();

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

	@Override
	public void onPositiveButtonClicked(final int requestCode) {
		SupplyDemand sd = new SupplyDemand();
		sd.setItem_id("-" + SupplyDemandList.get(requestCode - 1).getItem_id());
		new SupplyDemandBll().updateSupplyDemand(mActivity,
				application.mUser.getToken(), sd,
				new ZzStringHttpResponseListener() {

					@Override
					public void onSuccess(int statusCode, String content) {
						list.remove(requestCode - 1);
						SupplyDemandList.remove(requestCode - 1);
						adapter.notifyDataSetChanged();
						mActivity.showToast(content);
					}

					@Override
					public void onStart() {
						mActivity.showProgressDialog("正在删除...");
					}

					@Override
					public void onFinish() {
						mActivity.removeProgressDialog();

					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						mActivity.showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						mActivity.showToast(status_description);
					}
				});
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
}
