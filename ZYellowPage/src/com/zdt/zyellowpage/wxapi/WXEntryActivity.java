package com.zdt.zyellowpage.wxapi;

import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
	@Override
	public void onResp(BaseResp resp) {
		String result = "发送成功";

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "发送成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "发送被取消";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "发送被拒绝";
			break;
		default:
			result = "发送返回";
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		this.finish();
	}

}
