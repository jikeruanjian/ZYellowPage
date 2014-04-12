package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.SupplyDemand;

public class SupplyDemandDao extends AbDBDaoImpl<SupplyDemand> {
	public SupplyDemandDao(Context context) {
		super(new DBInsideHelper(context), SupplyDemand.class);
	}
}