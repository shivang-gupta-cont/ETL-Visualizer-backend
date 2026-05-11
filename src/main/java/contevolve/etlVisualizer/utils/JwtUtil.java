//package contevolve.etlVisualizer.utils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//import javax.crypto.SecretKey;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import contevolve.etlVisualizer.model.Users;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//
//@Component
//public class JwtUtil {
//	
//	@Value("${jwt.secretKey}")
//	private String jwtSecretKey;
//	
//	private SecretKey getSecretKey() {
//		return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
//	}
//	
//	public String generateAccessToken(Users user) {
//		return Jwts.builder()
//				.setSubject(user.getEmail())
//				.claim("userId", user.getId())
//				.claim("role", user.getRole())
//				.setIssuedAt(new Date())
//				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10)) // for 10 hours
//				.signWith(getSecretKey())
//				.compact();
//	}
//	
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSecretKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//	public String getEmailByToken(String token) {                        
//	    return extractAllClaims(token).getSubject();
//	}
//	
//    public Date getExpirationByToken(String token) {
//        return extractAllClaims(token).getExpiration();
//    }
//	
//    public boolean isTokenExpired(String token) {
//        return getExpirationByToken(token).before(new Date());
//    }
//
//}


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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Users user) {
        log.debug("Generating access token for user: {}", user.getEmail());
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(getSecretKey())
                .compact();
        log.debug("Access token generated successfully for user: {}", user.getEmail());
        return token;
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extracting claims from token");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public String getEmailByToken(String token) {
        log.debug("Extracting email from token");
        return extractAllClaims(token).getSubject();
    }

    public Date getExpirationByToken(String token) {
        log.debug("Extracting expiration date from token");
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        boolean expired = getExpirationByToken(token).before(new Date());
        log.debug("Token expiration check result: {}", expired ? "expired" : "valid");
        return expired;
    }
}
