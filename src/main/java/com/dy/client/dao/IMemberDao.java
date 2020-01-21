package com.dy.client.dao;

import com.dy.client.vo.Member;

/**
 * Copyright (c) 2019 DingYu Technology
 * @description 用户行为时间记录表 
 * @date 2019年10月25日
 */
public interface IMemberDao {

	/**
	 * @description 增加行为数据 
	 * @param vo 
	 * @date 2019年10月25日
	 */
	public Member get(String mid);
}
