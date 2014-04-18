package com.zdt.zyellowpage.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.SupplyDemandBll;
import com.zdt.zyellowpage.global.MyApplication;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;

public class BuySellContentActivity  extends AbActivity {
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
	private String Item_id = null;
	private TextView textContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.test);

		if (getIntent().getExtras() != null) {
			Item_id = (String) getIntent().getExtras().get("ITEMID");
		}
		application = (MyApplication) abApplication;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("供求详细信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		textContent= (TextView) this.findViewById(R.id.textView1test);
		textContent.setTextSize(15);
		if(Item_id != null){
			getData();
			
		}
	}
	private void getData(){
		SupplyDemandBll bll = new SupplyDemandBll();
		bll.getDetailOfSupplyDemand(BuySellContentActivity.this, Item_id, 
				new ZzObjectHttpResponseListener<SupplyDemand>(){

			@Override
			public void onSuccess(int statusCode, List<SupplyDemand> lis) {
				// TODO Auto-generated method stub
				if (lis == null || lis.size() == 0) {
					BuySellContentActivity.this.showToast("没有更多数据！");
					return;
				}
				textContent.setText(lis.get(0).getContent());
				BuySellContentActivity.this.removeProgressDialog();
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				BuySellContentActivity.this.showProgressDialog("同步信息...");
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error, List<SupplyDemand> localList) {
				// TODO Auto-generated method stub
				
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
 
}
