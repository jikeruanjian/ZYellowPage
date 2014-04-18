package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.BuySellContentActivity;
import com.zdt.zyellowpage.activity.ImagePagerActivity;
import com.zdt.zyellowpage.bll.SupplyDemandBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.SupplyDemandReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;
import com.zdt.zyellowpage.util.ImageListAdapter;

public class FragmentBuy extends Fragment {
	private MyApplication application;
	private AbActivity mActivity = null;
	private List<Map<String,Object>> list = null;
	private List<SupplyDemand> SupplyDemandList = null;
	private List<SupplyDemand> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 1;
	private boolean isRefresh = true;
	private   ListAdapter myListViewAdapter = null;
	private String member_Id;
	SimpleAdapter adapter;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			 member_Id=getArguments().getString("MEMBERID");
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		 mActivity = (AbActivity) this.getActivity();
		 application = (MyApplication) mActivity.getApplication();
		 View view = inflater.inflate(R.layout.pull_list, null);
		 //获取ListView对象
         mAbPullListView = (AbPullListView)view.findViewById(R.id.mListView);
      // 打开关闭下拉刷新加载更多功能
 		 mAbPullListView.setPullRefreshEnable(true);
 		 mAbPullListView.setPullLoadEnable(true);

         //ListView数据
    	 list = new ArrayList<Map<String,Object>>();
    	 SupplyDemandList = new ArrayList<SupplyDemand>();
    	 newList = new ArrayList<SupplyDemand>();
    	// supplyDemandList = new ArrayList<SupplyDemand>();
    	
    	adapter = new SimpleAdapter(mActivity,list,R.layout.text_item, 
                 new String[]{"textViewSellBuyItemNames"}, 
                 new int[]{R.id.textViewSellBuyItemName}); 
    	 mAbPullListView.setAdapter(adapter);
    	/* mAbPullListView.setAdapter(new ArrayAdapter<String> (mActivity,
    			 android.R.layout.simple_expandable_list_item_1,list));*/
    	 //item被点击事件
    	 mAbPullListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//mActivity.showToast(SupplyDemandList.get(position-1).getItem_id());
				Intent intent = new Intent(mActivity,
						BuySellContentActivity.class);
				intent.putExtra("ITEMID", SupplyDemandList.get(position-1).getItem_id());
				startActivity(intent);
			}
    	 });

 		/**
 		 * 添加事件
 		 */
 		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

 			@Override
 			public void onRefresh() {
 				//改写成执行查询
 				//mAbTaskQueue.execute(item1);
 				isRefresh = true;
 				SupplyDemandList.clear();
 				getData(0);
 			}

 			@Override
 			public void onLoadMore() {
 				//mAbTaskQueue.execute(item2);
 				isRefresh = false;
 				getData(++currentPage);
 			}

 		});

 		getData(0);
	    
		 return view;
		
	}

	/**
	 * 获取数据
	 */
	private void getData(int i){
		SupplyDemandBll bll = new SupplyDemandBll();
		SupplyDemandReqEntity supplyDemandParams = new SupplyDemandReqEntity(i,10,member_Id,"1");
		bll.getSupplyDemandList(mActivity, supplyDemandParams, 
				new ZzObjectHttpResponseListener<SupplyDemand>(){

			@Override
			public void onSuccess(int statusCode, List<SupplyDemand> lis) {
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					mActivity.showToast("没有更多数据！");
					return;
				}
				newList.addAll(lis);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<SupplyDemand> localList) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onErrorData(String status_description) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				SupplyDemandList.addAll(newList);
				int len = newList.size();
			//	 mAbPullListView.removeAllViews();
				newList.clear();
				list.clear();
				for(SupplyDemand s:SupplyDemandList ){
					 Map<String, Object> map = new HashMap<String, Object>(); 
					 map.put("textViewSellBuyItemNames", s.getTitle());
					list.add(map);
				}
				adapter. notifyDataSetChanged( );
		    	 //item被点击事件
		    	
				mActivity.removeProgressDialog();
				
				if(isRefresh){
					mAbPullListView.stopRefresh();
				}
				else{
					if(len == 10){
						mAbPullListView.stopLoadMore(true);
					}
					else{
						mAbPullListView.stopLoadMore(false);
					}
				}
			}
			
		});
	}
}
