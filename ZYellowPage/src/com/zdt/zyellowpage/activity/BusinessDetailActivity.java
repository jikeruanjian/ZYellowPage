package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class BusinessDetailActivity extends AbActivity implements OnClickListener {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String member_id;
	private User userCompany;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String[] imageUrls = new String[] {
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
		imageLoader.init(ImageLoaderConfiguration.createDefault(BusinessDetailActivity.this));
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
		TextView qyjs = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_qyjs);
		TextView jyfw = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_jyfw);
		TextView gddh = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_gddh);
		TextView tpzs = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_tpzs);
		TextView qysp = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_qysp);
		TextView sjewm = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_sjewm);
		TextView sjjwd = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_sjjwd);
		name.setText(userCompany.getFullname());
		num.setText(userCompany.getMember_id());
		userName.setText(userCompany.getUsername());
		tel.setText(userCompany.getTelephone());
		url.setText(userCompany.getWebsite());
		address.setText(userCompany.getAddress());
		email.setText(userCompany.getEmail());///////////////
		qq.setText(userCompany.getQq());
		qyjs.setText(userCompany.getSummary());
		jyfw.setText(userCompany.getScope());
		
		gddh.setText(userCompany.getEmail());
		tpzs.setText(userCompany.getEmail());
		qysp.setText(userCompany.getEmail());
		
		sjewm.setText(userCompany.getQr_code());//二维码地址
		sjjwd.setText("经度："+userCompany.getLatitude()+"      纬度:"+userCompany.getLongitude());
		
		TextView info = (TextView) BusinessDetailActivity.this
				.findViewById(R.id.user_company_info);
		info.setText(userCompany.getDiscount());//优惠   供求   其它
		info.setText(userCompany.getSummary());
		info.setText(userCompany.getDiscount());
		
		RadioButton radioButton = (RadioButton) BusinessDetailActivity.this
		.findViewById(R.id.user_company_info_btn);
		RadioButton radioButton1 = (RadioButton) BusinessDetailActivity.this
		.findViewById(R.id.user_company_gongqiu_btn);
		RadioButton radioButton2 = (RadioButton) BusinessDetailActivity.this
		.findViewById(R.id.user_company_other_btn);
		radioButton.setOnClickListener(this);
		radioButton1.setOnClickListener(this);
		radioButton2.setOnClickListener(this);
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
						userCompany = lis.get(0);
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
						userCompany = localList.get(0);
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
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								startImagePagerActivity(position);
							}

							private void startImagePagerActivity(int position) {
								Intent intent = new Intent(BusinessDetailActivity.this, ImagePagerActivity.class);
								intent.putExtra("imageUrls", imageUrls);
								startActivity(intent);
							}

						});
						getView();
					}
					/*
					 * @Override public void onSuccess(int statusCode,
					 * List<Object> lis) { // TODO Auto-generated method stub
					 * userCompany = (User)lis.get(0); }
					 * 
					 * @Override public void onStart() { // TODO Auto-generated
					 * method stub showProgressDialog("同步信息..."); }
					 * 
					 * @Override public void onFailure(int statusCode, String
					 * content, Throwable error) { // TODO Auto-generated method
					 * stub showToast(error.getMessage()); }
					 * 
					 * @Override public void onErrorData(String
					 * status_description) { // TODO Auto-generated method stub
					 * showToast(status_description); }
					 * 
					 * @Override public void onFinish() { // TODO Auto-generated
					 * method stub removeProgressDialog();
					 * //showToast(userCompany.getFullname());
					 * 
					 * TextView name=
					 * (TextView)BusinessDetailActivity.this.findViewById
					 * (R.id.companyfullname);
					 * name.setText(userCompany.getFullname()); }
					 */
				});
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
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(imageUrls[position], imageView, options);
			return imageView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_company_info_btn:
			
			break;
		case R.id.user_company_gongqiu_btn:
			
			break;
		case R.id.user_company_other_btn:

			break;

		default:
			break;
		}
		
	}
}
