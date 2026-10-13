package com.proyecto.gestioncitas.common.exception;

import com.proyecto.gestioncitas.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Traduce toda excepcion a una respuesta HTTP con mensaje en espanol
 * (instruccion G4). Nunca devuelve el stack trace al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Cubre BusinessException y todas sus hijas, incluida
     * ResourceNotFoundException y las excepciones propias de cada modulo.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> manejarNegocio(BusinessException ex, HttpServletRequest req) {
        ErrorCode codigo = ex.getCodigo();
        // Los errores de negocio son esperados: se registran como warn, sin traza.
        log.warn("Regla de negocio incumplida [{}] en {}: {}", codigo, req.getRequestURI(), ex.getMessage());
        return construir(codigo.name(), ex.getMessage(), codigo.getEstado(), req);
    }

    /** Validacion de @Valid sobre el cuerpo de la peticion. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacionCuerpo(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        Map<String, List<String>> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.computeIfAbsent(fe.getField(), k -> new ArrayList<>())
                    .add(fe.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors().forEach(ge -> errores
                .computeIfAbsent(ge.getObjectName(), k -> new ArrayList<>())
                .add(ge.getDefaultMessage()));

        ErrorCode codigo = ErrorCode.VALIDACION_FALLIDA;
        log.warn("Validacion fallida en {}: {}", req.getRequestURI(), errores);
        return ResponseEntity.status(codigo.getEstado())
                .body(ErrorResponse.deValidacion(codigo.name(), codigo.getMensaje(),
                        codigo.getEstado().value(), req.getRequestURI(), errores));
    }

    /** Validacion sobre parametros de metodo, como @RequestParam o @PathVariable. */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> manejarValidacionParametros(HandlerMethodValidationException ex,
            HttpServletRequest req) {
        ErrorCode codigo = ErrorCode.VALIDACION_FALLIDA;
        log.warn("Validacion de parametros fallida en {}", req.getRequestURI());
        return construir(codigo.name(), codigo.getMensaje(), codigo.getEstado(), req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> manejarParametroFaltante(MissingServletRequestParameterException ex,
            HttpServletRequest req) {
        String mensaje = "Falta el parametro obligatorio '%s'".formatted(ex.getParameterName());
        return construir(ErrorCode.DATOS_INVALIDOS.name(), mensaje, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex,
            HttpServletRequest req) {
        String mensaje = "El valor de '%s' no tiene el formato esperado".formatted(ex.getName());
        return construir(ErrorCode.DATOS_INVALIDOS.name(), mensaje, HttpStatus.BAD_REQUEST, req);
    }

    /** JSON malformado o un enum con un valor que no existe. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoIlegible(HttpMessageNotReadableException ex,
            HttpServletRequest req) {
        log.warn("Cuerpo ilegible en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return construir(ErrorCode.DATOS_INVALIDOS.name(),
                "El cuerpo de la peticion no se pudo interpretar", HttpStatus.BAD_REQUEST, req);
    }

    /**
     * 401. Se llega aqui solo si la excepcion escapa del filtro de seguridad;
     * el caso normal lo atiende JwtAuthenticationEntryPoint.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> manejarNoAutenticado(AuthenticationException ex, HttpServletRequest req) {
        ErrorCode codigo = ErrorCode.NO_AUTENTICADO;
        return construir(codigo.name(), codigo.getMensaje(), codigo.getEstado(), req);
    }

    /** 403, tipicamente lanzada por un @PreAuthorize que no se cumple. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> manejarSinPermisos(AccessDeniedException ex, HttpServletRequest req) {
        ErrorCode codigo = ErrorCode.SIN_PERMISOS;
        log.warn("Acceso denegado en {}", req.getRequestURI());
        return construir(codigo.name(), codigo.getMensaje(), codigo.getEstado(), req);
    }

    /**
     * Choque con una restriccion de la base de datos. El caso que mas importa
     * es el indice unico (medico_id, fecha_hora) de la tabla de citas, que es
     * la ultima linea de defensa contra la doble reserva.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        log.warn("Violacion de integridad en {}: {}", req.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        ErrorCode codigo = ErrorCode.CONFLICTO_DE_ESTADO;
        return construir(codigo.name(),
                "La operacion choca con un registro existente", codigo.getEstado(), req);
    }

    /** Dos usuarios modificaron la misma cita a la vez (campo @Version). */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> manejarBloqueoOptimista(OptimisticLockingFailureException ex,
            HttpServletRequest req) {
        log.warn("Conflicto de concurrencia en {}", req.getRequestURI());
        return construir(ErrorCode.CONFLICTO_DE_ESTADO.name(),
                "Otro usuario modifico este registro, vuelva a intentarlo", HttpStatus.CONFLICT, req);
    }

    @ExceptionHandler({ NoHandlerFoundException.class })
    public ResponseEntity<ErrorResponse> manejarRutaInexistente(Exception ex, HttpServletRequest req) {
        return construir(ErrorCode.RECURSO_NO_ENCONTRADO.name(),
                "La ruta solicitada no existe", HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        return construir(ErrorCode.DATOS_INVALIDOS.name(),
                "El metodo HTTP no esta permitido en esta ruta", HttpStatus.METHOD_NOT_ALLOWED, req);
    }

    /**
     * Ultimo recurso. Aqui si se registra la traza completa en el log del
     * servidor, pero al cliente solo le llega un mensaje generico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarNoPrevisto(Exception ex, HttpServletRequest req) {
        ErrorCode codigo = ErrorCode.ERROR_INTERNO;
        log.error("Error no previsto en {}", req.getRequestURI(), ex);
        return construir(codigo.name(), codigo.getMensaje(), codigo.getEstado(), req);
    }

    private ResponseEntity<ErrorResponse> construir(String codigo, String mensaje, HttpStatus estado,
            HttpServletRequest req) {
        return ResponseEntity.status(estado)
                .body(ErrorResponse.de(codigo, mensaje, estado.value(), req.getRequestURI()));
    }
}
