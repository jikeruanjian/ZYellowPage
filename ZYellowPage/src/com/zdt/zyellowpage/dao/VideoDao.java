package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.Video;

public class VideoDao extends AbDBDaoImpl<Video> {
	public VideoDao(Context context) {
		super(new DBInsideHelper(context), Video.class);
	}
}
