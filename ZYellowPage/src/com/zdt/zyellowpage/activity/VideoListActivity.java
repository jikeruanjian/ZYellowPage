package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.VideoBll;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Video;
import com.zdt.zyellowpage.util.ImageListAdapter;

public class VideoListActivity extends AbActivity {

	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 0;
	private ImageListAdapter myListViewAdapter = null;
	private String member_id;
	private boolean isRefresh = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		member_id = getIntent().getStringExtra("member_id");
		if (AbStrUtil.isEmpty(member_id)) {
			showToast("参数错误...");
			this.finish();
			return;
		}
		setAbContentView(R.layout.video_list);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.pull_list_name);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		// 获取ListView对象
		mAbPullListView = (AbPullListView) this.findViewById(R.id.mListView);

		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(true);

		// ListView数据
		list = new ArrayList<Map<String, Object>>();

		// 使用自定义的Adapter
		myListViewAdapter = new ImageListAdapter(this, list,
				R.layout.video_list_items, new String[] { "itemsTitle" },
				new int[] { R.id.itemsTitle });
		mAbPullListView.setAdapter(myListViewAdapter);

		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		// 列表上拉刷新，和下拉翻页的事件触发
		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {
			@Override
			public void onRefresh() {
				isRefresh = true;
				currentPage = 0;
				getData();
			}

			@Override
			public void onLoadMore() {
				isRefresh = false;
				currentPage++;
				getData();
			}

		});
	}

	/**
	 * 从网络获取数据，包括下拉刷新，和上拉翻页
	 */
	private void getData() {
		AlbumReqEntity params = new AlbumReqEntity(currentPage, 10, member_id);
		new VideoBll().getVideoList(VideoListActivity.this, params,
				new ZzObjectHttpResponseListener<Video>() {

					@Override
					public void onSuccess(int statusCode, List<Video> lis) {
						if (lis == null || lis.size() == 0) {
							newList = null;
							return;
						}
						newList = new ArrayList<Map<String, Object>>();
						Map<String, Object> map = null;
						for (Video video : lis) {
							map = new HashMap<String, Object>();
							map.put("itemsTitle", video.getTitle());
							map.put("url", video.getUrl());
							newList.add(map);
						}
					}

					@Override
					public void onStart() {
						showProgressDialog("请稍候...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Video> localList) {
						if (localList == null || localList.size() == 0) {
							newList = null;
							return;
						}
						newList = new ArrayList<Map<String, Object>>();
						Map<String, Object> map = null;
						for (Video video : localList) {
							map = new HashMap<String, Object>();
							map.put("itemsTitle", video.getTitle());
							map.put("url", video.getUrl());
							newList.add(map);
						}
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
						list.addAll(newList);
						myListViewAdapter.notifyDataSetChanged();
						newList.clear();
						removeProgressDialog();
						if (isRefresh) {
							mAbPullListView.stopRefresh();
						} else {
							mAbPullListView.stopLoadMore(true);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

}
