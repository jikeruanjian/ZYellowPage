package com.zdt.zyellowpage.activity.fragment;


import com.zdt.zyellowpage.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FragmentMore  extends Fragment {
	   public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		   View view = inflater.inflate(R.layout.test,container,false);
		   TextView text = (TextView)view.findViewById(R.id.textView1test);
	
	        return view;
	   }
}
