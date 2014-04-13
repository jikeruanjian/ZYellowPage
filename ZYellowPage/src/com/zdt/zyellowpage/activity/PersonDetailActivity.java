package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyOnClickListener;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyOnPageChangeListener;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyPagerAdapter;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class PersonDetailActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String member_id;
	private  User userPerson;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private TextView t1, t2,t3, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	//private String[] imageUrls = new String[] { };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_persondetail);
		application = (MyApplication) abApplication;
		
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.person_detail);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		userPerson =new User();
		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
		}
		getData();
//		姓名、性别、年龄、所在地、民族、电话、电子邮箱、编号、QQ、学校、专业、行业、
//		关键词、地址、个人资质、个人特长、个人简介、成功案例、个人二维码、附件地址
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(PersonDetailActivity.this));
	}
	
	
	protected void getData(){
		UserBll bll = new UserBll();
		bll.getDetailPerson(PersonDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<User>(){

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						userPerson = (User)lis.get(0);
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						if (localList == null || localList.size() == 0) {
							return;
						}
						userPerson = (User)localList.get(0);
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						removeProgressDialog();
						//showToast(userPerson.getFullname());
						getView();
					}
		});
	}


	protected void getView() {
		ImageView imageUserLogo= (ImageView)PersonDetailActivity.this.findViewById(R.id.person_detail_photo);
		new AbImageDownloader(PersonDetailActivity.this).display(imageUserLogo,
				userPerson.getLogo());
		
		TextView name= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_name);
		TextView sex= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_sex);
		TextView age= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_age);
		TextView adress= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_address);
		TextView mz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_mz);
		TextView tel= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_tel);
		TextView email= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_email);
		TextView num= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_num);
		TextView qq= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_qq);
		TextView school= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_school);
		TextView zy= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_zy);
		TextView hy= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_hy);
		//TextView gjc= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_gjc);
		TextView dz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_dz);
		//TextView grewm= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_grewm);
		//TextView fjdz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_fjdz);
		//TextView info= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_INFO);
		
		name.setText(userPerson.getFullname());
		sex.setText(userPerson.getSex());
		num.setText(userPerson.getArea_id());
		tel.setText(userPerson.getTelephone());
		adress.setText(userPerson.getCity());
		email.setText(userPerson.getEmail());
		age.setText(userPerson.getAge());
		mz.setText(userPerson.getNation());
		qq.setText(userPerson.getQq());
		school.setText(userPerson.getSchool());
		zy.setText(userPerson.getProfessional());
		hy.setText(userPerson.getCategory_name());
		//gjc.setText(userPerson.getKeyword());
		dz.setText(userPerson.getAddress());
		//grewm.setText(userPerson.getQr_code());
		//fjdz.setText(userPerson.getSchool());
		
		//info.setText(userPerson.getSummary());
		InitTextView();
		InitViewPager();
	}
	
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.textjianjie);
		t2 = (TextView) findViewById(R.id.textzizhi);
		t3 = (TextView) findViewById(R.id.texttechang);
		t4 = (TextView) findViewById(R.id.textanli);
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}
	
	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		
		listViews.add(addTextByText(userPerson.getSummary()));
		listViews.add(addTextByText(userPerson.getDiscount()));
		listViews.add(addTextByText(userPerson.getScope()));
		listViews.add(addTextByText(userPerson.getSummary()));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		t1.setTextColor(getResources().getColor(R.color.orange));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	public View addTextByText(String text) {
		// TextView tv = new TextView(this);
		// tv.setText(text);
		// tv.setBackgroundColor(color.white);
		// tv.setGravity(1);

		WebView webView = new WebView(this);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		//Log.i("PersonDetailActivity", text);
		webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
		return webView;
	}
	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};
	
	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			
			t1.setTextColor(getResources().getColor(R.color.black));
			t3.setTextColor(getResources().getColor(R.color.black));
			t2.setTextColor(getResources().getColor(R.color.black));
			t4.setTextColor(getResources().getColor(R.color.black));
			switch (arg0) {
			case 0:{
				t1.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			}
			case 1:{
				t2.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			}
			case 2:{
				t3.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;}
			case 3:{
				t4.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;}
			}
			currIndex = arg0;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(300);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}
