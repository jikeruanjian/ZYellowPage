package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.zdt.zyellowpage.bll.AreaBll;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Area;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends AbActivity implements OnCheckedChangeListener {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private FragmentHomePage newFragmentHome = null;
	private FragmentNearMap newFragmentNearMap = null;
	private FragmentUser newFragmentUser = null;
	private FragmentMore newFragmentMore = null;
	public static BMapManager mBMapMan = null;
	private AbPullView mAbPullView = null;
	private AbTaskQueue mAbTaskQueue = null;
	private TextView textViewArea;
	private MyApplication application;
	private EditText editRearch;
	private TextView textVSearch;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	private String types[] = {"商家", "个人"};
	boolean isFirst = false;
	//区域列表
	public static List<Area> listArea;
	public static List<String> listAreaName;
	//商家分类列表
	public  static List<Category> listCategory;
	public static List<String> listCategoryName;
	//个人分类列表
	public  static List<Category> listCategoryP;
	public static List<String> listCategoryNameP;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 10000) {
			if (resultCode == RESULT_OK) {
				textViewArea.setText(application.cityName);
				MainActivity.getAreaList(MainActivity.this, application.cityid);
				newFragmentHome.getData();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (MyApplication) abApplication;
		initView();
		// initHomePagePullView();
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
		listArea = new ArrayList<Area>();
		listCategory = new ArrayList<Category>();
		listAreaName = new ArrayList<String>();
		listCategoryName = new ArrayList<String>();
		listCategoryP = new ArrayList<Category>();
		listCategoryNameP = new ArrayList<String>();
		initChangeEvent();
		
		MainActivity.getAreaList(MainActivity.this, application.cityid);
		MainActivity.getCategoryList(MainActivity.this, "0");
		MainActivity.getCategoryListP(MainActivity.this, "0");
		textVSearch = (TextView) findViewById(R.id.textViewSearchType);
		textVSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//button.getTop();
				int y = textVSearch.getBottom() * 3 / 2;
				//int x = getWindowManager().getDefaultDisplay().getWidth() / 2;
				int x = textVSearch.getRight()+10;
				showPopupWindow(x, y);
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		editRearch.setText("");
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
		//displayUtil.setViewLayoutParamsR(this.findViewById(R.id.titileLinearLayout), 0, high / 2);
		displayUtil.setViewLayoutParamsR(this.findViewById(R.id.titileLinearLayout), 0, 150);
		// displayUtil.setViewLayoutParamsR(this.findViewById(R.id.LinearLayoutAllXX),0,
		// 5*high);
		//displayUtil.setViewLayoutParamsR(this.findViewById(R.id.main_radio), 0,high / 2);
		displayUtil.setViewLayoutParamsR(this.findViewById(R.id.main_radio), 0,160);
		textViewArea = (TextView) this.findViewById(R.id.textViewarea);
		textViewArea.setText(application.cityName);
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
					Intent in;
					if(textVSearch.getText().toString().equals("商家")){
						 in = new Intent(MainActivity.this,
								 PopBusinessListActivity.class);
						 in.putExtra("Type",editRearch.getText().toString());
						 in.putExtra("TypeId",editRearch.getText().toString());
						 startActivity(in);
					}
					else{
						in = new Intent(MainActivity.this,
								PopPersonListActivity.class);
					    in.putExtra("Type", editRearch.getText().toString());
					    in.putExtra("TypeId", editRearch.getText().toString());
						startActivity(in);
					}
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

				//Log.e("xxxx", "-----------+1");
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

				//Log.e("xxxx", "-----------+2");
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

				//Log.e("xxxx", "-----------+3");
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

				//Log.e("xxxx", "-----------+4");
				break;
			}
			default:
				break;
			}
		}
		
	}
	
	/**
	 * 获取市级下属的区县
	 * @param context
	 * @param id ---城市id
	 */
	public static void getAreaList(Context context,String id){
		AreaBll areaBll = new AreaBll();
		areaBll.getAreaList( context, id, new ZzObjectHttpResponseListener<Area>(){

			@Override
			public void onSuccess(int statusCode, List<Area> lis) {
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					return;
				}
				
				listArea.clear();
				listArea.addAll(lis);
				listArea.add(new Area(lis.get(0).getParentId(),"全部区域","0"));
				listAreaName.clear();
				for(Area area:listArea){
					listAreaName.add(area.getName());
				}
				//Log.e("xxxx", "包含的县区个数为-----"+listArea.size());
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<Area> localList) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onErrorData(String status_description) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	/**
	 * 
	 * @param context
	 * @param id 主分类id，为0时获取第一级分类
	 * @param type 0 为商家 1为个人
	 */
	public static void getCategoryList(Context context,String id){
		CategoryBll categoryBll = new CategoryBll();
		categoryBll.getCategoryist(context,id, "0", new ZzObjectHttpResponseListener<Category>(){

			@Override
			public void onSuccess(int statusCode, List<Category> lis) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					return;
				}
				listCategory.clear();
				listCategory.addAll(lis);
				listCategoryName.clear();
				//Log.e("xxxx", "包含的分类个数为-----"+listCategory.size());
				for(Category category:listCategory){
						listCategoryName.add(category.getName());
				}
				
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<Category> localList) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onErrorData(String status_description) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * 
	 * @param context
	 * @param id 主分类id，为0时获取第一级分类
	 * @param type 0 为商家 1为个人
	 */
	public static void getCategoryListP(Context context,String id){
		CategoryBll categoryBll = new CategoryBll();
		categoryBll.getCategoryist(context,id, "1", new ZzObjectHttpResponseListener<Category>(){

			@Override
			public void onSuccess(int statusCode, List<Category> lis) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					return;
				}
				listCategoryP.clear();
				listCategoryP.addAll(lis);
				listCategoryNameP.clear();
				//Log.e("xxxx", "包含的个人分类个数为-----"+listCategoryP.size());
				for(Category category:listCategoryP){
						listCategoryNameP.add(category.getName());
				}
				
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<Category> localList) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onErrorData(String status_description) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(
				R.layout.popdialog, null);
		listView = (ListView) layout.findViewById(R.id.listViewPopW);
		listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.text_itemselect, R.id.textViewSelectItemName,  types));

		popupWindow = new PopupWindow(MainActivity.this);
		
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);;
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		popupWindow.showAsDropDown(textVSearch, x, 10);
		//popupWindow.showAtLocation(findViewById(R.id.LinearLayoutpopwindows), Gravity.LEFT
			//	| Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				textVSearch.setText(types[arg2]);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

}
