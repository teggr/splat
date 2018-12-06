package splat.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	@ConfigurationProperties("splat.web.access")
	public WebAccessProperties accessProperties() {
		return new WebAccessProperties();
	}

	@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter(WebAccessProperties accessProperties) {
		return new WebSecurityConfigurerAdapter() {
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				
				// @formatter:off
 
				http
					.authorizeRequests()
						.antMatchers("/favicons/**").permitAll()
						.anyRequest().authenticated()						
						.and()
					.formLogin()
						.loginPage("/login")
							.permitAll()
							.and()
						.logout()
							.permitAll();
				
				// @formatter:on



				// http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests().anyRequest().permitAll();

			}

			@SuppressWarnings("deprecation")
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {

				UserBuilder builder = User.withDefaultPasswordEncoder().username(accessProperties.getUsername())
				        .password(accessProperties.getPassword()).roles("USER");

				auth.inMemoryAuthentication().withUser(builder);

			}
		};
	}

}
