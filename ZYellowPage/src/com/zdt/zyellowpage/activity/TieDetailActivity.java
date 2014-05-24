package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.util.AbStrUtil;
import com.ab.view.sliding.AbSlidingPlayView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.login.LoginActivity;
import com.zdt.zyellowpage.bll.TieBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Tie;
import com.zdt.zyellowpage.util.DisplayUtil;

public class TieDetailActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String item_id;
	private Tie mTie;
	private AbSlidingPlayView mSlidingPlayView = null;
	private String[] imageUrl;
	private DisplayUtil displayUtil;
	private String type;
	private TextView moreTextView;
	private ImageView imgLogo;
	private RelativeLayout layMain;
	private ImageView imageUserCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_tiedetail);
		application = (MyApplication) abApplication;
		layMain = (RelativeLayout) findViewById(R.id.layMain);

		if (getIntent().getExtras() != null) {
			item_id = (String) getIntent().getExtras().get("ITEM_ID");
			type = (String) getIntent().getExtras().get("TIE_TYPE");
			if (item_id == null || type == null) {
				showToast("参数错误");
				this.finish();
			} else {
				getData();
				mAbTitleBar = this.getTitleBar();
				mAbTitleBar.setTitleText("详细信息");
				mAbTitleBar.setLogo(R.drawable.button_selector_back);
				mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
				mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
				// mAbTitleBar.setLogoLine(R.drawable.line);
				initTypeView();
				// 在标题栏添加参加按钮
				mAbTitleBar.clearRightView();
				TextView tvSave = new TextView(this);
				tvSave.setText("+参加  ");
				tvSave.setTextColor(Color.WHITE);
				tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				tvSave.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent;
						if (application.mUser == null
								|| application.mUser.getToken() == null) {
							TieDetailActivity.this.showToast("请先登录");
							intent = new Intent(TieDetailActivity.this,
									LoginActivity.class);
							startActivity(intent);
						} else {

							intent = new Intent(TieDetailActivity.this,
									EditTieMessageActivity.class);
							intent.putExtra("ITEM_ID", mTie.getItem_id());
							intent.putExtra("TYPE", mTie.getType());
							intent.putExtra("PASSWORD", mTie.getPassword());
							startActivity(intent);
						}
					}

				});
				mAbTitleBar.addRightView(tvSave);
				mAbTitleBar
						.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

				mSlidingPlayView = (AbSlidingPlayView) this
						.findViewById(R.id.mAbSlidingPlayView);
				displayUtil = DisplayUtil.getInstance(TieDetailActivity.this);
				DisplayMetrics metric = new DisplayMetrics();
				this.getWindowManager().getDefaultDisplay().getMetrics(metric);
				int width = metric.widthPixels / 4 * 3;
				displayUtil.setViewLayoutParamsL(mSlidingPlayView, 0, width);
				mSlidingPlayView.setPageLineHorizontalGravity(Gravity.RIGHT);
			}
		} else {
			showToast("参数错误");
			this.finish();
		}

	}

	private void getData() {
		TieBll tieBll = new TieBll();
		tieBll.getDetailOfTie(TieDetailActivity.this, item_id,
				new ZzObjectHttpResponseListener<Tie>() {
					@Override
					public void onSuccess(int statusCode, List<Tie> lis) {
						if (lis == null || lis.size() == 0) {
							return;
						}
						mTie = (Tie) lis.get(0);
						initView();
						// svMain.setVisibility(View.VISIBLE);
						layMain.setVisibility(View.VISIBLE);
					}

					@Override
					public void onStart() {
						showProgressDialog("正在获取详细信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Tie> localList) {
						showToast(content);
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

	private void initTypeView() {
		if ("婚庆".equals(type)) {
			return;
		} else if ("乔迁".equals(type)) {
			this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(
					View.GONE);
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(
					View.GONE);
			TextView contentextView = (TextView) this
					.findViewById(R.id.tie_content_TextView);
			contentextView.setText("新居简介");
		} else if ("聚会".equals(type)) {
			this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(
					View.GONE);
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(
					View.GONE);
			TextView contentextView = (TextView) this
					.findViewById(R.id.tie_content_TextView);
			contentextView.setText("回忆录");
		} else if ("开业".equals(type)) {
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(
					View.GONE);
			TextView remarktextView = (TextView) this
					.findViewById(R.id.tie_remark_textView);
			remarktextView.setText("商企简介");
			TextView contentextView = (TextView) this
					.findViewById(R.id.tie_content_TextView);
			contentextView.setText("发展方向");

		} else if ("庆典".equals(type)) {
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(
					View.GONE);
			TextView remarktextView = (TextView) this
					.findViewById(R.id.tie_remark_textView);
			remarktextView.setText("发展历程");
			TextView contentextView = (TextView) this
					.findViewById(R.id.tie_content_TextView);
			contentextView.setText("经营成果");

		}
	}

	private void initView() {
		WebView remark = (WebView) this.findViewById(R.id.tie_remark_WebView);
		remark.getSettings().setDefaultTextEncodingName("UTF-8");
		if (AbStrUtil.isEmpty(mTie.getRemark())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html",
					"utf-8", null);
			// this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(View.GONE);
		} else {
			remark.loadDataWithBaseURL(null, mTie.getRemark(), "text/html",
					"utf-8", null);
		}

		WebView other = (WebView) this.findViewById(R.id.tie_other_WebView);
		if (AbStrUtil.isEmpty(mTie.getOther())) {
			other.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html",
					"utf-8", null);
		} else {
			other.loadDataWithBaseURL(null, mTie.getOther(), "text/html",
					"utf-8", null);
		}

		WebView content = (WebView) this.findViewById(R.id.tie_content_WebView);
		if (AbStrUtil.isEmpty(mTie.getContent())) {
			content.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html",
					"utf-8", null);
		} else {
			content.loadDataWithBaseURL(null, mTie.getContent(), "text/html",
					"utf-8", null);
		}

		WebView more = (WebView) this.findViewById(R.id.tie_more_WebView);
		// more.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// height));
		if (AbStrUtil.isEmpty(mTie.getMore())) {
			this.findViewById(R.id.tie_more_LinearLayout).setVisibility(
					View.GONE);
		} else {
			more.loadDataWithBaseURL(null, mTie.getMore(), "text/html",
					"utf-8", null);
		}
		if (mTie.getLogo() != null) {
			imgLogo = (ImageView) this.findViewById(R.id.tieLogoImage);
			new AbImageDownloader(this).display(imgLogo, mTie.getLogo());
		}

		if (mTie.getQr_code() != null) {
			ImageView codeImage = (ImageView) this
					.findViewById(R.id.TCodeTopRightimageView);
			new AbImageDownloader(this).display(codeImage, mTie.getQr_code());
		}
		TextView fullName = (TextView) this.findViewById(R.id.tiefullname);
		fullName.setText(mTie.getTitle());
		more.setVisibility(View.GONE);
		moreTextView = (TextView) this.findViewById(R.id.tie_more_TextView);
		TextView time = (TextView) this.findViewById(R.id.tie_detail_time);
		time.setText(mTie.getTime());
		TextView phone = (TextView) this.findViewById(R.id.tie_detail_phone);
		phone.setText(mTie.getTelephone());
		TextView address = (TextView) this
				.findViewById(R.id.tie_detail_address);
		address.setText(mTie.getAddress());
		initImageView();
		initEvent();
	}

	private void initImageView() {

		imageUrl = mTie.getAlbum().split(",");
		if (imageUrl.length < 1) {
			this.findViewById(R.id.mAbSlidingPlayViewLinearLayout)
					.setVisibility(View.GONE);
			return;
		}

		for (int i = 0; i < imageUrl.length; i++) {
			View mPlayView = new View(TieDetailActivity.this);
			mPlayView = mInflater.inflate(R.layout.play_view_item, null);
			ImageView mPlayImage = (ImageView) mPlayView
					.findViewById(R.id.mPlayImage);
			mPlayImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(TieDetailActivity.this,
							ImagePagerActivity.class);
					intent.putExtra("imageUrls", imageUrl);
					startActivity(intent);
				}

			});
			TextView mPlayText = (TextView) mPlayView
					.findViewById(R.id.mPlayText);
			new AbImageDownloader(TieDetailActivity.this).display(mPlayImage,
					imageUrl[i]);
			mPlayText.setText("");
			mSlidingPlayView.addView(mPlayView);
		}
	}

	private void initEvent() {
		this.findViewById(R.id.tie_image_phone).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + mTie.getTelephone()));
						startActivity(intent);
					}

				});

		this.findViewById(R.id.tie_image_loc).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(TieDetailActivity.this,
								CompanyMapActiviy.class);
						intent.putExtra("FUllNAME", mTie.getAddress());
						intent.putExtra("LAT", mTie.getLatitude());
						intent.putExtra("LON", mTie.getLongitude());
						startActivity(intent);
					}

				});
		this.findViewById(R.id.TCodeTopRightimageView).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						View mView = mInflater
								.inflate(R.layout.code_view, null);
						imageUserCode = (ImageView) mView
								.findViewById(R.id.imageViewCodeCP);
						new AbImageDownloader(TieDetailActivity.this).display(
								imageUserCode, mTie.getQr_code());
						//imageUserCode
						mView.findViewById(R.id.closeCodeImageTextView)
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 点击结束弹出框
								removeDialog(AbConstant.DIALOGCENTER);
							}
						});
						//imageUserCode.setLongClickable(true);
						//imageUserCode
						mView.findViewById(R.id.saveCodeImageTextView)
							.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
										
									imageUserCode.setDrawingCacheEnabled(true);
									Bitmap codeBitmap = Bitmap.createBitmap(imageUserCode.getDrawingCache());
									imageUserCode.setDrawingCacheEnabled(false);
									String imgUrl =  MediaStore.Images.
									Media.insertImage(getContentResolver(), codeBitmap, "", "");   
									Log.e("save codeimage", imgUrl);
									removeDialog(AbConstant.DIALOGCENTER);
									TieDetailActivity.this.showToast("二维码成功保存到相册！");
									
								}

								});
						showDialog(AbConstant.DIALOGCENTER, mView);
					}

				});
		moreTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TieDetailActivity.this.findViewById(R.id.tie_more_WebView)
						.getVisibility() == View.VISIBLE) {
					moreTextView.setText("更多▼");
					TieDetailActivity.this.findViewById(R.id.tie_more_WebView)
							.setVisibility(View.GONE);
				} else {
					moreTextView.setText("更多▲");
					TieDetailActivity.this.findViewById(R.id.tie_more_WebView)
							.setVisibility(View.VISIBLE);
				}
			}
		});
	}
}
