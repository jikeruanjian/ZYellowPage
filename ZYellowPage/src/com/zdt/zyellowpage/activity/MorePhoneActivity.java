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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.ContactBll;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.Contact;

public class MorePhoneActivity extends AbActivity{

	//private MyApplication application;
	//private List<Map<String, Object>> list = null;
	private List<Contact> listContact = null;
	private AbPullListView  mAbPullListView = null;
	private MyAdapterPhone adapter;
	private String member_Id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_morephone);
		if (getIntent().getExtras() != null) {
			member_Id = (String) getIntent().getExtras().get("MEMBER_ID");	
			Log.e("MorePhoneActivity", "----用户"+member_Id );
		}
		// 初始化标题栏
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("更多电话");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		//application = (MyApplication) abApplication;
		listContact = new ArrayList<Contact>();
		// 获取ListView对象
		mAbPullListView = (AbPullListView) this.findViewById(R.id.mListViewPhone);
		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(false);
		adapter = new MyAdapterPhone(MorePhoneActivity.this);
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
		ContactBll contactBll = new ContactBll();
		contactBll.getContactList(MorePhoneActivity.this, member_Id, 
				new ZzObjectHttpResponseListener<Contact>(){

					@Override
					public void onSuccess(int statusCode, List<Contact> lis) {
						// TODO Auto-generated method stub
						if (lis == null || lis.size() == 0) {
							showToast("没有更多数据！");
							return;
						}
						listContact.clear();
						listContact.addAll(lis);
						
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						showProgressDialog("同步信息...");
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error, List<Contact> localList) {
						// TODO Auto-generated method stub
						showToast(content);
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
			
		});
	}
	
	public final class ViewHolder{ 
        public TextView name;
        public TextView number; 
        public ImageView viewBtn; 
    } 
      
      
    public class MyAdapterPhone extends BaseAdapter{ 
  
        private LayoutInflater mInflater; 
          
          
        public MyAdapterPhone(Context context){ 
            this.mInflater = LayoutInflater.from(context); 
        } 
        @Override
        public int getCount() { 
            // TODO Auto-generated method stub 
            return listContact.size(); 
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
            holder.name.setText(listContact.get(position)
            		.getDepartment()+":"+listContact.get(position).getContacter()); 
            holder.number.setText(listContact.get(position).getTelephone()); 
            holder.viewBtn.setOnClickListener(new CallBtnListener(listContact.get(position).getTelephone()));
            return convertView; 
        }
    } 
    
    private class CallBtnListener implements OnClickListener{  
		String phoneNumber;  
        public CallBtnListener(String phone){  
        	phoneNumber= phone;  
        }  
        @Override  
        public void onClick(View v) {  
        	//打电话
        	Intent intent=new Intent(); 
        	intent.setAction(Intent.ACTION_DIAL);   //android.intent.action.DIAL 
        	intent.setData(Uri.parse("tel:" + phoneNumber)); 
        	startActivity(intent);  
        }
    }
}