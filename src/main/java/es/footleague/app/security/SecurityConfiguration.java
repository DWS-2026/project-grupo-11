package es.footleague.app.security;

import es.footleague.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public UserService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PÁGINAS PÚBLICAS
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/classification").permitAll()
                        .requestMatchers("/match-list").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/team/*/logo").permitAll()

                        // PÁGINAS PRIVADAS (Ejemplo: solo ADMIN puede editar perfiles de otros)
                        
                        // .requestMatchers("/admin/**").hasRole("ADMIN")

                        // El resto de peticiones requieren estar logueado
                        .anyRequest().authenticated());

        http
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Tu HTML de login
                        .failureUrl("/login") // Si falla, vuelve al login
                        .defaultSuccessUrl("/") // Si tiene éxito, va a la home
                        .permitAll());

        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login") // Al salir, va al login
                        .permitAll());

        return http.build();
    }
}
