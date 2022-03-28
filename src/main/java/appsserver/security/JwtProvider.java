package appsserver.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String privetKey;
    @Value("${jwt.expire_date_mill}")
    private Long expiredTimeMilliseconds;

    public String generate(String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMilliseconds))
                .signWith(SignatureAlgorithm.HS512, privetKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(privetKey)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(privetKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
