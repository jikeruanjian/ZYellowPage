package com.zdt.zyellowpage.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;


import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.util.ImageListAdapter;
import com.zdt.zyellowpage.util.ImageListAdapterC;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentSell  extends Fragment {
	private MyApplication application;
	private Activity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private int currentPage = 1;
	private com.ab.task.AbTaskQueue mAbTaskQueue = null;
	private ArrayList<String> mPhotoList = new ArrayList<String>();
	private ImageListAdapter myListViewAdapter = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		 mActivity = this.getActivity();
		 application = (MyApplication) mActivity.getApplication();
		 
		 View view = inflater.inflate(R.layout.pull_list, null);
		return view;
	}


}
