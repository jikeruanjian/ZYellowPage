package com.zdt.zyellowpage.activity.fragment;


import com.zdt.zyellowpage.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentMore  extends Fragment {
	   @Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		   View view = inflater.inflate(R.layout.test,container,false);
		   TextView text = (TextView)view.findViewById(R.id.textView1test);
		   text.setText("更多");
	        return view;
	   }
}
