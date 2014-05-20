package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.ContactBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;
import com.zdt.zyellowpage.model.Contact;

public class MorePhoneActivity extends AbActivity {

	private MyApplication application;
	private List<Contact> listContact = null;
	private AbPullListView mAbPullListView = null;
	private AbTitleBar mAbTitleBar;
	private MyAdapterPhone adapter;
	private MyAdapterPhoneEdit adapterEdit;
	private String member_Id;
	private boolean isEdit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_morephone);
		if (getIntent().getExtras() != null) {
			member_Id = (String) getIntent().getExtras().get("MEMBER_ID");
			isEdit = getIntent().getBooleanExtra("isEdit", false);
		}
		// 初始化标题栏
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("更多电话");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);

		// mAbTitleBar.setLogoLine(R.drawable.line);
		application = (MyApplication) abApplication;
		listContact = new ArrayList<Contact>();
		// 获取ListView对象
		mAbPullListView = (AbPullListView) this
				.findViewById(R.id.mListViewPhone);
		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(false);
		// 如果是编辑
		if (isEdit) {
			initTitleRightLayout();
			adapterEdit = new MyAdapterPhoneEdit(MorePhoneActivity.this);
			mAbPullListView.setAdapter(adapterEdit);
			mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 编
					Intent intent = new Intent(MorePhoneActivity.this,
							EditMorePhoneAcitivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable("Contact",
							listContact.get(position - 1));
					intent.putExtras(mBundle);
					startActivity(intent);
				}
			});
			mAbPullListView
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, final int position, long arg3) {
							MorePhoneActivity.this
									.showDialog(
											"确认",
											"确认删除？",
											new android.content.DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													Contact xcontact = new Contact();
													xcontact.setItem_id("-"
															+ listContact
																	.get(position - 1)
																	.getItem_id());
													new ContactBll()
															.updateContact(
																	MorePhoneActivity.this,
																	application.mUser
																			.getToken(),
																	xcontact,
																	new ZzStringHttpResponseListener() {

																		@Override
																		public void onSuccess(
																				int statusCode,
																				String content) {
																			listContact
																					.remove(position - 1);
																			adapterEdit
																					.notifyDataSetChanged();
																			showToast(content);
																		}

																		@Override
																		public void onStart() {
																			showProgressDialog("正在删除...");
																		}

																		@Override
																		public void onFailure(
																				int statusCode,
																				String content,
																				Throwable error) {
																			showToast(content);
																		}

																		@Override
																		public void onErrorData(
																				String status_description) {
																			showToast(status_description);
																		}

																		@Override
																		public void onFinish() {
																			removeProgressDialog();
																		}

																	});
												}

											});
							return false;

						}
					});
		} else {
			adapter = new MyAdapterPhone(MorePhoneActivity.this);
			mAbPullListView.setAdapter(adapter);
		}

		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				// 改写成执行查询
				getData();
			}

			@Override
			public void onLoadMore() {
			}
		});

		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		getData();

	}

	void getData() {
		ContactBll contactBll = new ContactBll();
		contactBll.getContactList(MorePhoneActivity.this, member_Id,
				new ZzObjectHttpResponseListener<Contact>() {

					@Override
					public void onSuccess(int statusCode, List<Contact> lis) {
						if (lis == null || lis.size() == 0) {
							showToast("没有更多数据！");
							return;
						}
						listContact.clear();
						listContact.addAll(lis);

					}

					@Override
					public void onStart() {
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Contact> localList) {
						showToast(content);
					}

					@Override
					public void onErrorData(String status_description) {
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						if (isEdit) {
							adapterEdit.notifyDataSetChanged();
						} else {
							adapter.notifyDataSetChanged();
						}
						mAbPullListView.stopRefresh();
						removeProgressDialog();
					}

				});
	}

	public final class ViewHolder {
		public TextView name;
		public TextView number;
		public ImageView viewBtn;
	}

	public class MyAdapterPhone extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapterPhone(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listContact.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_contact, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.morePhoneNametextView);
				holder.number = (TextView) convertView
						.findViewById(R.id.morePhoneNumbertextView);
				holder.viewBtn = (ImageView) convertView
						.findViewById(R.id.morePhoneimageViewMorePhone);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(listContact.get(position).getDepartment() + ":"
					+ listContact.get(position).getContacter());
			holder.number.setText(listContact.get(position).getTelephone());
			holder.viewBtn.setOnClickListener(new CallBtnListener(listContact
					.get(position).getTelephone()));
			return convertView;
		}
	}

	private class CallBtnListener implements OnClickListener {
		String phoneNumber;

		public CallBtnListener(String phone) {
			phoneNumber = phone;
		}

		@Override
		public void onClick(View v) {
			// 打电话
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
			intent.setData(Uri.parse("tel:" + phoneNumber));
			startActivity(intent);
		}
	}

	/**
	 * 编辑更多电话按钮
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyAdapterPhoneEdit extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapterPhoneEdit(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listContact.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_contact, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.morePhoneNametextView);
				holder.number = (TextView) convertView
						.findViewById(R.id.morePhoneNumbertextView);
				holder.viewBtn = (ImageView) convertView
						.findViewById(R.id.morePhoneimageViewMorePhone);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(listContact.get(position).getDepartment() + ":"
					+ listContact.get(position).getContacter());
			holder.number.setText(listContact.get(position).getTelephone());
			holder.viewBtn.setImageResource(R.drawable.index_fh);
			return convertView;
		}
	}

	/**
	 * 如果是编辑就添加添加按钮
	 */
	private void initTitleRightLayout() {

		View rightViewMore = mInflater.inflate(R.layout.more_btn, null);
		mAbTitleBar.addRightView(rightViewMore);

		Button btnAdd = (Button) rightViewMore.findViewById(R.id.moreBtn);
		btnAdd.setBackgroundResource(R.color.transparent);
		btnAdd.setText(" 添加  ");
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MorePhoneActivity.this,
						EditMorePhoneAcitivity.class);
				startActivity(intent);
			}
		});

	}
}