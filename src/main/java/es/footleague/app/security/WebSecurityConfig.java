package es.footleague.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.authorizeHttpRequests(authorize -> authorize
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
					// PUBLIC PAGES
					.requestMatchers("/").permitAll()
                    .requestMatchers("/register").permitAll()
					.requestMatchers("/user/*/avatar").permitAll()
					.requestMatchers("/team/*/logo").permitAll()
					.requestMatchers("/classification").permitAll()
					.requestMatchers("/match-list").permitAll()
					.requestMatchers("/match/{id}").permitAll()
					.requestMatchers("/error").permitAll()
					.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
					// PRIVATE PAGES
					.requestMatchers("/profile/*").hasAnyRole("USER")
					.requestMatchers("/rating/{{id}}/delete").hasAnyRole("USER")
                    .requestMatchers("/profile/*/my-ratings").hasAnyRole("USER")
                    .requestMatchers("/profile/*/edit").hasAnyRole("USER")
					.requestMatchers("/match/*/rating/new").hasAnyRole("USER")
					.requestMatchers("/rating/save").hasAnyRole("USER")
					.requestMatchers("/admin/**").hasAnyRole("ADMIN")
			)
			.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);

		return http.build();
	}

}
