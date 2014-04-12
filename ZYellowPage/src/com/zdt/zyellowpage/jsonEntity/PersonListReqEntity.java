package com.zdt.zyellowpage.jsonEntity;

/**
 * 获取个人列表参数类
 * 
 * @author Kevin
 * 
 */
public class PersonListReqEntity {

	private int page_number;

	private int max_size;

	private String area_id;

	/**
	 * 当关键词为”
	 * list-hot”时表示获取热门关注个人列表，当关键词为”list-yzz”表示有资质列表，当关键词为”list-wzz”表示无效列表
	 * ,当关键词为”list-5002”表示获取某分类下的个人列表, 5002表示个人分类id,当关键词为其它内容的时候，表示搜索个人列表
	 */
	private String keyword;

	public PersonListReqEntity( int page,int max,String area,String key){
		
		page_number= page;
		max_size = max;
		area_id = area;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
