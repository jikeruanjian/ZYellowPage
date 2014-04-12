package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import com.zdt.zyellowpage.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ab.activity.AbActivity;
import com.ab.view.sliding.AbSlidingTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.activity.fragment.FragmentBuy;
import com.zdt.zyellowpage.activity.fragment.FragmentSell;
import com.zdt.zyellowpage.global.MyApplication;

public class CompanyBuySellActivity  extends AbActivity {
	private MyApplication application;
	private AbSlidingTabView mAbSlidingTabView;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_companybuysell);
		application = (MyApplication) abApplication;
		
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("供求信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		initTitleRightLayout();

		mAbSlidingTabView = (AbSlidingTabView) findViewById(R.id.mAbSlidingTabView);
		
		//缓存数量
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);
		
	
		FragmentSell page1 = new FragmentSell();
		FragmentBuy page2 = new FragmentBuy();
		
		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(page1);
		mFragments.add(page2);
		
		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("供应信息");
		tabTexts.add("求购信息");
		
		mAbSlidingTabView.setTabColor(Color.BLACK);
		mAbSlidingTabView.setTabSelectColor(Color.rgb(86, 186, 70));
		
		mAbSlidingTabView.addItemViews(tabTexts, mFragments);
		
		mAbSlidingTabView.setTabLayoutBackground(R.drawable.slide_top);
	}
	
	
	

	@Override
	protected void onStart() {
		super.onStart();
		
	}




	private void initTitleRightLayout() {

	}
}
