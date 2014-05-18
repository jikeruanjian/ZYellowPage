package com.zdt.zyellowpage.global;

public class Constant {

	public static final boolean DEBUG = true;
	public static final String sharePath = "zyellowpage_share";
	public static final String USERSID = "user";

	// 页面默认显示南京，登陆后显示注册用户的城市
	public static final String CITYID = "cityId";
	public static final String CITYNAME = "cityName";
	public static final String DEFAULTCITYID = "530100";
	public static final String DEFAULTCITYNAME = "昆明市";

	// cookies
	public static final String USERNAMECOOKIE = "cookieName";
	public static final String USERPASSWORDCOOKIE = "cookiePassword";
	public static final String USERPASSWORDREMEMBERCOOKIE = "cookieRemember";
	public static final String FIRSTSTART = "firstStart";
	public static final String LASTUPDATETIME = "dataLastUpdateTime";

	// 连接超时
	public static final int timeOut = 12000;
	// 建立连接
	public static final int connectOut = 12000;
	// 获取数据
	public static final int getOut = 60000;

	// 1表示已下载完成
	public static final int downloadComplete = 1;
	// 1表示未开始下载
	public static final int undownLoad = 0;
	// 2表示已开始下载
	public static final int downInProgress = 2;
	// 3表示下载暂停
	public static final int downLoadPause = 3;

	public static final String BASEURL = "http://service.321hy.cn/api/generic";

	public static final String IMAGEBASEURL = "http://service.321hy.cn/image";

	public static final String AREAURL = "http://service.321hy.cn/generic/area";

	/**
	 * 获取所有区域
	 */
	public static final String ALLAREA = "http://service.321hy.cn/generic/areaall";

	/**
	 * 区域反查接口
	 */
	public static final String AREANAME = "http://service.321hy.cn/generic/areaid?area_name=";

	public static final String CATEGORYURL = "http://service.321hy.cn/generic/category";

	public static final String PICTUREUPLOADURL = "http://service.321hy.cn/api/upload";
	// 应用的key
	// 1512528
	public final static String APPID = "1512528";

	// jfa97P4HIhjxrAgfUdq1NoKC
	public final static String APIKEY = "jfa97P4HIhjxrAgfUdq1NoKC";

	public static final String NOCONNECT = "无法连接到网络";

	public static final int PAGE_SIZE = 10;

	public class DataType {
		/**
		 * 我的关注
		 */
		public static final String MYFOLLOWING = "myFollowing";

		/**
		 * 我的粉丝
		 */
		public static final String MYFANS = "myFans";
	}

}
