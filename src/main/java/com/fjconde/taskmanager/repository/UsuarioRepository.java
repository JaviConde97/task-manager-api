package com.fjconde.taskmanager.repository;

import com.fjconde.taskmanager.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos sobre Usuario.
 * JpaRepository nos da gratis: save, findById, findAll, delete, etc.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring genera el SQL automáticamente a partir del nombre del método:
    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // Comprueba si ya existe un usuario con ese email (para el registro)
    boolean existsByEmail(String email);
}
