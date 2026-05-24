package com.fjconde.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Servicio encargado de generar y validar tokens JWT.
 *
 * Un JWT tiene tres partes separadas por puntos:
 *   - Header: algoritmo usado (HS256)
 *   - Payload: datos del usuario (email, fecha de expiración)
 *   - Signature: firma que garantiza que nadie ha modificado el token
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secreto;

    @Value("${jwt.expiration}")
    private long expiracion;

    // Genera un token JWT para el usuario dado
    public String generarToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())       // el email del usuario
                .issuedAt(new Date())                     // fecha de creación
                .expiration(new Date(System.currentTimeMillis() + expiracion)) // fecha de expiración
                .signWith(obtenerClave())                 // firma con nuestra clave secreta
                .compact();
    }

    // Extrae el email (subject) del token
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // Comprueba si el token es válido para el usuario dado
    public boolean esTokenValido(String token, UserDetails userDetails) {
        String email = extraerEmail(token);
        return email.equals(userDetails.getUsername()) && !estaExpirado(token);
    }

    // --- Métodos privados de apoyo ---

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    // Convierte el texto del secreto en una clave criptográfica real
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secreto.getBytes());
    }
}
