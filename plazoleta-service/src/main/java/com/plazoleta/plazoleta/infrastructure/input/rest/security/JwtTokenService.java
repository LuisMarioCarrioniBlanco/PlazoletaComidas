package com.plazoleta.plazoleta.infrastructure.input.rest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenService {
    private final String secret;

    public JwtTokenService(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public DatosToken validar(String token) {
        Claims claims = Jwts.parser().verifyWith(clave()).build()
                .parseSignedClaims(token).getPayload();
        Number id = claims.get("usuarioId", Number.class);
        return new DatosToken(id.longValue(), claims.getSubject(), claims.get("rol", String.class));
    }

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public record DatosToken(Long usuarioId, String correo, String rol) {}
}
