package com.zdt.zyellowpage.model;

/**
 * 商家查询的条件
 * 
 * @author Administrator
 * 
 */
public class QueryEnterprise {
	// 0～n 页码
	private int page_number;
	// 0～n 每页最大条目数
	private int max_size;
	// 区域代码
	private String area_id;
	// 此字段删除，所有排序规则我已处理，开发时无需再考虑
	// private String sort;
	// 注意此处：传入关键词如下：当关键词为”list-hot”时表示获取热门商家列表，当关键词为”list-0102”表示获取某分类下的商家列表,0102表示分类id,当关键词为其它内容的时候，表示搜索商家列表
	private String keyword;

	public QueryEnterprise(int page, int max, String area, String s, String key) {
		page_number = page;
		max_size = max;
		area_id = area;
		// sort = s;
		keyword = key;
	}

	public int getPage_number() {
		return page_number;
	}

	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}

	public int getMax_size() {
		return max_size;
	}

	public void setMax_size(int max_size) {
		this.max_size = max_size;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	/*
	 * public String getSort() { return sort; } public void setSort(String sort)
	 * { this.sort = sort; }
	 */
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
