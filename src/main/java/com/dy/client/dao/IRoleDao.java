package com.dy.client.dao;

import java.util.Set;

/**
 * Copyright (c) 2019 DingYu Technology
 * @description 用户行为时间记录表 
 * @date 2019年10月25日
 */
public interface IRoleDao {

	/**
	 * @description 增加行为数据 
	 * @param vo 
	 * @date 2019年10月25日
	 */
	public Set<String> get(String mid);
}
