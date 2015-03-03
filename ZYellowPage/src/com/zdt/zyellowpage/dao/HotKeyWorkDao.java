package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.HotKeyWord;

public class HotKeyWorkDao extends AbDBDaoImpl<HotKeyWord> {
	public HotKeyWorkDao(Context context) {
		super(new DBInsideHelper(context), HotKeyWord.class);
	}
}