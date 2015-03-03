package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.dao.CategoryDao;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Category;

public class SelectCategoryActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;

	@AbIocView(id = R.id.spiMainCategory)
	private Spinner spiMainCategory;
	@AbIocView(id = R.id.spiChildCagetory)
	private Spinner spiChildCategory;

	@AbIocView(id = R.id.btnConfirm)
	private Button btnConfirm;
	@AbIocView(id = R.id.tvCurrentCategory)
	private TextView tvCurrentCategory;

	private ArrayAdapter<Category> adapterMainCategory;
	private ArrayAdapter<Category> adapterChildCategory;
	String currentCategoryName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.select_category);
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.selectCagegory);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// tvCurrentCategory
		// .setText(getIntent().getStringExtra("currentCategory"));
		currentCategoryName = getIntent().getStringExtra("currentCategory");

		spiMainCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取键的方法：mySpinner.getSelectedItem().toString()或((Dict)mySpinner.getSelectedItem()).getId()
				// 获取值的方法：((Dict)mySpinner.getSelectedItem()).getText();
				// showToast(((Area) spiProvince.getSelectedItem()).getId());
				initArea(
						((Category) spiMainCategory.getSelectedItem()).getId(),
						spiChildCategory, adapterChildCategory);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Category selectedCagtegory = (Category) spiChildCategory
						.getSelectedItem();
				if (selectedCagtegory == null) {
					showToast("没有正确选择区域，请重新选择");
					return;
				}
				// 判断空，我就不判断了。。。。
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("selectedCagtegory", selectedCagtegory);
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
				// 关闭掉这个Activity
				finish();
			}
		});

		initArea("0", spiMainCategory, adapterMainCategory);
	}

	/**
	 * 如果地区表还比没有数据，需要下载数据
	 */
	private void initArea(final String parentId, final Spinner spinner,
			ArrayAdapter<Category> mAdapter) {

		CategoryDao categoryDao = new CategoryDao(this);
		categoryDao.startReadableDatabase(true);
		List<Category> lis = categoryDao
				.queryList("parent=? and type=?", new String[] { parentId,
						application.mUser.getType().toString() });
		categoryDao.closeDatabase(true);

		if (lis != null && lis.size() > 0) {
			ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
					SelectCategoryActivity.this,
					R.layout.spinner_display_style, lis);
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
			spinner.setAdapter(adapter);
			return;
		}

		new CategoryBll().getCategoryist(this, parentId,
				String.valueOf(application.mUser.getType()),
				new ZzObjectHttpResponseListener<Category>() {

					@Override
					public void onSuccess(int statusCode, List<Category> lis) {

						ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
								SelectCategoryActivity.this,
								R.layout.spinner_display_style, lis);
						adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
						spinner.setAdapter(adapter);
					}

					@Override
					public void onStart() {
						showProgressDialog();
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Category> localList) {
						showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}
				});
	}
}
