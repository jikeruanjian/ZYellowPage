package com.zdt.zyellowpage.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.ab.activity.AbActivity;
import com.ab.util.AbViewUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AlbumBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Album;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

/**
 * 编辑相册
 * 
 * @author Kevin
 * 
 */
public class EditAlbumActivity extends AbActivity implements
		ISimpleDialogListener {
	private static final String TAG = "EditAlbumActivity";
	private static final String STATE_POSITION = "STATE_POSITION";
	private static final int ADD_ALBUM = 10001;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ViewPager pager;
	private MyApplication application;
	AbTitleBar mAbTitleBar;
	AlbumBll albumBll = new AlbumBll();
	PopupWindow popupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.ac_image_pager);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(getIntent().getStringExtra("title"));
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initTitleRightLayout();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				return false;
			}
		});

		getData();

	}

	private void getData() {
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
						// imageLoader.init(ImageLoaderConfiguration
						// .createDefault(EditAlbumActivity.this));

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

		public Album getCurrentAlbum(int position) {
			return images.get(position);
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
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		Button moreBtn = (Button) rightViewMore.findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	private void showPopupWindow(View parent) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(
				EditAlbumActivity.this).inflate(R.layout.popdialog, null);
		ListView listView = (ListView) view.findViewById(R.id.listViewPopW);
		listView.setAdapter(new ArrayAdapter<String>(EditAlbumActivity.this,
				R.layout.text_itemselect, R.id.textViewSelectItemName,
				new String[] { "添加", "删除" }));

		AbViewUtil.measureView(view);
		int popWidth = parent.getMeasuredWidth();
		if (view.getMeasuredWidth() > parent.getMeasuredWidth()) {
			popWidth = view.getMeasuredWidth();
		}
		int popMargin = (mAbTitlebar.getMeasuredHeight() - parent
				.getMeasuredHeight()) / 2;

		popupWindow = new PopupWindow(view, popWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);

		// int[] location = new int[2];
		// parent.getLocationInWindow(location);
		// int startX = location[0] - parent.getLeft();
		// if (startX + popWidth >= diaplayWidth) {
		// int startX = diaplayWidth - popWidth - 5;
		// }

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(
				android.R.color.transparent));

		popupWindow.showAsDropDown(parent, -2, popMargin + 2);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				popupWindow.dismiss();
				if (position == 0) {
					if (pager.getAdapter().getCount() >= 10) {
						showToast("最多只能添加10张图片");
						return;
					}
					EditAlbumActivity.this.startActivityForResult(new Intent(
							EditAlbumActivity.this, AddAlbumActivity.class),
							ADD_ALBUM);
				} else {
					// EditAlbumActivity.this
					// .showDialog(
					// "确认",
					// "确认删除？",
					// new android.content.DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(
					// DialogInterface dialog,
					// int which) {
					// Album entity = ((ImagePagerAdapter) pager
					// .getAdapter())
					// .getCurrentAlbum(pager
					// .getCurrentItem());
					// Album tempEntity = new Album();
					// tempEntity.setItem_id("-"
					// + entity.getItem_id());
					//
					// new AlbumBll()
					// .updateAlbum(
					// EditAlbumActivity.this,
					// application.mUser
					// .getToken(),
					// tempEntity,
					// new ZzStringHttpResponseListener() {
					//
					// @Override
					// public void onSuccess(
					// int statusCode,
					// String content) {
					// showToast(content);
					// getData();
					// }
					//
					// @Override
					// public void onStart() {
					// showProgressDialog("正在删除...");
					//
					// }
					//
					// @Override
					// public void onFinish() {
					// removeProgressDialog();
					// }
					//
					// @Override
					// public void onFailure(
					// int statusCode,
					// String content,
					// Throwable error) {
					// showToast(content);
					//
					// }
					//
					// @Override
					// public void onErrorData(
					// String status_description) {
					// showToast(status_description);
					// }
					// });
					// }
					//
					// });

					SimpleDialogFragment
							.createBuilder(EditAlbumActivity.this,
									getSupportFragmentManager()).setTitle("确认")
							.setMessage("删除该张图片吗？").setPositiveButtonText("删除")
							.setNegativeButtonText("取消")
							.setRequestCode(position).setTag(TAG)
							.show();
				}
			}
		});
	}

	/**
	 * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case ADD_ALBUM:
			getData();
			pager.notifyAll();
			break;
		}
	}

	@Override
	public void onPositiveButtonClicked(final int requestCode) {
		Album entity = ((ImagePagerAdapter) pager.getAdapter())
				.getCurrentAlbum(pager.getCurrentItem());
		Album tempEntity = new Album();
		tempEntity.setItem_id("-" + entity.getItem_id());

		new AlbumBll().updateAlbum(EditAlbumActivity.this,
				application.mUser.getToken(), tempEntity,
				new ZzStringHttpResponseListener() {

					@Override
					public void onSuccess(int statusCode, String content) {
						showToast(content);
						getData();
					}

					@Override
					public void onStart() {
						showProgressDialog("正在删除...");

					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast(content);

					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}
				});

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
}