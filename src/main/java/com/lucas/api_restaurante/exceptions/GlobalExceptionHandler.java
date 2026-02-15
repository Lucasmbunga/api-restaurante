package com.lucas.api_restaurante.exceptions;

import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> hadlerException(HttpServletRequest request, Exception exception) {
        List<String> errors = Arrays.asList(exception.getMessage());
        ApiResponse<Void> response = ResponseUtil.error(errors, "Ocorreu algum erro", 1000, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handlerResourceNotFoundException(HttpServletRequest request, RecursoNaoEncontradoException exception) {
        ApiResponse<Void> response = ResponseUtil.error(exception.getMessage(), "Recurso não encontrado", 1001, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handlerValidationException(HttpServletRequest request, ValidationException exception) {
        ApiResponse<Void> response = ResponseUtil.error(exception.getMessage(), "Erro de validacao", 1002, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
