package com.zdt.zyellowpage.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.MorePhoneActivity.MyAdapterPhoneEdit;
import com.zdt.zyellowpage.bll.CertificateBll;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Certificate;


public class CertificateListActivity extends AbActivity{

	//private MyApplication application;
	//private List<Map<String, Object>> list = null;
	private List<Certificate> listCertificate = null;
	private AbPullListView  mAbPullListView = null;
	private AbTitleBar mAbTitleBar;
	private MyAdapterCertificateEdit adapter;
	private MyAdapterPhoneEdit adapterEdit;
	private String member_Id;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_morephone);
		if (getIntent().getExtras() != null) {
			member_Id = (String) getIntent().getExtras().get("MEMBER_ID");	
		}
		// 初始化标题栏
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("资质列表");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		//application = (MyApplication) abApplication;
		listCertificate = new ArrayList<Certificate>();
		// 获取ListView对象
		mAbPullListView = (AbPullListView) this.findViewById(R.id.mListViewPhone);
		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(false);
		//如果是编辑
		initTitleRightLayout();
		adapter = new MyAdapterCertificateEdit(CertificateListActivity.this);
		mAbPullListView.setAdapter(adapter);
		
		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				//改写成执行查询
				//mAbTaskQueue.execute(item1);
				getData();
			}

			@Override
			public void onLoadMore() {
			
			}

		});
		getData();
	}
	void getData() {
		CertificateBll  certificateBll = new  CertificateBll();
		 certificateBll.getCertificateList(CertificateListActivity.this, member_Id,
				 new ZzObjectHttpResponseListener<Certificate>(){

					@Override
					public void onSuccess(int statusCode, List<Certificate> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							//PersonDetailActivity.this.showToast("获取资质信息失败！");
							return;
						}
						listCertificate.clear();
						listCertificate.addAll(lis);		
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}


					@Override
					public void onErrorData(String status_description) {
						// TODO Auto-generated method stub
						showToast(status_description);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						adapter.notifyDataSetChanged();
						mAbPullListView.stopRefresh();
						removeProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Certificate> localList) {
						// TODO Auto-generated method stub
						showToast(content);
					}
			
		});
	}
	public final class ViewHolder{ 
        public TextView name;
        public TextView number; 
        public ImageView viewBtn; 
    } 
      
	/**
     * 编辑更多电话按钮
     * @author Administrator
     *
     */
    public class MyAdapterCertificateEdit extends BaseAdapter{ 
    	  
        private LayoutInflater mInflater; 
          
          
        public MyAdapterCertificateEdit(Context context){ 
            this.mInflater = LayoutInflater.from(context); 
        } 
        @Override
        public int getCount() { 
            // TODO Auto-generated method stub 
            return listCertificate.size(); 
        } 
  
        @Override
        public Object getItem(int arg0) { 
            // TODO Auto-generated method stub 
            return null; 
        } 
  
        @Override
        public long getItemId(int arg0) { 
            // TODO Auto-generated method stub 
            return 0; 
        } 
  
        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
              
            ViewHolder holder = null; 
            if (convertView == null) { 
                holder=new ViewHolder();   
                convertView = mInflater.inflate(R.layout.item_contact, null); 
                holder.name = (TextView)convertView.findViewById(R.id.morePhoneNametextView); 
                holder.number = (TextView)convertView.findViewById(R.id.morePhoneNumbertextView); 
                holder.viewBtn = (ImageView)convertView.findViewById(R.id.morePhoneimageViewMorePhone); 
                convertView.setTag(holder); 
                  
            }else { 
                  
                holder = (ViewHolder)convertView.getTag(); 
            } 
            holder.name.setText(listCertificate.get(position)
            		.getCertificate_name()); 
            holder.number.setText(listCertificate.get(position).getCertificate_no()); 
            holder.viewBtn.setBackgroundResource(R.drawable.document_edit);
            holder.viewBtn.setOnClickListener(new EditCertificateBtnListener(listCertificate.get(position)));
            return convertView; 
        }
    } 
    /**
     * 编辑更多电话
     * @author Administrator
     *
     */
    private class EditCertificateBtnListener implements OnClickListener{  
    	Certificate certificateInfo;  
        public EditCertificateBtnListener(Certificate info){  
        	certificateInfo= info;  
        }  
        @Override  
        public void onClick(View v) {  
        	//编辑
        	Intent intent= new Intent(CertificateListActivity.this,
        			EditCertificateActivity.class);
        	Bundle mBundle = new Bundle(); 
        	mBundle.putSerializable("Certificate",certificateInfo); 
        	intent.putExtras(mBundle);
        	startActivity(intent);  
        }
    }
    /**
     * 如果是编辑就添加添加按钮
     */
    private void initTitleRightLayout() {
		
			View rightViewMore = mInflater.inflate(R.layout.more_btn, null);
			mAbTitleBar.addRightView(rightViewMore);

			Button btnAdd = (Button) rightViewMore.findViewById(R.id.moreBtn);
			btnAdd.setBackgroundResource(R.drawable.add);
			btnAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CertificateListActivity.this,
							EditCertificateActivity.class);
					startActivity(intent);
				}
			});
		
	}
}
