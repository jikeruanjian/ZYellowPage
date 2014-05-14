package com.zdt.zyellowpage.activity;

import android.graphics.Color;
import android.os.Bundle;
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
import com.zdt.zyellowpage.bll.CertificateBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Certificate;

public class EditCertificateActivity extends AbActivity {
	private MyApplication application;
	AbTitleBar mAbTitleBar;

	@AbIocView(id = R.id.CertificateNameEditText)
	EditText etCertificateName; // 证书
	@AbIocView(id = R.id.CertificateNoEditText)
	EditText etCertificateNo;// 编号

	@AbIocView(id = R.id.clearCertificateName)
	ImageButton clearCertificateName;
	@AbIocView(id = R.id.clearCertificateNo)
	ImageButton clearCertificateNo;
	Certificate certificate;
	String item_id = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edite_certificate);
		if (getIntent().getExtras() != null) {
			certificate = (Certificate) getIntent().getSerializableExtra(
					"Certificate");
		} else {
			certificate = null;
		}
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("修改更多联系方式");
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
				certificate = new Certificate();
				certificate.setItem_id(item_id);
				certificate.setCertificate_name(etCertificateName.getText()
						.toString());
				certificate.setCertificate_no(etCertificateNo.getText()
						.toString());
				new CertificateBll().updateCertificate(
						EditCertificateActivity.this,
						application.mUser.getToken(), certificate,
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								showToast(content);
								EditCertificateActivity.this.finish();
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
		if (certificate != null) {
			this.etCertificateName.setText(certificate.getCertificate_name());
			this.etCertificateNo.setText(certificate.getCertificate_no());
			item_id = certificate.getItem_id();
		}
	}

	/**
	 * 绑定textView清空按钮
	 */
	private void bindClear() {
		clearCertificateName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etCertificateName.setText("");
			}
		});

		clearCertificateNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etCertificateNo.setText("");
			}
		});

	}
}
