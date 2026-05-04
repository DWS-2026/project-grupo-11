package es.footleague.app.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {

	private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;

	public UserLoginService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String username = loginRequest.getUsername();
			UserDetails user = userDetailsService.loadUserByUsername(username);

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
			response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

			log.info("User {} logged in successfully", username);
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
					"Auth successful. Tokens are created in cookie.");
			return ResponseEntity.ok().body(loginResponse);

		} catch (Exception e) {
			log.error("Login failed for username: {}", loginRequest.getUsername(), e);
			AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Login failed: Invalid credentials", e.getMessage());
			return ResponseEntity.status(401).body(errorResponse);
		}
	}

	public ResponseEntity<AuthResponse> refresh(HttpServletResponse response, String refreshToken) {
		try {
			var claims = jwtTokenProvider.validateToken(refreshToken);
			UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

			log.info("Token refreshed for user {}", claims.getSubject());
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
					"Auth successful. New access token created in cookie.");
			return ResponseEntity.ok().body(loginResponse);

		} catch (Exception e) {
			log.error("Error while processing refresh token", e);
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Failure while processing refresh token", e.getMessage());
			return ResponseEntity.status(401).body(loginResponse);
		}
	}

	public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
		try {
			SecurityContextHolder.clearContext();
			response.addCookie(removeTokenCookie(TokenType.ACCESS));
			response.addCookie(removeTokenCookie(TokenType.REFRESH));

			log.info("User logged out successfully");
			AuthResponse logoutResponse = new AuthResponse(AuthResponse.Status.SUCCESS, "Logout successful");
			return ResponseEntity.ok().body(logoutResponse);

		} catch (Exception e) {
			log.error("Error while processing logout", e);
			AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Logout failed", e.getMessage());
			return ResponseEntity.status(500).body(errorResponse);
		}
	}

	private Cookie buildTokenCookie(TokenType type, String token) {
		Cookie cookie = new Cookie(type.cookieName, token);
		cookie.setMaxAge((int) type.duration.getSeconds());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	private Cookie removeTokenCookie(TokenType type) {
		Cookie cookie = new Cookie(type.cookieName, "");
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}
}
