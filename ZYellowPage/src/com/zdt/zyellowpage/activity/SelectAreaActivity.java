package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.SharedPreferences.Editor;
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
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AreaBll;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.select_area);
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.selectArea);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		spiProvince = (Spinner) findViewById(R.id.spiProvince);
		spiCity = (Spinner) findViewById(R.id.spiCity);
		spiCoutny = (Spinner) findViewById(R.id.spiCoutny);
		btnConfirm = (Button) findViewById(R.id.selectAreaBtn);
		tvCurrentAreaName = (TextView) findViewById(R.id.tvCurrentArea);
		tvCurrentAreaName.setText(application.cityName);

		initArea("0", spiProvince, adapterProvince);

		spiProvince.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取键的方法：mySpinner.getSelectedItem().toString()或((Dict)mySpinner.getSelectedItem()).getId()
				// 获取值的方法：((Dict)mySpinner.getSelectedItem()).getText();
				// showToast(((Area) spiProvince.getSelectedItem()).getId());
				initArea(((Area) spiProvince.getSelectedItem()).getId(),
						spiCity, adapterCity);
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spiCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取键的方法：mySpinner.getSelectedItem().toString()或((Dict)mySpinner.getSelectedItem()).getId()
				// 获取值的方法：((Dict)mySpinner.getSelectedItem()).getText();
				// showToast(((Area) spiProvince.getSelectedItem()).getId());
				initArea(((Area) spiCity.getSelectedItem()).getId(), spiCoutny,
						adapterCounty);
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Area selectedArea = (Area) spiCoutny.getSelectedItem();
				if (selectedArea == null) {
					showToast("没有正确选择区域，请重新选择");
					return;
				} else {
					if (selectedArea.getId().startsWith("-")) {
						selectedArea = (Area) spiCity.getSelectedItem();
					}
					application.cityid = selectedArea.getId();
					application.cityName = selectedArea.getName();
				}

				// 保存到配置文件
				Editor editor = abSharedPreferences.edit();
				editor.putString(Constant.CITYID, application.cityid);
				editor.putString(Constant.CITYNAME, application.cityName);
				showToast(application.cityid + application.cityName);
				editor.commit();
				SelectAreaActivity.this.finish();
			}
		});
	}

	/**
	 * 如果地区表还比没有数据，需要下载数据
	 */
	private void initArea(final String parentId, final Spinner spinner,
			ArrayAdapter<Area> mAdapter) {

		new AreaBll().getAreaList(this, parentId,
				new ZzObjectHttpResponseListener<Area>() {

					@Override
					public void onSuccess(int statusCode, List<Area> lis) {
						if (spinner.equals(spiCoutny)) {
							Area area = new Area();
							area.setId("-" + parentId);
							area.setParentId(parentId);
							area.setName("全部");
							lis.add(0, area);
						}

						ArrayAdapter<Area> adapter = new ArrayAdapter<Area>(
								SelectAreaActivity.this,
								android.R.layout.simple_spinner_item, lis);

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
						if (spinner.equals(spiCoutny)) {
							Area area = new Area();
							area.setId("-" + parentId);
							area.setParentId(parentId);
							area.setName("全部");
							localList.add(0, area);
						}

						ArrayAdapter<Area> adapter = new ArrayAdapter<Area>(
								SelectAreaActivity.this,
								android.R.layout.simple_spinner_item, localList);

						spinner.setAdapter(adapter);
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}
				});

	}

}
