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
import com.zdt.zyellowpage.bll.ContactBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Contact;

/**
 * 更改坐标
 * 
 * @author Kevin
 * 
 */
public class EditMorePhoneAcitivity extends AbActivity {
	private MyApplication application;
	AbTitleBar mAbTitleBar;

	@AbIocView(id = R.id.departmentEditText)
	EditText etDepartment; // 部门
	@AbIocView(id = R.id.phoneNameEditText)
	EditText etPhoneName;// 姓名
	@AbIocView(id = R.id.phoneNumberEditText)
	EditText etPhoneNumber;// 电话

	@AbIocView(id = R.id.clearDepartment)
	ImageButton clearDepartment;
	@AbIocView(id = R.id.clearPhoneName)
	ImageButton clearPhoneName;
	@AbIocView(id = R.id.clearPhoneNumber)
	ImageButton clearPhoneNumber;
	Contact newContact;
	String item_id = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edit_more_phone);
		if (getIntent().getExtras() != null) {
			newContact = (Contact) getIntent().getSerializableExtra("Contact");
		} else {
			newContact = null;
		}
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("详细信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);

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
				newContact = new Contact();
				newContact.setDepartment(etDepartment.getText().toString());
				newContact.setContacter(etPhoneName.getText().toString());
				newContact.setTelephone(etPhoneNumber.getText().toString());
				newContact.setItem_id(item_id);
				new ContactBll().updateContact(EditMorePhoneAcitivity.this,
						application.mUser.getToken(), newContact,
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								showToast(content);
								EditMorePhoneAcitivity.this.finish();
							}

							@Override
							public void onStart() {
								showProgressDialog("正在保存...");
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

							@Override
							public void onFinish() {
								removeProgressDialog();
							}

						});
			}
		});

	}

	/**
	 * 绑定默认的用户信息
	 */
	private void bindData() {
		if (newContact != null) {
			this.etDepartment.setText(newContact.getDepartment());
			this.etPhoneName.setText(newContact.getContacter());
			this.etPhoneNumber.setText(newContact.getTelephone());
			item_id = newContact.getItem_id();
		}
	}

	/**
	 * 绑定textView清空按钮
	 */
	private void bindClear() {
		clearDepartment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etDepartment.setText("");
			}
		});

		clearPhoneName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etPhoneName.setText("");
			}
		});

		clearPhoneNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etPhoneNumber.setText("");
			}
		});

		etPhoneNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = etPhoneNumber.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearPhoneNumber.setVisibility(View.VISIBLE);
					clearPhoneNumber.postDelayed(new Runnable() {

						@Override
						public void run() {
							clearPhoneNumber.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					clearPhoneNumber.setVisibility(View.INVISIBLE);
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
	}
}
