package io.passive.api.jwt;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.lettuce.core.dynamic.annotation.Key;
import io.passive.api.common.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import javax.servlet.http.HttpServletRequest;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class TokenUtils {
    private static final String secretKey = "ii";

    public static String generateJwtToken(UserVO user) {
        JwtBuilder builder = Jwts.builder()
         .setSubject(String.valueOf(user.getUserSq()))
         .setHeader(createHeader())
         .setClaims(createClaims(user))
         .setExpiration(createExpireDate())
         .signWith(SignatureAlgorithm.HS256, createSigningKey());
       
        return builder.compact();
    }

    public static String parseUserFromToken(String token) {
        String userId = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return userId;
    }

    public static String resolveToken(HttpServletRequest request) {
        return request.getHeader(AuthConstants.AUTH_HEADER);
    }

    public static boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFormToken(token);
            log.info("expireTime :" + claims.getExpiration());
            log.info("userId :" + claims.get("userId"));
            log.info("userNm :" + claims.get("userNm"));
            return true;
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");
            return false;
        }
    }

    private static Date createExpireDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 5);

        return c.getTime();
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    private static Map<String, Object> createClaims(UserVO user) {
        Map<String, Object> claims = new HashMap<>();
        log.info("userId :" + user.getUserId());
        log.info("userNm :" + user.getUserNm());
        claims.put("userId", user.getUserId());
        claims.put("userNm", user.getUserNm());

        return claims;
    }

    private static Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return (Key) new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(token).getBody();
    }

    public static Authentication getAuthentication(String token) {
        UserDetails userDetails = new User(
            String.valueOf(getUserIdFromToken(token)),
            "1234",
            Arrays.asList("ADMIN").stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return auth;
    }

    public static String getUserIdFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("userId");
    }
}