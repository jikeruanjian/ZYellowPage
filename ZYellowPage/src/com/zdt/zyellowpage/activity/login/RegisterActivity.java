package com.zdt.zyellowpage.activity.login;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.google.gson.Gson;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;
import com.zdt.zyellowpage.model.User;

public class RegisterActivity extends AbActivity {
	public static String TAG = "RegisterActivity";

	private MyApplication application;
	private AbHttpUtil mAbHttpUtil;
	private EditText userName = null;
	private EditText userPwd = null;
	private EditText userPwd2 = null;
	private EditText pwdAnswer = null;
	private EditText fullName = null;

	private ImageButton mClearName;
	private ImageButton mClearPwd;
	private ImageButton mClearPwd2;
	private ImageButton mClearPwdAnswer;
	private ImageButton mClearFullName;

	private AbTitleBar mAbTitleBar = null;
	private Spinner spiPwdQuestion;
	private Spinner spiSex;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.register);
		application = (MyApplication) abApplication;

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.register_name);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		userName = (EditText) this.findViewById(R.id.userName);
		userPwd = (EditText) this.findViewById(R.id.userPwd);
		userPwd2 = (EditText) this.findViewById(R.id.userPwd2);
		fullName = (EditText) findViewById(R.id.fullName);
		pwdAnswer = (EditText) findViewById(R.id.pwdAnswer);

		mClearName = (ImageButton) findViewById(R.id.clearName);
		mClearPwd = (ImageButton) findViewById(R.id.clearPwd);
		mClearPwd2 = (ImageButton) findViewById(R.id.clearPwd2);
		mClearPwdAnswer = (ImageButton) findViewById(R.id.clearPwdAnswer);
		mClearFullName = (ImageButton) findViewById(R.id.clearFullName);

		spiPwdQuestion = (Spinner) findViewById(R.id.pwdQuestion);
		spiPwdQuestion.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.pwdQuestions, android.R.layout.simple_spinner_item));

		spiSex = (Spinner) findViewById(R.id.sex);

		spiSex.setAdapter(ArrayAdapter.createFromResource(this, R.array.sex,
				android.R.layout.simple_spinner_item));

		Button registerBtn = (Button) this.findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(new RegisterOnClickListener());

		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

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

		pwdAnswer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = pwdAnswer.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearPwdAnswer.setVisibility(View.VISIBLE);
					mClearPwdAnswer.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearPwdAnswer.setVisibility(View.INVISIBLE);
						}
					}, 5000);
				} else {
					mClearPwdAnswer.setVisibility(View.INVISIBLE);
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

		fullName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = fullName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearFullName.setVisibility(View.VISIBLE);
					mClearFullName.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearFullName.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClearFullName.setVisibility(View.INVISIBLE);
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

		mClearPwdAnswer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pwdAnswer.setText("");
			}
		});

		mClearFullName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fullName.setText("");
			}
		});

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

			final long mPwdQuestion = spiPwdQuestion.getSelectedItemId();
			if (mPwdQuestion == 0) {
				showToast(R.string.error_pwdquestion_isempty);
				spiPwdQuestion.setFocusable(true);
				spiPwdQuestion.requestFocus();
				return;
			}

			final String pwdAnswerStr = pwdAnswer.getText().toString().trim();
			if (TextUtils.isEmpty(pwdAnswerStr)) {
				showToast(R.string.error_pwdanswer_isempty);
				pwdAnswer.setFocusable(true);
				pwdAnswer.requestFocus();
				return;
			}

			final String mStr_fullName = fullName.getText().toString().trim();
			if (TextUtils.isEmpty(mStr_fullName)) {
				showToast(R.string.error_fullName_isempty);
				fullName.setFocusable(true);
				fullName.requestFocus();
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
				if (spiPwdQuestion.getSelectedItemId() != 0) {
					joData.put("password_question",
							String.valueOf(spiPwdQuestion.getSelectedItemId()));
					joData.put("password_answer", pwdAnswerStr);
				}
				joData.put("fullname", mStr_fullName);
				joData.put("area_id", application.cityid);
				joData.put("sex", String.valueOf(spiSex.getSelectedItemId()));
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
										user.setFullname(mStr_fullName);
										user.setPassword_question(String
												.valueOf(mPwdQuestion));
										user.setUsername(mStr_name);
										user.setArea_id(application.cityid);
										user.setLoginUser(true);

										UserInsideDao userDao = new UserInsideDao(
												RegisterActivity.this);
										userDao.startReadableDatabase(false);
										List<User> lisUser = userDao.queryList(
												"member_id=?",
												new String[] { user
														.getMember_id() });
										userDao.closeDatabase(false);

										userDao.startWritableDatabase(false);
										if (lisUser != null
												&& lisUser.size() > 0) {
											user.set_id(lisUser.get(0).get_id());
											userDao.update(user);
										} else {
											userDao.insert(user);
										}
										userDao.closeDatabase(false);

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
									} else {
										showToast(bre.getStatus_description());
										return;
									}

								} catch (JSONException e) {
									e.printStackTrace();
									showToast("异常：" + e.getMessage());
									return;
								}

								showToast("注册成功！");
								RegisterActivity.this.finish();
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

}
