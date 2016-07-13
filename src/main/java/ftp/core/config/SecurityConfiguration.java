package ftp.core.config;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.security.*;
import ftp.core.service.face.tx.UserService;
import ftp.core.web.filter.CsrfCookieGeneratorFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.dao.SaltSource;
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

import javax.annotation.Resource;


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
	private UserService userService;

    @Resource
    private RememberMeServices rememberMeServices;

	@Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	public SaltSource saltSource() {
		final ReflectionSaltSource reflectionSaltSource = new ReflectionSaltSource();
		reflectionSaltSource.setUserPropertyToUse("getToken");
		return reflectionSaltSource;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		final DaoAuthenticationProvider daoAuthenticationProvider = new CusomDaoAuthenticationProvider(
				this.userService);
		daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth,
			final DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider);
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
        http.requiresChannel().anyRequest().requiresSecure().and()
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
.antMatchers("/api/**")
				.authenticated();

    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }



}
