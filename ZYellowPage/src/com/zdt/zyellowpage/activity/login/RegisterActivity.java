package com.zdt.zyellowpage.activity.login;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.EditPersonBaseResourceActivity;
import com.zdt.zyellowpage.bll.LoginBll;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class RegisterActivity extends AbActivity {
	public static String TAG = "RegisterActivity";

	private MyApplication application;
	private AbHttpUtil mAbHttpUtil;
	private EditText userName = null;
	private EditText userPwd = null;
	private EditText userPwd2 = null;
	private EditText code = null;
	private Button loadCode = null;

	private ImageButton mClearName;
	private ImageButton mClearPwd;
	private ImageButton mClearPwd2;
	private ImageButton mClearCode;

	private AbTitleBar mAbTitleBar = null;
	private int time = 60;
	Timer timer;

	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.register);
		application = (MyApplication) abApplication;

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.register_name);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		userName = (EditText) this.findViewById(R.id.userName);
		userPwd = (EditText) this.findViewById(R.id.userPwd);
		userPwd2 = (EditText) this.findViewById(R.id.userPwd2);
		code = (EditText) this.findViewById(R.id.code);
		loadCode = (Button) this.findViewById(R.id.loadCode);

		mClearName = (ImageButton) findViewById(R.id.clearName);
		mClearPwd = (ImageButton) findViewById(R.id.clearPwd);
		mClearPwd2 = (ImageButton) findViewById(R.id.clearPwd2);
		mClearCode = (ImageButton) findViewById(R.id.clearCode);

		Button registerBtn = (Button) this.findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(new RegisterOnClickListener());
		loadCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String mStr_name = userName.getText().toString().trim();
				if (TextUtils.isEmpty(mStr_name)) {
					showToast(R.string.error_name);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (!AbStrUtil.isMobileNo(mStr_name)) {
					showToast(R.string.error_name_expr);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				new LoginBll().getCode(RegisterActivity.this, mStr_name, "0",
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								if (content != null && !content.equals("")) {
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

										if (bre.getSuccess()) {
											loadCode.setEnabled(false);
											if (timer == null)
												timer = new Timer();
											TimerTask tt = new TimerTask() {

												@Override
												public void run() {
													handler.post(new Runnable() {

														@Override
														public void run() {

															time--;
															if (time > 0)
																loadCode.setText(time
																		+ "秒后获取");
															else {
																time = 60;
																timer.cancel();
																timer = null;
																loadCode.setText("获取验证码");
																loadCode.setEnabled(true);
															}
														}
													});
												}
											};
											timer.schedule(tt, 0, 1000);
											showToast(bre
													.getStatus_description());
										} else {
											showToast(bre
													.getStatus_description());
											return;
										}

									} catch (JSONException e) {
										e.printStackTrace();
										showToast("异常：" + e.getMessage());
										return;
									}
								} else {
									showToast("数据请求失败！");
								}
							}

							@Override
							public void onStart() {
								showProgressDialog("正在向你手机发送验证码...");
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

		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		bindClear();
		initTitleRightLayout();
	}

	private void initTitleRightLayout() {

	}

	public class RegisterOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			final String mStr_name = userName.getText().toString().trim();
			final String mStr_pwd = userPwd.getText().toString().trim();
			final String mStr_pwd2 = userPwd2.getText().toString().trim();
			if (TextUtils.isEmpty(mStr_name)) {
				showToast(R.string.error_name);
				userName.setFocusable(true);
				userName.requestFocus();
				return;
			}

			if (!AbStrUtil.isMobileNo(mStr_name)) {
				showToast(R.string.error_name_expr);
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

			if (TextUtils.isEmpty(mStr_pwd2)) {
				showToast(R.string.error_pwd);
				userPwd2.setFocusable(true);
				userPwd2.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_pwd2) < 6) {
				showToast(R.string.error_pwd_length1);
				userPwd2.setFocusable(true);
				userPwd2.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_pwd2) > 20) {
				showToast(R.string.error_pwd_length2);
				userPwd2.setFocusable(true);
				userPwd2.requestFocus();
				return;
			}

			if (!mStr_pwd2.equals(mStr_pwd)) {
				showToast(R.string.error_pwd_match);
				userPwd2.setFocusable(true);
				userPwd2.requestFocus();
				return;
			}

			if (AbStrUtil.isEmpty(code.getText().toString().trim())) {
				showToast("请输入验证码");
				code.setFocusable(true);
				code.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(code.getText().toString().trim()) < 6) {
				showToast("请输入正确的验证码");
				code.setFocusable(true);
				code.requestFocus();
				return;
			}

			// 提交数据
			mAbHttpUtil = AbHttpUtil.getInstance(RegisterActivity.this);

			JSONObject jo = new JSONObject();
			JSONObject joData = new JSONObject();
			try {
				jo.put("method", "register");
				joData.put("username", mStr_name);
				joData.put("password", mStr_pwd);
				joData.put("code", code.getText().toString().trim());
				jo.put("data", joData.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			AbRequestParams params = new AbRequestParams();

			params.put("id", jo.toString());
			Log.i(RegisterActivity.TAG, jo.toString());
			mAbHttpUtil.post(Constant.BASEURL, params,
					new AbStringHttpResponseListener() {
						// 获取数据成功会调用这里
						@Override
						public void onSuccess(int statusCode, String content) {
							if (content != null && !content.equals("")) {
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

									if (bre.getSuccess()) {
										JSONObject data = jo
												.getJSONObject("data");
										User user = new User();
										user.setToken(data.getString("token"));
										user.setUsername(data
												.getString("username"));
										user.setArea_id(application.cityid);

										application.mUser = user;

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
													Constant.FIRSTSTART, false);
											editor.commit();
										} else {
											Editor editor = abSharedPreferences
													.edit();
											application.firstStart = false;
											editor.putBoolean(
													Constant.FIRSTSTART, false);
											editor.commit();
										}
										showToast("注册成功，请填写个人资料");

										// 跳转到个人资料修改界面
										Intent intent = new Intent(
												RegisterActivity.this,
												EditPersonBaseResourceActivity.class);
										startActivity(intent);
										RegisterActivity.this.finish();
									} else {
										showToast(bre.getStatus_description());
										return;
									}

								} catch (JSONException e) {
									e.printStackTrace();
									showToast("异常：" + e.getMessage());
									return;
								}
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
						public void onFailure(int statusCode, String content,
								Throwable error) {
							// removeProgressDialog();
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

	private void bindClear() {

		userName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = userName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearName.setVisibility(View.VISIBLE);
					mClearName.postDelayed(new Runnable() {
						@Override
						public void run() {
							mClearName.setVisibility(View.INVISIBLE);
						}

					}, 5000);

				} else {
					mClearName.setVisibility(View.INVISIBLE);
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
					mClearPwd.setVisibility(View.VISIBLE);
					if (!AbStrUtil.isNumberLetter(str)) {
						str = str.substring(0, length - 1);
						userPwd.setText(str);
						String str1 = userPwd.getText().toString().trim();
						userPwd.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}

					mClearPwd.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearPwd.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClearPwd.setVisibility(View.INVISIBLE);
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

		userPwd2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = userPwd2.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearPwd2.setVisibility(View.VISIBLE);
					if (!AbStrUtil.isNumberLetter(str)) {
						str = str.substring(0, length - 1);
						userPwd2.setText(str);
						String str1 = userPwd2.getText().toString().trim();
						userPwd2.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}
					mClearPwd2.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearPwd2.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClearPwd2.setVisibility(View.INVISIBLE);
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

		mClearName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName.setText("");
			}
		});

		mClearPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userPwd.setText("");
			}
		});

		mClearPwd2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userPwd2.setText("");
			}
		});

		code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = code.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearCode.setVisibility(View.VISIBLE);
					mClearCode.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearCode.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClearCode.setVisibility(View.INVISIBLE);
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

		mClearCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				code.setText("");
			}
		});
	}

}
