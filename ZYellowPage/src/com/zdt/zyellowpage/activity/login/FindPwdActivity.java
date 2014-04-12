package com.zdt.zyellowpage.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;

public class FindPwdActivity extends AbActivity {
	public static String TAG = "FindPwdActivity";
	private AbTitleBar mAbTitleBar = null;
	private AbHttpUtil mabHttpUtil = null;

	private EditText userName = null;
	private EditText pwdAnswer = null;
	private EditText userPwd = null;
	private EditText userPwd2 = null;
	private ImageButton mClear1;
	private ImageButton mClear2;
	private ImageButton mClearPwd;
	private ImageButton mClearPwd2;

	private String mStr_name = null;
	private String mStr_pwdAnswer = null;
	String mStr_pwd = null;
	String mStr_pwd2 = null;

	private Button findPwdBtn = null;
	private Spinner spiPwdQuestion = null;

	RelativeLayout layoutSpiPwdQuestion = null;
	RelativeLayout layoutPwdAnswer = null;
	RelativeLayout layoutPwd = null;
	RelativeLayout layoutPwd2 = null;

	int step = 1;
	String token = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.find_pwd);
		mabHttpUtil = AbHttpUtil.getInstance(this);

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("找回密码");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		userName = (EditText) this.findViewById(R.id.userName);
		pwdAnswer = (EditText) this.findViewById(R.id.pwdAnswer);
		userPwd = (EditText) this.findViewById(R.id.userPwd);
		userPwd2 = (EditText) this.findViewById(R.id.userPwd2);

		mClear1 = (ImageButton) findViewById(R.id.clearName);
		mClear2 = (ImageButton) findViewById(R.id.clearPwdAnswer);
		mClearPwd = (ImageButton) findViewById(R.id.clearPwd);
		mClearPwd2 = (ImageButton) findViewById(R.id.clearPwd2);

		spiPwdQuestion = (Spinner) findViewById(R.id.pwdQuestion);
		spiPwdQuestion.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.pwdQuestions, android.R.layout.simple_spinner_item));
		spiPwdQuestion.setEnabled(false);

		layoutSpiPwdQuestion = (RelativeLayout) findViewById(R.id.layout02);
		layoutPwdAnswer = (RelativeLayout) findViewById(R.id.layout03);
		layoutSpiPwdQuestion.setVisibility(View.GONE);
		layoutPwdAnswer.setVisibility(View.GONE);

		layoutPwd = (RelativeLayout) findViewById(R.id.layout04);
		layoutPwd2 = (RelativeLayout) findViewById(R.id.layout05);
		layoutPwd.setVisibility(View.GONE);
		layoutPwd2.setVisibility(View.GONE);

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

		pwdAnswer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = pwdAnswer.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear2.setVisibility(View.VISIBLE);
					if (AbStrUtil.isContainChinese(str)) {
						str = str.substring(0, length - 1);
						pwdAnswer.setText(str);
						String str1 = pwdAnswer.getText().toString().trim();
						pwdAnswer.setSelection(str1.length());
						showToast(R.string.error_email_expr2);
					}
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
				pwdAnswer.setText("");
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

		findPwdBtn = (Button) findViewById(R.id.findPwdBtn);
		findPwdBtn.setText("下一步");
		findPwdBtn.setOnClickListener(new FindPwdOnClickListener());

	}

	public class FindPwdOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (step) {
			case 1: {
				mStr_name = userName.getText().toString().trim();
				if (TextUtils.isEmpty(mStr_name)) {
					showToast(R.string.error_name);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (!AbStrUtil.isMobileNo(mStr_name)
						&& !AbStrUtil.isEmail(mStr_name)) {
					showToast(R.string.error_name_expr);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_name) < 5) {
					showToast(R.string.error_name_length1);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}
				// 提交数据
				JSONObject jo = new JSONObject();
				JSONObject joData = new JSONObject();
				try {
					joData.put("username", mStr_name);
					jo.put("method", "forgot-password");
					jo.put("data", joData.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				AbRequestParams params = new AbRequestParams();
				params.put("id", jo.toString());
				mabHttpUtil.post(Constant.BASEURL, params,
						new AbStringHttpResponseListener() {
							// 获取数据成功会调用这里
							@Override
							public void onSuccess(int statusCode, String content) {
								if (content != null && !content.equals("")) {
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
											token = data.getString("token");
											String questionId = data
													.getString("password_question");
											if (!questionId.equals("none")) {
												spiPwdQuestion.setSelection(
														Integer.valueOf(questionId),
														true);
												layoutSpiPwdQuestion
														.setVisibility(View.VISIBLE);
												layoutPwdAnswer
														.setVisibility(View.VISIBLE);
											}
											userName.setEnabled(false);
											step = 2;
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
							};

							// 开始执行前
							@Override
							public void onStart() {
								// 显示进度框
								showProgressDialog();
							}

							// 失败，调用
							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								removeProgressDialog();
								showToast(error.getMessage());
							}

							// 完成后调用，失败，成功
							@Override
							public void onFinish() {
								// 移除进度框
								removeProgressDialog();
							};

						});
				break;
			}
			case 2: {
				mStr_pwdAnswer = pwdAnswer.getText().toString().trim();
				if (TextUtils.isEmpty(mStr_pwdAnswer)) {
					showToast("请回答密保问题");
					pwdAnswer.setFocusable(true);
					pwdAnswer.requestFocus();
					return;
				}

				// 提交数据
				JSONObject jo = new JSONObject();
				JSONObject joData = new JSONObject();
				try {
					joData.put("password_answer", mStr_pwdAnswer);
					jo.put("method", "verification-password");
					jo.put("token", token);
					jo.put("data", joData.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				AbRequestParams params = new AbRequestParams();
				params.put("id", jo.toString());

				mabHttpUtil.post(Constant.BASEURL, params,
						new AbStringHttpResponseListener() {
							// 获取数据成功会调用这里
							@Override
							public void onSuccess(int statusCode, String content) {
								if (content != null && !content.equals("")) {
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
											token = data.getString("token");

											layoutPwdAnswer
													.setVisibility(View.GONE);
											layoutSpiPwdQuestion
													.setVisibility(View.GONE);
											layoutPwd
													.setVisibility(View.VISIBLE);
											layoutPwd2
													.setVisibility(View.VISIBLE);
											step = 3;
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
							};

							// 开始执行前
							@Override
							public void onStart() {
								// 显示进度框
								showProgressDialog();
							}

							// 失败，调用
							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								removeProgressDialog();
								showToast(error.getMessage());
							}

							// 完成后调用，失败，成功
							@Override
							public void onFinish() {
								// 移除进度框
								removeProgressDialog();
							};

						});
				break;
			}
			case 3: {
				mStr_pwd = userPwd.getText().toString().trim();
				mStr_pwd2 = userPwd2.getText().toString().trim();

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

				// 提交数据
				JSONObject jo = new JSONObject();
				JSONObject joData = new JSONObject();
				try {
					joData.put("new_password", mStr_pwd);
					jo.put("method", "reset-password");
					jo.put("token", token);
					jo.put("data", joData.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				AbRequestParams params = new AbRequestParams();
				params.put("id", jo.toString());

				mabHttpUtil.post(Constant.BASEURL, params,
						new AbStringHttpResponseListener() {
							// 获取数据成功会调用这里
							@Override
							public void onSuccess(int statusCode, String content) {
								if (content != null && !content.equals("")) {
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
											showToast("找回密码成功，请重新登录！");
											startActivity(new Intent(
													FindPwdActivity.this,
													LoginActivity.class));
											FindPwdActivity.this.finish();
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
							};

							// 开始执行前
							@Override
							public void onStart() {
								// 显示进度框
								showProgressDialog();
							}

							// 失败，调用
							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								removeProgressDialog();
								showToast(error.getMessage());
							}

							// 完成后调用，失败，成功
							@Override
							public void onFinish() {
								// 移除进度框
								removeProgressDialog();
							};

						});
				break;
			}
			}
		}
	}

}
