package com.zdt.zyellowpage.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ab.activity.AbActivity;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.TieListActivity;

public class FragmentTie extends Fragment {
	AbActivity mActivity;
	RelativeLayout rllHunq;
	RelativeLayout rllQiaoq;
	RelativeLayout rllJuh;
	RelativeLayout rllKaiy;
	RelativeLayout rllQingd;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, Bundle savedInstanceState) {
		mActivity = (AbActivity) getActivity();
		View view = inflater.inflate(R.layout.new_fragment_tie, container,
				false);
		rllHunq = (RelativeLayout) view.findViewById(R.id.rllHunq);
		rllQiaoq = (RelativeLayout) view.findViewById(R.id.rllQiaoq);
		rllJuh = (RelativeLayout) view.findViewById(R.id.rllJuh);
		rllKaiy = (RelativeLayout) view.findViewById(R.id.rllKaiy);
		rllQingd = (RelativeLayout) view.findViewById(R.id.rllQingd);

		rllHunq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);
				intent.putExtra("tieType", "1");
				mActivity.startActivity(intent);
			}
		});

		rllQiaoq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);
				intent.putExtra("tieType", "2");
				mActivity.startActivity(intent);
			}
		});

		rllJuh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);
				intent.putExtra("tieType", "3");
				mActivity.startActivity(intent);
			}
		});

		rllKaiy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);
				intent.putExtra("tieType", "4");
				mActivity.startActivity(intent);
			}
		});

		rllQingd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, TieListActivity.class);
				intent.putExtra("tieType", "5");
				mActivity.startActivity(intent);
			}
		});

		return view;
	}
}
