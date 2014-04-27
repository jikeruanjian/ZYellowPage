package com.zdt.zyellowpage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Area;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.model.User;

public class EditPersonBaseResourceActivity extends AbActivity {
	private MyApplication application;
	AbTitleBar mAbTitleBar;

	@AbIocView(id = R.id.keyWord)
	EditText tvKeyword;
	@AbIocView(id = R.id.fullName)
	EditText tvFullName;
	@AbIocView(id = R.id.nation)
	EditText tvNation;
	@AbIocView(id = R.id.telphone)
	EditText tvTelphone;
	@AbIocView(id = R.id.professional)
	EditText tvProfessional;
	@AbIocView(id = R.id.school)
	EditText tvSchool;
	@AbIocView(id = R.id.email)
	EditText tvEmail;
	@AbIocView(id = R.id.address)
	EditText tvAddress;
	@AbIocView(id = R.id.website)
	EditText tvWebsite;
	@AbIocView(id = R.id.qq)
	EditText tvQQ;
	@AbIocView(id = R.id.sex)
	Spinner spiSex;
	@AbIocView(id = R.id.area)
	Button btnArea;
	@AbIocView(id = R.id.category)
	Button btnCategory;

	@AbIocView(id = R.id.clearKeyWord)
	ImageButton clearKeyWord;
	@AbIocView(id = R.id.clearFullName)
	ImageButton clearFullName;
	@AbIocView(id = R.id.clearNation)
	ImageButton clearNation;
	@AbIocView(id = R.id.cleartelphone)
	ImageButton cleartelphone;
	@AbIocView(id = R.id.clearProfessional)
	ImageButton clearProfessional;
	@AbIocView(id = R.id.clearSchool)
	ImageButton clearSchool;
	@AbIocView(id = R.id.clearEmail)
	ImageButton clearEmail;
	@AbIocView(id = R.id.clearAddress)
	ImageButton clearAddress;
	@AbIocView(id = R.id.clearWebsite)
	ImageButton clearWebsite;
	@AbIocView(id = R.id.clearQQ)
	ImageButton clearQQ;

	private Category selectedCategory;
	private Area selectedArea;
	private User newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edite_person_base_resource);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改资料");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		initTitleRightLayout();

		spiSex.setAdapter(ArrayAdapter.createFromResource(this, R.array.sex,
				android.R.layout.simple_spinner_item));

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

		tvSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 开始保存
				newUser = new User();
				newUser.setKeyword(tvKeyword.getText().toString());
				newUser.setFullname(tvFullName.getText().toString());
				newUser.setSex(Integer.valueOf(String.valueOf(spiSex
						.getSelectedItemId())));
				newUser.setNation(tvNation.getText().toString());
				newUser.setTelephone(tvTelphone.getText().toString());
				if (selectedArea != null) {
					newUser.setArea_id(selectedArea.getId());
					newUser.setArea_name(selectedArea.getName());
				}
				if (selectedCategory != null) {
					newUser.setCategory_id(selectedCategory.getId());
					newUser.setCategory_name(selectedCategory.getName());
				}
				newUser.setProfessional(tvProfessional.getText().toString());
				newUser.setSchool(tvSchool.getText().toString());
				newUser.setEmail(tvEmail.getText().toString());
				newUser.setAddress(tvAddress.getText().toString());
				newUser.setWebsite(tvWebsite.getText().toString());
				newUser.setQq(tvQQ.getText().toString());

				new UserBll().updateUser(EditPersonBaseResourceActivity.this,
						newUser, application.mUser.getToken(),
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								// 修改成功，把信息同步到 application.muser
								application.mUser.setKeyword(newUser
										.getKeyword());
								application.mUser.setFullname(newUser
										.getFullname());
								application.mUser.setSex(newUser.getSex());
								application.mUser.setNation(newUser.getNation());
								application.mUser.setTelephone(newUser
										.getTelephone());
								if (selectedArea != null) {
									application.mUser.setArea_id(selectedArea
											.getId());
									application.mUser.setArea_name(selectedArea
											.getName());
								}
								if (selectedCategory != null) {
									application.mUser
											.setCategory_id(selectedCategory
													.getId());
									application.mUser
											.setCategory_name(selectedCategory
													.getName());
								}
								application.mUser.setProfessional(newUser
										.getProfessional());
								application.mUser.setSchool(newUser.getSchool());
								application.mUser.setEmail(newUser.getEmail());
								application.mUser.setAddress(newUser
										.getAddress());
								application.mUser.setWebsite(newUser
										.getWebsite());
								application.mUser.setQq(newUser.getQq());
								UserInsideDao userDao = new UserInsideDao(
										EditPersonBaseResourceActivity.this);
								userDao.startWritableDatabase(false);
								userDao.update(application.mUser);
								userDao.closeDatabase(false);

								showToast(content);
								EditPersonBaseResourceActivity.this.finish();
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
			this.tvAddress.setText(user.getAddress());
			this.tvEmail.setText(user.getEmail());
			this.tvFullName.setText(user.getFullname());
			this.tvKeyword.setText(user.getKeyword());
			this.tvNation.setText(user.getNation());
			this.tvProfessional.setText(user.getProfessional());
			this.tvQQ.setText(user.getQq());
			this.tvSchool.setText(user.getSchool());
			this.tvTelphone.setText(user.getTelephone());
			this.tvWebsite.setText(user.getWebsite());
			// 绑定区域
			btnArea.setText(user.getArea_name());

			// 绑定行业
			btnCategory.setText(user.getCategory_name());

			// 绑定性别
			if (user.getSex() != null) {
				// String[] sex = getResources().getStringArray(R.array.sex);
				// for (int i = 0; i < sex.length; i++) {
				// if (user.getSex().equals(sex[i])) {
				spiSex.setSelection(user.getSex(), true);
				// }
				// }
			}

			btnArea.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							EditPersonBaseResourceActivity.this,
							SelectAreaActivity.class);
					startActivityForResult(intent, 10000);
				}
			});

			btnCategory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							EditPersonBaseResourceActivity.this,
							SelectCategoryActivity.class);
					intent.putExtra("currentCategory", user.getCategory_name());
					startActivityForResult(intent, 10001);
				}
			});
		}
	}

	/**
	 * 绑定textView清空按钮
	 */
	private void bindClear() {
		tvFullName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvFullName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearFullName.setVisibility(View.VISIBLE);
					clearFullName.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearFullName.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearFullName.setVisibility(View.INVISIBLE);
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

		clearFullName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvFullName.setText("");
			}
		});

		tvAddress.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvAddress.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearAddress.setVisibility(View.VISIBLE);
					clearAddress.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearAddress.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearAddress.setVisibility(View.INVISIBLE);
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

		clearAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvAddress.setText("");
			}
		});

		tvEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvEmail.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearEmail.setVisibility(View.VISIBLE);
					clearEmail.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearEmail.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearEmail.setVisibility(View.INVISIBLE);
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

		clearEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvEmail.setText("");
			}
		});

		tvKeyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvKeyword.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearKeyWord.setVisibility(View.VISIBLE);
					clearKeyWord.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearKeyWord.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearKeyWord.setVisibility(View.INVISIBLE);
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

		clearKeyWord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvKeyword.setText("");
			}
		});

		tvNation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvNation.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearNation.setVisibility(View.VISIBLE);
					clearNation.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearNation.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearNation.setVisibility(View.INVISIBLE);
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

		clearNation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvNation.setText("");
			}
		});

		tvProfessional.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvProfessional.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearProfessional.setVisibility(View.VISIBLE);
					clearProfessional.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearProfessional.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearProfessional.setVisibility(View.INVISIBLE);
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

		clearProfessional.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvProfessional.setText("");
			}
		});

		tvQQ.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvQQ.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearQQ.setVisibility(View.VISIBLE);
					clearQQ.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearQQ.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearQQ.setVisibility(View.INVISIBLE);
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

		clearQQ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvQQ.setText("");
			}
		});

		tvSchool.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvSchool.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearSchool.setVisibility(View.VISIBLE);
					clearSchool.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearSchool.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearSchool.setVisibility(View.INVISIBLE);
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

		clearSchool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvSchool.setText("");
			}
		});

		tvTelphone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvTelphone.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					cleartelphone.setVisibility(View.VISIBLE);
					cleartelphone.postDelayed(new Runnable() {

						@Override
						public void run() {
							cleartelphone.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					cleartelphone.setVisibility(View.INVISIBLE);
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

		cleartelphone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvTelphone.setText("");
			}
		});

		tvWebsite.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = tvWebsite.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearWebsite.setVisibility(View.VISIBLE);
					clearWebsite.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearWebsite.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearWebsite.setVisibility(View.INVISIBLE);
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

		clearWebsite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvWebsite.setText("");
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 10000) {
			if (resultCode == RESULT_OK) {
				btnArea.setText(application.cityName);
				selectedArea = new Area();
				selectedArea.setId(application.cityid);
				selectedArea.setName(application.cityName);
				Log.i("EditPersonbase", selectedArea.getName());
			}
		} else if (requestCode == 10001) {
			if (resultCode == RESULT_OK) {
				selectedCategory = (Category) intent
						.getSerializableExtra("selectedCagtegory");
				btnCategory.setText(selectedCategory.getName());
			}
		}
	}
}
