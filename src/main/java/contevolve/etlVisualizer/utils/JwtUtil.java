package contevolve.etlVisualizer.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import contevolve.etlVisualizer.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${jwt.secretKey}")
	private String jwtSecretKey;
	
	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateAccessToken(Users user) {
		return Jwts.builder()
				.setSubject(user.getUsername())
				.claim("userId", user.getId())
				.claim("role", user.getRole())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*10))
				.signWith(getSecretKey())
				.compact();
	}
	
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

	public String getUsernameByToken(String token) {                        
	    return extractAllClaims(token).getSubject();
	}
	
    public Date getExpirationByToken(String token) {
        return extractAllClaims(token).getExpiration();
    }
	
    public boolean isTokenExpired(String token) {
        return getExpirationByToken(token).before(new Date());
    }

}
