package com.zdt.zyellowpage.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.TieListActivity;

public class FragmentMore extends Fragment {

	AbActivity mActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbActivity) getActivity();
		View view = inflater.inflate(R.layout.supply_demand_detail, container,
				false);
		TextView text = (TextView) view.findViewById(R.id.tvTitle);

		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.startActivity(new Intent(mActivity,
						TieListActivity.class));
			}
		});
		return view;
	}
}
