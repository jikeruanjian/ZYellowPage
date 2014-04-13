package com.zdt.zyellowpage.global;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ab.global.AbConstant;
import com.zdt.zyellowpage.dao.UserInsideDao;
import com.zdt.zyellowpage.model.User;

public class MyApplication extends Application {

	// 登录用户
	public User mUser = null;

	public String cityid = Constant.DEFAULTCITYID;
	public String cityName = Constant.DEFAULTCITYNAME;
	public boolean userPasswordRemember = false;
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
		Boolean userPwdRemember = sp.getBoolean(
				Constant.USERPASSWORDREMEMBERCOOKIE, false);

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

			userPasswordRemember = userPwdRemember;
		}
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

}
