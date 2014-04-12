package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.Area;

public class AreaDao extends AbDBDaoImpl<Area> {
	public AreaDao(Context context) {
		super(new DBInsideHelper(context), Area.class);
	}
}