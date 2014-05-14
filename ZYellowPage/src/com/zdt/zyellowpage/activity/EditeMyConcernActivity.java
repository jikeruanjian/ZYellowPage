package com.zdt.zyellowpage.activity;

import android.os.Bundle;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.MyApplication;

public class EditeMyConcernActivity extends AbActivity{
	private MyApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edite_my_base_resource);
		
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改资料");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		
		
		}
	
	
	
	
}
