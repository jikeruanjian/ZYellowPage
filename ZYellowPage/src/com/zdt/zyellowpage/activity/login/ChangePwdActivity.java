package com.zdt.zyellowpage.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.jsonEntity.BaseResponseEntity;

public class ChangePwdActivity extends AbActivity {
	public static String TAG = "ChangePwdActivity";
	private AbTitleBar mAbTitleBar = null;
	private AbHttpUtil mabHttpUtil = null;

	private EditText oldPwd = null;
	private EditText userPwd = null;
	private EditText userPwd2 = null;
	private ImageButton mClearOldPwd;
	private ImageButton mClearPwd;
	private ImageButton mClearPwd2;
	String mStr_pwd = null;
	String mStr_pwd2 = null;
	String mStr_oldPwd = null;
	private Button findPwdBtn = null;
	private MyApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.change_pwd);
		mabHttpUtil = AbHttpUtil.getInstance(this);
		application = (MyApplication) abApplication;

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改密码");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);

		oldPwd = (EditText) this.findViewById(R.id.oldPwd);
		userPwd = (EditText) this.findViewById(R.id.userPwd);
		userPwd2 = (EditText) this.findViewById(R.id.userPwd2);

		mClearOldPwd = (ImageButton) findViewById(R.id.clearOldPwd);
		mClearPwd = (ImageButton) findViewById(R.id.clearPwd);
		mClearPwd2 = (ImageButton) findViewById(R.id.clearPwd2);

		oldPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = oldPwd.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClearOldPwd.setVisibility(View.VISIBLE);

					mClearOldPwd.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClearOldPwd.setVisibility(View.INVISIBLE);
						}

					}, 5000);

				} else {
					mClearOldPwd.setVisibility(View.INVISIBLE);
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

		mClearOldPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				oldPwd.setText("");
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
		findPwdBtn.setOnClickListener(new FindPwdOnClickListener());
	}

	public class FindPwdOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			mStr_oldPwd = oldPwd.getText().toString().trim();
			mStr_pwd = userPwd.getText().toString().trim();
			mStr_pwd2 = userPwd2.getText().toString().trim();

			if (TextUtils.isEmpty(mStr_oldPwd)) {
				showToast(R.string.error_pwd);
				oldPwd.setFocusable(true);
				oldPwd.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_oldPwd) < 6) {
				showToast(R.string.error_pwd_length1);
				oldPwd.setFocusable(true);
				oldPwd.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_oldPwd) > 20) {
				showToast(R.string.error_pwd_length2);
				oldPwd.setFocusable(true);
				oldPwd.requestFocus();
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

			// 提交数据
			JSONObject jo = new JSONObject();
			JSONObject joData = new JSONObject();
			try {
				joData.put("new_password", mStr_pwd);
				jo.put("method", "reset-password");
				jo.put("token", application.mUser.getToken());
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
										showToast("修改密码成功");
										ChangePwdActivity.this.finish();
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
							// 显示进度框
							showProgressDialog();
						}

						// 失败，调用
						@Override
						public void onFailure(int statusCode, String content,
								Throwable error) {
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
