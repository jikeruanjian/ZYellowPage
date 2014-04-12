package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.Category;

public class CategoryDao extends AbDBDaoImpl<Category> {
	public CategoryDao(Context context) {
		super(new DBInsideHelper(context), Category.class);
	}
}