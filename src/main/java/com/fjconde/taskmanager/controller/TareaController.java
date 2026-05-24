package com.fjconde.taskmanager.controller;

import com.fjconde.taskmanager.dto.tarea.TareaRequest;
import com.fjconde.taskmanager.dto.tarea.TareaResponse;
import com.fjconde.taskmanager.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tareas", description = "Gestión de tareas del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TareaController {

    private final TareaService tareaService;

    // GET /api/tareas — lista todas las tareas del usuario
    @Operation(summary = "Listar tareas", description = "Devuelve todas las tareas del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tareas"),
        @ApiResponse(responseCode = "403", description = "Token JWT ausente o inválido")
    })
    @GetMapping
    public ResponseEntity<List<TareaResponse>> listar(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tareaService.listarTareas(userDetails.getUsername()));
    }

    // POST /api/tareas — crea una nueva tarea
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea asociada al usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tarea creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de la tarea inválidos"),
        @ApiResponse(responseCode = "403", description = "Token JWT ausente o inválido")
    })
    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tareaService.crearTarea(request, userDetails.getUsername()));
    }

    // PUT /api/tareas/{id} — actualiza una tarea existente
    @Operation(summary = "Actualizar tarea", description = "Modifica los datos de una tarea existente del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarea actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de la tarea inválidos"),
        @ApiResponse(responseCode = "403", description = "Token JWT ausente o inválido"),
        @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody TareaRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tareaService.actualizarTarea(id, request, userDetails.getUsername()));
    }

    // DELETE /api/tareas/{id} — elimina una tarea
    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente"),
        @ApiResponse(responseCode = "403", description = "Token JWT ausente o inválido"),
        @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        tareaService.eliminarTarea(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
