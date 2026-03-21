package com.lucas.api_restaurante.exceptions;

import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlerResourceNotFoundException(HttpServletRequest request, NotFoundException exception) {
        ApiResponse<Void> response = ResponseUtil.error(exception.getMessage(), "Recurso não encontrado", 1001, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handlerValidationException(HttpServletRequest request, ValidationException exception) {
        ApiResponse<Void> response = ResponseUtil.error(exception.getMessage(), "Erro de validacao", 1002, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handlerDataIntegrityVioletionsException(HttpServletRequest request, DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(exception.getMessage(), "Erro de violação da integridade de dados", 1003, request.getRequestURI()));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handlerAuthenticationException(HttpServletRequest request, AuthenticationException exception) {
        ApiResponse<Void> response=ResponseUtil.error(exception.getMessage(), "Acesso negado. Usuário não autenticado. ", 401, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handlerAccessDeniedException(HttpServletRequest request, AccessDeniedException exception) {
        ApiResponse<Void> respons=ResponseUtil.error(exception.getMessage(), "Acesso negado. Usuário não autorizado ", 403, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respons);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlerMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        ApiResponse<Void> response=ResponseUtil.error(exception.getMessage(), "Erro de validacao", 400, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
