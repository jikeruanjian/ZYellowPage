package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.MyApplication;

public class MyResourceActivity extends AbActivity {
	private MyApplication application;
	private List<Map<String, Object>> list = null;
	private SimpleAdapter mAdapter = null;
	private ListView listView = null;
	private String[] perSonItems = new String[] { "基本信息", "设置头像", "个人简介",
			"成功案例", "我的特长", "资质管理" };
	private String[] companyItems = new String[] { "基本信息", "设置Logo", "供求信息",
			"图片展示", "更多电话", "地图坐标" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.my_resource);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("我的资料");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		application = (MyApplication) abApplication;

		listView = (ListView) findViewById(R.id.mListView);

		// ListView数据
		list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if (application.mUser.getType() == 0) {
			for (String item : companyItems) {
				map = new HashMap<String, Object>();
				map.put("itemsTitle", item);
				list.add(map);
			}
		} else {
			for (String item : perSonItems) {
				map = new HashMap<String, Object>();
				map.put("itemsTitle", item);
				list.add(map);
			}
		}

		// 使用自定义的Adapter
		mAdapter = new SimpleAdapter(this, list, R.layout.myresource_item,
				new String[] { "itemsTitle" }, new int[] { R.id.itemsTitle });
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedItem = list.get(position).get("itemsTitle")
						.toString();
				if (selectedItem.equals("基本信息")) {
					Intent intent = null;
					if (application.mUser.getType() == 0) {
						intent = new Intent(MyResourceActivity.this,
								EditCompanyBaseResourceActivity.class);
					} else {
						intent = new Intent(MyResourceActivity.this,
								EditPersonBaseResourceActivity.class);
					}
					startActivity(intent);
				} else if (selectedItem.equals("设置头像")
						|| selectedItem.equals("设置Logo")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							AddPhotoActivity.class);
					intent.putExtra("title", selectedItem);
					startActivity(intent);
				} else if (selectedItem.equals("个人简介")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content", application.mUser.getSummary());
					intent.putExtra("type", "summary");
					startActivity(intent);
				} else if (selectedItem.equals("成功案例")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content",
							application.mUser.getExperience());
					intent.putExtra("type", "experience");
					startActivity(intent);

				} else if (selectedItem.equals("我的特长")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content", application.mUser.getSpecialty());
					intent.putExtra("type", "specialty");
					startActivity(intent);
				} else if (selectedItem.equals("资质管理")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							CertificateListActivity.class);
					intent.putExtra("MEMBER_ID",
							application.mUser.getMember_id());
					startActivity(intent);
				} else if (selectedItem.equals("详细介绍")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content", application.mUser.getSummary());
					intent.putExtra("type", "summary");
					startActivity(intent);
				} else if (selectedItem.equals("优惠信息")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content", application.mUser.getDiscount());
					intent.putExtra("type", "discount");
					startActivity(intent);
				} else if (selectedItem.equals("经营范围")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							RichTextEditorActivity.class);
					intent.putExtra("title", selectedItem);
					intent.putExtra("content", application.mUser.getScope());
					intent.putExtra("type", "scope");
					startActivity(intent);
				} else if (selectedItem.equals("供求信息")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							CompanyBuySellActivity.class);
					intent.putExtra("FUllNAME", selectedItem);
					intent.putExtra("MEMBER_ID",
							application.mUser.getMember_id());
					intent.putExtra("isEdit", true);
					startActivity(intent);
				} else if (selectedItem.equals("图片展示")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							EditAlbumActivity.class);
					intent.putExtra("title", selectedItem);
					startActivity(intent);
				} else if (selectedItem.equals("更多电话")) {
					Intent intent = null;
					intent = new Intent(MyResourceActivity.this,
							MorePhoneActivity.class);
					intent.putExtra("MEMBER_ID",
							application.mUser.getMember_id());
					intent.putExtra("isEdit", true);
					startActivity(intent);
				} else if (selectedItem.equals("地图坐标")) {
					startActivity(new Intent(MyResourceActivity.this,
							EditCoordinateAcitivity.class));
				}
			}
		});
	}

}
