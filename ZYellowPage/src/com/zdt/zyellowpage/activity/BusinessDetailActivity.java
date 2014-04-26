package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.ab.global.AbConstant;
import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbHttpUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AlbumBll;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Album;
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
	private TextView t1, t2, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String[] imageUrls = new String[] {};
	private ImageView imgCompanyVideos;
	private  View mView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_businessdetail);

		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
			if(member_id == null){
				this.showDialog("错误", "数据获取失败", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				/*View mView = mInflater.inflate(R.layout.getdatafailed, null);
				mView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				showDialog(AbConstant.DIALOGBOTTOM, mView);*/
			}
			else{ 
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
		}
		
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
							BusinessDetailActivity.this.showDialog("错误", "数据获取失败",
									new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									finish();
								}
							});
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
						getView();
						getImgUrl(userCompany.getMember_id());
					}

				});
	}

	void getImgUrl(String m_id) {

		AlbumBll imgBll = new AlbumBll();
		imgBll.getAlbumList(BusinessDetailActivity.this, new AlbumReqEntity(0,
				5, m_id), new ZzObjectHttpResponseListener<Album>() {

			@Override
			public void onSuccess(int statusCode, List<Album> lis) {
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					return;
				}
				List<String> imgs = new ArrayList<String>();
				for (Album a : lis) {
					imgs.add(a.getUrl());
				}
				imageUrls = imgs.toArray(new String[] {});
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<Album> localList) {
				// TODO Auto-generated method stub
				if (localList == null || localList.size() == 0) {
					return;
				}
				List<String> imgs = new ArrayList<String>();
				for (Album a : localList) {
					imgs.add(a.getUrl());
				}
				imageUrls = imgs.toArray(new String[] {});
			}

			@Override
			public void onErrorData(String status_description) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				Gallery gallery = (Gallery) findViewById(R.id.user_company_gallery);
				Log.e("xxxx", "-----图片张数为" + imageUrls.length);
				gallery.setAdapter(new ImageGalleryAdapter());
				gallery.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						startImagePagerActivity(position);
					}

					private void startImagePagerActivity(int position) {
						Intent intent = new Intent(BusinessDetailActivity.this,
								ImagePagerActivity.class);
						intent.putExtra("imageUrls", imageUrls);
						startActivity(intent);
					}

				});
			}

		});
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t4 = (TextView) findViewById(R.id.text4);
		imgCompanyVideos = (ImageView) findViewById(R.id.imgCompanyVideos);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t4.setOnClickListener(new MyOnClickListener(3));

		// 点击了地图图标
		this.findViewById(R.id.business_detail_sjdt).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (userCompany.getFullname() == null
								|| "".equals(userCompany.getFullname())) {

						} else {
							Intent intent = new Intent(
									BusinessDetailActivity.this,
									CompanyMapActiviy.class);
							intent.putExtra("FUllNAME",
									userCompany.getFullname());
							intent.putExtra("LAT", userCompany.getLatitude());
							intent.putExtra("LON", userCompany.getLongitude());
							startActivity(intent);
						}
					}

				});
		// 点击了供求信息
		this.findViewById(R.id.imgCompanyBuySell).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(BusinessDetailActivity.this,
								CompanyBuySellActivity.class);
						intent.putExtra("FUllNAME", userCompany.getFullname());
						intent.putExtra("MEMBER_ID", userCompany.getMember_id());
						startActivity(intent);

					}

				});
		// 商家二维码
		this.findViewById(R.id.imgBussnissCode).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String url = userCompany.getQr_code()+"&area="+ application.cityid;
				Log.e("xxxxtp", "---" +url);
				AbHttpUtil.getInstance(BusinessDetailActivity.this).get(url, new AbBinaryHttpResponseListener() {
		        	
					// 获取数据成功会调用这里
		        	@Override
					public void onSuccess(int statusCode, byte[] content) {
		        		Log.d("xxxx", "onSuccess");
		        		Bitmap bitmap = AbImageUtil.bytes2Bimap(content);
		            	mView = mInflater.inflate(R.layout.code_view, null);
		            	ImageView imageUserCode = (ImageView) mView.findViewById(R.id.imageViewCodeCP);
		            	imageUserCode.setImageBitmap(bitmap);
		            	imageUserCode.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								removeDialog(AbConstant.DIALOGCENTER);
							}
						});
		            	showDialog(AbConstant.DIALOGCENTER, mView);
		            	
					}
		        	
		        	// 开始执行前
		            @Override
					public void onStart() {
		            	Log.d("xxxx", "onStart");
		            	//显示进度框
		            	showProgressDialog();
					}

		            // 失败，调用
		            @Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
		            	showToast(error.getMessage());
					}

					// 完成后调用，失败，成功
		            @Override
		            public void onFinish() { 
		            	Log.d("xxxx", "onFinish");
		            	//移除进度框
		            	removeProgressDialog();
		            };
		            
		        });
			}
		});
        

		this.findViewById(R.id.imgBussnissPhone).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + userCompany.getTelephone()));
						startActivity(intent);
					}
				});

		imgCompanyVideos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AbStrUtil.isEmpty(member_id)) {
					return;
				}
				Intent intent = new Intent(BusinessDetailActivity.this,
						VideoListActivity.class);
				intent.putExtra("member_id", member_id);
				startActivity(intent);
			}
		});
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
		listViews.add(addTextByText(userCompany.getScope()));
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
		// Log.i("BusinessDetailActivity", text);
		if (AbStrUtil.isEmpty(text)) {
			text = "用户暂时还未添加该项数据";
		}
		webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
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

			t1.setTextColor(getResources().getColor(R.color.black));

			t2.setTextColor(getResources().getColor(R.color.black));
			t4.setTextColor(getResources().getColor(R.color.black));
			switch (arg0) {
			case 0: {
				t1.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			}
			case 1: {
				t2.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			}
			case 2: {
				t4.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			case 3: {
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
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
