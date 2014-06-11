package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.ab.view.titlebar.AbTitleBar;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMWXHandler;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.CertificateBll;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Certificate;
import com.zdt.zyellowpage.model.User;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class PersonDetailActivity extends AbActivity implements
		ISimpleDialogListener, ISimpleDialogCancelListener {
	public static String TAG = "PersonDetailActivity";
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String member_id;
	private User userPerson;
	// private ImageLoader imageLoader = ImageLoader.getInstance();
	// private DisplayImageOptions options;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private TextView t1, t2, t3, t4;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String certificateStr = "";
	Bitmap codeBitmap;
	View mCodeView;
	RelativeLayout layMain;
	ImageView imageUserLogo;
	// 二维码弹出框
	private View mView;
	// sdk controller
	private UMSocialService mController = null;

	// private String[] imageUrls = new String[] { };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_persondetail);
		application = (MyApplication) abApplication;

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("详细信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		layMain = (RelativeLayout) findViewById(R.id.layMain);
		InitTitleView();
		userPerson = new User();
		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
			if (member_id == null) {
				SimpleDialogFragment
						.createBuilder(this, getSupportFragmentManager())
						.setTitle("错误").setMessage("参数错误")
						.setPositiveButtonText("返回").setTag(TAG)
						.setRequestCode(42).show();
			} else {
				getData();
				// 姓名、性别、年龄、所在地、民族、电话、电子邮箱、编号、QQ、学校、专业、行业、
				// 关键词、地址、个人资质、个人特长、个人简介、成功案例、个人二维码、附件地址
				// imageLoader.init(ImageLoaderConfiguration
				// .createDefault(PersonDetailActivity.this));
			}
		}

	}

	/**
	 * 获取个人详细信息
	 */
	protected void getData() {
		UserBll bll = new UserBll();
		bll.getDetailPerson(PersonDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<User>() {

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						if (lis == null || lis.size() == 0) {
							PersonDetailActivity.this.showToast("获取详细信息失败！");
							return;
						}
						userPerson = lis.get(0);
						layMain.setVisibility(View.VISIBLE);
						getDataCertificate();
						getView();
						getDataCode();
					}

					@Override
					public void onStart() {
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						SimpleDialogFragment
								.createBuilder(PersonDetailActivity.this,
										getSupportFragmentManager())
								.setTitle("错误").setMessage("数据获取失败")
								.setPositiveButtonText("返回").setTag(TAG)
								.setRequestCode(42).show();
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

	/**
	 * 获取二维码图片
	 */
	void getDataCode() {
		String url = userPerson.getQr_code() + "&area=" + application.cityid;
		AbHttpUtil.getInstance(PersonDetailActivity.this).get(url,
				new AbBinaryHttpResponseListener() {

					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, byte[] content) {
						codeBitmap = AbImageUtil.bytes2Bimap(content);

						ImageView UserCode = (ImageView) PersonDetailActivity.this
								.findViewById(R.id.codeTopRightimageView);
						UserCode.setImageBitmap(codeBitmap);
						UserCode.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// PersonDetailActivity.this.showDialog(AbConstant.DIALOGCENTER,
								// mCodeView);
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

	/**
	 * 获取资质
	 */
	void getDataCertificate() {
		CertificateBll certificateBll = new CertificateBll();
		certificateBll.getCertificateList(PersonDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<Certificate>() {

					@Override
					public void onSuccess(int statusCode, List<Certificate> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						StringBuffer strCertificate = new StringBuffer();
						for (Certificate cer : lis) {
							strCertificate.append(cer.getCertificate_name()
									+ ":" + cer.getCertificate_no() + "<br/>");
						}
						certificateStr = strCertificate.toString();
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Certificate> localList) {
						PersonDetailActivity.this.showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						PersonDetailActivity.this.showToast(status_description);
					}

					@Override
					public void onFinish() {
						InitViewPager();
					}

				});
	}

	protected void getView() {
		imageUserLogo = (ImageView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_photo);
		new AbImageDownloader(PersonDetailActivity.this).display(imageUserLogo,
				userPerson.getLogo());
		TextView nametitle = (TextView) PersonDetailActivity.this
				.findViewById(R.id.personFullNametextView);
		TextView name = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_name);
		TextView sex = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_sex);
		TextView age = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_age);
		TextView adress = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_address);
		TextView mz = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_mz);
		TextView tel = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_tel);
		TextView email = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_email);
		TextView num = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_num);
		TextView qq = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_qq);
		TextView school = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_school);
		TextView zy = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_zy);
		TextView hy = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_hy);
		// TextView gjc=
		// (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_gjc);
		TextView dz = (TextView) PersonDetailActivity.this
				.findViewById(R.id.person_detail_dz);
		// TextView grewm=
		// (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_grewm);
		// TextView fjdz=
		// (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_fjdz);
		// TextView info=
		// (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_INFO);
		nametitle.setText(userPerson.getFullname());
		name.setText(userPerson.getFullname());
		sex.setText(userPerson.getSex_name());
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
		// gjc.setText(userPerson.getKeyword());
		dz.setText(userPerson.getAddress());
		// grewm.setText(userPerson.getQr_code());
		// fjdz.setText(userPerson.getSchool());

		// info.setText(userPerson.getSummary());
		InitTextView();
		// InitViewPager();
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
					bll.followUser(PersonDetailActivity.this,
							application.mUser.getToken(),
							userPerson.getMember_id(), false,
							new ZzStringHttpResponseListener() {

								@Override
								public void onSuccess(int statusCode,
										String content) {
									Toast.makeText(PersonDetailActivity.this,
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
					Toast.makeText(PersonDetailActivity.this, "请先登录！",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(PersonDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
			}
		});
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
		this.findViewById(R.id.person_detail_phone).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
						intent.setData(Uri.parse("tel:"
								+ userPerson.getTelephone()));
						startActivity(intent);
					}
				});

		this.findViewById(R.id.person_detail_photo).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mView = PersonDetailActivity.this.mInflater.inflate(
								R.layout.code_view, null);
						ImageView imageUserCode = (ImageView) mView
								.findViewById(R.id.imageViewCodeCP);
						new AbImageDownloader(PersonDetailActivity.this)
								.display(imageUserCode, userPerson.getLogo());
						PersonDetailActivity.this.removeDialog(1);
						PersonDetailActivity.this.showDialog(
								AbConstant.DIALOGCENTER, mView);
						imageUserCode.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								PersonDetailActivity.this
										.removeDialog(AbConstant.DIALOGCENTER);
							}

						});
					}
				});
		// 分享
		this.findViewById(R.id.person_detail_Share).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mController == null) {
							mController = UMServiceFactory.getUMSocialService(
									Constant.UMentData.DESCRIPTOR,
									RequestType.SOCIAL);
							// qq
							mController.getConfig().supportQQPlatform(
									PersonDetailActivity.this,
									getResources()
											.getString(R.string.QQ_APP_ID),
									getResources().getString(
											R.string.QQ_APP_KEY),
									"http://www.321hy.cn");

							// weixin
							// 微信图文分享必须设置一个url
							// 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID,
							// 参数3为点击分享内容跳转到的目标url
							UMWXHandler wxHandler = mController.getConfig()
									.supportWXPlatform(
											PersonDetailActivity.this,
											getResources().getString(
													R.string.Weixin_APP_ID),
											"http://www.321hy.cn");
							// 设置分享标题
							wxHandler.setWXTitle(userPerson.getFullname());
							// 支持微信朋友圈
							UMWXHandler circleHandler = mController.getConfig()
									.supportWXCirclePlatform(
											PersonDetailActivity.this,
											getResources().getString(
													R.string.Weixin_APP_ID),
											"http://www.321hy.cn");
							circleHandler.setCircleTitle(userPerson
									.getFullname() + "http://www.321hy.cn");

						}
						UMImage mImage = new UMImage(PersonDetailActivity.this,
								userPerson.getLogo());
						mImage.setTitle(userPerson.getFullname());
						// http://m.321hy.cn/530100/person/detail/609417
						mImage.setTargetUrl("http://www.321hy.cn/"
								+ application.cityid + "/person/detail/"
								+ userPerson.getMember_id());

						// 设置分享内容
						mController.setShareContent(userPerson.getFullname()
								+ "。http://www.321hy.cn");
						// 设置分享图片, 参数2为图片的url地址
						mController.setShareMedia(mImage);
						mController.openShare(PersonDetailActivity.this, false);
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
		listViews.add(addTextByText(userPerson.getSummary()));
		listViews.add(addTextByText(certificateStr));
		// listViews.add(addTextByText(userPerson.getProfessional()));
		listViews.add(addTextByText(userPerson.getSpecialty()));
		listViews.add(addTextByText(userPerson.getExperience()));
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
			t3.setTextColor(getResources().getColor(R.color.black));
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
				t3.setTextColor(getResources().getColor(R.color.orange));
				if (currIndex == 0) {
					// animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					// animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			case 3: {
				t4.setTextColor(getResources().getColor(R.color.orange));
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
		View mChooseView = PersonDetailActivity.this.mInflater.inflate(
				R.layout.choose_lookimage, null);
		PersonDetailActivity.this.showDialog(1, mChooseView);
		// 查看
		mChooseView.findViewById(R.id.choose_lookimage).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mView = PersonDetailActivity.this.mInflater.inflate(
								R.layout.code_view, null);
						ImageView imageUserCode = (ImageView) mView
								.findViewById(R.id.imageViewCodeCP);
						imageUserCode.setImageBitmap(codeBitmap);
						PersonDetailActivity.this.removeDialog(1);
						PersonDetailActivity.this.showDialog(
								AbConstant.DIALOGCENTER, mView);

						imageUserCode.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								PersonDetailActivity.this
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
						// TODO Auto-generated method stub
						PersonDetailActivity.this.removeDialog(1);
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
						// TODO Auto-generated method stub
						PersonDetailActivity.this.removeDialog(1);

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
