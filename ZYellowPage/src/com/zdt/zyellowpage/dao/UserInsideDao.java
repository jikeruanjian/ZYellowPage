package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.User;
/**
 * 
 * Copyright (c) 2012 All rights reserved
 * 名称：UserInsideDao.java 
 * 描述：本地数据库 在data下面
 * @author kevin
 * @date：2014-03-23 15:34:25
 * @version v1.0
 */
public class UserInsideDao extends AbDBDaoImpl<User> {
	public UserInsideDao(Context context) {
		super(new DBInsideHelper(context),User.class);
	}
}
