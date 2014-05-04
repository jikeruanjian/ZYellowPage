package com.zdt.zyellowpage.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AlbumBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Album;

/**
 * 编辑相册
 * 
 * @author Kevin
 * 
 */
public class EditAlbumActivity extends AbActivity {

	private static final String STATE_POSITION = "STATE_POSITION";
	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ViewPager pager;
	private MyApplication application;
	AbTitleBar mAbTitleBar;
	AlbumBll albumBll = new AlbumBll();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.ac_image_pager);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(getIntent().getStringExtra("title"));
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initTitleRightLayout();

		pager.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		// TODO 先获取相册数据，判断是否有相册
		AlbumReqEntity are = new AlbumReqEntity(0, 50,
				application.mUser.getMember_id());
		albumBll.getAlbumList(this, are,
				new ZzObjectHttpResponseListener<Album>() {

					@Override
					public void onSuccess(int statusCode, List<Album> lis) {
						if (lis == null || lis.size() == 0) {
							showToast("当前还没有要展示 的图片，点击右上角的按钮添加更多");
							return;
						}
						options = new DisplayImageOptions.Builder()
								.showImageForEmptyUri(R.drawable.businessdetail)
								.showImageOnFail(R.drawable.businessdetail)
								.resetViewBeforeLoading(true).cacheOnDisc(true)
								.imageScaleType(ImageScaleType.EXACTLY)
								.bitmapConfig(Bitmap.Config.RGB_565)
								.considerExifParams(true)
								.displayer(new FadeInBitmapDisplayer(300))
								.build();
						// imageLoader.init(ImageLoaderConfiguration
						// .createDefault(EditAlbumActivity.this));

						pager = (ViewPager) findViewById(R.id.pager);
						pager.setAdapter(new ImagePagerAdapter(lis));
					}

					@Override
					public void onStart() {
						showProgressDialog();
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Album> localList) {
						options = new DisplayImageOptions.Builder()
								.showImageForEmptyUri(R.drawable.businessdetail)
								.showImageOnFail(R.drawable.businessdetail)
								.resetViewBeforeLoading(true).cacheOnDisc(true)
								.imageScaleType(ImageScaleType.EXACTLY)
								.bitmapConfig(Bitmap.Config.RGB_565)
								.considerExifParams(true)
								.displayer(new FadeInBitmapDisplayer(300))
								.build();
						imageLoader.init(ImageLoaderConfiguration
								.createDefault(EditAlbumActivity.this));

						pager = (ViewPager) findViewById(R.id.pager);
						pager.setAdapter(new ImagePagerAdapter(localList));
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}
				});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<Album> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<Album> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);

			imageLoader.displayImage(images.get(position).getUrl(), imageView,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "I/O 异常";
								break;
							case DECODING_ERROR:
								message = "不能打开此图片";
								break;
							case NETWORK_DENIED:
								message = "请求被拒绝";
								break;
							case OUT_OF_MEMORY:
								message = "内存溢出";
								break;
							case UNKNOWN:
								message = "未知错误";
								break;
							}
							showToast(message);

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	/**
	 * 添加 添加删除的按钮
	 */
	private void initTitleRightLayout() {
		View rightViewMore = mInflater.inflate(R.layout.more_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
	}
}