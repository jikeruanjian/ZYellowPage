package com.zdt.zyellowpage.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdt.zyellowpage.R;
import com.zdt.zyellowpage.model.Category;

public class CategoryExpandAdapter extends BaseExpandableListAdapter {
	private List<Category> firstLevel = new ArrayList<Category>();
	private List<List<Category>> secLevel = new ArrayList<List<Category>>();
	private Context mActivity;

	public CategoryExpandAdapter(List<Category> lisAll, Context mActivity) {
		this.mActivity = mActivity;
		if (lisAll != null && lisAll.size() > 0) {
			for (Category category : lisAll) {
				if (category.getType().equals("0")) {
					firstLevel.add(category);
				}
			}

			for (Category category : firstLevel) {
				List<Category> tempSecCategory = new ArrayList<Category>();
				for (Category categorySec : lisAll) {
					if (categorySec.getParent().equals(category.getId())) {
						tempSecCategory.add(categorySec);
					}
				}
				secLevel.add(tempSecCategory);
			}
		}
	}

	public CategoryExpandAdapter(List<Category> firstLevel,
			List<List<Category>> secLevel, Context mActivity) {
		this.firstLevel = firstLevel;
		this.secLevel = secLevel;
		this.mActivity = mActivity;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (secLevel != null && secLevel.size() > childPosition) {
			return secLevel.get(groupPosition).get(childPosition);
		}
		return null;
	}

	/**
	 * 该方法不可以
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// 自己定义一个获得文字信息的方法
	TextView getTextView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 64);
		TextView textView = new TextView(mActivity);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(50, 0, 0, 0);
		textView.setTextSize(16);
		textView.setTextColor(mActivity.getResources().getColor(
				R.color.propertyvalue));
		return textView;
	}

	// 重写ExpandableListAdapter中的各个方法
	@Override
	public int getGroupCount() {
		if (firstLevel != null)
			return firstLevel.size();
		else {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (firstLevel != null && firstLevel.size() > groupPosition) {
			return firstLevel.get(groupPosition);
		} else {
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (secLevel != null && secLevel.size() > groupPosition)
			return secLevel.get(groupPosition).size();
		else
			return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LinearLayout parentLayout = (LinearLayout) View.inflate(mActivity,
				R.layout.expandgroup_item, null);
		TextView parentTextView = (TextView) parentLayout
				.findViewById(R.id.groupNametextView);
		parentTextView.setText(((Category) getGroup(groupPosition)).getName());
		ImageView parentImageViw = (ImageView) parentLayout
				.findViewById(R.id.groupNameImageView);
		if (isExpanded) {
			parentImageViw.setBackgroundResource(R.drawable.changecity);
		} else {
			parentImageViw.setBackgroundResource(R.drawable.changecity_right);
		}

		return parentLayout;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(mActivity);
		ll.setOrientation(0);
		TextView textView = getTextView();
		textView.setText(((Category) getChild(groupPosition, childPosition))
				.getName());
		ll.addView(textView);
		return ll;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
