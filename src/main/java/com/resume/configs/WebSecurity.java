package com.resume.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		super.configure(http);
		http.csrf()
				.disable()
				.authorizeRequests()
				.antMatchers("/", "/register", "/submit", "/styles/**")
				.permitAll()
//				.antMatchers("/admin/**")
//				.hasRole("ADMIN")
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
//				.defaultSuccessUrl("/", true)
				.permitAll()
				.and()
				.logout()
				.permitAll();
	}

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.jdbcAuthentication()
				.passwordEncoder(new BCryptPasswordEncoder())
				.dataSource(dataSource)
				.usersByUsernameQuery("SELECT username, password, enabled FROM user where username=?")
				.authoritiesByUsernameQuery("SELECT username, role FROM user where username=?");
	}


//	@Override
//	@Bean
//	protected UserDetailsService userDetailsService() {
////		return super.userDetailsService();
//		UserDetails userDetails = User.withDefaultPasswordEncoder().username("ujjwal").password("ujjwal").roles("USER").build();
//		return new InMemoryUserDetailsManager(userDetails);
//	}


}
