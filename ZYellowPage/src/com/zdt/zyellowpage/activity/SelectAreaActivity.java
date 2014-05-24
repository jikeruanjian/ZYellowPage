package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AreaBll;
import com.zdt.zyellowpage.dao.AreaDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Area;

public class SelectAreaActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;

	private ArrayAdapter<Area> adapterProvince;
	private ArrayAdapter<Area> adapterCity;
	private ArrayAdapter<Area> adapterCounty;
	private Spinner spiProvince;
	private Spinner spiCity;
	private Spinner spiCoutny;
	private Button btnConfirm;
	private TextView tvCurrentAreaName;
	private List<Area> lisCurrentAreas;
	boolean isInit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.select_area);
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.selectArea);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		spiProvince = (Spinner) findViewById(R.id.spiProvince);
		spiCity = (Spinner) findViewById(R.id.spiCity);
		spiCoutny = (Spinner) findViewById(R.id.spiCoutny);
		btnConfirm = (Button) findViewById(R.id.selectAreaBtn);
		tvCurrentAreaName = (TextView) findViewById(R.id.tvCurrentArea);
		tvCurrentAreaName
				.setText(AbStrUtil.isEmpty(application.locateCityName) ? "未能定位"
						: application.locateCityName);
		lisCurrentAreas = getAreaParent();
		initArea("0", spiProvince, adapterProvince);

		spiProvince.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String parent1 = ((Area) spiProvince.getSelectedItem()).getId();
				if (parent1.equals("000000")) {
					spiCity.setVisibility(View.GONE);
					spiCoutny.setVisibility(View.GONE);
				} else {
					spiCity.setVisibility(View.VISIBLE);
					spiCoutny.setVisibility(View.VISIBLE);
					initArea(parent1, spiCity, adapterCity);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spiCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				initArea(((Area) spiCity.getSelectedItem()).getId(), spiCoutny,
						adapterCounty);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Area selectedArea = (Area) spiCoutny.getSelectedItem();

				if (spiCoutny.getVisibility() == View.GONE) {
					selectedArea = (Area) spiProvince.getSelectedItem();
				}
				if (selectedArea == null) {
					showToast("没有正确选择区域，请重新选择");
					return;
				} else {
					if (selectedArea.getId().startsWith("-")) {
						selectedArea = (Area) spiCity.getSelectedItem();
					}
					application.cityid = selectedArea.getId();
					application.cityName = selectedArea.getName().substring(0,
							2);
					if (application.firstStart) {
						Editor editor = abSharedPreferences.edit();
						editor.putBoolean(Constant.FIRSTSTART, false);
						editor.commit();
					}
				}

				// 保存到配置文件
				Editor editor = abSharedPreferences.edit();
				editor.putString(Constant.CITYID, application.cityid);
				editor.putString(Constant.CITYNAME, application.cityName);
				editor.commit();
				setResult(RESULT_OK, null);
				if (application.firstStart) {
					SelectAreaActivity.this.startActivity(new Intent(
							SelectAreaActivity.this, MainActivity.class));
				}

				application.firstStart = false;
				SelectAreaActivity.this.finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (application.firstStart) {
			Editor editor = abSharedPreferences.edit();
			editor.putBoolean(Constant.FIRSTSTART, false);
			editor.commit();
			application.firstStart = false;
			this.startActivity(new Intent(SelectAreaActivity.this,
					MainActivity.class));
			this.finish();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 如果地区表还比没有数据，需要下载数据
	 */
	private void initArea(final String parentId, final Spinner spinner,
			ArrayAdapter<Area> mAdapter) {

		AreaDao areaDao = new AreaDao(this);
		areaDao.startReadableDatabase(false);
		List<Area> areas = areaDao.queryList("parent=?",
				new String[] { parentId });
		areaDao.closeDatabase(false);

		if (areas != null && areas.size() > 0) {
			if (spinner.equals(spiProvince)) {
				Area area = new Area();
				area.setId("000000");
				area.setParent("0");
				area.setName("全国");
				areas.add(0, area);
			} else if (spinner.equals(spiCoutny)) {
				Area area = new Area();
				area.setId("-" + parentId);
				area.setParent(parentId);
				area.setName("全部");
				areas.add(0, area);
			}

			ArrayAdapter<Area> adapter = new ArrayAdapter<Area>(
					SelectAreaActivity.this, R.layout.spinner_display_style,
					areas);
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
			spinner.setAdapter(adapter);

			if (spinner.equals(spiProvince)) {
				if (isInit && lisCurrentAreas != null
						&& lisCurrentAreas.size() >= 1
						&& lisCurrentAreas.get(0) != null) {
					int i = 0;
					for (Area item : areas) {
						if (item.getId().equals(lisCurrentAreas.get(0).getId())) {
							spinner.setSelection(i);
							break;
						}
						i++;
					}
				}
			} else if (spinner.equals(spiCity)) {
				if (isInit && lisCurrentAreas != null
						&& lisCurrentAreas.size() > 1
						&& lisCurrentAreas.get(1) != null) {
					int i = 0;
					for (Area item : areas) {
						if (item.getId().equals(lisCurrentAreas.get(1).getId())) {
							spinner.setSelection(i);
							break;
						}
						i++;
					}
				}
			} else if (spinner.equals(spiCoutny)) {
				if (isInit && lisCurrentAreas != null
						&& lisCurrentAreas.size() > 2
						&& lisCurrentAreas.get(2) != null) {
					int i = 0;
					for (Area item : areas) {
						if (item.getId().equals(lisCurrentAreas.get(2).getId())) {
							spinner.setSelection(i);
							break;
						}
						i++;
					}
				}
				isInit = false;
			}
			return;
		}

		new AreaBll().getAreaList(this, parentId,
				new ZzObjectHttpResponseListener<Area>() {

					@Override
					public void onSuccess(int statusCode, List<Area> lis) {
						if (spinner.equals(spiCoutny)) {
							Area area = new Area();
							area.setId("-" + parentId);
							area.setParent(parentId);
							area.setName("全部");
							lis.add(0, area);
						}

						ArrayAdapter<Area> adapter = new ArrayAdapter<Area>(
								SelectAreaActivity.this,
								R.layout.spinner_display_style, lis);

						adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

						spinner.setAdapter(adapter);
					}

					@Override
					public void onStart() {
						showProgressDialog("请稍候...");
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Area> localList) {
						showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}
				});
	}

	private List<Area> getAreaParent() {
		AreaDao ad = new AreaDao(this);
		ad.startReadableDatabase(false);
		Area area = ad.queryOne(Integer.valueOf(application.cityid));
		List<Area> lisArea = new ArrayList<Area>();
		if (area == null)
			return null;

		if (!area.getParent().equals("0")) {
			Area area2 = ad.queryOne(Integer.valueOf(area.getParent()));
			if (!area2.getParent().equals("0")) {
				Area area3 = ad.queryOne(Integer.valueOf(area2.getParent()));
				lisArea.add(area3);
			}
			lisArea.add(area2);
		}
		ad.closeDatabase(false);
		lisArea.add(area);
		return lisArea;
	}
}
