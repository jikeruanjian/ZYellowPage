package com.zdt.zyellowpage.db;

import android.content.Context;

import com.ab.db.orm.AbDBHelper;
import com.zdt.zyellowpage.model.Album;
import com.zdt.zyellowpage.model.Area;
import com.zdt.zyellowpage.model.Category;
import com.zdt.zyellowpage.model.SupplyDemand;
import com.zdt.zyellowpage.model.User;
import com.zdt.zyellowpage.model.Video;

/**
 * 
 * Copyright (c) 2012 All rights reserved 名称：DBInsideHelper.java
 * 描述：手机data/data下面的数据库
 * 
 * @author kevin
 * @date：2014-03-23 14:40:27
 * @version v1.0
 */
public class DBInsideHelper extends AbDBHelper {
	// 数据库名
	private static final String DBNAME = "zyellowpage.db";

	// 当前数据库的版本
	private static final int DBVERSION = 1;

	// 要初始化的表
	private static final Class<?>[] clazz = { User.class, Area.class,
			Album.class, Video.class, SupplyDemand.class, Category.class };

	public DBInsideHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}
