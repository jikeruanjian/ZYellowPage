package com.zdt.zyellowpage.bll;

import android.content.Context;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.zdt.zyellowpage.global.Constant;
import com.zdt.zyellowpage.listenser.ZzStringHttpResponseListener;

/**
 * 图片上传
 * 
 * @author Kevin
 * 
 */
public class PictureBll {

	private ZzStringHttpResponseListener stringResponseListener;
	Context mContext;

	/**
	 * 
	 * @param file
	 *            图片byte数组
	 * @param fileName
	 *            文件名（包括扩展名）
	 * @param type
	 *            类型（1为logo，2为商家相册）
	 * @param token
	 *            令牌
	 */
	public void pictureUpload(Context context, byte[] file, String fileName,
			int type, String token) {
		mContext = context;
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(mContext);
		AbRequestParams params = new AbRequestParams();
		// params.put("file", );
		// params.put("fileName", fileName);
		// mAbHttpUtil.post(Constant.PICTUREUPLOADURL, new
		// AbStringHttpResponseListener);
		//
	}

}
