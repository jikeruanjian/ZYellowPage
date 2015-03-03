package com.zdt.zyellowpage.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.User;

/**
 * 更改坐标
 * 
 * @author Kevin
 * 
 */
public class EditCoordinateAcitivity extends AbActivity {
	private MyApplication application;
	AbTitleBar mAbTitleBar;

	@AbIocView(id = R.id.latitude)
	EditText etLatitude; // 经度
	@AbIocView(id = R.id.longitude)
	EditText etLongitude;// 纬度

	@AbIocView(id = R.id.clearLatitude)
	ImageButton clearLatitude;
	@AbIocView(id = R.id.clearLongitude)
	ImageButton clearLongitude;

	User newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edite_coordinate);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改资料");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initTitleRightLayout();
		// 绑定默认数据
		bindData();

		// 绑定textView的清楚事件
		bindClear();
	}

	private void initTitleRightLayout() {
		mAbTitleBar.clearRightView();
		TextView tvSave = new TextView(this);
		tvSave.setText(" 保存  ");
		tvSave.setTextColor(Color.WHITE);
		tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mAbTitleBar.addRightView(tvSave);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		tvSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 开始保存
				newUser = new User();
				newUser.setLatitude(Double.valueOf(etLatitude.getText()
						.toString()));
				newUser.setLongitude(Double.valueOf(etLongitude.getText()
						.toString()));

				new UserBll().updateUser(EditCoordinateAcitivity.this, newUser,
						application.mUser.getToken(),
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								// 修改成功，把信息同步到 application.muser
								application.mUser.setLatitude(newUser
										.getLatitude());
								application.mUser.setLongitude(newUser
										.getLongitude());
								UserInsideDao userDao = new UserInsideDao(
										EditCoordinateAcitivity.this);
								userDao.startWritableDatabase(false);
								userDao.update(application.mUser);
								userDao.closeDatabase(false);

								showToast(content);
								EditCoordinateAcitivity.this.finish();
							}

							@Override
							public void onStart() {
								showProgressDialog("正在提交数据...");
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
							public void onErrorData(String status_description) {
								showToast(status_description);
							}
						});
			}
		});

	}

	/**
	 * 绑定默认的用户信息
	 */
	private void bindData() {
		final User user = application.mUser;

		if (user != null) {
			this.etLatitude.setText(user.getLatitude().toString());
			this.etLongitude.setText(user.getLongitude().toString());
		}
	}

	/**
	 * 绑定textView清空按钮
	 */
	private void bindClear() {
		etLatitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = etLatitude.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearLatitude.setVisibility(View.VISIBLE);
					clearLatitude.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearLatitude.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearLatitude.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		clearLatitude.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etLatitude.setText("");
			}
		});

		etLongitude.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = etLongitude.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearLongitude.setVisibility(View.VISIBLE);
					clearLongitude.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearLongitude.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearLongitude.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		clearLongitude.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etLongitude.setText("");
			}
		});
	}
}
