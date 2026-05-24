package com.fjconde.taskmanager.controller;

import com.fjconde.taskmanager.dto.tarea.TareaRequest;
import com.fjconde.taskmanager.dto.tarea.TareaResponse;
import com.fjconde.taskmanager.service.TareaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de tareas.
 * Todos los endpoints requieren token JWT válido.
 *
 * @AuthenticationPrincipal nos da el usuario autenticado directamente
 * sin tener que leer el token manualmente.
 */
@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    // GET /api/tareas — lista todas las tareas del usuario
    @GetMapping
    public ResponseEntity<List<TareaResponse>> listar(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tareaService.listarTareas(userDetails.getUsername()));
    }

    // POST /api/tareas — crea una nueva tarea
    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tareaService.crearTarea(request, userDetails.getUsername()));
    }

    // PUT /api/tareas/{id} — actualiza una tarea existente
    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody TareaRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tareaService.actualizarTarea(id, request, userDetails.getUsername()));
    }

    // DELETE /api/tareas/{id} — elimina una tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        tareaService.eliminarTarea(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
