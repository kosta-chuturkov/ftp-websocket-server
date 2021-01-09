package ftp.core.config;

import ftp.core.security.*;
import ftp.core.service.face.tx.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@AutoConfigureOrder(SecurityProperties.BASIC_AUTH_ORDER)
@ConditionalOnProperty(value = "security.enabled", havingValue = "true")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
    private final Http401UnauthorizedEntryPoint authenticationEntryPoint;
    private final AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    private final AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Autowired
    public SecurityConfiguration(
            AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
            @Lazy UserService userService,
            AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler,
            AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler,
            Http401UnauthorizedEntryPoint authenticationEntryPoint,
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.ajaxAuthenticationSuccessHandler = ajaxAuthenticationSuccessHandler;
        this.userService = userService;
        this.ajaxAuthenticationFailureHandler = ajaxAuthenticationFailureHandler;
        this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new CusomDaoAuthenticationProvider(
                this.userService);
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth,
                                final DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider);
    }
    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
            "/swagger-ui/index.html"
    };

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/bower_components/**")
                .antMatchers("/i18n/**")
                .antMatchers("/swagger**")
                .antMatchers("/content/**")
                .antMatchers("/resources/**")
                .antMatchers("/test/**")
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().disable()
//                .and()
//                .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .formLogin()
                .loginProcessingUrl("/api/v1/login")
                .successHandler(this.ajaxAuthenticationSuccessHandler)
                .failureHandler(this.ajaxAuthenticationFailureHandler)
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/api/v1/logout")
                .logoutSuccessHandler(this.ajaxLogoutSuccessHandler)
                .deleteCookies("JSESSIONID", "CSRF-TOKEN")
                .permitAll()
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/register").permitAll()
                .antMatchers("/api/v1/login").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/api/v1/**")
                .authenticated();

    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }


}
