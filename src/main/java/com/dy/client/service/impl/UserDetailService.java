package com.dy.client.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dy.client.dao.IMemberDao;
import com.dy.client.dao.IRoleDao;
import com.dy.client.vo.Member;
@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	private IMemberDao memberDAO ;
	@Autowired
	private IRoleDao roleDAO ;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = this.memberDAO.get(username) ; // 根据用户ID查询数据
		if (member == null) {	// 数据信息不存在
			throw new UsernameNotFoundException("用户“" + username + "”信息不存在，无法进行登录！");
		}	
		// 定义一个集合，保存所有的授权信息
		List<GrantedAuthority> allGrantedAuthority = new ArrayList<GrantedAuthority>() ;
		// 授权信息的内容需要通过IRoleDAO获取，根据用户名查询
		Set<String> allRoles = this.roleDAO.get(username) ; // 查询所有的角色
		Iterator<String> roleIter = allRoles.iterator() ;
		while (roleIter.hasNext()) {	// 迭代保存角色信息
			allGrantedAuthority.add(new SimpleGrantedAuthority(roleIter.next())) ;	
		}
		boolean enabled = member.getEnabled().equals(1) ; // 启用状态
		UserDetails user = new User(username, member.getPassword(), enabled, true, true, true, allGrantedAuthority);
		return user ; 
	}

}
