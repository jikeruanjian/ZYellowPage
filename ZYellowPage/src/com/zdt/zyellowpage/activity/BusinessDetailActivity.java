package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbHttpUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.sliding.AbSlidingPlayView;
import com.ab.view.titlebar.AbTitleBar;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.CircleShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.WeiXinShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.AlbumBll;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Album;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class BusinessDetailActivity extends AbActivity implements
		ISimpleDialogListener, ISimpleDialogCancelListener {
	public static String TAG = "BusinessDetailActivity";
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private AbSlidingPlayView mSlidingPlayView = null;
	private String member_id;
	private User userCompany;
	// private ImageLoader imageLoader = ImageLoader.getInstance();
	// private DisplayImageOptions options;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private TextView t1, t2, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String[] imageUrls = new String[] {};
	private ImageView imgCompanyVideos;
	private ImageView imgLogo;
	private View mView;
	Bitmap codeBitmap;
	private DisplayUtil displayUtil;
	RelativeLayout layMain;
	ImageView imageUserLogo;
	AbImageDownloader imageLoader = null;

	// sdk controller
	private UMSocialService mController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_businessdetail);
		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
			if (member_id == null) {
				// this.showDialog("错误", "数据获取失败",
				// new android.content.DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface arg0, int arg1) {
				// finish();
				// }
				// });
				SimpleDialogFragment
						.createBuilder(this, getSupportFragmentManager())
						.setTitle("错误").setMessage("参数错误")
						.setPositiveButtonText("返回").setTag(TAG)
						.setRequestCode(42).show();
			} else {
				application = (MyApplication) abApplication;
				mAbTitleBar = this.getTitleBar();
				mAbTitleBar.setTitleText("详细信息");
				mAbTitleBar.setLogo(R.drawable.button_selector_back);
				mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
				mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
				// mAbTitleBar.setLogoLine(R.drawable.line);
				userCompany = new User();
				layMain = (RelativeLayout) findViewById(R.id.layMain);

				mSlidingPlayView = (AbSlidingPlayView) this
						.findViewById(R.id.mAbSlidingPlayViewB);
				displayUtil = DisplayUtil
						.getInstance(BusinessDetailActivity.this);
				DisplayMetrics metric = new DisplayMetrics();
				this.getWindowManager().getDefaultDisplay().getMetrics(metric);
				int width = metric.widthPixels / 4 * 3;
				displayUtil.setViewLayoutParamsL(mSlidingPlayView, 0, width);
				mSlidingPlayView.setPageLineHorizontalGravity(Gravity.RIGHT);

				imageLoader = new AbImageDownloader(BusinessDetailActivity.this);

				InitTitleView();
				getData();

				// options = new DisplayImageOptions.Builder()
				// .showImageForEmptyUri(R.drawable.businessdetail)
				// .showImageOnFail(R.drawable.businessdetail)
				// .resetViewBeforeLoading(true).cacheOnDisc(true)
				// .imageScaleType(ImageScaleType.EXACTLY)
				// .bitmapConfig(Bitmap.Config.RGB_565)
				// .considerExifParams(true)
				// .displayer(new FadeInBitmapDisplayer(300)).build();
				// imageLoader.init(ImageLoaderConfiguration
				// .createDefault(BusinessDetailActivity.this));
			}
		}
	}

	private void getView() {

		layMain.setVisibility(View.VISIBLE);
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
		TextView wifiUser = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_wifiUser);
		TextView wifiPW = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_wifiPW);

		name.setText(userCompany.getFullname());
		num.setText(userCompany.getMember_id());
		userName.setText(userCompany.getContact());
		tel.setText(userCompany.getTelephone());
		url.setText(userCompany.getWebsite());
		address.setText(userCompany.getAddress());
		email.setText(userCompany.getEmail());
		qq.setText(userCompany.getQq());
		wifiUser.setText(userCompany.getWifi_username());
		wifiPW.setText(userCompany.getWifi_password());
		imgLogo = (ImageView) this.findViewById(R.id.companyLogoImage);
		if (userCompany.getLogo() != null) {
			imageLoader.display(imgLogo, userCompany.getLogo());
		}
		InitTextView();
		InitViewPager();
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
	}

	private void InitTitleView() {
		mAbTitleBar.clearRightView();
		TextView tvSave = new TextView(this);
		tvSave.setText("+关注  ");
		tvSave.setTextColor(Color.WHITE);
		tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mAbTitleBar.addRightView(tvSave);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		tvSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (application.mUser != null
						&& application.mUser.getToken() != null) {
					UserBll bll = new UserBll();
					bll.followUser(BusinessDetailActivity.this,
							application.mUser.getToken(),
							userCompany.getMember_id(), false,
							new ZzStringHttpResponseListener() {
								@Override
								public void onSuccess(int statusCode,
										String content) {
									Toast.makeText(BusinessDetailActivity.this,
											content, Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onStart() {
									showProgressDialog();
								}

								@Override
								public void onFailure(int statusCode,
										String content, Throwable error) {
									showToast(content);
								}

								@Override
								public void onErrorData(
										String status_description) {
									showToast(status_description);
								}

								@Override
								public void onFinish() {
									removeProgressDialog();
								}
							});
				} else {
					Toast.makeText(BusinessDetailActivity.this, "请先登录！",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(BusinessDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	private void getData() {
		UserBll bll = new UserBll();
		bll.getDetailCompany(BusinessDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						userCompany = lis.get(0);
						if (!AbStrUtil.isEmpty(userCompany.getAlbum())) {
							// TODO 加载图片
							// getImgUrl(userCompany.getMember_id());
							imageUrls = userCompany.getAlbum().split(",");
							findViewById(R.id.mAbSlidingPlayViewBLinearLayout)
									.setVisibility(View.VISIBLE);
							initImageView();
						}
						getView();
						getCodeData();
					}

					@Override
					public void onStart() {
						showProgressDialog("正在获取详细信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						SimpleDialogFragment
								.createBuilder(BusinessDetailActivity.this,
										getSupportFragmentManager())
								.setTitle("错误").setMessage("数据获取失败")
								.setPositiveButtonText("返回").setRequestCode(42)
								.setTag(TAG).show();
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}
				});
	}

	private void initImageView() {

		if (imageUrls.length < 1) {
			this.findViewById(R.id.mAbSlidingPlayViewBLinearLayout)
					.setVisibility(View.GONE);
			return;
		}
		for (int i = imageUrls.length - 1; i >= 0; i--) {
			View mPlayView = new View(BusinessDetailActivity.this);
			mPlayView = mInflater.inflate(R.layout.play_view_item, null);
			ImageView mPlayImage = (ImageView) mPlayView
					.findViewById(R.id.mPlayImage);
			mPlayImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					AlbumBll imgBll = new AlbumBll();
					imgBll.getAlbumList(
							BusinessDetailActivity.this,
							new AlbumReqEntity(0, 200, userCompany
									.getMember_id()),
							new ZzObjectHttpResponseListener<Album>() {

								@Override
								public void onSuccess(int statusCode,
										List<Album> lis) {
									if (lis == null || lis.size() == 0) {
										return;
									}
									List<String> imgs = new ArrayList<String>();
									for (Album a : lis) {
										imgs.add(a.getUrl());
									}
									String[] allImageUrls = imgs
											.toArray(new String[] {});

									Intent intent = new Intent(
											BusinessDetailActivity.this,
											ImagePagerActivity.class);
									intent.putExtra("imageUrls", allImageUrls);
									startActivity(intent);
								}

								@Override
								public void onStart() {
									showProgressDialog("请稍后...");
								}

								@Override
								public void onFailure(int statusCode,
										String content, Throwable error,
										List<Album> localList) {
									if (AbStrUtil.isEmpty(content))
										content = "获取数据失败";
									showToast(content);
								}

								@Override
								public void onErrorData(
										String status_description) {
									if (AbStrUtil.isEmpty(status_description))
										status_description = "获取数据失败";
									showToast(status_description);
								}

								@Override
								public void onFinish() {
									removeProgressDialog();
								}

							});
				}

			});
			TextView mPlayText = (TextView) mPlayView
					.findViewById(R.id.mPlayText);
			imageLoader.setAnimation(true);
			imageLoader.display(mPlayImage,
					imageUrls[(imageUrls.length - 1 - i)]);
			mPlayText.setText("");
			mSlidingPlayView.addView(mPlayView);
		}

	}

	/**
	 * 获取二维码图片
	 */
	private void getCodeData() {
		String url = userCompany.getQr_code() + "&area=" + application.cityid;
		AbHttpUtil.getInstance(BusinessDetailActivity.this).get(url,
				new AbBinaryHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, byte[] content) {
						codeBitmap = AbImageUtil.bytes2Bimap(content);
						mView = mInflater.inflate(R.layout.code_view, null);

						ImageView UserCode = (ImageView) BusinessDetailActivity.this
								.findViewById(R.id.BCodeTopRightimageView);
						UserCode.setImageBitmap(codeBitmap);
						UserCode.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								showChosePopWindow();
							}
						});
					}

					// 开始执行前
					@Override
					public void onStart() {
						// 显示进度框
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
						// 移除进度框
						removeProgressDialog();
					};

				});
	}

	void getImgUrl(String m_id) {
		AlbumBll imgBll = new AlbumBll();
		imgBll.getAlbumList(BusinessDetailActivity.this, new AlbumReqEntity(0,
				10, m_id), new ZzObjectHttpResponseListener<Album>() {

			@Override
			public void onSuccess(int statusCode, List<Album> lis) {
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
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<Album> localList) {
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
			}

			@Override
			public void onFinish() {
				initImageView();
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
						if (userCompany.getFullname() == null
								|| "".equals(userCompany.getFullname())) {

						} else {
							Intent intent = new Intent(
									BusinessDetailActivity.this,
									CompanyMapActiviy.class);
							intent.putExtra("FUllNAME",
									userCompany.getFullname());
							intent.putExtra("LAT",
									Double.toString(userCompany.getLatitude()));
							intent.putExtra("LON",
									Double.toString(userCompany.getLongitude()));
							startActivity(intent);
						}
					}

				});
		// 点击了供求信息
		this.findViewById(R.id.imgCompanyBuySell).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BusinessDetailActivity.this,
								CompanyBuySellActivity.class);
						intent.putExtra("FUllNAME", userCompany.getFullname());
						intent.putExtra("MEMBER_ID", userCompany.getMember_id());
						startActivity(intent);

					}

				});
		//
		this.findViewById(R.id.imgBussnissPhone).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
						intent.setData(Uri.parse("tel:"
								+ userCompany.getTelephone()));
						startActivity(intent);
					}
				});
		// 商家logo
		this.findViewById(R.id.companyLogoImage).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!AbStrUtil.isEmpty(userCompany.getLogo())) {
							Intent intent = new Intent(
									BusinessDetailActivity.this,
									ImagePagerActivity.class);
							intent.putExtra("imageUrls",
									new String[] { userCompany.getLogo() });
							startActivity(intent);
						} else {
							showToast("该用户没有设置Logo");
						}
						/*
						 * mView =
						 * BusinessDetailActivity.this.mInflater.inflate(
						 * R.layout.code_view, null); ImageView imageUser =
						 * (ImageView) mView.
						 * findViewById(R.id.imageViewCodeCP); if
						 * (!AbStrUtil.isEmpty(userCompany.getLogo())) { new
						 * AbImageDownloader(BusinessDetailActivity.this).
						 * display(imageUser, userCompany.getLogo()); }
						 * BusinessDetailActivity
						 * .this.showDialog(AbConstant.DIALOGCENTER, mView);
						 * BusinessDetailActivity
						 * .this.removeDialog(AbConstant.DIALOGCENTER);
						 * imageUser.setOnClickListener(new OnClickListener() {
						 * 
						 * @Override public void onClick(View v) {
						 * BusinessDetailActivity
						 * .this.removeDialog(AbConstant.DIALOGCENTER); } });
						 * BusinessDetailActivity
						 * .this.showDialog(AbConstant.DIALOGCENTER, mView);
						 */
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

		this.findViewById(R.id.imageViewMoewPhone).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (AbStrUtil.isEmpty(member_id)) {
							return;
						}
						Intent intent = new Intent(BusinessDetailActivity.this,
								MorePhoneActivity.class);
						intent.putExtra("MEMBER_ID", member_id);
						startActivity(intent);
					}
				});
		// 分享
		this.findViewById(R.id.imgbusnessshare).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mController == null) {
							mController = UMServiceFactory.getUMSocialService(
									Constant.UMentData.DESCRIPTOR,
									RequestType.SOCIAL);
							mController.getConfig().setSsoHandler(
									new SinaSsoHandler());
							mController.getConfig().setSsoHandler(
									new TencentWBSsoHandler());
							mController.getConfig().setSsoHandler(
									new QZoneSsoHandler(
											BusinessDetailActivity.this,
											getResources().getString(
													R.string.QQ_APP_ID),
											getResources().getString(
													R.string.QQ_APP_KEY)));
							// qq
							mController.getConfig().supportQQPlatform(
									BusinessDetailActivity.this,
									getResources()
											.getString(R.string.QQ_APP_ID),
									getResources().getString(
											R.string.QQ_APP_KEY),
									"http://m.321hy.cn");

							// weixin
							// 微信图文分享必须设置一个url
							// 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID,
							// 参数3为点击分享内容跳转到的目标url
							UMWXHandler wxHandler = mController.getConfig()
									.supportWXPlatform(
											BusinessDetailActivity.this,
											getResources().getString(
													R.string.Weixin_APP_ID),
											"http://m.321hy.cn");
							// 设置分享标题
							wxHandler.setWXTitle(userCompany.getFullname());
							// 支持微信朋友圈
							UMWXHandler circleHandler = mController.getConfig()
									.supportWXCirclePlatform(
											BusinessDetailActivity.this,
											getResources().getString(
													R.string.Weixin_APP_ID),
											"http://m.321hy.cn");
							circleHandler.setCircleTitle(userCompany
									.getFullname() + "http://m.321hy.cn");

							// 人人
							mController.setAppWebSite(SHARE_MEDIA.RENREN,
									"http://m.321hy.cn/");

							// 通用内容
							UMImage mImage = new UMImage(
									BusinessDetailActivity.this, userCompany
											.getLogo());
							mImage.setTitle(userCompany.getFullname());
							// http://m.321hy.cn/530100/enterprise/detail/167776
							mImage.setTargetUrl("http://m.321hy.cn/"
									+ application.cityid
									+ "/enterprise/detail/"
									+ userCompany.getMember_id());

							WeiXinShareContent weixinContent = new WeiXinShareContent();
							weixinContent.setShareContent(userCompany
									.getFullname() + "。http://m.321hy.cn");
							weixinContent.setTitle(userCompany.getFullname());
							weixinContent.setTargetUrl("http://m.321hy.cn/"
									+ application.cityid
									+ "/enterprise/detail/"
									+ userCompany.getMember_id());
							weixinContent.setShareImage(mImage);
							mController.setShareMedia(weixinContent);

							// 设置朋友圈分享的内容
							CircleShareContent circleMedia = new CircleShareContent();
							circleMedia.setShareContent(userCompany
									.getFullname() + "。http://m.321hy.cn");
							circleMedia.setTitle(userCompany.getFullname());
							circleMedia.setTargetUrl("http://m.321hy.cn/"
									+ application.cityid
									+ "/enterprise/detail/"
									+ userCompany.getMember_id());
							circleMedia.setShareImage(mImage);
							mController.setShareMedia(circleMedia);

							// 设置分享内容
							mController.setShareContent(userCompany
									.getFullname() + "。http://m.321hy.cn");
							// 设置分享图片, 参数2为图片的url地址
							mController.setShareMedia(mImage);

						}
						mController.openShare(BusinessDetailActivity.this,
								false);
					}
				});
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		// LayoutInflater mInflater = getLayoutInflater();
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
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(LinearLayout.VERTICAL);
		WebView webView = new WebView(this);
		webView.setFocusable(false);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		if (AbStrUtil.isEmpty(text)) {
			text = "用户暂时还未添加该项数据";
		}
		webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);

		lay.addView(webView);
		return lay;
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
			// Animation animation = null;

			t1.setTextColor(getResources().getColor(R.color.black));

			t2.setTextColor(getResources().getColor(R.color.black));
			t4.setTextColor(getResources().getColor(R.color.black));
			switch (arg0) {
			case 0: {
				t1.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 1) {
					// animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					// animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			}
			case 1: {
				t2.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					// animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			}
			case 2: {
				t4.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					// animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			case 3: {
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					// animation = new TranslateAnimation(one, two, 0, 0);
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

	public void showChosePopWindow() {
		View mChooseView = BusinessDetailActivity.this.mInflater.inflate(
				R.layout.choose_lookimage, null);
		BusinessDetailActivity.this.showDialog(1, mChooseView);
		// 查看
		mChooseView.findViewById(R.id.choose_lookimage).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mView = BusinessDetailActivity.this.mInflater.inflate(
								R.layout.code_view, null);
						ImageView imageUserCode = (ImageView) mView
								.findViewById(R.id.imageViewCodeCP);
						imageUserCode.setImageBitmap(codeBitmap);
						BusinessDetailActivity.this.removeDialog(1);
						BusinessDetailActivity.this.showDialog(
								AbConstant.DIALOGCENTER, mView);

						imageUserCode.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BusinessDetailActivity.this
										.removeDialog(AbConstant.DIALOGCENTER);
							}

						});
					}

				});
		// 保存
		mChooseView.findViewById(R.id.choose_saveimagecode).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						BusinessDetailActivity.this.removeDialog(1);
						String imgUrl = MediaStore.Images.Media.insertImage(
								getContentResolver(), codeBitmap, "", "");
						Log.e("save codeimage", imgUrl);
						removeDialog(AbConstant.DIALOGCENTER);
						showToast("二维码成功保存到相册！");

					}
				});
		// 取消
		mChooseView.findViewById(R.id.choose_cancelimage).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						BusinessDetailActivity.this.removeDialog(1);
					}
				});
	}

	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == 42)
			this.finish();
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 42)
			this.finish();
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
}
