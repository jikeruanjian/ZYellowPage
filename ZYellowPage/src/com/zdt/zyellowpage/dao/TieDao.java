package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.Tie;

public class TieDao extends AbDBDaoImpl<Tie> {
	public TieDao(Context context) {
		super(new DBInsideHelper(context), Tie.class);
	}
}