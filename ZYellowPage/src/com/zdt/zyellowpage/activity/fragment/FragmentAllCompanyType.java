package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.MainActivity;
import com.zdt.zyellowpage.bll.CategoryBll;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Category;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class FragmentAllCompanyType extends Fragment{
	private AbActivity mActivity = null;
	View view;
	  //子视图显示文字
		private List<List<Category>> generalsC;
		private String[][] generals;
		List<Category> lowerType;
		ExpandableListAdapter adapter;
		ExpandableListView expandableListView;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) this.getActivity();

		view = inflater.inflate(R.layout.all_companytype_fragment, null);
		initView();
		initData();
		
		
		
		
		return view;
	}
	private  void initData(){
		generalsC = new ArrayList<List<Category>>();
		int x= MainActivity.listCategory.size();
		generals = new String[x][];
		for(int i = 0;i<x;i++){
			generalsC.add(getRightData(MainActivity.listCategory.get(i).getId(),"1"));
			generals[i] = new String[]{"测试1","测试2"};
					//getArrayData(generalsC.get(i));new 
		}
		Log.e("Company","----"+generalsC.size());
	}
	
	/**
	 * 抽取分类数组
	 * @param array
	 * @return
	 */
	private  String[] getArrayData(List<Category> array ){
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			list.add( array.get(i).getName());
		}
		return list.toArray(new String[0]);
	}
	
	/**
	 * 获取二级分类
	 * @param oId
	 * @param type
	 * @return
	 */
	List<Category> getRightData(String oId,String type){
		 lowerType = new ArrayList<Category>();
	    	CategoryBll categoryBll = new CategoryBll();
			categoryBll.getCategoryist(mActivity,oId, type, new ZzObjectHttpResponseListener<Category>(){
				@Override
				public void onSuccess(int statusCode, List<Category> lis) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
					if (lis == null || lis.size() == 0) {
						return;
					}
					lowerType.addAll(lis);
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
					((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				}
				
			});
			return lowerType;
	    }
	/**
	 * 初始化界面
	 */
	private  void initView(){
		adapter = new BaseExpandableListAdapter() {
            //设置组视图的显示文字
            private String[] generalsTypes = MainActivity.listCategoryName.toArray(new String[0]);
            //自己定义一个获得文字信息的方法
            TextView getTextView() {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, 64);
                TextView textView = new TextView(
                		mActivity );
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(36, 0, 0, 0);
                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            
            //重写ExpandableListAdapter中的各个方法
            @Override
            public int getGroupCount() {
                // TODO Auto-generated method stub
                return generalsTypes.length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                // TODO Auto-generated method stub
                return generalsTypes[groupPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                // TODO Auto-generated method stub
                return groupPosition;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition].length;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return generals[groupPosition][childPosition];
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(
                		mActivity);
                ll.setOrientation(0);
               
                TextView textView = getTextView();
                textView.setTextColor(Color.BLACK);
                textView.setText(getGroup(groupPosition).toString());
                ll.addView(textView);

                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(
                		mActivity );
                ll.setOrientation(0);
                
                TextView textView = getTextView();
                textView.setText(getChild(groupPosition, childPosition)
                        .toString());
                ll.addView(textView);
                return ll;
            }

            @Override
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }

        };

        expandableListView = (ExpandableListView) view.findViewById(R.id.companyListExpand);
        expandableListView.setAdapter(adapter);
        
        
        //设置item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                Toast.makeText(
                		mActivity,
                        "你点击了" + adapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        });

	}
}
