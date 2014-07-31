package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.HotWord;

public class HotWorkDao extends AbDBDaoImpl<HotWord> {
	public HotWorkDao(Context context) {
		super(new DBInsideHelper(context), HotWord.class);
	}
}