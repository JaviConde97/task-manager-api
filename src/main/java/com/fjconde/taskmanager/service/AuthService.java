package com.fjconde.taskmanager.service;

import com.fjconde.taskmanager.dto.auth.AuthResponse;
import com.fjconde.taskmanager.dto.auth.LoginRequest;
import com.fjconde.taskmanager.dto.auth.RegistroRequest;
import com.fjconde.taskmanager.entity.Usuario;
import com.fjconde.taskmanager.repository.UsuarioRepository;
import com.fjconde.taskmanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona el registro y login de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     */
    public AuthResponse registro(RegistroRequest request) {
        // Comprobamos que el email no esté ya en uso
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        // Creamos el usuario con la contraseña encriptada (nunca en texto plano)
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        usuarioRepository.save(usuario);

        // Generamos el token para que el usuario ya pueda hacer peticiones
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(token);
    }

    /**
     * Autentica un usuario existente y devuelve un token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security comprueba email y password automáticamente
        // Si son incorrectos lanza una excepción
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si llegamos aquí es que las credenciales son correctas
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(token);
    }
}
