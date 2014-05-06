package com.zdt.zyellowpage.activity;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.AlbumBll;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.AlbumReqEntity;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Album;
import com.zdt.zyellowpage.model.Picture;

/**
 * 编辑相册
 * 
 * @author Kevin
 * 
 */
public class EditAlbumActivity extends AbActivity {
	private static final String TAG = "EditAlbumActivity";
	private static final boolean D = Constant.DEBUG;
	private static final String STATE_POSITION = "STATE_POSITION";
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/* 用来标识请求裁剪图片后的activity */
	private static final int CAMERA_CROP_DATA = 3022;
	/* 拍照的照片存储位置 */
	private File PHOTO_DIR = null;
	// 照相机拍照得到的图片
	private File mCurrentPhotoFile;
	private String mFileName;
	private AbHttpUtil mAbHttpUtil = null;
	private View mAvatarView = null;

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
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initTitleRightLayout();

		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);

		// 初始化图片保存路径
		String photo_dir = AbFileUtil.getFullImageDownPathDir();
		if (AbStrUtil.isEmpty(photo_dir)) {
			showToast("存储卡不存在");
		} else {
			PHOTO_DIR = new File(photo_dir);
		}

		Button albumButton = (Button) mAvatarView
				.findViewById(R.id.choose_album);
		Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
		Button cancelButton = (Button) mAvatarView
				.findViewById(R.id.choose_cancel);

		albumButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeDialog(AbConstant.DIALOGBOTTOM);
				// 从相册中去获取
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
				} catch (ActivityNotFoundException e) {
					showToast("没有找到照片");
				}
			}

		});

		camButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeDialog(AbConstant.DIALOGBOTTOM);
				doPickPhotoAction();
			}

		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeDialog(AbConstant.DIALOGBOTTOM);
			}

		});

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				return false;
			}
		});

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
		Button moreBtn = (Button) rightViewMore.findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	public void showPopupWindow(View view) {
		if (popupWindow != null) {
			popupWindow.showAsDropDown(view, view.getRight() + 10, 10);
			return;
		}
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				EditAlbumActivity.this).inflate(R.layout.popdialog, null);
		ListView listView = (ListView) layout.findViewById(R.id.listViewPopW);
		listView.setAdapter(new ArrayAdapter<String>(EditAlbumActivity.this,
				R.layout.text_itemselect, R.id.textViewSelectItemName,
				new String[] { "添加", "删除" }));

		popupWindow = new PopupWindow(EditAlbumActivity.this);
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAsDropDown(view, view.getRight() + 10, 10);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				popupWindow.dismiss();
				if (position == 0) {
					// showDialog(1, mAvatarView);
					EditAlbumActivity.this.startActivity(new Intent(
							EditAlbumActivity.this, AddAlbumActivity.class));
				} else {
					Album entity = ((ImagePagerAdapter) pager.getAdapter())
							.getCurrentAlbum(pager.getCurrentItem());
					Album tempEntity = new Album();
					tempEntity.setItem_id("-" + entity.getItem_id());

					new AlbumBll().updateAlbum(EditAlbumActivity.this,
							application.mUser.getToken(), tempEntity,
							new ZzStringHttpResponseListener() {

								@Override
								public void onSuccess(int statusCode,
										String content) {

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
								public void onFailure(int statusCode,
										String content, Throwable error) {
									showToast(content);

								}

								@Override
								public void onErrorData(
										String status_description) {
									showToast(status_description);
								}
							});
				}
			}
		});
	}

	/**
	 * 描述：从照相机获取
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhoto();
		} else {
			showToast("没有可用的存储卡");
		}
	}

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			showToast("未找到系统相机程序");
		}
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
		case PHOTO_PICKED_WITH_DATA:
			Uri uri = mIntent.getData();
			String currentFilePath = getPath(uri);
			if (!AbStrUtil.isEmpty(currentFilePath)) {
				Intent intent1 = new Intent(this, CropImageActivity.class);
				intent1.putExtra("PATH", currentFilePath);
				startActivityForResult(intent1, CAMERA_CROP_DATA);
			} else {
				showToast("未在存储卡中找到这个文件");
			}
			break;
		case CAMERA_WITH_DATA:
			if (D)
				Log.d(TAG, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			Intent intent2 = new Intent(this, CropImageActivity.class);
			intent2.putExtra("PATH", currentFilePath2);
			startActivityForResult(intent2, CAMERA_CROP_DATA);
			break;
		case CAMERA_CROP_DATA:
			String path = mIntent.getStringExtra("PATH");
			if (D)
				Log.d(TAG, "裁剪后得到的图片的路径是 = " + path);

			// 已经在后台上传
			if (AbStrUtil.isEmpty(path)) {
				showToast("请先选中图片");
				return;
			}
			AbRequestParams params = new AbRequestParams();

			try {
				// File pathRoot =
				// Environment.getExternalStorageDirectory();
				// String path = pathRoot.getAbsolutePath();
				File file = new File(path);
				params.put("fileName",
						URLEncoder.encode(file.getName(), HTTP.UTF_8));
				params.put("type", "1");
				params.put("token", application.mUser.getToken());

				params.put("file", file);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mAbHttpUtil.post(Constant.PICTUREUPLOADURL, params,
					new AbStringHttpResponseListener() {

						@Override
						public void onSuccess(int statusCode, String content) {
							Log.i(TAG, content);
							JSONObject jo = null;
							BaseResponseEntity bre = new BaseResponseEntity();
							// 转换数据
							try {
								jo = new JSONObject(content);
								bre.setResult(jo.getString("result"));
								bre.setSuccess(jo.getBoolean("success"));
								bre.setStatus(jo.getInt("status"));
								bre.setStatus_description(jo
										.getString("status_description"));

								if (bre.getSuccess() && bre.getStatus() == 200) {
									JSONObject data = jo.getJSONObject("data");

									Picture tempPicture = new Gson().fromJson(
											data.toString(), Picture.class);
									// TODO 要添加到相册列表...

								}
							} catch (JSONException e) {
								e.printStackTrace();
								return;
							}
						}

						// 开始执行前
						@Override
						public void onStart() {
							Log.d(TAG, "onStart");
							// 打开进度框
							showProgressDialog("正在上传...");
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
							showToast(error.getMessage());
						}

						// 进度u
						@Override
						public void onProgress(int bytesWritten, int totalSize) {
							showProgressDialog("正在上传..."
									+ (bytesWritten / totalSize) * 100 + "%");
						}

						// 完成后调用，失败，成功，都要调用
						@Override
						public void onFinish() {
							Log.d(TAG, "onFinish");
							// 下载完成取消进度框
							removeProgressDialog();
						};

					});
			break;
		}
	}

	/**
	 * 从相册得到的url转换为SD卡中图片路径
	 */
	public String getPath(Uri uri) {
		if (AbStrUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}
}