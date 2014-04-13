package com.zdt.zyellowpage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.task.AbTaskQueue;
import com.ab.view.pullview.AbPullView;
import com.baidu.mapapi.BMapManager;

import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.fragment.FragmentHomePage;
import com.zdt.zyellowpage.activity.fragment.FragmentMore;
import com.zdt.zyellowpage.activity.fragment.FragmentNearMap;
import com.zdt.zyellowpage.activity.fragment.FragmentUser;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.util.DisplayUtil;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends AbActivity implements OnCheckedChangeListener {

	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	FragmentHomePage newFragmentHome = null;
	FragmentNearMap newFragmentNearMap = null;
	FragmentUser newFragmentUser = null;
	FragmentMore newFragmentMore = null;
	public static BMapManager mBMapMan = null;
	private AbPullView mAbPullView = null;
	private AbTaskQueue mAbTaskQueue = null;
	TextView textViewArea;
	private MyApplication application;
	EditText editRearch;
	boolean isFirst = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 10000) {
			if (resultCode == RESULT_OK) {
				textViewArea.setText(application.cityName);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		// initHomePagePullView();
		application = (MyApplication) abApplication;
		mBMapMan = new BMapManager(getApplication());
		// E25ED402F8E85C1714F86CC9042EA1B32BE151B2
		mBMapMan.init("RjlfVWfEcAecRGc5qG8xyLoX", null);
		fragmentManager = this.getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		newFragmentHome = new FragmentHomePage();
		newFragmentUser = new FragmentUser();
		newFragmentMore = new FragmentMore();

		fragmentTransaction.add(R.id.fragmentViewHome, newFragmentHome, "home");
		fragmentTransaction.add(R.id.fragmentViewUser, newFragmentUser, "user");
		fragmentTransaction.add(R.id.fragmentViewMore, newFragmentMore, "more");
		
		fragmentTransaction.commit();
		initChangeEvent();
	}

	protected void initOtherFragment() {

		fragmentTransaction = fragmentManager.beginTransaction();
		newFragmentNearMap = new FragmentNearMap();
		fragmentTransaction.add(R.id.fragmentViewNear, newFragmentNearMap,
				"near");
		fragmentTransaction.commit();
		
	}

	protected void initView() {
		DisplayUtil displayUtil = DisplayUtil.getInstance(this);
		;

		/**
		 * 获取当前屏幕的像素值
		 */
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		int high = metric.heightPixels / 6;
		displayUtil.setViewLayoutParamsR(
				this.findViewById(R.id.titileLinearLayout), 0, high / 2);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.LinearLayoutAllXX),0,
		// 5*high);
		displayUtil.setViewLayoutParamsR(this.findViewById(R.id.main_radio), 0,
				high / 2);

		textViewArea = (TextView) this.findViewById(R.id.textViewarea);
		textViewArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SelectAreaActivity.class);
				startActivityForResult(intent, 10000);
			}

		});
		editRearch = (EditText) this.findViewById(R.id.et_searchtext_search);
		editRearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(MainActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					Intent intent = new Intent(MainActivity.this,
							TypeBusinessListActivity.class);
					intent.putExtra("Type", editRearch.getText().toString());
					startActivity(intent);
				}
				return false;
			}

		});

	}

	protected void onDestroy() {
		super.onDestroy();
		if (mBMapMan != null) {
			mBMapMan.stop();
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}

	void initChangeEvent() {
		((RadioButton) this.findViewById(R.id.radio_buttonHome))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonNear))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonUser))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonMore))
				.setOnCheckedChangeListener(this);
		((RadioButton) this.findViewById(R.id.radio_buttonHome))
				.setChecked(true);
		isFirst = true;

	}

	void goneTitileView() {
		this.findViewById(R.id.fragmentViewHome).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewNear).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewUser).setVisibility(View.GONE);
		this.findViewById(R.id.fragmentViewMore).setVisibility(View.GONE);
		this.findViewById(R.id.homePageTitileLinearLayou).setVisibility(
				View.GONE);
		this.findViewById(R.id.mapTitileLinearLayou).setVisibility(View.GONE);
		this.findViewById(R.id.userTitileLinearLayout).setVisibility(View.GONE);
		this.findViewById(R.id.moreTitileLinearLayout).setVisibility(View.GONE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_buttonHome:{
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(true);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewHome).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.homePageTitileLinearLayou)
						.setVisibility(View.VISIBLE);

				Log.e("xxxx", "-----------+1");
				break;
			}
			case R.id.radio_buttonNear:{
				if (isFirst) {
					initOtherFragment();
					isFirst = false;
				}
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(true);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewNear).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.mapTitileLinearLayou).setVisibility(
						View.VISIBLE);

				Log.e("xxxx", "-----------+2");
				break;
			}
			case R.id.radio_buttonUser:{
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(true);
				this.findViewById(R.id.radio_buttonMore).setSelected(false);
				this.findViewById(R.id.fragmentViewUser).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.userTitileLinearLayout).setVisibility(
						View.VISIBLE);

				Log.e("xxxx", "-----------+3");
				break;
			}
			case R.id.radio_buttonMore:{
				goneTitileView();
				this.findViewById(R.id.radio_buttonHome).setSelected(false);
				this.findViewById(R.id.radio_buttonNear).setSelected(false);
				this.findViewById(R.id.radio_buttonUser).setSelected(false);
				this.findViewById(R.id.radio_buttonMore).setSelected(true);
				this.findViewById(R.id.fragmentViewMore).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.moreTitileLinearLayout).setVisibility(
						View.VISIBLE);

				Log.e("xxxx", "-----------+4");
				break;
			}
			default:
				break;
			}
		}
	}
}
