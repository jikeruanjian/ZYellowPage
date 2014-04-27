package com.zdt.zyellowpage.listenser;

import java.io.Serializable;

import android.app.Activity;

public interface ZzUserInfoChangeListener extends Serializable {

	public void onChange(String oldContent, String newContent, Activity activity);

}
