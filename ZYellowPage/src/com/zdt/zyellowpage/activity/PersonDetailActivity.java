package com.zdt.zyellowpage.activity;

import java.util.List;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.User;

public class PersonDetailActivity extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String member_id;
	private  User userPerson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_persondetail);
		application = (MyApplication) abApplication;
		
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.person_detail);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		userPerson =new User();
		if (getIntent().getExtras() != null) {
			member_id = (String) getIntent().getExtras().get("MEMBER_ID");
		}
		getData();
//		姓名、性别、年龄、所在地、民族、电话、电子邮箱、编号、QQ、学校、专业、行业、
//		关键词、地址、个人资质、个人特长、个人简介、成功案例、个人二维码、附件地址
	}
	
	
	protected void getData(){
		UserBll bll = new UserBll();
		bll.getDetailPerson(PersonDetailActivity.this, member_id,
				new ZzObjectHttpResponseListener<User>(){

					@Override
					public void onSuccess(int statusCode, List<User> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							return;
						}
						userPerson = (User)lis.get(0);
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<User> localList) {
						// TODO Auto-generated method stub
						if (localList == null || localList.size() == 0) {
							return;
						}
						userPerson = (User)localList.get(0);
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						removeProgressDialog();
						//showToast(userPerson.getFullname());
						getView();
					}

				/*	@Override
					public void onSuccess(int statusCode, List<Object> lis) {
						// TODO Auto-generated method stub
						userPerson = (User)lis.get(0);
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						// TODO Auto-generated method stub
						showToast(error.getMessage());
					}

					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						removeProgressDialog();
						//showToast(userPerson.getFullname());
						TextView name= (TextView)PersonDetailActivity.this.findViewById(R.id.personfullname);
						name.setText(userPerson.getFullname());
					}
			*/
		});
	}


	protected void getView() {
		ImageView imageUserLogo= (ImageView)PersonDetailActivity.this.findViewById(R.id.person_detail_photo);
		new AbImageDownloader(PersonDetailActivity.this).display(imageUserLogo,
				userPerson.getLogo());
		
		TextView name= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_name);
		TextView sex= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_sex);
		TextView age= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_age);
		TextView adress= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_address);
		TextView mz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_mz);
		TextView tel= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_tel);
		TextView email= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_email);
		TextView num= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_num);
		TextView qq= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_qq);
		TextView school= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_school);
		TextView zy= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_zy);
		TextView hy= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_hy);
		TextView gjc= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_gjc);
		TextView dz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_dz);
		TextView grewm= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_grewm);
		TextView fjdz= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_fjdz);
		TextView info= (TextView)PersonDetailActivity.this.findViewById(R.id.person_detail_INFO);
		
		name.setText(userPerson.getFullname());
		sex.setText(userPerson.getSex());
		num.setText(userPerson.getArea_id());
		tel.setText(userPerson.getTelephone());
		adress.setText(userPerson.getCity());
		email.setText(userPerson.getEmail());
		age.setText(userPerson.getAge());
		mz.setText(userPerson.getNation());
		qq.setText(userPerson.getQq());
		school.setText(userPerson.getSchool());
		zy.setText(userPerson.getProfessional());
		hy.setText(userPerson.getCategory_name());
		gjc.setText(userPerson.getKeyword());
		dz.setText(userPerson.getAddress());
		grewm.setText(userPerson.getQr_code());
		fjdz.setText(userPerson.getSchool());
		
		info.setText(userPerson.getSummary());
	}
	
	
}
