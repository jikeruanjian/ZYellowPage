package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.model.SupplyDemand;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

public class MyPopupWindow {
	public TextView button;
	public PopupWindow popupWindow;
	public ListView listViewClassB;//商家第一级分类
	public ListView listViewClassP;//个人第一级分类
	public ListView listViewClassLower;//第二级分类
	SimpleAdapter adapter;
	private List<Map<String,Object>> nameList = null;
	List<Category> listLowerCategory;
	public TextView tVBussniess;
	public TextView tVPerson;
	private Context mContext;
	private RelativeLayout layout;
	public LinearLayout layoutLeft;
	public LinearLayout layoutRight;
	MyAdapterB myAdapterB;
	MyAdapterP myAdapterP;
	private View changeView ;
	private View changeViewP ;
	public LinearLayout layoutBClass;
	public LinearLayout layoutPClass;

	public MyPopupWindow( Context context){
		
		layout = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.allclass_windowspop, null);
		mContext = context;
		layoutLeft = (LinearLayout) layout.findViewById(R.id.LeftLinearLayoutClass);
		layoutBClass = (LinearLayout)layout.findViewById(R.id.leftLayoutBussniessClass);
		layoutPClass =  (LinearLayout)layout.findViewById(R.id.leftLayoutPersonClass);
		layoutRight = (LinearLayout)layout.findViewById(R.id.rightLinearLayoutClass);
		listViewClassB = (ListView) layout.findViewById(R.id.ListViewBussniessClass);
		listViewClassP =(ListView) layout.findViewById(R.id.ListViewPersonClass);
		listViewClassLower = (ListView) layout.findViewById(R.id.listViewLowerClass);
		tVBussniess = (TextView) layout.findViewById(R.id.textViewBussniessClass);
		tVPerson = (TextView) layout.findViewById(R.id.textViewPersonClass);
		tVBussniess.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tVBussniess.setTextColor(mContext.getResources().getColor(R.color.orange));
				tVPerson.setTextColor(mContext.getResources().getColor(R.color.gray_black));
				tVPerson.setText("个人分类+");
				tVBussniess.setText("商家分类-");
				layout.findViewById(R.id.leftLayoutPersonClass).setVisibility(View.GONE);
				layout.findViewById(R.id.leftLayoutBussniessClass).setVisibility(View.VISIBLE);
				getRightData("0100",changeView);
			}
			
		});
		
		tVPerson.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tVBussniess.setTextColor(mContext.getResources().getColor(R.color.gray_black));
				tVPerson.setTextColor(mContext.getResources().getColor(R.color.orange));
				tVPerson.setText("个人分类-");
				tVBussniess.setText("商家分类+");
				layout.findViewById(R.id.leftLayoutPersonClass).setVisibility(View.VISIBLE);
				layout.findViewById(R.id.leftLayoutBussniessClass).setVisibility(View.GONE);
				getRightData("5000",changeViewP);
			}
			
		});
		changeView = new View(mContext);
		changeViewP = new View(mContext);
		 myAdapterB = new MyAdapterB(mContext);
		listViewClassB.setAdapter(myAdapterB);
		
		changeView.setBackgroundResource(R.color.window_bg);
		listViewClassB.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				getRightData(MainActivity.listCategory.get(arg2).getId(),arg1);
			}
			
		});
		 myAdapterP = new MyAdapterP(mContext);
		listViewClassP.setAdapter(myAdapterP);
		
		changeViewP.setBackgroundResource(R.color.window_bg);
		listViewClassP.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				getRightData(MainActivity.listCategoryP.get(arg2).getId(),arg1);
				
			}
			
		});
		
		listLowerCategory = new ArrayList<Category>();
		nameList = new ArrayList<Map<String,Object>>();
		getRightData("0100",changeView);
		adapter = new SimpleAdapter(mContext,nameList,R.layout.text_item2, 
                new String[]{"textViewSellBuyItemNames"}, 
                new int[]{R.id.textViewSellBuyItemName});
		listViewClassLower.setAdapter(adapter);
		listViewClassLower.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent;
				
				if(layoutPClass.getVisibility() == View.GONE){
					 intent = new Intent(mContext,
					 PopBusinessListActivity.class);
					 intent.putExtra("Type", listLowerCategory.get(arg2).getName());
					 intent.putExtra("TypeId", "list-"+listLowerCategory.get(arg2).getId());
					 mContext.startActivity(intent);
				}
				else{
					intent = new Intent(mContext,
							PopPersonListActivity.class);
					 intent.putExtra("Type", listLowerCategory.get(arg2).getName());
					 intent.putExtra("TypeId", "list-"+listLowerCategory.get(arg2).getId());
					 mContext.startActivity(intent);
				}
				popupWindow.dismiss();
			}
			
		});

		popupWindow = new PopupWindow(context);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);

	}
	
	public final class ViewHolder{ 
        public TextView title; 
        public TextView viewBtn; 
    } 
      
	
    public class MyAdapterB extends BaseAdapter{ 
  
        private LayoutInflater mInflater; 
       
          
        public MyAdapterB(Context context){ 
            this.mInflater = LayoutInflater.from(context); 
            
        } 
        @Override
        public int getCount() { 
            // TODO Auto-generated method stub 
            return MainActivity.listCategory.size(); 
        } 
  
        @Override
        public Object getItem(int arg0) { 
            // TODO Auto-generated method stub 
            return null; 
        } 
  
        @Override
        public long getItemId(int arg0) { 
            // TODO Auto-generated method stub 
            return 0; 
        } 
  
        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
              
            ViewHolder holder = null; 
            if (convertView == null) { 
                holder=new ViewHolder();   
                convertView = mInflater.inflate(R.layout.class_item, null); 
                holder.title = (TextView)convertView.findViewById(R.id.textViewTopClass); 
                holder.viewBtn = (TextView)convertView.findViewById(R.id.imageButtonLowerClass); 
                convertView.setTag(holder); 
                  
            }else { 
                  
                holder = (ViewHolder)convertView.getTag(); 
            } 
            
            holder.title.setText((String)MainActivity.listCategory.get(position).getName()); 
            //holder.title.setOnClickListener(new LeftClassBtnListener(MainActivity.listCategory.get(position).getId())); 
            //holder.viewBtn.setOnClickListener(new LeftClassBtnListener(MainActivity.listCategory.get(position).getId())); 
            
            return convertView; 
        } 
      
          
    } 
    
    public class MyAdapterP extends BaseAdapter{ 
    	  
        private LayoutInflater mInflater; 
           
          
        public MyAdapterP(Context context){ 
            this.mInflater = LayoutInflater.from(context); 
            
        } 
        @Override
        public int getCount() { 
            // TODO Auto-generated method stub 
            return MainActivity.listCategoryP.size(); 
        } 
  
        @Override
        public Object getItem(int arg0) { 
            // TODO Auto-generated method stub 
            return null; 
        } 
  
        @Override
        public long getItemId(int arg0) { 
            // TODO Auto-generated method stub 
            return 0; 
        } 
  
        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
              
            ViewHolder holder = null; 
            if (convertView == null) { 
                holder=new ViewHolder();   
                convertView = mInflater.inflate(R.layout.class_item, null); 
                holder.title = (TextView)convertView.findViewById(R.id.textViewTopClass); 
                holder.viewBtn = (TextView)convertView.findViewById(R.id.imageButtonLowerClass); 
                convertView.setTag(holder); 
                  
            }else { 
                  
                holder = (ViewHolder)convertView.getTag(); 
            } 
            holder.title.setText(MainActivity.listCategoryP.get(position).getName());
         //   holder.title.setOnClickListener(new LeftClassBtnListener(MainActivity.listCategoryP.get(position).getId())); 
         //   holder.viewBtn.setOnClickListener(new LeftClassBtnListener(MainActivity.listCategoryP.get(position).getId()));  
            
            return convertView; 
        } 
        
    } 
    
    /*private class LeftClassBtnListener implements OnClickListener{  
		String oId;  
        public LeftClassBtnListener(String id){  
        	oId= id;  
        }  
        @Override  
        public void onClick(View v) {  
        	//((TextView)v).setTextColor(mContext.getResources().getColor(R.color.orange));
        	getRightData(oId,changeView);
        }
    }*/

    void getRightData(String oId,View v){
    	String type = "0";
    	
    	//myAdapterB.g
    	if(layout.findViewById(R.id.leftLayoutPersonClass).getVisibility() == View.GONE){
    		 type = "0";
    		// changeView.setBackgroundResource(R.color.transparent);
			 //v.setBackgroundResource(R.color.window_bg);
			// changeView = v;
    	}
    	else{
    		 type = "1";
    		// changeViewP.setBackgroundResource(R.color.transparent);
			// v.setBackgroundResource(R.color.window_bg);
			// changeViewP = v;
    	}
    	CategoryBll categoryBll = new CategoryBll();
		categoryBll.getCategoryist(mContext,oId, type, new ZzObjectHttpResponseListener<Category>(){
			@Override
			public void onSuccess(int statusCode, List<Category> lis) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					return;
				}
				listLowerCategory.clear();
				listLowerCategory.addAll(lis);
				nameList.clear();
				for(Category c:listLowerCategory){
					 Map<String, Object> map = new HashMap<String, Object>(); 
					 map.put("textViewSellBuyItemNames", c.getName());
					 nameList.add(map);
				}
				adapter. notifyDataSetChanged();	
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
}
