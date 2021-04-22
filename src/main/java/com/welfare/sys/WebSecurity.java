package com.welfare.sys;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		  auth.
          jdbcAuthentication()
          .usersByUsernameQuery(usersQuery)
          .authoritiesByUsernameQuery(rolesQuery)
          .dataSource(dataSource)
          .passwordEncoder(bCryptPasswordEncoder);
	}



	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		 http.authorizeRequests().requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
   
		http.
            authorizeRequests()
            .antMatchers("/plugins/**").permitAll()
            .antMatchers("/bower_components/**").permitAll()
            .antMatchers("/dist/**").permitAll()
            .antMatchers("/css/**").permitAll()
           .antMatchers("/*").permitAll()
           .antMatchers("/plugins/**").permitAll()
           .antMatchers("/assets/**").permitAll()
           .antMatchers("/dist/**").permitAll()
           .antMatchers("/css/**").permitAll()
          .antMatchers("/add/*").permitAll()
          .antMatchers("/add/pic").permitAll()
          .antMatchers("/add/**").authenticated()
          .antMatchers("/*").permitAll()
           .antMatchers("/add/**").permitAll()
           .antMatchers("/login").permitAll()
           .antMatchers("/my/**").permitAll()
           .antMatchers("/registration").permitAll()
           .antMatchers("/admin/**").hasAnyAuthority("Clerk","Executive","Chairman")
           .antMatchers("/dashboard").authenticated()
           .antMatchers("/staff/**").hasAuthority("Staff").anyRequest()
           .authenticated().and().csrf().disable().formLogin()
           .loginPage("/login").failureUrl("/login?error=true")
           //successHandler(successHandler)
           .defaultSuccessUrl("/dashboard")
           .usernameParameter("email")
           .passwordParameter("password")
           .and().logout()
           .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
           .logoutSuccessUrl("/login").and().exceptionHandling()
           .accessDeniedPage("/pages/404");
	}



	@Override
	public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web)
			throws Exception {
		// TODO Auto-generated method stub
		  web
          .ignoring()
          .antMatchers("/resources/*", "/plugins/*", "/css/**", "/js/**", "dist/**");

	}
	   
}
