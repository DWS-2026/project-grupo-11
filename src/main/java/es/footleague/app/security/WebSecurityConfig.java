package es.footleague.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.footleague.app.security.jwt.JwtRequestFilter;
import es.footleague.app.security.jwt.JwtTokenProvider;
import es.footleague.app.security.jwt.UnauthorizedHandlerJwt;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.securityMatcher("/api/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC ENDPOINTS
						.requestMatchers("/.git/**", "/.env", "/**/*.bak", "/**/*.old").denyAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/logout").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/refresh").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/teams/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/matches/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/users/*/avatar").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						// USER ENDPOINTS
						.requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/users/*/avatar").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/ratings/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/events/*/ratings").hasAnyRole("USER", "ADMIN")
						// ADMIN ENDPOINTS
						.requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/teams/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/teams/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/teams/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/matches/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/matches/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/matches/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/events/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/events/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").hasRole("ADMIN")
						.anyRequest().denyAll());

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(new JwtRequestFilter(userDetailService, jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
						.requestMatchers("/.git/**", "/.env", "/**/*.bak", "/**/*.old").denyAll()
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
						.requestMatchers("/swagger-ui.html").permitAll()
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()
						.requestMatchers("/v3/api-docs.yaml").permitAll()
						// PRIVATE PAGES
						.requestMatchers("/profile/*").hasAnyRole("USER")
						.requestMatchers("/rating/{id}/delete").hasAnyRole("USER")
						.requestMatchers("/profile/*/my-ratings").hasAnyRole("USER")
						.requestMatchers("/profile/*/edit").hasAnyRole("USER")
						.requestMatchers("/match/*/rating/new").hasAnyRole("USER")
						.requestMatchers("/rating/save").hasAnyRole("USER")
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
						.anyRequest().denyAll())
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror?error=true")
						.defaultSuccessUrl("/")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.invalidateHttpSession(true) // Crucial: Invalida la sesión en el servidor
						.clearAuthentication(true) // Limpia el contexto de seguridad
						.deleteCookies("JSESSIONID") // Borra la cookie en el navegador del usuario
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}

}
