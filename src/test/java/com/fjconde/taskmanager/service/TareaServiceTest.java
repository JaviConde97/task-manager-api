package com.fjconde.taskmanager.service;

import com.fjconde.taskmanager.dto.tarea.TareaRequest;
import com.fjconde.taskmanager.dto.tarea.TareaResponse;
import com.fjconde.taskmanager.entity.Tarea;
import com.fjconde.taskmanager.entity.Usuario;
import com.fjconde.taskmanager.exception.RecursoNoEncontradoException;
import com.fjconde.taskmanager.repository.TareaRepository;
import com.fjconde.taskmanager.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del TareaService.
 * Verifica que cada operación CRUD funciona correctamente
 * y que el aislamiento por usuario se respeta.
 */
@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TareaService tareaService;

    private Usuario usuario;
    private Tarea tarea;
    private TareaRequest tareaRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Javi")
                .email("javi@test.com")
                .password("password-encriptado")
                .build();

        tarea = Tarea.builder()
                .id(1L)
                .titulo("Tarea de prueba")
                .descripcion("Descripción de prueba")
                .completada(false)
                .usuario(usuario)
                .build();

        tareaRequest = new TareaRequest();
        tareaRequest.setTitulo("Tarea de prueba");
        tareaRequest.setDescripcion("Descripción de prueba");
        tareaRequest.setCompletada(false);
    }

    @Test
    @DisplayName("Listar tareas devuelve solo las del usuario autenticado")
    void listar_tareas_devuelve_las_del_usuario() {
        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarea));

        List<TareaResponse> resultado = tareaService.listarTareas("javi@test.com");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Tarea de prueba");
    }

    @Test
    @DisplayName("Crear tarea la asocia al usuario autenticado")
    void crear_tarea_la_asocia_al_usuario() {
        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);

        TareaResponse resultado = tareaService.crearTarea(tareaRequest, "javi@test.com");

        assertThat(resultado.getTitulo()).isEqualTo("Tarea de prueba");
        verify(tareaRepository).save(argThat(t -> t.getUsuario().equals(usuario)));
    }

    @Test
    @DisplayName("Actualizar tarea modifica los campos correctamente")
    void actualizar_tarea_modifica_los_campos() {
        TareaRequest actualizacion = new TareaRequest();
        actualizacion.setTitulo("Título actualizado");
        actualizacion.setDescripcion("Nueva descripción");
        actualizacion.setCompletada(true);

        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);

        tareaService.actualizarTarea(1L, actualizacion, "javi@test.com");

        // verifica que se guardó con los nuevos valores
        verify(tareaRepository).save(argThat(t ->
                t.getTitulo().equals("Título actualizado") && t.isCompletada()
        ));
    }

    @Test
    @DisplayName("Actualizar tarea que no existe lanza excepción")
    void actualizar_tarea_inexistente_lanza_excepcion() {
        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tareaService.actualizarTarea(99L, tareaRequest, "javi@test.com"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Eliminar tarea la borra de la base de datos")
    void eliminar_tarea_la_borra() {
        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarea));

        tareaService.eliminarTarea(1L, "javi@test.com");

        verify(tareaRepository).delete(tarea);
    }

    @Test
    @DisplayName("Eliminar tarea de otro usuario lanza excepción")
    void eliminar_tarea_de_otro_usuario_lanza_excepcion() {
        // la tarea 1 no pertenece a este usuario (findByIdAndUsuarioId devuelve vacío)
        when(usuarioRepository.findByEmail("javi@test.com")).thenReturn(Optional.of(usuario));
        when(tareaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tareaService.eliminarTarea(1L, "javi@test.com"))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(tareaRepository, never()).delete(any());
    }
}
