package com.zdt.zyellowpage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.commonsware.cwac.richedit.RichEditText;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzUserInfoChangeListener;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.util.HTMLDecoder;

public class RichTextEditorActivity extends AbActivity {
	private MyApplication application;

	@AbIocView(id = R.id.editor)
	RichEditText editor = null;
	@AbIocView(id = R.id.btnConfirm)
	Button btnConfirm = null;

	String newContent;

	ZzUserInfoChangeListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rich_text_edit);
		application = (MyApplication) abApplication;

		final String content = getIntent().getStringExtra("content");
		String title = getIntent().getStringExtra("title");
		listener = createZzUserInfoListener(getIntent().getStringExtra("type"));

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(title);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);

		if (!AbStrUtil.isEmpty(content))
			editor.setText(Html.fromHtml(content));
		editor.enableActionModes(true);
		btnConfirm.setText("保存");

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newContent = HTMLDecoder.decode(Html.toHtml(editor.getText()));
				if ((content == null && newContent == null)
						|| newContent.equals(content)) {
					showToast("保存成功");
					finish();
					return;
				}

				if (listener != null)
					listener.onChange(content, newContent,
							RichTextEditorActivity.this);
			}
		});
	}

	private ZzUserInfoChangeListener createZzUserInfoListener(final String type) {

		ZzUserInfoChangeListener lis = new ZzUserInfoChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8976555448339728308L;

			@Override
			public void onChange(String oldContent, final String newContent,
					final Activity activity) {
				User user = new User();
				if (type.equals("summary")) {
					user.setSummary(newContent);
				} else if (type.equals("experience")) {
					user.setExperience(newContent);
				} else if (type.equals("specialty")) {
					user.setSpecialty(newContent);
				} else if (type.equals("discount")) {
					user.setDiscount(newContent);
				} else if (type.equals("scope")) {
					user.setScope(newContent);
				}
				new UserBll().updateUser(RichTextEditorActivity.this, user,
						application.mUser.getToken(),
						new ZzStringHttpResponseListener() {

							@Override
							public void onSuccess(int statusCode, String content) {
								// 修改成功，把信息同步到
								// application.muser
								if (type.equals("summary")) {
									application.mUser.setSummary(newContent);
								} else if (type.equals("experience")) {
									application.mUser.setExperience(newContent);
								} else if (type.equals("specialty")) {
									application.mUser.setSpecialty(newContent);
								} else if (type.equals("discount")) {
									application.mUser.setDiscount(newContent);
								} else if (type.equals("scope")) {
									application.mUser.setScope(newContent);
								}

								UserInsideDao userDao = new UserInsideDao(
										RichTextEditorActivity.this);
								userDao.startWritableDatabase(false);
								userDao.update(application.mUser);
								userDao.closeDatabase(false);

								showToast(content);
								RichTextEditorActivity.this.finish();
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
		};
		return lis;
	}
}
