package com.dy.client.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 自定义权限不足处理器来处理权限不足时候的操作
 * @author Dell
 *
 */
@Component
public class MyAuthenticationAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setContentType("application/json;charset=utf-8");
	        response.getWriter().write("很抱歉，您没有该访问权限");

	}

}
