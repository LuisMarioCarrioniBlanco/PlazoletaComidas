package com.plazoleta.usuarios.infrastructure.output.security;

import com.plazoleta.usuarios.application.port.output.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenAdapter implements TokenPort {
    private final String secret;
    private final long expirationMinutes;

    public JwtTokenAdapter(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:120}") long expirationMinutes) {
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String generar(Long usuarioId, String correo, String rol) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(correo)
                .claim("usuarioId", usuarioId)
                .claim("rol", rol)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(expirationMinutes * 60)))
                .signWith(clave())
                .compact();
    }

    @Override
    public DatosToken validar(String token) {
        Claims claims = Jwts.parser().verifyWith(clave()).build()
                .parseSignedClaims(token).getPayload();
        Number id = claims.get("usuarioId", Number.class);
        String rol = claims.get("rol", String.class);
        return new DatosToken(id.longValue(), claims.getSubject(), rol);
    }

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
