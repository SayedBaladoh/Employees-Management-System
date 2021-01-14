package com.sayedbaladoh.ems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final String url = "/api/employees/**";

	// @Override
	// protected void configure(AuthenticationManagerBuilder auth) throws Exception
	// {
	// auth.inMemoryAuthentication()
	// .withUser("user")
	// .password(passwordEncoder.encode("user"))
	// .roles("USER")
	// .and()
	// .withUser("admin")
	// .password(passwordEncoder.encode("admin"))
	// .roles("ADMIN");
	// }

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/",
						"/favicon.ico",
						"/**/*.png",
						"/**/*.gif",
						"/**/*.svg",
						"/**/*.jpg",
						"/**/*.html",
						"/**/*.css",
						"/**/*.js")
				.permitAll()
				.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**")
				.permitAll()
				.antMatchers("/actuator/**", "/h2/**")
				.permitAll()
				.antMatchers("/api/auth")
				.permitAll()
				.antMatchers("/api/employees/availabile/email/**", "/api/employees/availabile//phone/**")
				.permitAll()
				.antMatchers(HttpMethod.POST, "/api/employees")
				.permitAll()
				.antMatchers(HttpMethod.PUT, url)
				.permitAll()
				.antMatchers(HttpMethod.PATCH, url)
				.permitAll()
				.antMatchers(HttpMethod.DELETE, url)
				.permitAll()
				.antMatchers(HttpMethod.GET, url)
				.permitAll()
				.antMatchers(HttpMethod.GET, "**")
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
