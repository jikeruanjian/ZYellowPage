package com.zdt.zyellowpage.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.dao.CategoryDao;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.util.CategoryExpandAdapter;

public class FragmentAllPersonType extends Fragment {
	private View view;
	private AbActivity mActivity = null;
	ExpandableListAdapter adapter;
	ExpandableListView expandableListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) this.getActivity();
		view = inflater.inflate(R.layout.all_persontype_fragment, null);
		initView();
		return view;
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		CategoryDao categoryDao = new CategoryDao(mActivity);
		categoryDao.startReadableDatabase(true);
		List<Category> lisAll = categoryDao.queryList("Type = ? ",
				new String[] { "1" });
		categoryDao.closeDatabase(true);

		adapter = new CategoryExpandAdapter(lisAll, mActivity);

		expandableListView = (ExpandableListView) view
				.findViewById(R.id.personListExpand);
		expandableListView.setAdapter(adapter);

		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Category selectedChild = (Category) adapter.getChild(
						groupPosition, childPosition);

				Toast.makeText(mActivity, "你点击了" + selectedChild.getName(),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mActivity,
						PopBusinessListActivity.class);
				intent.putExtra("Type", selectedChild.getName());
				intent.putExtra("TypeId", "list-" + selectedChild.getId());
				mActivity.startActivity(intent);
				return false;
			}
		});

		expandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						for (int i = 0; i < adapter.getGroupCount(); i++) {
							if (groupPosition != i) {
								expandableListView.collapseGroup(i);
							}
						}
					}
				});

	}

}
