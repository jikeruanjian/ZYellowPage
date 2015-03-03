package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.dao.CategoryDao;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.util.CategoryExpandAdapter;

public class CompanytypeActivity extends AbActivity {

	View view;
	private AbTitleBar mAbTitleBar = null;
	// 子视图显示文字
	ExpandableListAdapter adapter;
	ExpandableListView expandableListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.all_companytype_fragment);
		InitTitleView();
		initTypeView();
	}

	/**
	 * 初始化界面
	 */
	private void initTypeView() {
		CategoryDao categoryDao = new CategoryDao(CompanytypeActivity.this);
		categoryDao.startReadableDatabase(false);
		List<Category> lisAll = categoryDao.queryList("Type = ? ",
				new String[] { "0" });
		// Log.e("FragmentAllCompanyType ", "商家分类数量"+lisAll.size());
		categoryDao.closeDatabase(false);

		adapter = new CategoryExpandAdapter(lisAll, CompanytypeActivity.this);

		expandableListView = (ExpandableListView) this
				.findViewById(R.id.companyListExpand);
		expandableListView.setAdapter(adapter);

		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Category selectedChild = (Category) adapter.getChild(
						groupPosition, childPosition);
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("TypeId", selectedChild.getId());
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
				CompanytypeActivity.this.finish();
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

	private void InitTitleView() {
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("选择分类");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.clearRightView();
		TextView tvSave = new TextView(this);
		tvSave.setText("全部 ");
		tvSave.setTextColor(Color.WHITE);
		tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("TypeId", "all");
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
				CompanytypeActivity.this.finish();
			}
		});
		mAbTitleBar.addRightView(tvSave);
	}
}