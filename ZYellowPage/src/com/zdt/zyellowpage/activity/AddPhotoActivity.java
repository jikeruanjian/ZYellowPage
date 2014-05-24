package com.zdt.zyellowpage.activity;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageCache;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbViewUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.google.gson.Gson;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.model.Picture;
import com.zdt.zyellowpage.util.ImageShowAdapter;

public class AddPhotoActivity extends AbActivity {
	private static final String TAG = "AddPhotoActivity";
	private static final boolean D = Constant.DEBUG;

	private MyApplication application;
	AbTitleBar mAbTitleBar;

	private GridView mGridView = null;
	private ImageShowAdapter mImagePathAdapter = null;
	private ArrayList<String> mPhotoList = new ArrayList<String>();
	private int selectIndex = 0;
	private int camIndex = 0;
	private View mAvatarView = null;
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
	private AlertDialog mAlertDialog = null;
	private AbHttpUtil mAbHttpUtil = null;

	private String selectedPath;

	@AbIocView(id = R.id.currentImage)
	ImageView currentImage; // 经度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.add_photo);
		application = (MyApplication) abApplication;

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(getIntent().getStringExtra("title"));
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		mAbTitleBar.setLogoLine(R.drawable.line);
		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);

		initTitleRightLayout();
		mPhotoList.add(String.valueOf(R.drawable.cam_photo));

		mGridView = (GridView) findViewById(R.id.myGrid);
		mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, 100, 100);
		mGridView.setAdapter(mImagePathAdapter);
		mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);

		if (!AbStrUtil.isEmpty(application.mUser.getLogo()))
			new AbImageDownloader(this).display(currentImage,
					application.mUser.getLogo());

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

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectIndex = position;
				if (selectIndex == camIndex) {
					showDialog(1, mAvatarView);
				} else {
					for (int i = 0; i < mImagePathAdapter.getCount(); i++) {
						ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) mGridView
								.getChildAt(i).getTag();
						if (mViewHolder != null) {
							mViewHolder.mImageView2.setBackgroundDrawable(null);
						}
					}

					ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) view
							.getTag();
					mViewHolder.mImageView2
							.setBackgroundResource(R.drawable.photo_select);
					selectedPath = mImagePathAdapter.getItem(position)
							.toString();
					currentImage.setImageDrawable(Drawable
							.createFromPath(selectedPath));
				}
			}

		});

	}

	private void initTitleRightLayout() {
		mAbTitleBar.clearRightView();
		TextView tvSave = new TextView(this);
		tvSave.setText(" 保存  ");
		tvSave.setTextColor(Color.WHITE);
		tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mAbTitleBar.addRightView(tvSave);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		// 文件上传
		tvSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 已经在后台上传
				if (mAlertDialog != null) {
					mAlertDialog.show();
					return;
				}
				if (AbStrUtil.isEmpty(selectedPath)) {
					showToast("请先选中图片");
					return;
				}
				AbRequestParams params = new AbRequestParams();

				try {
					// File pathRoot =
					// Environment.getExternalStorageDirectory();
					// String path = pathRoot.getAbsolutePath();
					File file = new File(selectedPath);
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

									if (bre.getSuccess()
											&& bre.getStatus() == 200) {
										JSONObject data = jo
												.getJSONObject("data");

										Picture tempPicture = new Gson()
												.fromJson(data.toString(),
														Picture.class);
										AbFileUtil.removeAllFileCache();
										AbImageCache.removeAllBitmapFromCache(); // 先要清空缓存
										application.mUser.setLogo(tempPicture
												.getUrl());
										// User user = new User();
										//
										// user.setLogo(tempPicture.getUrl());
										// new UserBll().updateUser(
										// AddPhotoActivity.this,
										// user,
										// application.mUser.getToken(),
										// new ZzStringHttpResponseListener() {
										//
										// @Override
										// public void onSuccess(
										// int statusCode,
										// String content) {
										showToast("设置成功");
										AddPhotoActivity.this.finish();
										// }
										//
										// @Override
										// public void onStart() {
										// showProgressDialog("正在保存...");
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
										//
										// }
										// });
									}
								} catch (JSONException e) {
									e.printStackTrace();
									return;
								}
							}

							// 开始执行前
							@Override
							public void onStart() {
								showProgressDialog("正在上传...");
							}

							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								showToast(error.getMessage());
							}

							// 进度
							@Override
							public void onProgress(int bytesWritten,
									int totalSize) {
								showProgressDialog("正在上传..."
										+ (bytesWritten / totalSize) * 100
										+ "%");
							}

							// 完成后调用，失败，成功，都要调用
							@Override
							public void onFinish() {
								Log.d(TAG, "onFinish");
								// 下载完成取消进度框
								removeProgressDialog();
							};

						});
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
			mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, path);
			camIndex++;
			AbViewUtil.setAbsListViewHeight(mGridView, 3, 25);
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
