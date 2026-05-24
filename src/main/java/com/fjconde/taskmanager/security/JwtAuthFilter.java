package com.fjconde.taskmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta UNA VEZ por cada petición HTTP.
 *
 * Lo que hace en orden:
 *   1. Lee el header "Authorization" de la petición
 *   2. Extrae el token JWT (viene como "Bearer <token>")
 *   3. Valida el token y obtiene el email del usuario
 *   4. Carga el usuario de la base de datos
 *   5. Registra al usuario como autenticado en Spring Security
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // El token viene en el header Authorization: Bearer eyJhbGci...
        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza por "Bearer ", dejamos pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quitamos "Bearer " (7 caracteres) para quedarnos solo con el token
        String token = authHeader.substring(7);
        String email = jwtService.extraerEmail(token);

        // Si tenemos email y el usuario aún no está autenticado en esta petición
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.esTokenValido(token, userDetails)) {
                // Creamos el objeto de autenticación y lo registramos en Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con la siguiente fase del filtro
        filterChain.doFilter(request, response);
    }
}
