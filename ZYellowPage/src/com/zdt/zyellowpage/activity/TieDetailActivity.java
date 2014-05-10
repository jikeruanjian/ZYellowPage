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
	private String type;
	private TextView moreTextView;
	//private String[] imageUrls = new String[] { };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_tiedetail);
		application = (MyApplication) abApplication;
		
		if (getIntent().getExtras() != null) {
			item_id = (String) getIntent().getExtras().get("ITEM_ID");
			type = (String) getIntent().getExtras().get("TIE_TYPE");
			if(item_id == null){
				this.showDialog("错误", "数据获取失败", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
			}
			else{
				getData();
				mAbTitleBar = this.getTitleBar();
				mAbTitleBar.setTitleText(type+"详细");
				mAbTitleBar.setLogo(R.drawable.button_selector_back);
				mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
				mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
				mAbTitleBar.setLogoLine(R.drawable.line);
				initTypeView();
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
 
	private void initTypeView(){
		if("婚庆".equals(type)){
			return;
		}
		else if("乔迁".equals(type) ){
			this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(View.GONE);
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(View.GONE);
			TextView contentextView =  (TextView)this.findViewById(R.id.tie_content_TextView);
			contentextView.setText("新居简介");
		}
		else if("聚会".equals(type)){
			this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(View.GONE);
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(View.GONE);
			TextView contentextView =  (TextView)this.findViewById(R.id.tie_content_TextView);
			contentextView.setText("回忆录");
		}
		else if("开业".equals(type) ){
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(View.GONE);
			TextView remarktextView =  (TextView)this.findViewById(R.id.tie_remark_textView);
			remarktextView.setText("商企简介");
			TextView contentextView =  (TextView)this.findViewById(R.id.tie_content_TextView);
			contentextView.setText("发展方向");
			
		}
		else if("庆典".equals(type)){
			this.findViewById(R.id.tie_other_LinearLayout).setVisibility(View.GONE);
			TextView remarktextView =  (TextView)this.findViewById(R.id.tie_remark_textView);
			remarktextView.setText("发展历程");
			TextView contentextView =  (TextView)this.findViewById(R.id.tie_content_TextView);
			contentextView.setText("经营成果");
			
		}
	}
	
	
	private void initView(){
		WebView remark= (WebView)this.findViewById(R.id.tie_remark_WebView);
		remark.getSettings().setDefaultTextEncodingName("UTF-8");
		if (AbStrUtil.isEmpty(mTie.getRemark())) {
			remark.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
			//this.findViewById(R.id.tie_remark_LinearLayout).setVisibility(View.GONE);
		}
		else{
			remark.loadDataWithBaseURL(null, mTie.getRemark(), "text/html","utf-8", null);
		}
		
		
		WebView other= (WebView)this.findViewById(R.id.tie_other_WebView);
		if (AbStrUtil.isEmpty(mTie.getOther())) {
			other.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		else{
			other.loadDataWithBaseURL(null, mTie.getOther(), "text/html","utf-8", null);
		}
		
		WebView content= (WebView)this.findViewById(R.id.tie_content_WebView);
		if (AbStrUtil.isEmpty(mTie.getContent())) {
			content.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		else{
			content.loadDataWithBaseURL(null, mTie.getContent(), "text/html","utf-8", null);
		}
		
		WebView more= (WebView)this.findViewById(R.id.tie_more_WebView);
		if (AbStrUtil.isEmpty(mTie.getMore())) {
			more.loadDataWithBaseURL(null, "用户暂时还未添加该项数据", "text/html","utf-8", null);
		}
		else{
			more.loadDataWithBaseURL(null, mTie.getMore(), "text/html","utf-8", null);
		}
		more.setVisibility(View.GONE);
		moreTextView = (TextView)this.findViewById(R.id.tie_more_TextView);
		TextView time= (TextView)this.findViewById(R.id.tie_detail_time);
		time.setText(mTie.getTime());
		TextView phone= (TextView)this.findViewById(R.id.tie_detail_phone);
		phone.setText(mTie.getTelephone());
		TextView address= (TextView)this.findViewById(R.id.tie_detail_address);
		address.setText(mTie.getAddress()); 
		initImageView();
		initEvent();
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
	
	private void initEvent(){
		this.findViewById(R.id.tie_image_join).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(TieDetailActivity.this,
								EditTieMessageActivity.class);
						intent.putExtra("ITEM_ID",mTie.getItem_id());
						intent.putExtra("TYPE",mTie.getType());
						intent.putExtra("PASSWORD",mTie.getPassword());
						startActivity(intent);
					}
					
				});
		this.findViewById(R.id.tie_image_phone).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(); 
			        	intent.setAction(Intent.ACTION_DIAL);   //android.intent.action.DIAL 
			        	intent.setData(Uri.parse("tel:" + mTie.getTelephone())); 
			        	startActivity(intent);  
					}
					
				});
		
		this.findViewById(R.id.tie_image_loc).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(TieDetailActivity.this,CompanyMapActiviy.class);
						intent.putExtra("FUllNAME",mTie.getAddress());
						intent.putExtra("LAT", mTie.getLatitude());
						intent.putExtra("LON",  mTie.getLongitude());
						startActivity(intent);
					}
					
				});
		moreTextView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						if(TieDetailActivity.this.findViewById(R.id.tie_more_WebView)
								.getVisibility() == View.VISIBLE ){
							moreTextView.setText("更多▼"); 
							TieDetailActivity.this.findViewById(R.id.tie_more_WebView).setVisibility(View.GONE);
						}
						else{
							moreTextView.setText("更多▲"); 
							TieDetailActivity.this.findViewById(R.id.tie_more_WebView).setVisibility(View.VISIBLE);
						}
					}
					});
	}
}
	