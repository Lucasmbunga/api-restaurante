package com.lucas.api_restaurante.responseutils;

import java.util.List;

public record ApiResponse<T>(
        boolean sucess,
        String message,
        T data,
        List<String> errors,
        int errorCode,
        long timestamp,
        String path) {
}
