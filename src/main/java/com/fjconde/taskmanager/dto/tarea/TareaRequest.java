package com.fjconde.taskmanager.dto.tarea;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos que el cliente envía para crear o actualizar una tarea.
 */
@Data
public class TareaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    private boolean completada;
}
