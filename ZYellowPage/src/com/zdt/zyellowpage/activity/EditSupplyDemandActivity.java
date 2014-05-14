package com.zdt.zyellowpage.activity;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.SupplyDemandBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;

public class EditSupplyDemandActivity extends AbActivity {
	private MyApplication application;
	private String fullName;
	AbTitleBar mAbTitleBar;
	String item_id = "0";

	@AbIocView(id = R.id.title)
	EditText tvTitle;
	@AbIocView(id = R.id.content)
	EditText tvContent;

	@AbIocView(id = R.id.type)
	Spinner spiType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edit_supply_demand);
		application = (MyApplication) abApplication;
		if (getIntent().getExtras() != null) {
			item_id = getIntent().getStringExtra("ITEMID");
		}
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(fullName);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		mAbTitleBar.setLogoLine(R.drawable.line);

		initTitleRightLayout();
		spiType.setAdapter(ArrayAdapter.createFromResource(this, R.array.type,
				android.R.layout.simple_spinner_item));
		bindData();
	}

	private void bindData() {
		if (AbStrUtil.isEmpty(item_id) || item_id.equals("0")) {
			return;
		}

		new SupplyDemandBll().getDetailOfSupplyDemand(this, item_id,
				new ZzObjectHttpResponseListener<SupplyDemand>() {

					@Override
					public void onSuccess(int statusCode, List<SupplyDemand> lis) {
						if (lis == null || lis.size() == 0)
							return;
						SupplyDemand sd = lis.get(0);
						tvTitle.setText(sd.getTitle());
						tvContent.setText(sd.getContent());
						spiType.setSelection(Integer.valueOf(sd.getType()),
								true);
					}

					@Override
					public void onStart() {
						showProgressDialog("请稍候...");
					}

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<SupplyDemand> localList) {
						showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
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
		tvSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SupplyDemand entity = new SupplyDemand();
				entity.setItem_id(item_id);
				entity.setTitle(tvTitle.getText().toString());
				entity.setContent(tvContent.getText().toString());
				entity.setType(String.valueOf(spiType.getSelectedItemPosition()));
				new SupplyDemandBll().updateSupplyDemand(
						EditSupplyDemandActivity.this,
						application.mUser.getToken(), entity,
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								showToast(content);
								EditSupplyDemandActivity.this.finish();
							}

							@Override
							public void onStart() {
								showProgressDialog();
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
}
