package com.shoppersapp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;

import java.time.Instant;
import java.util.Date;

public class TokenUtil {
    private static final String SECRET = "super-secret-key";

    public static String generateToken(String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600); // 1 hour

        return JWT.create()
                .withSubject("UserAuthentication")
                .withClaim("userId", userId)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String verifyToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withSubject("UserAuthentication")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("userId").asString();
    }
}