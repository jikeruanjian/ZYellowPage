package com.zdt.zyellowpage.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
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
				if (category.getParent().equals("0")) {
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

		final FirstLevViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mActivity,
					R.layout.expandgroup_item, null);
			viewHolder = new FirstLevViewHolder();
			viewHolder.itemsTitle = (TextView) convertView
					.findViewById(R.id.groupNametextView);
			viewHolder.itemsIcon = (ImageView) convertView
					.findViewById(R.id.groupNameImageView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (FirstLevViewHolder) convertView.getTag();
		}

		viewHolder.itemsTitle.setText(((Category) getGroup(groupPosition))
				.getName());
		if (isExpanded) {
			viewHolder.itemsIcon.setBackgroundResource(R.drawable.changecity);
		}else{
			viewHolder.itemsIcon.setBackgroundResource(R.drawable.changecity_right);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final SeclevViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mActivity,
					R.layout.expandchild_item, null);
			viewHolder = new SeclevViewHolder();
			viewHolder.itemsTitle = (TextView) convertView
					.findViewById(R.id.childNametextView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (SeclevViewHolder) convertView.getTag();
		}
		viewHolder.itemsTitle.setText(((Category) getChild(groupPosition,
				childPosition)).getName());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 第一级View元素
	 */
	static class FirstLevViewHolder {
		ImageView itemsIcon;
		TextView itemsTitle;
	}

	/**
	 * 第二级View元素
	 */
	static class SeclevViewHolder {
		TextView itemsTitle;
	}
}
