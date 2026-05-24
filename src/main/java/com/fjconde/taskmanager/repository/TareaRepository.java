package com.fjconde.taskmanager.repository;

import com.fjconde.taskmanager.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos sobre Tarea.
 */
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Devuelve todas las tareas de un usuario concreto
    List<Tarea> findByUsuarioId(Long usuarioId);

    // Busca una tarea por id pero solo si pertenece al usuario indicado
    // Esto evita que un usuario pueda acceder a tareas de otro
    Optional<Tarea> findByIdAndUsuarioId(Long id, Long usuarioId);
}
