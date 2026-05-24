package com.fjconde.taskmanager.dto.tarea;

import com.fjconde.taskmanager.entity.Tarea;
import lombok.Data;

/**
 * Datos que la API devuelve al cliente sobre una tarea.
 * No exponemos el objeto Tarea directamente para no filtrar datos internos.
 */
@Data
public class TareaResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private boolean completada;

    // Constructor que convierte una entidad Tarea en este DTO
    public TareaResponse(Tarea tarea) {
        this.id = tarea.getId();
        this.titulo = tarea.getTitulo();
        this.descripcion = tarea.getDescripcion();
        this.completada = tarea.isCompletada();
    }
}
