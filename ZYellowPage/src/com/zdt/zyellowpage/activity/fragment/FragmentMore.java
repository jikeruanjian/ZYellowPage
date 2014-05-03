package com.zdt.zyellowpage.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zdt.zyellowpage.R;

public class FragmentMore extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.supply_demand_detail, container,
				false);
		TextView text = (TextView) view.findViewById(R.id.title);

		return view;
	}
}
