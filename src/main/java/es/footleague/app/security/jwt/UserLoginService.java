package es.footleague.app.security.jwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    // ✅ NUEVA: Rate limiting para prevenir brute force
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockoutTimes = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_TIME = 15 * 60 * 1000; // 15 minutos

    public UserLoginService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ✅ NUEVA: Método para verificar si el usuario está bloqueado
    private boolean isLockedOut(String username) {
        if (!loginAttempts.containsKey(username)) {
            return false;
        }

        int attempts = loginAttempts.get(username);
        if (attempts < MAX_ATTEMPTS) {
            return false;
        }

        // Verificar si el lockout ha expirado
        Long lockoutTime = lockoutTimes.get(username);
        if (lockoutTime == null) {
            return true;
        }

        long elapsedTime = System.currentTimeMillis() - lockoutTime;
        if (elapsedTime > LOCKOUT_TIME) {
            // Lockout ha expirado, limpiar
            loginAttempts.remove(username);
            lockoutTimes.remove(username);
            return false;
        }

        return true;
    }

    // ✅ NUEVA: Método para obtener tiempo restante de bloqueo
    private long getRemainingLockoutTime(String username) {
        Long lockoutTime = lockoutTimes.get(username);
        if (lockoutTime == null) {
            return 0;
        }

        long elapsedTime = System.currentTimeMillis() - lockoutTime;
        long remainingTime = LOCKOUT_TIME - elapsedTime;
        return Math.max(0, remainingTime);
    }

    public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {
        String username = loginRequest.getUsername();

        // ✅ NUEVA: Verificar si el usuario está bloqueado
        if (isLockedOut(username)) {
            long remainingTime = getRemainingLockoutTime(username);
            long remainingMinutes = remainingTime / (60 * 1000);
            log.warn("SECURITY: Login attempt for locked-out user: {} (remaining lockout: {} min)", username,
                    remainingMinutes);
            
            AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Too many login attempts. Try again in " + remainingMinutes + " minutes.");
            return ResponseEntity.status(429).body(errorResponse);
        }

        try {
            // ✅ MEJORADO: Autenticación
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails user = userDetailsService.loadUserByUsername(username);

            var newAccessToken = jwtTokenProvider.generateAccessToken(user);
            var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
            response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

            // ✅ NUEVA: Reset en login exitoso
            loginAttempts.remove(username);
            lockoutTimes.remove(username);

            log.info("User {} logged in successfully", username);
            AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
                    "Auth successful. Tokens are created in cookie.");
            return ResponseEntity.ok().body(loginResponse);

        } catch (BadCredentialsException e) {
            // ✅ NUEVA: Incrementar contador de intentos fallidos
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            // ✅ NUEVA: Registrar timestamp del primer intento fallido en esta serie
            if (attempts == 1) {
                lockoutTimes.put(username, System.currentTimeMillis());
            }

            // ✅ NUEVA: Logging de seguridad
            if (attempts >= MAX_ATTEMPTS) {
                log.warn("SECURITY: {} lockout after {} failed login attempts", username, attempts);
            } else {
                log.warn("SECURITY: Failed login attempt for {} ({}/{} attempts)", username, attempts,
                        MAX_ATTEMPTS);
            }

            AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Login failed: Invalid credentials");
            return ResponseEntity.status(401).body(errorResponse);

        } catch (AuthenticationException e) {
            // ✅ NUEVA: Manejar otros errores de autenticación
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            if (attempts == 1) {
                lockoutTimes.put(username, System.currentTimeMillis());
            }

            log.warn("SECURITY: Authentication error for {} ({}/{} attempts)", username, attempts, MAX_ATTEMPTS);

            AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Login failed: Invalid credentials");
            return ResponseEntity.status(401).body(errorResponse);

        } catch (Exception e) {
            log.error("Unexpected error during login for username: {}", username, e);
            AuthResponse errorResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Login failed: An error occurred");
            return ResponseEntity.status(500).body(errorResponse);
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