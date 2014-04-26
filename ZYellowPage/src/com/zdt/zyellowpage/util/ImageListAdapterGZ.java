package com.zdt.zyellowpage.util;




import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.activity.BusinessDetailActivity;
import com.zdt.zyellowpage.activity.PopBusinessListActivity;
import com.zdt.zyellowpage.bll.UserBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

/**
 * Copyright (c) 2011 All rights reserved
 * 名称：MyListViewAdapter
 * 描述：在Adapter中释放Bitmap
 * @author zhaoqp
 * @date 2011-12-10
 * @version
 */
public class ImageListAdapterGZ extends BaseAdapter{
	
	private static String TAG = "ImageListAdapter";
	private static final boolean D = true;
  
	private Context mContext;
	//xml转View对象
    private LayoutInflater mInflater;
    //单行的布局
    private int mResource;
    //列表展现的数据
    private List mData;
    //Map中的key
    private String[] mFrom;
    //view的id
    private int[] mTo;
    //图片下载器
    private AbImageDownloader mAbImageDownloader = null;
    MyApplication application;

   /**
    * 构造方法
    * @param context
    * @param data 列表展现的数据
    * @param resource 单行的布局
    * @param from Map中的key
    * @param to view的id
    */
    public ImageListAdapterGZ(Context context,MyApplication app, List data,
            int resource, String[] from, int[] to){
    	application = app;
    	this.mContext = context;
    	this.mData = data;
    	this.mResource = resource;
    	this.mFrom = from;
    	this.mTo = to;
        //用于将xml转为View
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //图片下载器
        mAbImageDownloader = new AbImageDownloader(mContext);
        mAbImageDownloader.setWidth(100);
        mAbImageDownloader.setHeight(100);
        mAbImageDownloader.setType(AbConstant.SCALEIMG);
        mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
        mAbImageDownloader.setErrorImage(R.drawable.image_error);
        mAbImageDownloader.setNoImage(R.drawable.image_no);
        //mAbImageDownloader.setAnimation(true);
    }   
    
    @Override
    public int getCount() {
        return mData.size();
    }
    
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position){
      return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
    	  final ViewHolder holder;
    	   
          if(convertView == null){
	           //使用自定义的list_items作为Layout
	           convertView = mInflater.inflate(mResource, parent, false);
	           //减少findView的次数
			   holder = new ViewHolder();
			   

	           //初始化布局中的元素
			   holder.itemsIcon = ((ImageView) convertView.findViewById(mTo[0])) ;
			   holder.itemsTitle = ((TextView) convertView.findViewById(mTo[1]));
			   holder.itemsText = ((TextView) convertView.findViewById(mTo[2]));
			   holder.itemsBtn = ((Button) convertView.findViewById(mTo[3]));
			   holder.itemsBtn.setText("-取消关注");
			   convertView.setTag(holder);
          }else{
        	   holder = (ViewHolder) convertView.getTag();
          }
          
		  //获取该行的数据
          final Map<String, Object>  obj = (Map<String, Object>)mData.get(position);
          String imageUrl = (String)obj.get("itemsIcon");
          holder.itemsTitle.setText((String)obj.get("itemsTitle"));
          holder.itemsText.setText((String)obj.get("itemsText")); 
          //设置加载中的View
          //mAbImageDownloader.setLoadingView(convertView.findViewById(R.id.progressBar));
          //图片的下载
          mAbImageDownloader.display(holder.itemsIcon,imageUrl);
          holder.itemsBtn.setOnClickListener(new MyBtnListener(obj));
          holder.itemsTitle.setOnClickListener(new MyTitileListener((String)obj.get("Member_id")) );
          holder.itemsText.setOnClickListener(new MyTitileListener((String)obj.get("Member_id")) );
          holder.itemsIcon.setOnClickListener(new MyTitileListener((String)obj.get("Member_id")) );
          return convertView;
    }
    
    
    
    
    /**
	 * View元素
	 */
	static class ViewHolder {
		ImageView itemsIcon;
		TextView itemsTitle;
		TextView itemsText;
		Button itemsBtn;
	}
	  
	private class MyBtnListener implements OnClickListener{  
		Map<String,Object> mMember;  
        public MyBtnListener(Map<String,Object> inPosition){  
        	mMember= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub   
        	if (application.mUser != null && application.mUser.getToken() != null) {
        		  
        		UserBll bll = new UserBll();
        		 bll.followUser(mContext, application.mUser.getToken(), (String)mMember.get("Member_id"),true,
        				 new ZzStringHttpResponseListener(){

							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								
								Toast.makeText(mContext,content, Toast.LENGTH_SHORT).show();
								mData.remove(mMember);
								ImageListAdapterGZ.this.notifyDataSetChanged();
							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onFailure(int statusCode,
									String content, Throwable error) {
								// TODO Auto-generated method stub
								Toast.makeText(mContext, "取消关注失败！", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onErrorData(String status_description) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								
							}
        			 
        		 });
        		
        		
        	}
        	else
        	{
        		Toast.makeText(mContext, "请先登陆！", Toast.LENGTH_SHORT).show();  
        	}
        }  
          
    } 
	
	
	private class MyTitileListener implements OnClickListener{  
        String mMember;  
        public MyTitileListener(String inPosition){  
        	mMember= inPosition;  
        }  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub   
            //Toast.makeText(mContext, mPosition+"", Toast.LENGTH_SHORT).show(); 
        /*    Intent intent = new Intent(mContext,
					 BusinessDetailActivity.class);
			 intent.putExtra("MEMBER_ID", mMember);
			 mContext.startActivity(intent);*/
        }  
          
    } 

}
