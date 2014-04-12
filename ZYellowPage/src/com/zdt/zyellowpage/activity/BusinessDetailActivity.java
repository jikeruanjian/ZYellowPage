package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.R.color;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class BusinessDetailActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String member_id;
	private User userCompany;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private TextView t1, t2, t3, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String[] imageUrls = new String[] {
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg",
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg",
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg",
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg",
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg",
			"http://f.321hy.cn/Upload/609417/Logo/Avatar.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_businessdetail);

		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
		}
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.business_detail);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		userCompany = new User();
		getData();

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.businessdetail)
				.showImageOnFail(R.drawable.businessdetail)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(BusinessDetailActivity.this));
	}

	private void getView() {

		TextView name = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.companyfullname);
		TextView userName = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_name);
		TextView num = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_nummber);
		TextView tel = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_tel);
		TextView url = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_url);
		TextView address = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_address);
		TextView email = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_email);
		TextView qq = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_qq);

		name.setText(userCompany.getFullname());
		num.setText(userCompany.getMember_id());
		userName.setText(userCompany.getUsername());
		tel.setText(userCompany.getTelephone());
		url.setText(userCompany.getWebsite());
		address.setText(userCompany.getAddress());
		email.setText(userCompany.getEmail());
		qq.setText(userCompany.getQq());

		// sjjwd.setText("经度："+userCompany.getLatitude()+"      纬度:"+userCompany.getLongitude());

		InitTextView();
		InitViewPager();
	}

	protected void getData() {
		UserBll bll = new UserBll();
		bll.getDetailCompany(BusinessDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						userCompany = (User) lis.get(0);
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
						userCompany = (User) localList.get(0);
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

						Gallery gallery = (Gallery) findViewById(R.id.user_company_gallery);
						gallery.setAdapter(new ImageGalleryAdapter());
						gallery.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								startImagePagerActivity(position);
							}

							private void startImagePagerActivity(int position) {
								Intent intent = new Intent(
										BusinessDetailActivity.this,
										ImagePagerActivity.class);
								intent.putExtra("imageUrls", imageUrls);
								startActivity(intent);
							}

						});
						getView();
					}

				});
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);

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
		// listViews.add(mInflater.inflate(R.layout.business_detail_info,
		// null));
		// listViews.add(mInflater.inflate(R.layout.business_detail_info,
		// null));
		// listViews.add(mInflater.inflate(R.layout.business_detail_info,
		// null));
		listViews.add(addTextByText(userCompany.getSummary()));
		listViews.add(addTextByText(userCompany.getDiscount()));
		listViews.add(addTextByText("yyyyyyyyyyyyyyyy"));
		listViews.add(addTextByText(userCompany.getScope()));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public View addTextByText(String text) {
		// TextView tv = new TextView(this);
		// tv.setText(text);
		// tv.setBackgroundColor(color.white);
		// tv.setGravity(1);

		WebView webView = new WebView(this);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.loadData(text, "text/plain", "gb2312");
		return webView;
	}

	private class ImageGalleryAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(
						R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(imageUrls[position], imageView, options);
			return imageView;
		}
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
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
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
