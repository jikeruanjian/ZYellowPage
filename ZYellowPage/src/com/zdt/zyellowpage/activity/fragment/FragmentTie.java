package com.zdt.zyellowpage.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.listener.AbOnRefreshListener;
import com.ab.view.pullview.AbPullView;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.TieListActivity;
import com.zdt.zyellowpage.bll.TieBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Tie;

public class FragmentTie extends Fragment {

	AbActivity mActivity;
	MyApplication application;
	AbImageDownloader imageLoader;
	AbPullView mAbPullView;
	View[] tieViews = new View[5];
	int loadCount = 0;

	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, Bundle savedInstanceState) {
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.getApplication();
		View view = inflater.inflate(R.layout.fragment_tie, container, false);
		mAbPullView = (AbPullView) view.findViewById(R.id.mPullView);
		imageLoader = new AbImageDownloader(mActivity);

		for (int i = 1; i <= 5; i++) {
			View viewC = creatMenuItem(inflater, container, i + "");
			if (viewC != null)
				mAbPullView.addChildView(viewC);
		}

		mAbPullView.setAbOnRefreshListener(new AbOnRefreshListener() {

			@Override
			public void onRefresh() {
				for (int i = 1; i <= 5; i++) {
					View viewCl = creatMenuItem(inflater, container, i + "");
					if (viewCl != null)
						mAbPullView.addChildView(viewCl);
				}
			}
		});

		return view;
	}

	private View creatMenuItem(LayoutInflater inflater, ViewGroup container,
			final String tieType) {
		View childTieItem = null;
		View resultView = null;
		if (tieViews[Integer.valueOf(tieType) - 1] == null) {
			childTieItem = inflater
					.inflate(R.layout.item_tie, container, false);

			childTieItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, TieListActivity.class);
					intent.putExtra("tieType", tieType);
					mActivity.startActivity(intent);
				}
			});
			resultView = childTieItem;
			tieViews[Integer.valueOf(tieType) - 1] = childTieItem;
		} else {
			childTieItem = tieViews[Integer.valueOf(tieType) - 1];
		}

		final ImageView ivTieLogo = (ImageView) childTieItem
				.findViewById(R.id.ivTieLogo);
		TextView tvTypeName = (TextView) childTieItem
				.findViewById(R.id.tvTypeName);
		final TextView tvTieTitle = (TextView) childTieItem
				.findViewById(R.id.tvTieTitle);
		if ("1".equals(tieType))
			tvTypeName.setText("婚庆");
		else if ("2".equals(tieType))
			tvTypeName.setText("乔迁");
		else if ("3".equals(tieType))
			tvTypeName.setText("聚会");
		else if ("4".equals(tieType))
			tvTypeName.setText("开业");
		else if ("5".equals(tieType))
			tvTypeName.setText("庆典");

		new TieBll().getTieList(mActivity, 0, 1, application.cityid, tieType,
				new ZzObjectHttpResponseListener<Tie>() {

					@Override
					public void onSuccess(int statusCode, List<Tie> lis) {
						if (lis == null || lis.size() == 0)
							return;
						imageLoader.display(ivTieLogo, lis.get(0).getLogo());
						tvTieTitle.setText(lis.get(0).getTitle());
						if ("5".equals(tieType)) {
							mAbPullView.stopRefresh();
						}
						loadCount++;
					}

					@Override
					public void onStart() {
						if ("5".equals(tieType)) {
							loadCount = 0;
						}
					}

					@Override
					public void onFinish() {
						if ("5".equals(tieType)) {
							mAbPullView.stopRefresh();
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Tie> localList) {
						if ("5".equals(tieType) && loadCount == 0) {
							mActivity.showToast(content);
						}
					}

					@Override
					public void onErrorData(String status_description) {
					}
				});
		return resultView;
	}
}
