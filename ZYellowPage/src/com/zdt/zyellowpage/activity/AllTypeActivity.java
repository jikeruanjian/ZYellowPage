package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.fragment.FragmentAllCompanyType;
import com.zdt.zyellowpage.activity.fragment.FragmentAllPersonType;
import com.zdt.zyellowpage.dao.HotKeyWorkDao;
import com.zdt.zyellowpage.model.HotKeyWord;

public class AllTypeActivity extends AbActivity {
	private GridView gridview;
	private AbTitleBar mAbTitleBar = null;
	private String[] commonType;

	RadioButton radioBtn;
	RadioButton radioBtnP;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private FragmentAllCompanyType newFragmentCompany = null;
	private FragmentAllPersonType newFragmentPerson = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.all_class_activity);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("全部分类");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		gridview = (GridView) findViewById(R.id.commonClassGridView);

		newFragmentCompany = new FragmentAllCompanyType();
		newFragmentPerson = new FragmentAllPersonType();

		fragmentManager = this.getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragmentCompanyType, newFragmentCompany,
				"Company");
		fragmentTransaction.add(R.id.fragmentPersonType, newFragmentPerson,
				"Person");
		fragmentTransaction.commit();
		this.findViewById(R.id.fragmentPersonType).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentCompanyType).setVisibility(View.VISIBLE);
		initCommonClassGridView();
		initRadioBtn();
	}

	/**
	 * 初始化常用分类
	 */
	private void initCommonClassGridView() {
		// 生成动态数组，并且转入数据
		HotKeyWorkDao hotKeyWorkDao = new HotKeyWorkDao(AllTypeActivity.this);
		hotKeyWorkDao.startReadableDatabase(false);
		List<HotKeyWord> list = hotKeyWorkDao.queryList();
		hotKeyWorkDao.closeDatabase(false);
		if (list == null) {
			return;
		}
		commonType = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			commonType[i] = list.get(i).getName();
		}
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.layout_pressimg);// 添加图像资源的ID
			map.put("ItemText", commonType[i]);// 按序号做ItemText
			lstImageItem.add(map);
		}
		// 添加并且显示
		gridview.setAdapter(new CommonAdapter());
		// 添加消息处理
		gridview.setOnItemClickListener(new ItemClickListener());
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件

	class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// Toast.makeText(AllTypeActivity.this, commonType[arg2],
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AllTypeActivity.this,
					PopBusinessListActivity.class);
			intent.putExtra("Type", commonType[arg2]);
			intent.putExtra("TypeId", commonType[arg2]);
			AllTypeActivity.this.startActivity(intent);
		}

	}

	class CommonAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return commonType.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.gridview_item, parent,
					false);
			TextView text = (TextView) convertView.findViewById(R.id.ItemText);
			text.setText(commonType[position]);
			convertView.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.MATCH_PARENT, 100));
			convertView.setPadding(-10, -10, -10, -10);
			return convertView;
		}
	}

	private void initRadioBtn() {
		radioBtn = ((RadioButton) this.findViewById(R.id.companyRadio_button));
		radioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AllTypeActivity.this.findViewById(R.id.fragmentPersonType)
						.setVisibility(View.VISIBLE);
				AllTypeActivity.this.findViewById(R.id.fragmentCompanyType)
						.setVisibility(View.GONE);
			}
		});
		radioBtnP = ((RadioButton) this.findViewById(R.id.personRadio_button));
		radioBtnP.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AllTypeActivity.this.findViewById(R.id.fragmentCompanyType)
						.setVisibility(View.VISIBLE);
				AllTypeActivity.this.findViewById(R.id.fragmentPersonType)
						.setVisibility(View.GONE);
			}
		});
	}
}
