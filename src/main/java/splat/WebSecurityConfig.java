package splat;

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
	@ConfigurationProperties("access")
	public AccessProperties accessProperties() {
		return new AccessProperties();
	}

	@Bean
	public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter(AccessProperties accessProperties) {
		return new WebSecurityConfigurerAdapter() {
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().logout().permitAll();
			}

			@SuppressWarnings("deprecation")
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				
				UserBuilder builder = User.withDefaultPasswordEncoder().username(accessProperties.getUsername()).password(accessProperties.getPassword()).roles("USER");
				
				auth.inMemoryAuthentication().withUser(builder);
				
			}
		};
	}

}
