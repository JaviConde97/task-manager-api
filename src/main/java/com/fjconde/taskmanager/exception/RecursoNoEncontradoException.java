package com.fjconde.taskmanager.exception;

/**
 * Excepción que se lanza cuando no se encuentra un recurso en la base de datos.
 * Resulta en un HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
