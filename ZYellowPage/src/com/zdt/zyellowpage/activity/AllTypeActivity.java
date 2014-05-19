package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.sliding.AbSlidingTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.fragment.FragmentAllCompanyType;
import com.zdt.zyellowpage.activity.fragment.FragmentAllPersonType;
import com.zdt.zyellowpage.global.MyApplication;


public class AllTypeActivity extends AbActivity{
	private GridView gridview;
	private AbTitleBar mAbTitleBar = null;
	private String[] commonType;
	private AbSlidingTabView mAbSlidingTabView;
	private MyApplication application;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		 super.onCreate(savedInstanceState);  
	     setContentView(R.layout.all_class_activity);
	     
	     application = (MyApplication) abApplication;
	    mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("更多");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		//mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		
		
	     gridview = (GridView) findViewById(R.id.commonClassGridView);  
	     mAbSlidingTabView = (AbSlidingTabView) this.findViewById(R.id.mAllTypeAbSlidingTabView);
	     // 缓存数量
	     mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);
	     FragmentAllCompanyType page1 = new FragmentAllCompanyType();
	     FragmentAllPersonType page2 = new FragmentAllPersonType();	
		 List<Fragment> mFragments = new ArrayList<Fragment>();
		 mFragments.add(page1);
		 mFragments.add(page2);
		 List<String> tabTexts = new ArrayList<String>();
		 tabTexts.add("商家人类");
		 tabTexts.add("个人分类");

		 mAbSlidingTabView.setTabColor(R.color.propertyvalue);
		 mAbSlidingTabView.setTabTextSize(14);
		 mAbSlidingTabView.setTabSelectColor(getResources().getColor(
					R.color.orange));

			mAbSlidingTabView.addItemViews(tabTexts, mFragments);

			mAbSlidingTabView.setTabLayoutBackground(R.drawable.slide_top);	
	       initCommonClassGridView();
	     
	  }  
	    
	/**
	 * 初始化常用分类
	 */
	private void initCommonClassGridView(){
		 //生成动态数组，并且转入数据   
		 commonType = new String[]{"电影院","云南菜","火锅","小吃简餐","面包甜点","KTV",
				  "烧烤","西餐","川菜","经济型酒店","四星级酒店","全部"};
	      ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  
	      for(int i=0;i<12;i++)  
	      {  
	        HashMap<String, Object> map = new HashMap<String, Object>();  
	        map.put("ItemImage", R.drawable.layout_pressimg);//添加图像资源的ID   
	        map.put("ItemText", commonType[i]);//按序号做ItemText   
	        lstImageItem.add(map);  
	      }  
	      //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应   
	     // SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,R.layout.gridview_item,
	     // new String[] {"ItemImage","ItemText"}, new int[] {R.id.ItemImage,R.id.ItemText});  
	      
	       
	      //添加并且显示   
	      gridview.setAdapter(new CommonAdapter());  
	      //添加消息处理   
	      gridview.setOnItemClickListener(new ItemClickListener());  
	}
	  //当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件   
	
	  class  ItemClickListener implements OnItemClickListener  
	  {  
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Toast.makeText(AllTypeActivity.this, commonType[arg2], Toast.LENGTH_SHORT).show();  
		}  

	}
	  
	  
	  class CommonAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commonType.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.gridview_item, parent, false);
			TextView text = (TextView)convertView.findViewById(R.id.ItemText);
			text.setText(commonType[position]);
			convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 100));
			convertView.setPadding(-10, -10, -10, -10);
			return convertView ;
		}
		  
	  }
}
