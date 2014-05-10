package com.zdt.zyellowpage.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.util.AbStrUtil;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.MyConcernActivity;
import com.zdt.zyellowpage.activity.MyResourceActivity;
import com.zdt.zyellowpage.activity.login.ChangePwdActivity;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.util.DisplayUtil;

public class FragmentUser extends Fragment {
	AbActivity mActivity;
	MyApplication application;
	TextView btnLogin;
	ImageView imageUserLogo;
	Button btnChangePwd;
	Button btnMyFellow;
	Button btnMyResource;
	Button btnLogout;
	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_user, container, false);
		mActivity = (AbActivity) getActivity();
		DisplayUtil displayUtil = DisplayUtil.getInstance(mActivity);
		application = (MyApplication) mActivity.getApplication();
		btnLogin = (TextView) view.findViewById(R.id.buttonlogin);
		imageUserLogo = (ImageView) view.findViewById(R.id.imageHead);
		btnChangePwd = (Button) view.findViewById(R.id.changePwd);
		btnMyFellow = (Button) view.findViewById(R.id.myFellow);
		btnMyResource = (Button) view.findViewById(R.id.myResource);
		btnLogout = (Button) view.findViewById(R.id.btnLogout);

		btnChangePwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.startActivity(new Intent(mActivity,
						ChangePwdActivity.class));
			}
		});

		btnMyResource.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MyResourceActivity.class);
				startActivity(intent);
			}
		});

		btnMyFellow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MyConcernActivity.class);
				startActivity(intent);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, LoginActivity.class);
				startActivity(intent);
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				application.mUser = null;
				FragmentUser.this.onResume();
			}
		});

		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.RelativeLayoutLogin), width);
		view.findViewById(R.id.layoutChangeUserInfo).setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (application.mUser != null
				&& !AbStrUtil.isEmpty(application.mUser.getLogo())) {
			new AbImageDownloader(mActivity).display(imageUserLogo,
					application.mUser.getLogo());
		}
		if(application.mUser != null && application.mUser.getToken() != null) {
			btnLogin.setVisibility(View.GONE);
			view.findViewById(R.id.after_Login_layout).setVisibility(
					View.VISIBLE);
			view.findViewById(R.id.layoutChangeUserInfo).setVisibility(
					View.VISIBLE);

		}
		else{
			imageUserLogo.setBackgroundResource(R.drawable.btn_person_normal);
			btnLogin.setVisibility(View.VISIBLE);
			view.findViewById(R.id.after_Login_layout).setVisibility(
					View.GONE);
			view.findViewById(R.id.layoutChangeUserInfo).setVisibility(
					View.GONE);
		}
	}

}
