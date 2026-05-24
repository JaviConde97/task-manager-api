package com.fjconde.taskmanager.service;

import com.fjconde.taskmanager.dto.tarea.TareaRequest;
import com.fjconde.taskmanager.dto.tarea.TareaResponse;
import com.fjconde.taskmanager.entity.Tarea;
import com.fjconde.taskmanager.entity.Usuario;
import com.fjconde.taskmanager.exception.RecursoNoEncontradoException;
import com.fjconde.taskmanager.repository.TareaRepository;
import com.fjconde.taskmanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que gestiona las operaciones CRUD sobre las tareas.
 * Todas las operaciones están aisladas por usuario — cada uno solo accede a las suyas.
 */
@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;

    // Devuelve todas las tareas del usuario autenticado
    public List<TareaResponse> listarTareas(String email) {
        Usuario usuario = obtenerUsuario(email);
        return tareaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(TareaResponse::new)
                .toList();
    }

    // Crea una nueva tarea para el usuario autenticado
    public TareaResponse crearTarea(TareaRequest request, String email) {
        Usuario usuario = obtenerUsuario(email);

        Tarea tarea = Tarea.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .completada(request.isCompletada())
                .usuario(usuario)
                .build();

        return new TareaResponse(tareaRepository.save(tarea));
    }

    // Actualiza una tarea existente (solo si pertenece al usuario autenticado)
    public TareaResponse actualizarTarea(Long id, TareaRequest request, String email) {
        Usuario usuario = obtenerUsuario(email);

        Tarea tarea = tareaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con id: " + id));

        tarea.setTitulo(request.getTitulo());
        tarea.setDescripcion(request.getDescripcion());
        tarea.setCompletada(request.isCompletada());

        return new TareaResponse(tareaRepository.save(tarea));
    }

    // Elimina una tarea (solo si pertenece al usuario autenticado)
    public void eliminarTarea(Long id, String email) {
        Usuario usuario = obtenerUsuario(email);

        Tarea tarea = tareaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con id: " + id));

        tareaRepository.delete(tarea);
    }

    // Método privado de apoyo para no repetir la búsqueda del usuario
    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
