package com.zdt.zyellowpage.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.zdt.zyellowpage.db.DBInsideHelper;
import com.zdt.zyellowpage.model.Album;

public class AlbumDao extends AbDBDaoImpl<Album> {
	public AlbumDao(Context context) {
		super(new DBInsideHelper(context), Album.class);
	}
}