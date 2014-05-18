package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentAllPersonType  extends Fragment{
	private View view;
	private AbActivity mActivity = null;
	private String[][] generals;
	ExpandableListAdapter adapter;
	ExpandableListView expandableListView;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) this.getActivity();
		view = inflater.inflate(R.layout.all_persontype_fragment, null);
		initData();
		initView();
		return view;
	}
	
	private  void initData(){
		int x= MainActivity.listCategoryP.size();
		generals = new String[x][];
		for(int i = 0;i<x;i++){
			generals[i]=getArrayData(MainActivity.listChildCategoryP.get(i));
		}
	}
	
	/**
	 * 抽取分类数组名称
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
	 * 初始化界面
	 */
	private  void initView(){
		//initData();
		adapter = new BaseExpandableListAdapter() {
            //设置组视图的显示文字
            private String[] generalsTypes = MainActivity.listCategoryNameP.toArray(new String[0]);
            
            
            //自己定义一个获得文字信息的方法
            TextView getTextView() {
            	   AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                           ViewGroup.LayoutParams.FILL_PARENT, 64);
                   TextView textView = new TextView(
                   		mActivity );
                   textView.setLayoutParams(lp);
                   textView.setGravity(Gravity.CENTER_VERTICAL);
                   textView.setPadding(100, 0, 0, 0);
                   textView.setTextSize(14);
                   textView.setTextColor(R.color.propertyvalue); 
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
            	//generalsC.add(getRightData(MainActivity.listCategoryP.get(groupPosition).getId(),"1"));
            	//generals[groupPosition] = getArrayData(generalsC.get(groupPosition));
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
            //	generalsC.add(getRightData(MainActivity.listCategoryP.get(groupPosition).getId(),"1"));
            	//generals[groupPosition] = getArrayData(generalsC.get(groupPosition));
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

       expandableListView = (ExpandableListView) view.findViewById(R.id.personListExpand);
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
