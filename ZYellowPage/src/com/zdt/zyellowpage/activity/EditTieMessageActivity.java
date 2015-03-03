package com.zdt.zyellowpage.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.TieBll;
import com.zdt.zyellowpage.jsonEntity.TieMessageReqEntity;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

/**
 * 电子请帖签到,增加活修改
 * 
 * @author Kevin
 * 
 */
public class EditTieMessageActivity extends AbActivity {
	// private MyApplication application;
	AbTitleBar mAbTitleBar;
	/**
	 * 请帖ID
	 */
	String item_id = "0";

	String tie_type = "1";

	String tie_code;

	@AbIocView(id = R.id.fullname)
	EditText etFullname;
	@AbIocView(id = R.id.telephone)
	EditText etTelephone;
	@AbIocView(id = R.id.amount)
	EditText etAmount;
	@AbIocView(id = R.id.message)
	EditText etMessage;
	@AbIocView(id = R.id.code)
	EditText etCode;

	@AbIocView(id = R.id.friend)
	Spinner spiFriend;
	@AbIocView(id = R.id.attend)
	Spinner spiAttend;

	@AbIocView(id = R.id.clearFullname)
	ImageButton clearFullname;
	@AbIocView(id = R.id.clearTelephone)
	ImageButton clearTelephone;
	@AbIocView(id = R.id.clearAmount)
	ImageButton clearAmount;
	@AbIocView(id = R.id.clearMessage)
	ImageButton clearMessage;
	@AbIocView(id = R.id.clearCode)
	ImageButton clearCode;

	@AbIocView(id = R.id.rllFriend)
	RelativeLayout rllFriend;

	@AbIocView(id = R.id.rllCode)
	RelativeLayout rllCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.edit_tie_message);
		// application = (MyApplication) abApplication;
		if (getIntent().getExtras() != null) {
			item_id = getIntent().getStringExtra("ITEM_ID");
			tie_type = getIntent().getStringExtra("TYPE");
			tie_code = getIntent().getStringExtra("PASSWORD");
		}

		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("参加");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);

		initTitleRightLayout();
		if (!AbStrUtil.isEmpty(tie_code)) {
			rllCode.setVisibility(View.VISIBLE);
		}
		if (tie_type.equals("1")) {
			// 如果是婚庆
			rllFriend.setVisibility(View.VISIBLE);
			ArrayAdapter<CharSequence> friendAtapter = ArrayAdapter
					.createFromResource(this, R.array.tieFriend,
							android.R.layout.simple_spinner_item);
			friendAtapter
					.setDropDownViewResource(R.layout.spinner_dropdown_style);
			spiFriend.setAdapter(friendAtapter);
		}
		ArrayAdapter<CharSequence> attendAtapter = ArrayAdapter
				.createFromResource(this, R.array.tieAttend,
						android.R.layout.simple_spinner_item);
		attendAtapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
		spiAttend.setAdapter(attendAtapter);
		bindData();
	}

	/**
	 * 绑定默认数据，用在编辑的时候
	 */
	private void bindData() {
		// TODO 绑定默认数据
	}

	private boolean checkInput() {
		if (etFullname.getText().toString().length() <= 0) {
			showToast("请输入姓名");
			etFullname.setFocusable(true);
			etFullname.requestFocus();
			return false;
		} else if (etTelephone.getText().toString().length() <= 0) {
			showToast("请输入手机号码，以方便联系");
			etTelephone.setFocusable(true);
			etTelephone.requestFocus();
			return false;
		} else if (!AbStrUtil.isMobileNo(etTelephone.getText().toString())) {
			showToast("请输入正确手机号码");
			etTelephone.setFocusable(true);
			etTelephone.requestFocus();
			return false;
		} else if (etAmount.getText().toString().length() <= 0) {
			showToast("请输入参加人数");
			etAmount.setFocusable(true);
			etAmount.requestFocus();
			return false;
		} else if (!AbStrUtil.isEmpty(tie_code)
				&& etCode.getText().toString().length() <= 0) {
			showToast("请输入验证码");
			etCode.setFocusable(true);
			etCode.requestFocus();
			return false;
		} else if (!AbStrUtil.isEmpty(tie_code)
				&& !etCode.getText().toString().trim().equals(tie_code)) {
			showToast("验证码错误，请重新输入");
			etCode.setFocusable(true);
			etCode.requestFocus();
			return false;
		}
		return true;
	}

	private void initTitleRightLayout() {
		mAbTitleBar.clearRightView();
		TextView tvSave = new TextView(this);
		tvSave.setText(" 确定  ");
		tvSave.setTextColor(Color.WHITE);
		tvSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		mAbTitleBar.addRightView(tvSave);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		tvSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInput()) {
					TieMessageReqEntity message = new TieMessageReqEntity();
					message.setItem_id(item_id);
					message.setAmount(etAmount.getText().toString().trim());
					if ("1".equals(tie_type))
						message.setFriend((spiFriend.getSelectedItemPosition() + 1)
								+ "");
					message.setAttend((spiAttend.getSelectedItemPosition() + 1)
							+ "");
					if (!AbStrUtil.isEmpty(tie_code))
						message.setCode(etCode.getText().toString().trim());
					message.setMessage(etMessage.getText().toString().trim());
					message.setFullname(etFullname.getText().toString().trim());
					message.setTelephone(etTelephone.getText().toString()
							.trim());

					TieBll tieBll = new TieBll();
					tieBll.TieSign(EditTieMessageActivity.this, message,
							new ZzStringHttpResponseListener() {

								@Override
								public void onSuccess(int statusCode,
										String content) {
									showToast("参加成功");
									EditTieMessageActivity.this.finish();
								}

								@Override
								public void onStart() {
									showProgressDialog("正在提交...");

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
								public void onErrorData(
										String status_description) {
									showToast(status_description);
								}
							});
				}
			}
		});
	}
}
