package com.zdt.zyellowpage.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.CompanyBuySellActivity;
import com.zdt.zyellowpage.activity.CompanyMapActiviy;
import com.zdt.zyellowpage.activity.MyPopupWindow;
import com.zdt.zyellowpage.activity.PersonDetailActivity;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.activity.PopPersonListActivity;
import com.zdt.zyellowpage.activity.TypeBusinessListActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.CompanyListReqEntity;
import com.zdt.zyellowpage.jsonEntity.PersonListReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;

/**
 * 首页Fragment
 * 
 * @author Administrator
 * 
 */
public class FragmentHomePage extends Fragment implements OnClickListener{
	private View view;
	private AbActivity mActivity;
	private DisplayUtil displayUtil;
	private MyApplication application;
	private User hotUser;
	private User PersonUser;
	private TextView hotName;
	private TextView PersonName;
	private TextView hotConent;
	private TextView PersonConent;
	private ImageView hotImage;
	private ImageView PersonImage;
	MyPopupWindow myPopupWindow;
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
		
		ImageButton imgBPeople = (ImageButton) view
				.findViewById(R.id.imageButtonPeople);
		imgBPeople.setOnClickListener(this);
		
		ImageButton imgBAllClass= (ImageButton) view
				.findViewById(R.id.imageButtonAll);
		imgBAllClass.setOnClickListener(this);
		
		
		// 热门商家列表
		TextView hotbusiness  = (TextView) view
				.findViewById(R.id.textViewhotbusiness);
		hotbusiness.setOnClickListener(this);
		
		// 热门个人列表
		TextView textPerson = (TextView) view
				.findViewById(R.id.textViewhotperson);
		textPerson.setOnClickListener(this);
		view.findViewById(R.id.LinearLayouthot).setOnClickListener(this);
		view.findViewById(R.id.LinearLayoutPerson).setOnClickListener(this);
		// 首页热门商家
		hotName = (TextView) view.findViewById(R.id.textViewBusinessHot);
		//hotName.setOnClickListener(this);
		
		// 首页热门关注
		PersonName = (TextView) view.findViewById(R.id.textViewPersonHot);
		//PersonName.setOnClickListener(this);
		
		view.findViewById(R.id.btnConcernHotBussiness).setOnClickListener(this);
		view.findViewById(R.id.btnConcernHotPerson).setOnClickListener(this);
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
				view.findViewById(R.id.textViewBusinessHot), high-15);
		displayUtil.setViewLayoutParamsTextViewC(
				view.findViewById(R.id.textViewBusinessConetentHot), width,high+15);
		displayUtil.setViewLayoutParamsTextView(
				view.findViewById(R.id.textViewPersonHot), high-15);
		displayUtil.setViewLayoutParamsTextViewC(
				view.findViewById(R.id.textViewPersonContentHot), width, high+15);
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

	
	public void getData() {
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
						hotUser = (User) lis.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						
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
						hotUser = (User) localList.get(0);
						System.out.println("----"+hotUser.getFullname());
						if (hotUser.getFullname() != null) {
							hotName.setText(hotUser.getFullname());
						}
						
						if (hotUser.getKeyword()!= null) {
							hotConent = (TextView) view
									.findViewById(R.id.textViewBusinessConetentHot);
							hotConent.setText(hotUser.getKeyword());
						}
						
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
						PersonUser = (User) lis.get(0);
						if (PersonUser.getFullname() != null) {
							PersonName.setText(PersonUser.getFullname());
						}
						
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
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "美食");
			 intent.putExtra("TypeId", "list-0102");
			 startActivity(intent);
			break;
		case R.id.imageButtonMovie:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "电影");
			 intent.putExtra("TypeId", "list-电影");
			 startActivity(intent);
			break;
		case R.id.imageButtonHappy:
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "休闲娱乐");
			 intent.putExtra("TypeId", "list-0103");
			 startActivity(intent);
			break;
		case R.id.imageButtonHotel:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "酒店");
			 intent.putExtra("TypeId", "list-0110");
			 startActivity(intent);
			break;
		case R.id.imageButtonNewInfo:
			
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "今日新单");
			 intent.putExtra("TypeId", "今日新单");
			 startActivity(intent);
			break;
		case R.id.imageButtonCash:
			 intent = new Intent(mActivity,
					 PopBusinessListActivity.class);
			 intent.putExtra("Type", "代金券");
			 intent.putExtra("TypeId","代金券");
			 startActivity(intent);
			break;
		case R.id.imageButtonAll:
			 showPopWindows();
			break;
		case R.id.imageButtonPeople:
			 intent = new Intent(mActivity,
						PopPersonListActivity.class);
			 intent.putExtra("Type", "个人列表");
			 intent.putExtra("TypeId", "list-hot");
				startActivity(intent);
			break;
			//热门商家
		case R.id.textViewhotbusiness:
			 intent = new Intent(mActivity,
					PopBusinessListActivity.class);
			 intent.putExtra("Type", "热门商家");
			 intent.putExtra("TypeId", "list-hot");
			startActivity(intent);
			break;
		case R.id.textViewhotperson:
			 intent = new Intent(mActivity,
					PopPersonListActivity.class);
			 intent.putExtra("Type", "热门关注");
			 intent.putExtra("TypeId", "list-hot");
			startActivity(intent);
			break;
		case R.id.LinearLayouthot:
			if(hotUser.getMember_id()!=null){
			intent = new Intent(mActivity,
					BusinessDetailActivity.class);
			intent.putExtra("MEMBER_ID", hotUser.getMember_id());
			startActivity(intent);
			}
			else{
				mActivity.showToast("请检查网络情况！");
			}
			break;
		case R.id.LinearLayoutPerson:
			if(hotUser.getMember_id()!=null){
			intent = new Intent(mActivity,
					PersonDetailActivity.class);
			intent.putExtra("MEMBER_ID",  PersonUser.getMember_id());
			startActivity(intent);
			}
			else{
				mActivity.showToast("请检查网络情况！");
			}
			break;
		case R.id.btnConcernHotBussiness:
			if(hotUser.getMember_id()!=null){
				concernBussinessOrPerson(hotUser.getMember_id());
			}
			
			break;
		case R.id.btnConcernHotPerson:
			if(PersonUser.getMember_id()!=null){
				concernBussinessOrPerson(PersonUser.getMember_id());
			}
			break;
		default:
			break;
		}
	}
	
	void concernBussinessOrPerson(String id){
		if (application.mUser != null && application.mUser.getToken() != null) {
    		  
    		UserBll bll = new UserBll();
    		 bll.followUser(mActivity, application.mUser.getToken(), id,false,
    				 new ZzStringHttpResponseListener(){

						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							Toast.makeText(mActivity,content, Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFailure(int statusCode,
								String content, Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(mActivity, "关注失败！", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onErrorData(String status_description) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							
						}
    			 
    		 });
    		
    		
    	}
    	else
    	{
    		Toast.makeText(mActivity, "请先登陆！", Toast.LENGTH_SHORT).show();  
    	}
	}
	
	
	void  showPopWindows(){
		//Log.e("fragment", "-----点击了全部分类");
		myPopupWindow = new MyPopupWindow(mActivity);
		myPopupWindow.popupWindow.setWidth(mActivity.getWindowManager().getDefaultDisplay().getWidth()/7*6);
		displayUtil.setViewLayoutParamsR(myPopupWindow.layoutLeft,
				mActivity.getWindowManager().getDefaultDisplay().getWidth()/7*3,0);
		displayUtil.setViewLayoutParamsL(myPopupWindow.listViewClassB,
				0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		//displayUtil.setViewLayoutParamsR(myPopupWindow.layoutRight,0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		displayUtil.setViewLayoutParamsL(myPopupWindow.listViewClassP,
				0,mActivity.getWindowManager().getDefaultDisplay().getHeight()/5*3);
		myPopupWindow.popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		myPopupWindow.popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		
	}

}
