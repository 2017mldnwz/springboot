package com.dy.client.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dy.client.controller.TestController;
import com.dy.client.exception.ValidateCodeException;
import com.dy.client.util.ImageCode;
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

	@Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (StringUtils.equalsIgnoreCase("/login", request.getRequestURI())
                && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);

	}
	private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
		ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, TestController.SESSION_KEY_IMAGE_CODE);
	    String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

	    if (StringUtils.isBlank(codeInRequest)) {
	        throw new ValidateCodeException("验证码不能为空！");
	    }
	    if (codeInSession == null) {
	        throw new ValidateCodeException("验证码不存在！");
	    }
	    if (codeInSession.isExpire()) {
	        sessionStrategy.removeAttribute(servletWebRequest, TestController.SESSION_KEY_IMAGE_CODE);
	        throw new ValidateCodeException("验证码已过期！");
	    }
	    if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
	        throw new ValidateCodeException("验证码不正确！");
	    }
	    sessionStrategy.removeAttribute(servletWebRequest, TestController.SESSION_KEY_IMAGE_CODE);
    }	

}
