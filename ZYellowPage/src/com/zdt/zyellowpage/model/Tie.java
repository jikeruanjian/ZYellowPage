package com.zdt.zyellowpage.model;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 电子请帖
 * 
 * @author Kevin 　　//婚庆：remark表示新郎简介，other表示新娘简介，content表示恋爱史
 *         　　//乔迁：content表示新居简介，remark和other无用。
 *         　　//聚会：content表示回忆录，remark和other无用。
 *         　　//开业：remark表示商企简介，other无用，content表示发展方向
 *         　　//庆典：remark表示发展历程，other无用，content表示经营成果
 * 
 */
@Table(name = "tie")
public class Tie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 418987853355818158L;

	@Id
	@Column(name = "_id")
	private int _id;

	/**
	 * 编号
	 */
	@Column(name = "item_id")
	private String item_id;

	/**
	 * 区域编号
	 */
	@Column(name = "area_id")
	private String area_id;

	/**
	 * 　　多张图片，多图片地址，使用“,”号分割
	 */
	@Column(name = "album")
	private String album;

	/**
	 * 　　1为婚庆2乔迁3聚会4开业5庆典
	 */
	@Column(name = "type")
	private String type;

	/**
	 * 标题
	 */
	@Column(name = "title")
	private String title;

	/**
	 * 详细信息
	 */
	@Column(name = "content ")
	private String content;

	/**
	 * 　内容
	 */
	@Column(name = "remark")
	private String remark;

	/**
	 * 　　内容
	 */
	@Column(name = "other ")
	private String other;

	/**
	 * 　　多张图片，多图片地址，使用“,”号分割
	 */
	@Column(name = "telephone")
	private String telephone;

	/**
	 * 　　1为婚庆2乔迁3聚会4开业5庆典
	 */
	@Column(name = "address")
	private String address;

	/**
	 * 标题
	 */
	@Column(name = "latitude")
	private String latitude;

	/**
	 * 详细信息
	 */
	@Column(name = "longitude")
	private String longitude;

	/**
	 * 　内容
	 */
	@Column(name = "more")
	private String more;

	/**
	 * 　　内容
	 */
	@Column(name = "password")
	private String password;
}
