package com.dy.client.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.dy.client.filter.SmsCodeFilter;
import com.dy.client.filter.ValidateCodeFilter;
import com.dy.client.service.impl.UserDetailService;
import com.dy.client.util.MySessionExpiredStrategy;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启注释
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private MyAuthenticationSucessHandler authenticationSucessHandler;
	@Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;
	@Autowired
	private ValidateCodeFilter validateCodeFilter;
	@Autowired
    private UserDetailService userDetailService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;
    @Autowired
    private SmsCodeFilter smsCodeFilter;
    @Autowired
    private MySessionExpiredStrategy sessionExpiredStrategy;
    @Autowired
    private MyLogOutSuccessHandler logOutSuccessHandler;
	@Autowired
	private MyAuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
			.addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class); // 添加短信验证码校验过滤器
		http.formLogin() // 表单方式
		        .loginPage("/authentication/require")   //登录真实提交路径
		        .loginProcessingUrl("/login")  //跟表单提交路径保持一致
		        .successHandler(authenticationSucessHandler) // 处理登录成功
		        .failureHandler(authenticationFailureHandler); // 处理登录失败
        http.rememberMe()
            .tokenRepository(persistentTokenRepository()) // 配置 token 持久化仓库
            .tokenValiditySeconds(3600) // remember 过期时间，单为秒
            .userDetailsService(userDetailService); // 处理自动登录逻辑
        http
	        .authorizeRequests() // 授权配置
	        .antMatchers("/authentication/require",
	        		"/login.html","/code/image",
	        		"/code/sms","/session/invalid").permitAll()
	        .anyRequest()  // 所有请求
	        .authenticated(); // 都需要认证
        http
	        .logout()
	        .logoutUrl("/signout")
	        //.logoutSuccessUrl("/signout/success")
	        .logoutSuccessHandler(logOutSuccessHandler)
	        .deleteCookies("JSESSIONID");
        http
	        .sessionManagement() // 添加 Session管理器
	        .invalidSessionUrl("/session/invalid") // Session失效后跳转到这个链接
	        .maximumSessions(1)
	        .expiredSessionStrategy(sessionExpiredStrategy);
        http.exceptionHandling().accessDeniedHandler(authenticationAccessDeniedHandler);
        http
			.apply(smsAuthenticationConfig); // 将短信验证码认证配置加到 Spring Security 中
    }
	@Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }
}
