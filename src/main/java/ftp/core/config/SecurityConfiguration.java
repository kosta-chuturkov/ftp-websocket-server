package ftp.core.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.security.*;
import ftp.core.web.filter.CsrfCookieGeneratorFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Resource
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Resource
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Resource
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Resource
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private RememberMeServices rememberMeServices;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Resource
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
	public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/bower_components/**")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/resources/**")
                .antMatchers("/test/**")
                .antMatchers("/h2-console/**");
    }

    @Override
	protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf()
                .and()
                .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .rememberMe()
                .rememberMeServices(this.rememberMeServices)
                .rememberMeParameter("remember-me")
                .key(ServerConstants.REMEMBER_ME_SECURITY_KEY)
                .and()
                .formLogin()
                .loginProcessingUrl(APIAliases.LOGIN_ALIAS)
                .successHandler(this.ajaxAuthenticationSuccessHandler)
                .failureHandler(this.ajaxAuthenticationFailureHandler)
                .usernameParameter("email")
                .passwordParameter("pswd")
                .permitAll()
                .and()
                .logout()
                .logoutUrl(APIAliases.LOGOUT_ALIAS)
                .logoutSuccessHandler(this.ajaxLogoutSuccessHandler)
                .deleteCookies("JSESSIONID", "CSRF-TOKEN")
                .permitAll()
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .authorizeRequests()
                .antMatchers(APIAliases.REGISTRATION_ALIAS).permitAll()
                .antMatchers(APIAliases.LOGIN_ALIAS).permitAll()
                .antMatchers(APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS).permitAll()
.anyRequest().permitAll();
		// .antMatchers("/api/**").authenticated();

    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
