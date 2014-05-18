package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.view.sliding.AbSlidingTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.fragment.FragmentBuy;
import com.zdt.zyellowpage.activity.fragment.FragmentSell;

public class CompanyBuySellActivity extends AbActivity {
	private AbSlidingTabView mAbSlidingTabView;
	private String member_Id;
	private String fullName;
	AbTitleBar mAbTitleBar;
	private boolean isEdit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_companybuysell);
		// application = (MyApplication) abApplication;
		if (getIntent().getExtras() != null) {
			member_Id = (String) getIntent().getExtras().get("MEMBER_ID");
			fullName = (String) getIntent().getExtras().get("FUllNAME");
			isEdit = getIntent().getBooleanExtra("isEdit", false);
		}
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(fullName);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);
		initTitleRightLayout();
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		mAbSlidingTabView = (AbSlidingTabView) findViewById(R.id.mAbSlidingTabView);

		// 缓存数量
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);

		FragmentSell page1 = new FragmentSell();
		Bundle bundle = new Bundle();
		bundle.putString("MEMBERID", member_Id);
		// 向detailFragment传入参数
		page1.setArguments(bundle);

		FragmentBuy page2 = new FragmentBuy();
		// 向detailFragment传入参数
		page2.setArguments(bundle);

		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(page1);
		mFragments.add(page2);

		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("供应信息");
		tabTexts.add("求购信息");

		mAbSlidingTabView.setTabColor(Color.BLACK);
		mAbSlidingTabView.setTabSelectColor(getResources().getColor(
				R.color.orange));

		mAbSlidingTabView.addItemViews(tabTexts, mFragments);

		mAbSlidingTabView.setTabLayoutBackground(R.drawable.slide_top);
	}

	private void initTitleRightLayout() {
		if (isEdit) {
			View rightViewMore = mInflater.inflate(R.layout.more_btn, null);
			mAbTitleBar.addRightView(rightViewMore);

			Button btnAdd = (Button) rightViewMore.findViewById(R.id.moreBtn);
			btnAdd.setBackgroundResource(R.color.transparent);
			btnAdd.setText(" 添加  ");
			mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
			btnAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CompanyBuySellActivity.this,
							EditSupplyDemandActivity.class);
					startActivity(intent);
				}
			});
		}
	}
}
