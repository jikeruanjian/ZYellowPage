package com.zdt.zyellowpage.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyOnClickListener;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyOnPageChangeListener;
import com.zdt.zyellowpage.activity.BusinessDetailActivity.MyPagerAdapter;
import com.zdt.zyellowpage.bll.CertificateBll;
import com.zdt.zyellowpage.bll.TieBll;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Certificate;
import com.zdt.zyellowpage.model.Tie;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.DisplayUtil;

public class TieDetailActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String item_id;
	private Tie mTie;
	private AbSlidingPlayView mSlidingPlayView = null;
	private String[] imageUrl;
	private DisplayUtil displayUtil;
	
	//private String[] imageUrls = new String[] { };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_tiedetail);
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("婚庆详细");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		
		if (getIntent().getExtras() != null) {
			item_id = (String) getIntent().getExtras().get("ITEM_ID");
			if(item_id == null){
				this.showDialog("错误", "数据获取失败", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
			}
			else{
				getData();
//				姓名、性别、年龄、所在地、民族、电话、电子邮箱、编号、QQ、学校、专业、行业、
//				关键词、地址、个人资质、个人特长、个人简介、成功案例、个人二维码、附件地址
			
			}
		}
		
	}
	
	
	private void getData(){
		TieBll tieBll = new TieBll();
		tieBll.getDetailOfTie(TieDetailActivity.this, item_id, 
					new ZzObjectHttpResponseListener<Tie>(){

						@Override
						public void onSuccess(int statusCode, List<Tie> lis) {
							// TODO Auto-generated method stub
							if (lis == null || lis.size() == 0) {
								TieDetailActivity.this.showToast("获取详细信息失败！");
								return;
							}
							mTie = (Tie)lis.get(0);
							initView();
						}

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error, List<Tie> localList) {
							// TODO Auto-generated method stub
							
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
 
	private void initView(){
		WebView remark= (WebView)this.findViewById(R.id.tie_remark_WebView);
		remark.getSettings().setDefaultTextEncodingName("UTF-8");
		if (AbStrUtil.isEmpty(mTie.getRemark())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		remark.loadDataWithBaseURL(null, mTie.getRemark(), "text/html","utf-8", null);
		
		WebView other= (WebView)this.findViewById(R.id.tie_other_WebView);
		if (AbStrUtil.isEmpty(mTie.getOther())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		other.loadDataWithBaseURL(null, mTie.getOther(), "text/html","utf-8", null);
		
		WebView content= (WebView)this.findViewById(R.id.tie_content_WebView);
		if (AbStrUtil.isEmpty(mTie.getContent())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		content.loadDataWithBaseURL(null, mTie.getContent(), "text/html","utf-8", null);
		
		WebView more= (WebView)this.findViewById(R.id.tie_more_WebView);
		if (AbStrUtil.isEmpty(mTie.getMore())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		more.loadDataWithBaseURL(null, mTie.getMore(), "text/html","utf-8", null);
		
		TextView time= (TextView)this.findViewById(R.id.tie_detail_time);
		time.setText(mTie.getTime());
		TextView phone= (TextView)this.findViewById(R.id.tie_detail_phone);
		phone.setText(mTie.getTelephone());
		TextView address= (TextView)this.findViewById(R.id.tie_detail_address);
		address.setText(mTie.getAddress()); 
		initImageView();
	}
	private void initImageView() {
		mSlidingPlayView =  (AbSlidingPlayView)this.findViewById(R.id.mAbSlidingPlayView);
		displayUtil = DisplayUtil.getInstance(TieDetailActivity.this);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels/4*3;
		displayUtil.setViewLayoutParamsL(mSlidingPlayView,0,width);
		mSlidingPlayView.setPageLineHorizontalGravity(Gravity.RIGHT);
		imageUrl = mTie.getAlbum().split(",");
		for(int i = 0;i < imageUrl.length;i++){
			Log.e("tie", "------"+imageUrl[i]);
			View mPlayView = new View(TieDetailActivity.this);
			mPlayView = mInflater.inflate(R.layout.play_view_item, null);
			ImageView mPlayImage = (ImageView) mPlayView.findViewById(R.id.mPlayImage);
			TextView mPlayText = (TextView) mPlayView.findViewById(R.id.mPlayText);
			new AbImageDownloader(TieDetailActivity.this).display(mPlayImage,imageUrl[i]);
			mPlayText.setText(i+"");		
			mSlidingPlayView.addView(mPlayView);
		}
	}
}
	