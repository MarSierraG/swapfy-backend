package com.swapfy.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejar errores de validación con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse response = new ErrorResponse("Error de validación", "Hay un problema con los datos enviados.", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Manejar errores de integridad de datos (ej. duplicados)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";

        ErrorResponse response;

        if (rootMessage.contains("users") && rootMessage.contains("email")) {
            response = new ErrorResponse(
                    "Error de registro",
                    "El email ya está registrado",
                    "El email que intentas registrar ya está en uso. Por favor, usa otro.");
        } else if (rootMessage.contains("tags") && rootMessage.contains("name")) {
            response = new ErrorResponse(
                    "Error de etiqueta",
                    "La etiqueta ya existe",
                    "El nombre de la etiqueta que intentas registrar ya está en uso. Usa otro nombre.");
        } else {
            response = new ErrorResponse(
                    "Error de integridad",
                    "Violación de restricción de base de datos",
                    rootMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Validación de parámetros
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse response = new ErrorResponse("Error de validación", "Hay un problema con los datos enviados.", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Autorización (403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse response = new ErrorResponse("Error de autorización", ex.getMessage(), "Forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // RuntimeException personalizada (etiqueta en uso o logro duplicado)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        String msg = ex.getMessage();

        if (msg.contains("está en uso")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("Error de eliminación", msg, "Etiqueta en uso")
            );
        }

        if (msg.contains("desbloqueado")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("Error de validación", msg, "Este logro ya está asignado a este usuario.")
            );
        }

        // Si no encaja, envía al genérico
        return handleAllExceptions(ex);
    }

    @ExceptionHandler(TagInUseException.class)
    public ResponseEntity<ErrorResponse> handleTagInUse(TagInUseException ex) {
        ErrorResponse error = new ErrorResponse("Etiqueta en uso", ex.getMessage(), "En uso");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Genérico (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse response = new ErrorResponse("Error interno", "Hubo un error al procesar la solicitud.", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
