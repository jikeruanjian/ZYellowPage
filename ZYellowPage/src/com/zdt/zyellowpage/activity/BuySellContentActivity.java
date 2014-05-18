package com.zdt.zyellowpage.activity;

import java.util.List;

import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.bll.SupplyDemandBll;
import com.zdt.zyellowpage.listenser.ZzObjectHttpResponseListener;
import com.zdt.zyellowpage.model.SupplyDemand;

public class BuySellContentActivity extends AbActivity {
	private AbTitleBar mAbTitleBar = null;
	private String Item_id = null;
	private WebView webContent;
	private TextView tvTitle;
	private TextView tvTime;
	private TextView tvType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.supply_demand_detail);

		if (getIntent().getExtras() != null) {
			Item_id = (String) getIntent().getExtras().get("ITEMID");
		}
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("详细信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleLayoutBackground(R.color.orange_background);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// mAbTitleBar.setLogoLine(R.drawable.line);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.RIGHT);
		webContent = (WebView) this.findViewById(R.id.content);
		tvTitle = (TextView) this.findViewById(R.id.title);
		tvTime = (TextView) this.findViewById(R.id.time);
		tvType = (TextView) this.findViewById(R.id.type);
		if (Item_id != null) {
			getData();
		}
	}

	private void getData() {
		SupplyDemandBll bll = new SupplyDemandBll();
		bll.getDetailOfSupplyDemand(BuySellContentActivity.this, Item_id,
				new ZzObjectHttpResponseListener<SupplyDemand>() {

					@Override
					public void onSuccess(int statusCode, List<SupplyDemand> lis) {
						if (lis == null || lis.size() == 0) {
							BuySellContentActivity.this.showToast("没有更多数据！");
							return;
						}
						tvTitle.setText(lis.get(0).getTitle());
						tvTime.setText(lis.get(0).getTime());
						tvType.setText("0".equals(lis.get(0).getType()) ? "供应信息"
								: "求购信息");

						webContent.getSettings().setDefaultTextEncodingName(
								"UTF-8");
						String text = lis.get(0).getContent();
						if (AbStrUtil.isEmpty(text)) {
							text = "用户暂时还未添加该项数据";
						}
						webContent.loadDataWithBaseURL(null, text, "text/html",
								"utf-8", null);
						BuySellContentActivity.this.removeProgressDialog();
					}

					@Override
					public void onStart() {
						showProgressDialog("同步信息...");
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

					@Override
					public void onFinish() {
						removeProgressDialog();
					}

				});
	}

}
