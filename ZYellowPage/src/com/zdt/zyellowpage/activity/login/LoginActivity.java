package com.zdt.zyellowpage.activity.login;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ab.activity.AbActivity;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.google.gson.Gson;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.jsonEntity.LoginRequest;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class LoginActivity extends AbActivity {

	public static String TAG = "LoginActivity";

	private EditText userName = null;
	private EditText userPwd = null;
	private MyApplication application;
	private String mStr_name = null;
	private String mStr_pwd = null;
	private ImageButton mClear1;
	private ImageButton mClear2;
	private AbTitleBar mAbTitleBar = null;
	private AbHttpUtil mAbHttpUtil = null;
	private Button loginBtn = null;

	// 数据库操作类
	public UserInsideDao mUserDao = null;

	private boolean isSuccess = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.login);
		application = (MyApplication) abApplication;

		// 初始化数据库操作实现类
		mUserDao = new UserInsideDao(LoginActivity.this);

		mAbHttpUtil = AbHttpUtil.getInstance(this);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.login);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		userName = (EditText) this.findViewById(R.id.userName);
		userPwd = (EditText) this.findViewById(R.id.userPwd);
		CheckBox checkBox = (CheckBox) findViewById(R.id.login_check);
		mClear1 = (ImageButton) findViewById(R.id.clearName);
		mClear2 = (ImageButton) findViewById(R.id.clearPwd);

		loginBtn = (Button) this.findViewById(R.id.loginBtn);
		Button register = (Button) this.findViewById(R.id.registerBtn);
		loginBtn.setOnClickListener(new LoginOnClickListener());

		Button pwdBtn = (Button) findViewById(R.id.pwdBtn);

		pwdBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						FindPwdActivity.class);
				intent.putExtra(AbConstant.TITLE_TRANSPARENT_FLAG,
						AbConstant.TITLE_TRANSPARENT);
				startActivity(intent);
			}
		});

		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				intent.putExtra(AbConstant.TITLE_TRANSPARENT_FLAG,
						AbConstant.TITLE_TRANSPARENT);
				startActivity(intent);
				finish();
			}
		});

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = abSharedPreferences.edit();
				editor.putBoolean(Constant.USERPASSWORDREMEMBERCOOKIE,
						isChecked);
				editor.commit();
				application.userPasswordRemember = isChecked;
			}
		});

		String name = abSharedPreferences
				.getString(Constant.USERNAMECOOKIE, "");
		String password = abSharedPreferences.getString(
				Constant.USERPASSWORDCOOKIE, "");
		boolean userPwdRemember = abSharedPreferences.getBoolean(
				Constant.USERPASSWORDREMEMBERCOOKIE, false);

		if (userPwdRemember) {
			userName.setText(name);
			userPwd.setText(password);
			checkBox.setChecked(true);
		} else {
			userName.setText("");
			userPwd.setText("");
			checkBox.setChecked(false);
		}

		initTitleRightLayout();

		userName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = userName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear1.setVisibility(View.VISIBLE);
					mClear1.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClear1.setVisibility(View.INVISIBLE);
						}

					}, 5000);

				} else {
					mClear1.setVisibility(View.INVISIBLE);
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

		userPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = userPwd.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear2.setVisibility(View.VISIBLE);
					mClear2.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClear2.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClear2.setVisibility(View.INVISIBLE);
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

		mClear1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName.setText("");
			}
		});

		mClear2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userPwd.setText("");
			}
		});

	}

	private void initTitleRightLayout() {

	}

	/**
	 * 登录按钮
	 * 
	 * @author Kevin
	 * 
	 */
	public class LoginOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == loginBtn) {
				mStr_name = userName.getText().toString().trim();
				mStr_pwd = userPwd.getText().toString().trim();

				if (TextUtils.isEmpty(mStr_name)) {
					showToast(R.string.error_name);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				// if (!AbStrUtil.isEmail(mStr_name)
				// && !AbStrUtil.isMobileNo(mStr_name)) {
				// showToast(R.string.error_name_expr);
				// userName.setFocusable(true);
				// userName.requestFocus();
				// return;
				// }

				if (AbStrUtil.strLength(mStr_name) < 5) {
					showToast(R.string.error_name_length1);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_name) > 28) {
					showToast(R.string.error_name_length2);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (TextUtils.isEmpty(mStr_pwd)) {
					showToast(R.string.error_pwd);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_pwd) < 6) {
					showToast(R.string.error_pwd_length1);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_pwd) > 20) {
					showToast(R.string.error_pwd_length2);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				// 验证登录
				// post请求
				String url = Constant.BASEURL;
				// 绑定参数
				AbRequestParams params = new AbRequestParams();

				LoginRequest lu = new LoginRequest("login", mStr_name, mStr_pwd);
				params.put("id", new Gson().toJson(lu));

				mAbHttpUtil.post(url, params,
						new AbStringHttpResponseListener() {
							// 获取数据成功会调用这里
							@Override
							public void onSuccess(int statusCode, String content) {
								application.mUser = new User();
								if (content != null && !content.equals("")
										&& application.mUser != null) {
									System.out.println("-----------" + content);
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

										if (bre.getSuccess()) {
											JSONObject data = jo
													.getJSONObject("data");
											User user = new Gson().fromJson(
													data.toString(), User.class);
											user.setLoginUser(true);
											user.setPassword(mStr_pwd);

											UserInsideDao userDao = new UserInsideDao(
													LoginActivity.this);
											userDao.startWritableDatabase(false);
											userDao.delete("member_id=?",
													new String[] { user
															.getMember_id() });

											userDao.insert(user);
											userDao.closeDatabase(false);

											application.mUser = user;
											isSuccess = true;

											if (application.userPasswordRemember) {
												Editor editor = abSharedPreferences
														.edit();
												editor.putString(
														Constant.USERNAMECOOKIE,
														mStr_name);
												editor.putString(
														Constant.USERPASSWORDCOOKIE,
														mStr_pwd);
												application.firstStart = false;
												editor.putBoolean(
														Constant.FIRSTSTART,
														false);
												editor.commit();
											} else {
												Editor editor = abSharedPreferences
														.edit();
												application.firstStart = false;
												editor.putBoolean(
														Constant.FIRSTSTART,
														false);
												editor.commit();
											}
											updateUserInfo();
										} else {
											showToast(bre
													.getStatus_description());
										}

									} catch (JSONException e) {
										e.printStackTrace();
										showToast("异常：" + e.getMessage());
										return;
									}

									// showToast("登录成功！");
									// 将信息保存起来
								} else {
									showToast("数据请求失败！");
								}
							};

							// 开始执行前
							@Override
							public void onStart() {
								Log.d(TAG, "onStart");
								// 显示进度框
								showProgressDialog();
							}

							// 失败，调用
							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								showToast(error.getMessage());
							}

							// 完成后调用，失败，成功
							@Override
							public void onFinish() {
								// 移除进度框
								removeProgressDialog();
							};

						});
			}
		}

		/**
		 * 同步用户资料
		 */
		public void updateUserInfo() {
			if (!isSuccess) {
				return;
			}
			new UserBll().getDetailOfUser(LoginActivity.this,
					application.mUser.getMember_id(),
					application.mUser.getType(),
					new ZzObjectHttpResponseListener<User>() {

						@Override
						public void onSuccess(int statusCode, List<User> lis) {
							if (lis == null || lis.size() == 0) {
								return;
							}
							User tempUser = lis.get(0);
							tempUser.setLoginUser(true);
							tempUser.setPassword(mStr_pwd);

							UserInsideDao userDao = new UserInsideDao(
									LoginActivity.this);
							userDao.startReadableDatabase(false);
							List<User> lisUser = userDao.queryList(
									"member_id=?",
									new String[] { tempUser.getMember_id() });
							userDao.closeDatabase(false);

							userDao.startWritableDatabase(false);
							if (lisUser != null && lisUser.size() > 0) {
								User localUser = lisUser.get(0);
								tempUser.set_id(localUser.get_id());
								tempUser.setUsername(localUser.getUsername());
								tempUser.setToken(localUser.getToken());
								tempUser.setType(localUser.getType());
								tempUser.setFullname(localUser.getFullname());
								tempUser.setAddress(localUser.getAddress());
								tempUser.setMember_id(localUser.getMember_id());
								userDao.update(tempUser);
								application.mUser = tempUser;
							}
							userDao.closeDatabase(false);
						}

						@Override
						public void onStart() {
							showProgressDialog("同步用户信息...");
						}

						@Override
						public void onFinish() {
							removeProgressDialog();
							LoginActivity.this.finish();
						}

						@Override
						public void onErrorData(String status_description) {
							showToast(status_description);
						}

						@Override
						public void onFailure(int statusCode, String content,
								Throwable error, List<User> localList) {
							showToast(error.getMessage());
						}
					});
		}
	}
}
