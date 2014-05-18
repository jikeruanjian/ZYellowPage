package com.zdt.zyellowpage.global;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ab.global.AbConstant;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.model.User;

public class MyApplication extends Application {

	// 登录用户
	public User mUser = null;

	public String cityid = Constant.DEFAULTCITYID;
	public String cityName = Constant.DEFAULTCITYNAME;
	public boolean userPasswordRemember = true;
	public boolean ad = false;
	public boolean firstStart = true;

	@Override
	public void onCreate() {
		super.onCreate();
		initLoginParams();
	}

	/**
	 * 上次登录参数
	 * 
	 * @throws
	 * @date：2014年4月7日 15:10:40
	 * @version v1.0
	 */
	private void initLoginParams() {
		SharedPreferences sp = getSharedPreferences(AbConstant.SHAREPATH,
				Context.MODE_PRIVATE);
		String userName = sp.getString(Constant.USERNAMECOOKIE, null);
		String userPwd = sp.getString(Constant.USERPASSWORDCOOKIE, null);
		// Boolean userPwdRemember = sp.getBoolean(
		// Constant.USERPASSWORDREMEMBERCOOKIE, true);

		cityid = sp.getString(Constant.CITYID, Constant.DEFAULTCITYID);
		cityName = sp.getString(Constant.CITYNAME, Constant.DEFAULTCITYNAME);
		firstStart = sp.getBoolean(Constant.FIRSTSTART, true);

		if (userName != null) {

			UserInsideDao userDao = new UserInsideDao(getApplicationContext());
			userDao.startReadableDatabase(false);
			List<User> lisUser = userDao.queryList("username=?",
					new String[] { userName });
			if (lisUser != null && lisUser.size() > 0) {
				mUser = lisUser.get(0);
				mUser.setToken(null);
			}
			userDao.closeDatabase(false);
			if (mUser == null) {
				mUser = new User();
				mUser.setUsername(userName);
				mUser.setPassword(userPwd);
			}

			// userPasswordRemember = userPwdRemember;
		}
		initImageLoader(getApplicationContext());
	}

	/**
	 * 清空上次登录参数
	 */
	public void clearLoginParams() {
		SharedPreferences sp = getSharedPreferences(AbConstant.SHAREPATH,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		mUser = null;
	}

	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
