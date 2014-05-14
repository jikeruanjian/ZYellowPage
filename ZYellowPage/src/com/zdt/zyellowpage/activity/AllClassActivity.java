package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;

public class AllClassActivity extends AbActivity{
	private AbTitleBar mAbTitleBar = null;
	private GridView gridview;
	private String[] commonType;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		 super.onCreate(savedInstanceState);  
	      setContentView(R.layout.all_class_activity);  
	      mAbTitleBar = this.getTitleBar();
			mAbTitleBar.setTitleText("更多");
			mAbTitleBar.setLogo(R.drawable.button_selector_back);
			mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
			mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
			// mAbTitleBar.setLogoLine(R.drawable.line);
			mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
	      gridview = (GridView) findViewById(R.id.commonClassGridView);  
	      
	     
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
	      SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,R.layout.gridview_item,
	      new String[] {"ItemImage","ItemText"}, new int[] {R.id.ItemImage,R.id.ItemText});  
	      //添加并且显示   
	      gridview.setAdapter(saImageItems);  
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
		      
			Toast.makeText(AllClassActivity.this, commonType[arg2], Toast.LENGTH_SHORT).show();  
		}  

	}
}
