package contevolve.etlVisualizer.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import contevolve.etlVisualizer.model.Users;
import contevolve.etlVisualizer.repository.UsersRepository;
import contevolve.etlVisualizer.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter{

	private final UsersRepository usersRepository;
	private final JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		log.info("incoming request: {}",  request.getRequestURI());
		final String requestTokenHeader = request.getHeader("Authorization");
		
		log.info("requestTokenHeader: {}",requestTokenHeader );
		if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
			log.warn("No JWT token found in request: {}", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			String token = requestTokenHeader.split("Bearer ")[1];
			String username = jwtUtil.getUsernameByToken(token);
			boolean isTokenExpired = jwtUtil.isTokenExpired(token);
			
			if(username != null && !isTokenExpired && SecurityContextHolder.getContext().getAuthentication() == null) {
				Users user =  usersRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
				UsernamePasswordAuthenticationToken token2 = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				log.info("user enter with role: {}", user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(token2);
			}
		}catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT signature: token has been tampered with");
            return; // ← stop filter chain

        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token has expired, please login again");
            return;

        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Malformed JWT token");
            return;

        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unsupported JWT token");
            return;

        }catch (Exception e) {
			log.error("JWT authentication failed: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
            return;
		}
		filterChain.doFilter(request, response);
	}
	
	
	private void sendErrorResponse(HttpServletResponse response, HttpStatus status,String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.getWriter().write(
			String.format(
			"{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
			status.value(),
			status.getReasonPhrase(),
			message
			)
		);
	}
}
