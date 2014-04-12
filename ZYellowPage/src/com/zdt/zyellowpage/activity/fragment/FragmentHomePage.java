package com.zdt.zyellowpage.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.PersonDetailActivity;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.activity.PopPersonListActivity;
import com.zdt.zyellowpage.activity.TypeBusinessListActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.jsonEntity.PersonListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;

/**
 * 首页Fragment
 * 
 * @author Administrator
 * 
 */
public class FragmentHomePage extends Fragment implements OnClickListener{
	View view;
	private AbActivity mActivity;
	private DisplayUtil displayUtil;
	private MyApplication application;
	private User hotUser;
	private User PersonUser;
	TextView hotName;
	TextView PersonName;
	TextView hotConent;
	TextView PersonConent;
	ImageView hotImage;
	ImageView PersonImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 获取布局文件
		view = inflater.inflate(R.layout.fragment_homepage, container, false);
		// 获取所属Activity
		mActivity = (AbActivity) getActivity();
		application = (MyApplication) mActivity.abApplication;
		displayUtil = DisplayUtil.getInstance(mActivity);
		// 美食
		ImageButton imgBFood = (ImageButton) view
				.findViewById(R.id.imageButtonFood);
		imgBFood.setOnClickListener(this);
		
		ImageButton imgBMovie = (ImageButton) view
				.findViewById(R.id.imageButtonMovie);
		imgBMovie.setOnClickListener(this);
		
		ImageButton imgBHappy = (ImageButton) view
				.findViewById(R.id.imageButtonHappy);
		imgBHappy.setOnClickListener(this);
		
		ImageButton imgBHotel = (ImageButton) view
				.findViewById(R.id.imageButtonHotel);
		imgBHotel.setOnClickListener(this);
		
		
		ImageButton imgBNewInfo = (ImageButton) view
				.findViewById(R.id.imageButtonNewInfo);
		imgBNewInfo.setOnClickListener(this);
		
		ImageButton imgBCash = (ImageButton) view
				.findViewById(R.id.imageButtonCash);
		imgBCash.setOnClickListener(this);
		
		
		/*textBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mActivity, "测试", Toast.LENGTH_SHORT).show();
			}
		});*/
		// 热门商家列表
		TextView hotbusiness  = (TextView) view
				.findViewById(R.id.textViewhotbusiness);
		hotbusiness.setOnClickListener(this);
		/*hotbusiness.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,
						PopBusinessListActivity.class);
				startActivity(intent);
			}
		});*/
		// 热门个人列表
		TextView textPerson = (TextView) view
				.findViewById(R.id.textViewhotperson);
		textPerson.setOnClickListener(this);
		/*textPerson.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,
						PopPersonListActivity.class);
				startActivity(intent);
			}
		});*/
		// 首页热门商家
		hotName = (TextView) view.findViewById(R.id.textViewBusinessHot);
		hotName.setOnClickListener(this);
		/*hotName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,
						BusinessDetailActivity.class);
				startActivity(intent);
			}
		});*/
		// 首页热门关注
		PersonName = (TextView) view.findViewById(R.id.textViewPersonHot);
		PersonName.setOnClickListener(this);
		/*PersonName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,
						PersonDetailActivity.class);
				startActivity(intent);
			}
		});*/
		getData();
		initType(view);
		return view;

	}

	/**
	 * 初始化类型选择按钮背景图片布局，主要是为了适应屏幕
	 * 
	 * @param view
	 */
	void initType(View view) {
		Resources resouce = view.getResources();
		/**
		 * 获取当前屏幕的像素值
		 */
		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels / 4;
		int high = metric.heightPixels / 6;
		Log.e("xxxxxxxxxxxxxxx", "width=" + width);
		// displayUtil.setViewLayoutParamsX(view.findViewById(R.id.imageViewNews),
		// resouce.getDrawable(R.drawable.news), width*4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonFood),
				resouce.getDrawable(R.drawable.food), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonMovie),
				resouce.getDrawable(R.drawable.movie), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHappy),
				resouce.getDrawable(R.drawable.happy), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonHotel),
				resouce.getDrawable(R.drawable.hotel), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonNewInfo),
				resouce.getDrawable(R.drawable.newinfo), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonCash),
				resouce.getDrawable(R.drawable.cash), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonPeople),
				resouce.getDrawable(R.drawable.people), width, width*5/4);
		displayUtil.setViewLayoutParamsW(
				view.findViewById(R.id.imageButtonAll),
				resouce.getDrawable(R.drawable.all), width, width*5/4);

		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewhotbusiness), high);
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewhotperson), high);
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewBusinessHot), high);
		displayUtil.setViewLayoutParamsTextViewC(
				view.findViewById(R.id.textViewBusinessConetentHot), width,
				high);
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewPersonHot), high);
		displayUtil.setViewLayoutParamsTextViewC(
				view.findViewById(R.id.textViewPersonContentHot), width, high);
		displayUtil.setViewLayoutParamsLayout(
				view.findViewById(R.id.LinearLayouthot), high);
		displayUtil.setViewLayoutParamsLayout(
				view.findViewById(R.id.LinearLayoutPerson), high);
		displayUtil.setViewLayoutParamsR(view.findViewById(R.id.imageViewhot),
				high / 3 * 2, high / 2);
		displayUtil.setViewLayoutParamsR(
				view.findViewById(R.id.imageViewPerson), high / 3 * 2,
				high / 2);
	}

	
	void getData() {
		UserBll bll = new UserBll();
		hotUser = new User();
		CompanyListReqEntity companyParams = new CompanyListReqEntity(0, 10,
				application.cityid, "list-hot");

		bll.getListCompany(mActivity, companyParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						hotUser = lis.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						Log.e("xxxxSummary1","----------"+hotUser.getKeyword());
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						Log.e("xxxxLogo","----------"+hotUser.getLogo());
						if (hotUser.getLogo() != null) {
							hotImage = (ImageView) view.findViewById(R.id.imageViewhot);
							new AbImageDownloader(mActivity).display(hotImage,
									hotUser.getLogo());
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						if (localList == null || localList.size() == 0) {
							return;
						}
						hotUser = localList.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						Log.e("xxxxSummary1","----------"+hotUser.getKeyword());
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						Log.e("xxxxLogo","----------"+hotUser.getLogo());
						if (hotUser.getLogo() != null) {
							hotImage = (ImageView) view.findViewById(R.id.imageViewhot);
							new AbImageDownloader(mActivity).display(hotImage,
									hotUser.getLogo());
						}
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						mActivity.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						mActivity.removeProgressDialog();
					}
					/*@Override
					public void onSuccess(int statusCode, List<User> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						hotUser = (User) lis.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						Log.e("xxxxSummary1","----------"+hotUser.getKeyword());
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						Log.e("xxxxLogo","----------"+hotUser.getLogo());
						if (hotUser.getLogo() != null) {
							hotImage = (ImageView) view.findViewById(R.id.imageViewhot);
							new AbImageDownloader(mActivity).display(hotImage,
									hotUser.getLogo());
						}
					}

					@Override
					public void onStart() {
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFinish() {
						mActivity.removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						mActivity.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						mActivity.showToast(status_description);
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List localList) {
						// TODO Auto-generated method stub
						
					}*/
				});
		
		PersonUser = new User();
		PersonListReqEntity personParams = new PersonListReqEntity(0, 10,
				application.cityid, "list-hot");
		new UserBll().getListPerson(mActivity, personParams,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						PersonUser = lis.get(0);
						if (PersonUser.getFullname() != null) {
							PersonName.setText(PersonUser.getFullname());
						}
						Log.e("xxxxSummary","----------"+PersonUser.getAddress());
						if (PersonUser.getKeyword() != null) {
							PersonConent = (TextView) view
									.findViewById(R.id.textViewPersonContentHot);
							PersonConent.setText(PersonUser.getKeyword());
						}
						
						if (PersonUser.getLogo() != null) {
							PersonImage = (ImageView) view.findViewById(R.id.imageViewPerson);
							new AbImageDownloader(mActivity).display(PersonImage,
									PersonUser.getLogo());
						}
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						mActivity.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						mActivity.showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						mActivity.removeProgressDialog();
					}
				/*	@Override
					public void onSuccess(int statusCode, List<Object> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						PersonUser = (User) lis.get(0);
						if (PersonUser.getFullname() != null) {
							PersonName.setText(PersonUser.getFullname());
						}
						Log.e("xxxxSummary","----------"+PersonUser.getAddress());
						if (PersonUser.getKeyword() != null) {
							PersonConent = (TextView) view
									.findViewById(R.id.textViewPersonContentHot);
							PersonConent.setText(PersonUser.getKeyword());
						}
						
						if (PersonUser.getLogo() != null) {
							PersonImage = (ImageView) view.findViewById(R.id.imageViewPerson);
							new AbImageDownloader(mActivity).display(PersonImage,
									PersonUser.getLogo());
						}
					}

					@Override
					public void onStart() {
						mActivity.showProgressDialog("同步信息...");
					}

					@Override
					public void onFinish() {
						mActivity.removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						mActivity.showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						mActivity.showToast(status_description);
					}*/
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		//美食
		case R.id.imageButtonFood:
			
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "美食");
			 startActivity(intent);
			break;
		case R.id.imageButtonMovie:
			
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "电影");
			 startActivity(intent);
			break;
		case R.id.imageButtonHappy:
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "休闲");
			 startActivity(intent);
			break;
		case R.id.imageButtonHotel:
			
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "酒店");
			 startActivity(intent);
			break;
		case R.id.imageButtonNewInfo:
			
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "新增");
			 startActivity(intent);
			break;
		case R.id.imageButtonCash:
			 intent = new Intent(mActivity,
					 TypeBusinessListActivity.class);
			 intent.putExtra("Type", "代金券");
			 startActivity(intent);
			break;
			//热门商家
		case R.id.textViewhotbusiness:
			 intent = new Intent(mActivity,
					PopBusinessListActivity.class);
			startActivity(intent);
			break;
		case R.id.textViewhotperson:
			 intent = new Intent(mActivity,
					PopPersonListActivity.class);
			startActivity(intent);
			break;
		case R.id.textViewBusinessHot:
			intent = new Intent(mActivity,
					BusinessDetailActivity.class);
			intent.putExtra("MEMBER_ID", hotUser.getMember_id());
			startActivity(intent);
			break;
		case R.id.textViewPersonHot:
			intent = new Intent(mActivity,
					PersonDetailActivity.class);
			intent.putExtra("MEMBER_ID",  PersonUser.getMember_id());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
